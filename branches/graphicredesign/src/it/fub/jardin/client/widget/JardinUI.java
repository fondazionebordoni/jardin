package it.fub.jardin.client.widget;

import java.util.ArrayList;
import java.util.List;

import it.fub.jardin.client.model.Credentials;
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
import com.google.gwt.user.client.ui.UIObject;

public class JardinUI extends UIObject {

  interface Binder extends UiBinder<DockLayoutPanel, JardinUI> {
  }

  // interface GlobalResources extends ClientBundle {
  // @NotStrict
  // @Source("global.css")
  // CssResource css();
  // }

  private static final Binder binder = GWT.create(Binder.class);

  @UiField
  HeaderArea header;
  //@UiField SearchAreaAdvanced advancedsearch;
  // @UiField JardinGrid grid;
  // @UiField JardinDetail detail;

  /**
   * This method constructs the application user interface by instantiating
   * controls and hooking up event handler.
   */
  public JardinUI() {
    // Inject global styles.
    // GWT.<GlobalResources>create(GlobalResources.class).css().ensureInjected();

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
    List<ResultsetImproved> l = new ArrayList<ResultsetImproved>();
    l.add(new ResultsetImproved(1, "Test", "Test", "SELECT * FROM user", true,
        true, true, true, null));
    return new HeaderArea(new User(1, 1, new Credentials("Test", "test"),
        "Mario", "Rossi", "Test", "test@test.com", null, null, 1, 1, null, l,
        null));
  }

  @UiFactory
  SearchAreaAdvanced makeSearchAreaAdvanced() {
    ResultsetImproved r =
        new ResultsetImproved(1, "Mario", "Rossi", "SELECT * FROM user", true,
            true, true, true, null);
    return new SearchAreaAdvanced(r);
  }

  @UiFactory
  JardinGrid makeJardinGrid() {
    return new JardinGrid(null, null, null);
  }

  @UiFactory
  JardinDetail makeJardinDetail() {
    ResultsetImproved r =
      new ResultsetImproved(1, "Test", "Test", "SELECT * FROM user", true,
          true, true, true, null);
    return new JardinDetail(r);
  }
}
