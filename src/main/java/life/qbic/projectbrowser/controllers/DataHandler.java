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
package life.qbic.projectbrowser.controllers;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import life.qbic.portal.utils.PortalUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import ch.systemsx.cisd.openbis.dss.client.api.v1.DataSet;
import ch.systemsx.cisd.openbis.dss.generic.shared.api.v1.FileInfoDssDTO;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.ControlledVocabularyPropertyType;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Experiment;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Material;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.MaterialIdentifier;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.MaterialTypeIdentifier;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Project;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.PropertyType;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Sample;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.SpaceWithProjectsAndRoleAssignments;
import ch.systemsx.cisd.openbis.plugin.query.shared.api.v1.dto.QueryTableModel;
import life.qbic.datamodel.experiments.ExperimentType;
import life.qbic.datamodel.identifiers.ExperimentCodeFunctions;
import life.qbic.datamodel.samples.ISampleBean;
import life.qbic.datamodel.samples.SampleType;
import life.qbic.openbis.openbisclient.OpenBisClient;
import life.qbic.projectbrowser.helpers.Utils;
import life.qbic.projectbrowser.helpers.OpenBisFunctions;
import life.qbic.projectbrowser.helpers.BarcodeFunctions;
import life.qbic.projectbrowser.model.DatasetBean;
import life.qbic.projectbrowser.model.ExperimentBean;
import life.qbic.projectbrowser.model.ExperimentStatusBean;
import life.qbic.projectbrowser.model.NewIvacSampleBean;
import life.qbic.projectbrowser.model.SampleBean;
import life.qbic.projectbrowser.model.spaceToProjectPrefixMap;
import life.qbic.projectbrowser.model.ProjectBean;
import life.qbic.projectbrowser.model.DBManager;
import life.qbic.projectbrowser.helpers.AlternativeSecondaryNameCreator;
import life.qbic.xml.persons.Qperson;
import life.qbic.xml.properties.Property;
import life.qbic.xml.study.Qexperiment;
import life.qbic.xml.study.Qproperty;
import life.qbic.xml.study.TechnologyType;
import life.qbic.xml.manager.PersonParser;
import life.qbic.xml.manager.StudyXMLParser;


public class DataHandler implements Serializable {


  /**
   * 
   */
  private static final long serialVersionUID = -4814000017404997233L;
  private static final Logger LOG = LogManager.getLogger(DataHandler.class);

  Map<String, SampleBean> sampleMap = new HashMap<String, SampleBean>();
  Map<String, DatasetBean> datasetMap = new HashMap<String, DatasetBean>();

  // store search result containers here
  List<Sample> sampleResults = new ArrayList<Sample>();
  List<Experiment> expResults = new ArrayList<Experiment>();
  List<Project> projResults = new ArrayList<Project>();

  List<String> showOptions = Arrays.asList("Projects", "Experiments", "Samples");


  String lastQueryString = new String();


  List<SpaceWithProjectsAndRoleAssignments> space_list = null;
  IndexedContainer connectedPersons = new IndexedContainer();


  public List<Sample> getSampleResults() {
    return sampleResults;
  }

  public void setSampleResults(List<Sample> sampleResults) {
    Set<String> projects = new HashSet<String>();

    // we have to initialize the projects in order to get the experimental design for the navigation
    // from the searchbar view
    for (Sample s : sampleResults) {
      String expID = s.getExperimentIdentifierOrNull();
      String spaceCode = s.getSpaceCode();
      String projectId = String.format("/%s/%s", spaceCode, expID.split("/")[2]);

      if (!projects.contains(projectId)) {
        projects.add(projectId);
      }
    }

    for (String p : projects) {
      this.getProject2(p);
    }

    this.sampleResults = sampleResults;
  }

  public List<Experiment> getExpResults() {
    return expResults;
  }

  public void setExpResults(List<Experiment> expResults) {
    this.expResults = expResults;
  }

  public List<Project> getProjResults() {
    return projResults;
  }

  public void setProjResults(List<Project> projResults) {
    this.projResults = projResults;
  }

  public List<String> getShowOptions() {
    return showOptions;
  }


  public void setShowOptions(List<String> showOptions) {
    this.showOptions = showOptions;
  }

  public String getLastQueryString() {
    return lastQueryString;
  }


  public void setLastQueryString(String lastQueryString) {
    this.lastQueryString = lastQueryString;
  }

  public List<SpaceWithProjectsAndRoleAssignments> getSpacesWithProjectInformation() {
    if (space_list == null) {
      space_list = this.getOpenBisClient().getFacade().getSpacesWithProjects();
    }

    return space_list;
  }

  private OpenBisClient openBisClient;
  private DBManager databaseManager;

  private Map<String, Project> dtoProjects = new HashMap<String, Project>();
  // private Map<String, Experiment> dtoExperiments = new HashMap<String, Experiment>();
  private StudyXMLParser studyParser = new StudyXMLParser();
  private Set<String> experimentalFactorLabels;
  private Map<Pair<String, String>, Property> experimentalFactorsForLabelsAndSamples;
  private Map<String, List<Property>> propertiesForSamples;
  private JAXBElement<Qexperiment> experimentalSetup;

  public DataHandler(OpenBisClient client, DBManager databaseManager) {
    // reset(); //TODO useless?
    this.setOpenBisClient(client);
    this.setDatabaseManager(databaseManager);
  }

  private Date parseDate(String dateString) {
    Date date = null;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try {
      date = formatter.parse(dateString.split("\\+")[0]);

    } catch (ParseException e) {
      e.printStackTrace();
    }
    return date;
  }

  /**
   * 
   * @param datasets List of dataset codes
   * @return A list of DatasetBeans denoting the roots of the folder structure of each dataset.
   *         Subfolders and files can be reached by calling the getChildren() function on each Bean.
   */
  public List<DatasetBean> queryDatasetsForFolderStructure(
      List<ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.DataSet> datasets) {
    Map<String, List<String>> params = new HashMap<String, List<String>>();
    List<String> dsCodes = new ArrayList<String>();
    Map<String, String> types = new HashMap<String, String>();

    Map<String, Map<String, String>> props = new LinkedHashMap<String, Map<String, String>>();

    for (ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.DataSet ds : datasets) {
      dsCodes.add(ds.getCode());
      types.put(ds.getCode(), ds.getDataSetTypeCode());
      props.put(ds.getCode(), ds.getProperties());
    }

    params.put("codes", dsCodes);
    QueryTableModel res = getOpenBisClient().queryFileInformation(params);

    // TODO this should work, but here starts the new code in case it doesn't 07.08.15 - Andreas
    Map<String, List<DatasetBean>> folderStructure = new HashMap<String, List<DatasetBean>>();
    Map<String, DatasetBean> fileNames = new HashMap<String, DatasetBean>();
    // Set<DatasetBean> folders = new HashSet<DatasetBean>();

    for (Serializable[] ss : res.getRows()) {

      DatasetBean b = new DatasetBean();
      String dsCode = (String) ss[0];
      String fileName = (String) ss[2];
      String dssPath = (String) ss[1];
      b.setCode(dsCode);
      b.setType(types.get(dsCode));
      b.setFileName(fileName);
      b.setDssPath(dssPath);
      long size = (Long) ss[3];
      b.setFileSize(size);
      b.setRegistrationDate(parseDate((String) ss[5]));
      b.setProperties(props.get(dsCode));

      // both code and filename are needed for the keys to be unique
      // fileNames.put(dsCode + fileName, b);
      fileNames.put(dsCode + dssPath, b);

      // store file beans under their respective code+folder, except those with "original"
      // String folderKey = (String) ss[4];
      // safest way to be unique: folder is dss path without the last part of the path ("original"
      // isn't changed)
      String folderKey = dssPath;
      if (null != dssPath && dssPath.length() > 0) {
        int endIndex = dssPath.lastIndexOf("/");
        if (endIndex != -1) {
          folderKey = dssPath.substring(0, endIndex); // not forgot to put check if(endIndex !=
                                                      // -1)
        }
      }
      // LOG.debug("full path " + b.getDssPath());
      if (!folderKey.equals("original"))
        folderKey = dsCode + folderKey;
      // LOG.debug("folder key: " + folderKey);
      // folder known, add this file to folder
      if (folderStructure.containsKey(folderKey)) {
        folderStructure.get(folderKey).add(b);
      } else {
        // folder unknown, create new folder with dataset list containing this file
        List<DatasetBean> inFolder = new ArrayList<DatasetBean>();
        inFolder.add(b);
        folderStructure.put(folderKey, inFolder);
      }
    }
    // System.out.println("known folders with data: " + folderStructure.size());
    // System.out.println("known fileNames: " + fileNames.size());
    // for (String folder : folderStructure.keySet()) {
    // System.out.println(folder + " contains " + folderStructure.get(folder).size() + " files");
    // }
    // find children samples for our folders
    for (String fileNameKey : fileNames.keySet()) {
      // if the fileNameKey is in our folder map we have found a folder (not a file and not the
      // "original" folder)
      if (folderStructure.containsKey(fileNameKey)) {
        // and we add the files to this folder bean
        // System.out.println("filekey: " + fileNameKey);
        List<DatasetBean> children = folderStructure.get(fileNameKey);
        // if (children == null)
        // System.out.println("no subfiles for this key");
        // else
        // System.out.println(children.size() + " subfiles");
        fileNames.get(fileNameKey).setChildren(children);
        // System.out.println(fileNames.get(fileNameKey).getChildren());
      }
    }
    // System.out.println("first ds in original:");
    // DatasetBean ds = folderStructure.get("original").get(0);
    // System.out.println(ds);
    // System.out.println("subfolders:");
    // System.out.println(ds.getChildren());
    // Now the structure should be set up. Root structures have "original" as parent folder
    List<DatasetBean> roots = folderStructure.get("original");
    // Remove empty folders
    List<DatasetBean> level = roots;
    while (!level.isEmpty()) {
      List<DatasetBean> collect = new ArrayList<DatasetBean>();
      List<DatasetBean> toRemove = new ArrayList<DatasetBean>();
      for (DatasetBean b : level) {
        if (b.hasChildren()) {
          // collect subfolders + files for recursion
          collect.addAll(b.getChildren());
        } else {
          // no subfolders or files and empty? remove from this folder level
          if (b.getFileSize() == 0) {
            toRemove.add(b);
          }
        }
      }
      level.removeAll(toRemove);
      level = collect;
    }
    LOG.debug(fileNames.size() + " files found");
    LOG.debug(folderStructure.size() + " folders found");
    LOG.debug(roots.size() + " root folders");
    int annoyanceCount = 5;
    LOG.debug("subfiles for the first 5 root folders: ");
    for (DatasetBean b : roots) {
      annoyanceCount--;
      if (annoyanceCount > 0) {
        if (b.hasChildren())
          LOG.debug("root has attached subfiles: " + b.getChildren().size());
      }
    }
    return roots;
  }

  // Recursively get all samples which are above the corresponding sample in the tree
  public List<DatasetBean> getAllFiles(List<DatasetBean> found, DatasetBean root) {
    List<DatasetBean> current = root.getChildren();

    if (current == null) {
      found.add(root);
    } else if (current.size() == 0) {
      found.add(root);

    } else {
      for (int i = 0; i < current.size(); i++) {
        getAllFiles(found, current.get(i));
      }
    }
    return found;
  }


  public List<DatasetBean> queryDatasetsForFiles(
      List<ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.DataSet> datasets) {
    List<DatasetBean> results = new ArrayList<DatasetBean>();

    if (datasets.size() > 0) {
      List<DatasetBean> roots = queryDatasetsForFolderStructure(datasets);

      for (DatasetBean ds : roots) {
        List<DatasetBean> startList = new ArrayList<DatasetBean>();
        results.addAll(getAllFiles(startList, ds));
      }
    }

    return results;
  }


  /**
   * Method to get Bean from either openbis identifier or openbis object. Does NOT check if
   * corresponding bean is already stored in datahandler map. Should be used if project instance has
   * been modified from session
   * 
   * @param
   * @return
   */
  public ProjectBean getProjectFromDB(String projectIdentifier) {
    List<Experiment> experiments =
        this.getOpenBisClient().getExperimentsForProject2(projectIdentifier);

    float projectStatus = this.getOpenBisClient().computeProjectStatus(experiments);

    Project project = getOpenBisClient().getProjectByIdentifier(projectIdentifier);
    dtoProjects.put(projectIdentifier, project);

    ProjectBean newProjectBean = new ProjectBean();

    ProgressBar progressBar = new ProgressBar();
    progressBar.setValue(projectStatus);

    Date registrationDate = project.getRegistrationDetails().getRegistrationDate();

    // String pi = getDatabaseManager().getInvestigatorDetailsForProject(project.getCode());
    String pi = getDatabaseManager().getPersonDetailsForProject(project.getIdentifier(), "PI");
    String cp = getDatabaseManager().getPersonDetailsForProject(project.getIdentifier(), "Contact");
    // String manager = getDatabaseManager().getPersonDetailsForProject(project.getIdentifier(),
    // "Manager");//TODO
    String manager = "";
    String longDesc = getDatabaseManager().getLongProjectDescription(project.getIdentifier());

    if (pi.equals("")) {
      newProjectBean.setPrincipalInvestigator("n/a");
    } else {
      newProjectBean.setPrincipalInvestigator(pi);
    }

    if (cp.equals("")) {
      newProjectBean.setContactPerson("n/a");
    } else {
      newProjectBean.setContactPerson(cp);
    }

    if (manager.equals("")) {
      newProjectBean.setProjectManager("n/a");
    } else {
      newProjectBean.setProjectManager(manager);
    }

    String secondaryName = getDatabaseManager().getProjectName(projectIdentifier);
    if (secondaryName == null || secondaryName.isEmpty())
      secondaryName = "n/a";
    newProjectBean.setSecondaryName(secondaryName);

    if (longDesc == null)
      longDesc = "";

    newProjectBean.setId(project.getIdentifier());
    newProjectBean.setCode(project.getCode());
    String desc = project.getDescription();
    if (desc == null)
      desc = "";
    newProjectBean.setDescription(desc);
    newProjectBean.setRegistrationDate(registrationDate);
    newProjectBean.setProgress(progressBar);
    newProjectBean.setRegistrator(project.getRegistrationDetails().getUserId());
    newProjectBean.setContact(project.getRegistrationDetails().getUserEmail());

    BeanItemContainer<ExperimentBean> experimentBeans =
        new BeanItemContainer<ExperimentBean>(ExperimentBean.class);

    for (Experiment experiment : experiments) {
      ExperimentBean newExperimentBean = new ExperimentBean();
      String status = "";

      Map<String, String> assignedProperties = experiment.getProperties();

      if (assignedProperties.keySet().contains("Q_CURRENT_STATUS")) {
        status = assignedProperties.get("Q_CURRENT_STATUS");
      }

      else if (assignedProperties.keySet().contains("Q_WF_STATUS")) {
        status = assignedProperties.get("Q_WF_STATUS");
      }

      // Image statusColor = new Image(status, this.setExperimentStatusColor(status));
      // statusColor.setWidth("15px");
      // statusColor.setHeight("15px");
      // statusColor.setCaption(status);

      newExperimentBean.setId(experiment.getIdentifier());
      newExperimentBean.setCode(experiment.getCode());
      newExperimentBean.setType(experiment.getExperimentTypeCode());
      newExperimentBean.setStatus(status);
      newExperimentBean.setRegistrator(experiment.getRegistrationDetails().getUserId());
      newExperimentBean
          .setRegistrationDate(experiment.getRegistrationDetails().getRegistrationDate());
      experimentBeans.addBean(newExperimentBean);
    }

    newProjectBean.setLongDescription(longDesc);

    List<ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.DataSet> projectData = this
        .getOpenBisClient().getDataSetsOfProjectByIdentifierWithSearchCriteria(projectIdentifier);

    Boolean containsData = false;
    Boolean containsResults = false;
    Boolean attachmentResult = false;
    // Boolean containsAttachments = false;

    for (ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.DataSet ds : projectData) {
      attachmentResult = false;
      if (ds.getDataSetTypeCode().equals("Q_PROJECT_DATA")) {
        attachmentResult = ds.getProperties().get("Q_ATTACHMENT_TYPE").equals("RESULT");
      }

      if (!(ds.getDataSetTypeCode().equals("Q_PROJECT_DATA"))
          && !(ds.getDataSetTypeCode().contains("RESULTS"))) {
        containsData = true;
      } else if (ds.getDataSetTypeCode().contains("RESULTS") || attachmentResult) {
        containsResults = true;
      } // else if (ds.getDataSetTypeCode() == "Q_PROJECT_DATA") {
        // containsAttachments = true;
      // }
    }

    newProjectBean.setContainsData(containsData);
    newProjectBean.setContainsResults(containsResults);

    newProjectBean.setExperiments(experimentBeans);
    newProjectBean.setMembers(new HashSet<String>());
    return newProjectBean;
  }

  /**
   * Method to get Bean from either openbis identifier or openbis object. Checks if corresponding
   * bean is already stored in datahandler map.
   * 
   * @param
   * @return
   */
  public ProjectBean getProject2(String projectIdentifier) {
    List<Experiment> experiments =
        this.getOpenBisClient().getExperimentsForProject3(projectIdentifier);// TODO changed this
                                                                             // from
                                                                             // getExperimentsForProject2

    float projectStatus = this.getOpenBisClient().computeProjectStatus(experiments);

    Project project = getOpenbisDtoProject(projectIdentifier);
    if (project == null) {
      project = getOpenBisClient().getProjectByIdentifier(projectIdentifier);
      addOpenbisDtoProject(project);
    }
    ProjectBean newProjectBean = new ProjectBean();

    ProgressBar progressBar = new ProgressBar();
    progressBar.setValue(projectStatus);

    Date registrationDate = project.getRegistrationDetails().getRegistrationDate();

    // String pi = getDatabaseManager().getInvestigatorDetailsForProject(project.getCode());
    String pi = getDatabaseManager().getPersonDetailsForProject(project.getIdentifier(), "PI");
    String cp = getDatabaseManager().getPersonDetailsForProject(project.getIdentifier(), "Contact");
    String manager =
        getDatabaseManager().getPersonDetailsForProject(project.getIdentifier(), "Manager");

    String longDesc = getDatabaseManager().getLongProjectDescription(project.getIdentifier());

    if (pi.equals("")) {
      newProjectBean.setPrincipalInvestigator("n/a");
    } else {
      newProjectBean.setPrincipalInvestigator(pi);
    }

    if (cp.equals("")) {
      newProjectBean.setContactPerson("n/a");
    } else {
      newProjectBean.setContactPerson(cp);
    }

    if (manager.equals("")) {
      newProjectBean.setProjectManager("n/a");
    } else {
      newProjectBean.setProjectManager(manager);
    }

    if (longDesc == null)
      longDesc = "";
    newProjectBean.setLongDescription(longDesc);

    newProjectBean.setId(project.getIdentifier());
    newProjectBean.setCode(project.getCode());
    String desc = project.getDescription();
    if (desc == null)
      desc = "";
    newProjectBean.setDescription(desc);
    newProjectBean.setRegistrationDate(registrationDate);
    newProjectBean.setProgress(progressBar);
    newProjectBean.setRegistrator(project.getRegistrationDetails().getUserId());
    newProjectBean.setContact(project.getRegistrationDetails().getUserEmail());

    // Create sample Beans (or fetch them) for samples of experiments
    List<Sample> allSamples = this.getOpenBisClient()
        .getSamplesWithParentsAndChildrenOfProjectBySearchService(projectIdentifier);

    BeanItemContainer<ExperimentBean> experimentBeans =
        new BeanItemContainer<ExperimentBean>(ExperimentBean.class);

    AlternativeSecondaryNameCreator altNameCreator = new AlternativeSecondaryNameCreator(
        openBisClient.getVocabCodesAndLabelsForVocab("Q_NCBI_TAXONOMY"));

    // this is the experiment that stores the experimental design xml
    Experiment designExperiment = null;
    Set<String> allSampleCodes = new HashSet<>();
    for (Sample s : allSamples) {
      allSampleCodes.add(s.getCode());
    }

    // create basic experimental design, if it doesn't exist
    String space = project.getSpaceCode();
    String projectCode = project.getCode();
    String designExpID = ExperimentCodeFunctions.getInfoExperimentID(space, projectCode);

    for (Experiment experiment : experiments) {
      String id = experiment.getIdentifier();
      if (id.equals(designExpID)) {
        designExperiment = experiment;
        break;
      }
    }
    String user = PortalUtils.getNonNullScreenName();

    if (designExperiment == null) {
      LOG.info("design experiment null, creating new one.");
      Map<String, Object> params = new HashMap<String, Object>();
      Map<String, String> props = new HashMap<>();
      // TODO empty xml is not valid, but should we add one at all?
      // try {
      // String basicXML = studyParser.toString(studyParser.getEmptyXML());
      // props.put("Q_EXPERIMENTAL_SETUP", basicXML);
      // } catch (JAXBException e) {
      // // TODO Auto-generated catch block
      // e.printStackTrace();
      // }
      params.put("user", user);
      params.put("code", projectCode + "_INFO");
      params.put("type", ExperimentType.Q_PROJECT_DETAILS);
      params.put("project", projectCode);
      params.put("space", space);
      params.put("properties", props);
      openBisClient.triggerIngestionService("register-exp", params);
    }
    // parse experimental design for later use
    String xmlString = designExperiment.getProperties().get("Q_EXPERIMENTAL_SETUP");
    JAXBElement<Qexperiment> expDesign = null;
    try {
      expDesign = studyParser.parseXMLString(xmlString);
    } catch (JAXBException e) {
      LOG.error("could not parse experimental design xml!");
      e.printStackTrace();
    }
    if (expDesign != null) {
      // experimental design found and parsed. remove samples that have since been deleted:
      try {
        if (!allSampleCodes.isEmpty()) {
          LOG.info("comparing existing samples with references in experimental design");
          if (studyParser.hasReferencesToMissingIDs(expDesign, allSampleCodes)) {
            LOG.info("deleted samples found. updating xml in openBIS");
            expDesign = studyParser.removeReferencesToMissingIDs(expDesign, allSampleCodes, true);
          }
        }
        HashMap<String, Object> params = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        properties.put("Q_EXPERIMENTAL_SETUP", studyParser.toString(expDesign));
        params.put("user", user);
        params.put("identifier", designExpID);
        params.put("properties", properties);
        openBisClient.triggerIngestionService("update-experiment-metadata", params);
      } catch (JAXBException e) {
        LOG.warn(
            "could not create new experimental design xml from old one after removing missing ids. "
                + "will continue with old design.");
        e.printStackTrace();
      }

      this.experimentalSetup = expDesign;
      this.experimentalFactorLabels = studyParser.getFactorLabels(expDesign);
      this.experimentalFactorsForLabelsAndSamples =
          studyParser.getFactorsForLabelsAndSamples(expDesign);
      this.propertiesForSamples = studyParser.getPropertiesForSampleCode(expDesign);
    }

    for (Experiment experiment : experiments) {
      ExperimentBean newExperimentBean = new ExperimentBean();

      // TODO doesn't work with getExperimentsForProject2
      Map<String, String> assignedProperties = experiment.getProperties();

      String status = "";

      if (assignedProperties.keySet().contains("Q_CURRENT_STATUS")) {
        status = assignedProperties.get("Q_CURRENT_STATUS");
      }

      else if (assignedProperties.keySet().contains("Q_WF_STATUS")) {
        status = assignedProperties.get("Q_WF_STATUS");
      }

      List<Sample> samples = new ArrayList<Sample>();
      for (Sample s : allSamples) {
        if (s.getExperimentIdentifierOrNull().equals(experiment.getIdentifier()))
          samples.add(s);
      }
      BeanItemContainer<SampleBean> sampleBeans =
          new BeanItemContainer<SampleBean>(SampleBean.class);
      for (Sample sample : samples) {
        SampleBean sbean = new SampleBean();
        sbean.setId(sample.getIdentifier());
        sbean.setCode(sample.getCode());
        sbean.setType(sample.getSampleTypeCode());
        sbean.setProperties(sample.getProperties());
        List<Property> complexProps =
            studyParser.getFactorsAndPropertiesForSampleCode(experimentalSetup, sample.getCode());
        sbean.setComplexProperties(complexProps);
        sampleBeans.addBean(sbean);
      }
      newExperimentBean.setSamples(sampleBeans);

      newExperimentBean.setAltNameCreator(altNameCreator);
      newExperimentBean.setProperties(assignedProperties);
      newExperimentBean.setSecondaryName(assignedProperties.get("Q_SECONDARY_NAME"));
      newExperimentBean.setId(experiment.getIdentifier());
      newExperimentBean.setCode(experiment.getCode());
      newExperimentBean.setType(experiment.getExperimentTypeCode());
      newExperimentBean.setRegistrator(experiment.getRegistrationDetails().getUserId());
      newExperimentBean
          .setRegistrationDate(experiment.getRegistrationDetails().getRegistrationDate());
      newExperimentBean.setStatus(status);
      experimentBeans.addBean(newExperimentBean);
    }
    List<ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.DataSet> projectData = this
        .getOpenBisClient().getDataSetsOfProjectByIdentifierWithSearchCriteria(projectIdentifier);

    Boolean containsData = false;
    Boolean containsResults = false;
    Boolean attachmentResult = false;
    // Boolean containsAttachments = false;
    for (ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.DataSet ds : projectData) {
      attachmentResult = false;
      if (ds.getDataSetTypeCode().equals("Q_PROJECT_DATA")) {
        attachmentResult = ds.getProperties().get("Q_ATTACHMENT_TYPE").equals("RESULT");
      }

      if (!(ds.getDataSetTypeCode().equals("Q_PROJECT_DATA"))
          && !(ds.getDataSetTypeCode().contains("RESULTS"))) {
        containsData = true;
      } else if (ds.getDataSetTypeCode().contains("RESULTS") || attachmentResult) {
        containsResults = true;
      } // else if (ds.getDataSetTypeCode() == "Q_PROJECT_DATA") {
        // containsAttachments = true;
      // }
    }

    newProjectBean.setContainsData(containsData);
    newProjectBean.setContainsResults(containsResults);
    // newProjectBean.setContainsAttachments(containsAttachments);

    newProjectBean.setExperiments(experimentBeans);
    newProjectBean.setMembers(new HashSet<String>());

    String secondaryName = getDatabaseManager().getProjectName(projectIdentifier);
    if (secondaryName == null || secondaryName.isEmpty())
      secondaryName = "n/a";

    newProjectBean.setSecondaryName(secondaryName);
    return newProjectBean;
  }

  public Project getOpenbisDtoProject(String projectIdentifier) {
    if (this.dtoProjects.containsKey(projectIdentifier)) {
      return this.dtoProjects.get(projectIdentifier);
    }
    return null;

  }

  public void addOpenbisDtoProject(Project project) {
    if (project != null && !dtoProjects.containsKey(project.getIdentifier())) {
      this.dtoProjects.put(project.getIdentifier(), project);
    }
  }

  public ExperimentBean getExperiment2(String expIdentifiers) {
    ExperimentBean ebean = new ExperimentBean();

    AlternativeSecondaryNameCreator altNameCreator = new AlternativeSecondaryNameCreator(
        openBisClient.getVocabCodesAndLabelsForVocab("Q_NCBI_TAXONOMY"));
    ebean.setAltNameCreator(altNameCreator);

    String status = "";
    List<Experiment> experiments = getOpenBisClient().getExperimentById2(expIdentifiers);
    Experiment experiment = null;
    for (Experiment tmpexp : experiments) {
      if (tmpexp.getIdentifier().equals(expIdentifiers)) {
        experiment = tmpexp;
        break;
      }
    }
    if (experiment == null)
      throw new IllegalArgumentException(
          String.format("experiment Identifier %s does not exist", expIdentifiers));
    // Get all properties for metadata changing
    List<PropertyType> completeProperties = this.getOpenBisClient().listPropertiesForType(
        this.getOpenBisClient().getExperimentTypeByString(experiment.getExperimentTypeCode()));

    Map<String, String> assignedProperties = experiment.getProperties();
    Map<String, List<String>> controlledVocabularies = new HashMap<String, List<String>>();
    Map<String, String> properties = new HashMap<String, String>();

    if (assignedProperties.keySet().contains("Q_CURRENT_STATUS")) {
      status = assignedProperties.get("Q_CURRENT_STATUS");
    }

    else if (assignedProperties.keySet().contains("Q_WF_STATUS")) {
      status = assignedProperties.get("Q_WF_STATUS");
    }

    boolean material = false;

    for (PropertyType p : completeProperties) {

      if (p instanceof ControlledVocabularyPropertyType) {
        controlledVocabularies.put(p.getCode(),
            getOpenBisClient().listVocabularyTermsForProperty(p));
      }

      if (p.getDataType().toString().equals("MATERIAL")
          && (assignedProperties.get(p.getCode()) != null)) {
        String[] splitted = assignedProperties.get(p.getCode()).split("\\(");

        String materialType = splitted[1].replace(")", "").replace(" ", "");
        String materialCode = splitted[0].replace(" ", "");

        MaterialIdentifier matId =
            new MaterialIdentifier(new MaterialTypeIdentifier(materialType), materialCode);

        List<MaterialIdentifier> matIds = new ArrayList<MaterialIdentifier>();
        matIds.add(matId);

        List<Material> materials = getOpenBisClient().getOpenbisInfoService()
            .getMaterialByCodes(getOpenBisClient().getSessionToken(), matIds);

        Map<String, String> matProperties = materials.get(0).getProperties();
        String matProperty = "";

        for (Entry prop : matProperties.entrySet()) {
          matProperty += String.format("%s, ", prop.getValue());
        }

        properties.put(p.getCode(), matProperty.substring(0, matProperty.length() - 2));
        material = true;
      }

      if (assignedProperties.keySet().contains(p.getCode()) && !(material)) {
        properties.put(p.getCode(), assignedProperties.get(p.getCode()));
      } else if (!(material)) {
        properties.put(p.getCode(), "");
      }
    }

    Map<String, String> typeLabels = this.getOpenBisClient().getLabelsofProperties(
        this.getOpenBisClient().getExperimentTypeByString(experiment.getExperimentTypeCode()));

    // Image statusColor = new Image(status, this.setExperimentStatusColor(status));
    // statusColor.setWidth("15px");
    // statusColor.setHeight("15px");

    ebean.setId(experiment.getIdentifier());
    ebean.setCode(experiment.getCode());
    ebean.setType(experiment.getExperimentTypeCode());
    ebean.setStatus(status);
    ebean.setRegistrator(experiment.getRegistrationDetails().getUserId());
    ebean.setRegistrationDate(experiment.getRegistrationDetails().getRegistrationDate());
    ebean.setProperties(properties);
    ebean.setSecondaryName(properties.get("Q_SECONDARY_NAME"));
    ebean.setControlledVocabularies(controlledVocabularies);
    ebean.setTypeLabels(typeLabels);

    // TODO do we want to have that ? (last Changed)
    ebean.setLastChangedSample(null);
    ebean.setContainsData(this.getOpenBisClient()
        .getDataSetsOfExperimentByCodeWithSearchCriteria(experiment.getCode()).size() > 0);

    // List<Sample> samples = this.getOpenBisClient().getSamplesofExperiment(expIdentifiers);
    // Create sample Beans (or fetch them) for samples of experiment
    List<Sample> allSamples = new ArrayList<Sample>();
    if (allSamples.isEmpty()) {
      String[] splt = experiment.getIdentifier().split("/");
      String projID = "/" + splt[1] + "/" + splt[2];
      allSamples =
          this.getOpenBisClient().getSamplesWithParentsAndChildrenOfProjectBySearchService(projID);
    }
    List<Sample> samples = new ArrayList<Sample>();
    for (Sample s : allSamples) {
      if (s.getExperimentIdentifierOrNull().equals(experiment.getIdentifier()))
        samples.add(s);
    }

    BeanItemContainer<SampleBean> sampleBeans = new BeanItemContainer<SampleBean>(SampleBean.class);
    for (Sample sample : samples) {
      SampleBean sbean = new SampleBean();
      sbean.setId(sample.getIdentifier());
      sbean.setCode(sample.getCode());
      sbean.setType(sample.getSampleTypeCode());
      sbean.setProperties(sample.getProperties());
      List<Property> complexProps =
          studyParser.getFactorsAndPropertiesForSampleCode(experimentalSetup, sample.getCode());
      sbean.setComplexProperties(complexProps);
      /*
       * Map<String, String> sampleTypeLabels =
       * this.openBisClient.getLabelsofProperties(this.openBisClient.getSampleTypeByString(sample
       * .getSampleTypeCode())); sbean.setTypeLabels(sampleTypeLabels);
       */

      sampleBeans.addBean(sbean);
    }
    ebean.setSamples(sampleBeans);

    return ebean;
  }

  public SampleBean getSample2(String sampleIdentifiers) {
    Sample sample = this.getOpenBisClient().getSampleByIdentifier(sampleIdentifiers);
    SampleBean sbean = createSampleBean(sample);
    return sbean;
  }

  /**
   * Method to get Bean from either openbis identifier or openbis object. Checks if corresponding
   * bean is already stored in datahandler map.
   * 
   * @param
   * @return
   */
  public SampleBean getSample(Object samp) {
    Sample sample;
    SampleBean newSampleBean;

    if (samp instanceof Sample) {
      sample = (Sample) samp;
      newSampleBean = this.createSampleBean(sample);
      this.sampleMap.put(newSampleBean.getId(), newSampleBean);
    }

    else {
      if (this.sampleMap.get((String) samp) != null) {
        newSampleBean = this.sampleMap.get(samp);
      } else {
        sample = this.getOpenBisClient().getSampleByIdentifier((String) samp);
        newSampleBean = this.createSampleBean(sample);
        this.sampleMap.put(newSampleBean.getId(), newSampleBean);
      }
    }

    return newSampleBean;
  }

  /**
   * Method to get Bean from either openbis identifier or openbis object. Checks if corresponding
   * bean is already stored in datahandler map.
   * 
   * @param
   * @return
   */
  public DatasetBean getDataset(Object ds) {
    DataSet dataset;
    DatasetBean newDatasetBean;

    if (ds instanceof DataSet) {
      dataset = (DataSet) ds;
      newDatasetBean = this.createDatasetBean(dataset);
    }

    else {
      if (this.datasetMap.get((String) ds) != null) {
        newDatasetBean = this.datasetMap.get(ds);
      } else {
        dataset = this.getOpenBisClient().getFacade().getDataSet((String) ds);
        newDatasetBean = this.createDatasetBean(dataset);
      }
    }
    this.datasetMap.put(newDatasetBean.getCode(), newDatasetBean);
    return newDatasetBean;
  }


  /**
   * Returns all users of a Space.
   * 
   * @param spaceCode code of the openBIS space
   * @return set of user names as string
   */
  private Set<String> getSpaceMembers(String spaceCode) {
    List<SpaceWithProjectsAndRoleAssignments> spaces = this.getSpacesWithProjectInformation();
    for (SpaceWithProjectsAndRoleAssignments space : spaces) {
      if (space.getCode().equals(spaceCode)) {
        return space.getUsers();
      }
    }
    return null;
  }

  /**
   * checks which of the datasets in the given list is the oldest and writes that into the last tree
   * parameters Note: lastModifiedDate, lastModifiedExperiment, lastModifiedSample will be modified.
   * if lastModifiedSample, lastModifiedExperiment have value N/A datasets have no registration
   * dates Params should not be null
   * 
   * @param datasets List of datasets that will be compared
   * @param lastModifiedDate will contain the last modified date
   * @param lastModifiedExperiment will contain experiment identifier, which contains last
   *        registered dataset
   * @param lastModifiedSample will contain last sample identifier, which contains last registered
   *        dataset, or null if dataset does not belong to a sample.
   */
  public void lastDatasetRegistered(List<DataSet> datasets, Date lastModifiedDate,
      StringBuilder lastModifiedExperiment, StringBuilder lastModifiedSample) {
    String exp = "N/A";
    String samp = "N/A";
    for (DataSet dataset : datasets) {
      Date date = dataset.getRegistrationDate();

      if (date.after(lastModifiedDate)) {
        samp = dataset.getSampleIdentifierOrNull();
        if (samp == null) {
          samp = "N/A";
        }
        exp = dataset.getExperimentIdentifier();
        lastModifiedDate.setTime(date.getTime());
        break;
      }
    }
    lastModifiedExperiment.append(exp);
    lastModifiedSample.append(samp);
  }

  // public void reset() {
  // // this.spaces = new HashMap<String,IndexedContainer>();
  // // this.projects = new HashMap<String,IndexedContainer>();
  // // this.experiments = new HashMap<String,IndexedContainer>();
  // // this.samples = new HashMap<String,IndexedContainer>();
  // this.space_to_datasets = new HashMap<String, HierarchicalContainer>();
  // }


  /**
   * This method filters out qbic staff and other unnecessary space members TODO: this method might
   * be better of as not being part of the DataHandler...and not hardcoded
   * 
   * @param users a set of all space users or members
   * @return a new set which exculdes qbic staff and functional members
   */
  public Set<String> removeQBiCStaffFromMemberSet(Set<String> users) {
    // TODO there is probably a method to get users of the QBIC group out of openBIS
    Set<String> ret = new LinkedHashSet<String>(users);
    // ret.remove("iiswo01"); // QBiC Staff
    // ret.remove("iisfr01"); // QBiC Staff
    // ret.remove("kxmsn01"); // QBiC Staff
    // ret.remove("zxmbf02"); // QBiC Staff
    // ret.remove("qeana10"); // functional user
    ret.remove("etlserver"); // OpenBIS user
    ret.remove("admin"); // OpenBIS user
    ret.remove("QBIC"); // OpenBIS user
    ret.remove("sauron");
    ret.remove("regtestuser");
    ret.remove("student");
    // ret.remove("babysauron");
    return ret;
  }

  /**
   * Method to create SampleBean for sample object
   * 
   * @param sample
   * @return SampleBean for corresponding object
   */
  SampleBean createSampleBean(Sample sample) {

    SampleBean newSampleBean = new SampleBean();

    Map<String, String> properties = sample.getProperties();

    newSampleBean.setId(sample.getIdentifier());
    newSampleBean.setCode(sample.getCode());
    newSampleBean.setType(sample.getSampleTypeCode());
    newSampleBean.setProperties(properties);
    List<Property> complexProps =
        studyParser.getFactorsAndPropertiesForSampleCode(experimentalSetup, sample.getCode());
    newSampleBean.setComplexProperties(complexProps);
    newSampleBean.setParents(this.getOpenBisClient().getParentsBySearchService(sample.getCode()));
    newSampleBean
        .setChildren(this.getOpenBisClient().getFacade().listSamplesOfSample(sample.getPermId()));

    BeanItemContainer<DatasetBean> datasetBeans =
        new BeanItemContainer<DatasetBean>(DatasetBean.class);
    List<DataSet> datasets =
        this.getOpenBisClient().getDataSetsOfSampleByIdentifier(sample.getIdentifier());

    Date lastModifiedDate = new Date();
    if (datasets.size() > 0)
      lastModifiedDate = datasets.get(0).getRegistrationDate();

    for (DataSet dataset : datasets) {
      DatasetBean datasetBean = this.getDataset(dataset);
      datasetBean.setSample(newSampleBean);
      datasetBeans.addBean(datasetBean);
      Date date = dataset.getRegistrationDate();
      if (date.after(lastModifiedDate)) {
        lastModifiedDate.setTime(date.getTime());
        break;
      }
    }

    newSampleBean.setDatasets(datasetBeans);
    newSampleBean.setLastChangedDataset(lastModifiedDate);

    Map<String, String> typeLabels = this.getOpenBisClient().getLabelsofProperties(
        this.getOpenBisClient().getSampleTypeByString(sample.getSampleTypeCode()));
    newSampleBean.setTypeLabels(typeLabels);

    return newSampleBean;
  }


  /**
   * Method to create DatasetBean for dataset object
   * 
   * @param dataset
   * @return DatasetBean for corresponding object
   */
  private DatasetBean createDatasetBean(DataSet dataset) {

    DatasetBean newDatasetBean = new DatasetBean();
    FileInfoDssDTO[] filelist = dataset.listFiles("original", true);
    String download_link = filelist[0].getPathInDataSet();
    String[] splitted_link = download_link.split("/");
    String fileName = splitted_link[splitted_link.length - 1];
    newDatasetBean.setCode(dataset.getCode());
    newDatasetBean.setName(fileName);
    StringBuilder dssPath =
        new StringBuilder(dataset.getDataSetDss().tryGetInternalPathInDataStore());
    dssPath.append("/");
    dssPath.append(filelist[0].getPathInDataSet());
    newDatasetBean.setDssPath(dssPath.toString());
    newDatasetBean.setType(dataset.getDataSetTypeCode());
    newDatasetBean.setFileSize(filelist[0].getFileSize());
    // TODO
    // newDatasetBean.setRegistrator(registrator);
    newDatasetBean.setRegistrationDate(dataset.getRegistrationDate());

    newDatasetBean.setParent(null);
    newDatasetBean.setRoot(newDatasetBean);

    newDatasetBean.setSelected(false);


    if (filelist[0].isDirectory()) {
      newDatasetBean.setDirectory(filelist[0].isDirectory());
      String folderPath = filelist[0].getPathInDataSet();
      FileInfoDssDTO[] subList = dataset.listFiles(folderPath, false);
      datasetBeanChildren(newDatasetBean, subList, dataset);
    }

    // TODO
    // this.fileSize = fileSize;
    // this.humanReadableFileSize = humanReadableFileSize;
    // this.dssPath = dssPath;
    return newDatasetBean;
  }

  public void datasetBeanChildren(DatasetBean datasetBean, FileInfoDssDTO[] fileList, DataSet d) {
    ArrayList<DatasetBean> beans = new ArrayList<DatasetBean>();
    for (FileInfoDssDTO dto : fileList) {
      DatasetBean newBean = new DatasetBean();
      newBean.setCode(datasetBean.getCode());
      StringBuilder dssPath = new StringBuilder(datasetBean.getDssPath());
      dssPath.append("/");
      dssPath.append(dto.getPathInDataSet());
      newBean.setDssPath(dssPath.toString());
      newBean.setExperiment(datasetBean.getExperiment());
      String download_link = dto.getPathInDataSet();
      String[] splitted_link = download_link.split("/");
      newBean.setFileName(splitted_link[splitted_link.length - 1]);
      newBean.setFileSize(dto.getFileSize());
      newBean.setFileType(d.getDataSetTypeCode());
      newBean.setHumanReadableFileSize(PortalUtils.humanReadableByteCount(dto.getFileSize(), true));
      newBean.setParent(datasetBean);
      newBean.setProject(datasetBean.getProject());
      newBean.setRegistrationDate(datasetBean.getRegistrationDate());
      newBean.setRegistrator(datasetBean.getRegistrator());
      newBean.setRoot(datasetBean.getRoot());
      newBean.setSample(datasetBean.getSample());
      newBean.setSelected(datasetBean.getIsSelected().getValue());
      newBean.setDirectory(false);
      if (dto.isDirectory()) {
        newBean.setDirectory(true);
        String folderPath = dto.getPathInDataSet();
        FileInfoDssDTO[] subList = d.listFiles(folderPath, false);
        datasetBeanChildren(newBean, subList, d);
      }
      beans.add(newBean);

    }
    datasetBean.setChildren(beans);
  }

  /**
   * Creates a Map of project statuses fulfilled, keyed by their meaning. For this, different steps
   * in the project flow are checked by looking at experiment types and data registered
   * 
   * @param projectBean
   * @return
   */
  public Map<String, Integer> computeProjectStatuses(ProjectBean projectBean) {

    // Project p = this.openBisClient.getProjectByCode(projectId);
    Map<String, Integer> res = new HashMap<String, Integer>();
    BeanItemContainer<ExperimentBean> cont = projectBean.getExperiments();

    // project was planned (otherwise it would hopefully not exist :) )
    res.put("Project planned", 1);

    // design is pre-registered to the test sample level
    int prereg = 0;
    for (ExperimentBean bean : cont.getItemIds()) {
      String type = bean.getType();

      if (type.equals("Q_EXPERIMENTAL_DESIGN")) {
        prereg = 1;
        break;
      }
    }
    res.put("Experimental design registered", prereg);
    // data is uploaded
    // TODO fix that
    // if (datasetMap.get(p.getIdentifier()) != null)
    // res.put("Data Registered", 1);
    // else
    int dataregistered = projectBean.getContainsData() ? 1 : 0;
    int resultsregistered = projectBean.getContainsResults() ? 1 : 0;
    // int attachmentsregistered = projectBean.getContainsAttachments() ? 1 : 0;

    // res.put("Attachments registered", attachmentsregistered);
    res.put("Raw data registered", dataregistered);
    res.put("Results registered", resultsregistered);

    return res;
  }

  public BeanItemContainer<ExperimentStatusBean> computeIvacPatientStatus(ProjectBean projectBean) {

    BeanItemContainer<ExperimentStatusBean> res =
        new BeanItemContainer<ExperimentStatusBean>(ExperimentStatusBean.class);
    BeanItemContainer<ExperimentBean> cont = projectBean.getExperiments();

    // TODO set download link and workflow triggering
    // TODO add immune monitoring, report generation, vaccine design

    ExperimentStatusBean barcode = new ExperimentStatusBean();
    barcode.setDescription("Barcode Generation");
    barcode.setStatus(1.0);

    ExperimentStatusBean ngsCall = new ExperimentStatusBean();
    ngsCall.setDescription("Variant Calling");
    ngsCall.setStatus(0.0);

    ExperimentStatusBean hlaType = new ExperimentStatusBean();
    hlaType.setDescription("HLA Typing");
    hlaType.setStatus(0.0);

    ExperimentStatusBean variantAnno = new ExperimentStatusBean();
    variantAnno.setDescription("Variant Annotation");
    variantAnno.setStatus(0.0);

    ExperimentStatusBean epitopePred = new ExperimentStatusBean();
    epitopePred.setDescription("Epitope Prediction");
    epitopePred.setStatus(0.0);

    for (ExperimentBean bean : cont.getItemIds()) {
      String type = bean.getType();

      Double experimentStatus = bean.getProperties().get("Q_CURRENT_STATUS") == null ? 0.0
          : OpenBisFunctions
              .statusToDoubleValue(bean.getProperties().get("Q_CURRENT_STATUS").toString());
      if (type.equalsIgnoreCase(ExperimentType.Q_NGS_MEASUREMENT.name())) {

        ExperimentStatusBean ngsMeasure = new ExperimentStatusBean();
        ngsMeasure.setDescription("NGS Sequencing");
        ngsMeasure.setStatus(0.0);
        ngsMeasure.setStatus(experimentStatus);
        ngsMeasure.setCode(bean.getCode());
        ngsMeasure.setIdentifier(bean.getId());

        res.addBean(ngsMeasure);
      }
      if (type.equalsIgnoreCase(ExperimentType.Q_NGS_VARIANT_CALLING.name())) {
        ngsCall.setStatus(experimentStatus);
        ngsCall.setCode(bean.getCode());
        ngsCall.setIdentifier(bean.getId());
      }
      if (type.equalsIgnoreCase(ExperimentType.Q_NGS_HLATYPING.name())
          | type.equalsIgnoreCase(ExperimentType.Q_WF_NGS_HLATYPING.name())) {
        if (type.equalsIgnoreCase(ExperimentType.Q_WF_NGS_HLATYPING.name())) {
          hlaType.setStatus(OpenBisFunctions
              .statusToDoubleValue(bean.getProperties().get("Q_WF_STATUS").toString()));
        } else {
          hlaType.setStatus(experimentStatus);
        }
        hlaType.setCode(bean.getCode());
        hlaType.setIdentifier(bean.getId());
      }
      if (type.equalsIgnoreCase(ExperimentType.Q_WF_NGS_VARIANT_ANNOTATION.name())) {
        variantAnno.setStatus(OpenBisFunctions
            .statusToDoubleValue(bean.getProperties().get("Q_WF_STATUS").toString()));
        variantAnno.setCode(bean.getCode());
        variantAnno.setIdentifier(bean.getId());
      }
      if (type.equalsIgnoreCase(ExperimentType.Q_WF_NGS_EPITOPE_PREDICTION.name())) {
        epitopePred.setStatus(OpenBisFunctions
            .statusToDoubleValue(bean.getProperties().get("Q_WF_STATUS").toString()));
        epitopePred.setCode(bean.getCode());
        epitopePred.setIdentifier(bean.getId());
      }
    }

    res.addBean(barcode);
    res.addBean(ngsCall);
    res.addBean(hlaType);
    res.addBean(variantAnno);
    res.addBean(epitopePred);

    return res;
  }

  public ThemeResource setExperimentStatusColor(String status) {
    ThemeResource resource = null;
    if (status.equals("FINISHED")) {
      resource = new ThemeResource("green_light.png");
    } else if (status.equals("DELAYED")) {
      resource = new ThemeResource("yellow_light.png");
    } else if (status.equals("STARTED")) {
      resource = new ThemeResource("grey_light.png");
    } else if (status.equals("FAILED")) {
      resource = new ThemeResource("red_light.png");
    } else {
      resource = new ThemeResource("red_light.png");
    }

    // image.setWidth("15px");
    // image.setHeight("15px");\
    return resource;
  }

  // public String beanContainerToString(BeanItemContainer c) {
  // String header = "";
  // for (Object o : c.getContainerPropertyIds())
  // header += o.toString() + "\t";
  // for (c.get)
  // }


  public List<Qperson> parseConnectedPeopleInformation(String xmlString) {
    PersonParser xmlParser = new PersonParser();
    List<Qperson> xmlPersons = null;
    try {
      xmlPersons = xmlParser.getPersonsFromXML(xmlString);
    } catch (JAXBException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return xmlPersons;
  }

  public void fillPersonsContainer(String spaceIdentifier) {
    List<Sample> samplesOfSpace = new ArrayList<Sample>();
    samplesOfSpace = this.getOpenBisClient().getSamplesofSpace(spaceIdentifier);

    if (this.connectedPersons.size() == 0) {
      for (PropertyType p : this.getOpenBisClient()
          .listPropertiesForType(this.getOpenBisClient().getSampleTypeByString(("Q_USER")))) {
        this.connectedPersons.addContainerProperty(p.getLabel(), String.class, null);
      }
      this.connectedPersons.addContainerProperty("Project", String.class, null);
    }

    for (Sample s : samplesOfSpace) {
      List<Sample> parents = this.getOpenBisClient().getParentsBySearchService(s.getCode());
      Map<String, String> labelMap = this.getOpenBisClient().getLabelsofProperties(
          this.getOpenBisClient().getSampleTypeByString(s.getSampleTypeCode()));

      for (Sample parent : parents) {
        Object newPerson = this.connectedPersons.addItem();
        Iterator it = s.getProperties().entrySet().iterator();
        while (it.hasNext()) {
          Entry pairs = (Entry) it.next();
          this.connectedPersons.getContainerProperty(newPerson, labelMap.get(pairs.getKey()))
              .setValue(pairs.getValue());
        }
        this.connectedPersons.getContainerProperty(newPerson, "Project")
            .setValue(this.getOpenBisClient()
                .getProjectOfExperimentByIdentifier(parent.getExperimentIdentifierOrNull())
                .getCode().toString());

      }
    }
  }

  public void registerNewPatients(int numberPatients, List<String> secondaryNames,
      BeanItemContainer<NewIvacSampleBean> samplesToRegister, String space, String description,
      Map<String, List<String>> hlaTyping) {

    String portalUser = PortalUtils.getNonNullScreenName();

    // get prefix code for projects for corresponding space
    String projectPrefix = spaceToProjectPrefixMap.myMap.get(space);

    // extract to function for that
    List<Integer> projectCodes = new ArrayList<Integer>();
    for (Project p : getOpenBisClient().getProjectsOfSpace(space)) {
      // String maxValue = Collections.max(p.getCode());
      String maxValue = p.getCode().replaceAll("\\D+", "");
      int codeAsNumber;
      try {
        codeAsNumber = Integer.parseInt(maxValue);
      } catch (NumberFormatException nfe) {
        // bad data - set to sentinel
        codeAsNumber = 0;
      }

      projectCodes.add(codeAsNumber);
    }

    int numberOfProject;

    if (projectCodes.size() == 0) {
      numberOfProject = 0;
    } else {
      numberOfProject = Collections.max(projectCodes);
    }

    for (int i = 0; i < numberPatients; i++) {
      Map<String, Object> projectMap = new HashMap<String, Object>();
      Map<String, Object> firstLevel = new HashMap<String, Object>();

      numberOfProject += 1;
      int numberOfRegisteredExperiments = 1;
      int numberOfRegisteredSamples = 1;

      // register new patient (project), project prefixes differ in length
      String newProjectCode =
          projectPrefix + Utils.createCountString(numberOfProject, 5 - projectPrefix.length());

      projectMap.put("code", newProjectCode);
      projectMap.put("space", space);
      projectMap.put("desc", description + " [" + secondaryNames.get(i) + "]");
      projectMap.put("user", portalUser);

      // call of ingestion service to register project
      this.getOpenBisClient().triggerIngestionService("register-proj", projectMap);
      // helpers.Utils.printMapContent(projectMap);

      String newProjectDetailsID =
          ExperimentCodeFunctions.getInfoExperimentID(space, newProjectCode);

      String newExperimentalDesignCode = projectPrefix + Utils.createCountString(numberOfProject, 3)
          + "E" + numberOfRegisteredExperiments;
      String newExperimentalDesignID =
          "/" + space + "/" + newProjectCode + "/" + newExperimentalDesignCode;
      numberOfRegisteredExperiments += 1;

      // String newBiologicalEntitiyCode =
      // newProjectCode + Utils.createCountString(numberOfRegisteredSamples, 3) + "H";
      // String newBiologicalEntitiyID =
      // "/" + space + "/" + newBiologicalEntitiyCode
      // + helpers.BarcodeFunctions.checksum(newBiologicalEntitiyCode);
      String newBiologicalEntitiyID =
          String.format("/" + space + "/" + "%sENTITY-1", newProjectCode);

      numberOfRegisteredSamples += 1;

      // register first level of new patient, including "info experiment"
      firstLevel.put("lvl", "1");
      firstLevel.put("projectDetails", newProjectDetailsID);
      firstLevel.put("experimentalDesign", newExperimentalDesignID);
      firstLevel.put("secondaryName", secondaryNames.get(i));
      firstLevel.put("biologicalEntity", newBiologicalEntitiyID);
      firstLevel.put("user", portalUser);

      this.getOpenBisClient().triggerIngestionService("register-ivac-lvl", firstLevel);

      // helpers.Utils.printMapContent(firstLevel);

      Map<String, Object> fithLevel = new HashMap<String, Object>();

      List<String> newHLATypingIDs = new ArrayList<String>();
      List<String> newHLATypingSampleIDs = new ArrayList<String>();
      List<String> hlaClasses = new ArrayList<String>();
      List<String> typings = new ArrayList<String>();
      List<String> typingMethods = new ArrayList<String>();

      // TODO choose parent sample for hlaTyping
      String parentHLA = "";


      for (Iterator iter = samplesToRegister.getItemIds().iterator(); iter.hasNext();) {

        NewIvacSampleBean sampleBean = (NewIvacSampleBean) iter.next();

        for (int ii = 1; ii <= sampleBean.getAmount(); ii++) {
          Map<String, Object> secondLevel = new HashMap<String, Object>();
          Map<String, Object> thirdLevel = new HashMap<String, Object>();
          Map<String, Object> fourthLevel = new HashMap<String, Object>();

          List<String> newSamplePreparationIDs = new ArrayList<String>();
          List<String> newTestSampleIDs = new ArrayList<String>();
          List<String> testTypes = new ArrayList<String>();

          List<String> newNGSMeasurementIDs = new ArrayList<String>();
          List<String> newNGSRunIDs = new ArrayList<String>();
          List<Boolean> additionalInfo = new ArrayList<Boolean>();
          List<String> parents = new ArrayList<String>();

          List<String> newSampleExtractionIDs = new ArrayList<String>();
          List<String> newBiologicalSampleIDs = new ArrayList<String>();
          List<String> primaryTissues = new ArrayList<String>();
          List<String> detailedTissue = new ArrayList<String>();
          List<String> sequencerDevice = new ArrayList<String>();

          String newSampleExtractionCode = newProjectCode + "E" + numberOfRegisteredExperiments;
          newSampleExtractionIDs
              .add("/" + space + "/" + newProjectCode + "/" + newSampleExtractionCode);
          numberOfRegisteredExperiments += 1;

          String newBiologicalSampleCode =
              newProjectCode + Utils.createCountString(numberOfRegisteredSamples, 3) + "B";
          String newBiologicalSampleID = "/" + space + "/" + newBiologicalSampleCode
              + BarcodeFunctions.checksum(newBiologicalSampleCode);

          parentHLA = newBiologicalSampleID;

          newBiologicalSampleIDs.add(newBiologicalSampleID);
          numberOfRegisteredSamples += 1;

          primaryTissues.add(sampleBean.getTissue());
          detailedTissue.add(sampleBean.getType());

          // register second level of new patient
          secondLevel.put("lvl", "2");
          secondLevel.put("sampleExtraction", newSampleExtractionIDs);
          secondLevel.put("biologicalSamples", newBiologicalSampleIDs);

          if (sampleBean.getSecondaryName() == null) {
            secondLevel.put("secondaryNames", "");
          } else {
            secondLevel.put("secondaryNames", sampleBean.getSecondaryName());
          }
          secondLevel.put("parent", newBiologicalEntitiyID);
          secondLevel.put("primaryTissue", primaryTissues);
          secondLevel.put("detailedTissue", detailedTissue);
          secondLevel.put("user", portalUser);

          this.getOpenBisClient().triggerIngestionService("register-ivac-lvl", secondLevel);
          // helpers.Utils.printMapContent(secondLevel);

          if (sampleBean.getDnaSeq()) {
            String newSamplePreparationCode = newProjectCode + "E" + numberOfRegisteredExperiments;
            String newSamplePreparationID =
                "/" + space + "/" + newProjectCode + "/" + newSamplePreparationCode;
            newSamplePreparationIDs.add(newSamplePreparationID);
            numberOfRegisteredExperiments += 1;

            String newTestSampleCode =
                newProjectCode + Utils.createCountString(numberOfRegisteredSamples, 3) + "B";
            String newTestSampleID = "/" + space + "/" + newTestSampleCode
                + BarcodeFunctions.checksum(newTestSampleCode);
            newTestSampleIDs.add(newTestSampleID);
            numberOfRegisteredSamples += 1;
            testTypes.add("DNA");

            String newNGSMeasurementCode = newProjectCode + "E" + numberOfRegisteredExperiments;
            String newNGSMeasurementID =
                "/" + space + "/" + newProjectCode + "/" + newNGSMeasurementCode;
            newNGSMeasurementIDs.add(newNGSMeasurementID);
            numberOfRegisteredExperiments += 1;

            String newNGSRunCode =
                newProjectCode + Utils.createCountString(numberOfRegisteredSamples, 3) + "R";
            String newNGSRunID =
                "/" + space + "/" + newNGSRunCode + BarcodeFunctions.checksum(newNGSRunCode);
            newNGSRunIDs.add(newNGSRunID);
            numberOfRegisteredSamples += 1;

            additionalInfo.add(false);
            sequencerDevice.add(sampleBean.getSeqDevice());
            parents.add(newTestSampleID);

          }

          if (sampleBean.getRnaSeq()) {
            String newSamplePreparationCode = newProjectCode + "E" + numberOfRegisteredExperiments;
            String newSamplePreparationID =
                "/" + space + "/" + newProjectCode + "/" + newSamplePreparationCode;
            newSamplePreparationIDs.add(newSamplePreparationID);
            numberOfRegisteredExperiments += 1;

            String newTestSampleCode =
                newProjectCode + Utils.createCountString(numberOfRegisteredSamples, 3) + "B";
            String newTestSampleID = "/" + space + "/" + newTestSampleCode
                + BarcodeFunctions.checksum(newTestSampleCode);
            newTestSampleIDs.add(newTestSampleID);
            numberOfRegisteredSamples += 1;
            testTypes.add("RNA");

            String newNGSMeasurementCode = newProjectCode + "E" + numberOfRegisteredExperiments;
            String newNGSMeasurementID =
                "/" + space + "/" + newProjectCode + "/" + newNGSMeasurementCode;
            newNGSMeasurementIDs.add(newNGSMeasurementID);
            numberOfRegisteredExperiments += 1;

            String newNGSRunCode =
                newProjectCode + Utils.createCountString(numberOfRegisteredSamples, 3) + "R";
            String newNGSRunID =
                "/" + space + "/" + newNGSRunCode + BarcodeFunctions.checksum(newNGSRunCode);
            newNGSRunIDs.add(newNGSRunID);
            numberOfRegisteredSamples += 1;

            additionalInfo.add(false);
            sequencerDevice.add(sampleBean.getSeqDevice());
            parents.add(newTestSampleID);
          }

          if (sampleBean.getDeepSeq()) {
            String newSamplePreparationCode = newProjectCode + "E" + numberOfRegisteredExperiments;
            String newSamplePreparationID =
                "/" + space + "/" + newProjectCode + "/" + newSamplePreparationCode;
            newSamplePreparationIDs.add(newSamplePreparationID);
            numberOfRegisteredExperiments += 1;

            String newTestSampleCode =
                newProjectCode + Utils.createCountString(numberOfRegisteredSamples, 3) + "B";
            String newTestSampleID = "/" + space + "/" + newTestSampleCode
                + BarcodeFunctions.checksum(newTestSampleCode);
            newTestSampleIDs.add(newTestSampleID);
            numberOfRegisteredSamples += 1;
            testTypes.add("DNA");

            String newNGSMeasurementCode = newProjectCode + "E" + numberOfRegisteredExperiments;
            String newNGSMeasurementID =
                "/" + space + "/" + newProjectCode + "/" + newNGSMeasurementCode;
            newNGSMeasurementIDs.add(newNGSMeasurementID);
            numberOfRegisteredExperiments += 1;

            String newNGSRunCode =
                newProjectCode + Utils.createCountString(numberOfRegisteredSamples, 3) + "R";
            String newNGSRunID =
                "/" + space + "/" + newNGSRunCode + BarcodeFunctions.checksum(newNGSRunCode);
            newNGSRunIDs.add(newNGSRunID);
            numberOfRegisteredSamples += 1;

            additionalInfo.add(true);
            sequencerDevice.add(sampleBean.getSeqDevice());
            parents.add(newTestSampleID);
          }

          // register third and fourth level of new patient
          thirdLevel.put("lvl", "3");
          thirdLevel.put("parent", newBiologicalSampleID);
          thirdLevel.put("experiments", newSamplePreparationIDs);
          thirdLevel.put("samples", newTestSampleIDs);
          thirdLevel.put("types", testTypes);
          thirdLevel.put("user", portalUser);

          fourthLevel.put("lvl", "4");
          fourthLevel.put("experiments", newNGSMeasurementIDs);
          fourthLevel.put("samples", newNGSRunIDs);
          fourthLevel.put("parents", parents);
          fourthLevel.put("types", testTypes);
          fourthLevel.put("info", additionalInfo);
          fourthLevel.put("device", sequencerDevice);
          fourthLevel.put("user", portalUser);

          // TODO additional level for HLA typing

          // call of ingestion services for differeny levels
          // helpers.Utils.printMapContent(thirdLevel);
          // helpers.Utils.printMapContent(fourthLevel);
          this.getOpenBisClient().triggerIngestionService("register-ivac-lvl", thirdLevel);
          this.getOpenBisClient().triggerIngestionService("register-ivac-lvl", fourthLevel);
        }
      }

      for (Entry<String, List<String>> entry : hlaTyping.entrySet()) {

        String newHLATyping = newProjectCode + "E" + numberOfRegisteredExperiments;

        newHLATypingIDs.add("/" + space + "/" + newProjectCode + "/" + newHLATyping);

        numberOfRegisteredExperiments += 1;

        String newHLATypingSampleCode =
            newProjectCode + Utils.createCountString(numberOfRegisteredSamples, 3) + "H";

        String newHLATypingSampleID = "/" + space + "/" + newHLATypingSampleCode
            + BarcodeFunctions.checksum(newHLATypingSampleCode);

        newHLATypingSampleIDs.add(newHLATypingSampleID);
        numberOfRegisteredSamples += 1;

        hlaClasses.add(entry.getKey());
        typings.add(entry.getValue().get(0));
        typingMethods.add(entry.getValue().get(1));
      }

      fithLevel.put("lvl", "5");
      fithLevel.put("experiments", newHLATypingIDs);
      fithLevel.put("samples", newHLATypingSampleIDs);
      fithLevel.put("typings", typings);
      fithLevel.put("classes", hlaClasses);
      fithLevel.put("methods", typingMethods);
      fithLevel.put("parent", parentHLA);

      this.getOpenBisClient().triggerIngestionService("register-ivac-lvl", fithLevel);

      // main ivac metadata registered, finish experimental design information:
      // first, update design xml
      Map<String, Map<Pair<String, String>, List<String>>> expDesign = new HashMap<>();
      Map<String, List<Qproperty>> otherProps = new HashMap<>();
      List<TechnologyType> techTypes = new ArrayList<>();
      techTypes.add(new TechnologyType("Genomics"));
      JAXBElement<Qexperiment> res = null;
      try {
        res = studyParser.createNewDesign(new HashSet<>(), techTypes, expDesign, otherProps);
      } catch (JAXBException e) {
        LOG.error("Failed to create experimental design");
        e.printStackTrace();
      }
      String xml = "";
      try {
        xml = studyParser.toString(res);
      } catch (JAXBException e) {
        LOG.error("Failed to convert experimental design to xml");
        e.printStackTrace();
      }
      Map<String, Object> props = new HashMap<>();
      props.put("Q_EXPERIMENTAL_SETUP", xml);

      HashMap<String, Object> expParams = new HashMap<>();
      expParams.put("properties", props);
      expParams.put("user", portalUser);
      expParams.put("identifier", newProjectDetailsID);
      getOpenBisClient().triggerIngestionService("update-experiment-metadata", expParams);

      // then, add "attachment sample"
      String sampleCode = newProjectCode + "000";
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("user", portalUser);

      if (getOpenBisClient().sampleExists(sampleCode)) {
        LOG.warn(sampleCode + " already exists in " + newProjectCode
            + " Removing this sample from registration process.");
      } else {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", sampleCode);
        map.put("space", space);
        map.put("project", newProjectCode);
        map.put("experiment", newProjectCode + "_INFO");
        map.put("type", SampleType.Q_ATTACHMENT_SAMPLE.toString());
        map.put("metadata", new HashMap<>());
        params.put(sampleCode, map);
        LOG.info("Trying to register attachment sample for new ivac project: " + sampleCode);
        getOpenBisClient().triggerIngestionService("register-sample-batch", params);
      }
    }
  }

  /**
   * 
   * @param statusValues
   * @return
   * @deprecated
   */
  public VerticalLayout createProjectStatusComponent(Map<String, Integer> statusValues) {
    VerticalLayout projectStatusContent = new VerticalLayout();

    Iterator<Entry<String, Integer>> it = statusValues.entrySet().iterator();
    int finishedExperiments = 0;

    while (it.hasNext()) {
      Entry<String, Integer> pairs = (Entry<String, Integer>) it.next();

      if ((Integer) pairs.getValue() == 0) {
        Label statusLabel =
            new Label(pairs.getKey() + ": " + FontAwesome.TIMES.getHtml(), ContentMode.HTML);
        statusLabel.addStyleName("redicon");
        projectStatusContent.addComponent(statusLabel);
      }

      else {
        Label statusLabel =
            new Label(pairs.getKey() + ": " + FontAwesome.CHECK.getHtml(), ContentMode.HTML);
        statusLabel.addStyleName("greenicon");

        if (pairs.getKey().equals("Project Planned")) {
          projectStatusContent.addComponentAsFirst(statusLabel);
        } else {
          projectStatusContent.addComponent(statusLabel);

        }
        finishedExperiments += (Integer) pairs.getValue();
      }
    }
    // ProgressBar progressBar = new ProgressBar();
    // progressBar.setValue((float) finishedExperiments / statusValues.keySet().size());
    // projectStatusContent.addComponent(progressBar);

    return projectStatusContent;
  }

  /**
   * 
   * @param statusValues
   * @return
   */
  public VerticalLayout createProjectStatusComponentNew(Map<String, Integer> statusValues) {
    VerticalLayout projectStatusContent = new VerticalLayout();
    projectStatusContent.setResponsive(true);
    projectStatusContent.setMargin(true);
    projectStatusContent.setSpacing(true);

    Label planned = new Label();
    Label design = new Label();
    Label raw = new Label();
    Label results = new Label();

    Iterator<Entry<String, Integer>> it = statusValues.entrySet().iterator();

    while (it.hasNext()) {
      Entry<String, Integer> pairs = (Entry<String, Integer>) it.next();

      if ((Integer) pairs.getValue() == 0) {
        Label statusLabel = new Label(pairs.getKey());
        statusLabel.setStyleName(ValoTheme.LABEL_FAILURE);
        statusLabel.setResponsive(true);
        // statusLabel.addStyleName("redicon");
        if (pairs.getKey().equals("Project planned")) {
          planned = statusLabel;
        } else if (pairs.getKey().equals("Experimental design registered")) {
          design = statusLabel;
        } else if (pairs.getKey().equals("Raw data registered")) {
          raw = statusLabel;
        } else if (pairs.getKey().equals("Results registered")) {
          results = statusLabel;
        }
      }

      else {
        Label statusLabel = new Label(pairs.getKey());
        statusLabel.setStyleName(ValoTheme.LABEL_SUCCESS);
        statusLabel.setResponsive(true);

        // statusLabel.addStyleName("greenicon");

        if (pairs.getKey().equals("Project planned")) {
          planned = statusLabel;
        } else if (pairs.getKey().equals("Experimental design registered")) {
          design = statusLabel;
        } else if (pairs.getKey().equals("Raw data registered")) {
          raw = statusLabel;
        } else if (pairs.getKey().equals("Results registered")) {
          results = statusLabel;
        }
      }
    }

    projectStatusContent.addComponent(planned);
    projectStatusContent.addComponent(design);
    projectStatusContent.addComponent(raw);
    projectStatusContent.addComponent(results);

    // ProgressBar progressBar = new ProgressBar();
    // progressBar.setValue((float) finishedExperiments / statusValues.keySet().size());
    // projectStatusContent.addComponent(progressBar);

    return projectStatusContent;
  }

  /**
   * Get secondary Name of parent or parents of parent
   * 
   * @param samp
   * @return
   */
  public String getSecondaryName(Sample samp, String datsetSecName) {
    List<Sample> firstParents = samp.getParents();
    String secondaryName = "";
    Set<String> secNamesTest = new LinkedHashSet<String>();
    Set<String> secNamesBiological = new LinkedHashSet<String>();
    Set<String> secNamesEntities = new LinkedHashSet<String>();
    Set<String> allDescriptions = new LinkedHashSet<String>();
    List<Sample> allParents = new ArrayList<Sample>();

    for (Sample p : firstParents) {
      allParents.add(p);
      for (Sample q : p.getParents()) {
        allParents.add(q);
        for (Sample r : q.getParents()) {
          allParents.add(r);
          for (Sample s : r.getParents()) {
            allParents.add(s);
          }
        }
      }
    }

    for (Sample pp : allParents) {
      if (pp.getSampleTypeCode().equals("Q_TEST_SAMPLE")) {
        String new_sec = pp.getProperties().get("Q_SECONDARY_NAME");
        if (new_sec != null) {
          secNamesTest.add(new_sec);
        }
      } else if (pp.getSampleTypeCode().equals("Q_BIOLOGICAL_SAMPLE")) {
        String new_sec = pp.getProperties().get("Q_SECONDARY_NAME");
        if (new_sec != null) {
          secNamesBiological.add(new_sec);
        }

      } else if (pp.getSampleTypeCode().equals("Q_BIOLOGICAL_ENTITY")) {
        String new_sec = pp.getProperties().get("Q_SECONDARY_NAME");
        if (new_sec != null) {
          secNamesEntities.add(new_sec);
        }
      }
    }

    allDescriptions.addAll(secNamesEntities);
    allDescriptions.addAll(secNamesBiological);
    allDescriptions.addAll(secNamesTest);

    if (datsetSecName != null) {
      allDescriptions.add(datsetSecName);
    }

    secondaryName = String.join("_", allDescriptions);

    return secondaryName.replace("__", "_").replaceAll("^_+", "").replaceAll("_+$", "");
  }

  /**
   * generates informative description from dataset secondary name or the dataset's experiment,
   * given its ID. should only be used if a dataset does not have an associated sample
   * 
   * @param datasetSecondaryName the secondary name property of the dataset
   * @param experimentID the full openBIS identifier of the experiment of the dataset
   * @return informative String describing the dataset
   */
  public String retrieveDatasetInfoWithoutSample(String datasetSecondaryName, String experimentID) {
    // if dataset name was explicitly set, give it priority
    if (datasetSecondaryName != null && !datasetSecondaryName.isEmpty()) {
      return datasetSecondaryName;
    }
    // otherwise generate experiment-type-specific information
    List<Experiment> experiments = getOpenBisClient().getExperimentById2(experimentID);
    if (experiments.isEmpty()) {
      LOG.warn("Experiment with ID " + experimentID
          + " not found. Returning empty metadata to display.");
      return "";
    }
    Experiment experiment = experiments.get(0);
    Map<String, String> props = experiment.getProperties();
    String type = experiment.getExperimentTypeCode();

    if (type.equals("Q_NGS_NANOPORE_RUN")) {
      String res = props.get("Q_FLOWCELL_BARCODE");
      if (res == null) {
        LOG.warn("No flow cell barcode was found for Nanopore Run " + experimentID
            + ". Returning less informative metadata to display.");
        res = "unclassified reads";
      } else {
        res += " unclassified reads";
      }
      return res;
    } else {
      if (props.get("Q_SECONDARY_NAME") == null) {
        LOG.warn("No secondary name found for experiment " + experimentID
            + ". Returning empty metadata to display. You might want to implement a specific case for experiments of type "
            + type);
        return "";
      }
      return props.get("Q_SECONDARY_NAME");
    }
  }

  public OpenBisClient getOpenBisClient() {
    return openBisClient;
  }


  public void setOpenBisClient(OpenBisClient openBisClient) {
    this.openBisClient = openBisClient;
  }


  public DBManager getDatabaseManager() {
    return databaseManager;
  }


  public void setDatabaseManager(DBManager databaseManager) {
    this.databaseManager = databaseManager;
  }


  public Set<String> getFactorLabels() {
    return experimentalFactorLabels;
  }


  public Map<Pair<String, String>, Property> getFactorsForLabelsAndSamples() {
    return experimentalFactorsForLabelsAndSamples;
  }

  public Map<String, List<Property>> getPropertiesForSamples() {
    return propertiesForSamples;
  }

  public JAXBElement<Qexperiment> getExperimentalSetup() {
    return experimentalSetup;
  }
}
