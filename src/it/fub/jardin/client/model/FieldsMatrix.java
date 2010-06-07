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

package it.fub.jardin.client.model;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FieldsMatrix implements IsSerializable {

  private static final long serialVersionUID = 1L;

  private final HashMap<Integer, List<String>> matrix;

  public FieldsMatrix() {
    this.matrix = new HashMap<Integer, List<String>>();
  }

  public void addField(final Integer fieldId, final List<String> values) {
    this.matrix.put(fieldId, values);
  }

  public void removeField(final Integer fieldId) {
    this.matrix.remove(fieldId);
  }

  public List<String> getValues(final Integer fieldId) {
    return this.matrix.get(fieldId);
  }

}
