/**
 * 
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

/**
 * @author acozzolino
 */
public class JardinDetailPopUp extends Window {

  List<Field> fieldList = new ArrayList<Field>();

  final FormPanel formPanel;
  private static final int defaultWidth = 270; //width dei campi
  private static final int labelWidth = 170;
  private static final int padding = 0;
  Button button;
  // SearchParams searchData;
  ResultsetImproved resultset;
  HashMap<String, FieldSet> fieldSetList = new HashMap<String, FieldSet>();



  /**
   * Create a new Detail Area for Impianti printing all available fields
   */
  public JardinDetailPopUp(BaseModelData data) {

    /* Impostazione caratteristiche di Window */
    this.setSize(650, 550);
    this.setPlain(true);
    
    ResultsetImproved rs = data.get("RSLINKED");
    String linkingField = data.get("FK");
    
    this.setHeading("Visualizzazione dati in " + rs.getAlias() +" ("+linkingField+"="+data.get("VALUE")+")");
    this.setLayout(new FitLayout());

    this.resultset = rs;

    /* Creazione FormPanel */
    formPanel = new FormPanel();
    formPanel.setBodyBorder(false);
    formPanel.setLabelWidth(350);
    formPanel.setHeaderVisible(false);
    formPanel.setScrollMode(Scroll.AUTO);
    
    setAssistedFormPanel();
    // for (final ResultsetField field : this.resultset.getFields()) {

    add(formPanel);
    setButtons();

    show();

  }

  private void setAssistedFormPanel() {
	    for (ResultsetField field : this.resultset.getFields()) {

	        if (field.getReadperm()) {
	          /* Creo preventivamente un campo, poi ne gestisco la grafica */

	          List<String> values =
	              resultset.getForeignKeyList().getValues(field.getId());
	          // Field f = FieldCreator.getField(field, values, true, labelWidth);
	          // Field f = FieldCreator.getField(field,0);
	          Field f = FieldCreator.getField(field, values, 0, true);

	          if (!field.getModifyperm()) {
	            f.setEnabled(false);
	          }

	          /* Esamino il raggruppamento a cui appartiene il campo */
	          ResultsetFieldGroupings fieldGrouping =
	              this.resultset.getFieldGrouping(field.getIdgrouping());
	          /* Se il campo non ha raggruppamento l'aggancio a quello base */
	          if (fieldGrouping == null) {
	            formPanel.add(f);
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
	              formPanel.add(fieldSet);
	            }

	            /* Aggancio il campo al suo raggruppamento */
	            fieldSet.add(f);
	          }
	        }
	      }
  }

  private void setButtons() {
    ButtonBar buttonBar = new ButtonBar();
    button = new Button("Salva", new SelectionListener<ButtonEvent>() {

      @Override
      public void componentSelected(ButtonEvent ce) {

        List<BaseModelData> newItemList = new ArrayList<BaseModelData>();
        BaseModelData newItem = new BaseModelData();
        for (Field field : fieldList) {

          System.out.println(field.getValue());
          String property = field.getName();

          Object value = null;
          if (field instanceof SimpleComboBox) {
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
    formPanel.setBottomComponent(buttonBar);
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
