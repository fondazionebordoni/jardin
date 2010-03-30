/**
 * 
 */
package it.fub.jardin.server;

import it.fub.jardin.client.exception.HiddenException;
import it.fub.jardin.client.exception.VisibleException;
import it.fub.jardin.client.model.Credentials;
import it.fub.jardin.client.model.Template;
import it.fub.jardin.client.widget.UploadDialog;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.allen_sauer.gwt.log.client.Log;

/**
 * @author acozzolino
 * 
 */
public class Upload extends HttpServlet {
  private static final long serialVersionUID = 6098745782027999297L;
  private static final int MAX_SIZE = 30 * 1024 * 1024; // 30MB

  private String type = null;
  private String ts = null;
  private String fs = null;
  private int resultset = 0;
  private Credentials credentials = null;
  private String condition = null;
  private String tipologia;

  public void doPost(HttpServletRequest request, HttpServletResponse response)
  /* throws ServletException, IOException */{

    // Create a factory for disk-based file items
    DiskFileItemFactory factory = new DiskFileItemFactory();

    // Create a new file upload handler
    ServletFileUpload upload = new ServletFileUpload(factory);

    // Set overall request size constraint
    upload.setSizeMax(MAX_SIZE);

    String m = null;
    try {

      // Parse the request
      List<?> /* FileItem */items = upload.parseRequest(request);

      // Process the uploaded items
      Iterator<?> iter = items.iterator();
      while (iter.hasNext()) {
        FileItem item = (FileItem) iter.next();

        if (item.isFormField()) {
          processFormField(item);
        } else {
          m = processUploadedFile(item);
        }
      }
      response.setContentType("text/plain");
      response.getWriter().write(m);
    } catch (Exception e) {
      Log.warn("Errore durante l'upload del file", e);
    }
  }

  /**
   * Processa i valori ricevuti dalla form. I nomi dei campi vengono dalle
   * proprietà statiche dal dialog di upload (dove sono preceduti dala stringa
   * "FIELD_")
   * 
   * @param item
   *          il singolo campo ricevuto dal dialog di upload
   */
  private void processFormField(FileItem item) {
    String name = item.getFieldName();
    String value = item.getString();
    if (name.compareTo(UploadDialog.FIELD_TYPE) == 0) {
      this.type = value;
      Log.debug("TYPE: " + this.type);
    } else if (name.compareTo(UploadDialog.FIELD_RESULTSET) == 0) {
      this.resultset = Integer.parseInt(value);
      Log.debug("RESULTSET: " + this.resultset);
    } else if (name.compareTo(UploadDialog.FIELD_CREDENTIALS) == 0) {
      this.credentials = Credentials.parseCredentials(value);
      // Log.debug("USER: " + this.credentials.getUsername() + "; PASS: "
      // + this.credentials.getPassword());
    } else if (name.compareTo("textSep") == 0) {
      this.ts = value;
    } else if (name.compareTo("fieldSep") == 0) {
      this.fs = value;
    } else if (name.compareTo("limit") == 0) {
      this.tipologia = name;
    } else if (name.compareTo("fix") == 0) {
      this.tipologia = name;
    } else if (name.compareTo("condition") == 0) {
      this.condition = value;
    } else {
      Log.debug("attenzione campo non riconosciuto: " + name + "--->" + value);
    }
  }

  /**
   * Processa i file ricevuti dal dialog di upload. Le azioni da eseguire con i
   * file sono definite dal campo FIELD_TYPE del dialog di upload. I nomi delle
   * azioni da eseguire sono definite nelle proprieta statiche del dialog di
   * upload (dove sono precedute dalla stringa "TYPE_")
   * 
   * @param item
   *          il singolo campo contenente il nome del file da processare
   * @return una stringa con l'esito dell'operazione compiuta sul file. Un
   *         risultato positivo è preceduto dalla stringa definita nelle
   *         proprietà statiche del dialog di upload
   */
  private String processUploadedFile(FileItem item) {
    if (!isContentTypeAcceptable(item)) {
      return "Errore. Il file da caricare non è di un tipo consentito";
    }
    if (!isSizeAcceptable(item)) {
      return "Errore. Il file da caricare è troppo grande. Dimensione massima di upload: "
          + MAX_SIZE + " byte";
    }
    if (ts.length() != 1) {
      return "Il separatore di testo deve essere composto da un carattere";
    } else if (fs.length() != 1) {
      return "Il separatore di campo deve essere composto da un carattere";
    } else if (ts.compareToIgnoreCase(fs) == 0) {
      return "Il separatore di campo e il separatore di testo non possono essere uguali";
    }

    String name = item.getName();
    File f = null;

    if (this.resultset > 0) {
      name = this.resultset + "_" + name;
    }

    try {
      if (this.type != null) {
        String root = this.getServletContext().getRealPath("/");

        /* Decisione del posto dove salvare il file */
        if (this.type.compareTo(UploadDialog.TYPE_TEMPLATE) == 0) {
          f = new File(root + Template.TEMPLATE_DIR + name);
        } else if ((this.type.compareTo(UploadDialog.TYPE_IMPORT) == 0)
            || (this.type.compareTo(UploadDialog.TYPE_INSERT) == 0)) {
          f = File.createTempFile(name, "");
        } else {
          Log.warn("Azione da eseguire con il file '" + this.type
              + "' non riconosciuta");
          return "Errore. Non è possibile decidere quale azione eseguire con il file";
        }

        /* Creazione del file sul server */
        item.write(f);

        /* Decisione delle azioni da eseguire con il file */
        if (this.type.compareToIgnoreCase(UploadDialog.TYPE_IMPORT) == 0) {
          (new DbUtils()).importFile(this.credentials, this.resultset, f, ts,
              fs, tipologia, UploadDialog.TYPE_IMPORT, condition);
          return UploadDialog.SUCCESS
              + "Importazione dati avvenuta con successo";
        } else if (this.type.compareToIgnoreCase(UploadDialog.TYPE_INSERT) == 0) {
          (new DbUtils()).importFile(this.credentials, this.resultset, f, ts,
              fs, tipologia, UploadDialog.TYPE_INSERT, condition);
          return UploadDialog.SUCCESS
              + "Importazione dati avvenuta con successo";
        } else {
          return UploadDialog.SUCCESS + "Upload del file avvenuto con successo";
        }
      } else {
        /* Non esiste specifica del campo type */
        Log.warn("Manca il tipo di azione da eseguire con il file");
        return "Errore. Non è possibile decidere quale azione eseguire con il file";
      }
    } catch (HiddenException e) {
      Log.warn("Errore durante il caricamento dei dati", e);
      return "Errore. " + e.getLocalizedMessage();
    } catch (ArrayIndexOutOfBoundsException ex) {
      Log.warn("Troppi campi nel file", ex);
      return "Troppi campi nel file";
    } catch (Exception e) {
      Log.warn("Errore durante l'upload del file", e);
      return "Errore. Impossibile salvare il file sul server";
    }
  }

  private boolean isSizeAcceptable(FileItem item) {
    return item.getSize() <= MAX_SIZE;
  }

  private boolean isContentTypeAcceptable(FileItem item) {
    return true;
    // return item.getContentType().equals(ACCEPTABLE_CONTENT_TYPE);
  }

}