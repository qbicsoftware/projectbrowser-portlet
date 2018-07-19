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

import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Project;

public class SearchResultsProjectBean implements Comparable<Object>, Serializable {


  /**
   * 
   */
  private static final long serialVersionUID = -5213168232951534848L;
  private String projectID;
  private String description;
  private String queryString;



  public SearchResultsProjectBean(Project p, String query) {
    projectID = p.getIdentifier();
    description = p.getDescription();
    queryString = query;
  }


  public String getProjectID() {
    return projectID;
  }

  public void setProjectID(String projectID) {
    this.projectID = projectID;
  }

  public String getDescription() {
    return description;
  }



  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public int compareTo(Object o) {
    // TODO Auto-generated method stub
    return 0;
  }


}
