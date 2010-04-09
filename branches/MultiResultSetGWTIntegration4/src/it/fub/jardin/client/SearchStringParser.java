/**
 * 
 */
package it.fub.jardin.client;

import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;

/**
 * @author gpantanetti
 * 
 */
public class SearchStringParser {

  private Map<String, String> searchMap;
  public static final String SPECIAL_KEY = "SPECIAL_KEY";

  private static final char space = ' ';
  private static final char separator = '|';
  private static final char wrapper = '"';
  private static final char operator = ':';

  /**
   * @param string
   */
  public SearchStringParser(String string) {
    this.searchMap = this.parse(string);
  }

  private Map<String, String> parse(String string) {

    int state = 0;
    String key = null;
    String value = null;
    Map<String, String> result = new HashMap<String, String>();

    char c = ' ';
    for (int i = 0; i < string.length(); i++) {

      c = string.charAt(i);

      switch (state) {
      /* Stato d'ingresso: consuma gli spazi */
      case 0:
        if ((key != null) && (value != null)) {
          if (result.get(key) != null) {
            value = result.get(key) + separator + value;
          }
          result.put(key, value);
          key = value = null;
        }
        switch (c) {
        case space:
          state = 0;
          break;
        case wrapper:
          key = SPECIAL_KEY;
          value = "";
          state = 3;
          break;
        case operator:
          value = "" + c;
          state = 4;
          break;
        default:
          value = "" + c;
          state = 1;
          break;
        }
        break;
      /*
       * Stato di lettura di una possibile chiave o valore non delimitato da
       * apici
       */
      case 1:
        switch (c) {
        case space:
        case wrapper:
          key = SPECIAL_KEY;
          state = 0;
          break;
        case operator:
          key = value;
          value = null;
          state = 2;
          break;
        default:
          value += c;
          state = 1;
          break;
        }
        break;
      /* Stato di inizio lettura di un valore dopo aver letto una chiave */
      case 2:
        switch (c) {
        case space:
          value = key + operator;
          key = SPECIAL_KEY;
          state = 0;
          break;
        case wrapper:
          value = "";
          state = 3;
          break;
        default:
          value = "" + c;
          state = 4;
          break;
        }
        break;
      /* Stato di lettura di un valore delimitato da apici */
      case 3:
        switch (c) {
        case wrapper:
          state = 0;
          break;
        default:
          value += c;
          state = 3;
          break;
        }
        break;
      case 4:
        switch (c) {
        case space:
          state = 0;
          break;
        default:
          value += c;
          state = 4;
          break;
        }
        break;
      default:
        state = 0;
        break;
      }
    }

    if ((key != null) || (value != null)) {

      if (value == null) {
        value = key;
        if (c == operator) {
          value += c;
        }
        key = SPECIAL_KEY;
      } else if (key == null) {
        key = SPECIAL_KEY;
      }

      if (result.get(key) != null) {
        value = result.get(key) + separator + value;
      }
      result.put(key, value);
    }

    return result;
  }

  /**
   * Restituisce la stringa di ricerca che non è associata ad alcuna parola
   * chiave
   * 
   * @return una stringa (con spazi) che non è associata ad alcuna parola
   *         chiave. <b>null</b> se non esiste una stringa speciale
   */
  public String getSpecialSearchValue() {
    return this.searchMap.get(SPECIAL_KEY);
  }

  /**
   * Restituisce la Mappa chiave->valore delle stringhe di ricerca.
   * 
   * @return una mappa (eventualmente vuota) che rappresenta la stringa di
   *         ricerca
   */
  public Map<String, String> getSearchMap() {
    return this.searchMap;
  }

  public void printMap(Map<String, String> map) {
    for (String key : map.keySet()) {
      Log.debug("k: " + key + ", value: " + map.get(key));
    }
  }

}
