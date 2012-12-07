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

public class ParametricField extends ComboBox<BaseModelData> {

  public ParametricField(final int resultsetId,
      final String referencedFieldName, final String fieldName,
      final String fieldAlias) {

    this.setFieldLabel(fieldAlias);
    this.setName(fieldName);
    this.setEditable(true);
    this.setDisplayField(fieldName);
    this.setTriggerAction(TriggerAction.ALL);
    this.setHideTrigger(true);

    final ListStore<BaseModelData> newStore = new ListStore<BaseModelData>();
    BaseModelData bm = new BaseModelData();
    bm.set(fieldName, "");
    newStore.add(bm);
    this.setStore(newStore);

    Listener<BaseEvent> l = new Listener<BaseEvent>() {

      public void handleEvent(final BaseEvent be) {
        final MessageBox wait =
            MessageBox.wait("Attendere",
                "Recupero valori autocompletamento per " + fieldAlias, "");
        final ManagerServiceAsync service =
            (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

        RpcProxy<List<BaseModelData>> proxy =
            new RpcProxy<List<BaseModelData>>() {

              @Override
              protected void load(final Object loadConfig,
                  final AsyncCallback<List<BaseModelData>> callback) {
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
          public void loaderLoad(final LoadEvent le) {

            List<BaseModelData> elementes = fieldValuesStore.getModels();

            Collections.sort(elementes, new Comparator<BaseModelData>() {
              public int compare(final BaseModelData arg0,
                  final BaseModelData arg1) {
                if ((((String) arg0.get(fieldName)) == null)
                    || (((String) arg1.get(fieldName)) == null)) {
                  return 0;
                } else {
                  return ((String) arg0.get(fieldName)).compareTo((String) arg1.get(fieldName));
                }
              }
            });
            newStore.add(elementes);

            ParametricField.this.setStore(newStore);
            ParametricField.this.setToolTip(new ToolTipConfig("Informazione",
                "Digitare nel menù a tendina"
                    + " per utilizzare la funzione di autocompletamento "));

            // setEnabled(true);
            wait.close();

            if (ParametricField.this.isExpanded()) {
              ParametricField.this.collapse();
            }
          }

          @Override
          public void loaderLoadException(final LoadEvent le) {
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

    this.addListener(Events.OnClick, l);
  }
}
