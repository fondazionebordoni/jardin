/**
 * 
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

  private static final int defaultWidth = 270; //width dei campi
  private static final int labelWidth = 170;
  private static final int padding = 0;

  private ResultsetImproved resultset;

  public JardinDetail(ResultsetImproved resultset) {

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
    for (ResultsetField field : this.resultset.getFields()) {

      if (field.getReadperm()) {
        /* Creo preventivamente un campo, poi ne gestisco la grafica */

        List<String> values = new ArrayList<String>();
        //resultset.getForeignKeyList().getValues(field.getId());
        Field<?> f = FieldCreator.getField(field, values, 0, true);

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
      }
    }
  }
}
