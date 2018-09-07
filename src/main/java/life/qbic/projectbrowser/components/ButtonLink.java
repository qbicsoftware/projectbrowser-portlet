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
package life.qbic.projectbrowser.components;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;


/*
 * Link that looks like a Button, that is actually a Label Based on
 * https://vaadin.com/forum/#!/thread/69989
 */
public class ButtonLink extends Label{
  /**
   * 
   */
  private static final long serialVersionUID = 4654807493467264366L;
  String caption;

  /**
   * Link that looks like a Button, that is actually a Label. Set the caption of the "Button" and
   * the externalResource which should contain a url
   * 
   * @param caption
   * @param externalResource
   */
  public ButtonLink(String caption, ExternalResource externalResource) {
    super();
    super.setWidth(null);
    this.caption = caption;
    setResource(externalResource);
  }

  public void setResource(ExternalResource externalResource) {
    buildHTMLCode(externalResource.getURL());
    super.setContentMode(ContentMode.HTML);
  }

  // The following lines are copy pasted from rendered Vaadin v6.1 buttons.
  private void buildHTMLCode(String url) {
    StringBuilder sb = new StringBuilder("<a href='");
    sb.append(url);
    sb.append("' style='text-decoration: display: block;'>");
    sb.append("<div class='v-button' tabindex='0' style='width: 100%;'>");
    sb.append("<span class='v-button-wrap'>");
    sb.append(caption);
    sb.append("</span>");
    sb.append("</div>");
    sb.append("</a>");
    super.setValue(sb.toString());
  }

}
