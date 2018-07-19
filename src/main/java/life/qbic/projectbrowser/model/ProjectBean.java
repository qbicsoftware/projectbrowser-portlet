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

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ProgressBar;

public class ProjectBean implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -4463436578711695346L;
  private String id;
  private String secondaryName;
  private String code;
  private String description;
  private String longDescription;
  private String space;
  private BeanItemContainer<ExperimentBean> experiments;
  private ProgressBar progress;
  private Date registrationDate;
  private String registrator;
  private String contact;
  private String principalInvestigator;
  private String contactPerson;
  private String projectManager;
  private Set<String> members;
  private Boolean containsData;
  private Boolean containsResults;
  private Boolean containsAttachments;

  public ProjectBean(String id, String code, String secondaryName, String description, String space,
      BeanItemContainer<ExperimentBean> experiments, ProgressBar progress, Date registrationDate,
      String registrator, String contact, Set<String> members, Boolean containsData,
      Boolean containsResults, Boolean containsAttachments, String projectManager) {
    super();
    this.id = id;
    this.code = code;
    this.secondaryName = secondaryName;
    this.description = description;
    this.space = space;
    this.experiments = experiments;
    this.progress = progress;
    this.registrationDate = registrationDate;
    this.registrator = registrator;
    this.contact = contact;
    this.members = members;
    this.setContainsData(containsData);
    this.setContainsResults(containsResults);
    this.setContainsAttachments(containsAttachments);
  }

  public ProjectBean() {
    // TODO Auto-generated constructor stub
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

  public void setSecondaryName(String secondaryName) {
    this.secondaryName = secondaryName;
  }

  public String getSecondaryName() {
    return secondaryName;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getSpace() {
    return space;
  }

  public void setSpace(String space) {
    this.space = space;
  }

  public BeanItemContainer<ExperimentBean> getExperiments() {
    return experiments;
  }

  public void setExperiments(BeanItemContainer<ExperimentBean> experiments) {
    this.experiments = experiments;
  }

  public ProgressBar getProgress() {
    return progress;
  }

  public void setProgress(ProgressBar progress) {
    this.progress = progress;
  }

  public Date getRegistrationDate() {
    return registrationDate;
  }

  public void setRegistrationDate(Date registrationDate) {
    this.registrationDate = registrationDate;
  }

  public String getRegistrator() {
    return registrator;
  }

  public void setRegistrator(String registrator) {
    this.registrator = registrator;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public Set<String> getMembers() {
    return members;
  }

  public void setMembers(Set<String> members) {
    this.members = members;
  }

  @Override
  public String toString() {
    return "ProjectBean [id=" + id + "]";
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
    ProjectBean other = (ProjectBean) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  public Boolean getContainsData() {
    return containsData;
  }

  public Boolean getContainsResults() {
    return containsResults;
  }

  public Boolean getContainsAttachments() {
    return containsAttachments;
  }

  public void setContainsData(Boolean containsData) {
    this.containsData = containsData;
  }

  public String getPrincipalInvestigator() {
    return principalInvestigator;
  }

  public void setPrincipalInvestigator(String principalInvestigator) {
    this.principalInvestigator = principalInvestigator;
  }

  public String getContactPerson() {
    return contactPerson;
  }

  public void setContactPerson(String contactPerson) {
    this.contactPerson = contactPerson;
  }

  public String getProjectManager() {
    return this.projectManager;
  }

  public void setProjectManager(String manager) {
    this.projectManager = manager;
  }

  public void setLongDescription(String longDesc) {
    this.longDescription = longDesc;
  }

  public String getLongDescription() {
    return longDescription;
  }

  public void setContainsResults(Boolean containsResults) {
    this.containsResults = containsResults;
  }

  public void setContainsAttachments(Boolean containsAttachment) {
    this.containsAttachments = containsAttachment;
  }
}
