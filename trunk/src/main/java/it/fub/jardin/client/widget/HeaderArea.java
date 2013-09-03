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
import it.fub.jardin.client.model.User;
import it.fub.jardin.client.mvc.JardinView;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;

public class HeaderArea extends HtmlContainer {

  private final User user;

  public HeaderArea(final User user) {
    this.user = user;
    String header =
        "<div id='" + JardinView.HEADER_AREA + "'>" + "<div id='"
            + JardinView.HEADER_AREA + "-left'>"
            + "<b>JARDiN</b> Manager</div>" + "<div id='"
            + JardinView.HEADER_AREA + "-right'></div></div>";
    this.setHtml(header);
    this.createButtons();
  }

  private void createButtons() {

    final HorizontalPanel toolbar = new HorizontalPanel();
    toolbar.setId(JardinView.HEADER_AREA + "-toolbar");
    this.add(toolbar, "#" + JardinView.HEADER_AREA + "-right");

    Button about = new Button("Info");
    about.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(final ButtonEvent ce) {
        Window w = new Window();
        w.setTitle("Info");
        w.setModal(true);
        w.setSize(460, 140);
        w.setMaximizable(false);
        w.setResizable(false);
        w.setBodyBorder(false);
        w.setUrl("help/about.xml");
        w.show();
      }
    });
    toolbar.add(about);

    Button help = new Button("Aiuto");
    help.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(final ButtonEvent ce) {
        Window w = new Window();
        w.setIconStyle("icon-book");
        w.setTitle("Aiuto");
        w.setModal(false);
        w.setSize(460, 410);
        w.setMaximizable(true);
        w.setUrl("help/help.html");
        w.show();
      }
    });
    toolbar.add(help);

    Button faq = new Button("FAQ");
    faq.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(final ButtonEvent ce) {
        Window w = new Window();
        w.setIconStyle("icon-book");
        w.setTitle("FAQ");
        w.setModal(false);
        w.setSize(460, 410);
        w.setMaximizable(true);
        w.setUrl("help/faq.html");
        w.show();
      }
    });
    toolbar.add(faq);

    Button calc = new Button("Calcolatrice");
    calc.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(final ButtonEvent ce) {
        Window w = new Window();
        w.setTitle("Calcolatrice");
        w.setModal(false);
        w.setSize(340, 410);
        w.setMaximizable(false);
        w.setResizable(false);
        w.setUrl("calculator.html");
        w.show();
      }
    });
    // toolbar.add(calc);

    Button calendar = new Button("Calendario");
    calendar.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(final ButtonEvent ce) {
        Window w = new Window();
        w.setTitle("Calendario");
        w.setModal(false);
        w.setSize(460, 250);
        w.setMaximizable(false);
        w.setResizable(false);
        w.setUrl("calendar.html");
        w.show();
      }
    });
    // toolbar.add(calendar);

    final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
      public void handleEvent(final MessageBoxEvent ce) {
        Button btn = ce.getButtonClicked();
        if (btn.getText().compareToIgnoreCase("yes") == 0) {
          Dispatcher.forwardEvent(EventList.Logout);
        }
      }
    };

    Button exit = new Button("Uscita");
    exit.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(final ButtonEvent ce) {
        MessageBox.confirm("Uscita", "Sei sicuro?", l);
      }
    });
    toolbar.add(exit);

    final Window window = new UserWindow(this.user);
    final Button welcome = new Button(this.user.getUsername());
    welcome.setId(JardinView.HEADER_AREA + "-welcome");
    welcome.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(final ButtonEvent ce) {
        window.show();
      }
    });
    toolbar.add(welcome);
  }

}
