/**
 * 
 */
package it.fub.jardin.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author gpantanetti
 * 
 */
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
  public Template(String ext, String name, String info, String xsl) {
    this.ext = ext;
    this.name = name;
    this.info = info;
    this.xsl = xsl;
  }

  public Template(Template template) {
    this.ext = template.getExt();
    this.name = template.getName();
    this.info = template.getInfo();
    this.xsl = template.getXsl();
  }

  public String getExt() {
    return ext;
  }

  public String getName() {
    return name;
  }

  public String getInfo() {
    return info;
  }

  public String getXsl() {
    return xsl;
  }

  public void setXsl(String xsl) {
    this.xsl = xsl;
  }

  public String toString() {
    return name;
  }
}
