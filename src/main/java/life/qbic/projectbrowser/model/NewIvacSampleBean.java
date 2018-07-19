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

public class NewIvacSampleBean implements Serializable {

  String type;
  Integer amount;
  String tissue;
  String seqDevice;
  String secondaryName;
  Boolean dnaSeq;
  Boolean rnaSeq;
  Boolean deepSeq;

  public NewIvacSampleBean(String type, Integer amount, String tissue, Boolean dnaSeq,
      Boolean rnaSeq, Boolean deepSeq, String seqDevice, String secondaryName) {
    this.type = type;
    this.amount = amount;
    this.tissue = tissue;
    this.dnaSeq = dnaSeq;
    this.rnaSeq = rnaSeq;
    this.deepSeq = deepSeq;
    this.seqDevice = seqDevice;
    this.secondaryName = secondaryName;
  }

  public NewIvacSampleBean() {

  }
  
  public String getSecondaryName() {
    return secondaryName;
  }

  public void setSecondaryName(String secondaryName) {
    this.secondaryName = secondaryName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public String getTissue() {
    return tissue;
  }

  public void setTissue(String tissue) {
    this.tissue = tissue;
  }

  public Boolean getDnaSeq() {
    return dnaSeq;
  }

  public void setDnaSeq(Boolean dnaSeq) {
    this.dnaSeq = dnaSeq;
  }

  public Boolean getRnaSeq() {
    return rnaSeq;
  }

  public void setRnaSeq(Boolean rnaSeq) {
    this.rnaSeq = rnaSeq;
  }

  public Boolean getDeepSeq() {
    return deepSeq;
  }

  public void setDeepSeq(Boolean deepSeq) {
    this.deepSeq = deepSeq;
  }

  public String getSeqDevice() {
    return seqDevice;
  }

  public void setSeqDevice(String seqDevice) {
    this.seqDevice = seqDevice;
  }

}
