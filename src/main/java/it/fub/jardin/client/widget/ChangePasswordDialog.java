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
import it.fub.jardin.client.model.User;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.Label;

public class ChangePasswordDialog extends Dialog {

  private TextField<String> newPassword;
  private TextField<String> oldPassword;
  private Label indicator;
  private Button submit;
  private Status status;
  private User user;

  public ChangePasswordDialog(User user) {
    this.user = user;

    FormLayout layout = new FormLayout();
    layout.setLabelWidth(110);
    layout.setDefaultWidth(140);
    layout.setLabelAlign(LabelAlign.RIGHT);
    this.setLayout(layout);

    this.setIconStyle("icon-user");
    this.setTitle("Primo login per l'utente " + this.user.getUsername()
        + " - creare nuova password");
    this.setModal(true);
    this.setBodyStyle("padding: 8px 4px;");
    this.setWidth(400);
    this.setResizable(false);

    KeyListener keyListener = new KeyListener() {
      @Override
      public void componentKeyUp(final ComponentEvent event) {
        if (ChangePasswordDialog.this.isValid()
            && (event.getKeyCode() == KeyCodes.KEY_ENTER)) {
          ChangePasswordDialog.this.submit();
        }
      }
    };

    // this.username = new Label("username: " + user.getName());
    // this.username.setMinLength(4);
    // this.username.setFieldLabel("Username");
    // this.username.addKeyListener(keyListener);
    // this.add(this.username);

    this.oldPassword = new TextField<String>();
    this.oldPassword.setMinLength(4);
    this.oldPassword.setPassword(true);
    this.oldPassword.setFieldLabel("New Password");
    this.oldPassword.addKeyListener(keyListener);
    this.add(this.oldPassword);

    this.newPassword = new TextField<String>();
    this.newPassword.setMinLength(4);
    this.newPassword.setPassword(true);
    this.newPassword.setFieldLabel("Confirm New Password");
    this.newPassword.addKeyListener(keyListener);
    this.add(this.newPassword);
    
    this.indicator = new Label();
    this.add(this.indicator);
    

    this.setFocusWidget(this.oldPassword);
  }

  @Override
  protected void createButtons() {
    ButtonBar bb = this.getButtonBar();
    bb.removeAll();
    bb.setAlignment(HorizontalAlignment.LEFT);

    this.status = new Status();
    this.status.setText("La nuova password deve essere di almeno 8 caratteri");
    bb.add(this.status);
    bb.add(new FillToolItem());

    this.submit = new Button("Submit");
    this.submit.setEnabled(false);
    this.submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(final ButtonEvent ce) {
        ChangePasswordDialog.this.submit();
      }
    });
    bb.add(this.submit);

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
    PasswordStrengthWidget.updateStrengthIndicator(newPassword, indicator);
    if (this.hasValue(this.newPassword) && this.hasValue(this.oldPassword) && (this.newPassword.getValue().length() > 7)) {
      this.status.setText("Premere il bottone Invio");
      this.submit.setEnabled(true);      
      return true;
    } else {
//      this.status.setText("Inserire nuova password");
      this.submit.setEnabled(false);
    }
    
    return false;
  }

  private void submit() {
    this.status.setBusy("Verifica password...");
    this.submit.setEnabled(false);

    // System.out.println("creazione credentials per: " + this.user.get);
    Credentials c =
        new Credentials(this.user.getUsername(), this.oldPassword.getValue());
    c.setNewPassword(this.newPassword.getValue());
    // Dispatcher.forwardEvent(EventList.CheckUser, c);
    if (newPassword.getValue().compareTo(oldPassword.getValue()) == 0) {
      Dispatcher.forwardEvent(EventList.CheckCredentialAndChangePassword, c);
    } else {
      this.status.clearStatus("La nuova password deve essere di almeno 8 caratteri");      
      MessageBox.alert("Password non coincidenti", "Le password inserite non coincidono", null);
    }
  }

  /**
   * @return the indicator
   */
  public Label getIndicator() {
    return indicator;
  }

  /**
   * @param indicator the indicator to set
   */
  public void setIndicator(Label indicator) {
    this.indicator = indicator;
  }

}
