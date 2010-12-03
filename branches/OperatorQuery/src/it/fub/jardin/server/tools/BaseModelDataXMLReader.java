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

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class BaseModelDataXMLReader extends AbstractObjectReader {

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
   */
  @Override
  public void parse(final InputSource input) throws IOException, SAXException {
    if (input instanceof BaseModelDataInputSource) {
      this.parse(((BaseModelDataInputSource) input).getRecords());
    } else {
      throw new SAXException("Unsupported InputSource specified. "
          + "Must be a ListBaseModelDataInputSource");
    }
  }

  private void parse(final List<BaseModelData> records) throws SAXException {
    if (records == null) {
      throw new NullPointerException("Parameter list must not be null");
    }
    if (this.handler == null) {
      throw new IllegalStateException("ContentHandler not set");
    }

    // Start the document
    this.handler.startDocument();

    this.handler.startElement("items");
    // Generate SAX events for single item
    this.generateFor(records);
    this.handler.endElement("items");

    // End the document
    this.handler.endDocument();

  }

  private void generateFor(final List<BaseModelData> list) throws SAXException {
    if (list == null) {
      throw new NullPointerException("Parameter list must not be null");
    }
    if (this.handler == null) {
      throw new IllegalStateException("ContentHandler not set");
    }

    for (BaseModelData m : list) {
      this.generateFor(m);
    }

  }

  private void generateFor(final BaseModelData data) throws SAXException {
    if (data == null) {
      throw new NullPointerException("Parameter data must not be null");
    }
    if (this.handler == null) {
      throw new IllegalStateException("ContentHandler not set");
    }

    this.handler.startElement("item");
    /* Traverse data printing properties with their values */
    for (Entry<String, Object> e : data.getProperties().entrySet()) {
      this.handler.element(e.getKey(), String.valueOf(e.getValue()));
    }
    this.handler.endElement("item");
  }

}
