package it.fub.jardin.client.widget;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.model.Credentials;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.event.dom.client.KeyCodes;

/**
 * @author gpantanetti
 * 
 */
public class LoginDialog extends Dialog {

  private TextField<String> username;
  private TextField<String> password;
  private Button login;
  private Status status;

  public LoginDialog() {
    FormLayout layout = new FormLayout();
    layout.setLabelWidth(110);
    layout.setDefaultWidth(140);
    layout.setLabelAlign(LabelAlign.RIGHT);
    this.setLayout(layout);

    this.setIconStyle("icon-user");
    this.setHeading("Manager Login");
    this.setModal(true);
    this.setBodyStyle("padding: 8px 4px;");
    this.setWidth(320);
    this.setResizable(false);

    KeyListener keyListener = new KeyListener() {
      public void componentKeyUp(ComponentEvent event) {
        if (isValid() && (event.getKeyCode() == KeyCodes.KEY_ENTER)) {
          submit();
        }
      }
    };

    this.username = new TextField<String>();
    this.username.setMinLength(4);
    this.username.setFieldLabel("Username");
    this.username.addKeyListener(keyListener);
    this.add(username);

    this.password = new TextField<String>();
    this.password.setMinLength(4);
    this.password.setPassword(true);
    this.password.setFieldLabel("Password");
    this.password.addKeyListener(keyListener);
    this.add(password);

    this.setFocusWidget(username);
  }

  @Override
  protected void createButtons() {
    ButtonBar bb = getButtonBar();
    bb.removeAll();
    bb.setAlignment(HorizontalAlignment.LEFT);

    status = new Status();
    status.setText("Inserire Username e Password");
    bb.add(status);
    bb.add(new FillToolItem());

    login = new Button("Login");
    login.setEnabled(false);
    login.addSelectionListener(new SelectionListener<ButtonEvent>() {
      public void componentSelected(ButtonEvent ce) {
        submit();
      }
    });
    bb.add(login);

  }

  protected boolean hasValue(TextField<String> field) {
    return field.getValue() != null && field.getValue().length() > 0;
  }

  /**
   * Controlla che i campi del login dialog siano validi e abilita i tasti
   * 
   * @return true se tutti i campi sono compilati e la password è lunga almeno
   *         quattro caratteri. false altrimenti
   */
  protected boolean isValid() {
    if (hasValue(username) && username.getValue().length() > 3
        && hasValue(password) && password.getValue().length() > 3) {
      status.setText("Premere Invio o il bottone Login");
      login.setEnabled(true);
      return true;
    } else {
      status.setText("Inserire Username e Password");
      login.setEnabled(false);
    }
    return false;
  }

  private void submit() {
    status.setBusy("Verifica Username e Password...");
    login.setEnabled(false);

    Credentials c = new Credentials(username.getValue(), password.getValue());
    Dispatcher.forwardEvent(EventList.CheckUser, c);
  }

}
