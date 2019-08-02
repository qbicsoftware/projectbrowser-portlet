/*******************************************************************************
 * QBiC Project qNavigator enables users to manage their projects. Copyright (C) "2016” Christopher
 * Mohr, David Wojnar, Andreas Friedrich
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package life.qbic.projectbrowser.components;

import java.util.HashMap;

import life.qbic.projectbrowser.model.maxquant.Group;
import life.qbic.projectbrowser.model.maxquant.GroupSpecificParameters;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;


/**
 * extends vaadins CustomComponent and represents the UI of Maxquants Group spefic parameters which
 * define parameters for a group of raw files. The groups can be updated via the update method.
 * 
 * @author wojnar
 * 
 */
public class GroupSpecificParameterComponent extends CustomComponent {
  private static final long serialVersionUID = -1912407218446455308L;

  public static String CAPTION = "Group-specific parameters";
  private NativeSelect groupSelection;

  private NativeSelect typeSelect;

  private QuantificationLabelComponent labels;

  private FormLayout parameterlayout;

  private TwinColSelect variableModifications;

  private NativeSelect digestionMode;

  private TwinColSelect enzyme;

  private TextField missedcleavage;

  private HashMap<Integer, Group> groups;

  private NativeSelect multiplicitySelect;

  private NativeSelect matchType;

  private CheckBox labelfreequant;

  public GroupSpecificParameterComponent() {

    this.setCaption(CAPTION);


    this.setCompositionRoot(init());

  }

  public Component init() {
    // Group specific parameters

    FormLayout groupSpecificParameters = new FormLayout();

    // does not need to be initialized more than once.
    if (groupSelection == null) {
      groupSelection = new NativeSelect("Group:");
      groupSelection.setNullSelectionAllowed(false);
      groupSpecificParameters.addComponent(groupSelection);
      groupSelection.addValueChangeListener(new ValueChangeListener() {
        private static final long serialVersionUID = -2948673674040494963L;

        @Override
        public void valueChange(ValueChangeEvent event) {
          if (event.getProperty() != null && event.getProperty().getValue() != null) {
            updateParameters((Integer) event.getProperty().getValue());
          }
        }
      });
    }
    parameterlayout = initParameters();
    groupSpecificParameters.addComponent(parameterlayout);
    parameterlayout.setVisible(false);
    return groupSpecificParameters;
  }


  private FormLayout initParameters() {
    // select the type
    FormLayout parameterLayout = new FormLayout();

    labelfreequant = new CheckBox();
    labelfreequant.setCaption("Label-Free Quantification");
    parameterLayout.addComponent(labelfreequant);

    typeSelect = new NativeSelect();
    typeSelect.setCaption("Type");
    typeSelect.addItem("Standard");
    typeSelect.addItem("Reporter Ion");
    // add it
    parameterLayout.addComponent(typeSelect);
    // select multiplicity.
    multiplicitySelect = new NativeSelect();
    multiplicitySelect.setCaption("Multiplicity");
    multiplicitySelect.addItem("1 - label-free quantification");
    multiplicitySelect.addItem("2 - light and heavy labels");
    multiplicitySelect.addItem("3 - light, medium, and heavy lables");
    multiplicitySelect.setNullSelectionAllowed(false);
    parameterLayout.addComponent(multiplicitySelect);
    // add labels
    labels = new QuantificationLabelComponent();
    parameterLayout.addComponent(labels);
    multiplicitySelect.addValueChangeListener(new ValueChangeListener() {
      private static final long serialVersionUID = 2788813707970284834L;

      @Override
      public void valueChange(ValueChangeEvent event) {
        String multiplicity = (String) event.getProperty().getValue();
        if (multiplicity.startsWith("1")) {
          labels.noLables();
        } else if (multiplicity.startsWith("2")) {
          labels.lightAndHeavyLabels();
        } else if (multiplicity.startsWith("3")) {
          labels.lightMediumAndHeavyLabels();
        }

      }
    });
    parameterLayout.addComponent(labels);

    // variable modifications
    variableModifications = new TwinColSelect("Variable Modifications");

    variableModifications.addItems("Acetyl (Protein N-term)", "Acetyl (K)", "Oxidation (M)",
        "Ala->Arg", "Phospho (STY)", "Phospho (C)", "Phospho (D)", "Phospho (H)", "Phospho (K)",
        "Phospho (R)", "Phospho (S)", "Phospho (T)", "Phospho (Y)");
    parameterLayout.addComponent(variableModifications);
    // digestion mode
    digestionMode = new NativeSelect("Digestion mode");
    digestionMode.addItem("Specific");
    parameterLayout.addComponent(digestionMode);
    // enzyme
    enzyme = new TwinColSelect("Enzyme");
    enzyme.addItems("Trypsin/P", "ArgC", "Trypsin", "GluN");
    parameterLayout.addComponent(enzyme);
    // missed cleavage
    missedcleavage = new TextField("Max Missed Cleavage");
    parameterLayout.addComponent(missedcleavage);
    matchType = new NativeSelect("Match type");
    matchType.addItem("MatchFromAndTo");
    parameterLayout.addComponent(matchType);
    return parameterLayout;
  }

  public void update(HashMap<Integer, Group> groups) {
    groupSelection.removeAllItems();
    this.resetParams();
    parameterlayout.setVisible(false);
    if (groups == null || groups.isEmpty())
      return;
    for (Integer key : groups.keySet()) {
      groupSelection.addItem(key);
    }
    this.groups = groups;
  }

  private void resetParams() {
    // TODO Auto-generated method stub

  }

  void updateParameters(int value) {
    parameterlayout.setVisible(true);
    BeanFieldGroup<GroupSpecificParameters> binder =
        new BeanFieldGroup<GroupSpecificParameters>(GroupSpecificParameters.class);
    binder.setItemDataSource(groups.get(value).getParameters());
    binder.bind(labelfreequant, "lfqMode");
    binder.bind(typeSelect, "type");
    binder.bind(multiplicitySelect, "multiplicity");
    binder.bind(variableModifications, "variableModifications");
    binder.bind(digestionMode, "digestionMode");
    binder.bind(enzyme, "enzymes");
    binder.bind(missedcleavage, "maxMissedCleavage");
    // binder.bind(TODO, "matchType");
    binder.bind(labels.getLightLabels(), "lightLabels");
    binder.bind(labels.getMediumLabels(), "mediumLabels");
    binder.bind(labels.getHeavyLabels(), "heavyLabels");
    binder.bind(matchType, "matchType");

    // update values immediately
    binder.setBuffered(false);
  }



}
