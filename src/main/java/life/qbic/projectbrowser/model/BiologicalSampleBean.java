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
package life.qbic.projectbrowser.model;

import life.qbic.projectbrowser.helpers.UglyToPrettyNameMapper;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import life.qbic.xml.manager.XMLParser;
import life.qbic.xml.properties.Qproperties;

public class BiologicalSampleBean implements Comparable<Object>, Serializable {

  /**
     * 
     */
  private static final long serialVersionUID = -61486023394904818L;
  private String id;
  private String code;
  private String type;

  private String biologicalEntity;

  private String primaryTissue;
  private String tissueDetailed;

  private String secondaryName;
  private String additionalInfo;
  private String externalDB;

  private String properties;

  private UglyToPrettyNameMapper prettyNameMapper = new UglyToPrettyNameMapper();



  public BiologicalSampleBean(String id, String code, String type, String biologicalEntity,
      String primaryTissue, String tissueDetailed, String secondaryName, String additionalInfo,
      String externalDB, String properties) {
    super();
    this.id = id;
    this.code = code;
    this.type = type;
    this.setBiologicalEntity(biologicalEntity);
    this.setPrimaryTissue(primaryTissue);
    this.setTissueDetailed(tissueDetailed);
    this.secondaryName = secondaryName;
    this.additionalInfo = additionalInfo;
    this.externalDB = externalDB;
    this.properties = properties;

  }

  public BiologicalSampleBean() {

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
  }



  @Override
  public int compareTo(Object o) {
    return id.compareTo(((SampleBean) o).getId());
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof SampleBean) {
      SampleBean b = (SampleBean) o;
      return id.equals(b.getId());
    } else
      return false;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  public String getSecondaryName() {
    return secondaryName;
  }

  public void setSecondaryName(String secondaryName) {
    this.secondaryName = secondaryName;
  }

  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  public String getExternalDB() {
    return externalDB;
  }

  public void setExternalDB(String externalDB) {
    this.externalDB = externalDB;
  }


  public String getProperties() {
    return properties;
  }

  public void setProperties(Map<String, String> properties) {
    try {
      this.properties = generateXMLPropertiesFormattedString(properties);
    } catch (JAXBException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }


  public String generateXMLPropertiesFormattedString(Map<String, String> properties)
      throws JAXBException {

    String xmlPropertiesString = "";
    Iterator<Entry<String, String>> it = properties.entrySet().iterator();
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

          xmlPropertiesString += pairsProperties.getKey() + ": " + pairsProperties.getValue() + " ";
        }
        break;
      }
    }
    return xmlPropertiesString;
  }

  public String getPrimaryTissue() {
    return primaryTissue;
  }

  public void setPrimaryTissue(String primaryTissue) {
    this.primaryTissue = primaryTissue;
  }

  public String getTissueDetailed() {
    return tissueDetailed;
  }

  public void setTissueDetailed(String tissueDetailed) {
    this.tissueDetailed = tissueDetailed;
  }

  public String getBiologicalEntity() {
    return biologicalEntity;
  }

  public void setBiologicalEntity(String biologicalEntity) {
    this.biologicalEntity = biologicalEntity;
  }

}
