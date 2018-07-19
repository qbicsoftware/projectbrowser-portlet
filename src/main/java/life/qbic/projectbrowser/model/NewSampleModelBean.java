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

public class NewSampleModelBean {

  String Code;
  String Secondary_Name;
  String Type;

  public NewSampleModelBean(String code, String secondaryName, String type) {
    this.Code = code;
    this.Secondary_Name = secondaryName;
    this.Type = type;
  }

  public String getType() {
    return Type;
  }

  public void setType(String type) {
    this.Type = type;
  }

  public String getCode() {
    return Code;
  }

  public void setCode(String code) {
    this.Code = code;
  }

  public String getSecondary_Name() {
    return Secondary_Name;
  }

  public void setSecondary_Name(String secondaryName) {
    this.Secondary_Name = secondaryName;
  }


}
