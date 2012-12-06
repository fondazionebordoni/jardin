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
    this.fields = new ArrayList<F>();
    this.baseStyle = "x-form-group";
    this.invalidStyle = "none";
  }

  /**
   * Creates a new checkbox group.
   * 
   * @param fieldLabel
   *          the field label
   * @param fields
   *          the field(s) to add
   */
  public ExtendedMultiField(final String fieldLabel, final F... fields) {
    this();
    this.setFieldLabel(fieldLabel);
    for (F f : fields) {
      this.add(f);
    }
  }

  @Override
  public abstract void setValue(D value);

  /**
   * Adds a field (pre-render).
   * 
   * @param field
   *          the field to add
   */
  public void add(final F field) {
    this.assertPreRender();
    this.fields.add(field);
  }

  @Override
  public void disable() {
    super.disable();
    for (Field<?> field : this.fields) {
      field.disable();
    }
  }

  @Override
  public void enable() {
    super.enable();
    for (Field<?> field : this.fields) {
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
  public F get(final int index) {
    return this.fields.get(index);
  }

  /**
   * Returns all the child field's.
   * 
   * @return the fields
   */
  public List<F> getAll() {
    return new ArrayList<F>(this.fields);
  }

  /**
   * Returns the fields orientation.
   * 
   * @return the orientation
   * @uml.property name="orientation"
   */
  public Orientation getOrientation() {
    return (this.orientation);
  }

  /**
   * Returns the field's spacing.
   * 
   * @return the spacing
   * @uml.property name="spacing"
   */
  public int getSpacing() {
    return this.spacing;
  }

  /**
   * Returns the field's validator.
   * 
   * @return the validator
   * @uml.property name="validator"
   */
  public Validator getValidator() {
    return this.validator;
  }

  /**
   * Returns true if child fields are being resized.
   * 
   * @return the resize field state
   * @uml.property name="resizeFields"
   */
  public boolean isResizeFields() {
    return this.resizeFields;
  }

  @Override
  public boolean isValid() {
    boolean ret = super.isValid();
    for (Field<?> f : this.fields) {
      if (!f.isValid()) {
        return false;
      }
    }
    return ret;
  }

  @Override
  public void onBrowserEvent(final Event event) {

  }

  @Override
  public void onComponentEvent(final ComponentEvent ce) {

  }

  @Override
  public void reset() {
    for (Field<?> f : this.fields) {
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
  public void setOrientation(final Orientation orientation) {
    this.orientation = orientation;
  }

  @Override
  public void setReadOnly(final boolean readOnly) {
    for (Field<?> field : this.fields) {
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
  public void setResizeFields(final boolean resizeFields) {
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
  public void setSpacing(final int spacing) {
    this.spacing = spacing;
  }

  /**
   * Sets the field's validator.
   * 
   * @param validator
   *          the validator
   * @uml.property name="validator"
   */
  public void setValidator(final Validator validator) {
    this.validator = validator;
  }

  @Override
  protected void doAttachChildren() {
    ComponentHelper.doAttach(this.lc);
  }

  @Override
  protected void doDetachChildren() {
    ComponentHelper.doDetach(this.lc);
  }

  @Override
  protected El getInputEl() {
    if (this.fields.size() > 0) {
      return this.fields.get(0).el();
    }
    return super.getInputEl();
  }

  @Override
  protected void onRender(final Element target, final int index) {
    boolean vertical = this.orientation == Orientation.VERTICAL;
    if (vertical) {
      this.lc = new VerticalPanel();
    } else {
      this.lc = new HorizontalPanel();
    }
    if (GXT.isIE) {
      this.lc.setStyleAttribute("position", "relative");
    }

    for (int i = 0, len = this.fields.size(); i < len; i++) {
      Field<?> f = this.fields.get(i);
      boolean last = i == (this.fields.size() - 1);
      TableData data = (TableData) ComponentHelper.getLayoutData(f);
      if (data == null) {
        data = new TableData();
      }
      String style = "position: static;";

      if (vertical && !last && (this.spacing > 0)) {
        style += "paddingBottom:" + this.spacing + ";";
      } else if (!vertical && (this.spacing > 0)) {
        style += "paddingRight:" + this.spacing + ";";
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

      this.lc.add(c, data);
    }

    this.lc.render(target, index);
    this.setElement(this.lc.getElement());
  }

  @Override
  protected void onResize(final int width, final int height) {
    super.onResize(width, height);
    if (this.resizeFields) {
      if (this.orientation == Orientation.HORIZONTAL) {
        int w = width / this.fields.size();
        w -= (this.fields.size() * this.spacing);
        for (Field<?> f : this.fields) {
          f.setWidth(w);
        }
      } else {
        for (Field<?> f : this.fields) {
          f.setWidth(width);
        }
      }
    }
  }

  @Override
  protected boolean validateValue(final String value) {
    // validate multi field
    if (this.validator != null) {
      String msg = this.validator.validate(this, value);
      if (msg != null) {
        this.markInvalid(msg);
        return false;
      }
    }

    this.clearInvalid();
    return true;
  }
}
