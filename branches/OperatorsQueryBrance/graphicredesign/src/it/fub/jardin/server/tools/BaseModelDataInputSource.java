/**
 * 
 */
package it.fub.jardin.server.tools;

import java.util.List;

import org.xml.sax.InputSource;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * @author gpantanetti
 */
public class BaseModelDataInputSource extends InputSource {

  /**
   * @uml.property name="record"
   */
  private List<BaseModelData> records;

  public BaseModelDataInputSource(List<BaseModelData> records) {
    this.records = records;
  }

  /**
   * @param records
   *          the record to set
   * @uml.property name="record"
   */
  public void setRecords(List<BaseModelData> records) {
    this.records = records;
  }

  /**
   * @return the record
   * @uml.property name="record"
   */
  public List<BaseModelData> getRecords() {
    return this.records;
  }

}
