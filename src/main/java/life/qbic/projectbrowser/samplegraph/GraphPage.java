package life.qbic.projectbrowser.samplegraph;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import ch.systemsx.cisd.openbis.dss.client.api.v1.DataSet;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Sample;
import life.qbic.portal.portlet.ProjectBrowserPortlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GraphPage extends VerticalLayout {

  // private OpenBisClient openbis;
  private static final Logger LOG = LogManager.getLogger(GraphPage.class);

  private List<Sample> currentSamples;
  private ProjectParser parser;
  private StructuredExperiment structure;
  private ComboBox factorBox;

  private ProjectGraph sampleGraph;

  public GraphPage(Map<String, String> taxMap, Map<String, String> tissueMap) {
    Map<String, String> reverseTaxMap = taxMap.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    Map<String, String> reverseTissueMap = tissueMap.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    parser = new ProjectParser(reverseTaxMap, reverseTissueMap);

    this.factorBox = new ComboBox("Experimental Factor");
    factorBox.setVisible(false);

    addComponent(factorBox);
  }

  private String buildImagePath() {
    URI location = UI.getCurrent().getPage().getLocation();
    StringBuilder pathBuilder = new StringBuilder();
    // http
    pathBuilder.append(location.getScheme() + "://");
    // host+port
    pathBuilder.append(location.getAuthority());

    // can we get this dynamically?
    final String portletName = "qnavigator";

    // String port = (Integer.toString(location.getPort()));
    // if (location.toString().contains(port))
    // pathBuilder.append(":" + port);
    pathBuilder.append("/" + portletName);
    pathBuilder.append("/VAADIN/themes/" + UI.getCurrent().getTheme() + "/img/");
    return pathBuilder.toString();
  }

  public void loadProjectGraph(String projectIdentifier, List<Sample> samples,
      List<DataSet> datasets) {
    factorBox.removeAllItems();
    factorBox.setVisible(false);

    currentSamples = samples;
    // openbis
    // .getSamplesWithParentsAndChildrenOfProjectBySearchService(project.toString());
    // List<DataSet> datasets = openbis.getDataSetsOfProjectByIdentifier(
    // "/" + spaceBox.getValue().toString() + "/" + project.toString());
    if (currentSamples.isEmpty()) {
      LOG.info("No samples to show found in this project.");
    } else {
      // structure = parser.parseAll(currentSamples, datasets);
      try {
        structure = parser.parseSamplesBreadthFirst(currentSamples, datasets);
        if (!structure.getFactorsToSamples().isEmpty()) {
          factorBox.addItems(structure.getFactorsToSamples().keySet());
          factorBox.setVisible(true);
        }
      } catch (JAXBException e) {
        e.printStackTrace();
        LOG.error("JAXB Parsing error!");
      }
    }

    factorBox.setImmediate(true);
    factorBox.setNullSelectionAllowed(false);
    final GraphPage parent = this;
    factorBox.addValueChangeListener(new ValueChangeListener() {

      @Override
      public void valueChange(ValueChangeEvent event) {
        Object factor = factorBox.getValue();
        if (sampleGraph != null)
          parent.removeComponent(sampleGraph);
        sampleGraph = new ProjectGraph(parent, buildImagePath());
        sampleGraph.setSizeFull();
        parent.addComponent(sampleGraph);
        if (factor != null) {
          sampleGraph.setProject(structure.getFactorsToSamples().get(factor));
        }
      }
    });
  }

  public void showDatasetsForSamples(String label, List<String> sampleCodes) {
    Window subWindow = new Window(" " + label + " Dataset information");
    subWindow.setWidth("680px");

    VerticalLayout layout = new VerticalLayout();
    layout.setSpacing(true);
    layout.setMargin(true);
    List<Sample> samplesWithData = new ArrayList<Sample>();
    List<Sample> samplesWithoutData = new ArrayList<Sample>();
    for (String code : sampleCodes) {
      Sample s = parser.getSampleFromCode(code);
      if (parser.codeHasDatasets(code))
        samplesWithData.add(s);
      else
        samplesWithoutData.add(s);
    }

    Table haveData = new Table("Samples with Data");
    haveData.setStyleName(ValoTheme.TABLE_SMALL);
    haveData.addContainerProperty("Sample", String.class, null);
    haveData.addContainerProperty("Secondary Name", String.class, null);
    haveData.addContainerProperty("Lab ID", String.class, null);
    int i = 0;
    for (Sample s : samplesWithData) {
      i++;
      List<Object> row = new ArrayList<Object>();
      row.add(s.getCode());
      Map<String, String> props = s.getProperties();
      String secName = "";
      if (props.get("Q_SECONDARY_NAME") != null)
        secName = props.get("Q_SECONDARY_NAME");
      row.add(secName);

      String extID = "";
      if (props.get("Q_EXTERNALDB_ID") != null)
        extID = props.get("Q_EXTERNALDB_ID");
      row.add(extID);

      haveData.addItem(row.toArray(new Object[row.size()]), s);
    }

    Table noData = new Table("Samples without Data");
    noData.setStyleName(ValoTheme.TABLE_SMALL);
    noData.addContainerProperty("Sample", String.class, null);
    noData.addContainerProperty("Secondary Name", String.class, null);
    noData.addContainerProperty("Lab ID", String.class, null);
    for (Sample s : samplesWithoutData) {
      i++;
      List<Object> row = new ArrayList<Object>();
      row.add(s.getCode());
      Map<String, String> props = s.getProperties();
      String secName = "";
      if (props.get("Q_SECONDARY_NAME") != null)
        secName = props.get("Q_SECONDARY_NAME");
      row.add(secName);

      String extID = "";
      if (props.get("Q_EXTERNALDB_ID") != null)
        extID = props.get("Q_EXTERNALDB_ID");
      row.add(extID);

      noData.addItem(row.toArray(new Object[row.size()]), s);
    }
    haveData.setPageLength(samplesWithData.size());
    noData.setPageLength(samplesWithoutData.size());

    layout.addComponent(haveData);
    layout.addComponent(noData);
    Button ok = new Button("Close");
    ok.addClickListener(new ClickListener() {

      @Override
      public void buttonClick(ClickEvent event) {
        subWindow.close();
      }
    });

    layout.addComponent(ok);

    subWindow.setContent(layout);
    // Center it in the browser window
    subWindow.center();
    subWindow.setModal(true);
    subWindow.setIcon(FontAwesome.FILE);
    subWindow.setResizable(false);
    ProjectBrowserPortlet ui = (ProjectBrowserPortlet) UI.getCurrent();
    ui.addWindow(subWindow);
  }

  //
  // public void showSamples(List<String> nodeEntries) {
  // sampleTable.setPageLength(Math.min(6, nodeEntries.size()));
  // sampleTable.removeAllItems();
  // sampleTable.addContainerProperty("Code", String.class, null);
  // sampleTable.addContainerProperty("Secondary Name", String.class, null);
  // int i = 0;
  // for (Sample s : currentSamples) {
  // i++;
  // String code = s.getCode();
  // String name = s.getProperties().get("Q_SECONDARY_NAME");
  // if (nodeEntries.contains(code)) {
  // List<Object> row = new ArrayList<Object>();
  // row.add(code);
  // row.add(name);
  // sampleTable.addItem(row.toArray(new Object[row.size()]), i);
  // }
  // }
  // sampleTable.setVisible(true);
  // }

}
