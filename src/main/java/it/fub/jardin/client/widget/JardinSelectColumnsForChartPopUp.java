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

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetImproved;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;

public class JardinSelectColumnsForChartPopUp extends Window {

  final FormPanel formPanel;
  final Button button;
  // SearchParams searchData;
  ResultsetImproved resultset;

  private final JardinGrid grid;

  /**
   * Create a new Detail Area for Impianti printing all available fields
   */
  public JardinSelectColumnsForChartPopUp(final JardinGrid grid, final String ct) {

    this.grid = grid;

    /* Impostazione caratteristiche di Window */
    this.setMinHeight(210);
    // this.setMinWidth(1000);
    this.setPlain(true);
    this.setTitle("Selezione le colonne");
    this.setLayout(new FitLayout());

    this.resultset = grid.getResultset();

    /* Creazione FormPanel */
    this.formPanel = new FormPanel();
    this.formPanel.setBodyBorder(false);
    this.formPanel.setHeaderVisible(false);
    this.formPanel.setScrollMode(Scroll.AUTO);

    final ListBox lbTitle = new ListBox();
    lbTitle.setVisibleItemCount(1);
    lbTitle.setName("title");
    lbTitle.setTitle("Colonna titolo");

    final ListBox lbValue = new ListBox();
    lbValue.setVisibleItemCount(1);
    lbValue.setName("value");
    lbValue.setTitle("Colonna valore");

    /* Recupero le informazioni sui campi */
    BaseModelData fieldsInfo = new BaseModelData();
    for (ResultsetField field : this.resultset.getFields()) {
      fieldsInfo.set(field.getName(), field.getType());
      // System.out.println(field.getType());
    }

    ColumnModel cm = this.grid.getColumnModel();
    for (int i = 0; i < cm.getColumnCount(); i++) {
      if (!(cm.getColumn(i).isHidden())) {
        lbTitle.addItem(cm.getColumn(i).getId());
        if ((fieldsInfo.get(cm.getColumn(i).getId()).toString().compareToIgnoreCase(
            "int") == 0)
            || (fieldsInfo.get(cm.getColumn(i).getId()).toString().compareToIgnoreCase(
                "real") == 0)) {
          lbValue.addItem(cm.getColumn(i).getId());
        }
      }
    }
    // formPanel.addText("Verranno visualizzate solo le prime 50 righe.<br /><br />");
    this.formPanel.addText("Colonna titolo:<br />");
    this.formPanel.add(lbTitle);
    this.formPanel.addText("<br />Colonna valore:<br />");
    this.formPanel.add(lbValue);
    this.formPanel.addText("<br />");
    this.button = new Button("Crea grafico");
    this.formPanel.add(this.button);

    this.button.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(final ClickEvent event) {
        ArrayList<String> dataToChart = new ArrayList<String>();
        dataToChart.add(ct);
        dataToChart.add(""
            + JardinSelectColumnsForChartPopUp.this.resultset.getId());
        dataToChart.add(lbTitle.getValue(lbTitle.getSelectedIndex()));
        dataToChart.add(lbValue.getValue(lbValue.getSelectedIndex()));
        Dispatcher.forwardEvent(EventList.ShowChart, dataToChart);
        //
        JardinSelectColumnsForChartPopUp.this.removeAll();
        JardinSelectColumnsForChartPopUp.this.hide();
      }
    });

    this.add(this.formPanel);

  }
}
