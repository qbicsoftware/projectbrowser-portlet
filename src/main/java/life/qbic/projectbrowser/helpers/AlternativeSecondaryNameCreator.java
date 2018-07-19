package life.qbic.projectbrowser.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import com.vaadin.data.util.BeanItemContainer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import life.qbic.projectbrowser.model.SampleBean;

public class AlternativeSecondaryNameCreator {

  private static final Logger LOG = LogManager.getLogger(AlternativeSecondaryNameCreator.class);
  Map<String, String> taxIdToName;

  public AlternativeSecondaryNameCreator(Map<String, String> taxMap) {
    this.taxIdToName = new HashMap<String, String>();
    for (Map.Entry<String, String> entry : taxMap.entrySet())
      taxIdToName.put(entry.getValue(), entry.getKey());
  }

  public String createName(Map<String, String> properties, String type,
      BeanItemContainer<SampleBean> samples) {
    String result = "";
    int MAX_PARSE_SIZE = 200; // save time in large projects
    String name = properties.get("Q_SECONDARY_NAME");
    if (name != null && !name.isEmpty())
      return name;
    List<SampleBean> beans = samples.getItemIds();
    switch (type) {
      case "Q_NGS_MEASUREMENT":
        String device = properties.get("Q_SEQUENCER_DEVICE");
        if (device != null && !device.isEmpty())
          result = device + " measurement";
        else
          return "";
        break;
      case "Q_EXPERIMENTAL_DESIGN":
        String res = "";
        if (beans.size() < MAX_PARSE_SIZE) {
          for (SampleBean s : beans) {
            String species = taxIdToName.get(s.getProperties().get("Q_NCBI_ORGANISM"));
            if (!res.isEmpty()) {
              if (!res.equals(species))
                return "Organism information";
            } else
              res = species;
          }
        }
        if (res.isEmpty())
          return res;
        else
          result = res + " information";
        break;
      case "Q_SAMPLE_EXTRACTION":
        res = "";
        if (beans.size() < MAX_PARSE_SIZE) {
          for (SampleBean s : beans) {
            String tissue = s.getProperties().get("Q_PRIMARY_TISSUE");
            String other = s.getProperties().get("Q_TISSUE_DETAILED");
            if (tissue.equals("CELL_CULTURE") || tissue.equals("OTHER")) {
              tissue = other;
              if (other == null)
                tissue = "";
            }
            if (!res.isEmpty()) {
              if (!res.equals(tissue))
                return "Extract information";
            } else
              res = tissue;
          }
        }
        if (res.isEmpty())
          return "Extract information";
        else
          result = res + " information";
        break;
      case "Q_SAMPLE_PREPARATION":
        if (beans.isEmpty())
          return "No Preparations";
        else
          result = beans.get(0).getProperties().get("Q_SAMPLE_TYPE") + " preparation";
        break;
      case "Q_PROJECT_DETAILS":
        return "Attachments and Details";
      case "Q_EXT_MS_QUALITYCONTROL":
        return "";
      case "Q_EXT_NGS_QUALITYCONTROL":
        return "";
      case "Q_MICROARRAY_MEASUREMENT":
        return "";
      case "Q_MS_MEASUREMENT":
        device = properties.get("Q_MS_DEVICE");
        if (device != null && !device.isEmpty())
          result = device + " measurement";
        else
          return "";
        break;
      case "Q_NGS_EPITOPE_PREDICTION":
      case "Q_NGS_FLOWCELL_RUN":
      case "Q_NGS_HLATYPING":
      case "Q_NGS_IMMUNE_MONITORING":
      case "Q_NGS_MAPPING":
      case "Q_NGS_SINGLE_SAMPLE_RUN":
      case "Q_NGS_VARIANT_CALLING":
      case "Q_WF_MA_QUALITYCONTROL":
      case "Q_WF_MS_MAXQUANT":
      case "Q_WF_MS_PEPTIDEID":
      case "Q_WF_MS_QUALITYCONTROL":
      case "Q_WF_NGS_EPITOPE_PREDICTION":
      case "Q_WF_NGS_HLATYPING":
      case "Q_WF_NGS_MERGE":
      case "Q_WF_NGS_QUALITYCONTROL":
      case "Q_WF_NGS_RNA_EXPRESSION_ANALYSIS":
      case "Q_WF_NGS_VARIANT_ANNOTATION":
      case "Q_WF_NGS_VARIANT_CALLING":
      case "Q_WF_NGS_MAPPING":
      case "Q_WF_MS_INDIVIDUALIZED_PROTEOME":
      case "Q_WF_MS_LIGANDOMICS_ID":
      case "Q_WF_MS_LIGANDOMICS_QC":
      case "Q_MHC_LIGAND_EXTRACTION":
      default:
        return "";
    }
    if (result.contains("RNA") || result.contains("DNA"))
      return result;
    else
      return WordUtils.capitalizeFully(result.replace("_", " "));
  }

}
