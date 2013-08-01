/**
 * 
 */
package it.fub.jardin.client.widget;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.model.NewObjects;
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetFieldGroupings;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.tools.FieldDataType;
import it.fub.jardin.client.tools.PopupOperations;

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

/**
 * @author acozzolino
 * 
 */
public class JardinFormPopup extends Window {

  private String source;
  List<Field<?>> fieldList = new ArrayList<Field<?>>();

  private FormPanel formPanel;
  private static final int defaultWidth = 270; // width dei campi
  private static final int labelWidth = 240;
  private static final int padding = 0;

  Button button;
  // SearchParams searchData;
  private ResultsetImproved resultset;
  private BaseModelData record = null;
  private String username;

  HashMap<String, FieldSet> fieldSetList = new HashMap<String, FieldSet>();

  public enum operations {
    ADDRECORD, MODRECORD, ADDFKRECORD
  };

  /**
   * 
   */
  public JardinFormPopup(ResultsetImproved resultset,
      BaseModelData fieldsAndValues, String foreignKey, String username,
      String operation) {

    this.setSize(650, 550);
    this.setPlain(true);
    this.setLayout(new FitLayout());

    this.resultset = resultset;
    this.record = fieldsAndValues;
    this.username = username;
    /* Creazione FormPanel */
    this.formPanel = new FormPanel();
    this.formPanel.setBodyBorder(false);
    this.formPanel.setLabelWidth(350);
    this.formPanel.setHeaderVisible(false);
    this.formPanel.setScrollMode(Scroll.AUTO);

    // TODO Auto-generated constructor stub
    if (operation.compareToIgnoreCase(PopupOperations.ADDRECORD) == 0) {
      setHeading("Aggiungi record in " + resultset.getAlias());
      setSource("addingrowpopup");
      createAddRowPopup();
      this.setButtons(PopupOperations.ADDRECORD);
    } else if (operation.compareToIgnoreCase(PopupOperations.MODRECORD) == 0) {
      setHeading("Modifica dati in " + resultset.getAlias());
      setSource("editorpopup");
      createAddRowPopup();
//      createModRowPopup();
      this.setButtons(PopupOperations.MODRECORD);
    } else if (operation.compareToIgnoreCase(PopupOperations.ADDFKRECORD) == 0) {
      setHeading("Dettaglio record in " + resultset.getAlias());
      setSource("detailpopup");
      createAddRowPopup();
//      createModRowPopup();
      this.setButtons(PopupOperations.ADDFKRECORD);
    }

    this.add(this.formPanel);

    this.show();

  }



  private void createAddRowPopup() {
    // TODO Auto-generated method stub
    for (ResultsetField field : this.resultset.getFields()) {
//      System.out.println("campo " + field.getName() + " con valore " + record.get(field.getName()));
      Field PF = FieldCreator.getField(field, record.get(field.getName()), source);

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
    }
  }

  /**
   * @return the source
   */
  public String getSource() {
    return source;
  }

  /**
   * @param source
   *          the source to set
   */
  public void setSource(String source) {
    this.source = source;
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
   * @return the record
   */
  public BaseModelData getRecord() {
    return record;
  }

  /**
   * @param record
   *          the record to set
   */
  public void setRecord(BaseModelData record) {
    this.record = record;
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username
   *          the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  private void setButtons(final String op) {
    ButtonBar buttonBar = new ButtonBar();
    this.button = new Button("Submit", new SelectionListener<ButtonEvent>() {

      @Override
      public void componentSelected(final ButtonEvent ce) {

        List<BaseModelData> newItemList = new ArrayList<BaseModelData>();
        BaseModelData newItem = new BaseModelData();
        for (Field<?> field : JardinFormPopup.this.fieldList) {

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

        if (op.compareToIgnoreCase(PopupOperations.ADDRECORD) == 0) {
          AppEvent event = new AppEvent(EventList.saveNewRecord);
          event.setData("object", newItemList);
          event.setData("resultsetid", resultset.getId());
          Dispatcher.forwardEvent(event);
        } else if (op.compareToIgnoreCase(PopupOperations.MODRECORD) == 0) {
          NewObjects no = new NewObjects(resultset.getId(), newItemList);
          AppEvent event = new AppEvent(EventList.UpdateObjects);
          event.setData(no);
          Dispatcher.forwardEvent(event);
        } else if (op.compareToIgnoreCase(PopupOperations.ADDFKRECORD) == 0) {
          Dispatcher.forwardEvent(EventList.UpdateObjects, new NewObjects(
              resultset.getId(), newItemList));
        }

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
