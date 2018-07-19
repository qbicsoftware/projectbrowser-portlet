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

import life.qbic.openbis.openbisclient.OpenBisClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import life.qbic.projectbrowser.helpers.TSVReadyRunnable;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class TSVDownloadComponent extends VerticalLayout {

  /**
   * 
   */
  private static final long serialVersionUID = 6140141377721355605L;

  private HorizontalLayout downloads;
  // private ProgressBar bar;

  private Button dlEntities;
  private Button dlExtracts;
  private Button dlPreps;

  private static final Logger LOG = LogManager.getLogger(TSVDownloadComponent.class);
  private List<FileDownloader> downloaders = new ArrayList<FileDownloader>();

  public TSVDownloadComponent() {
    // TODO progress bar not used atm because the labels in available UpdateProgressBar classes are
    // too specific
    // probably not needed anyway since ProjInformationComponent seems to show when everything is
    // complete
    // this.bar = new ProgressBar();
    // this.info = new Label();
    // info.setCaption("Preparing Spreadsheets");
    // addComponent(info);
    // addComponent(bar);

    downloads = new HorizontalLayout();
    downloads.setCaption("Spreadsheets");
    downloads.setSpacing(true);
    dlEntities = new Button("Sample Sources");
    dlExtracts = new Button("Sample Extracts");
    dlPreps = new Button("Sample Preparations");

    dlEntities.setStyleName(ValoTheme.BUTTON_LINK);
    dlEntities.setIcon(FontAwesome.DOWNLOAD);

    dlExtracts.setStyleName(ValoTheme.BUTTON_LINK);
    dlExtracts.setIcon(FontAwesome.DOWNLOAD);

    dlPreps.setStyleName(ValoTheme.BUTTON_LINK);
    dlPreps.setIcon(FontAwesome.DOWNLOAD);

    dlEntities.setEnabled(false);
    dlExtracts.setEnabled(false);
    dlPreps.setEnabled(false);
    downloads.addComponent(dlEntities);
    downloads.addComponent(dlExtracts);
    downloads.addComponent(dlPreps);
    addComponent(downloads);
  }

  public void disableSpreadSheets() {
    dlEntities.setEnabled(false);
    dlExtracts.setEnabled(false);
    dlPreps.setEnabled(false);
  }

  public void prepareSpreadsheets(final List<String> sampleTypes, String space,
      final String project, final OpenBisClient openbis) {
    final TSVDownloadComponent layout = this;

    // final int todo = 1;
    // Thread t = new Thread(new Runnable() {
    // // volatile int current = 0;
    //
    // @Override
    // public void run() {
    //
    Map<String, List<String>> tables = new HashMap<String, List<String>>();
    for (String type : sampleTypes) {
      tables.put(type, openbis.getProjectTSV(project, type));
      // current++;
      // updateProgressBar(current, todo, bar, info);
    }
    // UI.getCurrent().setPollInterval(-1);
    // UI.getCurrent().access(new TSVReadyRunnable(layout, tables, project));
    // }
    // });
    // t.start();
    // UI.getCurrent().setPollInterval(100);
    TSVReadyRunnable r = new TSVReadyRunnable(layout, tables, project);
    r.run();
  }

  // private void updateProgressBar(int current, int todo, ProgressBar bar, Label info) {
  // double frac = current * 1.0 / todo;
  // UI.getCurrent().access(new UpdateProgressBar(bar, info, frac));
  // }

  public void armButtons(List<StreamResource> streams) {
    armDownloadButton(dlEntities, streams.get(0), 1);
    armDownloadButton(dlExtracts, streams.get(1), 2);
    if (streams.size() > 2)
      armDownloadButton(dlPreps, streams.get(2), 3);
  }

  protected void armDownloadButton(Button b, StreamResource stream, int dlnum) {
    if (downloaders.size() < dlnum) {
      FileDownloader dl = new FileDownloader(stream);
      dl.extend(b);
      downloaders.add(dl);
    } else
      downloaders.get(dlnum - 1).setFileDownloadResource(stream);
    b.setEnabled(true);
  }

  public void enableDownloads(boolean b) {
    downloads.setEnabled(b);
  }

}
