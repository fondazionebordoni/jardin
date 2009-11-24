/**
 * 
 */
package it.fub.jardin.client.widget;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.Jardin;
import it.fub.jardin.client.ManagerServiceAsync;
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
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

/**
 * @author acozzolino
 */
public class AddRowForm extends Window {

  List<Field> fieldList = new ArrayList<Field>();

  final FormPanel formPanel;
  Button button;
  // SearchParams searchData;
  ResultsetImproved resultset;

  private JardinGrid grid;

  /**
   * Create a new Detail Area for Impianti printing all available fields
   */
  public AddRowForm(JardinGrid grid) {

    this.grid = grid;

    /* Impostazione caratteristiche di Window */
    this.setSize(650, 550);
    this.setPlain(true);
    this.setHeading("Inserimento nuovo elemento");
    this.setLayout(new FitLayout());

    this.resultset = (ResultsetImproved) grid.getResultset();

    /* Creazione FormPanel */
    formPanel = new FormPanel();
    formPanel.setBodyBorder(false);
    formPanel.setLabelWidth(350);
    formPanel.setHeaderVisible(false);
    formPanel.setScrollMode(Scroll.AUTO);

    Radio radio1 = new Radio();
    radio1.setBoxLabel("Yes");
    radio1.setData("valore", "yes");
    radio1.setValue(false);

    Radio radio2 = new Radio();
    radio2.setBoxLabel("No");
    radio2.setData("valore", "no");
    radio2.setValue(true);

    final RadioGroup group = new RadioGroup();
    group.setFieldLabel("usare tendine per delimitare i valori consentiti?");
    group.add(radio1);
    group.add(radio2);

    formPanel.add(group);

    group.addListener(Events.Change, new Listener<ComponentEvent>() {

      public void handleEvent(ComponentEvent be) {
        Radio selected = group.getValue();
        // MessageBox.alert("selezione", "selezionato: "
        // + selected.getData("valore"), null);

        if (((String) selected.getData("valore")).compareToIgnoreCase("yes") == 0) {
          formPanel.removeAll();
          setAssistedFormPanel();
          layout();
        } else {
          formPanel.removeAll();
          setUnAssistedFormPanel();
          layout();
        }
      }

    });

    setAssistedFormPanel();
    // for (final ResultsetField field : this.resultset.getFields()) {

    add(formPanel);
    setButtons();

    show();

  }

  private void setAssistedFormPanel() {
    for (ResultsetField field : this.resultset.getFields()) {

      if (field.getForeignKey().compareToIgnoreCase("") != 0) {
        List<String> values =
            resultset.getForeignKeyList().getValues(field.getId());

        Field PF = FieldCreator.getField(field, values, 0, true);
        // ParametricField PF =
        // new ParametricField(grid.getResultset().getId(),
        // field.getForeignKey().split("\\.")[1], field.getName(),
        // field.getAlias());

        if (!field.getInsertperm()) {
          PF.setEnabled(false);
        }

        // if (!field.getDeleteperm()) {
        // PF.setEnabled(false);
        // }

        fieldList.add(PF);
        formPanel.add(PF);

      } else {
        if (((String) field.getType()).compareToIgnoreCase("TIME") == 0) {
          TimeField textField = new TimeField();
          textField.setName((String) field.getName());
          textField.setFieldLabel((String) field.getAlias());
          textField.setFormat(DateTimeFormat.getFormat("kk:mm:ss"));

          fieldList.add(textField);
          formPanel.add(textField);

          if (!field.getInsertperm()) {
            textField.setEnabled(false);
            textField.setValue(null);
          }

        } else if (((String) field.getType()).compareToIgnoreCase("DATE") == 0
            || ((String) field.getType()).compareToIgnoreCase("DATETIME") == 0) {

          DateField textField = new DateField();
          textField.setName((String) field.getName());
          textField.setFieldLabel((String) field.getAlias());
          textField.getPropertyEditor().setFormat(
              DateTimeFormat.getFormat("dd/MM/y"));
          java.util.Date date = new java.util.Date();
          textField.setValue(date);

          if (!field.getInsertperm()) {
            textField.setEnabled(false);
            textField.setValue(null);
          }

          fieldList.add(textField);
          formPanel.add(textField);

        } else if ((((String) field.getType()).compareToIgnoreCase("BLOB") == 0)
            || (((String) field.getType()).compareToIgnoreCase("TEXT") == 0)) {
          TextArea textField = new TextArea();
          textField.setName((String) field.getName());
          textField.setFieldLabel((String) field.getAlias());
          fieldList.add(textField);
          formPanel.add(textField);
        } else {

          TextField<String> textField = new TextField<String>();
          textField.setName((String) field.getName());
          textField.setFieldLabel((String) field.getAlias());

          if (!field.getInsertperm()) {
            textField.setEnabled(false);
            textField.setValue(null);
          }

          if (!field.getInsertperm()) {
            textField.setEnabled(false);
            textField.setValue(null);
          }

          fieldList.add(textField);
          formPanel.add(textField);
        }
      }
    }
  }

  private void setUnAssistedFormPanel() {
    for (ResultsetField field : this.resultset.getFields()) {

      if (field.getForeignKey().compareToIgnoreCase("") != 0) {
        // List<String> values =
        // resultset.getForeignKeyList().getValues(field.getId());

        // Field PF = FieldCreator.getField(field, values, 0);
        ParametricField PF =
            new ParametricField(grid.getResultset().getId(),
                field.getForeignKey().split("\\.")[1], field.getName(),
                field.getAlias());

        if (!field.getInsertperm()) {
          PF.setEnabled(false);
        }

        // if (!field.getDeleteperm()) {
        // PF.setEnabled(false);
        // }

        fieldList.add(PF);
        formPanel.add(PF);

      } else {
        if (((String) field.getType()).compareToIgnoreCase("TIME") == 0) {
          TimeField textField = new TimeField();
          textField.setName((String) field.getName());
          textField.setFieldLabel((String) field.getAlias());
          textField.setFormat(DateTimeFormat.getFormat("kk:mm:ss"));

          fieldList.add(textField);
          formPanel.add(textField);

          if (!field.getInsertperm()) {
            textField.setEnabled(false);
            textField.setValue(null);
          }

        } else if (((String) field.getType()).compareToIgnoreCase("DATE") == 0
            || ((String) field.getType()).compareToIgnoreCase("DATETIME") == 0) {

          DateField textField = new DateField();
          textField.setName((String) field.getName());
          textField.setFieldLabel((String) field.getAlias());
          java.util.Date date = new java.util.Date();
          textField.setValue(date);

          if (!field.getInsertperm()) {
            textField.setEnabled(false);
            textField.setValue(null);
          }

          fieldList.add(textField);
          formPanel.add(textField);

        } else {

          TextField<String> textField = new TextField<String>();
          textField.setName((String) field.getName());
          textField.setFieldLabel((String) field.getAlias());

          if (!field.getInsertperm()) {
            textField.setEnabled(false);
            textField.setValue(null);
          }

          fieldList.add(textField);
          formPanel.add(textField);
        }
      }
    }
  }

  private void setButtons() {
    ButtonBar buttonBar = new ButtonBar();
    button = new Button("Aggiungi", new SelectionListener<ButtonEvent>() {

      @Override
      public void componentSelected(ButtonEvent ce) {

        List<BaseModelData> newItemList = new ArrayList<BaseModelData>();
        BaseModelData newItem = new BaseModelData();
        for (Field field : fieldList) {

          System.out.println(field.getValue());
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
          } else if (field instanceof SimpleComboBox) {
            if (field.getValue() == null) {
              value = "";
            } else {
              SimpleComboValue scv = (SimpleComboValue) field.getValue();
              value = scv.getValue().toString();
              // value = ((BaseModelData)
              // field.getValue())
              // .get(field.getName());
            }
          } else if (field instanceof ComboBox) {
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
            System.out.println("aggiunto item " + newItem.get(property));
          }
        }
        newItemList.add(newItem);
        commitChangesAsync(resultset.getId(), newItemList);
      }
    });

    buttonBar.add(button);
    this.setBottomComponent(buttonBar);
  }

  /**
   * @param resultset
   * @param items
   */
  private void commitChangesAsync(final Integer resultset,
      List<BaseModelData> items) {

    final MessageBox waitbox =
        MessageBox.wait("Attendere", "Salvataggio in corso...", "");

    /* Create the service proxy class */
    final ManagerServiceAsync service =
        (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    /* Set up the callback */
    AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
      public void onFailure(Throwable caught) {
        waitbox.close();
        Dispatcher.forwardEvent(EventList.Error, caught.getLocalizedMessage());
      }

      public void onSuccess(Integer result) {
        waitbox.close();
        if (result.intValue() > 0) {
          Info.display("Informazione", "Dati salvati", "");
          SearchParams sp = new SearchParams(resultset);
          List<BaseModelData> queryFieldList = new ArrayList<BaseModelData>();
          BaseModelData bm = new BaseModelData();

          bm.set("searchField", "");
          queryFieldList.add(bm);
          sp.setFieldsValuesList(queryFieldList);
          hide();
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
