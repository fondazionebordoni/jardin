/**
 * 
 */
package it.fub.jardin.client.model;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author acozzolino
 * 
 */
public class FieldsMatrix implements IsSerializable {

  private static final long serialVersionUID = 1L;

  private HashMap<Integer, List<String>> matrix;

  public FieldsMatrix() {
    this.matrix = new HashMap<Integer, List<String>>();
  }

  public void addField(Integer fieldId, List<String> values) {
    this.matrix.put(fieldId, values);
  }

  public void removeField(Integer fieldId) {
    this.matrix.remove(fieldId);
  }

  public List<String> getValues(Integer fieldId) {
    return this.matrix.get(fieldId);
  }

}
