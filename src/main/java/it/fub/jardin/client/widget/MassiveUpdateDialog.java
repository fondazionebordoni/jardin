/**
 * 
 */
package it.fub.jardin.client.widget;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.model.MassiveUpdateObject;
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetFieldGroupings;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;

/**
 * @author acozzolino
 * 
 */
public class MassiveUpdateDialog extends Window {

  private ResultsetImproved resultset;
  private ListStore<BaseModelData> gridStore;
  private SearchParams searchParams;
  private FormPanel formPanel;
  private List<Field> fieldList;
  HashMap<String, FieldSet> fieldSetList = new HashMap<String, FieldSet>();
  private ResultsetField primaryKey;
  private List<String> primaryKeyValues;
  private static final int defaultWidth = 270; // width dei campi
  private static final int labelWidth = 170;
  private static final int padding = 0;
  Button saveButton;
  Button closeButton;
  boolean hasPk = false;

  /**
   * @return the resultset
   */
  public ResultsetImproved getResultset() {
    return resultset;
  }

  /**
   * @param resultset
   *          the resultset to set
   */
  public void setResultset(ResultsetImproved resultset) {
    this.resultset = resultset;
  }

  /**
   * @return the searchParams
   */
  public SearchParams getSearchParams() {
    return searchParams;
  }

  /**
   * @param searchParams
   *          the searchParams to set
   */
  public void setSearchParams(SearchParams searchParams) {
    this.searchParams = searchParams;
  }

  /**
   * @return the gridStore
   */
  public ListStore<BaseModelData> getGridStore() {
    return gridStore;
  }

  /**
   * @param gridStore
   *          the gridStore to set
   */
  public void setGridStore(ListStore<BaseModelData> gridStore) {
    this.gridStore = gridStore;
  }

  /**
   * 
   */
  public MassiveUpdateDialog() {

  }

  public MassiveUpdateDialog(SearchParams searchparams,
      ListStore<BaseModelData> gridStore, ResultsetImproved resultset) {
    // TODO Auto-generated constructor stub

    setResultset(resultset);
    setGridStore(gridStore);
    setSearchParams(searchparams);

    setHeading("Modifica Massiva");
    setModal(true);
    setSize(600, 400);
    setMaximizable(true);
    setToolTip("Modifica massiva record...");

    setSize(650, 550);
    setPlain(true);

    setLayout(new FitLayout());

    formPanel = new FormPanel();
    formPanel = new FormPanel();
    formPanel.setBodyBorder(false);
    formPanel.setLabelWidth(350);
    formPanel.setHeaderVisible(false);
    formPanel.setScrollMode(Scroll.AUTO);    
    
    setFormPanel(searchparams);

    add(formPanel);
    setButtons();
    
    if (gridStore.getCount() == 0) {
      MessageBox.alert("Errore", "Effettuare prima una ricerca", null);
    } else
      show();
  }

  private void setFormPanel(SearchParams searchparams) {

    // System.out.println("resultset coinvolto: " + this.resultset.getName());
    // System.out.println("numero campi di " + this.resultset.getName() + ": "
    // + this.resultset.getFields().size());
    this.fieldList = new ArrayList<Field>();
    // System.out.println("numero campi per il rs: " +
    // resultset.getFields().size());
    for (ResultsetField field : this.resultset.getFields()) {

      List values = new ArrayList();
      Field f = null;

      if (field.getIsPK()) {
        primaryKey = field;
        hasPk = true;
        primaryKeyValues = new ArrayList<String>();
        
        for (BaseModelData m : gridStore.getModels()) {
          primaryKeyValues.add((String) m.get(field.getName()));
          System.out.println("aggiunto valore di pk ("+field.getName()+"): "+ (String) m.get(field.getName()));
        }
      }

      if (field.getModifyperm()) {

        f = FieldCreator.getField(field, values, 0, true);

      } else {
        f = new TextField<String>();
        // f.setValue("");
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

      final Field res = f;
      res.setEnabled(false);

      CheckBox check = new CheckBox("abilita");
      check.setName(f.getName() + "-combo");
      check.setValue(false);
      check.addClickHandler(new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
          // TODO Auto-generated method stub
          boolean checked = ((CheckBox) event.getSource()).getValue();
          if (checked) {
            enableField(res);
          } else
            disableField(res);

        }
      });

      if (!field.getIsPK()) {
        fieldSet.add(check);
      }

      // this.fieldList.add(f);

    }
  }

  private void disableField(Field res) {
    // TODO Auto-generated method stub
    res.setEnabled(false);
    this.fieldList.remove(res);
    System.out.println("RIMOSSO dalla lista campo " + res.getName());
    System.out.println("lunghezza lista: " + fieldList.size());
  }

  private void enableField(Field res) {
    // TODO Auto-generated method stub
    res.setEnabled(true);
    this.fieldList.add(res);
    System.out.println("aggiunto alla lista campo " + res.getName());
    System.out.println("lunghezza lista: " + fieldList.size());
  }

  private void setButtons() {
    ButtonBar buttonBar = new ButtonBar();

    saveButton =
        new Button("Salva modifica massiva",
            new SelectionListener<ButtonEvent>() {

              @Override
              public void componentSelected(ButtonEvent ce) {
                // TODO Auto-generated method stub
                if (!hasPk) {
                  MessageBox.alert(
                      "Errore!",
                      "Attenzione! Questo RS non risulta avere una primary key: modifica massiva impossibile",
                      null);
                } else {
                  MassiveUpdateObject muo = new MassiveUpdateObject();
                  muo.setFieldName(primaryKey.getName());
                  muo.setResultsetId(searchParams.getResultsetId());
                  muo.setPrimaryKeyValues(primaryKeyValues);
                  for (Field f : fieldList) {
                    System.out.println("nuovo valore per il campo "
                        + f.getName() + ": " + f.getValue().toString());
                    muo.getNewValues().set(f.getName(), f.getValue().toString());
                  }

                  Dispatcher.forwardEvent(EventList.MassUpdate, muo);                  
                }
              }
            });

    buttonBar.add(saveButton);
    
    closeButton = new Button("Chiudi", new SelectionListener<ButtonEvent>() {

      @Override
      public void componentSelected(ButtonEvent ce) {
        // TODO Auto-generated method stub
        Dispatcher.forwardEvent(EventList.Search, searchParams);
        close();
      }
    });
    
    buttonBar.add(closeButton);
    formPanel.setBottomComponent(buttonBar);
  }

  /**
   * @return the primaryKey
   */
  public ResultsetField getPrimaryKey() {
    return primaryKey;
  }

  /**
   * @param primaryKey
   *          the primaryKey to set
   */
  public void setPrimaryKey(ResultsetField primaryKey) {
    this.primaryKey = primaryKey;
  }

  /**
   * @return the primaryKeyValues
   */
  public List getPrimaryKeyValues() {
    return primaryKeyValues;
  }

  /**
   * @param primaryKeyValues
   *          the primaryKeyValues to set
   */
  public void setPrimaryKeyValues(List primaryKeyValues) {
    this.primaryKeyValues = primaryKeyValues;
  }
}
