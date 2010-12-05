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

package it.fub.jardin.client;

import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;

public class SearchStringParser {

  private final Map<String, String> searchMap;
  public static final String SPECIAL_KEY = "SPECIAL_KEY";

  private static final char space = ' ';
  private static final char separator = '|';
  private static final char wrapper = '"';
  private static final char operatorEqual = '=';
  private static final char operatorGreater= '>';
  private static final char operatorLesser= '<';
  private static boolean compare = false;

  /**
   * @param string
   */
  public SearchStringParser(final String string) {
    this.searchMap = this.parse(string);
  }

  private Map<String, String> parse (final String string) {

    int state = 0;
    String key = null;
    String value = null;
    Map<String, String> result = new HashMap<String, String>();

    char c = ' ';
    char c1= ' ';
    for (int i = 0; i < string.length(); i++) {

      c = string.charAt(i);
      
      // se c è = a <,>, =  .... valorizzi operator 

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
        case operatorEqual:	
        	if(!compare)
        		value = "" + c;
        	state = 4;
        	break;
        case operatorGreater:
        	compare = true;
            value = "" + c;
            state = 4;
            break;  
        case operatorLesser:
        	compare = true;
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
        case operatorEqual:
          // se l'operatore è = va bene, 
        	if(!compare){
        		key = value;
        		value = null;
        	}
          compare = false;	
          state = 2;
          break;
        case operatorGreater:
        	compare = true;
        	key = value;
            value = null;
            c1= string.charAt(i + 1);
            if(c1==operatorEqual)
            	state = 1;
            else
            	state = 2;
            break;
        case operatorLesser:
        	compare = true;
        	key = value;
            value = null;
            c1= string.charAt(i + 1);
            if(c1==operatorEqual)
            	state = 1;
            else
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
          value = key + operatorEqual;
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
        if (c == operatorEqual) {
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

  public void printMap(final Map<String, String> map) {
    for (String key : map.keySet()) {
      Log.debug("k: " + key + ", value: " + map.get(key));
    }
  }

}
