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
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.event.dom.client.KeyCodes;

public class LoginDialog extends Dialog {

  private final TextField<String> username;
  private final TextField<String> password;
  private Button login;
  private Status status;
  private Button registrationFormButton;

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
    this.setWidth(440);
    this.setResizable(false);

    KeyListener keyListener = new KeyListener() {
      @Override
      public void componentKeyUp(final ComponentEvent event) {
        if (LoginDialog.this.isValid()
            && (event.getKeyCode() == KeyCodes.KEY_ENTER)) {
          LoginDialog.this.submit();
        }
      }
    };

    this.username = new TextField<String>();
    this.username.setMinLength(4);
    this.username.setFieldLabel("Username");
    this.username.addKeyListener(keyListener);
    this.add(this.username);

    this.password = new TextField<String>();
    this.password.setMinLength(4);
    this.password.setPassword(true);
    this.password.setFieldLabel("Password");
    this.password.addKeyListener(keyListener);
    this.add(this.password);
    
    
//    this.add(this.registrationFormButton);
    this.setFocusWidget(this.username);
  }

  @Override
  protected void createButtons() {
    ButtonBar bb = this.getButtonBar();
    bb.removeAll();
    bb.setAlignment(HorizontalAlignment.LEFT);

    this.status = new Status();
    this.status.setText("Inserire Username e Password");
    bb.add(this.status);
//    bb.add(new FillToolItem());

    this.login = new Button("Login");
    this.login.setEnabled(false);
    this.login.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(final ButtonEvent ce) {
        LoginDialog.this.submit();
      }
    });
    bb.add(this.login);
    
    registrationFormButton = new Button("Accedi al form di registrazione");
    registrationFormButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

      @Override
      public void componentSelected(ButtonEvent ce) {
        // TODO Auto-generated method stub
        Dispatcher.forwardEvent(EventList.OpenRegistrationForm);
      }    
        
       
    });
    
    
    bb.add(registrationFormButton);

  }

  protected boolean hasValue(final TextField<String> field) {
    return (field.getValue() != null) && (field.getValue().length() > 0);
  }

  /**
   * Controlla che i campi del login dialog siano validi e abilita i tasti
   * 
   * @return true se tutti i campi sono compilati e la password è lunga almeno
   *         quattro caratteri. false altrimenti
   */
  protected boolean isValid() {
    if (this.hasValue(this.username) && (this.username.getValue().length() > 3)
        && this.hasValue(this.password)
        && (this.password.getValue().length() > 3)) {
      this.status.setText("Premere Invio o il bottone Login");
      this.login.setEnabled(true);
      return true;
    } else {
      this.status.setText("Inserire Username e Password");
      this.login.setEnabled(false);
    }
    return false;
  }

  private void submit() {
    this.status.setBusy("Verifica Username e Password...");
    this.login.setEnabled(false);

    Credentials c =
        new Credentials(this.username.getValue(), this.password.getValue());
//    Dispatcher.forwardEvent(EventList.CheckUser, c);
    Dispatcher.forwardEvent(EventList.CheckCredential, c);
  }

  /**
   * @return the registrationForm
   */
  public Button getRegistrationForm() {
    return registrationFormButton;
  }

  /**
   * @param registrationForm the registrationForm to set
   */
  public void setRegistrationForm(Button registrationForm) {
    this.registrationFormButton = registrationForm;
  }

}
