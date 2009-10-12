/**
 * 
 */
package it.fub.jardin.client.widget;

import it.fub.jardin.client.model.User;
import it.fub.jardin.client.model.Warning;

import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * @author gpantanetti
 * 
 */
public class UserWindow extends Window {

  private TabPanel main;
  private User user;

  /**
   * 
   */
  public UserWindow(User user) {
    this.user = user;

    this.setSize(300, 300);
    this.setHeading("Area personale utente: <b>" + user.getFullName() + "</b>");
    this.setLayout(new FitLayout());
    this.setBorders(false);
    // this.setResizable(false);

    this.main = new TabPanel();
    this.main.setBorders(false);
    this.main.setPlain(true);
    this.main.setAnimScroll(true);
    // this.main.setTabScroll(true);

    this.main.add(settingsItem());
    this.main.add(messagesItem());

    this.add(main);
  }

  private static boolean isValidEmailAddress(String email) {
    if (email == null)
      return false;
    if (email.matches(".+@.+\\.[a-z]+"))
      return true;
    else
      return false;
  }

  private TabItem settingsItem() {

    int defaultWidth = 140;
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
    username.setValue((String) user.getUsername());
    username.disable();

    final TextField<String> password = new TextField<String>();
    password.setFieldLabel("Password");
    password.setTitle("Password");
    password.setName("password");
    password.setPassword(true);
    password.setValue((String) user.getPassword());

    final TextField<String> name = new TextField<String>();
    name.setFieldLabel("Nome");
    name.setName("name");
    name.setValue((String) user.getName());

    final TextField<String> surname = new TextField<String>();
    surname.setFieldLabel("Cognome");
    surname.setName("surname");
    surname.setValue((String) user.getSurname());

    final TextField<String> email = new TextField<String>();
    email.setFieldLabel("Email");
    email.setName("email");
    email.setValue((String) user.getEmail());

    final TextField<String> office = new TextField<String>();
    office.setFieldLabel("Ufficio");
    office.setName("office");
    office.setValue((String) user.getOffice());

    final TextField<String> telephone = new TextField<String>();
    telephone.setFieldLabel("Telefono");
    telephone.setName("telephone");
    telephone.setValue((String) user.getTelephone());

    Button submit = new Button("Salva Modifiche");
    submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(ButtonEvent ce) {
        if (isValidEmailAddress(email.getValue()) || email.getValue() == "") {
          user.setName(name.getValue());
          user.setSurname(surname.getValue());
          user.setPassword(password.getValue());
          user.setEmail(email.getValue());
          user.setOffice(office.getValue());
          user.setTelephone(telephone.getValue());
          user.updateUserProperties();
          setHeading("Area personale utente: <b>" + user.getFullName() + "</b>");
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

    ContentPanel text = new ContentPanel();
    text.setStyleName("message-area");
    text.setHeaderVisible(false);
    text.setBorders(false);
    text.setBodyBorder(false);
    text.setFrame(false);
    text.setScrollMode(Scroll.AUTOY);

    List<Warning> warnings = user.getWarnings();

    if (warnings.size() <= 0) {
      text.addText("<i>Nessuna comunicazione</i>");
    }

    for (Warning w : warnings) {
      String body = w.getBody();
      String date = "(" + w.getDate() + ")";
      String title = "<b>" + w.getTitle() + "</b>";
      String type = "[" + w.getType().substring(0, 1) + "]";
      text.addText("<i>" + type + " " + date + "</i><br>" + title + "<br>"
          + body + "<br><br>");
    }

    item.add(text);
    return item;
  }

}
