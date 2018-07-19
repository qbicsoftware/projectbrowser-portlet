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
import java.util.LinkedHashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import life.qbic.projectbrowser.helpers.JsonHelper;

public class GroupSpecificParameters implements Serializable {
  private static final long serialVersionUID = -6075378925052182947L;

  private final String defaults = "default";
  private String type = "";

  private String multiplicity = "";
  private Set<String> variableModifications = new LinkedHashSet<String>();

  // enzymeMode
  private DigestionMode digestionMode = new DigestionMode(0);
  // enzymes
  private LinkedHashSet<String> enzymes;
  // maxMissedCleavages
  private int maxMissedCleavage;
  private String matchType;
  private boolean lfqMode;
  private LinkedHashSet<String> lightLabels;
  private LinkedHashSet<String> mediumLabels;
  private LinkedHashSet<String> heavyLabels;


  public String getType() {
    return type;
  }



  public void setType(String type) {
    this.type = type;
  }



  public String getMultiplicity() {
    return multiplicity;
  }



  public void setMultiplicity(String multiplicity) {
    this.multiplicity = multiplicity;
  }



  public Set<String> getVariableModifications() {
    return variableModifications;
  }



  public void setVariableModifications(Set<String> variableModifications) {
    this.variableModifications = variableModifications;
  }



  public DigestionMode getDigestionMode() {
    return digestionMode;
  }



  public void setDigestionMode(DigestionMode digestionMode) {
    this.digestionMode = digestionMode;
  }



  public LinkedHashSet<String> getEnzymes() {
    return enzymes;
  }



  public void setEnzymes(LinkedHashSet<String> enzymes) {
    this.enzymes = enzymes;
  }



  public int getMaxMissedCleavage() {
    return maxMissedCleavage;
  }



  public void setMaxMissedCleavage(int maxMissedCleavage) {
    this.maxMissedCleavage = maxMissedCleavage;
  }



  public String getMatchType() {
    return matchType;
  }



  public void setMatchType(String matchType) {
    this.matchType = matchType;
  }



  public LinkedHashSet<String> getLightLabels() {
    return lightLabels;
  }



  public void setLightLabels(LinkedHashSet<String> lightLabels) {
    this.lightLabels = lightLabels;
  }



  public LinkedHashSet<String> getMediumLabels() {
    return mediumLabels;
  }



  public void setMediumLabels(LinkedHashSet<String> mediumLabels) {
    this.mediumLabels = mediumLabels;
  }



  public LinkedHashSet<String> getHeavyLabels() {
    return heavyLabels;
  }



  public void setHeavyLabels(LinkedHashSet<String> heavyLabels) {
    this.heavyLabels = heavyLabels;
  }



  @Override
  public String toString() {
    return "GroupSpecificParameters [defaults=" + defaults + ", type=" + type + ", multiplicity="
        + multiplicity + ", variableModifications=" + variableModifications + ", digestionMode="
        + digestionMode + ", enzymes=" + enzymes + ", maxMissedCleavage=" + maxMissedCleavage
        + ", MatchType=" + matchType + ", lightLabels=" + lightLabels + ", mediumLabels="
        + mediumLabels + ", heavyLabels=" + heavyLabels + "]";
  }


  public JSONObject toJson() throws JSONException {
    JSONObject param = new JSONObject();
    param.put("enzymeMode", digestionMode.getValue());
    param.put("variableModifications", JsonHelper.fromSet(variableModifications));
    param.put("lfqMode", (lfqMode ? 1 : 0));
    if (multiplicity.startsWith("1")) {
      param.put("multiplicity", 1);
    } else if (multiplicity.startsWith("2")) {
      param.put("multiplicity", 2);

      JSONArray labelMods = new JSONArray();
      labelMods.put(JsonHelper.fromSet(lightLabels));
      labelMods.put(JsonHelper.fromSet(heavyLabels));
      param.put("labelMods", labelMods);
    } else if (multiplicity.startsWith("3")) {
      param.put("multiplicity", 3);

      JSONArray labelMods = new JSONArray();
      labelMods.put(JsonHelper.fromSet(lightLabels));
      labelMods.put(JsonHelper.fromSet(mediumLabels));
      labelMods.put(JsonHelper.fromSet(heavyLabels));
      param.put("labelMods", labelMods);
    }
    param.put("maxMissedCleavages", maxMissedCleavage);
    param.put("matchType", matchType);

    // needed by MaxQuant?
    param.put("defaults", "default");
    return param;
  }



  public boolean isLfqMode() {
    return lfqMode;
  }



  public void setLfqMode(boolean lfqMode) {
    this.lfqMode = lfqMode;
  }
}
