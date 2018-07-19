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
package life.qbic.projectbrowser.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.ui.UI;

public class State extends Observable implements Serializable {



  /**
   * 
   */
  private static final long serialVersionUID = -8448995433087062650L;
  private static final Logger LOG = LogManager.getLogger(State.class);

  public void notifyObservers(ArrayList<String> message) {

    try {
      String message2 = message.get(2).toLowerCase();
      if (message.get(1).contains("IVAC") && (message2.equals("project"))) {
        UI.getCurrent().getNavigator()
            .navigateTo(String.format("ivac%s/%s", message2, message.get(1)));
      } else {
        UI.getCurrent().getNavigator().navigateTo(String.format("%s/%s", message2, message.get(1)));
      }
      this.setChanged();
      super.notifyObservers(message);
    } catch (IllegalArgumentException e) {
      LOG.error(
          String.format("message1: %s, message2: %s, current View: %s.", message.get(1),
              message.get(2), "not available"), e.getStackTrace());
    }
  }

  public State() {

  }
}
