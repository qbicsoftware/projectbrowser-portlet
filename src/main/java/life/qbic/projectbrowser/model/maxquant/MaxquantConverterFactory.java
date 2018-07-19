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

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;

/**
 * According to this https://vaadin.com/forum#!/thread/8216422 The Converter (in this case
 * MaxquantDigestionModeToNameConverter) will not call for Native select by defaut So one has to
 * create a Factory and include it into VaadinSession via
 * VaadinSession.getCurrent().setConverterFactory( new MaxquantConverterFactory()); this is true for
 * vaadin 7.5
 * 
 * @author wojnar
 * 
 */
public class MaxquantConverterFactory extends DefaultConverterFactory {
  private static final long serialVersionUID = 6988206501368446880L;

  @SuppressWarnings("unchecked")
  @Override
  public <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> createConverter(
      Class<PRESENTATION> presentationType, Class<MODEL> modelType) {
    // Handle one particular type conversion
    if ((String.class == presentationType || Object.class == presentationType)
        && DigestionMode.class == modelType)
      return (Converter<PRESENTATION, MODEL>) new MaxquantDigestionModeToNameConverter();

    // Default to the supertype
    return super.createConverter(presentationType, modelType);
  }
}
