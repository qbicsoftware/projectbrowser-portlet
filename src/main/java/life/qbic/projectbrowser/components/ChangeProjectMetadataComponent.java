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

import java.util.Collection;
import java.util.HashMap;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import life.qbic.portal.utils.PortalUtils;
import life.qbic.projectbrowser.helpers.Utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import life.qbic.projectbrowser.model.ProjectBean;
import life.qbic.projectbrowser.controllers.*;


public class ChangeProjectMetadataComponent extends CustomComponent {
  /**
   * TODO generic component for experiments and samples
   */
  private static final long serialVersionUID = -5318223225284123020L;

  private static final Logger LOG = LogManager.getLogger(ChangeProjectMetadataComponent.class);

  private DataHandler datahandler;
  private String resourceUrl;
  private State state;
  private VerticalLayout properties;

  private FormLayout form;
  private FieldGroup fieldGroup;
  VerticalLayout vert;

  private String currentDescription;

  public ChangeProjectMetadataComponent(DataHandler dh, State state, String resourceurl) {
    this.datahandler = dh;
    this.resourceUrl = resourceurl;
    this.state = state;

    // this.setCaption("Metadata");

    this.initUI();
  }

  private void initUI() {
    properties = new VerticalLayout();
    properties.setWidth(100.0f, Unit.PERCENTAGE);
    properties.setMargin(new MarginInfo(true, false, true, true));
    properties.setSpacing(true);

    this.setWidth(Page.getCurrent().getBrowserWindowWidth() * 0.8f, Unit.PIXELS);
    this.setCompositionRoot(properties);
  }

  public void updateUI(final ProjectBean projectBean) {
    properties.removeAllComponents();
    Button saveButton = new Button("Submit Changes");
    saveButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);

    currentDescription = projectBean.getDescription();

    saveButton.addClickListener(new ClickListener() {
      @Override
      public void buttonClick(final ClickEvent event) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        Collection<Field<?>> registeredFields = fieldGroup.getFields();

        // List<Property> factors = new ArrayList<Property>();

        for (Field<?> field : registeredFields) {
          parameters.put("description", field.getValue());
        }

        parameters.put("identifier", projectBean.getId());
        parameters.put("user", PortalUtils.getNonNullScreenName());
        datahandler.getOpenBisClient().triggerIngestionService("update-project-metadata",
            parameters);
        Utils.Notification("Project details changed succesfully", String
            .format("Details of project %s have been commited successfully.", projectBean.getId()),
            "success");
      }
    });

    buildFormLayout();
    Label desc = new Label(String.format(
        "This view shows project details and can be used to change the corresponding values. \nIdentifier: %s",
        projectBean.getId()), Label.CONTENT_PREFORMATTED);
    desc.setWidth("50%");
    properties.addComponent(desc);

    properties.addComponent(this.form);
    properties.addComponent(saveButton);
  }


  private void buildFormLayout() {
    final FieldGroup fieldGroup = new FieldGroup();
    final FormLayout form2 = new FormLayout();

    TextArea tf = new TextArea("Description");
    tf.setWidth("50%");
    tf.setHeight("50%");
    fieldGroup.bind(tf, "Description");
    tf.setCaption("Description");
    tf.setDescription("Description of this project.");
    tf.setValue(currentDescription);
    form2.addComponent(tf);

    this.fieldGroup = fieldGroup;
    this.form = form2;
  }

}
