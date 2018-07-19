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

import java.io.Serializable;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ProgressBar;


public class SpaceBean implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 5127186974184020095L;
  private String id;
  private String description;
  private Boolean containsData;
  private BeanItemContainer<ProjectBean> projects;
  private BeanItemContainer<ExperimentBean> experiments;
  private BeanItemContainer<SampleBean> samples;
  private BeanItemContainer<DatasetBean> datasets;
  // TODO bean ?
  private List<String> members;
  private ProgressBar progress;

  // TODO statistics ? LastChanged?

  public SpaceBean(String id, String description, Boolean containsData,
      BeanItemContainer<ProjectBean> projects, BeanItemContainer<ExperimentBean> experiments,
      BeanItemContainer<SampleBean> samples, BeanItemContainer<DatasetBean> datasets,
      List<String> members, ProgressBar progress) {
    super();
    this.id = id;
    this.description = description;
    this.containsData = containsData;
    this.projects = projects;
    this.experiments = experiments;
    this.samples = samples;
    this.datasets = datasets;
    this.setMembers(members);
    this.setProgress(progress);
  }

  public SpaceBean() {}

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getContainsData() {
    return containsData;
  }

  public void setContainsData(Boolean containsData) {
    this.containsData = containsData;
  }

  public BeanItemContainer<ProjectBean> getProjects() {
    return projects;
  }

  public void setProjects(BeanItemContainer<ProjectBean> projects) {
    this.projects = projects;
  }

  public BeanItemContainer<ExperimentBean> getExperiments() {
    return experiments;
  }

  public void setExperiments(BeanItemContainer<ExperimentBean> experiments) {
    this.experiments = experiments;
  }

  public BeanItemContainer<SampleBean> getSamples() {
    return samples;
  }

  public void setSamples(BeanItemContainer<SampleBean> samples) {
    this.samples = samples;
  }

  public BeanItemContainer<DatasetBean> getDatasets() {
    return datasets;
  }

  public void setDatasets(BeanItemContainer<DatasetBean> datasets) {
    this.datasets = datasets;
  }

  @Override
  public String toString() {
    return "SpaceBean [id=" + id + "]";
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
    SpaceBean other = (SpaceBean) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  public List<String> getMembers() {
    return members;
  }

  public void setMembers(List<String> members) {
    this.members = members;
  }

  public ProgressBar getProgress() {
    return progress;
  }

  public void setProgress(ProgressBar progress) {
    this.progress = progress;
  }

}
