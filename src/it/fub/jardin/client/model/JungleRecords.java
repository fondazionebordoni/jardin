/**
 * 
 */
package it.fub.jardin.client.model;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.FieldType;

/**
 * @author gpantanetti
 * 
 */
public class JungleRecords extends DataSource {

  public JungleRecords(ResultsetImproved resultset, String URL) {

    // setID(resultset.getName());

    setDataFormat(DSDataFormat.XML);
    setRecordXPath("/items/item");

    DataSourceField[] list = new DataSourceField[resultset.getFields().size()];

    int i = 0;
    for (ResultsetField field : resultset.getFields()) {
      FieldType ft = FieldType.TEXT;

      if (field.getType().compareToIgnoreCase("int") == 0) {
        ft = FieldType.INTEGER;
      } else if (field.getType().compareToIgnoreCase("date") == 0) {
        ft = FieldType.DATE;
      } /*else if (field.getType().compareToIgnoreCase("datetime") == 0) {
        ft = FieldType.DATE;
      }*/ else if (field.getType().compareToIgnoreCase("real") == 0) {
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

    setDataURL(URL);
    setClientOnly(true);
  }

}
