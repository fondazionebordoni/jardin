/**
 * 
 */
package it.fub.jardin.client.widget;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.model.Message;
import it.fub.jardin.client.model.MessageType;
import it.fub.jardin.client.model.User;

import java.util.Date;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextArea;
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
  private RadioGroup typeSelect;
  private ContentPanel messageArea;
  private static final String TYPE = "TYPE";

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

    messageArea = new ContentPanel(new RowLayout(Orientation.VERTICAL));
    messageArea.setHeaderVisible(false);
    messageArea.setBorders(false);
    messageArea.setBodyBorder(false);
    messageArea.setFrame(false);
    messageArea.setScrollMode(Scroll.AUTOY);
    messageArea.setStyleAttribute("font", "120% serif");
    area.add(messageArea);

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
        if (text.isValid() && event.isControlKey()
            && (event.getKeyCode() == KeyCodes.KEY_ENTER)) {
          submit();
        }
      }
    };

    text = new TextArea();
    text.setStyleAttribute("font", "110% serif");
    // text.setEmptyText("Digitare qui il messaggio da inviare");
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

    /* Update button */
    Button update = new Button("Aggiorna");
    update.addSelectionListener(new SelectionListener<ButtonEvent>() {
      public void componentSelected(ButtonEvent ce) {
        updateMessages();
      }
    });

    /* Radio selector for recipients */
    Radio radio = new Radio();
    radio.setData(TYPE, MessageType.GROUP);
    radio.setBoxLabel("Gruppo");
    radio.setValue(true);

    Radio radio2 = new Radio();
    radio2.setData(TYPE, MessageType.ALL);
    radio2.setBoxLabel("Tutti");

    typeSelect = new RadioGroup("recipient");
    typeSelect.add(radio);
    typeSelect.add(radio2);

    /* Status label */
    status = new Status();
    status.setText(String.valueOf(MAX_CHARACTERS));

    bb.add(typeSelect);
    bb.add(new FillToolItem());
    bb.add(status);
    bb.add(send);
    bb.add(update);

    return area;
  }

  private void submit() {
    String body = text.getValue();

    // TODO manage recipient?
    // TODO eliminate title
    MessageType type = (MessageType) typeSelect.getValue().getData(TYPE);
    Message message = null;
    switch (type) {
    case GROUP:
      message =
          new Message("Titolo messaggio", body, new Date(), type, user.getGid());
      break;
    case ALL:
      message = new Message("Titolo messaggio", body, new Date(), type, -1);
      break;
    default:
      break;
    }

    Log.debug(message.toString());
    Dispatcher.forwardEvent(EventList.SendMessage, message);
    text.reset();
    text.validate();
  }

  @Override
  protected void onShow() {
    super.onShow();
    this.updateMessages();
  }

  private void updateMessages() {
    messageArea.removeAll();

    List<Message> m = this.user.getMessages();

    if (m.size() <= 0) {
      messageArea.addText("<i>Nessuna comunicazione</i>");
    }

    for (Message w : m) {
      String body = w.getBody();
      String date = "<i>(" + w.getDate().toString() + ")</i>";
      String title = "<b>" + w.getTitle() + "</b>";
      String type = "";
      switch (w.getType()) {
      case GROUP:
        type = "&raquo;";
        break;
      case ALL:
        type = "#";
        break;
      case USER:
        type = "&rsaquo;";
        break;
      default:
        break;
      }
      type = "<b>" + type + "</b>";

      messageArea.addText("<p>" + type + " " + date + ": " + title + "<br>"
          + body + "</p>");
    }

    messageArea.layout();
  }

}
