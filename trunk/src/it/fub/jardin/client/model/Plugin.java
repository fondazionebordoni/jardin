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

import com.google.gwt.user.client.rpc.IsSerializable;


public class Plugin implements IsSerializable {

  private static final long serialVersionUID = -6542980077484895269L;
  
  private String pluginName;
  private String configFile;
  private String type;
  private String note;
  private int pluginId;
  private int rsId;
  private int GId;
  
  @SuppressWarnings("unused")
  private Plugin() {
    // As of GWT 1.5, it must have a default (zero argument) constructor
    // (with any access modifier) or no constructor at all.
  }

  public Plugin(final String pluginName, final String configFile,
      final String type, final String note, final int pluginId, final int rsId,
      final int gId) {
    this.pluginName = pluginName;
    this.configFile = configFile;
    this.type = type;
    this.note = note;
    this.pluginId = pluginId;
    this.rsId = rsId;
    this.GId = gId;
  }

  public String getPluginName() {
    return this.pluginName;
  }

  public void setPluginName(final String pluginName, final String configFile,
      final String type) {
    this.pluginName = pluginName;
  }

  public String getConfigFile() {
    return this.configFile;
  }

  public void setConfigFile(final String configFile) {
    this.configFile = configFile;
  }

  public String getType() {
    return this.type;
  }

  public void setType(final String type) {
    this.type = type;
  }

  public String getNote() {
    return this.note;
  }

  public void setNote(final String note) {
    this.note = note;
  }

  public int getPluginId() {
    return this.pluginId;
  }

  public void setPluginId(final int pluginId) {
    this.pluginId = pluginId;
  }

  public int getRsId() {
    return this.rsId;
  }

  public void setRsId(final int rsId) {
    this.rsId = rsId;
  }

  public int getGId() {
    return this.GId;
  }

  public void setGId(final int gId) {
    this.GId = gId;
  }
}
