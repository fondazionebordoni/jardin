/**
 * 
 */
package it.fub.jardin.client.widget;

import it.fub.jardin.client.Jardin;
import it.fub.jardin.client.ManagerServiceAsync;
import it.fub.jardin.client.model.ResultsetField;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
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
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author gpantanetti
 * 
 */
public class FieldCreator {

  public static Field<?> getField(ResultsetField field, List<String> values,
      boolean combo, boolean textarea) {
    return getField(field, values, combo, 0, textarea);
  }

  /**
   * Crea una widget per un campo a partire da un campo di un resultset.
   * Restituisce un combo con in valori di values se combo = true.
   * USATO PER L'AUTOCOMPLETAMENTO IN RICERCA
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
  @SuppressWarnings("unchecked")
public static/* TODO deve restituire <T extends Field> al posto di Field */Field<?> getField(
      final ResultsetField field, List<String> values, boolean combo, int labelWidth,
      boolean textarea) {
    Field<?> result = null;
    String fieldType = field.getType();
    /* Se il campo è una data non creo un combo */
    if (fieldType.compareToIgnoreCase("DATE") == 0) {
      DateField f = new DateField();
      f.getPropertyEditor().setFormat(DateTimeFormat.getFormat("dd/MM/y"));
      result = f;
    } else if (fieldType.compareToIgnoreCase("DATETIME") == 0) {
      DateField f = new DateField();
      f.getPropertyEditor().setFormat(DateTimeFormat.getFormat("dd/MM/y HH:mm:ss"));
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
//        if (values != null && values.size() > 0) {
        if (values != null) {
          final SimpleComboBox f;
          if ((fieldType.compareToIgnoreCase("int") == 0)
              || (fieldType.compareToIgnoreCase("real") == 0)) {
            f = new SimpleComboBox<Integer>();
            f.setTriggerAction(TriggerAction.ALL);
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
            f.add(values);
          }
          
          /////////////////////////
          Listener<BaseEvent> l = new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {

              final MessageBox wait =
                  MessageBox.wait(
                      "Attendere",
                      "Recupero valori autocompletamento per " + field.getAlias(),
                      "");
              final ManagerServiceAsync service =
                  (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

              RpcProxy<List<BaseModelData>> proxy =
                  new RpcProxy<List<BaseModelData>>() {

                    protected void load(Object loadConfig,
                        AsyncCallback<List<BaseModelData>> callback) {
                      service.getValuesOfAField(
                          field.getResultsetid(),
                          field.getName(), callback);
                    }
                  };

              final BaseListLoader loader = new BaseListLoader(proxy);
              loader.setRemoteSort(false);
              final ListStore<BaseModelData> fieldValuesStore =
                  new ListStore<BaseModelData>(loader);

              loader.addLoadListener(new LoadListener() {
                @Override
                public void loaderLoad(LoadEvent le) {

                  f.removeAll();
                  List<BaseModelData> elementes = fieldValuesStore.getModels();

                  List<String> newValues = new ArrayList<String>();
                  for (BaseModelData bm : elementes) {
                    newValues.add((String) bm.get(field.getName()));
                  }

                  f.add(newValues);

                  wait.close();

                  if (!f.isExpanded()) {
                    f.expand();
                  }
                }

                @Override
                public void loaderLoadException(LoadEvent le) {
                  MessageBox.alert("Recupero store autocompletamento campo "
                      + field.getName(),
                      "loaderLoadException: "
                          + le.exception.getLocalizedMessage(), null);
                  le.exception.printStackTrace();
                }

              });

              loader.load();
            }

          };

          f.addListener(Events.OnClick, l);
          /////////////////////////
          
          result = f;
        } else {
          result = new TextField<String>();
        }
        
      } else {
        result = new TextField<String>();
      }
    }
    result.setName(field.getName());
    if (labelWidth > 0 && field.getAlias().length() > labelWidth / 10) {
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
   * Restituisce un combo con in valori di values se combo = true.
   * USATO PER L'AUTOCOMPLETAMENTO IN GRIGLIA E DETTAGLIO
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
      List<String> values, int labelWidth, boolean textarea) {
    Field<?> result = null;
    String fieldType = field.getType();

    /* Se il campo è una data non creo un combo */
    if (fieldType.compareToIgnoreCase("DATE") == 0) {
      DateField f = new DateField();
      f.getPropertyEditor().setFormat(DateTimeFormat.getFormat("dd/MM/y"));

      result = f;
      Log.debug(field.getName() + ": DATE");
    }  else if (fieldType.compareToIgnoreCase("DATETIME") == 0) {
      DateField f = new DateField();
      f.getPropertyEditor().setFormat(DateTimeFormat.getFormat("dd/MM/y HH:mm:ss"));
      result = f;
    }/*
      * else if (fieldType.compareToIgnoreCase("INT") == 0) { NumberField f =
      * new NumberField(); f.setFormat(NumberFormat.getFormat("#")); result = f;
      * }
      */else if (fieldType.compareToIgnoreCase("TIME") == 0) {
      TimeField f = new TimeField();
      f.setFormat(DateTimeFormat.getFormat("HH:mm"));
      result = f;
      Log.debug(field.getName() + ": TIME");
    } else if (((fieldType.compareToIgnoreCase("BLOB") == 0) || (fieldType.compareToIgnoreCase("TEXT") == 0))
        && textarea) {
      TextArea f = new TextArea();
      // f.setFormat(DateTimeFormat.getFormat("HH:mm"));
      result = f;
    } else {
      if (field.getForeignKey().compareToIgnoreCase("") != 0) {
        Log.debug(field.getName() + ": COMBO");

        final SimpleComboBox f;
        if ((fieldType.compareToIgnoreCase("int") == 0)
            || (fieldType.compareToIgnoreCase("real") == 0)) {
          f = new SimpleComboBox<Integer>();
          f.setTriggerAction(TriggerAction.ALL);
          // Trasforno la List<String> in List<Integer>
          //
          List<Integer> intVals = new ArrayList<Integer>();
          // Iterator itr = values.iterator();
          // while(itr.hasNext()){
          // Integer intVal = Integer.valueOf((String) itr.next());
          // intVals.add(intVal);
          // }
          for (int i = 0; i < values.size(); i++) {
            if (values.get(i) != null) {
              Integer intVal = Integer.valueOf((String) values.get(i));
              intVals.add(i, intVal);
System.out.println("campo: "+f.getName()+i+"->"+f.getClass());
            } else {
              intVals.add(null);
            }
          }
          f.add(intVals);
        } else {
          f = new SimpleComboBox<String>();
          f.setTriggerAction(TriggerAction.ALL);
          f.add(values);
//System.out.println("campo: "+f.getName()+"->"+f.getClass()+f.get);
        }
        // System.out.println("test");
        // f = new SimpleComboBox<String>();
        // f.setTriggerAction(TriggerAction.ALL);
        // f.add(values);

        Listener<BaseEvent> l = new Listener<BaseEvent>() {

          public void handleEvent(BaseEvent be) {

            final MessageBox wait =
                MessageBox.wait(
                    "Attendere",
                    "Recupero valori autocompletamento per " + field.getAlias(),
                    "");
            final ManagerServiceAsync service =
                (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

            RpcProxy<List<BaseModelData>> proxy =
                new RpcProxy<List<BaseModelData>>() {

                  protected void load(Object loadConfig,
                      AsyncCallback<List<BaseModelData>> callback) {
                    service.getValuesOfAFieldFromTableName(
                        field.getForeignKey().split("\\.")[0],
                        field.getForeignKey().split("\\.")[1], callback);
                  }
                };

            final BaseListLoader loader = new BaseListLoader(proxy);
            loader.setRemoteSort(false);
            final ListStore<BaseModelData> fieldValuesStore =
                new ListStore<BaseModelData>(loader);

            loader.addLoadListener(new LoadListener() {
              @Override
              public void loaderLoad(LoadEvent le) {

                f.removeAll();
                List<BaseModelData> elementes = fieldValuesStore.getModels();

                List<String> newValues = new ArrayList<String>();
                for (BaseModelData bm : elementes) {
                  newValues.add((String) bm.get(field.getForeignKey().split(
                      "\\.")[1]));
                }

                f.add(newValues);

                wait.close();

                if (!f.isExpanded()) {
                  f.expand();
                }
              }

              @Override
              public void loaderLoadException(LoadEvent le) {
                MessageBox.alert("Recupero store autocompletamento campo "
                    + field.getForeignKey().split("\\.")[0]
                    + " per foreign key: "
                    + field.getForeignKey().split("\\.")[1],
                    "loaderLoadException: "
                        + le.exception.getLocalizedMessage(), null);
                le.exception.printStackTrace();
              }

            });

            loader.load();
          }

        };

        f.addListener(Events.OnClick, l);

        result = f;

      } else {
        Log.debug(field.getName() + ": TEXT");
        result = new TextField<String>();
      }
    }
    result.setName(field.getName());
    if (labelWidth > 0 && field.getAlias().length() > labelWidth / 10) {
      result.setFieldLabel(field.getAlias().substring(0, labelWidth / 10)
          + "...");
      result.setToolTip(field.getAlias());
    } else {
      result.setFieldLabel(field.getAlias());
    }

    return result;
  }

}
