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

public class Download extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(final HttpServletRequest req,
      final HttpServletResponse res) throws ServletException, IOException {

    BufferedInputStream in = null;
    BufferedOutputStream out = null;

    /* Apri il file, controlla il tipo e imposta il content-type corretto */
    File f =
        new File(this.getServletContext().getRealPath("/")
            + req.getParameter("file"));
    if (!f.exists()) {
      this.printError(res, "File " + f.getName() + " doesn't exist");
      return;
    }
    if (!f.canRead()) {
      this.printError(res, "Can't read file " + f.getName());
      return;
    }

    /* Stampa il file in output */
    res.setContentType(this.getMimeType(f.getName()));
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

  @Override
  public String getServletInfo() {
    return "Download Servlet for Jardin (c) 2009 Fondazione Ugo Bordoni";
  }

  private void printError(final HttpServletResponse res, final String error)
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
  private String getMimeType(final String filename) {
    String format = filename.substring(filename.lastIndexOf('.'));

    if (format.equalsIgnoreCase("pdf")) {
      return "application/pdf";
    } else if (format.equalsIgnoreCase("wav")) {
      return "audio/wav";
    } else if (format.equalsIgnoreCase("gif")) {
      return "image/gif";
    } else if (format.equalsIgnoreCase("jpg")) {
      return "image/jpeg";
    } else if (format.equalsIgnoreCase("bmp")) {
      return "image/bmp";
    } else if (format.equalsIgnoreCase("png")) {
      return "image/x-png";
    } else if (format.equalsIgnoreCase("avi")) {
      return "video/avi";
    } else if (format.equalsIgnoreCase("mpg")) {
      return "video/mpeg";
    } else if (format.equalsIgnoreCase("html")) {
      return "text/html";
    } else if (format.equalsIgnoreCase("xml")) {
      return "text/xml";
    } else if (format.equalsIgnoreCase("csv")) {
      return "text/csv";
    } else {
      return null;
    }
  }

}
