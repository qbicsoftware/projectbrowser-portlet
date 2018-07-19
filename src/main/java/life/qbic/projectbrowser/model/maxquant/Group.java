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
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Group implements Serializable {



  private static final long serialVersionUID = 5252681184750898666L;
  List<RawFilesBean> files = new ArrayList<RawFilesBean>();
  private GroupSpecificParameters parameters = new GroupSpecificParameters();


  /**
   * adds a raw file bean to the groups files. If a bean is already contained with the exact same
   * values, it will not be added again.
   * 
   * @param bean
   */
  public void addFile(RawFilesBean bean) {
    if (!files.contains(bean)) {
      files.add(bean);
    }
  }

  /**
   * removes all files from this group
   */
  public void removeFiles() {
    files.clear();
  }


  public void setFiles(List<RawFilesBean> list) {
    removeFiles();
    files.addAll(list);
  }

  @Override
  public String toString() {
    return "Group [files=" + files + ", parameters=" + parameters + "]";
  }

  public GroupSpecificParameters getParameters() {
    return parameters;
  }

  public JSONObject toJson() throws JSONException {
    JSONObject group = new JSONObject();
    JSONObject params = this.parameters.toJson();
    JSONArray files = this.filesToJson();

    group.put("params", params);
    group.put("files", files);

    return group;
  }

  private JSONArray filesToJson() throws JSONException {
    JSONArray fls = new JSONArray();
    for (RawFilesBean file : files) {
      JSONObject tmp = new JSONObject();
      // tmp.put("name", file.getFile());
      // mqrun needs filenames without file ending
      tmp.put("name", file.generateParamFile());
      tmp.put("fraction", file.getFraction());
      tmp.put("experiment", file.getExperiment());
      fls.put(tmp);
    }
    return fls;
  }

}
