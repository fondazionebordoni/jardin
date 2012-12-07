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

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This class is an implementation of ContentHandler which acts as a proxy to
 * another ContentHandler and has the purpose to provide a few handy methods
 * that make life easier when generating SAX events. <br>
 * Note: This class is only useful for simple cases with no namespaces.
 */

public class EasyGenerationContentHandlerProxy implements ContentHandler {

  /** An empty Attributes object used when no attributes are needed. */
  public static final Attributes EMPTY_ATTS = new AttributesImpl();

  private final ContentHandler target;

  /**
   * Main constructor.
   * 
   * @param forwardTo
   *          ContentHandler to forward the SAX event to.
   */
  public EasyGenerationContentHandlerProxy(final ContentHandler forwardTo) {
    this.target = forwardTo;
  }

  /**
   * Sends the notification of the beginning of an element.
   * 
   * @param name
   *          Name for the element.
   * @throws SAXException
   *           Any SAX exception, possibly wrapping another exception.
   */
  public void startElement(final String name) throws SAXException {
    this.startElement(name, EMPTY_ATTS);
  }

  /**
   * Sends the notification of the beginning of an element.
   * 
   * @param name
   *          Name for the element.
   * @param atts
   *          The attributes attached to the element. If there are no
   *          attributes, it shall be an empty Attributes object.
   * @throws SAXException
   *           Any SAX exception, possibly wrapping another exception.
   */
  public void startElement(final String name, final Attributes atts)
      throws SAXException {
    this.startElement(null, name, name, atts);
  }

  /**
   * Send a String of character data.
   * 
   * @param s
   *          The content String
   * @throws SAXException
   *           Any SAX exception, possibly wrapping another exception.
   */
  public void characters(final String s) throws SAXException {
    this.target.characters(s.toCharArray(), 0, s.length());
  }

  /**
   * Send the notification of the end of an element.
   * 
   * @param name
   *          Name for the element.
   * @throws SAXException
   *           Any SAX exception, possibly wrapping another exception.
   */
  public void endElement(final String name) throws SAXException {
    this.endElement(null, name, name);
  }

  /**
   * Sends notifications for a whole element with some String content.
   * 
   * @param name
   *          Name for the element.
   * @param value
   *          Content of the element.
   * @throws SAXException
   *           Any SAX exception, possibly wrapping another exception.
   */
  public void element(final String name, final String value)
      throws SAXException {
    this.element(name, value, EMPTY_ATTS);
  }

  /**
   * Sends notifications for a whole element with some String content.
   * 
   * @param name
   *          Name for the element.
   * @param value
   *          Content of the element.
   * @param atts
   *          The attributes attached to the element. If there are no
   *          attributes, it shall be an empty Attributes object.
   * @throws SAXException
   *           Any SAX exception, possibly wrapping another exception.
   */
  public void element(final String name, final String value,
      final Attributes atts) throws SAXException {
    this.startElement(name, atts);
    if (value != null) {
      this.characters(value.toCharArray(), 0, value.length());
    }
    this.endElement(name);
  }

  /* =========== ContentHandler interface =========== */

  /**
   * @see org.xml.sax.ContentHandler#setDocumentLocator(Locator)
   */
  public void setDocumentLocator(final Locator locator) {
    this.target.setDocumentLocator(locator);
  }

  /**
   * @see org.xml.sax.ContentHandler#startDocument()
   */
  public void startDocument() throws SAXException {
    this.target.startDocument();
  }

  /**
   * @see org.xml.sax.ContentHandler#endDocument()
   */
  public void endDocument() throws SAXException {
    this.target.endDocument();
  }

  /**
   * @see org.xml.sax.ContentHandler#startPrefixMapping(String, String)
   */
  public void startPrefixMapping(final String prefix, final String uri)
      throws SAXException {
    this.target.startPrefixMapping(prefix, uri);
  }

  /**
   * @see org.xml.sax.ContentHandler#endPrefixMapping(String)
   */
  public void endPrefixMapping(final String prefix) throws SAXException {
    this.target.endPrefixMapping(prefix);
  }

  /**
   * @see org.xml.sax.ContentHandler#startElement(String, String, String,
   *      Attributes)
   */
  public void startElement(final String namespaceURI, final String localName,
      final String qName, final Attributes atts) throws SAXException {
    this.target.startElement(namespaceURI, localName, qName, atts);
  }

  /**
   * @see org.xml.sax.ContentHandler#endElement(String, String, String)
   */
  public void endElement(final String namespaceURI, final String localName,
      final String qName) throws SAXException {
    this.target.endElement(namespaceURI, localName, qName);
  }

  /**
   * @see org.xml.sax.ContentHandler#characters(char[], int, int)
   */
  public void characters(final char[] ch, final int start, final int length)
      throws SAXException {
    this.target.characters(ch, start, length);
  }

  /**
   * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
   */
  public void ignorableWhitespace(final char[] ch, final int start,
      final int length) throws SAXException {
    this.target.ignorableWhitespace(ch, start, length);
  }

  /**
   * @see org.xml.sax.ContentHandler#processingInstruction(String, String)
   */
  public void processingInstruction(final String target, final String data)
      throws SAXException {
    this.target.processingInstruction(target, data);
  }

  /**
   * @see org.xml.sax.ContentHandler#skippedEntity(String)
   */
  public void skippedEntity(final String name) throws SAXException {
    this.target.skippedEntity(name);
  }

}
