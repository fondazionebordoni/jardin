/**
 * 
 */
package it.fub.jardin.client.widget;

import it.fub.jardin.client.Jardin;
import it.fub.jardin.client.ManagerServiceAsync;
import it.fub.jardin.client.model.ResultsetField;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author acozzolino
 */
public class JardinColumnConfig extends ColumnConfig {

  private int groupingId;
  private int fieldId;
  private boolean isKey;
  private String foreignKey;

  /**
   * Creazione di un ColumnConfig che rispecchia le caratteristiche del campo
   * del resultset
   * 
   * @param field
   *          il campo del resultset contenuto nella colonna
   * @param values
   *          i valori assumibili
   */
  public JardinColumnConfig(ResultsetField field, List<String> values) {
    // public JardinColumnConfig(ResultsetField field) {
    // TODO inserire larghezza campo di default in field?
    super(field.getName(), field.getAlias(), 120);
    this.groupingId = field.getIdgrouping();
    this.fieldId = field.getId();
    this.setKey(field.getIsPK());

    /* Gestione modifica del campo */
    if (field.getModifyperm()) {
      // final Field f = FieldCreator.getField(field, values, true);
      final Field f = FieldCreator.getField(field, values, 0, false);

      CellEditor editor = null;
      if (f instanceof SimpleComboBox) {
        ((SimpleComboBox) f).setEditable(false);
        editor = new CellEditor((SimpleComboBox) f) {
          @Override
          public Object preProcessValue(Object value) {
            if (value == null) {
              return value;
            }
            return ((SimpleComboBox) f).findModel(value.toString());
          }

          @Override
          public Object postProcessValue(Object value) {
            if (value == null) {
              return value;
            }
            return ((BaseModelData) value).get("value");
          }
        };
      } else {
        editor = new CellEditor(f);
      }

      this.setEditor(editor);
    } else {
      this.setEditor(null);
    }

    /* Gestione personalizzazione in base al tipo di campo */
    String type = field.getType();
    if ((type.compareToIgnoreCase("INT") == 0)
        || (type.compareToIgnoreCase("DATE") == 0)
        || (type.compareToIgnoreCase("DATETIME") == 0)
        || (type.compareToIgnoreCase("TIME") == 0)) {
      this.setAlignment(HorizontalAlignment.RIGHT);
    }

    // if ((type.compareToIgnoreCase("DATE") == 0)
    // || (type.compareToIgnoreCase("DATETIME") == 0)
    // || (type.compareToIgnoreCase("TIME") == 0)) {
    // this.setDateTimeFormat(DateTimeFormat.getMediumDateFormat());
    // }

    this.setResizable(true);
    this.setHidden(!field.getVisible());
  }

  public int getGroupingId() {
    return groupingId;
  }

  /**
   * @param fieldId
   *          the fieldId to set
   */
  public void setFieldId(int fieldId) {
    this.fieldId = fieldId;
  }

  /**
   * @return the fieldId
   */
  public int getFieldId() {
    return fieldId;
  }

  /**
   * @param isKey
   *          the isKey to set
   */
  public void setKey(boolean isKey) {
    this.isKey = isKey;
  }

  /**
   * @return the isKey
   */
  public boolean isKey() {
    return isKey;
  }

}