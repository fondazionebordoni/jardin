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

import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

public class SimpleFieldSet extends FieldSet {

  private static int defaultWidth = 80;
  private static int labelWidth = 130;
  private static int padding = 0;

  public SimpleFieldSet(final String heading) {
    this(heading, defaultWidth, labelWidth, padding);
  }

  public SimpleFieldSet(final String heading, final int defaultWidth,
      final int labelWidth, final int padding) {
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
