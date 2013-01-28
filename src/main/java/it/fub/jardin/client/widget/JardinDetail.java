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

import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetFieldGroupings;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.mvc.JardinView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;

public class JardinDetail extends FormPanel {

  private static final int defaultWidth = 270; // width dei campi
  private static final int labelWidth = 170;
  private static final int padding = 0;

  private final ResultsetImproved resultset;
  private final String source = "detail";

  private List<Field> fieldList;

  public JardinDetail(final ResultsetImproved resultset) {

    fieldList = new ArrayList<Field>();
    this.resultset = resultset;
    this.addStyleName(JardinView.DETAIL_AREA);

    if (GXT.isChrome) {
      this.setAutoWidth(true);
    }

    if (GXT.isIE) {
      MessageBox.alert("Attenzione!!!", "State usando Internet Explorer."
          + " Le funzioni di export"
          + " sono possibili solo attivando 'Richiesta"
          + " automatica per download di file' nelle"
          + " opzioni di sicurezza del browser", null);
    }

    this.setBodyBorder(false);
    this.setWidth("100%");
    this.setFieldWidth(defaultWidth);
    this.setLabelWidth(labelWidth);
    this.setLabelAlign(LabelAlign.RIGHT);
    this.setHeaderVisible(false);
    this.setScrollMode(Scroll.AUTO);

    HashMap<String, FieldSet> fieldSetList = new HashMap<String, FieldSet>();

    /* Esamino tutti i campi */
    for (final ResultsetField field : this.resultset.getFields()) {

//      Field f = null;

//      field.doTypeAnalisys(); // specifica variabili tipo, lunghezza e valori

      if (field.getReadperm()) {
        /* Creo preventivamente un campo, poi ne gestisco la grafica */
        // System.out.println("campo del dettaglio: " + field.getName());

         List<String> values = new ArrayList<String>();
         Field f = FieldCreator.getField(field, values, 0, true, source);


        if ((labelWidth > 0) && (field.getAlias().length() > labelWidth / 10)) {
          f.setFieldLabel(field.getAlias().substring(0, labelWidth / 10)
              + "...");
          f.setToolTip(field.getAlias());
        } else {
          f.setFieldLabel(field.getAlias());
        }

        if (!field.getModifyperm()) {
          f.setEnabled(false);
        }

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
        
        fieldList.add(f);
//        System.out.println("AGGIUNTO " + f.getName() + " al dettaglio");
      }

      
    }
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
