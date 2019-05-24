package life.qbic.projectbrowser.model;

import java.util.Map;

import de.uni_tuebingen.qbic.beans.DatasetBean;

/**
 * Used to fix Vaadin Grid Filter bug without touching guse workflow project code
 * 
 * @author Andreas Friedrich
 *
 */
public class DatasetBeanWithInfos extends DatasetBean {

  private String additionalInfo;

  public DatasetBeanWithInfos(String fileName, String fileType, String openbisCode, String fullPath,
      String sampleIdentifier, Map<String, String> properties) {
    super(fileName, fileType, openbisCode, fullPath, sampleIdentifier);
    if (properties != null) {
      setProperties(properties);
      if (properties.containsKey("Q_ADDITIONAL_INFO")) {
        additionalInfo = properties.get("Q_ADDITIONAL_INFO");
      }
    }
  }

  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

}
