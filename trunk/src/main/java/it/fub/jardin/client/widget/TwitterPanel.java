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

public class TwitterPanel extends ContentPanel {

  private static final int MAX_CHARACTERS = 160;
  private final User user;
  private TextArea text;
  private Button send;
  private Status status;
  private RadioGroup typeSelect;
  private ContentPanel messageArea;
  private static final String TYPE = "TYPE";

  public TwitterPanel(final User user) {
    this.user = user;

    this.setStyleName("twitter-area");
    this.setHeaderVisible(false);
    this.setBorders(false);
    this.setBodyBorder(false);
    this.setFrame(false);
    this.setLayout(new RowLayout(Orientation.VERTICAL));

    this.add(this.getSendArea(), new RowData(1, -1, new Margins(4)));
    this.add(this.getMessageArea(), new RowData(1, 1, new Margins(0)));

  }

  private Component getMessageArea() {
    ContentPanel area = new ContentPanel(new FitLayout());
    area.setStyleName("message-area");
    area.setHeaderVisible(false);
    area.setBorders(false);
    area.setBodyBorder(false);
    area.setFrame(false);

    this.messageArea = new ContentPanel(new RowLayout(Orientation.VERTICAL));
    this.messageArea.setHeaderVisible(false);
    this.messageArea.setBorders(false);
    this.messageArea.setBodyBorder(false);
    this.messageArea.setFrame(false);
    this.messageArea.setScrollMode(Scroll.AUTOY);
    this.messageArea.setStyleAttribute("font", "120% serif");
    area.add(this.messageArea);

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

      public String validate(final Field<?> field, final String value) {

        boolean result =
            ((value != null) && (value.length() > 0) && (value.length() <= MAX_CHARACTERS));
        TwitterPanel.this.status.setText(String.valueOf(MAX_CHARACTERS
            - value.length()));
        TwitterPanel.this.send.setEnabled(result);

        if (result) {
          return null;
        } else {
          return "Messaggio non valido";
        }
      }
    };

    KeyListener keyListener = new KeyListener() {
      @Override
      public void componentKeyUp(final ComponentEvent event) {
        TwitterPanel.this.text.validate();

        // Use Ctrl+Enter to send messages
        if (TwitterPanel.this.text.isValid() && event.isControlKey()
            && (event.getKeyCode() == KeyCodes.KEY_ENTER)) {
          TwitterPanel.this.submit();
        }
      }
    };

    this.text = new TextArea();
    this.text.setStyleAttribute("font", "110% serif");
    // text.setEmptyText("Digitare qui il messaggio da inviare");
    this.text.addKeyListener(keyListener);
    this.text.setValidator(validator);
    this.text.setMinLength(1);
    this.text.setMaxLength(MAX_CHARACTERS);

    area.add(this.text);

    /* Button bar */
    ButtonBar bb = area.getButtonBar();
    bb.removeAll();
    bb.setAlignment(HorizontalAlignment.LEFT);

    /* Send button */
    this.send = new Button("Invia");
    this.send.setEnabled(false);
    this.send.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(final ButtonEvent ce) {
        TwitterPanel.this.submit();
      }
    });

    /* Update button */
    Button update = new Button("Aggiorna");
    update.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(final ButtonEvent ce) {
        TwitterPanel.this.updateMessages();
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

    this.typeSelect = new RadioGroup("recipient");
    this.typeSelect.add(radio);
    this.typeSelect.add(radio2);

    /* Status label */
    this.status = new Status();
    this.status.setText(String.valueOf(MAX_CHARACTERS));

    bb.add(this.typeSelect);
    bb.add(new FillToolItem());
    bb.add(this.status);
    bb.add(this.send);
    bb.add(update);

    return area;
  }

  private void submit() {
    String body = this.text.getValue();

    // TODO manage recipient?
    // TODO eliminate title
    MessageType type = (MessageType) this.typeSelect.getValue().getData(TYPE);
    Message message = null;
    switch (type) {
    case GROUP:
      message =
          new Message("Titolo messaggio", body, new Date(), type,
              this.user.getGid());
      break;
    case ALL:
      message = new Message("Titolo messaggio", body, new Date(), type, -1);
      break;
    default:
      break;
    }

    Log.debug(message.toString());
    Dispatcher.forwardEvent(EventList.SendMessage, message);
    this.text.reset();
    this.text.validate();
  }

  @Override
  protected void onShow() {
    super.onShow();
    this.updateMessages();
  }

  private void updateMessages() {
    this.messageArea.removeAll();

    List<Message> m = this.user.getMessages();

    if (m.size() <= 0) {
      this.messageArea.addText("<i>Nessuna comunicazione</i>");
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

      this.messageArea.addText("<p>" + type + " " + date + ": " + title
          + "<br>" + body + "</p>");
    }

    this.messageArea.layout();
  }

}
