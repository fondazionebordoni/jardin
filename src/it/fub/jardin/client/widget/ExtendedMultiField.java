/**
 * 
 */
package it.fub.jardin.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

/**
 * @author gpantanetti
 */
public abstract class ExtendedMultiField<F extends Field<?>, D extends CharSequence>
    extends Field<D> {

  protected List<F> fields;
  protected LayoutContainer lc;
  /**
   * @uml.property name="validator"
   */
  protected Validator validator;
  /**
   * @uml.property name="orientation"
   */
  protected Orientation orientation = Orientation.HORIZONTAL;
  /**
   * @uml.property name="spacing"
   */
  protected int spacing;
  /**
   * @uml.property name="resizeFields"
   */
  private boolean resizeFields;

  /**
   * Creates a new checkbox group.
   */
  public ExtendedMultiField() {
    fields = new ArrayList<F>();
    baseStyle = "x-form-group";
    invalidStyle = "none";
  }

  /**
   * Creates a new checkbox group.
   * 
   * @param fieldLabel
   *          the field label
   * @param fields
   *          the field(s) to add
   */
  public ExtendedMultiField(String fieldLabel, F... fields) {
    this();
    setFieldLabel(fieldLabel);
    for (F f : fields) {
      add(f);
    }
  }

  public abstract void setValue(D value);

  /**
   * Adds a field (pre-render).
   * 
   * @param field
   *          the field to add
   */
  public void add(F field) {
    assertPreRender();
    fields.add(field);
  }

  @Override
  public void disable() {
    super.disable();
    for (Field<?> field : fields) {
      field.disable();
    }
  }

  @Override
  public void enable() {
    super.enable();
    for (Field<?> field : fields) {
      field.enable();
    }
  }

  /**
   * Returns the field at the index.
   * 
   * @param index
   *          the index
   * @return the field
   */
  public F get(int index) {
    return fields.get(index);
  }

  /**
   * Returns all the child field's.
   * 
   * @return the fields
   */
  public List<F> getAll() {
    return new ArrayList<F>(fields);
  }

  /**
   * Returns the fields orientation.
   * 
   * @return the orientation
   * @uml.property name="orientation"
   */
  public Orientation getOrientation() {
    return (orientation);
  }

  /**
   * Returns the field's spacing.
   * 
   * @return the spacing
   * @uml.property name="spacing"
   */
  public int getSpacing() {
    return spacing;
  }

  /**
   * Returns the field's validator.
   * 
   * @return the validator
   * @uml.property name="validator"
   */
  public Validator getValidator() {
    return validator;
  }

  /**
   * Returns true if child fields are being resized.
   * 
   * @return the resize field state
   * @uml.property name="resizeFields"
   */
  public boolean isResizeFields() {
    return resizeFields;
  }

  @Override
  public boolean isValid() {
    boolean ret = super.isValid();
    for (Field<?> f : fields) {
      if (!f.isValid()) {
        return false;
      }
    }
    return ret;
  }

  @Override
  public void onBrowserEvent(Event event) {

  }

  @Override
  public void onComponentEvent(ComponentEvent ce) {

  }

  @Override
  public void reset() {
    for (Field<?> f : fields) {
      f.reset();
    }
  }

  /**
   * Sets the fields orientation (defaults to horizontal).
   * 
   * @param orientation
   *          the orientation
   * @uml.property name="orientation"
   */
  public void setOrientation(Orientation orientation) {
    this.orientation = orientation;
  }

  @Override
  public void setReadOnly(boolean readOnly) {
    for (Field<?> field : fields) {
      field.setReadOnly(readOnly);
    }
  }

  /**
   * True to resize the child fields to fit available space (defaults to false).
   * 
   * @param resizeFields
   *          true to resize children
   * @uml.property name="resizeFields"
   */
  public void setResizeFields(boolean resizeFields) {
    this.resizeFields = resizeFields;
  }

  /**
   * Sets the amount of spacing between fields. Spacing is applied to the right
   * of each field for horizontal orientataion and applied to the bottom of each
   * field for vertical orientation (defaults to 0, pre-render).
   * 
   * @param spacing
   *          the spacing in pixels
   * @uml.property name="spacing"
   */
  public void setSpacing(int spacing) {
    this.spacing = spacing;
  }

  /**
   * Sets the field's validator.
   * 
   * @param validator
   *          the validator
   * @uml.property name="validator"
   */
  public void setValidator(Validator validator) {
    this.validator = validator;
  }

  @Override
  protected void doAttachChildren() {
    ComponentHelper.doAttach(lc);
  }

  @Override
  protected void doDetachChildren() {
    ComponentHelper.doDetach(lc);
  }

  @Override
  protected El getInputEl() {
    if (fields.size() > 0) {
      return fields.get(0).el();
    }
    return super.getInputEl();
  }

  @Override
  protected void onRender(Element target, int index) {
    boolean vertical = orientation == Orientation.VERTICAL;
    if (vertical) {
      lc = new VerticalPanel();
    } else {
      lc = new HorizontalPanel();
    }
    if (GXT.isIE)
      lc.setStyleAttribute("position", "relative");

    for (int i = 0, len = fields.size(); i < len; i++) {
      Field<?> f = fields.get(i);
      boolean last = i == (fields.size() - 1);
      TableData data = (TableData) ComponentHelper.getLayoutData(f);
      if (data == null) {
        data = new TableData();
      }
      String style = "position: static;";

      if (vertical && !last && spacing > 0) {
        style += "paddingBottom:" + spacing + ";";
      } else if (!vertical && spacing > 0) {
        style += "paddingRight:" + spacing + ";";
      }
      data.setStyle(style);

      // TODO esportare l'allineamento della label come variabile di stato
      LayoutContainer c = new LayoutContainer();
      FormLayout layout = new FormLayout(LabelAlign.TOP);
      layout.setDefaultWidth(f.getWidth());
      layout.setLabelPad(0);
      layout.setLabelWidth(f.getWidth());
      // layout.setPadding(0);
      c.setLayout(layout);
      c.add(f);

      lc.add(c, data);
    }

    lc.render(target, index);
    setElement(lc.getElement());
  }

  @Override
  protected void onResize(int width, int height) {
    super.onResize(width, height);
    if (resizeFields) {
      if (orientation == Orientation.HORIZONTAL) {
        int w = width / fields.size();
        w -= (fields.size() * spacing);
        for (Field<?> f : fields) {
          f.setWidth(w);
        }
      } else {
        for (Field<?> f : fields) {
          f.setWidth(width);
        }
      }
    }
  }

  @Override
  protected boolean validateValue(String value) {
    // validate multi field
    if (validator != null) {
      String msg = validator.validate(this, value);
      if (msg != null) {
        markInvalid(msg);
        return false;
      }
    }

    clearInvalid();
    return true;
  }
}
