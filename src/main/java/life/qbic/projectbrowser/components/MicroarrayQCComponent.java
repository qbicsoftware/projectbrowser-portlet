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

import guse.workflowrepresentation.GuseNode;
import guse.workflowrepresentation.GuseWorkflowRepresentation;
import guse.workflowrepresentation.InputPort;

import java.util.List;
import java.util.Map;

import org.json.JSONException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import life.qbic.projectbrowser.controllers.WorkflowViewController;

import submitter.Workflow;

import de.uni_tuebingen.qbic.beans.DatasetBean;

public class MicroarrayQCComponent extends CustomComponent {

  private static final Logger LOG = LogManager.getLogger(MicroarrayQCComponent.class);

  private Button submit = new Button("Submit");
  private Button reset = new Button("Reset");

  private ParameterComponent parameterComponent = new ParameterComponent();
  private InputFilesComponent inputFileComponent = new InputFilesComponent();

  private WorkflowViewController controller;

  public MicroarrayQCComponent(WorkflowViewController controller) {
    this.controller = controller;
    reset.setDescription("Reset Parameters to default values.");
    submit
        .setDescription("Execute Workflow. With given input files, database/reference files and parameters.");

    HorizontalLayout buttonContent = new HorizontalLayout();
    buttonContent.addComponent(reset);
    buttonContent.addComponent(submit);

    VerticalLayout submissionContent = new VerticalLayout();
    submissionContent.setSpacing(true);
    submissionContent.addComponent(inputFileComponent);
    submissionContent.addComponent(parameterComponent);
    submissionContent.addComponent(buttonContent);
    setCompositionRoot(submissionContent);
  }

  public List<DatasetBean> getSelectedDatasets() {
    return inputFileComponent.getSelectedDatasets();
  }

  public Workflow getWorkflow() {
    Workflow tmp = parameterComponent.getWorkflow();
    boolean success = inputFileComponent.updateWorkflow(tmp, controller);
    return success ? tmp : null;
  }

  public void update(Workflow workflow, BeanItemContainer<DatasetBean> input) {
    this.inputFileComponent.buildLayout(workflow.getData().getData(), input);
    this.parameterComponent.buildLayout(workflow);
    this.parameterComponent.setComboboxOptions("Microarray QC.1.f",
        controller.getExperimentalFactors());
  }

  public void resetParameters() {
    parameterComponent.resetParameters();
  }

  public void addResetListener(ClickListener listener) {
    reset.addClickListener(listener);
  }

  public void addSubmissionListener(ClickListener listener) {
    submit.addClickListener(listener);
  }

  public void writeParametersToWorkflow() throws IllegalArgumentException, JSONException {
    if (parameterComponent.getWorkflow() instanceof GuseWorkflowRepresentation) {
      GuseWorkflowRepresentation w = (GuseWorkflowRepresentation) parameterComponent.getWorkflow();

      String header = "file\tinternal_ID";
      for (String f : controller.getExperimentalFactors())
        header = header + "\t" + f;
      
      StringBuilder tsv = new StringBuilder(header);
      
      Map<String,String> fileProps = controller.getExperimentalPropsForFiles();
      for(DatasetBean b : inputFileComponent.getSelectedDatasets()) {
        String file = b.getFileName();
        tsv.append("\n"+file+"\t"+fileProps.get(file));
      }
      tsv.append("\n");
      
      GuseNode node = w.getNode("Workflow");
      InputPort port = node.getPort("PHENOFILE");
      port.getParams().get("pheno").setValue(tsv.toString());
    }

  }


}
