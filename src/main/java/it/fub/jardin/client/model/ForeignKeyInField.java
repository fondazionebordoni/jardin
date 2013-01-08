/**
 * 
 */
package it.fub.jardin.client.model;

import java.io.Serializable;

/**
 * @author acozzolino
 *
 */
public class ForeignKeyInField extends ResultsetPlusField implements
    Serializable {

  /**
   * @param id
   * @param name
   * @param alias
   * @param resultsetid
   * @param defaultheader
   * @param searchgrouping
   * @param idgrouping
   * @param readperm
   * @param deleteperm
   * @param modifyperm
   * @param insertperm
   * @param visible
   */
  public ForeignKeyInField(Integer id, String name, String alias,
      Integer resultsetid, boolean defaultheader, Integer searchgrouping,
      Integer idgrouping, boolean readperm, boolean deleteperm,
      boolean modifyperm, boolean insertperm, boolean visible) {
    super(id, name, alias, resultsetid, defaultheader, searchgrouping,
        idgrouping, readperm, deleteperm, modifyperm, insertperm, visible);
    // TODO Auto-generated constructor stub
  }

}
