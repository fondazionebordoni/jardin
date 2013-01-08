package it.fub.jardin.client.model;

import java.io.Serializable;

public class ResourcePermissions implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -3074222402338693785L;

  private int resourceId;
  private int groupId;
  private boolean readperm;
  private boolean deleteperm;
  private boolean modifyperm;
  private boolean insertperm;
  
  
  public ResourcePermissions() {};
  
  public ResourcePermissions(boolean r, boolean d, boolean m , boolean i) {
    setReadperm(r);
    setDeleteperm(d);
    setModifyperm(m);
    setInsertperm(i);
  };
  
  /**
   * @return the resourceId
   */
  public int getResourceId() {
    return resourceId;
  }
  /**
   * @param resourceId the resourceId to set
   */
  public void setResourceId(int resourceId) {
    this.resourceId = resourceId;
  }
  /**
   * @return the groupId
   */
  public int getGroupId() {
    return groupId;
  }
  /**
   * @param groupId the groupId to set
   */
  public void setGroupId(int groupId) {
    this.groupId = groupId;
  }
  /**
   * @return the readperm
   */
  public boolean isReadperm() {
    return readperm;
  }
  /**
   * @param readperm the readperm to set
   */
  public void setReadperm(boolean readperm) {
    this.readperm = readperm;
  }
  /**
   * @return the deleteperm
   */
  public boolean isDeleteperm() {
    return deleteperm;
  }
  /**
   * @param deleteperm the deleteperm to set
   */
  public void setDeleteperm(boolean deleteperm) {
    this.deleteperm = deleteperm;
  }
  /**
   * @return the modifyperm
   */
  public boolean isModifyperm() {
    return modifyperm;
  }
  /**
   * @param modifyperm the modifyperm to set
   */
  public void setModifyperm(boolean modifyperm) {
    this.modifyperm = modifyperm;
  }
  /**
   * @return the insertperm
   */
  public boolean isInsertperm() {
    return insertperm;
  }
  /**
   * @param insertperm the insertperm to set
   */
  public void setInsertperm(boolean insertperm) {
    this.insertperm = insertperm;
  }
}
