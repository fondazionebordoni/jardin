/*
 * Copyright (c) 2010 Jardin Development Group <jardin.project@gmail.com>.
 * 
 * Jardin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Jardin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Jardin.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.fub.jardin.client.widget;

import it.fub.jardin.client.model.ResultsetFieldGroupings;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.RnfMessages;

import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;

public class JardinGridView extends GridView {

  private final RnfMessages mess;
  private final ResultsetImproved resultset;
  private final JardinColumnModel cm;

  public JardinGridView(final ResultsetImproved resultset,
      final JardinColumnModel cm) {
    this.mess = (RnfMessages) GWT.create(RnfMessages.class);
    this.resultset = resultset;
    this.cm = cm;

  }

  private void restrictMenu(final Menu columns) {
    int count = 0;
    for (int i = 0, len = this.cm.getColumnCount(); i < len; i++) {
      if (!this.cm.isHidden(i) && !this.cm.isFixed(i)) {
        count++;
      }
    }

    if (count == 1) {
      for (Component item : columns.getItems()) {
        CheckMenuItem ci = (CheckMenuItem) item;
        if (ci.isChecked()) {
          ci.disable();
        }
      }
    } else {
      for (Component item : columns.getItems()) {
        item.enable();
      }
    }
  }

  @Override
  protected Menu createContextMenu(final int colIndex) {
    final Menu menu = new Menu();
    List<ResultsetFieldGroupings> groups = this.resultset.getFieldGroupings();

    if (this.cm.isSortable(colIndex)) {
      MenuItem item = new MenuItem();
      item.setText(GXT.MESSAGES.gridView_sortAscText());
      item.setIconStyle("my-icon-asc");
      item.addSelectionListener(new SelectionListener<MenuEvent>() {
        @Override
        public void componentSelected(final MenuEvent ce) {
          JardinGridView.this.ds.sort(
              JardinGridView.this.cm.getDataIndex(colIndex), SortDir.ASC);
        }

      });
      menu.add(item);

      item = new MenuItem();
      item.setText(GXT.MESSAGES.gridView_sortDescText());
      item.setIconStyle("my-icon-desc");
      item.addSelectionListener(new SelectionListener<MenuEvent>() {
        @Override
        public void componentSelected(final MenuEvent ce) {
          JardinGridView.this.ds.sort(
              JardinGridView.this.cm.getDataIndex(colIndex), SortDir.DESC);
        }
      });
      menu.add(item);
    }

    if (groups != null) {
      int numFields = this.cm.getColumnCount();
      int numMenu = groups.size();

      int i = 0;
      int x = 0;
      int incrementalMenu = 0;
      boolean exit = false;
      int addCounter = 0;

      for (int j = 0; j < numMenu; j++) {
        // columns[j] = new MenuItem();

        if (exit) { // è uscito perchè il contatore è arrivato
          exit = false;
        } else { // è uscito dal while perchè i == numFields
          incrementalMenu = 0;
          x = 0;
        }

        MenuItem columns = new MenuItem();
        Integer groupingId = groups.get(j).getId();
        String groupingAlias = groups.get(j).getAlias();
        columns.setText(this.mess.gridHeaderColumnsMessage(groupingAlias));
        columns.setIconStyle("x-cols-icon");

        final Menu columnMenu = new Menu();

        addCounter = 0;
        i = x;
        while ((i < numFields) && !exit) {

          if (this.cm.getColumn(x).getGroupingId() == groupingId.intValue()) {
            if ((this.cm.getColumnHeader(x) == null)
                || this.cm.getColumnHeader(x).equals("") || this.cm.isFixed(x)) {
              continue;
            }
            final int fcol = x;
            final CheckMenuItem check = new CheckMenuItem();
            check.setHideOnClick(false);
            check.setText(this.cm.getColumnHeader(x));
            // check.setText(cm.getColumnId(x));
            check.setChecked(!this.cm.isHidden(x));
            check.addSelectionListener(new SelectionListener<MenuEvent>() {
              @Override
              public void componentSelected(final MenuEvent ce) {
                JardinGridView.this.cm.setHidden(fcol,
                    !JardinGridView.this.cm.isHidden(fcol));
                JardinGridView.this.restrictMenu(columnMenu);
              }
            });
            columnMenu.add(check);
            addCounter++;
            /*
             * Ogni Volta che aggiungo una voce al menù, aumento i counter. Se
             * il counter supera il numero massimo di elementi previsti in un
             * menù, devo creare un nuovo sottomenù ovvero diminuisco j di 1.
             */
            if (addCounter > 26) {
              incrementalMenu++;
              j--;
              exit = true;
            }

          }
          i++;
          x++;
        }

        this.restrictMenu(columnMenu);
        if (columnMenu.getItemCount() > 0) {
          columns.setSubMenu(columnMenu);
          menu.add(columns);
        }

      }
    }

    return menu;
  }

  public void setGridHeader() {
    // TODO aggiunta per sottolineare la foreignkey
    for (int i = 0; i < this.cm.getColumnCount(); i++) {
      if ((this.cm.getColumn(i).isKey()) || (this.cm.getColumn(i).isUnique())) {
        this.getHeader().setHeader(i,
            "<u><b>" + this.cm.getColumn(i).getHeader() + "</u></b>");
      }
    }
  }

}
