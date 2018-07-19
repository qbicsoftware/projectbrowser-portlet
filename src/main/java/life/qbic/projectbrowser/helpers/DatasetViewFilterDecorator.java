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
package life.qbic.projectbrowser.helpers;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Locale;

import org.tepi.filtertable.FilterDecorator;
// import org.tepi.filtertable.demo.FilterTableDemoUI.State;
import org.tepi.filtertable.numberfilter.NumberFilterPopupConfig;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.datefield.Resolution;

@SuppressWarnings("serial")
public class DatasetViewFilterDecorator implements FilterDecorator, Serializable {

  @Override
  public String getEnumFilterDisplayName(Object propertyId, Object value) {
    /*
     * if ("state".equals(propertyId)) { State state = (State) value; switch (state) { case CREATED:
     * return "Order has been created"; case PROCESSING: return "Order is being processed"; case
     * PROCESSED: return "Order has been processed"; case FINISHED: return "Order is delivered"; } }
     */
    // returning null will output default value
    return null;
  }

  @Override
  public Resource getEnumFilterIcon(Object propertyId, Object value) {
    /*
     * if ("state".equals(propertyId)) { State state = (State) value; switch (state) { case CREATED:
     * return new ThemeResource("../runo/icons/16/document.png"); case PROCESSING: return new
     * ThemeResource("../runo/icons/16/reload.png"); case PROCESSED: return new
     * ThemeResource("../runo/icons/16/ok.png"); case FINISHED: return new
     * ThemeResource("../runo/icons/16/globe.png"); } }
     */
    return null;
  }

  @Override
  public String getBooleanFilterDisplayName(Object propertyId, boolean value) {
    if ("validated".equals(propertyId)) {
      return value ? "Validated" : "Not validated";
    }
    // returning null will output default value
    return null;
  }

  @Override
  public Resource getBooleanFilterIcon(Object propertyId, boolean value) {
    if ("validated".equals(propertyId)) {
      return value ? new ThemeResource("../runo/icons/16/ok.png") : new ThemeResource(
          "../runo/icons/16/cancel.png");
    }
    return null;
  }

  @Override
  public String getFromCaption() {
    return "Start date:";
  }

  @Override
  public String getToCaption() {
    return "End date:";
  }

  @Override
  public String getSetCaption() {
    // use default caption
    return null;
  }

  @Override
  public String getClearCaption() {
    // use default caption
    return null;
  }

  @Override
  public boolean isTextFilterImmediate(Object propertyId) {
    // use text change events for all the text fields
    return true;
  }

  @Override
  public int getTextChangeTimeout(Object propertyId) {
    // use the same timeout for all the text fields
    return 500;
  }

  @Override
  public String getAllItemsVisibleString() {
    return "Search";
  }

  @Override
  public Resolution getDateFieldResolution(Object propertyId) {
    return Resolution.DAY;
  }

  public DateFormat getDateFormat(Object propertyId) {
    return DateFormat.getDateInstance(DateFormat.SHORT, new Locale("fi", "FI"));
  }

  @Override
  public boolean usePopupForNumericProperty(Object propertyId) {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  public String getDateFormatPattern(Object propertyId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Locale getLocale() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public NumberFilterPopupConfig getNumberFilterPopupConfig() {
    // TODO Auto-generated method stub
    return null;
  }
}
