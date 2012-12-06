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

import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ResultsetFieldGroupings extends BaseTreeModel implements
    IsSerializable {

  // TODO non usare i BaseTreeModel, creare una classe semplice che implementi
  // IsSerializable

  private static final long serialVersionUID = 3101953646052145987L;

  public ResultsetFieldGroupings() {
  }

  public ResultsetFieldGroupings(final Map<String, Object> properties) {
    super(properties);
  }

  public ResultsetFieldGroupings(final Integer id, final String name,
      final String alias) {
    this.set("id", id);
    this.set("name", name);
    this.set("alias", alias);
  }

  public void setAlias(final String alias) {
    this.set("alias", alias);
  }

  public String getAlias() {
    return this.get("alias");
  }

  public void setId(final Integer id) {
    this.set("id", id);
  }

  public Integer getId() {
    return this.get("id");
  }

  public void setName(final String name) {
    this.set("name", name);
  }

  public String getName() {
    return this.get("name");
  }
}
