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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.vaadin.server.StreamResource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import life.qbic.xml.manager.XMLParser;
import life.qbic.xml.properties.Property;
import life.qbic.projectbrowser.components.TSVDownloadComponent;

public class TSVReadyRunnable implements Runnable {

  private TSVDownloadComponent layout;
  Map<String, List<String>> tables;
  private String project;
  private static final Logger LOG = LogManager.getLogger(TSVReadyRunnable.class);

  public TSVReadyRunnable(TSVDownloadComponent layout, Map<String, List<String>> tables,
      String project) {
    this.layout = layout;
    this.tables = tables;
    this.project = project;
  }

  @Override
  public void run() {
    List<StreamResource> streams = new ArrayList<StreamResource>();
    streams.add(
        getTSVStream(getTSVString(tables.get("Q_BIOLOGICAL_ENTITY")), project + "_sample_sources"));
    streams.add(getTSVStream(getTSVString(tables.get("Q_BIOLOGICAL_SAMPLE")),
        project + "_sample_extracts"));
    if (tables.containsKey("Q_TEST_SAMPLE"))
      streams.add(getTSVStream(getTSVString(tables.get("Q_TEST_SAMPLE")),
          project + "_sample_preparations"));
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

  private static String getTSVString(List<String> table) {
    XMLParser p = new XMLParser();

    StringBuilder header = new StringBuilder(table.get(0).replace("\tAttributes", ""));
    StringBuilder tsv = new StringBuilder();
    table.remove(0);

    String xmlStart = "<?xml";
    // header
    List<String> factorLabels = new ArrayList<String>();
    for (String row : table) {
      String[] lineSplit = row.split("\t", -1);// doesn't remove trailing whitespaces
      String xml = "";
      for (String cell : lineSplit) {
        if (cell.startsWith(xmlStart))
          xml = cell;
      }
      List<Property> factors = new ArrayList<Property>();
      if (!xml.equals(xmlStart)) {
        try {
          factors = p.getAllPropertiesFromXML(xml);
        } catch (JAXBException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        for (Property f : factors) {
          String label = f.getLabel();
          if (!factorLabels.contains(label)) {
            factorLabels.add(label);
            header.append("\tCondition: " + label);
          }
        }
      }
    }

    // data
    for (String row : table) {
      String[] lineSplit = row.split("\t", -1);// doesn't remove trailing whitespaces
      String xml = "";
      for (String cell : lineSplit) {
        if (cell.startsWith(xmlStart))
          xml = cell;
      }
      row = row.replace("\t" + xml, "");
      StringBuilder line = new StringBuilder("\n" + row);
      List<Property> factors = new ArrayList<Property>();
      if (!xml.equals(xmlStart)) {
        try {
          factors = p.getAllPropertiesFromXML(xml);
        } catch (JAXBException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Map<Integer, Property> order = new HashMap<Integer, Property>();
        for (Property f : factors) {
          String label = f.getLabel();
          order.put(factorLabels.indexOf(label), f);
        }
        for (int i = 0; i < factorLabels.size(); i++) {
          if (order.containsKey(i)) {
            Property f = order.get(i);
            line.append("\t" + f.getValue());
            if (f.hasUnit())
              line.append(f.getUnit());
          } else {
            line.append("\t");
          }
        }
      } else {
        for (int i = 0; i < factorLabels.size() - 1; i++) {
          line.append("\t");
        }
      }
      tsv.append(line);
    }
    return header.append(tsv).toString();
  }

}
