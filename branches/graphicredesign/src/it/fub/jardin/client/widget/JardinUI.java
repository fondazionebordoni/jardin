package it.fub.jardin.client.widget;

import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.User;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.UIObject;

public class JardinUI extends UIObject {
  
  private User user;

  interface Binder extends UiBinder<DockLayoutPanel, JardinUI> {}
  private static final Binder binder = GWT.create(Binder.class);

  @UiField
  HeaderArea header;
  @UiField
  TabLayoutPanel main;

  /**
   * This method constructs the application user interface by instantiating
   * controls and hooking up event handler.
   */
  public JardinUI(User user) {
    this.user = user;

    // Create the UI defined in JardinUI.ui.xml.
    DockLayoutPanel outer = binder.createAndBindUi(this);

    // Get rid of scrollbars, and clear out the window's built-in margin,
    // because we want to take advantage of the entire client area.
    Window.enableScrolling(false);
    Window.setMargin("0px");

    // Special-case stuff to make topPanel overhang a bit.
    Element topElem = outer.getWidgetContainerElement(header);
    topElem.getStyle().setZIndex(2);
    topElem.getStyle().setOverflow(Overflow.VISIBLE);

    // Add the outer panel to the RootLayoutPanel, so that it will be
    // displayed.
    RootLayoutPanel root = RootLayoutPanel.get();
    root.add(outer);
    Log.debug("end.");
  }

  @UiFactory
  HeaderArea makeHeaderArea() {
    return new HeaderArea(this.user);
  }

  public void addTab(ResultsetImproved resultset) {
    main.add(new JardinTab(resultset), resultset.getAlias());
  }
}
