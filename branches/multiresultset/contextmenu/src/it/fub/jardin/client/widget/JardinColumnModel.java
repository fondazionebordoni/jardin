/**
 * 
 */
package it.fub.jardin.client.widget;

import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetImproved;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;

/**
 * @author acozzolino
 */
public class JardinColumnModel extends ColumnModel {

  public JardinColumnModel(ResultsetImproved resultset) {
    super(new ArrayList<ColumnConfig>());

    /* Crea un ColumnConfig per ogni campo del resultset */
    for (ResultsetField field : resultset.getFields()) {

      List<String> values =
          resultset.getForeignKeyList().getValues(field.getId());

      ColumnConfig column = new JardinColumnConfig(field, values);
      // ColumnConfig column = new JardinColumnConfig(field);
      column.setHidden(!(boolean) field.getVisible());
      this.configs.add(column);
    }
  }

  @Override
  public JardinColumnConfig getColumn(int colIndex) {
    return (JardinColumnConfig) this.configs.get(colIndex);
  }

}
