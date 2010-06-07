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

package it.fub.jardin.client.widget;

public class Coordinate {

  private final String coord;

  public Coordinate(final String value) {
    this.coord = value;
  }

  public String CoordinateParser() {
    if (this.isValid()) {
      return this.coord.substring(0, 2) + "+" + this.coord.substring(3, 5)
          + "+" + this.coord.substring(5);
    }
    return null;
  }

  private boolean isValid() {
    if ((this.coord.length() == 7)
        && ((this.coord.substring(2, 3) == "N") || (this.coord.substring(2, 3) == "E"))) {
      return true;
    }
    return false;
  }

  public static String createMapLink(final String value1, final String value2) {
    String maplink1 =
        value1.substring(0, 2) + "+" + value1.substring(3, 5) + "+"
            + value1.substring(5) + "+N";
    String maplink2 =
        value2.substring(0, 2) + "+" + value2.substring(3, 5) + "+"
            + value2.substring(5) + "+E";

    return maplink1 + "," + maplink2;
  }

}
