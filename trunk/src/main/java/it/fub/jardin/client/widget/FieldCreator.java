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

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.model.ForeignKey;
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.tools.FieldDataType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.google.gwt.i18n.client.DateTimeFormat;

public class FieldCreator {

  // public static Field<?> getField(final ResultsetField field,
  // final List<String> values, final boolean combo, final boolean textarea) {
  // return getField(field, values, combo, 0, textarea);
  // }

  /**
   * Crea una widget per un campo a partire da un campo di un resultset.
   * Restituisce un combo con in valori di values se combo = true. USATO PER
   * L'AUTOCOMPLETAMENTO IN RICERCA
   * 
   * @param field
   *          il campo del resultset da disegnare
   * @param values
   *          i valori da inserire nel combo
   * @param combo
   *          settare a true se si vuole un combo, false altrimenti
   * @param labelWidth
   * @return una widget per la gestione del campo del resultset
   */
  public static Field<?> getField(final ResultsetField field,
      final List<String> values, final int labelWidth, final boolean textarea) {
    Field<?> result = null;

    String fieldType = field.getSpecificType();

    if (fieldType.compareToIgnoreCase(FieldDataType.BOOLEAN) == 0) {
      CheckBox f = new CheckBox();
      result = f;
    } else if (fieldType.compareToIgnoreCase(FieldDataType.DATE) == 0) {
      DateField f = new DateField();
      f.getPropertyEditor().setFormat(DateTimeFormat.getFormat("yyyy-MM-dd"));
      result = f;
    } else if (fieldType.compareToIgnoreCase(FieldDataType.DATETIME) == 0) {
      final DateField f = new DateField();
      f.getPropertyEditor().setFormat(
          DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss"));
      result = f;
    }/*
      * else if (fieldType.compareToIgnoreCase("INT") == 0) { NumberField f =
      * new NumberField(); f.setFormat(NumberFormat.getFormat("#")); result = f;
      * }
      */else if (fieldType.compareToIgnoreCase(FieldDataType.TIME) == 0) {
      TimeField f = new TimeField();
      f.setFormat(DateTimeFormat.getFormat("HH:mm:ss"));
      result = f;
      // Log.debug(field.getName() + ": TIME");
    } else if ((fieldType.compareToIgnoreCase(FieldDataType.TEXT) == 0)
        && textarea) {
      TextArea f = new TextArea();
      // f.setFormat(DateTimeFormat.getFormat("HH:mm"));
      result = f;
    } else if (field.getSpecificType().compareToIgnoreCase(FieldDataType.ENUM) == 0) {
      final SimpleComboBox<String> f = new SimpleComboBox<String>();

      f.add(field.getFixedElements());
      f.setTriggerAction(TriggerAction.ALL);
      Listener<BaseEvent> l = new Listener<BaseEvent>() {

        @Override
        public void handleEvent(BaseEvent be) {
          // TODO Auto-generated method stub
          f.removeAll();
          f.add(field.getFixedElements());

        }

      };
      f.addListener(Events.OnClick, l);
      result = f;
    } else if (field.getSpecificType().compareToIgnoreCase(FieldDataType.CHAR) == 0) {
      TextField<String> f = new TextField<String>();
      f.setMaxLength(field.getLenght());
      f.setMinLength(field.getLenght());
      f.setRawValue(field.getDefaultVAlue());

      result = f;
    } else {
      // Log.debug(field.getName() + ": TEXT");
      TextField<String> f = new TextField<String>();

      result = f;
    }

    result.setName(field.getName());
    if ((labelWidth > 0) && (field.getAlias().length() > labelWidth / 10)) {
      result.setFieldLabel(field.getAlias().substring(0, labelWidth / 10)
          + "...");
      result.setToolTip(field.getAlias());
    } else {
      result.setFieldLabel(field.getAlias());
    }

    return result;

  }

  /**
   * Crea una widget per un campo a partire da un campo di un resultset.
   * Restituisce un combo con in valori di values se combo = true. USATO PER
   * L'AUTOCOMPLETAMENTO IN GRIGLIA E DETTAGLIO
   * 
   * @param field
   *          il campo del resultset da disegnare
   * @param values
   *          i valori da inserire nel combo
   * @param combo
   *          settare a true se si vuole un combo, false altrimenti
   * @param labelWidth
   * @return una widget per la gestione del campo del resultset
   */
  public static Field<?> getField(final ResultsetField field,
      final List<String> values, final int labelWidth, final boolean textarea,
      final String source) {
    Field<?> result = null;

    // String fieldType = field.getType();
    String fieldType = field.getSpecificType();

    if (source.compareToIgnoreCase("searcharea") == 0) {

      // final SimpleComboBox f;
      if (fieldType.compareToIgnoreCase(FieldDataType.INT) == 0) {
        final SimpleComboBox f = new SimpleComboBox<Integer>();
        List<Integer> intVals = new ArrayList<Integer>();
        for (int i = 0; i < values.size(); i++) {
          Integer intVal = Integer.parseInt(values.get(i));
          intVals.add(i, intVal);
        }
        //
        f.add(intVals);
        f.setTriggerAction(TriggerAction.ALL);
        // ///////////////////////
        Listener<BaseEvent> l = new Listener<BaseEvent>() {

          public void handleEvent(final BaseEvent be) {

            SearchParams sObject = new SearchParams(field.getResultsetid());
            List<BaseModelData> searchFieldList =
                new ArrayList<BaseModelData>();
            BaseModelData bmd = new BaseModelData();
            bmd.set("field", field.getName());
            searchFieldList.add(bmd);

            sObject.setFieldsValuesList(searchFieldList);

            f.removeAll();

            AppEvent event = new AppEvent(EventList.GetValuesOfAField);
            event.setData("object", sObject);
            event.setData("source", source);
            Dispatcher.forwardEvent(event);

          }

        };

        f.addListener(Events.OnClick, l);
        // ///////////////////////

        result = f;
      } else if (fieldType.compareToIgnoreCase(FieldDataType.FLOAT) == 0) {
        final SimpleComboBox f = new SimpleComboBox<Float>();
        List<Float> intVals = new ArrayList<Float>();
        for (int i = 0; i < values.size(); i++) {
          Float intVal = Float.parseFloat(values.get(i));
          intVals.add(i, intVal);
        }
        f.add(intVals);
        f.setTriggerAction(TriggerAction.ALL);
        // ///////////////////////
        Listener<BaseEvent> l = new Listener<BaseEvent>() {

          public void handleEvent(final BaseEvent be) {

            SearchParams sObject = new SearchParams(field.getResultsetid());
            List<BaseModelData> searchFieldList =
                new ArrayList<BaseModelData>();
            BaseModelData bmd = new BaseModelData();
            bmd.set("field", field.getName());
            searchFieldList.add(bmd);

            sObject.setFieldsValuesList(searchFieldList);

            f.removeAll();

            AppEvent event = new AppEvent(EventList.GetValuesOfAField);
            event.setData("object", sObject);
            event.setData("source", source);
            Dispatcher.forwardEvent(event);

          }

        };

        f.addListener(Events.OnClick, l);
        // ///////////////////////

        result = f;
      } else if (fieldType.compareToIgnoreCase(FieldDataType.DOUBLE) == 0) {
        final SimpleComboBox f = new SimpleComboBox<Double>();
        List<Double> intVals = new ArrayList<Double>();
        for (int i = 0; i < values.size(); i++) {
          Double intVal = Double.parseDouble(values.get(i));
          intVals.add(i, intVal);
        }
        f.add(intVals);
        f.setTriggerAction(TriggerAction.ALL);
        // ///////////////////////
        Listener<BaseEvent> l = new Listener<BaseEvent>() {

          public void handleEvent(final BaseEvent be) {

            SearchParams sObject = new SearchParams(field.getResultsetid());
            List<BaseModelData> searchFieldList =
                new ArrayList<BaseModelData>();
            BaseModelData bmd = new BaseModelData();
            bmd.set("field", field.getName());
            searchFieldList.add(bmd);

            sObject.setFieldsValuesList(searchFieldList);

            f.removeAll();

            AppEvent event = new AppEvent(EventList.GetValuesOfAField);
            event.setData("object", sObject);
            event.setData("source", source);
            Dispatcher.forwardEvent(event);

          }

        };

        f.addListener(Events.OnClick, l);
        // ///////////////////////

        result = f;
      } else if (fieldType.compareToIgnoreCase(FieldDataType.BOOLEAN) == 0) {
        CheckBox f = new CheckBox();
        // final ComboBox<SimpleBoolean> f = new ComboBox<SimpleBoolean>();
        // // f.setDisplayField("label");
        // ListStore<SimpleBoolean> boxStore = new ListStore<SimpleBoolean>();
        // boxStore.add(new SimpleBoolean("true", 1));
        // boxStore.add(new SimpleBoolean("false", 0));
        // f.setStore(boxStore);
        // f.setTypeAhead(true);
        // f.setTriggerAction(TriggerAction.ALL);
        result = f;
      } else {
        final SimpleComboBox f = new SimpleComboBox<String>();
        // f.setTriggerAction(TriggerAction.ALL);
        // f.setId(f.getName());
        f.add(values);

        f.setTriggerAction(TriggerAction.ALL);
        // ///////////////////////
        Listener<BaseEvent> l = new Listener<BaseEvent>() {

          public void handleEvent(final BaseEvent be) {

            SearchParams sObject = new SearchParams(field.getResultsetid());
            List<BaseModelData> searchFieldList =
                new ArrayList<BaseModelData>();
            BaseModelData bmd = new BaseModelData();
            bmd.set("field", field.getName());
            searchFieldList.add(bmd);

            sObject.setFieldsValuesList(searchFieldList);

            f.removeAll();

            AppEvent event = new AppEvent(EventList.GetValuesOfAField);
            event.setData("object", sObject);
            event.setData("source", source);
            Dispatcher.forwardEvent(event);

          }

        };

        f.addListener(Events.OnClick, l);
        // ///////////////////////

        result = f;
      }
    } else if (field.getForeignKey().compareToIgnoreCase("") != 0) {
      // Log.debug(field.getName() + ": COMBO");

      final SimpleComboBox f;
      if (fieldType.compareToIgnoreCase(FieldDataType.INT) == 0) {
        f = new SimpleComboBox<Integer>();
        // f.setTriggerAction(TriggerAction.ALL);
        // Trasforno la List<String> in List<Integer>
        //
        List<Integer> intVals = new ArrayList<Integer>();

        for (int i = 0; i < values.size(); i++) {
          if (values.get(i) != null) {
            Integer intVal = Integer.valueOf(values.get(i));
            intVals.add(i, intVal);
            // System.out.println("campo: " + f.getName() + i + "->"
            // + f.getClass());
          } else {
            intVals.add(null);
          }
        }
        f.add(intVals);
      } else if (fieldType.compareToIgnoreCase(FieldDataType.FLOAT) == 0) {
        f = new SimpleComboBox<Float>();
        List<Float> intVals = new ArrayList<Float>();
        for (int i = 0; i < values.size(); i++) {
          if (values.get(i) != null) {
            Float intVal = Float.parseFloat(values.get(i));
            intVals.add(i, intVal);
          } else {
            intVals.add(null);
          }
        }
        f.add(intVals);
      } else if (fieldType.compareToIgnoreCase(FieldDataType.DOUBLE) == 0) {
        f = new SimpleComboBox<Double>();
        List<Double> intVals = new ArrayList<Double>();
        for (int i = 0; i < values.size(); i++) {
          if (values.get(i) != null) {
            Double intVal = Double.parseDouble(values.get(i));
            intVals.add(i, intVal);
          } else {
            intVals.add(null);
          }
        }
        f.add(intVals);
      } else {
        f = new SimpleComboBox<String>();
        // System.out.println("stile del combo: " + f.getStyleName());
        f.add(values);
        // System.out.println("campo: "+f.getName()+"->"+f.getClass()+f.get);
      }
      f.setTriggerAction(TriggerAction.ALL);

      Listener<BaseEvent> l = new Listener<BaseEvent>() {

        public void handleEvent(final BaseEvent be) {

          ForeignKey fkObject = new ForeignKey();
          fkObject.setPointingResultsetId(field.getResultsetid());
          fkObject.setPointingFieldName(field.getName());
          fkObject.setPointedTableName(field.getForeignKey().split("\\.")[0]);
          fkObject.setPointedFieldName(field.getForeignKey().split("\\.")[1]);

          // f.removeAll();

          AppEvent event = new AppEvent(EventList.GetValuesOfAField);
          event.setData("object", fkObject);
          event.setData("source", source);
          f.removeAll();
          Dispatcher.forwardEvent(event);

        }

      };

      f.addListener(Events.OnClick, l);

      result = f;

    } else if (fieldType.compareToIgnoreCase(FieldDataType.DATE) == 0) {
      DateField f = new DateField();
      f.getPropertyEditor().setFormat(DateTimeFormat.getFormat("yyyy-MM-dd"));

      result = f;
    } else if (fieldType.compareToIgnoreCase(FieldDataType.BOOLEAN) == 0) {
      // System.out.println("FIELDCREATOR: boleano!!!!");
//      final SimpleComboBox<String> f = new SimpleComboBox<String>();
//
//      f.add(field.getFixedElements());
//      f.setTriggerAction(TriggerAction.ALL);
//      Listener<BaseEvent> l = new Listener<BaseEvent>() {
//
//        @Override
//        public void handleEvent(BaseEvent be) {
//          f.removeAll();
//          f.add(field.getFixedElements());
//        }
//      };
//      f.addListener(Events.OnClick, l);
      
      final SimpleComboBox<Boolean> f = new SimpleComboBox<Boolean>();
//      f.removeAll();
      f.add(new Boolean(Boolean.TRUE));
      f.add(new Boolean(Boolean.FALSE));
      f.setTriggerAction(TriggerAction.ALL);
      Listener<BaseEvent> l = new Listener<BaseEvent>() {

        @Override
        public void handleEvent(BaseEvent be) {
          f.removeAll();
          f.add(new Boolean(Boolean.TRUE));
          f.add(new Boolean(Boolean.FALSE));
        }
      };
      f.addListener(Events.OnClick, l);

      // ListStore<SimpleBoolean> boxStore = new ListStore<SimpleBoolean>();
      // boxStore.add(getSimpleBooleans());
      // // System.out.println("echo " +
      // SimpleBoolean.getSimpleBoobleans().get(0).getName());
      // final ComboBox<SimpleBoolean> f = new ComboBox<SimpleBoolean>();
      // f.setDisplayField("name");
      // f.setEmptyText("sadfasdf...");
      // f.setStore(boxStore);
      // f.setTypeAhead(true);
      // f.setTriggerAction(ComboBox.TriggerAction.ALL);
      // Listener<BaseEvent> l = new Listener<BaseEvent>() {
      //
      // @Override
      // public void handleEvent(BaseEvent be) {
      // System.out.println(f.getStore().getAt(0).getName() +
      // f.getStore().getAt(1).getName());
      // }
      // };
      // f.addListener(Events.OnClick, l);
      result = f;
    } else if (fieldType.compareToIgnoreCase(FieldDataType.DATETIME) == 0) {
      DateField f = new DateField();
      f.getPropertyEditor().setFormat(
          DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss"));
      result = f;
    } else if (fieldType.compareToIgnoreCase(FieldDataType.INT) == 0) {
      NumberField f = new NumberField();
      f.setPropertyEditorType(Integer.class);
      result = f;
    } else if (fieldType.compareToIgnoreCase(FieldDataType.FLOAT) == 0) {
      NumberField f = new NumberField();
      f.setPropertyEditorType(Float.class);
      result = f;
    } else if (fieldType.compareToIgnoreCase(FieldDataType.DOUBLE) == 0) {
      NumberField f = new NumberField();
      f.setPropertyEditorType(Double.class);
      result = f;
    } else if (fieldType.compareToIgnoreCase(FieldDataType.TIME) == 0) {
      TimeField f = new TimeField();
      f.setFormat(DateTimeFormat.getFormat("HH:mm"));
      result = f;
      // Log.debug(field.getName() + ": TIME");
    } else if ((fieldType.compareToIgnoreCase(FieldDataType.TEXT) == 0)
        && textarea) {
      TextArea f = new TextArea();
      // f.setFormat(DateTimeFormat.getFormat("HH:mm"));
      result = f;
    } else if (field.getSpecificType().compareToIgnoreCase(FieldDataType.ENUM) == 0) {
      final SimpleComboBox<String> f = new SimpleComboBox<String>();

      f.add(field.getFixedElements());
      f.setTriggerAction(TriggerAction.ALL);
      Listener<BaseEvent> l = new Listener<BaseEvent>() {

        @Override
        public void handleEvent(BaseEvent be) {
          // TODO Auto-generated method stub
          f.removeAll();
          f.add(field.getFixedElements());

        }

      };
      f.addListener(Events.OnClick, l);
      result = f;
    } else if (field.getSpecificType().compareToIgnoreCase(FieldDataType.CHAR) == 0) {
      TextField<String> f = new TextField<String>();
      f.setMaxLength(field.getLenght());
      f.setMinLength(field.getLenght());
      f.setRawValue(field.getDefaultVAlue());

      result = f;
    } else {
      // Log.debug(field.getName() + ": TEXT");
      TextField<String> f = new TextField<String>();

      result = f;
    }

    result.setName(field.getName());
    if ((labelWidth > 0) && (field.getAlias().length() > labelWidth / 10)) {
      result.setFieldLabel(field.getAlias().substring(0, labelWidth / 10)
          + "...");
      result.setToolTip(field.getAlias());
    } else {
      result.setFieldLabel(field.getAlias());
    }

    return result;
  }



  /*
   * usato nel JardinFormPopup
   */
  public static Field<?> getField(final ResultsetField field,
      Object defaultValue, final String source) {
    int labelwidth = 20;
    Field result;
    String fieldType = field.getSpecificType();
    if (field.getForeignKey().compareToIgnoreCase("") != 0) {
      // Log.debug(field.getName() + ": COMBO");

      final SimpleComboBox f;
      if (fieldType.compareToIgnoreCase(FieldDataType.INT) == 0) {
        f = new SimpleComboBox<Integer>();

        if (defaultValue != null) {
          List<Integer> intVals = new ArrayList<Integer>();
          intVals.add(Integer.parseInt(defaultValue.toString()));
          f.add(intVals);
          f.setSimpleValue(Integer.parseInt(defaultValue.toString()));
        }
      } else if (fieldType.compareToIgnoreCase(FieldDataType.FLOAT) == 0) {
        f = new SimpleComboBox<Float>();
        if (defaultValue != null) {
          List<Float> intVals = new ArrayList<Float>();
          intVals.add(Float.valueOf(defaultValue.toString()));
          f.add(intVals);
          f.setSimpleValue(Float.valueOf(defaultValue.toString()));
        }
      } else if (fieldType.compareToIgnoreCase(FieldDataType.DOUBLE) == 0) {
        f = new SimpleComboBox<Double>();
        if (defaultValue != null) {
          List<Double> intVals = new ArrayList<Double>();
          intVals.add(Double.valueOf(defaultValue.toString()));
          f.add(intVals);
          f.setSimpleValue(Double.valueOf(defaultValue.toString()));
        }
      } else {
        f = new SimpleComboBox<String>();
        if (defaultValue != null) {
          List<String> intVals = new ArrayList<String>();
          intVals.add(defaultValue.toString());
          f.add(intVals);
          f.setSimpleValue(defaultValue.toString());
        }
      }
      f.setTriggerAction(TriggerAction.ALL);

      Listener<BaseEvent> l = new Listener<BaseEvent>() {

        public void handleEvent(final BaseEvent be) {

          ForeignKey fkObject = new ForeignKey();
          fkObject.setPointingResultsetId(field.getResultsetid());
          fkObject.setPointingFieldName(field.getName());
          fkObject.setPointedTableName(field.getForeignKey().split("\\.")[0]);
          fkObject.setPointedFieldName(field.getForeignKey().split("\\.")[1]);

          // f.removeAll();

          AppEvent event = new AppEvent(EventList.GetValuesOfAField);
          event.setData("object", fkObject);
          event.setData("source", source);
          f.removeAll();
          Dispatcher.forwardEvent(event);

        }

      };

      f.addListener(Events.OnClick, l);

      result = f;

    } else if (fieldType.compareToIgnoreCase(FieldDataType.DATE) == 0) {
      DateField f = new DateField();
      f.getPropertyEditor().setFormat(DateTimeFormat.getFormat("yyyy-MM-dd"));
      if (defaultValue != null) {
        f.setValue((Date) defaultValue);
      }
      result = f;
      // Log.debug(field.getName() + ": DATE");
    } else if (fieldType.compareToIgnoreCase(FieldDataType.BOOLEAN) == 0) {
      // System.out.println("FIELDCREATOR: boleano!!!!");
      CheckBox f = new CheckBox();
      if (defaultValue != null) {

        if (defaultValue.toString().compareToIgnoreCase("true") == 0
            || defaultValue.toString().compareToIgnoreCase("1") == 0) {
          f.setValue(Boolean.TRUE);
        } else
          f.setValue(Boolean.FALSE);
      }
      result = f;
    } else if (fieldType.compareToIgnoreCase(FieldDataType.DATETIME) == 0) {
      DateField f = new DateField();
      f.getPropertyEditor().setFormat(
          DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss"));
      if (defaultValue != null) {
        java.util.Date date = new java.util.Date();
        date.setTime(Long.parseLong(defaultValue.toString()));
        f.setValue(date);
      }
      result = f;
    } else if (fieldType.compareToIgnoreCase(FieldDataType.INT) == 0) {
      NumberField f = new NumberField();
      f.setPropertyEditorType(Integer.class);
      if (defaultValue != null) {
        f.setValue(Integer.parseInt(defaultValue.toString()));
      }
      result = f;
    } else if (fieldType.compareToIgnoreCase(FieldDataType.FLOAT) == 0) {
      NumberField f = new NumberField();
      f.setPropertyEditorType(Float.class);
      if (defaultValue != null) {
        f.setValue(Float.parseFloat(defaultValue.toString()));
      }
      result = f;
    } else if (fieldType.compareToIgnoreCase(FieldDataType.DOUBLE) == 0) {
      NumberField f = new NumberField();
      f.setPropertyEditorType(Double.class);
      if (defaultValue != null) {
        f.setValue(Double.parseDouble(defaultValue.toString()));
      }
      result = f;
    } else if (fieldType.compareToIgnoreCase(FieldDataType.TIME) == 0) {
      TimeField f = new TimeField();
      Time defVal = new Time();
      f.setFormat(DateTimeFormat.getFormat("HH:mm"));
      if (defaultValue != null) {
        int hours = Integer.parseInt(defaultValue.toString().substring(0, 2));
        int mins = Integer.parseInt(defaultValue.toString().substring(3, 5));
        defVal.setHour(hours);
        defVal.setMinutes(mins);

        defVal = ((TimeField) f).findModel(hours, mins);

        ((TimeField) f).select(defVal);
      }

      result = f;
      // Log.debug(field.getName() + ": TIME");
    } else if ((fieldType.compareToIgnoreCase(FieldDataType.TEXT) == 0)
        && field.getLenght() > 20) {
      TextArea f = new TextArea();
      // f.setFormat(DateTimeFormat.getFormat("HH:mm"));
      if (defaultValue != null) {
        f.setValue(defaultValue.toString());
      }
      result = f;
    } else if (field.getSpecificType().compareToIgnoreCase(FieldDataType.ENUM) == 0) {
      final SimpleComboBox<String> f = new SimpleComboBox<String>();

      f.add(field.getFixedElements());
      f.setTriggerAction(TriggerAction.ALL);
      Listener<BaseEvent> l = new Listener<BaseEvent>() {

        @Override
        public void handleEvent(BaseEvent be) {
          // TODO Auto-generated method stub
          f.removeAll();
          f.add(field.getFixedElements());

        }

      };
      f.addListener(Events.OnClick, l);
      f.setSimpleValue(defaultValue.toString());
      result = f;
    } else if (field.getSpecificType().compareToIgnoreCase(FieldDataType.CHAR) == 0) {
      TextField<String> f = new TextField<String>();
      f.setMaxLength(field.getLenght());
      f.setMinLength(field.getLenght());
      f.setRawValue(field.getDefaultVAlue());
      if (defaultValue != null) {
        f.setValue(defaultValue.toString());
      }
      result = f;
    } else {
      // Log.debug(field.getName() + ": TEXT");
      TextField<String> f = new TextField<String>();
      if (defaultValue != null) {
        f.setValue(defaultValue.toString());
      }
      result = f;
    }

    result.setName(field.getName());
    // System.out.println("aggiunto campo: "+field.getName());
    // if ((labelWidth > 0) && (field.getAlias().length() > labelWidth / 10)) {
    // result.setFieldLabel(field.getAlias().substring(0, labelWidth / 10)
    // + "...");
    result.setToolTip(field.getAlias());
    // } else {
    result.setFieldLabel(field.getAlias());
    // }

    return result;

  }
}
