package life.qbic.projectbrowser.samplegraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import ch.systemsx.cisd.openbis.dss.client.api.v1.DataSet;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Sample;

import life.qbic.xml.manager.XMLParser;
import life.qbic.xml.properties.Property;
import life.qbic.xml.properties.PropertyType;


public class ProjectParser {

  private XMLParser xmlParser;
  private Map<String, String> taxMap;
  private Map<String, String> tissueMap;
  // int idCounter;
  // private Set<String> visited;
  private Set<String> validLeafs =
      new HashSet<String>(Arrays.asList("Q_TEST_SAMPLE", "Q_MHC_LIGAND_EXTRACT"));
  private Set<String> validSamples = new HashSet<String>(Arrays.asList("Q_TEST_SAMPLE",
      "Q_MHC_LIGAND_EXTRACT", "Q_BIOLOGICAL_ENTITY", "Q_BIOLOGICAL_SAMPLE"));
  private Map<String, List<DataSet>> sampCodeToDS;
  private Set<String> codesWithDatasets;
  private Map<String, Sample> sampCodeToSamp;

  public ProjectParser(Map<String, String> taxMap, Map<String, String> tissueMap) {
    this.taxMap = taxMap;
    this.tissueMap = tissueMap;
  }

  private boolean collectCodesOfDatasetsAttachedToSamples(List<Sample> samples,
      Set<String> nodeCodes, int maxDepth) {
    boolean hasDatasets = false;
    if (maxDepth >= 0) {
      for (Sample s : samples) {
        hasDatasets = false;
        String code = s.getCode();
        if (sampCodeToDS.containsKey(code)) {
          hasDatasets = true;
          // for (DataSet ds : sampCodeToDS.get(code))//TODO codes needed?
          // dsCodes.add(ds.getCode());
        }
        hasDatasets |=
            collectCodesOfDatasetsAttachedToSamples(s.getChildren(), nodeCodes, maxDepth - 1);
        if (hasDatasets && validSamples.contains(s.getSampleTypeCode())) {
          nodeCodes.add(code);
        }
      }
    }
    return hasDatasets;
  }

  // add percentage of expected datasets that are found in the data store
  private void addDataSetCount(Collection<SampleSummary> summaries) {
    int maxDepth = 1; // maximum child levels from a sample that datasets count for
    for (SampleSummary node : summaries) {
      if (validLeafs.contains(node.getSampleType())) {
        Set<String> nodeCodes = new HashSet<String>();
        collectCodesOfDatasetsAttachedToSamples(node.getSamples(), nodeCodes, maxDepth);
        int expected = node.getSamples().size();
        int numData = nodeCodes.size();
        if (numData > expected)
          expected = numData;
        node.setMeasuredPercent(numData * 100 / expected);

        codesWithDatasets.addAll(nodeCodes);
      }
    }
  }

  // long startTime = System.nanoTime();
  // methodToTime();
  // long endTime = System.nanoTime();
  //
  // long duration = (endTime - startTime); //divide by 1000000 to get milliseconds.

  // public StructuredExperiment parseAll(List<Sample> allSamples, List<DataSet> datasets)
  // throws JAXBException {
  // this.xmlParser = new XMLParser();
  // sampCodeToDS = new HashMap<String, List<DataSet>>();
  // sampCodeToSamp = new HashMap<String, Sample>();
  // for (DataSet d : datasets) {
  // String code = d.getSampleIdentifierOrNull().split("/")[2];
  // if (sampCodeToDS.containsKey(code))
  // sampCodeToDS.get(code).add(d);
  // else
  // sampCodeToDS.put(code, new ArrayList<DataSet>(Arrays.asList(d)));
  // }
  // Set<String> knownFactors = new HashSet<String>();
  // List<Sample> roots = new ArrayList<Sample>();
  //
  // for (Sample s : allSamples) {
  // sampCodeToSamp.put(s.getCode(), s);
  // Map<String, String> p = s.getProperties();
  // List<Factor> factors = xmlParser.getFactors(xmlParser.parseXMLString(p.get("Q_PROPERTIES")));
  // for (Factor f : factors)
  // knownFactors.add(f.getLabel());
  // if (s.getParents().isEmpty() && validSamples.contains(s.getSampleTypeCode()))
  // roots.add(s);
  // }
  // knownFactors.add("None");
  // Map<String, List<SampleSummary>> factorsToSamples = new HashMap<String, List<SampleSummary>>();
  //
  // // arrays/lists of everything needed, so samples have to be visited only once (speedup)
  // int numFactors = knownFactors.size();
  // int[] ids = new int[numFactors];
  // // stores Node Summary, everything that makes a node unique...and the samples that lead to
  // // this summary
  // List<Map<NewNodeSummary, List<Sample>>> summariesList =
  // new ArrayList<Map<NewNodeSummary, List<Sample>>>();
  // // stores in which summary a sample ends up
  // List<Map<Sample, NewNodeSummary>> sampleToSummaryList =
  // new ArrayList<Map<Sample, NewNodeSummary>>();
  // List<Map<Integer, NewNodeSummary>> orderList = new ArrayList<Map<Integer, NewNodeSummary>>();
  //
  // for (int i = 0; i < numFactors; i++) {
  // summariesList.add(new HashMap<NewNodeSummary, List<Sample>>());
  // sampleToSummaryList.add(new HashMap<Sample, NewNodeSummary>());
  // orderList.add(new HashMap<Integer, NewNodeSummary>());
  // }
  // List<String> factorLabels = new ArrayList<String>(knownFactors);
  //
  //
  // createSummariesRecursive2(roots, sampleToSummaryList, summariesList, factorLabels, ids,
  // orderList);
  // for (int i = 0; i < ids.length; i++) {
  // for (NewNodeSummary sum : summariesList.get(i).keySet()) {
  // for (Sample s : summariesList.get(i).get(sum)) {
  // for (Sample c : s.getChildren()) {
  // if (sampleToSummaryList.get(i).containsKey(c))
  // sum.addChild(sampleToSummaryList.get(i).get(c));
  // }
  // }
  // }
  // // create real graph objects
  // List<SampleSummary> res = convertNodeSummariesToSampleSummaries(summariesList.get(i),
  // sampleToSummaryList.get(i), orderList.get(i));
  // addDataSetCount(res);
  // factorsToSamples.put(WordUtils.capitalize(factorLabels.get(i)), res);
  // }
  // // one graph for every experimental factor
  // // for (String label : knownFactors) {
  // // // stores Node Summary, everything that makes a node unique...and the samples that lead to
  // // // this summary
  // // Map<NewNodeSummary, List<Sample>> summaries = new HashMap<NewNodeSummary, List<Sample>>();
  // // // stores in which summary a sample ends up
  // // Map<Sample, NewNodeSummary> sampleToSummary = new HashMap<Sample, NewNodeSummary>();
  // //
  // // int[] id = {0};
  // // Map<Integer, NewNodeSummary> order = new HashMap<Integer, NewNodeSummary>();
  // // System.out.println("creating summaries for factor label " + label);
  // // startTime = System.nanoTime();
  // // createSummariesRecursive(roots, sampleToSummary, summaries, label, id, order);
  // // endTime = System.nanoTime();
  // // System.err.println((endTime - startTime) / 1000000);
  // // for (NewNodeSummary sum : summaries.keySet()) {
  // // for (Sample s : summaries.get(sum)) {
  // // for (Sample c : s.getChildren()) {
  // // if (sampleToSummary.containsKey(c))
  // // sum.addChild(sampleToSummary.get(c));
  // // }
  // // }
  // // }
  // // // create real graph objects
  // // List<SampleSummary> res =
  // // convertNodeSummariesToSampleSummaries(summaries, sampleToSummary, order);
  // // addDataSetCount(res);
  // // factorsToSamples.put(WordUtils.capitalize(label), res);
  // // }
  // return new StructuredExperiment(factorsToSamples);
  // }

  // private void createSummariesRecursive2(List<Sample> roots,
  // List<Map<Sample, NewNodeSummary>> sampleToSummaryList,
  // List<Map<NewNodeSummary, List<Sample>>> summariesList, List<String> factorLabels, int[] ids,
  // List<Map<Integer, NewNodeSummary>> orderList) throws JAXBException {
  // // System.out.println("recurse");
  // // List<Sample> nextLvl = new ArrayList<Sample>();
  // for (Sample s : roots) {
  // String type = s.getSampleTypeCode();
  // List<Sample> nextLvl = new ArrayList<Sample>();
  // // if sample type should be shown in graph...TODO intermediate samples should never be
  // // hidden for obvious reasons
  // if (validSamples.contains(type)) {
  // nextLvl.addAll(s.getChildren());
  //
  // for (int i = 0; i < ids.length; i++) {
  // // fill maps
  // Map<NewNodeSummary, List<Sample>> summaries = summariesList.get(i);
  // Map<Integer, NewNodeSummary> order = orderList.get(i);
  // Map<Sample, NewNodeSummary> sampleToSummary = sampleToSummaryList.get(i);
  // String label = factorLabels.get(i);
  // NewNodeSummary node = sampleToBucket(s, label);
  // if (summaries.containsKey(node)) {
  // summaries.get(node).add(s);
  // node = sampleToSummary.get(summaries.get(node).get(0));// for ids
  // } else {
  // summaries.put(node, new ArrayList<Sample>(Arrays.asList(s)));
  // node.setID(ids[i]);
  // order.put(ids[i], node);
  // ids[i] = ids[i] + 1;
  // }
  // sampleToSummaryList.get(i).put(s, node);
  //
  // }
  // }
  // createSummariesRecursive2(nextLvl, sampleToSummaryList, summariesList, factorLabels, ids,
  // orderList);
  // }
  // // if (!nextLvl.isEmpty())
  // // createSummariesRecursive(nextLvl, sampleToSummary, summaries, label, id, order);
  // }
  //
  // private void createSummariesRecursive(List<Sample> roots,
  // Map<Sample, NewNodeSummary> sampleToSummary, Map<NewNodeSummary, List<Sample>> summaries,
  // String label, int[] id, Map<Integer, NewNodeSummary> order) throws JAXBException {
  // // System.out.println("recurse");
  // // List<Sample> nextLvl = new ArrayList<Sample>();
  // for (Sample s : roots) {
  // String type = s.getSampleTypeCode();
  // List<Sample> nextLvl = new ArrayList<Sample>();
  // // if sample type should be shown in graph...TODO intermediate samples should never be
  // // hidden for obvious reasons
  // if (validSamples.contains(type)) {
  // nextLvl.addAll(s.getChildren());
  // // fill maps
  // NewNodeSummary node = sampleToBucket(s, label);
  // if (summaries.containsKey(node)) {
  // summaries.get(node).add(s);
  // node = sampleToSummary.get(summaries.get(node).get(0));// for ids
  // } else {
  // summaries.put(node, new ArrayList<Sample>(Arrays.asList(s)));
  // node.setID(id[0]);
  // order.put(id[0], node);
  // id[0] = id[0] + 1;
  // }
  // sampleToSummary.put(s, node);
  // }
  // createSummariesRecursive(nextLvl, sampleToSummary, summaries, label, id, order);
  // }
  // // if (!nextLvl.isEmpty())
  // // createSummariesRecursive(nextLvl, sampleToSummary, summaries, label, id, order);
  // }
  //
  // // create list of real nodes for the graph
  // private List<SampleSummary> convertNodeSummariesToSampleSummaries(
  // Map<NewNodeSummary, List<Sample>> summaries, Map<Sample, NewNodeSummary> sampleToSummary,
  // Map<Integer, NewNodeSummary> order) {
  // List<SampleSummary> res = new ArrayList<SampleSummary>();
  // for (int i = 0; i < order.size(); i++) {
  // NewNodeSummary node = order.get(i);
  // Set<NewNodeSummary> children = node.getChildren();
  // SampleSummary sum =
  // new SampleSummary(node.getID(), summaries.get(node), node.getName(), children.isEmpty());
  // List<Integer> childIDs = new ArrayList<Integer>();
  // for (NewNodeSummary childNode : children) {
  // childIDs.add(childNode.getID());
  // }
  // sum.setChildIDs(childIDs);
  // res.add(sum);
  // }
  // return res;
  // }

  private Property getFactorOfSampleOrNull(Sample s, String factorLabel) throws JAXBException {
    Map<String, String> props = s.getProperties();
    List<Property> factors = new ArrayList<Property>();
    if (props.containsKey("Q_PROPERTIES"))
      factors = xmlParser.getAllProperties(xmlParser.parseXMLString(props.get("Q_PROPERTIES")));// TODO
                                                                                                // only
                                                                                                // factors?
    for (Property f : factors) {
      if (f.getLabel().equals(factorLabel))
        return f;
    }
    return null;
  }

  public StructuredExperiment parseSamplesBreadthFirst(List<Sample> samples, List<DataSet> datasets)
      throws JAXBException {
    sampCodeToDS = new HashMap<String, List<DataSet>>();
    codesWithDatasets = new HashSet<String>();
    for (DataSet d : datasets) {
      String code = d.getSampleIdentifierOrNull().split("/")[2];
      if (sampCodeToDS.containsKey(code))
        sampCodeToDS.get(code).add(d);
      else
        sampCodeToDS.put(code, new ArrayList<DataSet>(Arrays.asList(d)));
    }

    this.xmlParser = new XMLParser();
    Map<String, List<SampleSummary>> factorsToSamples = new HashMap<String, List<SampleSummary>>();
    Set<String> knownFactors = new HashSet<String>();
    sampCodeToSamp = new HashMap<String, Sample>();
    knownFactors.add("None");

    Queue<Sample> samplesBreadthFirst = new LinkedList<Sample>();
    Set<Sample> visited = new HashSet<Sample>();
    // init
    for (Sample s : samples) {
      sampCodeToSamp.put(s.getCode(), s);
      String type = s.getSampleTypeCode();
      if (validSamples.contains(type)) {
        Map<String, String> p = s.getProperties();
        List<Property> factors = new ArrayList<Property>();
        if (p.containsKey("Q_PROPERTIES")) {
          factors = xmlParser.getAllProperties(xmlParser.parseXMLString(p.get("Q_PROPERTIES")));// TODO
                                                                                                // all
                                                                                                // props?
        }
        for (Property f : factors)
          knownFactors.add(f.getLabel());
        // collect roots
        if (s.getParents().isEmpty()) {
          samplesBreadthFirst.add(s);
        }
      }
    }
    // TODO maybe fill stack (then copy stack) and map to parents outside this loop
    Map<String, Integer> idCounterPerLabel = new HashMap<String, Integer>();
    Map<String, Map<Sample, Set<SampleSummary>>> sampleToParentNodesPerLabel =
        new HashMap<String, Map<Sample, Set<SampleSummary>>>();
    Map<String, Set<SampleSummary>> nodesForFactorPerLabel =
        new HashMap<String, Set<SampleSummary>>();
    for (String label : knownFactors) {
      idCounterPerLabel.put(label, 1);
      sampleToParentNodesPerLabel.put(label, new HashMap<Sample, Set<SampleSummary>>());
      nodesForFactorPerLabel.put(label, new LinkedHashSet<SampleSummary>());
    }

    // Set<SampleSummary> nodesForFactor = new HashSet<SampleSummary>();
    // Map<Sample, Set<SampleSummary>> sampleToParentNodes =
    // new HashMap<Sample, Set<SampleSummary>>();
    // breadth first stack loop
    while (!samplesBreadthFirst.isEmpty()) {
      Sample s = samplesBreadthFirst.poll();
      // System.out.println(nodesForFactor);
      String type = s.getSampleTypeCode();
      if (validSamples.contains(type) && !visited.contains(s)) {
        visited.add(s);
        List<Sample> children = s.getChildren();

        for (String label : knownFactors) {
          // compute new summary
          Map<Sample, Set<SampleSummary>> sampleToParentNodes =
              sampleToParentNodesPerLabel.get(label);
          Set<SampleSummary> parentSummaries = sampleToParentNodes.get(s);
          if (parentSummaries == null)
            parentSummaries = new LinkedHashSet<SampleSummary>();
          SampleSummary node =
              createSummary(s, parentSummaries, label, idCounterPerLabel.get(label));
          // check for hashcode and add current sample s if node exists
          boolean exists = false;
          for (SampleSummary oldNode : nodesForFactorPerLabel.get(label)) {
            if (oldNode.equals(node)) {
              oldNode.addSample(s);
              exists = true;
              node = oldNode;
            }
          }
          if (!exists)
            idCounterPerLabel.put(label, idCounterPerLabel.get(label) + 1);
          // idCounter++;
          // adds node if not already contained in set
          Set<SampleSummary> theseNodes = nodesForFactorPerLabel.get(label);
          theseNodes.add(node);
          nodesForFactorPerLabel.put(label, theseNodes);
          // add this id to parents' child ids
          for (SampleSummary parentSummary : parentSummaries) {
            // System.out.println("adding " + node.getId() + " as child of " + parentSummary);
            parentSummary.addChildID(node.getId());
            // System.out.println(parentSummary);
          }
          // System.out.println(node);
          for (Sample c : children) {
            samplesBreadthFirst.add(c);
            if (!sampleToParentNodes.containsKey(c)) {
              sampleToParentNodes.put(c, new LinkedHashSet<SampleSummary>());
            }
            sampleToParentNodes.get(c).add(node);
            sampleToParentNodesPerLabel.put(label, sampleToParentNodes);
          }
          // }
        }
      }
      // factorsToSamples.put(label, new ArrayList<SampleSummary>(nodesForFactor));
    }
    for (String label : nodesForFactorPerLabel.keySet()) {
      Set<SampleSummary> nodes = nodesForFactorPerLabel.get(label);
      addDataSetCount(nodes);
      factorsToSamples.put(label, new ArrayList<SampleSummary>(nodes));
    }
    return new StructuredExperiment(factorsToSamples);
  }

  // new "sample to bucket" function, creates new summaries from sample metadata in reference to
  // parent summaries and experimental factor
  private SampleSummary createSummary(Sample s, Set<SampleSummary> parents, String label,
      int currentID) throws JAXBException {
    // name: should be the visible discriminating factor between nodes
    // 1. contains the source, if the source is not the selected factor (e.g. tissues)
    // 2. contains the selected factor's value, except
    // a) if parent sample has the same factor value
    // b) if it has no factor
    // factor: the current selected factor object. If none exists, parents' sources are used.

    // the name alone is not enough to discriminate between different nodes! (e.g. different parent
    // nodes, same child node name)
    String type = s.getSampleTypeCode();
    String source = "unknown";
    Property factor = getFactorOfSampleOrNull(s, label);
    boolean newFactor = true;
    Set<String> parentSources = new HashSet<String>();
    Set<Integer> parentIDs = new HashSet<Integer>();
    for (SampleSummary parentSum : parents) {
      parentIDs.add(parentSum.getId());
      String factorVal = parentSum.getFactorValue();
      if (factorVal != null && !factorVal.isEmpty()) {
        newFactor = false;
      }
      parentSources.add(parentSum.getSource());
    }
    if (factor == null) {
      factor = new Property("parents", StringUtils.join(parentSources, "+"), PropertyType.Factor);// TODO
                                                                                                  // makes
                                                                                                  // sense?
      newFactor = false;
    }
    String value = "";
    if (newFactor)
      value = factor.getValue();
    // while (!parents.isEmpty()) {
    // depth += 1;
    // List<Sample> gramps = new ArrayList<Sample>();
    // for (Sample p : parents) {
    // gramps.addAll(p.getParents());
    // }
    // parents = gramps;
    // }
    Map<String, String> props = s.getProperties();
    switch (type) {
      case "Q_BIOLOGICAL_ENTITY":
        source = taxMap.get(props.get("Q_NCBI_ORGANISM"));
        value = source + " " + value;
        break;
      case "Q_BIOLOGICAL_SAMPLE":
        source = tissueMap.get(props.get("Q_PRIMARY_TISSUE"));
        boolean isCellLine = source.equals("Cell Line");
        if (source.equals("Other") || isCellLine) {
          String detail = props.get("Q_TISSUE_DETAILED");
          if (detail != null && !detail.isEmpty()) {
            source = detail;
            // if (isCellLine)
            // source = "Cell Line " + source;
          }
        }
        if (!newFactor || source.equals(value)) {
          value = source;// test
        } else {
          value = source + " " + value;
        }
        break;
      case "Q_TEST_SAMPLE":
        source = props.get("Q_SAMPLE_TYPE");
        value = source + " " + value;
        break;
      case "Q_MHC_LIGAND_EXTRACT":
        source = props.get("Q_MHC_CLASS");
        value = source;
        break;
    }
    // String code = s.getCode();
    // int measured = 0;
    // if (sampCodeToDS.containsKey(code))
    // measured = sampCodeToDS.get(code).size();
    // System.out.println(type);
    // System.out.println(measured);// TODO connect to datasets
    return new SampleSummary(currentID, parentIDs, new ArrayList<Sample>(Arrays.asList(s)), factor.getValue(),
        tryShortenName(value, s), type, s.getChildren().isEmpty());
  }
  //
  //
  // private NewNodeSummary sampleToBucket(Sample s, String factorLabel) throws JAXBException {
  // String type = s.getSampleTypeCode();
  // String source = "unknown";
  // Factor factor = getFactorOfSampleOrNull(s, factorLabel);
  // int depth = 0;
  // boolean factorInherited = false;
  // List<Sample> parents = s.getParents();
  // Set<String> parentSources = new HashSet<String>();
  // for (Sample p : parents) {
  // if (getFactorOfSampleOrNull(p, factorLabel) != null) {
  // factorInherited = true;
  // }
  // parentSources.add(sampleToBucket(p, factorLabel).getSource());
  // }
  // if (factor == null) {
  // factor = new Factor("parents", StringUtils.join(parentSources, "+"));
  // }
  // while (!parents.isEmpty()) {
  // depth += 1;
  // List<Sample> gramps = new ArrayList<Sample>();
  // for (Sample p : parents) {
  // gramps.addAll(p.getParents());
  // }
  // parents = gramps;
  // }
  // Map<String, String> props = s.getProperties();
  // String name = factor.getValue();
  // switch (type) {
  // case "Q_BIOLOGICAL_ENTITY":
  // source = taxMap.get(props.get("Q_NCBI_ORGANISM"));
  // name = source + " " + name;
  // break;
  // case "Q_BIOLOGICAL_SAMPLE":
  // source = tissueMap.get(props.get("Q_PRIMARY_TISSUE"));
  // boolean isCellLine = source.equals("Cell Line");
  // if (source.equals("Other") || isCellLine) {
  // String detail = props.get("Q_TISSUE_DETAILED");
  // if (detail != null && !detail.isEmpty()) {
  // source = detail;
  // if (isCellLine)
  // source = "Cell Line " + source;
  // }
  // }
  // if (factorInherited || source.equals(name))
  // name = source;// test
  // else {
  // name = source + " " + name;
  // }
  // break;
  // case "Q_TEST_SAMPLE":
  // source = props.get("Q_SAMPLE_TYPE");
  // name = source + factor.getValue();
  // break;
  // case "Q_MHC_LIGAND_EXTRACT":
  // source = props.get("Q_MHC_CLASS");
  // name = source;
  // break;
  // }
  // return new NewNodeSummary(source, factor, depth, tryShortenName(name, s));
  // }

  private String tryShortenName(String key, Sample s) {
    switch (s.getSampleTypeCode()) {
      case "Q_BIOLOGICAL_ENTITY":
        return key;
      case "Q_BIOLOGICAL_SAMPLE":
        return key;
      case "Q_TEST_SAMPLE":
        String type = s.getProperties().get("Q_SAMPLE_TYPE");
        return key.replace(type, "") + " " + shortenInfo(type);
      case "Q_MHC_LIGAND_EXTRACT":
        return s.getProperties().get("Q_MHC_CLASS").replace("_", " ").replace("CLASS", "Class");
    }
    return key;
  }

  private String shortenInfo(String info) {
    switch (info) {
      case "CARBOHYDRATES":
        return "Carbohydrates";
      case "SMALLMOLECULES":
        return "Smallmolecules";
      case "DNA":
        return "DNA";
      case "RNA":
        return "RNA";
      default:
        return WordUtils.capitalizeFully(info.replace("_", " "));
    }
  }

  public Sample getSampleFromCode(String code) {
    return sampCodeToSamp.get(code);
  }

  public List<DataSet> getDatasetsOfCode(String code) {
    return sampCodeToDS.get(code);
  }

  public boolean codeHasDatasets(String code) {
    return codesWithDatasets.contains(code);
  }

}
