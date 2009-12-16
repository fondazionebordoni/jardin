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
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author gpantanetti
 * 
 */
public class HeaderArea extends Composite {
  
  private User user;

  interface Binder extends UiBinder<Widget, HeaderArea> {}
  private static final Binder binder = GWT.create(Binder.class);

  @UiField
  Anchor about;
  @UiField
  Anchor help;
  @UiField
  Anchor faq;
  @UiField
  Anchor calculator;
  @UiField
  Anchor calendar;
  @UiField
  Anchor settings;
  @UiField
  Anchor exit;
  @UiField
  SpanElement username;
  
  
  public HeaderArea(User user) {
    initWidget(binder.createAndBindUi(this));
    
    this.user = user;
    this.username.setInnerText(user.getFullName());
   
    addStyleName("header-area");
  }

  @UiHandler("about")
  void onAboutClicked(ClickEvent event) {
    AboutDialog dlg = new AboutDialog();
    dlg.show();
    dlg.center();
  }

  @UiHandler("help")
  void onHelpClicked(ClickEvent event) {
    Window w = new Window();
    w.setIconStyle("icon-book");
    w.setHeading("Aiuto");
    w.setModal(false);
    w.setSize(460, 410);
    w.setMaximizable(true);
    w.setUrl("help/help.html");
    w.show();
  }

  @UiHandler("faq")
  void onFaqClicked(ClickEvent event) {
    Window w = new Window();
    w.setIconStyle("icon-book");
    w.setHeading("FAQ");
    w.setModal(false);
    w.setSize(460, 410);
    w.setMaximizable(true);
    w.setUrl("help/faq.html");
    w.show();
  }

  @UiHandler("exit")
  void onExitClicked(ClickEvent event) {

    final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
      public void handleEvent(MessageBoxEvent ce) {
        Button btn = ce.getButtonClicked();
        if (btn.getText().compareToIgnoreCase("yes") == 0) {
          Dispatcher.forwardEvent(EventList.Refresh);
        }
      }
    };

    MessageBox.confirm("Uscita", "Sei sicuro?", l);
  }

  @UiHandler("calculator")
  void onCalculatorClicked(ClickEvent event) {
    Window w = new Window();
    w.setHeading("Calcolatrice");
    w.setModal(false);
    w.setSize(340, 410);
    w.setMaximizable(false);
    w.setResizable(false);
    w.setUrl("calculator.html");
    w.show();
  }

  @UiHandler("calendar")
  void onCalClicked(ClickEvent event) {
    Window w = new Window();
    w.setHeading("Calendario");
    w.setModal(false);
    w.setSize(460, 250);
    w.setMaximizable(false);
    w.setResizable(false);
    w.setUrl("calendar.html");
    w.show();
  }

  @UiHandler("settings")
  void onSettingsClicked(ClickEvent event) {
    Window window = new UserWindow(user);
    window.show();
  }

}