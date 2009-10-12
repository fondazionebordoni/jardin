package it.fub.jardin.client.widget;

public class Coordinate {

  private String coord;

  public Coordinate(String value) {
    coord = value;
  }

  public String CoordinateParser() {
    if (isValid()) {
      return coord.substring(0, 2) + "+" + coord.substring(3, 5) + "+"
          + coord.substring(5);
    }
    return null;
  }

  private boolean isValid() {
    if (this.coord.length() == 7
        && (this.coord.substring(2, 3) == "N" || this.coord.substring(2, 3) == "E")) {
      return true;
    }
    return false;
  }

  public static String createMapLink(String value1, String value2) {
    String maplink1 =
        value1.substring(0, 2) + "+" + value1.substring(3, 5) + "+"
            + value1.substring(5) + "+N";
    String maplink2 =
        value2.substring(0, 2) + "+" + value2.substring(3, 5) + "+"
            + value2.substring(5) + "+E";

    return maplink1 + "," + maplink2;
  }

}
