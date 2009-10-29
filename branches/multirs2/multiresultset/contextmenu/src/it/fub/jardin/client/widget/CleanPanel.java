/**
 * 
 */
package it.fub.jardin.client.widget;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * @author gpantanetti
 * 
 */
public class CleanPanel extends ContentPanel {

  /**
	 * 
	 */
  public CleanPanel() {
    super();
    this.setHeaderVisible(false);
    this.setBorders(false);
    this.setBodyBorder(false);
    this.setMonitorWindowResize(true);
    this.setLayout(new FitLayout());
    this.setButtonAlign(HorizontalAlignment.CENTER);
  }

}
