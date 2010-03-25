/**
 * 
 */
package it.fub.jardin.client.widget;

import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * @author acozzolino
 * 
 */
public class SimpleFieldSet extends FieldSet {

  private static int defaultWidth = 80;
  private static int labelWidth = 130;
  private static int padding = 0;

  public SimpleFieldSet(String heading) {
    this(heading, defaultWidth, labelWidth, padding);
  }

  public SimpleFieldSet(String heading, int defaultWidth, int labelWidth,
      int padding) {
    this.setHeading(heading);
    this.setCollapsible(true);
    this.setExpanded(true);
    FormLayout layout = new FormLayout();
    layout.setLabelWidth(labelWidth);
    layout.setLabelPad(padding);
    layout.setDefaultWidth(defaultWidth);
    this.setLayout(layout);
  }
}
