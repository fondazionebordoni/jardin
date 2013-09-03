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

import it.fub.jardin.client.model.JungleRecords;
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetImproved;

import java.util.List;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;

public class Jungle extends Dialog {

  private final ListGrid grid;

  public Jungle(final ResultsetImproved resultset, final List<String> cm,
      final String xml) {
    this.setMaximizable(true);
    this.setTitle("Jungle (Jardin warehousing): " + resultset.getAlias());
    this.setWidth(500);
    this.setHeight(500);
    this.setLayout(new FitLayout());

    ToolBar toolBar = new ToolBar();
    toolBar.add(this.formulaButton());
    toolBar.add(this.summaryButton());
    this.setTopComponent(toolBar);

    this.grid = this.getGrid(resultset, cm);
    this.grid.setDataSource(new JungleRecords(resultset, xml));
    this.grid.setAutoFetchData(true);
    // this.grid.resizeTo(this.getWidth(), this.getHeight());

    this.add(this.grid);

    Listener<WindowEvent> listener = new Listener<WindowEvent>() {
      public void handleEvent(final WindowEvent be) {
        Jungle.this.grid.resizeTo(be.getWidth() - 14, be.getHeight());
      }
    };

    this.addListener(Events.Resize, listener);
    this.getButtonBar().removeAll();
  }

  private Button formulaButton() {
    SelectionListener l = new SelectionListener() {
      @Override
      public void componentSelected(final ComponentEvent ce) {
        Jungle.this.grid.addFormulaField();
      }
    };

    return new Button("Formula", IconHelper.createStyle("icon-formula"), l);
  }

  private Button summaryButton() {
    SelectionListener l = new SelectionListener() {
      @Override
      public void componentSelected(final ComponentEvent ce) {
        Jungle.this.grid.addSummaryField();
      }
    };

    return new Button("Summary", IconHelper.createStyle("icon-summary"), l);
  }

  private ListGrid getGrid(final ResultsetImproved resultset,
      final List<String> columns) {
    ListGrid grid = new ListGrid();
    ListGridField[] list = new ListGridField[resultset.getFields().size()];

    int i = 0;
    for (ResultsetField field : resultset.getFields()) {
      if (field.getReadperm() && columns.contains(field.getName())) {
        list[i++] = new ListGridField(field.getName(), field.getAlias());
      }
    }

    grid.setFields(list);
    return grid;
  }
}
