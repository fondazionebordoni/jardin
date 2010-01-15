/**
 * 
 */
package it.fub.jardin.client.widget;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.model.User;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;

/**
 * @author acozzolino
 * 
 */
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
   * Definisce il nome dell'azione di import di un file da usare come template
   * di esportazione dei dati
   */
  public static final String TYPE_TEMPLATE = "template";

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
  public UploadDialog(User user, final String type, final int resultset) {
    final FormPanel panel = new FormPanel();
    panel.setFrame(true);

    panel.setAction(ACTION);
    panel.setEncoding(Encoding.MULTIPART);
    panel.setMethod(Method.POST);

    panel.setLabelAlign(LabelAlign.RIGHT);
    panel.setHeaderVisible(false);
    panel.setBorders(false);
    panel.setBodyBorder(false);
    panel.setFrame(false);

    panel.setLabelAlign(LabelAlign.RIGHT);
    panel.setButtonAlign(HorizontalAlignment.CENTER);
    panel.setScrollMode(Scroll.AUTO);
    panel.setFieldWidth(180);
    panel.setLabelWidth(50);
    
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
      public void componentSelected(ButtonEvent ce) {
        if (!panel.isValid()) {
          return;
        }
        panel.submit();
      }
    });
    panel.addButton(btn);
    
    panel.addListener(Events.Submit, new Listener<FormEvent>() {
      public void handleEvent(FormEvent fe) {
        hide();
        String message = fe.getResultHtml();
        message = message.replaceAll("<(.)?pre>", "");
        if (message.contains(SUCCESS)) {
          message = message.replaceAll(SUCCESS, "");
          if (type.compareTo(TYPE_IMPORT) == 0) {
            Dispatcher.forwardEvent(EventList.UpdateStore, resultset);
          }
          if (type.compareTo(TYPE_TEMPLATE) == 0) {
            Dispatcher.forwardEvent(EventList.UpdateTemplates, resultset);
          }
          Info.display("Informazione", message);
        } else {
          Dispatcher.forwardEvent(EventList.Error, message);
        }
      }

    });
    
    this.setLayout(new FitLayout());
    this.setIconStyle("icon-upload-file");
    this.setHeading("File upload");
    this.setModal(true);
    this.setBodyStyle("padding: 8px 4px;");
    this.setWidth(300);
    this.setResizable(false);
    this.addText("Se si carica un file contente uno più record già presenti "
        + "nel DB, il sistema aggiornerà tali record con i nuovi valori "
        + "contenuti nel file stesso."
        + "<b>La coincidenza deve sussistere a livello di chiave primaria o chiave unique.</b>"+"<BR />Attenzione: la prima riga del file da importare deve contenere i nomi delle colonne!");

    this.add(panel);
    this.setFocusWidget(file);
  }

}
