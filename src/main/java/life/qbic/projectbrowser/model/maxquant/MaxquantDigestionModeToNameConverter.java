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

import java.util.HashMap;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/**
 * converts maxquant digestion modes from digestionMode(integer) to a user readable string and vice versa.
 * @author wojnar
 *
 */
public class MaxquantDigestionModeToNameConverter implements Converter<String, DigestionMode> {
  private static final long serialVersionUID = 8425874280007470256L;

  HashMap<Integer, String> modeToName = new HashMap<Integer, String>();
  HashMap<String, Integer> nameToMode = new HashMap<String, Integer>();

  public MaxquantDigestionModeToNameConverter() {
    add(0, "Specific");
  }

  public void add(int mode, String name) {
    modeToName.put(mode, name);
    nameToMode.put(name, mode);
  }

  @Override
  public DigestionMode convertToModel(String value, Class<? extends DigestionMode> targetType,
      Locale locale) throws ConversionException {
    if (nameToMode.containsKey(value)) {
      return new DigestionMode(nameToMode.get(value));
    } else {
      throw new ConversionException(
          "Unknown digestion mode: " + value);
    }
  }

  @Override
  public String convertToPresentation(DigestionMode value, Class<? extends String> targetType,
      Locale locale) throws ConversionException {
    if (modeToName.containsKey(value.getValue())) {
      return modeToName.get(value.getValue());
    } else {
      throw new ConversionException(
          "Unknown digestion mode: " + value);
    }
  }

  @Override
  public Class<DigestionMode> getModelType() {
    return DigestionMode.class;
  }

  @Override
  public Class<String> getPresentationType() {
    // TODO Auto-generated method stub
    return String.class;
  }

}
