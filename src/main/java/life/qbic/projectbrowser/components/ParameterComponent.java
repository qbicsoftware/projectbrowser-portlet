/*******************************************************************************
 * QBiC Project qNavigator enables users to manage their projects. Copyright (C) "2016”
 * Christopher Mohr, David Wojnar, Andreas Friedrich
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.net.SyslogAppender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import submitter.Workflow;
import submitter.parameters.BooleanParameter;
import submitter.parameters.FloatParameter;
import submitter.parameters.InputList;
import submitter.parameters.IntParameter;
import submitter.parameters.Parameter;
import submitter.parameters.ParameterSet;
import submitter.parameters.StringParameter;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToFloatConverter;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.FloatRangeValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

import life.qbic.projectbrowser.helpers.*;

public class ParameterComponent extends WorkflowParameterComponent {

  /**
   * 
   */
  private static final long serialVersionUID = -3182823029401916444L;
  private static final Logger LOG = LogManager.getLogger(ParameterComponent.class);

  private FormLayout parameterForm = new FormLayout();
  private FieldGroup parameterFieldGroup;
  private FieldGroup inputListFieldGroup;
  private Workflow workFlow;

  public ParameterComponent(Workflow workFlow) {
    this.workFlow = workFlow;
    this.buildLayout(workFlow);
    setCompositionRoot(parameterForm);
  }

  public ParameterComponent() {
    setCompositionRoot(parameterForm);
  }

  @Override
  public void buildLayout(Workflow workFlow) {
    this.workFlow = workFlow;
    this.setCaption("<font color=#FF0000> Set Parameter Values for Workflow Submission </font>");
    this.setCaptionAsHtml(true);
    buildForm(workFlow);
  }

  public void buildForm(final Workflow workFlow) {

    parameterForm.removeAllComponents();
    parameterFieldGroup = new FieldGroup();
    inputListFieldGroup = new FieldGroup();

    /*
     * for (Map.Entry<String, Parameter> entry : workFlow.getData().getData().entrySet()) {
     * FileParameter param = (FileParameter) entry.getValue(); FileNameValidator fileNameValidator =
     * new FileNameValidator("Please provide a valid file path"); TextField newField =
     * createInputField(param, fileNameValidator);
     * 
     * parameterForm.addComponent(newField); inputListFieldGroup.bind(newField, entry.getKey()); //
     * Have to set it here because field gets cleared upon binding
     * newField.setValue(param.getValue().toString()); }
     */

    for (Map.Entry<String, Parameter> entry : workFlow.getParameters().getParams().entrySet()) {
      if (entry.getValue() instanceof FloatParameter) {
        FloatParameter param = (FloatParameter) entry.getValue();
        FloatRangeValidator floatValidator =
            new FloatRangeValidator(String.format("Parameter has to be in the range of %s to %s",
                param.getMinimum(), param.getMaximum()), param.getMinimum(), param.getMaximum());
        TextField newField =
            createParameterField(param, floatValidator, new StringToFloatConverter());

        parameterForm.addComponent(newField);
        parameterFieldGroup.bind(newField, entry.getKey());
        // Have to set it here because field gets cleared upon binding
        newField.setValue(param.getValue().toString());
        newField.setRequired(param.isRequired());
      }

      else if (entry.getValue() instanceof IntParameter) {
        IntParameter param = (IntParameter) entry.getValue();
        IntegerRangeValidator intValidator =
            new IntegerRangeValidator(String.format("Parameter has to be in the range of %s to %s",
                param.getMinimum(), param.getMaximum()), param.getMinimum(), param.getMaximum());
        TextField newField =
            createParameterField(param, intValidator, new StringToIntegerConverter());

        parameterForm.addComponent(newField);
        parameterFieldGroup.bind(newField, entry.getKey());
        // Have to set it here because field gets cleared upon binding
        newField.setValue(param.getValue().toString());
        newField.setRequired(param.isRequired());
      }

      else if (entry.getValue() instanceof StringParameter) {
        StringParameter param = (StringParameter) entry.getValue();
        // necessary for Microarray QC to create ComboBox instead of TextField
        if (param.getRange().size() == 0 && !workFlow.getName().equals("Microarray QC")) {
          TextField newField = createInputField(param, null);

          parameterForm.addComponent(newField);
          parameterFieldGroup.bind(newField, entry.getKey());
          // Have to set it here because field gets cleared upon binding
          newField.setValue(param.getValue().toString());
          newField.setRequired(param.isRequired());
        } else {
          ComboBox newField = createStringSelectionParameterField(param);
          parameterForm.addComponent(newField);
          parameterFieldGroup.bind(newField, entry.getKey());
          // Have to set it here because field gets cleared upon binding
          newField.setValue(param.getValue().toString());
          newField.setRequired(param.isRequired());
        }
      }

      else if (entry.getValue() instanceof BooleanParameter) {
        BooleanParameter param = (BooleanParameter) entry.getValue();

        CheckBox newField = createParameterCheckBox(param);
        newField.setValue((boolean) param.getValue());
        parameterForm.addComponent(newField);
        parameterFieldGroup.bind(newField, entry.getKey());
        // Have to set it here because field gets cleared upon binding
        newField.setValue((Boolean) param.getValue());
        newField.setRequired(param.isRequired());
      }
    }
  }

  @Override
  public Workflow getWorkflow() {
    boolean parametersValid = writeSetParameters();
    if (!parametersValid)
      return null;
    writetInputList();
    return this.workFlow;
  }

  /**
   * writes UI parameters to their model(workflow) equivalent returns true if all fields where set
   * with meaningfull values.
   * 
   * @return
   */
  boolean writeSetParameters() {
    Collection<Field<?>> registeredFields = parameterFieldGroup.getFields();
    ParameterSet paramSet = workFlow.getParameters();

    for (Field<?> field : registeredFields) {
      if (!field.isValid() || (field.isEmpty() & field.isRequired())) {
        String errorMessage = "Warning: Parameter " + field.getDescription() + "is invalid!";
        Utils
            .Notification(
                "Invalid parameter setting.",
                "Value for parameter \""
                    + field.getDescription()
                    + String
                        .format(
                            "\" is invalid or parameter field of required parameter (%s) must not be empty.",
                            field.getDescription()), "error");
        LOG.info(errorMessage);
        return false;
      }

      String value = field.getValue().toString();
      // paramSet.getParam(field.getCaption()).setValue(value);
      paramSet.getParam(field.getDescription()).setValue(value);
    }
    return true;
  }

  /**
   * Can be used to add Options to a Combobox that are only available at runtime (e.g. are created
   * from openBIS metainformation
   * 
   * @param caption Caption of the field, normally wf name followed by port and parameter name, e.g.
   *        "Microarray QC.1.f"
   * @param params Set of options for this parameter
   */
  public void setComboboxOptions(String caption, Set<String> params) {
    for (Field<?> field : parameterFieldGroup.getFields()) {
      if (field.getDescription().equals(caption)) {
        if (field instanceof ComboBox) {
          ((ComboBox) field).addItems(params);
        }
      }
    }
  }

  void writetInputList() {
    Collection<Field<?>> registeredFields = inputListFieldGroup.getFields();
    InputList inpList = workFlow.getData();

    for (Field<?> field : registeredFields) {
      inpList.getParam(field.getCaption()).setValue(field.getValue());
    }
  }

  @Override
  public void resetParameters() {
    Collection<Field<?>> registeredFields = parameterFieldGroup.getFields();
    ParameterSet paramSet = workFlow.getParameters();

    for (Field field : registeredFields) {
      // String resetValue = paramSet.getParam(field.getCaption()).getValue().toString();
      String resetValue = paramSet.getParam(field.getDescription()).getValue().toString();
      field.setValue(resetValue);
    }
  }

  public void resetInputList() {
    Collection<Field<?>> registeredFields = inputListFieldGroup.getFields();
    InputList inpList = workFlow.getData();

    for (Field<?> field : registeredFields) {
      TextField fieldToReset = (TextField) field;
      fieldToReset.setValue(inpList.getParam(field.getCaption()).getValue().toString());
    }
  }

  private TextField createParameterField(Parameter param, Validator validator, Converter converter) {
    // TextField field = new TextField(param.getTitle());
    // field.setDescription(param.getDescription());
    String description;
    if (param.getDescription().contains("#br#")) {
      description = param.getDescription().split("#br#")[0];
    } else {
      description = param.getDescription();
    }

    TextField field = new TextField(description);
    field.setDescription(param.getTitle());
    field.addValidator(validator);
    field.setNullRepresentation("");
    field.setNullSettingAllowed(true);
    field.setImmediate(true);
    field.setConverter(converter);
    return field;
  }

  private CheckBox createParameterCheckBox(Parameter param) {
    String description;
    if (param.getDescription().contains("#br#")) {
      description = param.getDescription().split("#br#")[0];
    } else {
      description = param.getDescription();
    }

    CheckBox box = new CheckBox(description);
    box.setDescription(param.getTitle());
    box.setImmediate(true);
    return box;
  }

  private TextField createInputField(Parameter param, Validator validator) {
    String description;
    if (param.getDescription().contains("#br#")) {
      description = param.getDescription().split("#br#")[0];
    } else {
      description = param.getDescription();
    }

    TextField field = new TextField(description);
    field.setDescription(param.getTitle());
    // field.setWidth("50%");
    if (validator != null) {
      field.addValidator(validator);
    }

    field.setImmediate(true);
    return field;
  }

  private ComboBox createStringSelectionParameterField(StringParameter param) {
    // ComboBox box = new ComboBox(param.getTitle());
    // box.setDescription(param.getDescription());
    // for openMS inis
    String description;
    if (param.getDescription().contains("#br#")) {
      description = param.getDescription().split("#br#")[0];
    } else {
      description = param.getDescription();
    }
    ComboBox box = new ComboBox(description);
    box.setDescription(param.getTitle());
    box.setFilteringMode(FilteringMode.CONTAINS);
    box.addItems(param.getRange());
    // should only be the range.
    box.setNullSelectionAllowed(false);
    box.setImmediate(true);
    return box;
  }

  @Override
  public void buildLayout() {
    // TODO Auto-generated method stub

  }


  @Override
  public ParameterSet getParameters() {
    Collection<Field<?>> registeredFields = parameterFieldGroup.getFields();
    ParameterSet paramSet = workFlow.getParameters();

    Map<String, Parameter> updatedParams = new HashMap<String, Parameter>();

    for (Field<?> field : registeredFields) {
      // Parameter updatedParam = paramSet.getParam(field.getCaption());
      Parameter updatedParam = paramSet.getParam(field.getDescription());
      updatedParam.setValue(field.getValue());
      // updatedParams.put(updatedParam.getTitle(), updatedParam);
      updatedParams.put(updatedParam.getDescription(), updatedParam);
    }

    ParameterSet updatedParamSet =
        new ParameterSet(workFlow.getName(), workFlow.getDescription(), updatedParams);
    return updatedParamSet;
  }
}
