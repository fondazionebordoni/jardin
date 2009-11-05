/**
 * 
 */
package it.fub.jardin.client.model;

import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author acozzolino
 * 
 */
public class ResultsetFieldGroupings extends BaseTreeModel implements
    IsSerializable {

  // TODO non usare i BaseTreeModel, creare una classe semplice che implementi IsSerializable
  
  private static final long serialVersionUID = 3101953646052145987L;

  public ResultsetFieldGroupings() {
  }

  public ResultsetFieldGroupings(Map<String, Object> properties) {
    super(properties);
  }

  public ResultsetFieldGroupings(Integer id, String name, String alias) {
    set("id", id);
    set("name", name);
    set("alias", alias);
  }

  public void setAlias(String alias) {
    set("alias", alias);
  }

  public String getAlias() {
    return get("alias");
  }

  public void setId(Integer id) {
    set("id", id);
  }

  public Integer getId() {
    return get("id");
  }

  public void setName(String name) {
    set("name", name);
  }

  public String getName() {
    return get("name");
  }
}
