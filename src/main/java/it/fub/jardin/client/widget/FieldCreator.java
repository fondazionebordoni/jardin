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
import it.fub.jardin.client.Jardin;
import it.fub.jardin.client.ManagerServiceAsync;
import it.fub.jardin.client.model.ForeignKey;
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.tools.FieldDataType;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.charts.client.model.Text;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FieldCreator {

  public static Field<?> getField(final ResultsetField field,
      final List<String> values, final boolean combo, final boolean textarea) {
    return getField(field, values, combo, 0, textarea);
  }

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
  public static/* TODO deve restituire <T extends Field> al posto di Field */Field<?> getField(
      final ResultsetField field, final List<String> values,
      final boolean combo, final int labelWidth, final boolean textarea) {
    
    final String source = "searcharea";
    Field<?> result = null;
    String fieldType = field.getType();
    /* Se il campo è una data non creo un combo */
    if (fieldType.compareToIgnoreCase("DATE") == 0) {
      DateField f = new DateField();
      f.getPropertyEditor().setFormat(DateTimeFormat.getFormat("dd/MM/y"));
      result = f;
    } else if (fieldType.compareToIgnoreCase("DATETIME") == 0) {
      DateField f = new DateField();
      f.getPropertyEditor().setFormat(
          DateTimeFormat.getFormat("dd/MM/y HH:mm:ss"));
      result = f;
    } /*
       * else if (fieldType.compareToIgnoreCase("INT") == 0) { NumberField f =
       * new NumberField(); f.setFormat(NumberFormat.getFormat("#")); result =
       * f;
       * 
       * }
       */else if (fieldType.compareToIgnoreCase("TIME") == 0) {
      TimeField f = new TimeField();
      f.setFormat(DateTimeFormat.getFormat("HH:mm"));
      result = f;
    } else if (((fieldType.compareToIgnoreCase("BLOB") == 0) || (fieldType.compareToIgnoreCase("TEXT") == 0))
        && textarea) {
      TextArea f = new TextArea();
      // f.setHeight(5);
      // f.setFormat(DateTimeFormat.getFormat("HH:mm"));
      result = f;
    } else {
      if (combo) {
        // if (values != null && values.size() > 0) {
        if (values != null) {
          final SimpleComboBox f;
          if ((fieldType.compareToIgnoreCase("int") == 0)
              || (fieldType.compareToIgnoreCase("real") == 0)) {
            f = new SimpleComboBox<Integer>();
            f.setTriggerAction(TriggerAction.ALL);
//            f.setId(f.getName());
            // Trasforno la List<String> in List<Integer>
            //
            List<Integer> intVals = new ArrayList<Integer>();
            for (int i = 0; i < values.size(); i++) {
              Integer intVal = Integer.parseInt(values.get(i));
              intVals.add(i, intVal);
            }
            f.add(intVals);
          } else {
            f = new SimpleComboBox<String>();
            f.setTriggerAction(TriggerAction.ALL);
//            f.setId(f.getName());
            f.add(values);
          }

          // ///////////////////////
          Listener<BaseEvent> l = new Listener<BaseEvent>() {

            public void handleEvent(final BaseEvent be) {
              
              SearchParams sObject = new SearchParams(field.getResultsetid());
              List<BaseModelData> searchFieldList = new ArrayList<BaseModelData>();
              BaseModelData bmd = new BaseModelData();              
              bmd.set("field", field.getName());
              searchFieldList.add(bmd);
              
              sObject.setFieldsValuesList(searchFieldList);
              
//              f.removeAll();
              
              AppEvent event = new AppEvent(EventList.GetValuesOfAField);
              event.setData("object", sObject);
              event.setData("source", source);
              Dispatcher.forwardEvent(event);

            }

          };

          f.addListener(Events.OnClick, l);
          // ///////////////////////

          result = f;
        } else {
          result = new TextField<String>();
        }

      } else {
        result = new TextField<String>();
      }
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
      final List<String> values, final int labelWidth, final boolean textarea, final String source) {
    Field<?> result = null;
    
    /////////////////
//    field.doTypeAnalisys();
    /////////////////
    String fieldType = field.getType();

    /* Se il campo è una data non creo un combo */
    if (fieldType.compareToIgnoreCase("DATE") == 0) {
      DateField f = new DateField();
      f.getPropertyEditor().setFormat(DateTimeFormat.getFormat("dd/MM/y"));

      result = f;
      // Log.debug(field.getName() + ": DATE");
    } else if (fieldType.compareToIgnoreCase("DATETIME") == 0) {
      DateField f = new DateField();
      f.getPropertyEditor().setFormat(
          DateTimeFormat.getFormat("dd/MM/y HH:mm:ss"));
      result = f;
    }/*
      * else if (fieldType.compareToIgnoreCase("INT") == 0) { NumberField f =
      * new NumberField(); f.setFormat(NumberFormat.getFormat("#")); result = f;
      * }
      */else if (fieldType.compareToIgnoreCase("TIME") == 0) {
      TimeField f = new TimeField();
      f.setFormat(DateTimeFormat.getFormat("HH:mm"));
      result = f;
      // Log.debug(field.getName() + ": TIME");
    } else if (((fieldType.compareToIgnoreCase("BLOB") == 0) || (fieldType.compareToIgnoreCase("TEXT") == 0))
        && textarea) {
      TextArea f = new TextArea();
      // f.setFormat(DateTimeFormat.getFormat("HH:mm"));
      result = f;
    }
    else if (field.getSpecificType().compareToIgnoreCase(FieldDataType.ENUM) == 0) {
      final SimpleComboBox<String> f = new SimpleComboBox<String>();
//      for (String ele : field.getFixedElements()) {
//        System.out.println("aggiunto al combo: " + ele);
//      }
//      f.add(field.getFixedElements());
      
      Listener<BaseEvent> l = new Listener<BaseEvent>() {

        @Override
        public void handleEvent(BaseEvent be) {
          // TODO Auto-generated method stub
          f.removeAll();
          f.add(field.getFixedElements());
          f.setTriggerAction(TriggerAction.ALL);
//          f.expand();
        }
        
      };
      f.addListener(Events.OnClick, l);
      result = f;
    } else if (field.getSpecificType().compareToIgnoreCase(FieldDataType.CHAR) == 0) {
      TextField<String> f = new TextField<String>();
      f.setWidth(field.getLenght());
      f.setRawValue(field.getDefaultVAlue());
      
      result = f;
    }
    else {
      if (field.getForeignKey().compareToIgnoreCase("") != 0) {
        // Log.debug(field.getName() + ": COMBO");

        final SimpleComboBox f;
        if ((fieldType.compareToIgnoreCase("int") == 0)
            || (fieldType.compareToIgnoreCase("real") == 0)) {
          f = new SimpleComboBox<Integer>();
          f.setTriggerAction(TriggerAction.ALL);
          // Trasforno la List<String> in List<Integer>
          //
          List<Integer> intVals = new ArrayList<Integer>();

          for (int i = 0; i < values.size(); i++) {
            if (values.get(i) != null) {
              Integer intVal = Integer.valueOf(values.get(i));
              intVals.add(i, intVal);
//              System.out.println("campo: " + f.getName() + i + "->"
//                  + f.getClass());
            } else {
              intVals.add(null);
            }
          }
          f.add(intVals);
        } else {
          f = new SimpleComboBox<String>();
          f.setTriggerAction(TriggerAction.ALL);

//          System.out.println("stile del combo: " + f.getStyleName());
          f.add(values);
          // System.out.println("campo: "+f.getName()+"->"+f.getClass()+f.get);
        }


        Listener<BaseEvent> l = new Listener<BaseEvent>() {

          public void handleEvent(final BaseEvent be) {

            ForeignKey fkObject = new ForeignKey();
            fkObject.setPointingResultsetId(field.getResultsetid());
            fkObject.setPointingFieldName(field.getName());
            fkObject.setPointedTableName(field.getForeignKey().split("\\.")[0]);
            fkObject.setPointedFieldName(field.getForeignKey().split("\\.")[1]);
            
//            f.removeAll();
            
            AppEvent event = new AppEvent(EventList.GetValuesOfAField);
            event.setData("object", fkObject);
            event.setData("source", source);
            f.removeAll();
            Dispatcher.forwardEvent(event);

          }

        };

        f.addListener(Events.OnClick, l);

        result = f;

      } else {
        // Log.debug(field.getName() + ": TEXT");
        result = new TextField<String>();
      }
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

//  public static Field<?> createField(final ResultsetField field) {
//    
//    String specificType = field.getSpecificType();
//    if (field.isCombo()) {
//      if (specificType.compareToIgnoreCase(FieldDataType.VARCHAR) == 0) {
//        SimpleComboBox<String> f = new SimpleComboBox<String>();        
//        f.setTriggerAction(TriggerAction.ALL);
//        f.add(field.getFixedElements());
//        return f;
//      }
//            
//    } else {
//      
//    }
//    
//    
//    return null;    
//  }
}
