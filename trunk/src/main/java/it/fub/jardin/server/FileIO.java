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
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * Some simple file IO primitives reimplemented in Java. All methods are static
 * since there is no state.
 */

public class FileIO {

  /** Copy a file from one filename to another */
  public static void copyFile(final String inName, final String outName)
      throws FileNotFoundException, IOException {
    BufferedInputStream is =
        new BufferedInputStream(new FileInputStream(inName));
    BufferedOutputStream os =
        new BufferedOutputStream(new FileOutputStream(outName));
    copyFile(is, os, true);

  }

  /** Copy a file from an opened InputStream to an opened OutputStream */
  public static void copyFile(final InputStream is, final OutputStream os,
      final boolean close) throws IOException {
    int b; // the byte read from the file
    while ((b = is.read()) != -1) {
      os.write(b);
    }
    is.close();
    if (close) {
      os.close();
    }
  }

  /** Copy a file from an opened Reader to an opened Writer */
  public static void copyFile(final Reader is, final Writer os,
      final boolean close) throws IOException {
    int b; // the byte read from the file
    while ((b = is.read()) != -1) {
      os.write(b);
    }
    is.close();
    if (close) {
      os.close();
    }
  }

  /** Copy a file from a filename to a PrintWriter. */
  public static void copyFile(final String inName, final PrintWriter pw,
      final boolean close) throws FileNotFoundException, IOException {
    BufferedReader ir = new BufferedReader(new FileReader(inName));
    copyFile(ir, pw, close);
  }

  /** Open a file and read the first line from it. */
  public static String readLine(final String inName)
      throws FileNotFoundException, IOException {
    BufferedReader is = new BufferedReader(new FileReader(inName));
    String line = null;
    line = is.readLine();
    is.close();
    return line;
  }

  /** The size of blocking to use */
  protected static final int BLKSIZ = 8192;

  /**
   * Copy a data file from one filename to another, alternate method. As the
   * name suggests, use my own buffer instead of letting the BufferedReader
   * allocate and use the buffer.
   */
  public void copyFileBuffered(final String inName, final String outName)
      throws FileNotFoundException, IOException {
    InputStream is = new FileInputStream(inName);
    OutputStream os = new FileOutputStream(outName);
    int count = 0; // the byte count
    byte[] b = new byte[BLKSIZ]; // the bytes read from the file
    while ((count = is.read(b)) != -1) {
      os.write(b, 0, count);
    }
    is.close();
    os.close();
  }

  /** Read the entire content of a Reader into a String */
  public static String readerToString(final Reader is) throws IOException {
    StringBuffer sb = new StringBuffer();
    char[] b = new char[BLKSIZ];
    int n;
    // Read a block. If it gets any chars, append them.
    while ((n = is.read(b)) > 0) {
      sb.append(b, 0, n);
    }
    // Only construct the String object once, here.
    return sb.toString();
  }

  /** Read the content of a Stream into a String */
  public static String inputStreamToString(final InputStream is)
      throws IOException {
    return readerToString(new InputStreamReader(is));
  }
}
