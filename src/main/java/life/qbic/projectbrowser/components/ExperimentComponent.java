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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickListener;

import life.qbic.portal.portlet.ProjectBrowserPortlet;
import life.qbic.projectbrowser.helpers.Utils;
import life.qbic.projectbrowser.controllers.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import life.qbic.projectbrowser.model.ExperimentBean;
import life.qbic.projectbrowser.model.ProjectBean;

public class ExperimentComponent extends CustomComponent {
  /**
   * 
   */
  private static final long serialVersionUID = -278474320056258264L;

  private static final Logger LOG = LogManager.getLogger(ExperimentComponent.class);

  private DataHandler datahandler;
  private String resourceUrl;
  private State state;
  private VerticalLayout expSteps;
  private Grid experiments;
  private Button export;
  private ProjectBean projectBean;
  private ChangeExperimentMetadataComponent changeMetadata;


  private FileDownloader fileDownloader;

  public ExperimentComponent(DataHandler dh, State state, String resourceurl) {
    this.datahandler = dh;
    this.resourceUrl = resourceurl;
    this.state = state;

    changeMetadata = new ChangeExperimentMetadataComponent(dh, state, resourceurl);

    this.setCaption("Exp. Steps");

    this.initUI();
  }

  private void initUI() {
    expSteps = new VerticalLayout();
    expSteps.setWidth(100.0f, Unit.PERCENTAGE);

    expSteps.setMargin(new MarginInfo(true, true, true, true));
    expSteps.setSpacing(true);

    export = new Button("Export as TSV");
    export.setIcon(FontAwesome.DOWNLOAD);
    VerticalLayout buttonLayoutSection = new VerticalLayout();
    HorizontalLayout buttonLayout = new HorizontalLayout();
    buttonLayout.setHeight(null);
    buttonLayout.setWidth("100%");
    buttonLayout.addComponent(this.export);
    buttonLayout.setMargin(new MarginInfo(false, false, false, false));
    buttonLayoutSection.addComponent(buttonLayout);
    buttonLayoutSection.setSpacing(true);
    buttonLayoutSection.setMargin(new MarginInfo(false, false, false, false));

    experiments = new Grid();
    experiments.setReadOnly(true);
    experiments.setWidth(100.0f, Unit.PERCENTAGE);
    experiments.setCaption("Registered Experimental Steps");

    // experiments.setContainerDataSource(new
    // BeanItemContainer<ExperimentBean>(ExperimentBean.class));

    expSteps.addComponent(new Label(
        "This view shows the experimental steps which have been registered for this project. Experimental steps contain real biological experiments as well as executed computational workflows on project data",
        ContentMode.HTML));

    expSteps.addComponent(experiments);
    expSteps.addComponent(buttonLayoutSection);

    setCompositionRoot(expSteps);

  }

  public void updateUI(ProjectBean currentBean) {
    projectBean = currentBean;
    experiments.removeAllColumns();
    // experiments.setContainerDataSource(projectBean.getExperiments());

    // BeanItemContainer<ExperimentBean> experimentBeans =
    // loadMoreExperimentInformation(projectBean.getExperiments());
    // GeneratedPropertyContainer gpc = new GeneratedPropertyContainer(experimentBeans);
    GeneratedPropertyContainer gpc = new GeneratedPropertyContainer(projectBean.getExperiments());

    gpc.removeContainerProperty("containsData");
    gpc.removeContainerProperty("controlledVocabularies");
    gpc.removeContainerProperty("id");
    gpc.removeContainerProperty("lastChangedDataset");
    gpc.removeContainerProperty("lastChangedSample");
    gpc.removeContainerProperty("properties");
    gpc.removeContainerProperty("type");
    gpc.removeContainerProperty("samples");
    gpc.removeContainerProperty("status");
    gpc.removeContainerProperty("typeLabels");
    gpc.removeContainerProperty("registrationDate");


    experiments.addItemClickListener(new ItemClickListener() {

      /**
       * 
       */
      private static final long serialVersionUID = -43367719647620455L;

      @Override
      public void itemClick(ItemClickEvent event) {

        BeanItem selected = (BeanItem) projectBean.getExperiments().getItem(event.getItemId());
        ExperimentBean selectedExp = (ExperimentBean) selected.getBean();

        State state = (State) UI.getCurrent().getSession().getAttribute("state");
        ArrayList<String> message = new ArrayList<String>();
        message.add("clicked");
        message.add(selectedExp.getId());
        message.add("experiment");
        state.notifyObservers(message);
      }
    });

    gpc.addGeneratedProperty("edit", new PropertyValueGenerator<String>() {

      /**
       * 
       */
      private static final long serialVersionUID = 7558511163500976236L;

      @Override
      public String getValue(Item item, Object itemId, Object propertyId) {
        return "Edit";
      }

      @Override
      public Class<String> getType() {
        return String.class;
      }
    });

    gpc.addGeneratedProperty("registrationDate", new PropertyValueGenerator<String>() {

      @Override
      public Class<String> getType() {
        return String.class;
      }

      @Override
      public String getValue(Item item, Object itemId, Object propertyId) {
        BeanItem selected = (BeanItem) projectBean.getExperiments().getItem(itemId);
        ExperimentBean expBean = (ExperimentBean) selected.getBean();
        Date date = expBean.getRegistrationDate();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        String dateString = sd.format(date);

        return dateString;
      }
    });

    experiments.setContainerDataSource(gpc);
    experiments.getColumn("prettyType").setHeaderCaption("Type");
    experiments.getColumn("edit").setRenderer(new ButtonRenderer(new RendererClickListener() {

      @Override
      public void click(RendererClickEvent event) {
        BeanItem selected = (BeanItem) projectBean.getExperiments().getItem(event.getItemId());
        ExperimentBean selectedSample = (ExperimentBean) selected.getBean();

        Window subWindow = new Window("Edit Metadata");

        changeMetadata.updateUI(selectedSample.getId(), selectedSample.getType());
        VerticalLayout subContent = new VerticalLayout();
        subContent.setMargin(true);
        subContent.addComponent(changeMetadata);
        subWindow.setContent(subContent);
        // Center it in the browser window
        subWindow.center();
        subWindow.setModal(true);
        subWindow.setSizeUndefined();
        subWindow.setIcon(FontAwesome.PENCIL);
        subWindow.setHeight("75%");
        subWindow.setResizable(false);

        ProjectBrowserPortlet ui = (ProjectBrowserPortlet) UI.getCurrent();
        ui.addWindow(subWindow);
      }

    }));

    experiments.getColumn("edit").setWidth(70);
    experiments.setColumnOrder("edit", "prettyType");
    experiments.getColumn("edit").setHeaderCaption("");

    // experiments.setHeightMode(HeightMode.ROW);
    // experiments.setHeightByRows(gpc.size());

    if (fileDownloader != null)
      this.export.removeExtension(fileDownloader);
    StreamResource sr =
        Utils.getTSVStream(Utils.containerToString(projectBean.getExperiments()), String.format(
            "%s_%s_", projectBean.getId().substring(1).replace("/", "_"), "experimental_steps"));
    fileDownloader = new FileDownloader(sr);
    fileDownloader.extend(export);
  }
}
