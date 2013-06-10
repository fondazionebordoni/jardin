/**
 * 
 */
package it.fub.jardin.client.widget;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.model.IncomingForeignKeyInformation;
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetFieldGroupings;
import it.fub.jardin.client.model.ResultsetImproved;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
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

/**
 * @author acozzolino
 * 
 */
public class JardinAddingPopUp extends Window {

  private IncomingForeignKeyInformation fkIN;
  List<Field<?>> fieldList = new ArrayList<Field<?>>();

  private FormPanel formPanel;
  private static final int defaultWidth = 270; // width dei campi
  private static final int labelWidth = 170;
  private static final int padding = 0;
  private static final String source = "addingpopup";
  Button button;
  // SearchParams searchData;
  ResultsetImproved resultset;
  HashMap<String, FieldSet> fieldSetList = new HashMap<String, FieldSet>();
  private String username;

  public JardinAddingPopUp(IncomingForeignKeyInformation fkIN, String username) {
    this.fkIN = fkIN;

    this.setSize(650, 550);
    this.setPlain(true);

    this.setHeading("Aggiunta record in "
        + this.fkIN.getInterestedResultset().getAlias());
    this.setLayout(new FitLayout());

    this.resultset = this.fkIN.getInterestedResultset();
    this.username = username;

    /* Creazione FormPanel */
    this.formPanel = new FormPanel();
    this.formPanel.setBodyBorder(false);
    this.formPanel.setLabelWidth(350);
    this.formPanel.setHeaderVisible(false);
    this.formPanel.setScrollMode(Scroll.AUTO);

    this.setFormPanel(this.fkIN.getLinkingField(), this.fkIN.getFieldValue());

    this.add(this.formPanel);
    this.setButtons();

    this.show();
  }

  private void setFormPanel(String linkingField, String fdValue) {

    // System.out.println("resultset coinvolto: " + this.resultset.getName());
    // System.out.println("numero campi di " + this.resultset.getName() + ": "
    // + this.resultset.getFields().size());
    for (ResultsetField field : this.resultset.getFields()) {

      List values = new ArrayList();
      Field f = null;

      if (field.getInsertperm()) {
        // System.out.println("aggiunto campo: " + field.getName());
        /* Creo preventivamente un campo, poi ne gestisco la grafica */

        // System.out.println("FK: " + field.getForeignKey());
        if (field.getForeignKey().compareToIgnoreCase("__system_user.username") == 0) {
          f = new TextField<String>();
          f.setValue(username);
          f.setEnabled(false);
        } else if (field.getName().compareToIgnoreCase(linkingField) == 0) {
          f = new TextField<String>();

          f.setValue(fdValue);
          f.setEnabled(false);

        } else {

          f = FieldCreator.getField(field, values, 0, true, source);

          // System.out.println("permessi campo " + field.getName() + " r:"
          // + field.getReadperm() + " m:" + field.getModifyperm() + " i:"
          // + field.getInsertperm() + " d:" + field.getDeleteperm());

          if (!field.getInsertperm()) {
            f.setEnabled(false);
          }

          if (f instanceof DateField) {
            java.util.Date date = new java.util.Date();
            // if (fdValue != null && fdValue != "") {
            f.setValue(date);
            // }
          } else if (f instanceof TimeField) {
            Time time = new Time();
            if (fdValue != null && fdValue != "") {
              f.setValue(time);
            }
          } else if (f instanceof SimpleComboBox<?>) {
            // if ((field.getType().compareToIgnoreCase("int") == 0)
            // || (field.getType().compareToIgnoreCase("real") == 0)) {
            // ((SimpleComboBox<Integer>) f).add(Integer.valueOf(fdValue));
            // if (fdValue != null && fdValue != "") {
            // ((SimpleComboBox<Integer>)
            // f).setSimpleValue(Integer.valueOf(fdValue));
            // }
            // } else {
            // ((SimpleComboBox<String>) f).add(fdValue);
            // if (fdValue != null && fdValue != "") {
            // ((SimpleComboBox<String>) f).setSimpleValue(fdValue);
            // }
            // }
          } else if ((f instanceof TextField<?>) || (f instanceof TextArea)) {
            f = new TextField<String>();
            f.setValue("");
          }
        }
      } else {
        f = new TextField<String>();
        f.setValue("");
        f.setEnabled(false);
      }

      f.setFieldLabel(field.getAlias());
      f.setName(field.getName());

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
      fieldSet.add(f);

      this.fieldList.add(f);

    }
  }

  private void setButtons() {
    ButtonBar buttonBar = new ButtonBar();

    final List<Field<?>> fieldList = this.fieldList;

    this.button = new Button("Salva", new SelectionListener<ButtonEvent>() {

      @Override
      public void componentSelected(final ButtonEvent ce) {

        List<BaseModelData> newItemList = new ArrayList<BaseModelData>();
        BaseModelData newItem = new BaseModelData();
        for (Field<?> field : fieldList) {

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
              value = "";
            } else {
              value = ((BaseModelData) field.getValue()).get(field.getName());
            }
          } else {
            value = field.getValue();
          }

          if (value != null) {
            // System.out.println("aggiungere: " + property + " con valore "
            // + value);
            newItem.set(property, value);
            // Log.debug("aggiunto item " + newItem.get(property));
          }
        }

        // BaseModelData resultsetIdentifier = new BaseModelData();
        // resultsetIdentifier.set("RSID", resultset.getId());

        // il primo record è l'id del RS, il secondo i valori dei campi da
        // salvare
        // newItemList.add(resultsetIdentifier);
        newItemList.add(newItem);

        AppEvent event = new AppEvent(EventList.saveNewRecord);
        event.setData("object", newItemList);
        event.setData("resultsetid", resultset.getId());

        Dispatcher.forwardEvent(event);
        // JardinDetailPopUp.this.commitChangesAsync(
        // JardinDetailPopUp.this.resultset.getId(), newItemList);
        hide();
      }
    });

    buttonBar.add(this.button);
    this.formPanel.setBottomComponent(buttonBar);
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
