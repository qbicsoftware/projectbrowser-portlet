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

import com.vaadin.ui.UI;
import life.qbic.openbis.openbisclient.OpenBisClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import life.qbic.projectbrowser.helpers.TSVReadyRunnable;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import life.qbic.xml.properties.Property;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TSVDownloadComponent extends VerticalLayout {

  private static final long serialVersionUID = 6140141377721355605L;

  private HorizontalLayout downloads;

  private Button dlEntities;
  private Button dlExtracts;
  private Button dlPreps;

  private static final Logger LOG = LogManager.getLogger(TSVDownloadComponent.class);

  private List<FileDownloader> downloaders = new ArrayList<FileDownloader>();

  public TSVDownloadComponent() {
    // TODO: progress bar not used atm because the labels in available UpdateProgressBar classes are
    // too specific
    // probably not needed anyway since ProjInformationComponent seems to show when everything is
    // complete

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
    dlEntities.setImmediate(true);
    dlExtracts.setImmediate(true);
    dlPreps.setImmediate(true);
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
      final String project, final OpenBisClient openbis, final Set<String> factors,
      final Map<Pair<String, String>, Property> factorsForLabelsAndSamples,
      Map<String, List<Property>> samplesToProperties) {
    final TSVDownloadComponent layout = this;

    final Thread t = new Thread(() -> {
      Map<String, String> tables = new HashMap<String, String>();
      for (String type : sampleTypes) {
        tables.put(type, getTSVString(openbis.getProjectTSV(project, type), factors,
            factorsForLabelsAndSamples, samplesToProperties));
      }
      UI.getCurrent().access(new TSVReadyRunnable(layout, tables, project));
      UI.getCurrent().setPollInterval(-1);
    });
    t.start();
    UI.getCurrent().setPollInterval(100);
  }

  private static String getTSVString(List<String> table, Set<String> factors,
      Map<Pair<String, String>, Property> factorsForLabelsAndSamples,
      Map<String, List<Property>> samplesToProperties) {

    final StringBuilder header = new StringBuilder(table.get(0).replace("\tAttributes", ""));
    final StringBuilder tsv = new StringBuilder();
    table.remove(0);

    final String xmlStart = "<?xml";
    List<String> labels = new ArrayList<>();
    // header

    Set<String> sampleCodes = new HashSet<>();
    Set<String> propertyLabels = new HashSet<>();
    for (String row : table) {
      String[] lineSplit = row.split("\t", -1);// doesn't remove trailing whitespaces
      String code = lineSplit[0];
      sampleCodes.add(code);
    }

    for (String label : factors) {
      boolean used = false;
      for (String code : sampleCodes) {
        if (factorsForLabelsAndSamples.get(new ImmutablePair<>(label, code)) != null) {
          used = true;
          break;
        }
      }
      if (used) {
        labels.add(label);
        header.append("\tCondition: " + label);
      }
    }

    // first collect all properties used on this level of the experiment
    for (String code : sampleCodes) {
      if (samplesToProperties.containsKey(code)) {
        for (Property p : samplesToProperties.get(code)) {
          String label = p.getLabel();
          propertyLabels.add(label);
        }
      }
    }
    // then determine an ordering for header and data
    List<String> propLabelsOrdered = new ArrayList<>(propertyLabels);
    for (String propLabel : propLabelsOrdered) {
      header.append("\tProperty: " + propLabel);
    }

    // data
    for (String row : table) {
      String[] lineSplit = row.split("\t", -1);// doesn't remove trailing whitespaces
      String xml = "";
      String sampleCode = lineSplit[0];
      for (String cell : lineSplit) {
        if (cell.startsWith(xmlStart))
          xml = cell;
      }
      row = row.replace("\t" + xml, "");
      StringBuilder line = new StringBuilder("\n" + row);

      for (String label : labels) {
        Property f = factorsForLabelsAndSamples.get(new ImmutablePair<>(label, sampleCode));
        if (f != null) {
          line.append("\t" + f.getValue());
          if (f.hasUnit())
            line.append(f.getUnit());
        } else {
          line.append("\t");
        }
      }

      for (String propLabel : propertyLabels) {
        boolean found = false;
        for (Property p : samplesToProperties.get(sampleCode)) {
          if (p.getLabel().equals(propLabel)) {
            line.append("\t" + p.getValue());
            if (p.hasUnit())
              line.append(p.getUnit());
            found = true;
            break;
          }
        }
        if (!found) {
          line.append("\t");
        }
      }

      tsv.append(line);
    }
    return header.append(tsv).toString();
  }

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
