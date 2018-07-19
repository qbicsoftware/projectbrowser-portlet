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
package life.qbic.projectbrowser.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class spaceToProjectPrefixMap {
  public static final Map<String, String> myMap;
  static {
    Map<String, String> aMap = new HashMap<String, String>();
    aMap.put("IVAC_ALL", "QA");
    aMap.put("IVAC_ALL_1", "QO");
    aMap.put("IVAC_CEGAT", "QC");
    aMap.put("IVAC_TEST_SPACE", "QT");
    aMap.put("IVAC_HEPA_VAC", "QH");
    aMap.put("IVAC_INDIVIDUAL_LIVER", "QI");
    aMap.put("IVAC_SFB685_C9_PC", "QS");
    aMap.put("IVAC_ALL_DKTK", "QD");
    aMap.put("IVAC_AML_KIKLI", "QAK");
    aMap.put("IVAC_EWING", "QE");
    aMap.put("IVAC_INFORM_DKTK_KIKLI", "QFK");
    aMap.put("IVAC_LUCA", "QL");
    aMap.put("IVAC_PANC", "QP");
    aMap.put("IVAC_PANC_KIKLI", "QPK");
    aMap.put("IVAC_RCC", "QR");
    aMap.put("IVAC_RCC_KIKLI", "QRK");
    aMap.put("IVAC_SARC", "QS");
    aMap.put("IVAC_MACA", "QB");
    aMap.put("IVAC_OVCA", "QO");
    aMap.put("IVAC_BRAINTUMOR_MUE", "QBM");
    myMap = Collections.unmodifiableMap(aMap);
  }
}
