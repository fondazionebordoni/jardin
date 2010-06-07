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
import it.fub.jardin.client.Jardin;
import it.fub.jardin.client.ManagerServiceAsync;
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetFieldGroupings;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class JardinDetailPopUp extends Window {

  List<Field<?>> fieldList = new ArrayList<Field<?>>();

  final FormPanel formPanel;
  private static final int defaultWidth = 270; // width dei campi
  private static final int labelWidth = 170;
  private static final int padding = 0;
  Button button;
  // SearchParams searchData;
  ResultsetImproved resultset;
  HashMap<String, FieldSet> fieldSetList = new HashMap<String, FieldSet>();

  /**
   * Create a new Detail Area for Impianti printing all available fields
   */
  public JardinDetailPopUp(final ArrayList<BaseModelData> infoToView) {

    /* Impostazione caratteristiche di Window */
    this.setSize(650, 550);
    this.setPlain(true);

    BaseModelData data = infoToView.get(0);
    BaseModelData entry = infoToView.get(1);

    ResultsetImproved rs = data.get("RSLINKED");
    String linkingField = data.get("FK");

    this.setHeading("Visualizzazione dati in " + rs.getAlias() + " ("
        + linkingField + "=" + data.get("VALUE") + ")");
    this.setLayout(new FitLayout());

    this.resultset = rs;

    /* Creazione FormPanel */
    this.formPanel = new FormPanel();
    this.formPanel.setBodyBorder(false);
    this.formPanel.setLabelWidth(350);
    this.formPanel.setHeaderVisible(false);
    this.formPanel.setScrollMode(Scroll.AUTO);

    this.setFormPanel(data, entry);

    this.add(this.formPanel);
    this.setButtons();

    this.show();

  }

  private void setFormPanel(final BaseModelData data, final BaseModelData entry) {

    for (ResultsetField field : this.resultset.getFields()) {

      if (field.getReadperm()) {
        /* Creo preventivamente un campo, poi ne gestisco la grafica */

        List values = new ArrayList();
        Field f = FieldCreator.getField(field, values, 0, true);
        this.fieldList.add(f);
        if (!field.getModifyperm()) {
          f.setEnabled(false);
        }

        if (f instanceof DateField) {
          java.util.Date date = new java.util.Date();
          date = entry.get(field.getName());
          f.setValue(date);
        } else if (f instanceof TimeField) {
          Time time = new Time();
          time = entry.get(field.getName());
          f.setValue(time);
        } else if (f instanceof SimpleComboBox<?>) {
          if ((field.getType().compareToIgnoreCase("int") == 0)
              || (field.getType().compareToIgnoreCase("real") == 0)) {
            ((SimpleComboBox<Integer>) f).add((Integer) entry.get(field.getName()));
            ((SimpleComboBox<Integer>) f).setSimpleValue((Integer) entry.get(field.getName()));
          } else {
            ((SimpleComboBox<String>) f).add((String) entry.get(field.getName()));
            ((SimpleComboBox<String>) f).setSimpleValue((String) entry.get(field.getName()));
          }
        } else if ((f instanceof TextField<?>) || (f instanceof TextArea)) {
          f.setValue(entry.get(field.getName()));
        }

        /* Esamino il raggruppamento a cui appartiene il campo */
        ResultsetFieldGroupings fieldGrouping =
            this.resultset.getFieldGrouping(field.getIdgrouping());
        /* Se il campo non ha raggruppamento l'aggancio a quello base */
        if (fieldGrouping == null) {
          this.formPanel.add(f);
        } else {
          String fieldSetName = fieldGrouping.getName();

          /*
           * Se il fieldset non esiste lo creo e l'aggancio a pannello
           */
          FieldSet fieldSet = this.fieldSetList.get(fieldSetName);
          if (fieldSet == null) {
            fieldSet =
                new SimpleFieldSet(fieldGrouping.getAlias(), defaultWidth,
                    labelWidth, padding);
            this.fieldSetList.put(fieldSetName, fieldSet);
            this.formPanel.add(fieldSet);
          }

          /* Aggancio il campo al suo raggruppamento */
          fieldSet.add(f);
        }
      }
    }
  }

  private void setButtons() {
    ButtonBar buttonBar = new ButtonBar();
    this.button = new Button("Salva", new SelectionListener<ButtonEvent>() {

      @Override
      public void componentSelected(final ButtonEvent ce) {

        List<BaseModelData> newItemList = new ArrayList<BaseModelData>();
        BaseModelData newItem = new BaseModelData();
        for (Field<?> field : JardinDetailPopUp.this.fieldList) {

          String property = field.getName();

          Object value = null;
          if (field instanceof SimpleComboBox<?>) {
            if (field.getValue() == null) {
              value = "";
            } else {
              SimpleComboValue<?> scv = (SimpleComboValue<?>) field.getValue();
              value = scv.getValue().toString();
            }
          } else if (field instanceof ComboBox<?>) {
            if (field.getValue() == null) {
              value = null;
            } else {
              value = ((BaseModelData) field.getValue()).get(field.getName());
            }
          } else {
            value = field.getValue();
          }
          if (field.getValue() != null) {
            newItem.set(property, value);
            // Log.debug("aggiunto item " + newItem.get(property));
          }
        }
        newItemList.add(newItem);
        JardinDetailPopUp.this.commitChangesAsync(
            JardinDetailPopUp.this.resultset.getId(), newItemList);
      }
    });

    buttonBar.add(this.button);
    this.formPanel.setBottomComponent(buttonBar);
  }

  /**
   * @param resultset
   * @param items
   */
  private void commitChangesAsync(final Integer resultset,
      final List<BaseModelData> items) {

    final MessageBox waitbox =
        MessageBox.wait("Attendere", "Salvataggio in corso...", "");

    /* Create the service proxy class */
    final ManagerServiceAsync service =
        (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    /* Set up the callback */
    AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
      public void onFailure(final Throwable caught) {
        waitbox.close();
        Dispatcher.forwardEvent(EventList.Error, caught.getLocalizedMessage());
      }

      public void onSuccess(final Integer result) {
        waitbox.close();
        if (result.intValue() > 0) {
          Info.display("Informazione", "Dati salvati", "");
          SearchParams sp = new SearchParams(resultset);
          List<BaseModelData> queryFieldList = new ArrayList<BaseModelData>();
          BaseModelData bm = new BaseModelData();

          bm.set("searchField", "");
          queryFieldList.add(bm);
          sp.setFieldsValuesList(queryFieldList);
          JardinDetailPopUp.this.hide();
          Dispatcher.forwardEvent(EventList.Search, sp);

        } else {
          Dispatcher.forwardEvent(EventList.Error,
              "Impossibile salvare le modifiche");
        }
      }
    };

    /* Make the call */
    service.setObjects(resultset, items, callback);
  }

}
