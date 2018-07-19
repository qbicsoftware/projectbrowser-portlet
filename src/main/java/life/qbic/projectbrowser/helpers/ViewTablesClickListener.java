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
package life.qbic.projectbrowser.helpers;

import java.util.ArrayList;

import org.tepi.filtertable.FilterTable;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.UI;

import life.qbic.projectbrowser.controllers.State;

/**
 * This Class communicates clicks on the tables in the View classes to the state object, which in
 * turn notifys the TreeViews and a change in the Navigator. e.g. In SpaceView the user clicks on a
 * project in the table --> notify state --> notify TreeView --> TreeView notifies Navigator to
 * navigate to clicked project. Important: If the viewTable and the type are not set, valueChange
 * will through Errors or behave unexpectedly.
 * 
 * @author wojnar
 * 
 */
public class ViewTablesClickListener implements Property.ValueChangeListener {


  public ViewTablesClickListener(FilterTable table, String type) {
    this.viewTable = table;
    this.type = type;
  }

  public ViewTablesClickListener() {
    this.viewTable = null;
    this.type = null;
  }

  /**
	 * 
	 */
  private static final long serialVersionUID = -2654127722351401378L;

  /**
	 * 
	 */
  private FilterTable viewTable;



  /**
   * returns the table that the listener is listening to.
   * 
   * @return
   */
  public FilterTable getViewTable() {
    return viewTable;
  }

  /**
   * sets the table that this listener is listening to.
   * 
   * @param viewTable
   */
  public void setViewTable(FilterTable viewTable) {
    this.viewTable = viewTable;
  }

  /**
   * Get the type of View that the table belongs to. e.g. Space, Project, Experiment.
   * 
   * @return
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the type of View that the table belongs to. e.g. Space, Project, Experiment.
   * 
   * @return
   */
  public void setType(String type) {
    this.type = type;
  }

  private String type;

  @Override
  public void valueChange(ValueChangeEvent event) {
    // TODO Auto-generated method stub
    Object property = event.getProperty().getValue();
    if (property == null) {
      return;
    }
    // String experiment = (String)
    // this.viewTable.getItem(property).getItemProperty(this.type).getValue();
    String entity = (String) this.viewTable.getItem(property).getItemProperty("id").getValue();
    State state = (State) UI.getCurrent().getSession().getAttribute("state");
    ArrayList<String> message = new ArrayList<String>();
    message.add("clicked");
    message.add(entity);
    message.add(this.type);
    state.notifyObservers(message);
  }

  /**
   * checks whether the table and the type are set.
   * 
   * @return
   */
  public boolean isInitialized() {
    return viewTable == null || type == null;
  }


}
