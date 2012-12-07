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
import java.util.Map;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * This class can be used as base class for XMLReaders that generate SAX events
 * from Java objects.
 */

public abstract class AbstractObjectReader implements XMLReader {

  private static final String NAMESPACES =
      "http://xml.org/sax/features/namespaces";
  private static final String NS_PREFIXES =
      "http://xml.org/sax/features/namespace-prefixes";

  private final Map<String, Boolean> features =
      new java.util.HashMap<String, Boolean>();
  private ContentHandler orgHandler;

  /**
   * Proxy for easy SAX event generation
   * 
   * @uml.property name="handler"
   * @uml.associationEnd
   */
  protected EasyGenerationContentHandlerProxy handler;
  /**
   * Error handler
   * 
   * @uml.property name="errorHandler"
   */
  protected ErrorHandler errorHandler;

  /**
   * Constructor for the AbstractObjectReader object
   */
  public AbstractObjectReader() {
    this.setFeature(NAMESPACES, false);
    this.setFeature(NS_PREFIXES, false);
  }

  /* ============ XMLReader interface ============ */

  /**
   * @see org.xml.sax.XMLReader#getContentHandler()
   */
  public ContentHandler getContentHandler() {
    return this.orgHandler;
  }

  /**
   * @see org.xml.sax.XMLReader#setContentHandler(ContentHandler)
   */
  public void setContentHandler(final ContentHandler handler) {
    this.orgHandler = handler;
    this.handler = new EasyGenerationContentHandlerProxy(handler);
  }

  /**
   * @see org.xml.sax.XMLReader#getErrorHandler()
   * @uml.property name="errorHandler"
   */
  public ErrorHandler getErrorHandler() {
    return this.errorHandler;
  }

  /**
   * @see org.xml.sax.XMLReader#setErrorHandler(ErrorHandler)
   * @uml.property name="errorHandler"
   */
  public void setErrorHandler(final ErrorHandler handler) {
    this.errorHandler = handler;
  }

  /**
   * @see org.xml.sax.XMLReader#getDTDHandler()
   */
  public DTDHandler getDTDHandler() {
    return null;
  }

  /**
   * @see org.xml.sax.XMLReader#setDTDHandler(DTDHandler)
   */
  public void setDTDHandler(final DTDHandler handler) {
  }

  /**
   * @see org.xml.sax.XMLReader#getEntityResolver()
   */
  public EntityResolver getEntityResolver() {
    return null;
  }

  /**
   * @see org.xml.sax.XMLReader#setEntityResolver(EntityResolver)
   */
  public void setEntityResolver(final EntityResolver resolver) {
  }

  /**
   * @see org.xml.sax.XMLReader#getProperty(String)
   */
  public Object getProperty(final java.lang.String name) {
    return null;
  }

  /**
   * @see org.xml.sax.XMLReader#setProperty(String, Object)
   */
  public void setProperty(final java.lang.String name,
      final java.lang.Object value) {
  }

  /**
   * @see org.xml.sax.XMLReader#getFeature(String)
   */
  public boolean getFeature(final java.lang.String name) {
    return (this.features.get(name)).booleanValue();
  }

  /**
   * Returns true if the NAMESPACES feature is enabled.
   * 
   * @return boolean true if enabled
   */
  protected boolean isNamespaces() {
    return this.getFeature(NAMESPACES);
  }

  /**
   * Returns true if the MS_PREFIXES feature is enabled.
   * 
   * @return boolean true if enabled
   */
  protected boolean isNamespacePrefixes() {
    return this.getFeature(NS_PREFIXES);
  }

  /**
   * @see org.xml.sax.XMLReader#setFeature(String, boolean)
   */
  public void setFeature(final java.lang.String name, final boolean value) {
    this.features.put(name, new Boolean(value));
  }

  /**
   * @see org.xml.sax.XMLReader#parse(String)
   */
  public void parse(final String systemId) throws IOException, SAXException {
    throw new SAXException(this.getClass().getName()
        + " cannot be used with system identifiers (URIs)");
  }

  /**
   * @see org.xml.sax.XMLReader#parse(InputSource)
   */
  public abstract void parse(InputSource input) throws IOException,
      SAXException;

}
