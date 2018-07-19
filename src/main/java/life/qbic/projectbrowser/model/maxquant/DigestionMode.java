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

/**
 * Had problems with vaadin converters and nativeselect. This is used so that only the digestion
 * mode is converted but nothing else.
 * 
 * @author wojnar
 * 
 */
public class DigestionMode implements Serializable {
  private static final long serialVersionUID = -6145111851511617158L;

  int value;

  public DigestionMode(int v) {
    value = v;
  }

  void setValue(int v) {
    value = v;
  }

  int getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  public boolean equals(DigestionMode mode) {
    return this.value == mode.getValue();
  }

  public boolean equals(int v) {
    return this.value == v;
  }

}
