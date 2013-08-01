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
import it.fub.jardin.client.tools.FieldDataType;

import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.google.gwt.i18n.client.DateTimeFormat;

public class JardinColumnConfig extends ColumnConfig {

  private final int groupingId;
  private int fieldId;
  private boolean isKey;
  private boolean isUnique;
  private final String source = "grid";

  /**
   * Creazione di un ColumnConfig che rispecchia le caratteristiche del campo
   * del resultset
   * 
   * @param field
   *          il campo del resultset contenuto nella colonna
   * @param values
   *          i valori assumibili
   */
  public JardinColumnConfig(final ResultsetField field,
      final List<String> values) {
    // public JardinColumnConfig(ResultsetField field) {
    // TODO inserire larghezza campo di default in field?
    super(field.getName(), field.getAlias(), 120);
    this.groupingId = field.getIdgrouping();
    this.fieldId = field.getId();
    this.setKey(field.getIsPK());
    this.setUnique(field.isUnique());

    final Field f = FieldCreator.getField(field, values, 0, false, source);
    /* Gestione modifica del campo */

    // final Field f = FieldCreator.getField(field, values, true);

    // final Field<?> f = FieldCreator.getField(field, values, 0, true);

    CellEditor editor = null;

    if (f instanceof SimpleComboBox) {
      if (field.getSpecificType().compareToIgnoreCase(FieldDataType.INT) == 0) {
        ((SimpleComboBox) f).setEditable(false);
        editor = new CellEditor(f) {
          @Override
          public Object preProcessValue(final Object value) {
            if (value == null) {
              return value;
            }
            return ((SimpleComboBox) f).findModel(value);
          }

          @Override
          public Object postProcessValue(final Object value) {
            if (value == null) {
              return value;
            }
            return ((BaseModelData) value).get("value");
          }
        };
      } else if (field.getSpecificType().compareToIgnoreCase(FieldDataType.TIME) == 0) {
        editor = new CellEditor((TimeField) f) {
          @Override
          public Object preProcessValue(final Object value) {
            if (value == null) {
              return value;
            }
            return ((TimeField) f).findModel((Date) value);
          }

          @Override
          public Object postProcessValue(final Object value) {
            if (value == null) {
              return value;
            }
            return ((BaseModelData) value).get("value");
          }
        };
      } else {
        ((SimpleComboBox) f).setEditable(false);
        editor = new CellEditor(f) {
          @Override
          public Object preProcessValue(final Object value) {
            if (value == null) {
              return value;
            }
            return ((SimpleComboBox) f).findModel(value.toString());
          }

          @Override
          public Object postProcessValue(final Object value) {
            if (value == null) {
              return value;
            }
            return ((BaseModelData) value).get("value");
          }
        };
      }
    } else if (f instanceof DateField) {
      editor = new CellEditor((DateField) f);
      if (field.getSpecificType().compareToIgnoreCase(FieldDataType.DATETIME) == 0) {
        this.setDateTimeFormat(DateTimeFormat.getFormat("dd MMM yyyy HH:mm:ss"));
        ((DateField) f).getPropertyEditor().setFormat(DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss"));
      } else if (field.getSpecificType().compareToIgnoreCase(FieldDataType.DATE) == 0) {
        this.setDateTimeFormat(DateTimeFormat.getFormat("dd MMM yyyy"));
        ((DateField) f).getPropertyEditor().setFormat(DateTimeFormat.getFormat("yyyy-MM-dd"));
      }

    } else if (f instanceof TimeField) {
      editor = new CellEditor((TimeField) f);
      this.setDateTimeFormat(DateTimeFormat.getFormat("HH:mm"));
    } else if (f instanceof CheckBox) {
      editor = new CellEditor((CheckBox) f);      
    } else {
      editor = new CellEditor(f);
    }

    this.setEditor(editor);
    if (field.getModifyperm()) {
      f.enable();
    } else {
      // this.setEditor(null);
      f.disable();
    }

    /* Gestione personalizzazione in base al tipo di campo */
    // String type = field.getType();
    // if ((type.compareToIgnoreCase("INT") == 0)
    // || (type.compareToIgnoreCase("DATE") == 0)
    // || (type.compareToIgnoreCase("DATETIME") == 0)
    // || (type.compareToIgnoreCase("TIME") == 0)) {
    // this.setAlignment(HorizontalAlignment.RIGHT);
    // }

    this.setResizable(true);
    this.setHidden(!field.getVisible());
  }

  public int getGroupingId() {
    return this.groupingId;
  }

  /**
   * @param fieldId
   *          the fieldId to set
   */
  public void setFieldId(final int fieldId) {
    this.fieldId = fieldId;
  }

  /**
   * @return the fieldId
   */
  public int getFieldId() {
    return this.fieldId;
  }

  /**
   * @param isKey
   *          the isKey to set
   */
  public void setKey(final boolean isKey) {
    this.isKey = isKey;
  }

  /**
   * @return the isKey
   */
  public boolean isKey() {
    return this.isKey;
  }

  /**
   * @param isKey
   *          the isKey to set
   */
  public void setUnique(final boolean isUnique) {
    this.isUnique = isUnique;
  }

  /**
   * @return the isUnique
   */
  public boolean isUnique() {
    return this.isUnique;
  }

}
