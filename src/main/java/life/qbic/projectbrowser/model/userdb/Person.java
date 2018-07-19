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
package life.qbic.projectbrowser.model.userdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Person {

  private int id;
  private String username;
  private String title;
  private String first;
  private String last;
  private String eMail;
  private String phone;
  private Map<Integer, RoleAt> affiliationInfo; // ids and roles

  public Person(String username, String title, String first, String last, String eMail,
      String phone, int affiliationID, String affiliationName, String affRole) {
    super();
    this.username = username;
    this.title = title;
    this.first = first;
    this.last = last;
    this.eMail = eMail;
    this.phone = phone;
    affiliationInfo = new HashMap<Integer, RoleAt>();
    affiliationInfo.put(affiliationID, new RoleAt(affiliationName, affRole));
  }

  public Person(int id, String username, String title, String first, String last, String eMail,
      String phone, int affiID, String affiliation, String role) {
    super();
    this.id = id;
    this.username = username;
    this.title = title;
    this.first = first;
    this.last = last;
    this.eMail = eMail;
    this.phone = phone;
    affiliationInfo = new HashMap<Integer, RoleAt>();
    affiliationInfo.put(affiID, new RoleAt(affiliation, role));
  }

  /**
   * returns a random affiliation with its role for this user
   * 
   * @return
   */
  public RoleAt getOneAffiliationWithRole() {
    Random random = new Random();
    List<Integer> keys = new ArrayList<Integer>(affiliationInfo.keySet());
    Integer randomKey = keys.get(random.nextInt(keys.size()));
    return affiliationInfo.get(randomKey);
  }

  public String getUsername() {
    return username;
  }

  public String getTitle() {
    return title;
  }

  public String getFirst() {
    return first;
  }

  public String getLast() {
    return last;
  }

  public String geteMail() {
    return eMail;
  }

  public String getPhone() {
    return phone;
  }

  public Map<Integer, RoleAt> getAffiliationInfos() {
    return affiliationInfo;
  }

  public void addAffiliationInfo(int id, String name, String role) {
    affiliationInfo.put(id, new RoleAt(name, role));
  }

  public int getID() {
    return id;
  }
}
