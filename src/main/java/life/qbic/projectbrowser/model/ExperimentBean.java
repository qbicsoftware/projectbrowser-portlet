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
package life.qbic.projectbrowser.model;

import life.qbic.projectbrowser.helpers.AlternativeSecondaryNameCreator;
import life.qbic.projectbrowser.helpers.UglyToPrettyNameMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import life.qbic.xml.manager.XMLParser;
import life.qbic.xml.properties.Qproperties;

import com.vaadin.data.util.BeanItemContainer;

public class ExperimentBean implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -1927159369993633824L;
  private static final Logger LOG = LogManager.getLogger(ExperimentBean.class);
  private String id;
  private String code;
  private String secondaryName;
  private String type;
  private String prettyType;
  private String status;
  private String registrator;
  private Date registrationDate;
  private BeanItemContainer<SampleBean> samples;
  private String lastChangedSample;
  private Date lastChangedDataset;
  private Map<String, String> properties;
  private Map<String, List<String>> controlledVocabularies;
  private Map<String, String> typeLabels;
  private Boolean containsData;

  private UglyToPrettyNameMapper prettyNameMapper = new UglyToPrettyNameMapper();
  private AlternativeSecondaryNameCreator nameCreator;

  public Map<String, String> getTypeLabels() {
    return typeLabels;
  }



  public void setTypeLabels(Map<String, String> typeLabels) {
    this.typeLabels = typeLabels;
  }



  public ExperimentBean(String id, String code, String type, String status, String registrator,
      Date registrationDate, BeanItemContainer<SampleBean> samples, String lastChangedSample,
      Date lastChangedDataset, Map<String, String> properties,
      Map<String, List<String>> controlledVocabularies, Map<String, String> typeLabels) {
    super();
    this.id = id;
    this.code = code;
    this.secondaryName = nameCreator.createName(properties, type, samples);
    this.type = type;
    this.prettyType = prettyNameMapper.getPrettyName(type);
    this.type = type;
    this.status = status;
    this.registrator = registrator;
    this.registrationDate = registrationDate;
    this.samples = samples;
    this.lastChangedSample = lastChangedSample;
    this.lastChangedDataset = lastChangedDataset;
    this.properties = properties;
    this.controlledVocabularies = controlledVocabularies;
    this.typeLabels = typeLabels;
  }

  public ExperimentBean() {
    // TODO Auto-generated constructor stub
  }

  public String getSecondaryName() {
    if (secondaryName == null || secondaryName.isEmpty()) {
      secondaryName = nameCreator.createName(properties, type, samples);
    }
    return secondaryName;
  }

  public String getId() {
    return id;
  }



  public void setId(String id) {
    this.id = id;
  }



  public String getCode() {
    return code;
  }



  public void setCode(String code) {
    this.code = code;
  }



  public String getType() {
    return type;
  }



  public void setType(String type) {
    this.type = type;
    this.prettyType = prettyNameMapper.getPrettyName(type);
  }



  public String getStatus() {
    return status;
  }



  public void setStatus(String status) {
    this.status = status;
  }



  public String getRegistrator() {
    return registrator;
  }


  public void setRegistrator(String registrator) {
    this.registrator = registrator;
  }



  public Date getRegistrationDate() {
    return registrationDate;
  }



  public void setRegistrationDate(Date registrationDate) {
    this.registrationDate = registrationDate;
  }



  public BeanItemContainer<SampleBean> getSamples() {
    return samples;
  }



  public void setSamples(BeanItemContainer<SampleBean> samples) {
    this.samples = samples;
  }



  public String getLastChangedSample() {
    return lastChangedSample;
  }



  public void setLastChangedSample(String lastChangedSample) {
    this.lastChangedSample = lastChangedSample;
  }



  public Date getLastChangedDataset() {
    return lastChangedDataset;
  }



  public void setLastChangedDataset(Date lastChangedDataset) {
    this.lastChangedDataset = lastChangedDataset;
  }



  public Map<String, String> getProperties() {
    return properties;
  }



  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
  }



  public Map<String, List<String>> getControlledVocabularies() {
    return controlledVocabularies;
  }



  public void setControlledVocabularies(Map<String, List<String>> controlledVocabularies) {
    this.controlledVocabularies = controlledVocabularies;
  }



  @Override
  public String toString() {
    return "ExperimentBean [id=" + id + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ExperimentBean other = (ExperimentBean) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  public String generatePropertiesFormattedString() throws JAXBException {
    String propertiesBottom = "<ul> ";

    Iterator<Entry<String, String>> it = this.getProperties().entrySet().iterator();
    while (it.hasNext()) {
      Entry pairs = (Entry) it.next();
      if (pairs.getKey().equals("Q_PROPERTIES") || pairs.getKey().equals("Q_NOTES")) {
        continue;
      } else {
        propertiesBottom +=
            "<li><b>" + (typeLabels.get(pairs.getKey()) + ":</b> " + pairs.getValue() + "</li>");
      }
    }
    propertiesBottom += "</ul>";

    return propertiesBottom;
  }

  public String generateXMLPropertiesFormattedString() throws JAXBException {

    String xmlPropertiesBottom = "<ul> ";

    Iterator<Entry<String, String>> it = this.getProperties().entrySet().iterator();
    while (it.hasNext()) {
      Entry pairs = (Entry) it.next();
      if (pairs.getKey().equals("Q_PROPERTIES")) {
        XMLParser xmlParser = new XMLParser();
        JAXBElement<Qproperties> xmlProperties =
            xmlParser.parseXMLString(pairs.getValue().toString());
        Map<String, String> xmlPropertiesMap = xmlParser.getMapOfProperties(xmlProperties);

        Iterator itProperties = xmlPropertiesMap.entrySet().iterator();
        while (itProperties.hasNext()) {
          Entry pairsProperties = (Entry) itProperties.next();

          xmlPropertiesBottom += "<li><b>"
              // + (typeLabels.get(pairsProperties.getKey()) + ":</b> "
              + (pairsProperties.getKey() + ":</b> " + pairsProperties.getValue() + "</li>");
        }
        break;
      }
    }
    return xmlPropertiesBottom;
  }



  public Boolean getContainsData() {
    return containsData;
  }

  public void setContainsData(Boolean containsData) {
    this.containsData = containsData;
  }

  public String getPrettyType() {
    return prettyType;
  }

  public void setPrettyType(String prettyType) {
    this.prettyType = prettyType;
  }

  public void setSecondaryName(String name) {
    this.secondaryName = name;
  }

  public void setAltNameCreator(AlternativeSecondaryNameCreator altNameCreator) {
    nameCreator = altNameCreator;
  }

}
