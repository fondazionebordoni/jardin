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

public class Template implements IsSerializable {

  private String ext;
  private String name;
  private String info;
  private String xsl;

  public static String TEMPLATE_DIR = "/templates/";
  public static Template CSV =
      new Template("csv", "CSV", "Comma Separated Values", null);
  public static Template XML =
      new Template("xml", "XML", "Extensible Markup Language", null);
  public static Template DEFAULT =
      new Template("pdf", "Default PDF", "Default PDF Export", null);

  @SuppressWarnings("unused")
  private Template() {
    /*
     * As of GWT 1.5, it must have a default (zero argument) constructor (with
     * any access modifier) or no constructor at all.
     */
  }

  /**
   * @param ext
   * @param info
   * @param xsl
   */
  public Template(final String ext, final String name, final String info,
      final String xsl) {
    this.ext = ext;
    this.name = name;
    this.info = info;
    this.xsl = xsl;
  }

  public Template(final Template template) {
    this.ext = template.getExt();
    this.name = template.getName();
    this.info = template.getInfo();
    this.xsl = template.getXsl();
  }

  public String getExt() {
    return this.ext;
  }

  public String getName() {
    return this.name;
  }

  public String getInfo() {
    return this.info;
  }

  public String getXsl() {
    return this.xsl;
  }

  public void setXsl(final String xsl) {
    this.xsl = xsl;
  }

  @Override
  public String toString() {
    return this.name;
  }
}
