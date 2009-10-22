/**
 * 
 */
package it.fub.jardin.client.widget;

import it.fub.jardin.client.model.User;
import it.fub.jardin.client.model.Warning;

import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.event.dom.client.KeyCodes;

/**
 * @author gpantanetti
 * 
 */
public class TwitterPanel extends ContentPanel {

  private static final int MAX_CHARACTERS = 160;
  private User user;
  private TextArea text;
  private Button send;
  private Status status;

  public TwitterPanel(User user) {
    this.user = user;

    this.setStyleName("twitter-area");
    this.setHeaderVisible(false);
    this.setBorders(false);
    this.setBodyBorder(false);
    this.setFrame(false);
    this.setLayout(new RowLayout(Orientation.VERTICAL));

    this.add(getSendArea(), new RowData(1, -1, new Margins(4)));
    this.add(getMessageArea(), new RowData(1, 1, new Margins(0)));

  }

  private Component getMessageArea() {
    ContentPanel area = new ContentPanel(new FitLayout());
    area.setStyleName("message-area");
    area.setHeaderVisible(false);
    area.setBorders(false);
    area.setBodyBorder(false);
    area.setFrame(false);

    ContentPanel text = new ContentPanel();
    text.setHeaderVisible(false);
    text.setBorders(false);
    text.setBodyBorder(false);
    text.setFrame(false);
    text.setScrollMode(Scroll.AUTOY);
    text.setStyleAttribute("font", "serif");

    List<Warning> warnings = this.user.getWarnings();

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
    area.add(text);

    return area;
  }

  private Component getSendArea() {
    ContentPanel area = new ContentPanel(new FitLayout());
    area.setStyleName("text-area");
    area.setHeaderVisible(false);
    area.setBorders(false);
    area.setBodyBorder(false);
    area.setFrame(false);

    Validator validator = new Validator() {

      public String validate(Field<?> field, String value) {

        boolean result =
            (value != null && value.length() > 0 && value.length() <= MAX_CHARACTERS);
        status.setText(String.valueOf(MAX_CHARACTERS - value.length()));
        send.setEnabled(result);
        
        if (result) {
          return null;
        } else {
          return "Messaggio non valido";
        }
      }
    };

    KeyListener keyListener = new KeyListener() {
      public void componentKeyUp(ComponentEvent event) {
        text.validate();
        
        // Use Ctrl+Enter to send messages
        if (text.isValid() && event.isControlKey() && (event.getKeyCode() == KeyCodes.KEY_ENTER)) {
          submit();
        }
      }
    };

    text = new TextArea();
    text.setStyleAttribute("font", "110% serif");
    //text.setEmptyText("Digitare qui il messaggio da inviare");
    text.addKeyListener(keyListener);
    text.setValidator(validator);
    text.setMinLength(1);
    text.setMaxLength(MAX_CHARACTERS);

    area.add(text);

    /* Button bar */
    ButtonBar bb = area.getButtonBar();
    bb.removeAll();
    bb.setAlignment(HorizontalAlignment.LEFT);

    /* Send button */
    send = new Button("Invia");
    send.setEnabled(false);
    send.addSelectionListener(new SelectionListener<ButtonEvent>() {
      public void componentSelected(ButtonEvent ce) {
        submit();
      }
    });

    /* Radio selector for recipents */
    Radio radio = new Radio();
    radio.setName("radio");
    radio.setBoxLabel("Gruppo");
    radio.setValue(true);

    Radio radio2 = new Radio();
    radio2.setName("radio");
    radio2.setBoxLabel("Tutti");

    RadioGroup radioGroup = new RadioGroup("recipient");
    radioGroup.add(radio);
    radioGroup.add(radio2);

    /* Status label */
    status = new Status();
    status.setText(String.valueOf(MAX_CHARACTERS));

    bb.add(radioGroup);
    bb.add(new FillToolItem());
    bb.add(status);
    bb.add(send);

    return area;
  }

  private void submit() {
    Info.display("Messaggio", text.getValue());
    text.reset();
  }

}
