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

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.remoting.RemoteAccessException;

import life.qbic.projectbrowser.model.maxquant.MaxQuantModel;
import life.qbic.projectbrowser.model.maxquant.MaxquantConverterFactory;
import life.qbic.projectbrowser.model.maxquant.RawFilesBean;
import life.qbic.projectbrowser.helpers.Utils;

import submitter.SubmitFailedException;
import submitter.Workflow;
import submitter.parameters.FileListParameter;
import submitter.parameters.FileParameter;
import submitter.parameters.Parameter;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WebBrowser;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.DetailsGenerator;
import com.vaadin.ui.Grid.RowReference;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

import life.qbic.projectbrowser.controllers.WorkflowViewController;

// from workflow API
import de.uni_tuebingen.qbic.beans.DatasetBean;
import fasta.FastaBean;
import fasta.FastaDB;

public class WorkflowComponent extends CustomComponent {

  /**
	 * 
	 */
  private static final long serialVersionUID = -2235244881205474571L;

  private static final Logger LOG = LogManager.getLogger(WorkflowComponent.class);
  private static final String WORKFKLOW_GRID_DESCRIPTION =
      "If you want to execute a workflow, click on one of the rows in the table. Then select the parameters, input files database/reference files and click on submit.";
  private static final String SUBMISSION_CAPTION = "Submission";
  protected static final String SUBMISSION_FAILED_MESSAGE =
      "Workflow submission failed due to internal errors! Please try again later or contact your project manager.";

  private WorkflowViewController controller;
  private VerticalLayout viewContent = new VerticalLayout();
  private VerticalLayout workflows;
  private Grid availableWorkflows = new Grid();
  private VerticalLayout submission;

  // data
  BeanItemContainer<DatasetBean> datasetBeans;
  private String type;
  private String id;

  public WorkflowComponent(WorkflowViewController controller) {
    this.controller = controller;
    this.setCaption("Workflows");
    init();
  }

  private void init() {
    VerticalLayout workflowLayout = new VerticalLayout();

    viewContent.setWidth("100%");
    viewContent.setMargin(true);

    // select available workflows
    workflows = new VerticalLayout();
    VerticalLayout workflowsContent = new VerticalLayout();
    workflows.setMargin(new MarginInfo(false, true, true, false));

    workflowsContent.addComponent(availableWorkflows);
    availableWorkflows.setSizeFull();
    availableWorkflows.setDescription(WORKFKLOW_GRID_DESCRIPTION);
    // availableWorkflows.setWidth("100%");
    workflows.setVisible(false);

    workflows.setCaption("Available Workflows");
    workflows.setIcon(FontAwesome.EXCHANGE);
    workflows.addComponent(workflowsContent);
    workflows.setWidth(100.0f, Unit.PERCENTAGE);

    // submission
    submission = new VerticalLayout();
    submission.setMargin(new MarginInfo(false, true, true, false));


    submission.setCaption(SUBMISSION_CAPTION);
    submission.setIcon(FontAwesome.PLAY);
    submission.setWidth(100.0f, Unit.PERCENTAGE);
    submission.setVisible(false);

    // add sections to layout
    viewContent.addComponent(workflows);
    viewContent.addComponent(submission);

    workflowLayout.addComponent(viewContent);
    setCompositionRoot(workflowLayout);
    addComponentListeners();
  }

  /**
   * updates view, if height, width or the browser changes.
   * 
   * @param browserHeight
   * @param browserWidth
   * @param browser
   */
  public void updateView(int browserHeight, int browserWidth, WebBrowser browser) {
    setWidth((browserWidth * 0.6f), Unit.PIXELS);
  }

  public void update(Map<String, String> map) {

    if (map == null)
      return;
    // TODO In background thread?
    type = map.get("type");
    id = map.get("id");

    switch (type) {
      case "project":
        datasetBeans = controller.getcontainer(type, id);
        List<String> datasetTypesInProject = new ArrayList<String>();

        for (Iterator<DatasetBean> i = datasetBeans.getItemIds().iterator(); i.hasNext();) {
          DatasetBean dsBean = (DatasetBean) i.next();
          datasetTypesInProject.add(dsBean.getFileType());
        }
        updateWorkflowSelection(datasetTypesInProject);
        break;

      case "experiment":
        break;

      case "sample":
        break;

      case "workflowExperimentType":
        String experiment = map.get("experiment");

        BeanItemContainer<Workflow> suitableWorkflows =
            controller.suitableWorkflowsByExperimentType(experiment);

        BeanItemContainer<DatasetBean> suitableDatasets =
            new BeanItemContainer<DatasetBean>(DatasetBean.class);

        List<String> workflowDatasetTypes = new ArrayList<String>();
        for (Iterator i = suitableWorkflows.getItemIds().iterator(); i.hasNext();) {
          Workflow workflowBean = (Workflow) i.next();

          Map<String, List<String>> requiredFiles = workflowBean.getFileTypes().get("required");

          for (Entry<String, List<String>> entry : requiredFiles.entrySet()) {
            workflowDatasetTypes.addAll(entry.getValue());
          }

        }

        for (Iterator i = controller.getcontainer("project", id).getItemIds().iterator(); i
            .hasNext();) {
          DatasetBean datasetBean = (DatasetBean) i.next();

          if (workflowDatasetTypes.contains(datasetBean.getFileType())) {
            suitableDatasets.addBean(datasetBean);
          }
        }

        datasetBeans = suitableDatasets;
        updateSelection(suitableWorkflows);
        break;

      default:
        updateSelection(new BeanItemContainer<Workflow>(Workflow.class));
        break;
    }
  }

  protected void updateWorkflowSelection(DatasetBean dataset) {
    updateSelection(controller.suitableWorkflows(dataset.getFileType()));
  }

  protected void updateWorkflowSelection(List<String> datasetTypes) {
    updateSelection(controller.suitableWorkflows(datasetTypes));
  }

  protected void updateWorkflowSelection(String experimentType) {
    updateSelection(controller.suitableWorkflowsByExperimentType(experimentType));
  }

  /**
   * updates availableWorkflows to contain only workflows according to dataset selection.
   * 
   * @param suitableWorkflows
   */
  void updateSelection(BeanItemContainer<Workflow> suitableWorkflows) {
    this.submission.setCaption("");
    this.submission.removeAllComponents();

    if (!(suitableWorkflows.size() > 0)) {
      Utils
          .Notification(
              "No suitable workflows available.",
              "No workflows are shown because no suitable data is available in this project. If this is unexpected please contact your project manager.",
              "info");
    }

    availableWorkflows.setContainerDataSource(filtergpcontainer(suitableWorkflows));
    // availableWorkflows.setColumnOrder("name", "version", "fileTypes");
    availableWorkflows.setColumnOrder("name", "version");

    workflows.setVisible(true);
  }

  void showNotification(String message) {
    Notification notif = new Notification(message, Type.TRAY_NOTIFICATION);
    // Customize it
    notif.setDelayMsec(60000);
    notif.setPosition(Position.MIDDLE_CENTER);
    // Show it in the page
    notif.show(Page.getCurrent());

  }

  /**
   * filter grid columns
   * 
   * @param suitableWorkflows
   * @return
   */
  GeneratedPropertyContainer filtergpcontainer(BeanItemContainer<Workflow> suitableWorkflows) {
    // ONLY SHOW SPECIFIC COLUMNS IN GRID
    GeneratedPropertyContainer gpcontainer = new GeneratedPropertyContainer(suitableWorkflows);

    gpcontainer.removeContainerProperty("ID");
    gpcontainer.removeContainerProperty("fileTypes");
    gpcontainer.removeContainerProperty("data");
    gpcontainer.removeContainerProperty("datasetType");
    gpcontainer.removeContainerProperty("nodes");
    gpcontainer.removeContainerProperty("experimentType");
    gpcontainer.removeContainerProperty("parameterToNodesMapping");
    gpcontainer.removeContainerProperty("parameters");
    gpcontainer.removeContainerProperty("sampleType");
    gpcontainer.removeContainerProperty("description");
    return gpcontainer;
  }

  private void updateParameterView(Workflow workFlow, BeanItemContainer<DatasetBean> projectDatasets) {
    this.submission.setCaption(SUBMISSION_CAPTION + ": " + workFlow.getName());
    this.submission.removeAllComponents();

    if (workFlow.getName().equals("MaxQuant")) {
      BeanItemContainer<RawFilesBean> rawFilesBeans =
          new BeanItemContainer<RawFilesBean>(RawFilesBean.class);
      BeanItemContainer<FastaBean> selectedfastas =
          new BeanItemContainer<FastaBean>(FastaBean.class);
      BeanItemContainer<FastaBean> fastas = new BeanItemContainer<FastaBean>(FastaBean.class);
      FastaDB db = new FastaDB();
      fastas.addAll(db.getAll());

      Map<String, Parameter> params = workFlow.getData().getData();
      BeanItemContainer<DatasetBean> subContainer =
          new BeanItemContainer<DatasetBean>(DatasetBean.class);


      for (Entry<String, Parameter> entry : params.entrySet()) {

        if (entry.getValue() instanceof FileParameter
            || entry.getValue() instanceof FileListParameter) {

          List<String> associatedDataTypes = new ArrayList<String>();

          if (entry.getValue() instanceof FileParameter) {
            associatedDataTypes = ((FileParameter) entry.getValue()).getRange();
          } else if (entry.getValue() instanceof FileListParameter) {
            associatedDataTypes = ((FileListParameter) entry.getValue()).getRange();
          }

          if (associatedDataTypes.contains("fasta") || associatedDataTypes.contains("gtf")) {
            continue;
          } else {
            for (Iterator<DatasetBean> i = projectDatasets.getItemIds().iterator(); i
                .hasNext();) {
              DatasetBean dataset = i.next();

              if (associatedDataTypes.contains(dataset.getFileType())) {
                subContainer.addBean(dataset);

              }
            }
          }
        }
      }

      MaxQuantModel model = new MaxQuantModel(rawFilesBeans, subContainer, selectedfastas, fastas);
      VaadinSession.getCurrent().setConverterFactory(new MaxquantConverterFactory());
      MaxQuantComponent maxquantComponent = new MaxQuantComponent(model, controller);
      maxquantComponent.setWorkflow(workFlow);
      maxquantComponent.addSubmissionListener(new MaxQuantSubmissionListener(maxquantComponent));

      this.submission.addComponent(maxquantComponent);
    } else if (workFlow.getName().contains("Microarray QC")) {
      MicroarrayQCComponent qcComp = new MicroarrayQCComponent(controller);
      qcComp.update(workFlow, projectDatasets);
      qcComp.addResetListener(new MicroarrayQCResetListener(qcComp));
      qcComp.addSubmissionListener(new MicroarrayQCSubmissionListener(qcComp));
      this.submission.addComponent(qcComp);
    } else if ((workFlow.getName().contains("NGS Read Alignment"))
        || (workFlow.getName().contains("RNA-seq"))) {
      NGSMappingComponent mappComp = new NGSMappingComponent(controller);
      mappComp.update(workFlow, projectDatasets);
      mappComp.addResetListener(new NGSMappingResetListener(mappComp));
      mappComp.addSubmissionListener(new NGSMappingSubmissionListener(mappComp));
      this.submission.addComponent(mappComp);
    } else if (workFlow.getName().contains("Differential")) {
      DifferentialExpressionComponent diffComp = new DifferentialExpressionComponent(controller);
      diffComp.update(workFlow, projectDatasets);
      diffComp.addResetListener(new DifferentialExpressionResetListener(diffComp));
      diffComp.addSubmissionListener(new DifferentialExpressionSubmissionListener(diffComp));
      this.submission.addComponent(diffComp);
    } else {
      StandardWorkflowComponent standardComponent = new StandardWorkflowComponent(controller);
      standardComponent.update(workFlow, projectDatasets);
      standardComponent.addResetListener(new ResetListener(standardComponent));
      standardComponent.addSubmissionListener(new StandardSubmissionListener(standardComponent));
      this.submission.addComponent(standardComponent);
    }
  }

  private void addComponentListeners() {

    availableWorkflows.setDetailsGenerator(new DetailsGenerator() {
      private static final long serialVersionUID = 6123522348935657638L;

      @Override
      public Component getDetails(RowReference rowReference) {
        Workflow w = (Workflow) rowReference.getItemId();

        Label description = new Label(w.getDescription(), ContentMode.HTML);
        description.setCaption("Description");

        VerticalLayout main = new VerticalLayout(description);
        main.setMargin(true);
        return main;
      }
    });

    availableWorkflows.addItemClickListener(new ItemClickListener() {
      private static final long serialVersionUID = 3786125825391677177L;

      @Override
      public void itemClick(ItemClickEvent event) {
        // TODO get path of datasetBean and set it as input ?!
        Workflow selectedWorkflow = (Workflow) event.getItemId();
        if (selectedWorkflow != null) {
          updateParameterView(selectedWorkflow, datasetBeans);
          submission.setVisible(true);

          // detailed Description should be visible
          availableWorkflows.setDetailsVisible(selectedWorkflow,
              !availableWorkflows.isDetailsVisible(selectedWorkflow));

        } else {
          LOG.warn("selected Workflow is null?");
          submission.setVisible(false);
        }

      }
    });
    availableWorkflows.setEditorEnabled(false);
  }

  private class ResetListener implements ClickListener {
    private static final long serialVersionUID = 6800369140265363672L;
    private StandardWorkflowComponent swc;

    public ResetListener(StandardWorkflowComponent standardWorkflowComponent) {
      swc = standardWorkflowComponent;
    }

    @Override
    public void buttonClick(ClickEvent event) {
      swc.resetParameters();
    }
  }

  /**
   * 
   * @author mohr
   * 
   */
  public class DifferentialExpressionResetListener implements ClickListener {
    private static final long serialVersionUID = -127474228749885664L;
    private DifferentialExpressionComponent qcc;

    public DifferentialExpressionResetListener(DifferentialExpressionComponent wfComp) {
      qcc = wfComp;
    }

    @Override
    public void buttonClick(ClickEvent event) {
      qcc.resetParameters();
    }
  }

  /**
   * 
   * @author mohr
   * 
   */
  private class DifferentialExpressionSubmissionListener implements ClickListener {
    private static final long serialVersionUID = 24386950203184318L;
    private DifferentialExpressionComponent comp;

    public DifferentialExpressionSubmissionListener(DifferentialExpressionComponent comp) {
      this.comp = comp;
    }

    @Override
    public void buttonClick(ClickEvent event) {
      try {
        List<DatasetBean> selectedDatasets = comp.getSelectedDatasets();
        comp.writeParametersToWorkflow();
        Workflow submittedWf = comp.getWorkflow();
        submit(submittedWf, new ArrayList<DatasetBean>(selectedDatasets));
      } catch (Exception e) {
        handleException(e);
      }
    }
  }
  /**
   * 
   * @author mohr
   * 
   */
  public class NGSMappingResetListener implements ClickListener {
    private static final long serialVersionUID = -127474228749885664L;
    private NGSMappingComponent qcc;

    public NGSMappingResetListener(NGSMappingComponent wfComp) {
      qcc = wfComp;
    }

    @Override
    public void buttonClick(ClickEvent event) {
      qcc.resetParameters();
    }
  }

  /**
   * 
   * @author mohr
   * 
   */
  private class NGSMappingSubmissionListener implements ClickListener {
    private static final long serialVersionUID = 24386950203184318L;
    private NGSMappingComponent comp;

    public NGSMappingSubmissionListener(NGSMappingComponent comp) {
      this.comp = comp;
    }

    @Override
    public void buttonClick(ClickEvent event) {
      try {
        List<DatasetBean> selectedDatasets = comp.getSelectedDatasets();
        comp.writeParametersToWorkflow();
        Workflow submittedWf = comp.getWorkflow();
        submit(submittedWf, new ArrayList<DatasetBean>(selectedDatasets));
      } catch (Exception e) {
        handleException(e);
      }
    }
  }

  public class MicroarrayQCResetListener implements ClickListener {
    private static final long serialVersionUID = -127474228749885664L;
    private MicroarrayQCComponent qcc;

    public MicroarrayQCResetListener(MicroarrayQCComponent wfComp) {
      qcc = wfComp;
    }

    @Override
    public void buttonClick(ClickEvent event) {
      qcc.resetParameters();
    }
  }

  /**
   * listens to clicks on submit button. Executes microarray qc workflow.
   * 
   * @author friedrich
   * 
   */
  private class MicroarrayQCSubmissionListener implements ClickListener {
    private static final long serialVersionUID = 24386950203184318L;
    private MicroarrayQCComponent comp;

    public MicroarrayQCSubmissionListener(MicroarrayQCComponent comp) {
      this.comp = comp;
    }

    @Override
    public void buttonClick(ClickEvent event) {
      try {
        List<DatasetBean> selectedDatasets = comp.getSelectedDatasets();
        comp.writeParametersToWorkflow();
        Workflow submittedWf = comp.getWorkflow();
        submit(submittedWf, new ArrayList<DatasetBean>(selectedDatasets));
      } catch (Exception e) {
        handleException(e);
      }
    }
  }

  /**
   * listenes to clicks on submit button. Executes standard workflow.
   * 
   * @author wojnar
   * 
   */
  private class StandardSubmissionListener implements ClickListener {
    private static final long serialVersionUID = 243869502031843198L;
    private StandardWorkflowComponent swc;

    public StandardSubmissionListener(StandardWorkflowComponent standardWorkflowComponent) {
      swc = standardWorkflowComponent;
    }

    @Override
    public void buttonClick(ClickEvent event) {
      try {
        List<DatasetBean> selectedDatasets = swc.getSelectedDatasets();
        Workflow submittedWf = swc.getWorkflow();
        submit(submittedWf, new ArrayList<DatasetBean>(selectedDatasets));
      } catch (Exception e) {
        handleException(e);
      }
    }
  }
  /**
   * listenes to clicks on submit button. Executes maxquant workflow.
   * 
   * @author wojnar
   * 
   */
  private class MaxQuantSubmissionListener implements ClickListener {
    private static final long serialVersionUID = 1888557742642278371L;
    private MaxQuantComponent maxQuantComponent;

    public MaxQuantSubmissionListener(MaxQuantComponent mqc) {
      maxQuantComponent = mqc;
    }

    @Override
    public void buttonClick(ClickEvent event) {

      try {
        maxQuantComponent.updateGroups();
        maxQuantComponent.writeParametersToWorkflow();
        Workflow submittedWf = maxQuantComponent.getWorkflow();
        Collection<DatasetBean> selectedDatasets = maxQuantComponent.getSelectedDatasets();
        submit(submittedWf, new ArrayList<DatasetBean>(selectedDatasets));
      } catch (Exception e) {
        // handleException(e);
        e.printStackTrace();
      }
    }

  }

  /**
   * submits workflow with given datasets
   * 
   * @throws SubmitFailedException
   * @throws IllegalArgumentException
   * @throws ConnectException
   */
  void submit(Workflow submittedWf, List<DatasetBean> selectedDatasets) throws ConnectException,
      IllegalArgumentException, SubmitFailedException {
    if (submittedWf == null || selectedDatasets.isEmpty()) {
      return;
    }
    // THIS IS THE IMPORTANT LINE IN THAT MESS
    String openbisId = controller.submitAndRegisterWf(type, id, submittedWf, selectedDatasets);
    Utils.Notification("Workflow submitted",
        "Workflow submitted successfully and saved under " + openbisId, "success");
    // showNotification("Workflow submitted and saved under " + openbisId);
  }

  /**
   * logs error and shows user that submission failed. Different exceptions yield different error
   * messages
   * 
   * @param e
   */
  public void handleException(Exception e) {
    if (e instanceof ConnectException || e instanceof IllegalArgumentException
        || e instanceof SubmitFailedException) {
      LOG.error("Submission failed, probably gUSE. " + e.getMessage(), e.getStackTrace());
      Utils
          .Notification(
              "Workflow submission failed",
              "Requested workflow could not be submitted due to internal errors. Please try again later and contact your project manager if the problem persists.",
              "error");
      try {
        VaadinService
            .getCurrentResponse()
            .sendError(
                HttpServletResponse.SC_GATEWAY_TIMEOUT,
                "An error occured, while trying to connect to the database. Please try again later, or contact your project manager.");
      } catch (IOException | IllegalArgumentException e1) {
        // TODO Auto-generated catch block
        LOG.error("Something went wrong: " + e.getMessage(), e.getStackTrace());
        VaadinService.getCurrentResponse().setStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT);
      }
    } else if (e instanceof RemoteAccessException) {
      LOG.error("Submission failed, probably openbis. error message: " + e.getMessage(),
          e.getStackTrace());
      Utils
          .Notification(
              "Workflow submission failed",
              "Requested workflow could not be submitted due to internal errors. Please try again later and contact your project manager if the problem persists.",
              "error");
    } else {
      LOG.error("Internal error: " + e.getMessage(), e.getStackTrace());
      Utils
          .Notification(
              "Workflow submission failed",
              "Requested workflow could not be submitted due to internal errors. Please try again later and contact your project manager if the problem persists.",
              "error");
    }
  }
}
