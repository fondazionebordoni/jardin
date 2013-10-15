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
import it.fub.jardin.client.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
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
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AddRowForm extends Window {

  private static final String source = "addingrowpopup";

  List<Field<?>> fieldList = new ArrayList<Field<?>>();
  HashMap<String, FieldSet> fieldSetList = new HashMap<String, FieldSet>();
  private static final int defaultWidth = 270; // width dei campi
  private static final int labelWidth = 170;
  private static final int padding = 0;

  final FormPanel formPanel;
  Button button;
  // SearchParams searchData;
  private String username;
  ResultsetImproved resultset;

  // private final JardinGrid grid;

  /**
   * Create a new Detail Area for Impianti printing all available fields
   */
  public AddRowForm(ResultsetImproved resultset) {

    // this.grid = grid;

    /* Impostazione caratteristiche di Window */
    this.setSize(650, 550);
    this.setPlain(true);
    this.setTitle("Inserimento nuovo elemento");
    this.setLayout(new FitLayout());

    this.resultset = resultset;

    /* Creazione FormPanel */
    this.formPanel = new FormPanel();
    this.formPanel.setBodyBorder(false);
    this.formPanel.setLabelWidth(350);
    this.formPanel.setHeaderVisible(false);
    this.formPanel.setScrollMode(Scroll.AUTO);

    this.setAssistedFormPanel();
    // for (final ResultsetField field : this.resultset.getFields()) {

    this.add(this.formPanel);
    this.setButtons();

    this.show();

  }

  public Field getFieldByName(String name) {
    for (Field fg : this.fieldList) {
      if (fg.getName().compareToIgnoreCase(name) == 0) {
        // System.out.println("ritorno campo: " + fg.getName());
        return fg;
      }
    }
    // System.out.println("ritorno campo: UN CAZZO!");
    return null;
  }

  private void setAssistedFormPanel() {
    for (ResultsetField field : this.resultset.getFields()) {

      // foreignkey con la tabella di utenti di sistema
      if (field.getReadperm()) {
        List values = new ArrayList();
        Field PF = FieldCreator.getField(field, values, 0, true, source);
        // Field PF = null;
        if (field.getForeignKey().compareToIgnoreCase("__system_user") == 0) {
          PF = new TextField<String>();
          PF.setValue(username);
          PF.setEnabled(false);
        }
//        else {
////          List values = new ArrayList();
//          PF = FieldCreator.getField(field, values, 0, true, source);
//        }

        if (!field.getInsertperm()) {
          PF.setEnabled(false);
        }

        /* Esamino il raggruppamento a cui appartiene il campo */
        ResultsetFieldGroupings fieldGrouping =
            this.resultset.getFieldGrouping(field.getIdgrouping());

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
        fieldSet.add(PF);

        this.fieldList.add(PF);
        // this.formPanel.add(PF);
      }
    }
  }

  private void setButtons() {
    ButtonBar buttonBar = new ButtonBar();
    this.button = new Button("Aggiungi", new SelectionListener<ButtonEvent>() {

      @Override
      public void componentSelected(final ButtonEvent ce) {

        List<BaseModelData> newItemList = new ArrayList<BaseModelData>();
        BaseModelData newItem = new BaseModelData();
        for (Field<?> field : AddRowForm.this.fieldList) {

          String property = field.getName();

          Object value = null;
          if (field instanceof TimeField) {
            if (field.getValue() == null) {
              value = null;
            } else {
              value =
                  ((Time) field.getValue()).getHour() + ":"
                      + ((Time) field.getValue()).getMinutes();
            }
          } else if (field instanceof SimpleComboBox<?>) {
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
          }
        }
        newItemList.add(newItem);

        AppEvent event = new AppEvent(EventList.saveNewRecord);
        event.setData("object", newItemList);
        event.setData("resultsetid", AddRowForm.this.resultset.getId());
        Dispatcher.forwardEvent(event);

        AddRowForm.this.hide();
      }
    });

    buttonBar.add(this.button);
    this.setBottomComponent(buttonBar);
  }

}
