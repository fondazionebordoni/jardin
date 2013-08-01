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
import it.fub.jardin.client.model.NewObjects;
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetFieldGroupings;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.tools.FieldDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.bcel.generic.INSTANCEOF;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
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

public class JardinEditorPopUp extends Window {

  List<Field<?>> fieldList = new ArrayList<Field<?>>();

  private FormPanel formPanel;
  private static final int defaultWidth = 270; // width dei campi
  private static final int labelWidth = 170;
  private static final int padding = 0;

  private static final String source = "editorpopup";
  Button button;
  // SearchParams searchData;
  private ResultsetImproved resultset;
  private BaseModelData record;
  private String username;

  HashMap<String, FieldSet> fieldSetList = new HashMap<String, FieldSet>();

  /**
   * Create a new Detail Area for Impianti printing all available fields
   * 
   * @param user
   * @param resultset
   */
  public JardinEditorPopUp(ResultsetImproved resultset, String username,
      BaseModelData record) {

    /* Impostazione caratteristiche di Window */
    this.setSize(650, 550);
    this.setPlain(true);

    this.setHeading("Modifica dati in " + resultset.getAlias());
    this.setLayout(new FitLayout());

    this.resultset = resultset;
    this.record = record;
    this.username = username;

    /* Creazione FormPanel */
    this.formPanel = new FormPanel();
    this.formPanel.setBodyBorder(false);
    this.formPanel.setLabelWidth(350);
    this.formPanel.setHeaderVisible(false);
    this.formPanel.setScrollMode(Scroll.AUTO);

    setFormPanel();

    this.add(this.formPanel);
    this.setButtons();

    this.show();

  }

  private void setFormPanel() {

    for (ResultsetField field : this.resultset.getFields()) {

      List values = new ArrayList();
      Field f = null;

      if (field.getReadperm()) {
        /* Creo preventivamente un campo, poi ne gestisco la grafica */

        f = FieldCreator.getField(field, values, 0, true, source);
        // this.fieldList.add(f);

        if (!field.getModifyperm()) {
          f.setEnabled(false);
        }

        if (f instanceof CheckBox) {
          Boolean defaultValue = Boolean.FALSE;
          if (record.get(field.getName()).toString().compareToIgnoreCase("1") == 0
              || record.get(field.getName()).toString().compareToIgnoreCase(
                  "true") == 0) {
            defaultValue = Boolean.TRUE;
          }
          ((CheckBox) f).setValue(defaultValue);
        } else if (f instanceof TimeField) {
          Time defaultValue = new Time();
          if (record.get(field.getName()) != null) {

            int hours =
                Integer.parseInt(record.get(field.getName()).toString().substring(
                    0, 2));
            int mins =
                Integer.parseInt(record.get(field.getName()).toString().substring(
                    3, 5));
            defaultValue.setHour(hours);
            defaultValue.setMinutes(mins);

            defaultValue = ((TimeField) f).findModel(hours, mins);

            ((TimeField) f).select(defaultValue);

          }

        } else if (f instanceof DateField) {
          java.util.Date date = new java.util.Date();
          date = record.get(field.getName());
          f.setValue(date);
        } else if (f instanceof SimpleComboBox) {
          if (field.getSpecificType().compareToIgnoreCase(FieldDataType.INT) == 0) {
            if (record.get(field.getName()) != null) {
              ((SimpleComboBox<Integer>) f).add((Integer) record.get(field.getName()));
              ((SimpleComboBox<Integer>) f).setSimpleValue((Integer) record.get(field.getName()));
            }
          } else if (field.getSpecificType().compareToIgnoreCase(
              FieldDataType.FLOAT) == 0) {
            ((SimpleComboBox<Float>) f).add((Float) record.get(field.getName()));
            ((SimpleComboBox<Float>) f).setSimpleValue((Float) record.get(field.getName()));
          } else if (field.getSpecificType().compareToIgnoreCase(
              FieldDataType.DOUBLE) == 0) {
            ((SimpleComboBox<Double>) f).add((Double) record.get(field.getName()));
            ((SimpleComboBox<Double>) f).setSimpleValue((Double) record.get(field.getName()));
          } else if (field.getSpecificType().compareToIgnoreCase(
              FieldDataType.ENUM) == 0) {
            String defaultValue = record.get(field.getName());
            ((SimpleComboBox<String>) f).add(field.getFixedElements());
            ((SimpleComboBox<String>) f).setSimpleValue(defaultValue);
          } else {
            ((SimpleComboBox<String>) f).add((String) record.get(field.getName()));
            ((SimpleComboBox<String>) f).setSimpleValue((String) record.get(field.getName()));
          }
        } else if ((f instanceof TextField) || (f instanceof TextArea)) {
          f.setValue(record.get(field.getName()));
        }

        /* Esamino il raggruppamento a cui appartiene il campo */
        /* Esamino il raggruppamento a cui appartiene il campo */
        ResultsetFieldGroupings fieldGrouping =
            this.resultset.getFieldGrouping(field.getIdgrouping());
        /* Se il campo non ha raggruppamento l'aggancio a quello base */
        if (fieldGrouping == null) {
          this.add(f);
        } else {
          String fieldSetName = fieldGrouping.getName();

          /*
           * Se il fieldset non esiste lo creo e l'aggancio a pannello
           */
          FieldSet fieldSet = fieldSetList.get(fieldSetName);
          if (fieldSet == null) {
            fieldSet =
                new SimpleFieldSet(fieldGrouping.getAlias(), defaultWidth,
                    labelWidth, padding);
            fieldSetList.put(fieldSetName, fieldSet);
            this.add(fieldSet);
          }

          /* Aggancio il campo al suo raggruppamento */
          fieldSet.add(f);
        }

        this.fieldList.add(f);

      }

    }
  }

  private void setButtons() {
    ButtonBar buttonBar = new ButtonBar();

    final List<Field<?>> fieldList = this.fieldList;

    this.button =
        new Button("Salva modifiche", new SelectionListener<ButtonEvent>() {

          @Override
          public void componentSelected(final ButtonEvent ce) {

            List<BaseModelData> newItemList = new ArrayList<BaseModelData>();
            BaseModelData newItem = new BaseModelData();
            for (Field field : fieldList) {

              String property = field.getName();

              // System.out.println(property + " campo di tipo "
              // + field.getClass());
              Object value = null;
              if (field instanceof SimpleComboBox) {
                if (field.getValue() == null) {
                  value = "";
                } else {
                  SimpleComboValue<?> scv =
                      (SimpleComboValue<?>) field.getValue();
                  value = scv.getValue().toString();
                }
              } else if (field instanceof TimeField) {
                value =
                    ((TimeField) field).getValue().getHour() + ":"
                        + ((TimeField) field).getValue().getMinutes();
              } else if (field instanceof ComboBox) {
                if (field.getValue() == null) {
                  value = "";
                } else {
                  value =
                      ((BaseModelData) field.getValue()).get(field.getName());
                }
              } else {
                value = field.getValue();
              }

              // if (value != null) {
              // System.out.println("aggiungere: " + property + " con valore "
              // + value);
              newItem.set(property, value);
              // Log.debug("aggiunto item " + newItem.get(property));
              // }
            }

            // BaseModelData resultsetIdentifier = new BaseModelData();
            // resultsetIdentifier.set("RSID", resultset.getId());

            // il primo record è l'id del RS, il secondo i valori dei campi da
            // salvare
            // newItemList.add(resultsetIdentifier);
            newItemList.add(newItem);

            NewObjects no = new NewObjects(resultset.getId(), newItemList);
            AppEvent event = new AppEvent(EventList.UpdateObjects);
            event.setData(no);

            Dispatcher.forwardEvent(event);

            hide();
          }
        });

    buttonBar.add(this.button);
    this.setBottomComponent(buttonBar);
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

}
