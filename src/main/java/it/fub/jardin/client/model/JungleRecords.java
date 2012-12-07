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

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.FieldType;

public class JungleRecords extends DataSource {

  public JungleRecords(final ResultsetImproved resultset, final String URL) {

    // setID(resultset.getName());

    this.setDataFormat(DSDataFormat.XML);
    this.setRecordXPath("/items/item");

    DataSourceField[] list = new DataSourceField[resultset.getFields().size()];

    int i = 0;
    for (ResultsetField field : resultset.getFields()) {
      FieldType ft = FieldType.TEXT;

      if (field.getType().compareToIgnoreCase("int") == 0) {
        ft = FieldType.INTEGER;
      } else if (field.getType().compareToIgnoreCase("date") == 0) {
        ft = FieldType.DATE;
      } else if (field.getType().compareToIgnoreCase("datetime") == 0) {
        ft = FieldType.DATETIME;
      } else if (field.getType().compareToIgnoreCase("real") == 0) {
        ft = FieldType.FLOAT;
      } else if (field.getType().compareToIgnoreCase("link") == 0) {
        ft = FieldType.LINK;
      } else if (field.getType().compareToIgnoreCase("bool") == 0) {
        ft = FieldType.BOOLEAN;
      } else if (field.getType().compareToIgnoreCase("blob") == 0) {
        ft = FieldType.BINARY;
      }

      DataSourceField f =
          new DataSourceField(field.getName(), ft, field.getAlias());
      if (field.getIsPK()) {
        f.setPrimaryKey(true);
      }
      if (!field.getVisible()) {
        f.setHidden(true);
      }

      list[i++] = f;
    }

    this.setFields(list);

    this.setDataURL(URL);
    this.setClientOnly(true);
  }

}
