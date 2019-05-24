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
package life.qbic.projectbrowser.helpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;

import com.vaadin.ui.TextField;

public class GridFunctions {
  // Set up a filter for all columns

  private static final Logger LOGGER = LogManager.getLogger(GridFunctions.class);

  public static void addColumnFilters(Grid grid, final GeneratedPropertyContainer gpcBio) {
    HeaderRow filterRow = grid.appendHeaderRow();
    
    for (final Object pid : grid.getContainerDataSource().getContainerPropertyIds()) {
      HeaderCell cell = filterRow.getCell(pid);

      // Have an input field to use for filter
      final TextField filterField = new TextField();
      filterField.setWidth("100%");

      // filterField.setColumns(8);

      // Update filter When the filter input is changed
      filterField.addTextChangeListener(new TextChangeListener() {

        /**
         * 
         */
        private static final long serialVersionUID = 7670817216478146116L;
        Filter currentFilter = null;

        @Override
        public void textChange(TextChangeEvent event) {
          if (currentFilter != null) {
            gpcBio.removeContainerFilter(currentFilter);
            currentFilter = null;
          }

          if (!event.getText().isEmpty()) {
            currentFilter = new SimpleStringFilter(pid, event.getText(), true, false);
            gpcBio.addContainerFilter(currentFilter);
          }
          // (Re)create the filter if necessary
          // if (!filterField.getValue().equals("")) {
          // currentFilter = new SimpleStringFilter(pid, filterField.getValue(), true, false);
          // gpcBio.addContainerFilter(currentFilter);
          // }

        }
      });
      cell.setComponent(filterField);
    }
  }
}
