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

package it.fub.jardin.server;

import it.fub.jardin.client.exception.VisibleException;
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.Template;
import it.fub.jardin.server.tools.BaseModelDataInputSource;
import it.fub.jardin.server.tools.BaseModelDataXMLReader;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * Static Class for writing/reading files.
 */
public class FileUtils {

  // public static final char CSV_SEPARATOR = '|';
  // public static final char CSV_WRAPPER = '"';
  public static final char CSV_REPLACER = '?';

  public static String createReport(final String file, final String xsl,
      final Template template, final List<BaseModelData> records,
      final List<String> columns, final char fs, final char ts)
      throws VisibleException {
    try {
      // TODO Possibile che non ci sia un modo migliore? Vedi EventList
      File f = createTempFile(file, template.getExt());
      ArrayList<BaseModelData> recordsToExport = new ArrayList<BaseModelData>();

      for (BaseModelData record : records) {
        /*
         * Create a collection of record data. Getting values from properties
         * ensures that values are correctly matched with corresponding property
         */
        BaseModelData bmd = new BaseModelData();
        for (String property : columns) {
          String value = String.valueOf(record.get(property));
          bmd.set(property, value);
        }
        recordsToExport.add(bmd);
      }
      if (template.getInfo().compareTo(Template.CSV.getInfo()) == 0) {
        writeCsvReport(f, recordsToExport, columns, fs, ts);
      } else if (template.getInfo().compareTo(Template.XML.getInfo()) == 0) {
        writeXmlReport(f, recordsToExport, columns);
      } else {
        writePdfReport(f, recordsToExport, new File(xsl));
      }
      return f.getPath();
    } catch (Exception e) {
      Log.error("Errore durante l'esportazione del file " + file, e);
      throw new VisibleException("Impossibile creare il file d'esportazione");
    }
  }

  private static File createTempFile(final String file, final String ext)
      throws IOException {

    File f = new File(file);
    String filename = f.getName();

    /* Aggancia la data al nome del file */
    if (filename == null) {
      filename = "export";
    }

    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmm");
    String date = formatter.format(new Date());
    // TODO effettuare la traslitterazione del nome del file
    filename = filename + "_" + date + "_";
    filename = filename.replace(' ', '.');
    filename = filename.replace('/', '.');
    // filename = filename.replace('\\', '.');
    filename = filename.toLowerCase();

    return File.createTempFile(filename, "." + ext, f.getParentFile());
  }

  /**
   * Save records on a new CSV file and returns the it's path. It uses semicolon
   * (;) as fields separator. Each field is wrapped by double quotes (")
   * 
   * @param filename
   * @param records
   *          the List of BaseModelData (taken from a store)
   * @param columns
   * @return full path for the created CSV file
   * @throws IOException
   */
  private static void writeCsvReport(final File f,
      final List<BaseModelData> records, final List<String> columns,
      final char fs, final char ts) throws IOException {

    char separator = fs;
    char wrapper = ts;
    char replacer = CSV_REPLACER;

    BufferedWriter out = new BufferedWriter(new FileWriter(f));

    try {
      /*
       * Take records' labels and put them on the first row of the CSV file
       */
      out.write(collection2Csv(columns, separator, wrapper, replacer));

      /* Put records' data on file */
      for (BaseModelData record : records) {
        /*
         * Create a collection of record data. Getting values from properties
         * ensures that values are correctly matched with corresponding property
         */
        Collection<String> c = new ArrayList<String>();
        for (String property : columns) {
          String value = String.valueOf(record.get(property));
          if ((value == null) || (value.compareToIgnoreCase("null") == 0)) {
            value = "";
          }
          c.add(value);
        }
        /* Write out data collection in the file using CSV creator */
        out.write(collection2Csv(c, separator, wrapper, replacer));
      }
    } catch (IOException e) {
      throw e;
    } finally {
      out.close();
    }
  }

  private static String collection2Csv(final Collection<String> c,
      final char separator, final char wrapper, final char replacer) {
    String s = "";

    for (String v : c) {
      if (wrapper != '\0') {
        v = v.replace(wrapper, replacer);
        s += wrapper + v + wrapper;
      } else {
        s += v;
      }
      if (separator != '\0') {
        v = v.replace(separator, replacer);
        s += separator;
      }
    }
    if (separator != '\0') {
      return s.substring(0, s.lastIndexOf(separator)) + "\r\n";
    } else {
      return s + "\r\n";
    }

  }

  private static void writeXmlReport(final File f,
      final List<BaseModelData> records, final List<String> columns)
      throws IOException, TransformerException {

    // Setup XSLT
    TransformerFactory factory = TransformerFactory.newInstance();
    Transformer transformer = factory.newTransformer();

    // Setup input
    Source src =
        new SAXSource(new BaseModelDataXMLReader(),
            new BaseModelDataInputSource(records));
    // Setup output
    Result res = new StreamResult(f);

    // Start XSLT transformation
    transformer.transform(src, res);
  }

  private static void writePdfReport(final File f,
      final List<BaseModelData> records, final File xsl)
      throws MalformedURLException, IOException, URISyntaxException,
      TransformerException, FOPException {

    Log.debug("Template: " + xsl.getAbsolutePath());
    if (xsl.exists() && xsl.canRead()) {

      // configure fopFactory as desired
      FopFactory fopFactory = FopFactory.newInstance();

      FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
      // configure foUserAgent as desired

      // Setup output
      OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
      try {
        // Construct fop with desired output format
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
        // Setup XSLT
        TransformerFactory factory = TransformerFactory.newInstance();
        FileReader fr = new FileReader(xsl);
        BufferedReader br = new BufferedReader(fr);
        StreamSource sr = new StreamSource(br);
        Transformer transformer = factory.newTransformer(sr);

        // Set the value of a <param> in the stylesheet
        transformer.setParameter("versionParam", "2.0");

        // Setup input for XSLT transformation
        Source src =
            new SAXSource(new BaseModelDataXMLReader(),
                new BaseModelDataInputSource(records));

        // Resulting SAX events (the generated FO) must be piped
        // through to FOP
        Result res = new SAXResult(fop.getDefaultHandler());

        // Start XSLT transformation and FOP processing
        transformer.transform(src, res);
      } catch (TransformerConfigurationException e) {
        throw e;
      } catch (FOPException e) {
        throw e;
      } finally {
        /* Close the file */
        out.close();
      }
    } else {
      throw new IOException("Il file del template non è accessibile");
    }
  }

  public static void prepareDefaultTemplate(final ResultsetImproved resultset,
      final String xsl, final List<String> columns) throws IOException {

    final String TITLE = "$$TITLE$$";
    final String ROW_BEGIN = "$$ROW_BEGIN$$";
    final String ROW_END = "$$ROW_END$$";
    final String LABEL = "$$LABEL_";
    final String VALUE = "$$VALUE_";

    /*
     * Se il file del template esiste già non lo creare e restituisci il
     * template
     */
    Log.debug("XSL file: " + xsl);
    File f = new File(xsl);
    /*
     * if (f.exists() && f.canRead()) { Log.debug("XSL file " + xsl +
     * " already exists. I do not create it."); return; }
     */

    /*
     * ----------------------------------------------------------------------
     * --- Creazione del template di default --------------------------------
     * -----------------------------------------
     */

    /* Prendi lo scheletro dell'XSL di default */
    Reader is = new FileReader(f.getParent() + "/default.xsl");
    String input = FileIO.readerToString(is);

    String header = input.substring(0, input.indexOf(ROW_BEGIN) - 1);
    header =
        header.replaceFirst(Matcher.quoteReplacement(TITLE),
            resultset.getAlias());
    String row =
        input.substring(input.indexOf(ROW_BEGIN) + ROW_BEGIN.length(),
            input.indexOf(ROW_END) - 1);
    String footer = input.substring(input.indexOf(ROW_END) + ROW_END.length());

    /* if (f.canWrite()) { */
    BufferedWriter out = new BufferedWriter(new FileWriter(f));
    out.write(header);

    int i = 1;
    String tmp = row;
    for (ResultsetField field : resultset.getFields()) {
      if (field.getReadperm() && columns.contains(field.getName())) {
        tmp =
            tmp.replaceFirst(Matcher.quoteReplacement(LABEL + i + "$$"),
                field.getAlias());
        tmp =
            tmp.replaceFirst(Matcher.quoteReplacement(VALUE + i + "$$"),
                field.getName());
        if (i % 4 == 0) {
          out.write(tmp);
          tmp = row;
          i = 0;
        }
        i++;
      }
    }

    if (--i % 4 != 0) {
      for (int j = i; j <= 4; j++) {
        tmp = tmp.replaceFirst(Matcher.quoteReplacement(LABEL + j + "$$"), "");
        tmp = tmp.replaceFirst(Matcher.quoteReplacement(VALUE + j + "$$"), "@");
      }
      out.write(tmp);
    }
    out.write(footer);
    out.close();
    return;
    /*
     * } else { throw new IOException("Can't write on file " + xsl); }
     */
  }
}
