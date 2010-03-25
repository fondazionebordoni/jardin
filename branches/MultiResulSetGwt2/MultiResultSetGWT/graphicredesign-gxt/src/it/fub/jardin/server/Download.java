/**
 * 
 */
package it.fub.jardin.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author gpantanetti
 * 
 */
public class Download extends HttpServlet {

  private static final long serialVersionUID = 1L;

  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {

    BufferedInputStream in = null;
    BufferedOutputStream out = null;

    /* Apri il file, controlla il tipo e imposta il content-type corretto */
    File f =
        new File(this.getServletContext().getRealPath("/")
            + req.getParameter("file"));
    if (!f.exists()) {
      printError(res, "File " + f.getName() + " doesn't exist");
      return;
    }
    if (!f.canRead()) {
      printError(res, "Can't read file " + f.getName());
      return;
    }

    /* Stampa il file in output */
    res.setContentType(getMimeType(f.getName()));
    res.setHeader("Pragma", "no-cache");
    res.setHeader("Content-Disposition", "attachment; filename=" + f.getName());

    try {
      URLConnection urlc = f.toURI().toURL().openConnection();
      int length = urlc.getContentLength();
      res.setContentLength(length);

      in = new BufferedInputStream(urlc.getInputStream());
      out = new BufferedOutputStream(res.getOutputStream());

      byte[] buff = new byte[length];
      int bytesRead;
      // Simple read/write loop.
      while (-1 != (bytesRead = in.read(buff, 0, buff.length))) {
        out.write(buff, 0, bytesRead);
      }

    } finally {
      if (out != null) {
        out.close();
      }
      if (in != null) {
        in.close();
      }
    }

  }

  public String getServletInfo() {
    return "Download Servlet for Jardin (c) 2009 Fondazione Ugo Bordoni";
  }

  private void printError(HttpServletResponse res, String error)
      throws IOException {
    res.setContentType("text/html");
    res.setHeader("pragma", "no-cache");
    PrintWriter out = res.getWriter();
    out.println("<html><head><title>Error!</title>" + "</head><body>" + error
        + "</body></html>");
    return;

  }

  /*
   * This Method Returns the right MIME type for a particular format <p>
   * 
   * @param String format ex: xml or HTML etc.
   * 
   * @return String MIMEtype
   */
  private String getMimeType(String filename) {
    String format = filename.substring(filename.lastIndexOf('.'));

    if (format.equalsIgnoreCase("pdf")) // check the out type
      return "application/pdf";
    else if (format.equalsIgnoreCase("wav"))
      return "audio/wav";
    else if (format.equalsIgnoreCase("gif"))
      return "image/gif";
    else if (format.equalsIgnoreCase("jpg"))
      return "image/jpeg";
    else if (format.equalsIgnoreCase("bmp"))
      return "image/bmp";
    else if (format.equalsIgnoreCase("png"))
      return "image/x-png";
    else if (format.equalsIgnoreCase("avi"))
      return "video/avi";
    else if (format.equalsIgnoreCase("mpg"))
      return "video/mpeg";
    else if (format.equalsIgnoreCase("html"))
      return "text/html";
    else if (format.equalsIgnoreCase("xml"))
      return "text/xml";
    else if (format.equalsIgnoreCase("csv"))
      return "text/csv";
    else
      return null;
  }

}
