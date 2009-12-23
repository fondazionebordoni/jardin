/**
 * 
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

/**
 * @author gpantanetti
 * 
 */
public class HeaderArea extends HtmlContainer {

  private final String aboutMessage =
      "<b>Fondazione Ugo Bordoni<br>" + "JARDiN Manager<br></b>"
          + "Versione 0.9.7.3";

  private User user;

  public HeaderArea(User user) {
    this.user = user;
    String header =
        /*"<div id='" + JardinView.HEADER_AREA + "-int" + "'>" + */ 
    	"<div id='" + JardinView.HEADER_AREA + "-int" + "-left'>" + "<b>JARDiN</b> Manager</div>" + 
        "<div id='" + JardinView.HEADER_AREA + "-int" + "-right'> </div>" 	/* + "</div>" */ ;
    this.setHtml(header);
    // this.setHeight(32);
    this.createButtons();
  }

  private void createButtons() {

    final HorizontalPanel toolbar = new HorizontalPanel();
    toolbar.setId(JardinView.HEADER_AREA + "-toolbar");
    this.add(toolbar, "#" + JardinView.HEADER_AREA + "-int" + "-right");

    Button about = new Button("Info");
    about.addSelectionListener(new SelectionListener<ButtonEvent>() {
      public void componentSelected(ButtonEvent ce) {
        MessageBox m = new MessageBox();
        m.setMinWidth(320);
        m.setTitle("Info");
        m.setMessage(aboutMessage);
        m.setIcon(MessageBox.INFO);
        m.show();
      }
    });
    toolbar.add(about);

    Button help = new Button("Aiuto");
    help.addSelectionListener(new SelectionListener<ButtonEvent>() {
      public void componentSelected(ButtonEvent ce) {
        Window w = new Window();
        w.setIconStyle("icon-book");
        w.setHeading("Aiuto");
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
      public void componentSelected(ButtonEvent ce) {
        Window w = new Window();
        w.setIconStyle("icon-book");
        w.setHeading("FAQ");
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
      public void componentSelected(ButtonEvent ce) {
        Window w = new Window();
        w.setHeading("Calcolatrice");
        w.setModal(false);
        w.setSize(340, 410);
        w.setMaximizable(false);
        w.setResizable(false);
        w.setUrl("calculator.html");
        w.show();
      }
    });
    toolbar.add(calc);

    Button calendar = new Button("Calendario");
    calendar.addSelectionListener(new SelectionListener<ButtonEvent>() {
      public void componentSelected(ButtonEvent ce) {
        Window w = new Window();
        w.setHeading("Calendario");
        w.setModal(false);
        w.setSize(460, 250);
        w.setMaximizable(false);
        w.setResizable(false);
        w.setUrl("calendar.html");
        w.show();
      }
    });
    toolbar.add(calendar);

    final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
      public void handleEvent(MessageBoxEvent ce) {
        Button btn = ce.getButtonClicked();
        if (btn.getText().compareToIgnoreCase("yes") == 0) {
          Dispatcher.forwardEvent(EventList.Refresh);
        }
      }
    };

    Button exit = new Button("Uscita");
    exit.addSelectionListener(new SelectionListener<ButtonEvent>() {
      public void componentSelected(ButtonEvent ce) {
        MessageBox.confirm("Uscita", "Sei sicuro?", l);
      }
    });
    toolbar.add(exit);

    final Window window = new UserWindow(user);
    final Button welcome = new Button(user.getUsername());
    welcome.setId(JardinView.HEADER_AREA + "-welcome");
    welcome.addSelectionListener(new SelectionListener<ButtonEvent>() {
      public void componentSelected(ButtonEvent ce) {
        window.show();
      }
    });
    toolbar.add(welcome);
  }

}