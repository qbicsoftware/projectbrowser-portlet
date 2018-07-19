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

import java.util.List;

import life.qbic.projectbrowser.model.NewIvacSampleBean;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;

public class GridEditForm extends GridLayout {

  public BeanFieldGroup<NewIvacSampleBean> fieldGroup = new BeanFieldGroup<NewIvacSampleBean>(
      NewIvacSampleBean.class);

  private TextField type = new TextField("Type");
  private TextField amount = new TextField("Amount");
  private TextField secondaryName = new TextField("Secondary Name");
  private CheckBox dnaSeq = new CheckBox("DNA Seq");
  private CheckBox rnaSeq = new CheckBox("RNA Seq");
  private CheckBox deepSeq = new CheckBox("Deep Seq");
  private ComboBox tissue = new ComboBox("Tissue");
  private ComboBox seqDevice = new ComboBox("Sequencing Device");

  public GridEditForm(List<String> tissueOptions, List<String> sequenceOptions) {
    super(5, 3);
    setSpacing(true);
    fieldGroup.buildAndBindMemberFields(this);

    addComponent(type, 0, 0);
    addComponent(secondaryName,1,0);
    addComponent(amount, 2, 0);
    addComponent(tissue, 3, 0);
    addComponent(seqDevice, 4, 0);
    addComponent(dnaSeq, 0, 1);
    addComponent(rnaSeq, 1, 1);
    addComponent(deepSeq, 2, 1);

    amount.setConverter(new StringToIntegerConverter());
    tissue.addItems(tissueOptions);
    seqDevice.addItems(sequenceOptions);

    space();
    addComponent(new Button("Save", new ClickListener() {

      @Override
      public void buttonClick(ClickEvent event) {
        try {
          fieldGroup.commit();
        } catch (CommitException e) {
          // TODO: Say and do something meaningful
        }
      }
    }), 0, 2);

    addComponent(new Button("Cancel", new ClickListener() {

      @Override
      public void buttonClick(ClickEvent event) {
        fieldGroup.discard();
      }
    }), 1, 2);
  }
}
