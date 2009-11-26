/**
 * 
 */
package it.fub.jardin.server.tools;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * @author gpantanetti
 * 
 */
public class BaseModelDataXMLReader extends AbstractObjectReader {

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
   */
  public void parse(InputSource input) throws IOException, SAXException {
    if (input instanceof BaseModelDataInputSource) {
      parse(((BaseModelDataInputSource) input).getRecords());
    } else {
      throw new SAXException("Unsupported InputSource specified. "
          + "Must be a ListBaseModelDataInputSource");
    }
  }

  private void parse(List<BaseModelData> records) throws SAXException {
    if (records == null) {
      throw new NullPointerException("Parameter list must not be null");
    }
    if (handler == null) {
      throw new IllegalStateException("ContentHandler not set");
    }

    // Start the document
    handler.startDocument();

    handler.startElement("items");
    // Generate SAX events for single item
    generateFor(records);
    handler.endElement("items");

    // End the document
    handler.endDocument();

  }

  private void generateFor(List<BaseModelData> list) throws SAXException {
    if (list == null) {
      throw new NullPointerException("Parameter list must not be null");
    }
    if (handler == null) {
      throw new IllegalStateException("ContentHandler not set");
    }

    for (BaseModelData m : list) {
      generateFor(m);
    }

  }

  private void generateFor(BaseModelData data) throws SAXException {
    if (data == null) {
      throw new NullPointerException("Parameter data must not be null");
    }
    if (handler == null) {
      throw new IllegalStateException("ContentHandler not set");
    }

    handler.startElement("item");
    /* Traverse data printing properties with their values */
    for (Entry<String, Object> e : data.getProperties().entrySet()) {
      handler.element(e.getKey(), String.valueOf(e.getValue()));
    }
    handler.endElement("item");
  }

}
