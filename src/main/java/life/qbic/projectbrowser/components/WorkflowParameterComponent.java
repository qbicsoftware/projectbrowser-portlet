/*******************************************************************************
 * QBiC Project qNavigator enables users to manage their projects. Copyright (C) "2016”
 * Christopher Mohr, David Wojnar, Andreas Friedrich
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
package life.qbic.projectbrowser.components;

import submitter.Workflow;
import submitter.parameters.ParameterSet;

import com.vaadin.ui.CustomComponent;

import de.uni_tuebingen.qbic.beans.DatasetBean;

/**
 * Abstract class that will show workflow paramters. It extends the vaadin class CustomComponent. A
 * class that extends this class can be included as a workflow parameter component into the workflow
 * submission layout.
 * 
 * @author wojnar
 * 
 */
public abstract class WorkflowParameterComponent extends CustomComponent {
  static public String datasetType = "noDataSetType";
  private DatasetBean datasetbean;

  public WorkflowParameterComponent(DatasetBean bean) {
    this.datasetbean = bean;
  }

  public WorkflowParameterComponent() {
    // TODO Auto-generated constructor stub
  }

  public abstract Workflow getWorkflow();

  public abstract ParameterSet getParameters();

  /**
   * resets all paramters of this component to default values
   */
  abstract public void resetParameters();

  /**
   * 
   */
  abstract public void buildLayout();

  abstract public void buildLayout(Workflow wf);

}
