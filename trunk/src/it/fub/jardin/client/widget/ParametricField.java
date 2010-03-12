/**
 * 
 */
package it.fub.jardin.client.widget;

import it.fub.jardin.client.Jardin;
import it.fub.jardin.client.ManagerServiceAsync;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author acozzolino
 * 
 */
public class ParametricField extends ComboBox<BaseModelData> {

  public ParametricField(final int resultsetId,
      final String referencedFieldName, final String fieldName,
      final String fieldAlias) {

    setFieldLabel(fieldAlias);
    setName(fieldName);
    setEditable(true);
    setDisplayField(fieldName);
    setTriggerAction(TriggerAction.ALL);
    setHideTrigger(true);

    final ListStore<BaseModelData> newStore = new ListStore<BaseModelData>();
    BaseModelData bm = new BaseModelData();
    bm.set(fieldName, "");
    newStore.add(bm);
    setStore(newStore);

    Listener<BaseEvent> l = new Listener<BaseEvent>() {

      public void handleEvent(BaseEvent be) {
        final MessageBox wait =
            MessageBox.wait("Attendere",
                "Recupero valori autocompletamento per " + fieldAlias, "");
        final ManagerServiceAsync service =
            (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

        RpcProxy<List<BaseModelData>> proxy =
            new RpcProxy<List<BaseModelData>>() {

              protected void load(Object loadConfig,
                  AsyncCallback<List<BaseModelData>> callback) {
                service.getValuesOfAField(resultsetId, referencedFieldName,
                    callback);
              }
            };

        final BaseListLoader loader = new BaseListLoader(proxy);
        loader.setRemoteSort(false);
        final ListStore<BaseModelData> fieldValuesStore =
            new ListStore<BaseModelData>(loader);

        loader.addLoadListener(new LoadListener() {

          @Override
          public void loaderLoad(LoadEvent le) {

            List<BaseModelData> elementes = fieldValuesStore.getModels();

            Collections.sort(elementes, new Comparator<BaseModelData>() {
              public int compare(BaseModelData arg0, BaseModelData arg1) {
                if (((String) arg0.get(fieldName)) == null
                    || ((String) arg0.get(fieldName)) == null) {
                  return 0;
                } else {
                  return ((String) arg0.get(fieldName)).compareTo((String) arg1.get(fieldName));
                }
              }
            });
            newStore.add(elementes);

            setStore(newStore);
            setToolTip(new ToolTipConfig("Informazione",
                "Digitare nel menù a tendina"
                    + " per utilizzare la funzione di autocompletamento "));

            // setEnabled(true);
            wait.close();

            if (isExpanded()) {
              collapse();
            }
          }

          @Override
          public void loaderLoadException(LoadEvent le) {
            MessageBox.alert("Recupero store autocompletamento campo "
                + referencedFieldName + " per RS: " + resultsetId,
                "loaderLoadException: " + le.exception.getLocalizedMessage(),
                null);
            le.exception.printStackTrace();
          }

        });

        loader.load();
        // disabilitare l'evento!!!
        // removeListener(Events.OnClick, this);
      }
    };

    addListener(Events.OnClick, l);
  }
}
