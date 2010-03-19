package it.fub.jardin.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;
/**
 * @author mavellino
 * 
 */
public class Plugin implements IsSerializable {

  private String pluginName;
  private String configFile;
  private String type;
  private String note;
  private int pluginId;
  private int rsId;
  private int GId;

  private Plugin() {
  }

  public Plugin(String pluginName, String configFile, String type,
      String note, int pluginId, int rsId, int gId) {
    this.pluginName = pluginName;
    this.configFile = configFile;
    this.type = type;
    this.note = note;
    this.pluginId = pluginId;
    this.rsId = rsId;
    GId = gId;
  }
  
  public String getPluginName() {
    return pluginName;
  }

  public void setPluginName(String pluginName, String configFile, String type) {
    this.pluginName = pluginName;
  }

  public String getConfigFile() {
    return configFile;
  }

  public void setConfigFile(String configFile) {
    this.configFile = configFile;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public int getPluginId() {
    return pluginId;
  }

  public void setPluginId(int pluginId) {
    this.pluginId = pluginId;
  }

  public int getRsId() {
    return rsId;
  }

  public void setRsId(int rsId) {
    this.rsId = rsId;
  }

  public int getGId() {
    return GId;
  }

  public void setGId(int gId) {
    GId = gId;
  }
}
