/*******************************************************************************
 * QBiC Project qNavigator enables users to manage their projects.
 * Copyright (C) "2016”  Christopher Mohr, David Wojnar, Andreas Friedrich
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package life.qbic.projectbrowser.components;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class SelectFileComponent extends CustomComponent {
  private static final long serialVersionUID = -8740268090810853563L;
  private Button toLeft;
  private Button toRight;
  private Grid available;
  private Grid selected;

  public SelectFileComponent(String mainCaption, String info, String sourceCaption,
      String destinationCaption, BeanItemContainer<?> source, BeanItemContainer<?> destination) {
    setCaption(mainCaption);

    VerticalLayout files = new VerticalLayout();
    files.setSpacing(true);

    // info label
    Label rawFilesInfo = new Label(info);
    rawFilesInfo.addStyleName(ValoTheme.LABEL_COLORED);
    files.addComponent(rawFilesInfo);
    files.setWidth("100%");

    // available files in openbis
    available = new Grid(source);
    available.setCaption(sourceCaption);
    available.setSelectionMode(SelectionMode.MULTI);

    // selected files for anaylsis
    selected = new Grid(destination);

    if (mainCaption.equals("Raw files")) {
      available.removeColumn("fullPath");
      available.removeColumn("openbisCode");
    }

    else if (mainCaption.equals("")) {
      available.removeColumn("name");
      available.removeColumn("path");
      selected.removeColumn("name");
      selected.removeColumn("path");
    }

    selected.setCaption(destinationCaption);
    selected.setSelectionMode(SelectionMode.MULTI);


    for (Grid.Column col : available.getColumns()) {
      col.setWidthUndefined();
    }

    // selectedFiles.set
    // buttons to add or remove files
    VerticalLayout buttons = new VerticalLayout();
    toLeft = new Button();
    toLeft.setIcon(FontAwesome.ARROW_LEFT);

    toRight = new Button();
    toRight.setIcon(FontAwesome.ARROW_RIGHT);
    buttons.addComponent(toRight);
    buttons.addComponent(toLeft);

    GridLayout grids = new GridLayout(3, 1);
    grids.setWidth("100%");

    // grids.setSpacing(true);
    grids.addComponent(available, 0, 0);

    available.setWidth("100%");
    grids.addComponent(buttons, 1, 0);
    grids.addComponent(selected, 2, 0);
    grids.setColumnExpandRatio(0, 0.45f);
    grids.setColumnExpandRatio(1, 0.029f);
    grids.setColumnExpandRatio(2, 0.45f);
    grids.setSpacing(false);
    grids.setComponentAlignment(buttons, com.vaadin.ui.Alignment.MIDDLE_CENTER);

    selected.setWidth("100%");

    files.addComponent(grids);

    this.setCompositionRoot(files);
    files.setMargin(new MarginInfo(true, true, true, false));
    this.setWidth("100%");

  }

  /**
   * returns the button that should move files from left to right. Basically add to destination
   * 
   * @return
   */
  public Button getToRightButton() {
    return toRight;
  }

  /**
   * returns the button to should move files from right to left. Basically remove from destination
   * 
   * @return
   */
  public Button getToLeftButton() {
    return toLeft;
  }

  public Grid getSource() {
    return available;
  }

  public Grid getDestination() {
    return selected;
  }

  public Grid getSelected() {
    return selected;
  }

  public void setSelected(Grid selected) {
    this.selected = selected;
  }


}
