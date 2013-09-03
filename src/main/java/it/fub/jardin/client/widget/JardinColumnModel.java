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

import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.tools.FieldDataType;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;

public class JardinColumnModel extends ColumnModel {

  public JardinColumnModel(final ResultsetImproved resultset) {
    super(new ArrayList<ColumnConfig>());

    /* Crea un ColumnConfig per ogni campo del resultset */
    for (ResultsetField field : resultset.getFields()) {
      if (field.getReadperm()) {
        List<String> values = new ArrayList<String>();
        // resultset.getForeignKeyList().getValues(field.getId());
        ColumnConfig column = null;
//        if (field.getSpecificType().compareToIgnoreCase(FieldDataType.BOOLEAN) == 0) {
//          column = new JardinCheckColumnConfig(field, values);
//        } else
          column = new JardinColumnConfig(field, values);
        // ColumnConfig column = new JardinColumnConfig(field);

        column.setHidden(!field.getVisible());
        this.configs.add(column);
      }

    }
  }

  @Override
  public JardinColumnConfig getColumn(final int colIndex) {
    return (JardinColumnConfig) this.configs.get(colIndex);
  }

}
