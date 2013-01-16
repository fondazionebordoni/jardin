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

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * @author acozzolino
 * 
 */
public class MassiveUpdateDialog extends Window {

  private ResultsetImproved resultset;
  private List<BaseModelData> gridStore;
  private SearchParams searchParams;
  private FormPanel formPanel;
  private List<Field> fieldList;
  private HashMap<String, SimpleFieldSet> fieldSetList =
      new HashMap<String, SimpleFieldSet>();
  private ResultsetField primaryKey;
  private List<String> primaryKeyValues;
  private static final int defaultWidth = 270; // width dei campi
  private static final int labelWidth = 170;
  private static final int padding = 0;
  Button saveButton;
  Button closeButton;
  boolean hasPk = false;


  public MassiveUpdateDialog(List<BaseModelData> gridStore, SearchParams searchParams, ResultsetImproved resultset) {
    // TODO Auto-generated constructor stub

    setResultset(resultset);
    setGridStore(gridStore);
    setSearchParams(searchParams);

    setHeading("Modifica Massiva");
    setModal(true);
    setMaximizable(true);
    setToolTip("Modifica massiva record...");

    setSize(700, 550);
    setPlain(true);

    setLayout(new FitLayout());

    formPanel = new FormPanel();
    formPanel.setFrame(true);
    formPanel.setBodyBorder(false);
    formPanel.setLabelWidth(350);
    formPanel.setHeaderVisible(false);
    formPanel.setLabelAlign(LabelAlign.LEFT);
    formPanel.setButtonAlign(HorizontalAlignment.CENTER);
    formPanel.setScrollMode(Scroll.AUTO);
    formPanel.setPadding(5);

    setFormPanel();

    add(formPanel);
    setButtons();

    if (getGridStore().size() == 0) {
      MessageBox.alert("Errore", "Effettuare prima una ricerca", null);
    } else
      show();
  }

  private void setFormPanel() {

    fieldList = new ArrayList<Field>();

    SimpleFieldSet cp = new SimpleFieldSet("info");
    cp.setCollapsible(true);
    // cp.setIcon(IconHelper.createStyle("icon-table-update"));
    // cp.setStyleName("alert-massupdate-message");

    formPanel.add(cp);

    String labelText =
        "Modifica di massa dei record corrispondenti alla ricerca appena effettuata:<br>";
    for (ResultsetField field : this.resultset.getFields()) {

      // ////////// creazione field
      List values = new ArrayList();
      Field f = null;

      if (field.getIsPK()) {
        primaryKey = field;
        hasPk = true;
        primaryKeyValues = new ArrayList<String>();

        if (gridStore != null) {
          for (BaseModelData m : gridStore) {
            primaryKeyValues.add((String) m.get(field.getName()));
            // System.out.println("aggiunto valore di pk (" + field.getAlias()
            // + "): " + (String) m.get(field.getName()));
            labelText += field.getName() + "=" + m.get(field.getName()) + "|";
          }
        }
      }

      if (field.getModifyperm()) {

        f = FieldCreator.getField(field, values, 0, true);

      } else {
        f = new TextField<String>();
        // f.setValue("");
        f.setEnabled(false);
      }

      // f.setFieldLabel(field.getAlias());
      f.setName(field.getName());

      // ////////////

      /* Esamino il raggruppamento a cui appartiene il campo */
      ResultsetFieldGroupings fieldGrouping =
          this.resultset.getFieldGrouping(field.getIdgrouping());

      String fieldSetName = fieldGrouping.getName();

      /*
       * Se il fieldset non esiste lo creo e l'aggancio a pannello
       */
      SimpleFieldSet fieldSet = this.fieldSetList.get(fieldSetName);
      if (fieldSet == null) {
        fieldSet =
            new SimpleFieldSet(fieldGrouping.getAlias(), defaultWidth,
                labelWidth, padding);
        FlexTable table = new FlexTable();
        table.setVisible(true);
        fieldSet.setTable(table);

        fieldSet.add(fieldSet.getTable());

        this.fieldSetList.put(fieldSetName, fieldSet);
        this.formPanel.add(fieldSet);
      }

      final Field res = f;
      res.setEnabled(false);

      CheckBox check = new CheckBox("abilita alla modifica");
      check.setName(field.getName() + "-combo");
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

      Label fieldLabel = new Label(field.getAlias() + ": ");
      fieldSet.getTable().setWidget(this.resultset.getFields().indexOf(field),
          0, fieldLabel);
      fieldSet.getTable().setWidget(this.resultset.getFields().indexOf(field),
          1, f);

      if (!field.getIsPK()) {
        fieldSet.getTable().setWidget(
            this.resultset.getFields().indexOf(field), 2, check);
      }

    }

    // Dialog mainLabel = new Dialog();
    HtmlContainer html = new HtmlContainer();
    html.setHtml(labelText);
    // mainLabel.add(html);

    // MessageBox.info("Modifica simultanea di più record", labelText, null);
    cp.add(html);
    // mainLabel.okText = "ho capito";
    // add(mainLabel);
    // mainLabel.show();

  }

  private void disableField(Field res) {
    // TODO Auto-generated method stub
    res.setEnabled(false);
    this.fieldList.remove(res);
    // System.out.println("RIMOSSO dalla lista campo " + res.getName());
    // System.out.println("lunghezza lista: " + fieldList.size());
  }

  private void enableField(Field res) {
    // TODO Auto-generated method stub
    res.setEnabled(true);
    this.fieldList.add(res);
    // System.out.println("aggiunto alla lista campo " + res.getName());
    // System.out.println("lunghezza lista: " + fieldList.size());
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
                  muo.setResultsetId(resultset.getId());
                  muo.setPrimaryKeyValues(primaryKeyValues);

                  for (Field<?> f : fieldList) {
//                     System.out.println("nuovo valore per il campo "
//                     + f.getName() + ": " + f.getValue().toString());
                     Object value = null;
                     if (f instanceof SimpleComboBox<?>) {
                       if (f.getValue() == null) {
                         value = "";
                       } else {
                         SimpleComboValue<?> scv = (SimpleComboValue<?>) f.getValue();
                         value = scv.getValue().toString();
                       }
                     } else if (f instanceof ComboBox<?>) {
                       if (f.getValue() == null) {
                         value = "";
                       } else {
                         value = ((BaseModelData) f.getValue()).get(f.getName());
                       }
                     } else {
                       value = f.getValue();
                     }
                     
                    muo.getNewValues().set(f.getName(), value);
                  }

                  Dispatcher.forwardEvent(EventList.MassUpdate, muo);
//                  hide();
                }
              }
            });

    buttonBar.add(saveButton);

    closeButton = new Button("Chiudi", new SelectionListener<ButtonEvent>() {

      @Override
      public void componentSelected(ButtonEvent ce) {
        // TODO Auto-generated method stub
        Dispatcher.forwardEvent(EventList.Search, searchParams);
        hide();
      }
    });

    buttonBar.add(closeButton);
    formPanel.setBottomComponent(buttonBar);
  }

  

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
  public List<BaseModelData> getGridStore() {
    return gridStore;
  }

  /**
   * @param gridStore
   *          the gridStore to set
   */
  public void setGridStore(List<BaseModelData> gridStore) {
    this.gridStore = gridStore;
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
