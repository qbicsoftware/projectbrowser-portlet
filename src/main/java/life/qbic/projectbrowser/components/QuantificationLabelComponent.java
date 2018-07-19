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


import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;

public class QuantificationLabelComponent extends CustomComponent{
  private static final long serialVersionUID = -8953869755178358731L;
  private HorizontalLayout mainlayout;
  private OptionGroup light;
  private OptionGroup medium;
  private OptionGroup heavy;

  public QuantificationLabelComponent(){
    mainlayout = new HorizontalLayout();
    mainlayout.setCaption("Labels");
    mainlayout.setSpacing(true);
    light = labels("light labels");
    medium = labels("medium labels");
    heavy = labels("heavy labels");
    setCompositionRoot(mainlayout);
  }
  
  public void noLables() {
    mainlayout.setVisible(false);
  }

  public void lightAndHeavyLabels() {
    mainlayout.setVisible(true);
    mainlayout.removeAllComponents();
    mainlayout.addComponent(light);
    mainlayout.addComponent(heavy);
  }

  public void lightMediumAndHeavyLabels() {
    mainlayout.setVisible(true);
    mainlayout.removeAllComponents();
    mainlayout.addComponent(light);
    mainlayout.addComponent(medium);
    mainlayout.addComponent(heavy);
    
  }
  OptionGroup labels(String caption){
    OptionGroup labels = new OptionGroup(caption);
    labels.setMultiSelect(true);
    labels.addItems("Arg6", "Arg10", "Leu7", "Averigin");
    return labels;  
  }
  OptionGroup getLightLabels(){
    return light;
  }
  OptionGroup getMediumLabels(){
    return medium;
  }
  OptionGroup getHeavyLabels(){
    return heavy;
  }
  
  
}
