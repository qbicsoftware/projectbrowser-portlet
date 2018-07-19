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
package life.qbic.projectbrowser.model.maxquant;

import java.io.Serializable;

public class RawFilesBean implements Serializable {
  private static final long serialVersionUID = 3975747833269788515L;

  private String file;
  private Integer parameterGroup;
  private Integer fraction;
  private String experiment;

  public RawFilesBean(String file, Integer parameterGroup, Integer fraction, String experiment) {
    super();
    this.file = file;
    this.parameterGroup = parameterGroup;
    this.fraction = fraction;
    this.experiment = experiment;
  }

  /**
   * will set fields accordingly. fraction and parameter group will both be set to 1 (standard).
   * 
   * @param file
   * @param experiment
   */
  public RawFilesBean(String file, String experiment) {
    super();
    this.file = file;
    this.parameterGroup = 1;
    this.fraction = 1;
    this.experiment = experiment;
  }


  public String getFile() {
    return file;
  }

  public String generateParamFile() {
    return file.split("\\.")[0];
  }

  public void setFile(String file) {
    this.file = file;
  }


  public Integer getParameterGroup() {
    return parameterGroup;
  }


  public void setParameterGroup(Integer parameterGroup) {
    this.parameterGroup = parameterGroup;
  }


  public Integer getFraction() {
    return fraction;
  }


  public void setFraction(Integer fraction) {
    this.fraction = fraction;
  }


  public String getExperiment() {
    return experiment;
  }


  public void setExperiment(String experiment) {
    this.experiment = experiment;
  }

  @Override
  public String toString() {
    return "RawFilesBean [file=" + file + ", parameterGroup=" + parameterGroup + ", fraction="
        + fraction + ", experiment=" + experiment + "]";
  }

  /*
   * uncommented because it causes java.lang.IllegalArgumentException: Given item id (RawFilesBean
   * [file=abc, parameterGroup=1, fraction=3, experiment=qbic1233]) does not exist in the container
   * if one tries to edit values in a grid
   * 
   * @Override public int hashCode() { final int prime = 31; int result = 1; result = prime * result
   * + ((experiment == null) ? 0 : experiment.hashCode()); result = prime * result + ((file == null)
   * ? 0 : file.hashCode()); result = prime * result + ((fraction == null) ? 0 :
   * fraction.hashCode()); result = prime * result + ((parameterGroup == null) ? 0 :
   * parameterGroup.hashCode()); return result; }
   */

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RawFilesBean other = (RawFilesBean) obj;
    if (experiment == null) {
      if (other.experiment != null)
        return false;
    } else if (!experiment.equals(other.experiment))
      return false;
    if (file == null) {
      if (other.file != null)
        return false;
    } else if (!file.equals(other.file))
      return false;
    if (fraction == null) {
      if (other.fraction != null)
        return false;
    } else if (!fraction.equals(other.fraction))
      return false;
    if (parameterGroup == null) {
      if (other.parameterGroup != null)
        return false;
    } else if (!parameterGroup.equals(other.parameterGroup))
      return false;
    return true;
  }

}
