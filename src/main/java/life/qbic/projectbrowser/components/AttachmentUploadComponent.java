/*******************************************************************************
 * QBiC Project qNavigator enables users to manage their projects. Copyright (C) "2016” Christopher
 * Mohr, David Wojnar, Andreas Friedrich
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
package life.qbic.projectbrowser.components;

import java.util.ArrayList;
import java.util.Arrays;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

import life.qbic.openbis.openbisclient.OpenBisClient;
import life.qbic.portal.utils.ConfigurationManager;

import life.qbic.portal.utils.PortalUtils;
import life.qbic.projectbrowser.helpers.AttachmentConfig;

public class AttachmentUploadComponent extends CustomComponent {

  private VerticalLayout mainView;

  VerticalLayout vert;
  String id;

  public AttachmentUploadComponent() {
    this.setCaption("Upload Files");
    this.initUI();
  }

  private void initUI() {
    mainView = new VerticalLayout();
    mainView.setResponsive(true);

    mainView.setWidth(100.0f, Unit.PERCENTAGE);
    mainView.setMargin(new MarginInfo(true, false, true, true));
    mainView.setSpacing(true);

    setResponsive(true);
  }

  public void updateUI(ConfigurationManager manager, String projectCode, String space,
      OpenBisClient openBisClient) {
    AttachmentConfig attachConfig = new AttachmentConfig(
        Integer.parseInt(manager.getAttachmentMaxSize()), manager.getAttachmentURI(),
        manager.getAttachmentUser(), manager.getAttachmenPassword());

    mainView = new UploadsPanel(manager.getTmpFolder(), space, projectCode,
        new ArrayList<String>(Arrays.asList("Project Planning", "Results")),
            PortalUtils.getUser().getScreenName(), attachConfig, openBisClient);
    mainView.setResponsive(true);

    mainView.setWidth(100.0f, Unit.PERCENTAGE);
    mainView.setMargin(new MarginInfo(true, false, true, true));
    mainView.setSpacing(true);

    this.setCompositionRoot(mainView);

  }
}
