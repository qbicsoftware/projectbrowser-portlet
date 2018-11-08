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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import life.qbic.projectbrowser.model.ExperimentStatusBean;
import life.qbic.projectbrowser.model.ProjectBean;
import life.qbic.projectbrowser.controllers.*;
import life.qbic.projectbrowser.views.PatientView;
import life.qbic.projectbrowser.helpers.Utils;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickListener;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.ProgressBarRenderer;


public class PatientStatusComponent extends CustomComponent {
  private DataHandler datahandler;
  private String resourceUrl;
  private State state;
  private VerticalLayout status;
  private Grid experiments;

  public PatientStatusComponent(DataHandler dh, State state, String resourceurl) {
    this.datahandler = dh;
    this.resourceUrl = resourceurl;
    this.state = state;

    this.setCaption("Status");

    this.initUI();
  }

  private void initUI() {
    status = new VerticalLayout();
    status.setWidth(100.0f, Unit.PERCENTAGE);

    status.setMargin(new MarginInfo(true, false, false, true));
    status.setSpacing(true);

    // status.setSizeFull();

    VerticalLayout projectStatus = new VerticalLayout();
    projectStatus.setMargin(new MarginInfo(true, false, true, true));
    projectStatus.setSpacing(true);

    experiments = new Grid();
    experiments.setReadOnly(true);
    experiments.setWidth(100.0f, Unit.PERCENTAGE);
    // experiments.setHeightMode(HeightMode.ROW);
    status.addComponent(experiments);

    ProgressBar progressBar = new ProgressBar();
    progressBar.setValue(0f);
    status.addComponent(progressBar);
    projectStatus.addComponent(status);

    this.setWidth(Page.getCurrent().getBrowserWindowWidth() * 0.85f, Unit.PIXELS);
    this.setCompositionRoot(projectStatus);
  }

  public void updateUI(final ProjectBean currentBean) {
    BeanItemContainer<ExperimentStatusBean> experimentstatusBeans =
        datahandler.computeIvacPatientStatus(currentBean);

    int finishedExperiments = 0;
    status.removeAllComponents();
    status.setWidth(100.0f, Unit.PERCENTAGE);


    // Generate button caption column
    final GeneratedPropertyContainer gpc = new GeneratedPropertyContainer(experimentstatusBeans);
    gpc.addGeneratedProperty("started", new PropertyValueGenerator<String>() {

      @Override
      public Class<String> getType() {
        return String.class;
      }

      @Override
      public String getValue(Item item, Object itemId, Object propertyId) {
        String status = null;

        if ((double) item.getItemProperty("status").getValue() > 0.0) {
          status =
              "<span class=\"v-icon\" style=\"font-family: " + FontAwesome.CHECK.getFontFamily()
                  + ";color:" + "#2dd085" + "\">&#x"
                  + Integer.toHexString(FontAwesome.CHECK.getCodepoint()) + ";</span>";
        } else {
          status =
              "<span class=\"v-icon\" style=\"font-family: " + FontAwesome.TIMES.getFontFamily()
                  + ";color:" + "#f54993" + "\">&#x"
                  + Integer.toHexString(FontAwesome.TIMES.getCodepoint()) + ";</span>";
        }

        return status.toString();
      }
    });
    gpc.removeContainerProperty("identifier");

    experiments.setContainerDataSource(gpc);
    // experiments.setHeaderVisible(false);
    // experiments.setHeightMode(HeightMode.ROW);
    experiments.setHeightByRows(gpc.size());
    experiments.setWidth(Page.getCurrent().getBrowserWindowWidth() * 0.6f, Unit.PIXELS);



    experiments.getColumn("status").setRenderer(new ProgressBarRenderer());
    // experiments.setColumnOrder("started", "code", "description", "status", "download",
    // "runWorkflow");
    experiments.setColumnOrder("started", "code", "description", "status", "workflow");

    experiments.getColumn("workflow").setRenderer(new ButtonRenderer(new RendererClickListener() {
      @Override
      public void click(RendererClickEvent event) {
        ExperimentStatusBean esb = (ExperimentStatusBean) event.getItemId();
        TabSheet parent = (TabSheet) getParent();
        PatientView pv = (PatientView) parent.getParent().getParent();
        WorkflowComponent wp = pv.getWorkflowComponent();

        // TODO WATCH OUT NUMBER OF WORKFLOW TAB IS HARDCODED AT THE MOMENT, NO BETTER SOLUTION
        // FOUND SO FAR, e.g. get Tab by Name ?

        // TODO idea get description of item to navigate to the correct workflow ?!
        if (esb.getDescription().equals("Barcode Generation")) {
          ArrayList<String> message = new ArrayList<String>();
          message.add("clicked");
          message.add(currentBean.getId());
          //TODO navigate to barcode dragon rawwwr
//          message.add(BarcodeView.navigateToLabel);
//          state.notifyObservers(message);
        } else if (esb.getDescription().equals("Variant Annotation")) {
          /*
           * ArrayList<String> message = new ArrayList<String>(); message.add("clicked");
           * StringBuilder sb = new StringBuilder("type="); sb.append("workflowExperimentType");
           * sb.append("&"); sb.append("id="); sb.append(currentBean.getId()); sb.append("&");
           * sb.append("experiment="); sb.append("Q_WF_NGS_VARIANT_ANNOTATION");
           * message.add(sb.toString()); message.add(WorkflowView.navigateToLabel);
           * state.notifyObservers(message);
           */

          Map<String, String> args = new HashMap<String, String>();
          args.put("id", currentBean.getId());
          args.put("type", "workflowExperimentType");
          args.put("experiment", "Q_WF_NGS_VARIANT_ANNOTATION");
          parent.setSelectedTab(8);
          wp.update(args);

        } else if (esb.getDescription().equals("Epitope Prediction")) {
          /*
           * ArrayList<String> message = new ArrayList<String>(); message.add("clicked");
           * StringBuilder sb = new StringBuilder("type="); sb.append("workflowExperimentType");
           * sb.append("&"); sb.append("id="); sb.append(currentBean.getId()); sb.append("&");
           * sb.append("experiment="); sb.append("Q_WF_NGS_EPITOPE_PREDICTION");
           * message.add(sb.toString()); message.add(WorkflowView.navigateToLabel);
           * state.notifyObservers(message);
           */
          Map<String, String> args = new HashMap<String, String>();
          args.put("id", currentBean.getId());
          args.put("type", "workflowExperimentType");
          args.put("experiment", "Q_WF_NGS_EPITOPE_PREDICTION");
          parent.setSelectedTab(8);
          wp.update(args);
        } else if (esb.getDescription().equals("HLA Typing")) {
          /*
           * ArrayList<String> message = new ArrayList<String>(); message.add("clicked");
           * StringBuilder sb = new StringBuilder("type="); sb.append("workflowExperimentType");
           * sb.append("&"); sb.append("id="); sb.append(currentBean.getId()); sb.append("&");
           * sb.append("experiment="); sb.append("Q_WF_NGS_HLATYPING"); message.add(sb.toString());
           * message.add(WorkflowView.navigateToLabel); state.notifyObservers(message);
           */
          Map<String, String> args = new HashMap<String, String>();
          args.put("id", currentBean.getId());
          args.put("type", "workflowExperimentType");
          args.put("experiment", "Q_WF_NGS_HLATYPING");
          parent.setSelectedTab(8);
          wp.update(args);
        }

        else {
          Utils.Notification("Not available.", "Workflow not available or no workflow selected.", "General");
        }
      }
    }));

    experiments.getColumn("started").setRenderer(new HtmlRenderer());

    ProgressBar progressBar = new ProgressBar();
    progressBar.setCaption("Overall Progress");
    progressBar.setWidth(Page.getCurrent().getBrowserWindowWidth() * 0.6f, Unit.PIXELS);
    progressBar.setStyleName("patientprogress");

    status.addComponent(progressBar);
    status.addComponent(experiments);
    status.setComponentAlignment(progressBar, Alignment.MIDDLE_CENTER);
    status.setComponentAlignment(experiments, Alignment.MIDDLE_CENTER);


    /**
     * Defined Experiments for iVac - Barcodes available -> done with project creation (done) -
     * Sequencing done (Status Q_NGS_MEASUREMENT) - Variants annotated (Status
     * Q_NGS_VARIANT_CALLING) - HLA Typing done (STATUS Q_NGS_WF_HLA_TYPING) - Epitope Prediction
     * done (STATUS Q_WF_NGS_EPITOPE_PREDICTION)
     */


    for (Iterator i = experimentstatusBeans.getItemIds().iterator(); i.hasNext();) {
      ExperimentStatusBean statusBean = (ExperimentStatusBean) i.next();

      finishedExperiments += statusBean.getStatus();

      // statusBean.setDownload("Download");
      statusBean.setWorkflow("Run");
    }


    progressBar.setValue((float) finishedExperiments / experimentstatusBeans.size());
  }

}
