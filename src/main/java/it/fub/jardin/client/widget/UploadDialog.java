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
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.User;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RadioButton;

public class UploadDialog extends Window {

  private static final String ACTION = GWT.getModuleBaseURL() + "upload";
  /**
   * La stringa da far precedere al risultato della servlet in caso di
   * processamento corretto del file caricato
   */
  public static final String SUCCESS = "SUCCESS";
  /**
   * Definisce il nome del campo che contiene il tipo di azione di eseguire sul
   * file
   */
  public static final String FIELD_TYPE = "type";
  /**
   * Definisce il nome del campo che contiene il valore dell'id del resultset su
   * cui stiamo operando
   */
  public static final String FIELD_RESULTSET = "resultset";
  /**
   * Definisce il nome del campo che contiene il valore delle credenziali
   * dell'utente che sta effettuando l'upload
   */
  public static final String FIELD_CREDENTIALS = "credentials";
  /**
   * Definisce il nome dell'azione di import di un file contenente record da
   * caricare
   */
  public static final String TYPE_IMPORT = "import";
  /**
   * Definisce il nome dell'azione di import di un file contenente record da
   * inserire
   */
  public static final String TYPE_INSERT = "insert";
  /**
   * Definisce il nome dell'azione di import di un file da usare come template
   * di esportazione dei dati
   */
  public static final String TYPE_TEMPLATE = "template";

  private MessageBox waitBox = new MessageBox();

  /**
   * Costruisce una finestra per l'upload di file. E' possibile definire il tipo
   * di azioni da eseguire una volta che il file è caricato sul server
   * (impostare la variabile type per questo).
   * 
   * @param type
   *          Usato per definire l'azione che il serer deve compiere una volta
   *          caricato il file. Le azioni possibili sono definite nelle
   *          proprietà statiche della classe e sono precedute dalla stringa
   *          "TYPE_"
   * @param resultset
   *          Il resulset in cui avviene l'upload del file
   */
  public UploadDialog(final User user, final String type, final int resultset) {
    final FormPanel panel = new FormPanel();
    panel.setFrame(true);

    panel.setAction(ACTION);
    panel.setEncoding(Encoding.MULTIPART);
    panel.setMethod(Method.POST);

    panel.setLabelAlign(LabelAlign.LEFT);
    panel.setHeaderVisible(false);
    panel.setBorders(false);
    panel.setBodyBorder(false);
    panel.setFrame(false);

    panel.setButtonAlign(HorizontalAlignment.CENTER);
    panel.setScrollMode(Scroll.AUTO);

    panel.setLabelWidth(150);
    panel.setFieldWidth(280);

    if ((type.compareTo(TYPE_IMPORT) == 0)
        || (type.compareTo(TYPE_INSERT) == 0)) {

      panel.addText("Scegliere il tipo di import:");

      final RadioButton limit = new RadioButton("limit");
      limit.setText("Delimitati da separatori");
      limit.setValue(true);
      panel.add(limit);

      panel.addText("");

      final RadioButton fix = new RadioButton("fix");
      fix.setText("Lunghezza fissa");
      fix.setEnabled(false);
      panel.add(fix);

      final TextField<String> fieldSeparator = new TextField<String>();
      fieldSeparator.setName("fieldSep");
      fieldSeparator.setWidth(20);
      fieldSeparator.setMaxLength(1);
      fieldSeparator.setFieldLabel("Separatore di campo");
      fieldSeparator.setValue(";");
      panel.add(fieldSeparator);

      final SimpleComboBox<String> textSeparator = new SimpleComboBox<String>();
      textSeparator.setName("textSep");
      textSeparator.setWidth(20);
      textSeparator.setMaxLength(1);
      textSeparator.setFieldLabel("Separatore di testo");
      List<String> values = new ArrayList<String>();
      values.add("\"");
      values.add("'");
      textSeparator.add(values);
      textSeparator.setEditable(false);
      textSeparator.setTriggerAction(TriggerAction.ALL);
      textSeparator.setForceSelection(true);
      textSeparator.setSimpleValue("\"");
      panel.add(textSeparator);

      limit.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(final ClickEvent arg0) {
          fieldSeparator.show();
          textSeparator.show();
          fix.setValue(false);
        }
      });

      fix.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(final ClickEvent arg0) {
          fieldSeparator.hide();
          textSeparator.hide();
          limit.setValue(false);
        }
      });

      // group.addListener(Events.Change, new Listener<ComponentEvent>() {
      //
      // public void handleEvent(ComponentEvent be) {
      // Radio selected = group.getValue();
      // // MessageBox.alert("selezione", "selezionato: "
      // // + selected.getData("valore"), null);
      //
      // if (((String) selected.getData("tipologia")).compareToIgnoreCase("fix")
      // == 0) {
      // fieldSeparator.hide();
      // textSeparator.hide();
      // } else {
      // fieldSeparator.show();
      // textSeparator.show();
      // }
      // }
      //
      // });

    }

    if (type.compareTo(TYPE_IMPORT) == 0) {
      SimpleComboBox<String> conditions = new SimpleComboBox<String>();
      ResultsetImproved rsi = user.getResultsetImprovedFromId(resultset);
      List<ResultsetField> fields = rsi.getFields();
      for (ResultsetField field : fields) {
        if (field.getIsPK() || field.isUnique()) {
          conditions.add(field.getName());
        }
      }
      conditions.setFieldLabel("Colonna di riferimento");
      conditions.setName("condition");
      conditions.setEditable(false);
      conditions.setTriggerAction(TriggerAction.ALL);
      conditions.setForceSelection(true);
      conditions.setAllowBlank(false);
      panel.add(conditions);
    }

    HiddenField<String> importType = new HiddenField<String>();
    importType.setName(FIELD_TYPE);
    importType.setValue(type);
    panel.add(importType);

    HiddenField<Integer> uploadResultset = new HiddenField<Integer>();
    uploadResultset.setName(FIELD_RESULTSET);
    uploadResultset.setValue(resultset);
    panel.add(uploadResultset);

    HiddenField<String> uploadCredentials = new HiddenField<String>();
    uploadCredentials.setName(FIELD_CREDENTIALS);
    uploadCredentials.setValue(user.getCredentials().encode());
    panel.add(uploadCredentials);

    FileUploadField file = new FileUploadField();
    file.setAllowBlank(false);
    file.setFieldLabel("File");
    file.setName("file");
    panel.add(file);

    Button btn = new Button("Invia");
    btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(final ButtonEvent ce) {
        if (!panel.isValid()) {
          return;
        }
        UploadDialog.this.waitBox =
            MessageBox.wait("Caricamento dati", "Attendere prego...",
                "Loading...");
        panel.submit();
      }
    });
    panel.addButton(btn);

    panel.addListener(Events.Submit, new Listener<FormEvent>() {

      public void handleEvent(final FormEvent fe) {
        UploadDialog.this.hide();
        String message = fe.getResultHtml();
        message = message.replaceAll("<(.)?pre>", "");
        if (message.contains(SUCCESS)) {
          message = message.replaceAll(SUCCESS, "");
          if ((type.compareTo(TYPE_IMPORT) == 0)
              || (type.compareTo(TYPE_INSERT) == 0)) {
            Dispatcher.forwardEvent(EventList.UpdateStore, resultset);
            UploadDialog.this.waitBox.close();
          } else if (type.compareTo(TYPE_TEMPLATE) == 0) {
            Dispatcher.forwardEvent(EventList.UpdateTemplates, resultset);
          }
          Info.display("Informazione", message);
        } else {
          Dispatcher.forwardEvent(EventList.Error, message);
        }
        UploadDialog.this.waitBox.close();
      }

    });

    // this.setLayout(new FitLayout());
    this.setIconStyle("icon-upload-file");
    this.setTitle("File upload");
    this.setModal(true);
    this.setBodyStyle("padding: 8px 4px;");
    this.setWidth(500);
    this.setMinHeight(300);

    this.setResizable(false);

    if (type.compareTo(TYPE_IMPORT) == 0) {
      this.addText("<b><u>Attenzione: la prima riga del file da importare deve contenere i nomi delle colonne!</u></b>");
    }
    this.add(panel);
    this.setFocusWidget(file);
  }

}
