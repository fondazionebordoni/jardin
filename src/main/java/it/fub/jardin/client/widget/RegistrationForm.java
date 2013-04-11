/**
 * 
 */
package it.fub.jardin.client.widget;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.model.Credentials;
import it.fub.jardin.client.model.RegistrationInfo;
import it.fub.jardin.client.model.ResultsetImproved;

import java.util.HashMap;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.PasswordTextBox;

/**
 * @author acozzolino
 * 
 */
public class RegistrationForm extends Window {

  private FormPanel formPanel;
  private static final int defaultWidth = 270; // width dei campi
  private static final int labelWidth = 170;
  private static final int padding = 0;
  private static final String source = "registrationform";
  private TextField<String> nome;
  private TextField<String> cognome;
  private TextField<String> email;
  private TextField<String> telefono;
  private TextField<String> username;
  // private TextField<String> password;
  Button button;
  Status status;

  HashMap<String, FieldSet> fieldSetList = new HashMap<String, FieldSet>();

  /**
   * 
   */
  public RegistrationForm() {
    this.setSize(650, 250);
    // setAutoHeight(true);
    // setAutoWidth(true);
    this.setPlain(true);

    this.setHeading("FORM di REGISTRAZIONE UTENTE - Inserire dati preregistrati");
    this.setLayout(new FitLayout());

    KeyListener keyListener = new KeyListener() {
      @Override
      public void componentKeyUp(final ComponentEvent event) {
        if (isValid() && (event.getKeyCode() == KeyCodes.KEY_ENTER)) {
          RegistrationForm.this.submit();
        }
      }
    };

    /* Creazione FormPanel */
    this.formPanel = new FormPanel();
    this.formPanel.setBodyBorder(false);
    this.formPanel.setLabelWidth(350);
    this.formPanel.setHeaderVisible(false);
    this.formPanel.setScrollMode(Scroll.AUTO);

    nome = new TextField<String>();
    nome.setFieldLabel("Nome");
    nome.addKeyListener(keyListener);
    cognome = new TextField<String>();
    cognome.setFieldLabel("Cognome:");
    cognome.addKeyListener(keyListener);
    email = new TextField<String>();
    email.setFieldLabel("Email");
    email.addKeyListener(keyListener);
    telefono = new TextField<String>();
    telefono.setFieldLabel("Telefono");
    username = new TextField<String>();
    username.setFieldLabel("scegli uno username");
    username.addKeyListener(keyListener);
    // password = new TextField<String>();
    // password.setFieldLabel("scegli una password");
    // password.setMinLength(8);
    // password.setPassword(true);
    Label noteLabel = new Label("* Attenzione!!! Rispettare maiuscole e minuscole dei dati forniti!");

    this.formPanel.add(nome);
    this.formPanel.add(cognome);
    this.formPanel.add(email);
    this.formPanel.add(telefono);
    this.formPanel.add(username);
    this.formPanel.add(noteLabel);

//    Label alert = new Label("*I dati devono essere precaricati. Contattare gli amministratori di sistema");
//    this.formPanel.add(alert);
    this.add(this.formPanel);
    this.setButtons();

    this.show();
  }

  protected boolean hasValue(final TextField<String> field) {
    return (field.getValue() != null) && (field.getValue().length() > 0);
  }

  protected boolean isValid() {
    // TODO Auto-generated method stub
    // if (hasValue(nome) && hasValue(cognome) && hasValue(email)
    // && hasValue(username) && hasValue(password)
    // && password.getValue().length() > 7) {
    if (hasValue(nome) && hasValue(cognome) && hasValue(email)
        && hasValue(username) && username.getValue().length() >= 4) {
      this.button.setEnabled(true);
      return true;
    } else
      return false;
  }

  private void setButtons() {
    // TODO Auto-generated method stub
    ButtonBar buttonBar = new ButtonBar();
    buttonBar.removeAll();
    buttonBar.setAlignment(HorizontalAlignment.LEFT);

    this.formPanel.setBottomComponent(buttonBar);

    this.button =
        new Button("Registra utente", new SelectionListener<ButtonEvent>() {

          @Override
          public void componentSelected(ButtonEvent ce) {
            // TODO Auto-generated method stub
            RegistrationForm.this.submit();
          }
        });

    button.setEnabled(false);
    buttonBar.add(button);
    buttonBar.setAlignment(HorizontalAlignment.CENTER);
    this.formPanel.setBottomComponent(buttonBar);

  }

  protected void submit() {
    // TODO Auto-generated method stub
    // this.status.setBusy("Verifica credenziali registrazione...");
    this.button.setEnabled(false);

    RegistrationInfo r =
        new RegistrationInfo(nome.getValue(), cognome.getValue(),
            email.getValue(), username.getValue());
    // new RegistrationInfo(nome.getValue(), cognome.getValue(),
    // email.getValue(), username.getValue(), password.getValue());
    if (hasValue(telefono))
      r.setTelefono(telefono.getValue());
    // Dispatcher.forwardEvent(EventList.CheckUser, c);
    Dispatcher.forwardEvent(EventList.CheckRegistrationInfo, r);
  }

  /**
   * @return the username
   */
  public TextField<String> getUsername() {
    return username;
  }

  /**
   * @param username
   *          the username to set
   */
  public void setUsername(TextField<String> username) {
    this.username = username;
  }

  /**
   * @return the password
   */
  // public TextField<String> getPassword() {
  // return password;
  // }
  //
  // /**
  // * @param password
  // * the password to set
  // */
  // public void setPassword(TextField<String> password) {
  // this.password = password;
  // }

}
