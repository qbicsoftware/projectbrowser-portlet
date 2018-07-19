package life.qbic.projectbrowser.components;

import guse.workflowrepresentation.GuseNode;
import guse.workflowrepresentation.GuseWorkflowRepresentation;
import guse.workflowrepresentation.InputPort;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

import submitter.Workflow;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import life.qbic.projectbrowser.controllers.WorkflowViewController;

// from workflow API
import de.uni_tuebingen.qbic.beans.DatasetBean;

public class DifferentialExpressionComponent extends CustomComponent {

  private static final Logger LOG = LogManager.getLogger(DifferentialExpressionComponent.class);

  private Button submit = new Button("Submit");
  private Button reset = new Button("Reset");

  private ParameterComponent parameterComponent = new ParameterComponent();
  private InputFilesComponent inputFileComponent = new InputFilesComponent();

  private WorkflowViewController controller;

  public DifferentialExpressionComponent(WorkflowViewController controller) {
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

  public Map<String, List<DatasetBean>> getSelectedDatasetsAsMap() {
    return inputFileComponent.getSelectedDatasetsAsMap();
  }

  public Workflow getWorkflow() {
    Workflow tmp = parameterComponent.getWorkflow();
    boolean success = inputFileComponent.updateWorkflow(tmp, controller);
    return success ? tmp : null;
  }

  public void update(Workflow workflow, BeanItemContainer<DatasetBean> input) {
    this.inputFileComponent.buildLayout(workflow.getData().getData(), input);
    this.parameterComponent.buildLayout(workflow);
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

      String header = "filename\tQBIC_ID\tinternal_ID\ttreatment\ttime\tgroup\tbio_rep";

      StringBuilder tsv = new StringBuilder(header);

      // TODO iterate over map
      for (Map.Entry<String, List<DatasetBean>> entry : inputFileComponent
          .getSelectedDatasetsAsMap().entrySet()) {
        String group = "";
        if (entry.getKey().contains("tumor")) {
          group = "tumor";
        } else if (entry.getKey().contains("normal")) {
          group = "normal";
        } else {
          group = "unknown";
        }
        for (DatasetBean b : entry.getValue()) {
          String file = b.getFileName();
          String[] splittedID = b.getSampleIdentifier().split("/");
          String id = splittedID[splittedID.length - 1];
          tsv.append("\n" + file + "\t" + id + "\t" + "-" + "\t" + "-" + "\t" + "-" + "\t" + group
              + "\t" + "-");
        }
      }
      tsv.append("\n");

      GuseNode node = w.getNode("Workflow");
      InputPort port = node.getPort("GROUPS");
      port.getParams().get("groups").setValue(tsv.toString());
    }

  }
}
