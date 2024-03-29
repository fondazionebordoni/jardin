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
import it.fub.jardin.client.model.ResultsetFieldGroupings;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.ResultsetPlus;
import it.fub.jardin.client.model.ResultsetPlusField;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;

public class SearchAreaAdvanced extends FormPanel {

  private static int defaultWidth = 120;
  private static int labelWidth = 130;
  private static int padding = 0;

  private final ResultsetImproved resultset;
  private final SearchParams searchParams;
  private final List<Field<?>> fieldList;
  private final String source = "searcharea";

  public SearchAreaAdvanced(final ResultsetImproved resultset, User user) {

    this.resultset = resultset;
    this.searchParams = new SearchParams(resultset.getId());
    this.searchParams.setGroupId(user.getGid());
    this.searchParams.setUserId(user.getUid());
    this.fieldList = new ArrayList<Field<?>>();
    this.addStyleName("search-area-advanced");

    this.setBodyBorder(false);
    this.setScrollMode(Scroll.AUTO);
    this.setHeaderVisible(false);
    this.setWidth("100%");
    this.setLayout(new FlowLayout());

    this.createSearchSet();
    this.setButtons();
  }

  private void createSearchSet() {
    HashMap<String, FieldSet> fieldSetList = new HashMap<String, FieldSet>();

    /* Esamino tutti i campi di ricerca */
    for (ResultsetField field : this.resultset.getFields()) {
      // System.out.println("campo: " + field.getName());
      /* Esamino il raggruppamento a cui appartiene il campo */

      ResultsetFieldGroupings fieldGrouping =
          this.resultset.getFieldGrouping(field.getIdgrouping());

      if (field.getReadperm()) {
        /* Esamino se appartiene alla ricerca base o avanzata */
        String mainFieldSetAlias = "Ricerca autocompletata";
        if (field.getSearchgrouping() == 1) {
          mainFieldSetAlias = "Ricerca base";
        }

        /*
         * Se il fieldset principale (base o avanzato) non esiste lo creo e
         * l'aggancio al pannello
         */
        FieldSet mainFieldSet = fieldSetList.get(mainFieldSetAlias);
        if (mainFieldSet == null) {
          mainFieldSet =
              new SimpleFieldSet(mainFieldSetAlias, defaultWidth, labelWidth,
                  padding);
          fieldSetList.put(mainFieldSetAlias, mainFieldSet);
          mainFieldSet.collapse();
          this.add(mainFieldSet);
        }

        /* Creo preventivamente un campo, poi ne gestisco la grafica */

        boolean combo = field.getSearchgrouping() == 0;
        // Field f = FieldCreator.getField(field, values, combo);

        List<String> values = new ArrayList<String>();

        Field<?> f;
        if (combo) {
          // System.out.println(field.getAlias() + ": combo");
          // f = FieldCreator.getField(field, values, true, true);
          f = FieldCreator.getField(field, values, 0, true, source);
          // new ParametricField(resultset.getId(), field.getName(),
          // field.getName(), field.getAlias());
        } else {
          f = FieldCreator.getField(field, values, 0, true);
          // f = new TextField<String>();
          // f.setName(field.getName());
          // f.setFieldLabel(field.getAlias());
        }
        this.fieldList.add(f);

        /*
         * Usiamo un identificativo che comprenda anche il tipo di
         * raggruppamento base, per poter avere categorie distribuite nei due
         * tipi di raggruppamenti base
         */
        String fieldSetName = mainFieldSetAlias + fieldGrouping.getName();

        // /* Esamino il raggruppamento a cui appartiene il campo */
        // ResultsetFieldGroupings fieldGrouping =
        // this.resultset.getFieldGrouping(field.getIdgrouping());
        /* Se il campo non ha raggruppamento l'aggancio a quello base */
        if (fieldGrouping == null) {
          mainFieldSet.add(f);
        } else {

          /*
           * Se il fieldset non esiste lo creo e l'aggancio al fieldset
           * principale
           */
          FieldSet fieldSet = fieldSetList.get(fieldSetName);
          if (fieldSet == null) {
            fieldSet =
                new SimpleFieldSet(fieldGrouping.getAlias(), defaultWidth,
                    labelWidth, padding);
            fieldSetList.put(fieldSetName, fieldSet);
            mainFieldSet.add(fieldSet);
          }

          // System.out.println("SA - campo " + f.getName() +
          // " raggruppamento '" + fieldGrouping.getName() + "("
          // +field.getIdgrouping() + ")");
          /* Aggancio il campo al suo raggruppamento */
          fieldSet.add(f);

        }

        // System.out.println("SA - campo " + f.getName() + " ricerca '" +
        // mainFieldSetAlias
        // + "' (sg=" + field.getSearchgrouping() + ")");

      }
    }
  }

  private void setButtons() {

    this.setButtonAlign(HorizontalAlignment.CENTER);
    this.addButton(new Button("Cerca", new SelectionListener<ButtonEvent>() {

      @Override
      public void componentSelected(final ButtonEvent ce) {
        List<BaseModelData> queryFieldList = new ArrayList<BaseModelData>();

        for (Field<?> field : SearchAreaAdvanced.this.fieldList) {
          String value = field.getRawValue();
          if ((value != null) && (value.length() > 0)) {
            if (field instanceof CheckBox) {
              if (value.compareToIgnoreCase("true") == 0) {
                value = "1";
              } else
                value = "0";
            }
            
            BaseModelData m = new BaseModelData();
            m.set(field.getName(), value);
            queryFieldList.add(m);

          }
        }

        SearchAreaAdvanced.this.searchParams.setFieldsValuesList(queryFieldList);
        Dispatcher.forwardEvent(EventList.Search,
            SearchAreaAdvanced.this.searchParams);
      }
    }));

    this.addButton(new Button("Cancella", new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(final ButtonEvent ce) {
        for (Field<?> field : SearchAreaAdvanced.this.fieldList) {
          field.reset();
        }
      }
    }));

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
