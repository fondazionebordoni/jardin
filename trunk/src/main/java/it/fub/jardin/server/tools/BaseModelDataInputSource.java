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

package it.fub.jardin.server.tools;

import java.util.List;

import org.xml.sax.InputSource;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class BaseModelDataInputSource extends InputSource {

  /**
   * @uml.property name="record"
   */
  private List<BaseModelData> records;

  public BaseModelDataInputSource(final List<BaseModelData> records) {
    this.records = records;
  }

  /**
   * @param records
   *          the record to set
   * @uml.property name="record"
   */
  public void setRecords(final List<BaseModelData> records) {
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
