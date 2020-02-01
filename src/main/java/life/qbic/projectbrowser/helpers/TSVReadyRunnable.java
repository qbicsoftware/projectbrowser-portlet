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
package life.qbic.projectbrowser.helpers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vaadin.server.StreamResource;


import life.qbic.projectbrowser.components.TSVDownloadComponent;
import life.qbic.utils.TimeUtils;

public class TSVReadyRunnable implements Runnable {

  private TSVDownloadComponent layout;
  Map<String, String> tableContentStrings;
  private String project;

  public TSVReadyRunnable(TSVDownloadComponent layout, Map<String, String> tableContentStrings,
      String project) {
    this.layout = layout;
    this.tableContentStrings = tableContentStrings;
    this.project = project;
  }

  @Override
  public void run() {
    List<StreamResource> streams = new ArrayList<StreamResource>();
    String ts = TimeUtils.getCurrentTimestampString();
    streams.add(
        getTSVStream(tableContentStrings.get("Q_BIOLOGICAL_ENTITY"), project + "_sample_sources_"+ts));
    streams.add(
        getTSVStream(tableContentStrings.get("Q_BIOLOGICAL_SAMPLE"), project + "_sample_extracts_"+ts));
    if (tableContentStrings.containsKey("Q_TEST_SAMPLE"))
      streams.add(
          getTSVStream(tableContentStrings.get("Q_TEST_SAMPLE"), project + "_sample_preparations_"+ts));
    layout.armButtons(streams);
  }

  public StreamResource getTSVStream(final String content, String name) {
    StreamResource resource = new StreamResource(new StreamResource.StreamSource() {
      @Override
      public InputStream getStream() {
        try {
          InputStream is = new ByteArrayInputStream(content.getBytes());
          return is;
        } catch (Exception e) {
          e.printStackTrace();
          return null;
        }
      }
    }, String.format("%s.tsv", name));
    return resource;
  }
}
