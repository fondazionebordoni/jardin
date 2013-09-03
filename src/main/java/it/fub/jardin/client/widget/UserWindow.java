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

import it.fub.jardin.client.model.User;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class UserWindow extends Window {

  private final TabPanel main;
  private final User user;

  /**
   * 
   */
  public UserWindow(final User user) {
    this.user = user;

    this.setSize(380, 380);
    this.setTitle("Area personale utente: <b>" + user.getFullName() + "</b>");
    this.setLayout(new FitLayout());
    this.setBorders(false);
    // this.setResizable(false);

    this.main = new TabPanel();
    this.main.setBorders(false);
    this.main.setPlain(true);
    this.main.setAnimScroll(true);
    // this.main.setTabScroll(true);

    this.main.add(this.settingsItem());
    this.main.add(this.messagesItem());

    this.add(this.main);
  }

  private static boolean isValidEmailAddress(final String email) {
    if (email == null) {
      return false;
    }
    if (email.matches(".+@.+\\.[a-z]+")) {
      return true;
    } else {
      return false;
    }
  }

  private TabItem settingsItem() {

    int defaultWidth = 220;
    int labelWidth = 100;

    TabItem item = new TabItem("Preferenze");
    item.setBorders(false);
    item.setLayout(new FitLayout());

    FormPanel form = new FormPanel();
    form.setStyleName("user-area");
    form.setHeaderVisible(false);
    form.setBorders(false);
    form.setBodyBorder(false);
    form.setFrame(false);
    form.setFieldWidth(defaultWidth);
    form.setLabelWidth(labelWidth);
    form.setLabelAlign(LabelAlign.RIGHT);
    form.setButtonAlign(HorizontalAlignment.CENTER);
    form.setScrollMode(Scroll.AUTO);

    final TextField<String> username = new TextField<String>();
    username.setFieldLabel("Username");
    username.setName("username");
    username.setValue(this.user.getUsername());
    username.disable();

    final TextField<String> password = new TextField<String>();
    password.setFieldLabel("Password");
    password.setTitle("Password");
    password.setName("password");
    password.setPassword(true);
    password.setValue(this.user.getPassword());

    final TextField<String> name = new TextField<String>();
    name.setFieldLabel("Nome");
    name.setName("name");
    name.setValue(this.user.getName());

    final TextField<String> surname = new TextField<String>();
    surname.setFieldLabel("Cognome");
    surname.setName("surname");
    surname.setValue(this.user.getSurname());

    final TextField<String> email = new TextField<String>();
    email.setFieldLabel("Email");
    email.setName("email");
    email.setValue(this.user.getEmail());

    final TextField<String> office = new TextField<String>();
    office.setFieldLabel("Ufficio");
    office.setName("office");
    office.setValue(this.user.getOffice());

    final TextField<String> telephone = new TextField<String>();
    telephone.setFieldLabel("Telefono");
    telephone.setName("telephone");
    telephone.setValue(this.user.getTelephone());

    Button submit = new Button("Salva Modifiche");
    submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(final ButtonEvent ce) {
        if (isValidEmailAddress(email.getValue()) || (email.getValue() == "")) {
          UserWindow.this.user.setName(name.getValue());
          UserWindow.this.user.setSurname(surname.getValue());
          UserWindow.this.user.setPassword(password.getValue());
          UserWindow.this.user.setEmail(email.getValue());
          UserWindow.this.user.setOffice(office.getValue());
          UserWindow.this.user.setTelephone(telephone.getValue());
          UserWindow.this.user.updateUserProperties();
          UserWindow.this.setTitle("Area personale utente: <b>"
              + UserWindow.this.user.getFullName() + "</b>");
        } else {
          MessageBox.info("Attenzione", "Inserire un'indirizzo email valido",
              null);
        }
      }
    });

    form.add(name);
    form.add(surname);
    form.add(username);
    form.add(password);
    form.add(email);
    form.add(office);
    form.add(telephone);
    form.addButton(submit);

    item.add(form);

    return item;
  }

  private TabItem messagesItem() {

    TabItem item = new TabItem("Comunicazioni");
    item.setBorders(false);
    item.setLayout(new FitLayout());
    item.add(new TwitterPanel(this.user));

    return item;
  }

}
