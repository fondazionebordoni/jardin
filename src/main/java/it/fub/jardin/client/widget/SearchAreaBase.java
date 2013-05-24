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

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.SearchStringParser;
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.event.dom.client.KeyCodes;

public class SearchAreaBase extends HtmlContainer {

  private static final String SPECIAL_FIELD = "searchField";
  private TextField<String> field;
  private final CheckBox accurate = new CheckBox();
  private final List<String> fieldNames;
  private final String searchId;
  private final SearchParams searchParams;
  private String tooltip;
  private final String tooltipAccurate;

  public SearchAreaBase(final ResultsetImproved resultset, User user) {

    this.fieldNames = new ArrayList<String>();
    this.tooltip =
        "Per una ricerca avanzata &egrave; possibile usare le seguenti parole chiave:<br/>";
    for (ResultsetField f : resultset.getFields()) {
      String fieldName = f.getName();
      this.fieldNames.add(fieldName);
      this.tooltip += "<b>" + fieldName + "</b>; ";
    }
    this.tooltip +=
        "<br>Costruire un testo per la ricerca usando parole "
            + "chiave e valori da cercare in questo modo:<br>"
            + "[parola_chiave:]valore [parola_chiave:valore ...]";

    this.tooltipAccurate =
        "La selezione del checkbox permette di fare una ricerca pi&ugrave; accurata della parola.";

    this.searchId = "search-area-" + resultset.getId();

    String header =
        "<div id='" + this.searchId + "-box' class='search-box'>" + "<div id='"
            + this.searchId + "-text'  class='search-text'></div>"
            + "<div id='" + this.searchId
            + "-toolbar'  class='search-button'></div>" + "</div>";
    this.setHtml(header);
    this.setWidth("100%");

    this.searchParams = new SearchParams(resultset.getId());
    this.searchParams.setGroupId(user.getGid());
    this.searchParams.setUserId(user.getUid());

    this.setFields();
    this.setButtons();
  }

  private void setFields() {
    this.field = new TextField<String>();
    this.field.setBorders(true);
    this.field.setName(SPECIAL_FIELD);
    this.field.setWidth(300);
    this.field.setFieldLabel("Ricerca");
    this.field.setEmptyText("inserisci il testo...");
    this.field.setToolTip(this.tooltip);
    this.field.addKeyListener(new KeyListener() {
      @Override
      public void componentKeyPress(final ComponentEvent event) {
        if (event.getKeyCode() == KeyCodes.KEY_ENTER) {
          SearchAreaBase.this.search(false);
        }
      }
    });

    this.add(this.field, "#" + this.searchId + "-text");
  }

  private void setButtons() {

    final String SEARCH = this.searchId + "-search";
    final String SEARCH_ALL = this.searchId + "-searchall";

    SelectionListener<ButtonEvent> listener =
        new SelectionListener<ButtonEvent>() {
          @Override
          public void componentSelected(final ButtonEvent te) {
            if (te.getButton().getId().compareTo(SEARCH) == 0) {
              SearchAreaBase.this.search(false);
            } else if (te.getButton().getId().compareTo(SEARCH_ALL) == 0) {
              SearchAreaBase.this.search(true);
            }
          }
        };

    HorizontalPanel hp = new HorizontalPanel();

    this.accurate.setValue(true);
    this.accurate.setStyleAttribute("margin-top", "10px");
    this.accurate.setToolTip(this.tooltipAccurate);
    hp.add(this.accurate);

    LabelField lblAccurate = new LabelField("ricerca accurata");
    lblAccurate.setStyleAttribute("margin-top", "8px");
    lblAccurate.setStyleAttribute("margin-left", "3px");
    hp.add(lblAccurate);

    Button search = new Button("cerca");
    search.setId(SEARCH);
    search.addSelectionListener(listener);
    hp.add(search);

    Button searchAll = new Button("cerca tutti");
    searchAll.setId(SEARCH_ALL);
    searchAll.addSelectionListener(listener);
    hp.add(searchAll);

    this.add(hp, "#" + this.searchId + "-toolbar");
  }

  /**
   * Avvia la ricerca su database nel resultset associato alla tabella
   * 
   * @param searchAll
   *          se avviare o no la ricerca su tutti i valori del resultset.
   *          Impostare a true per visualizzare tutti i record del resultset
   */
  private void search(final boolean searchAll) {
    String s = this.field.getRawValue().trim();

    List<BaseModelData> queryFieldList = new ArrayList<BaseModelData>();

    if (!searchAll && (s.length() > 0)) {
      SearchStringParser parser = new SearchStringParser(s);

      String specialSearchValue = parser.getSpecialSearchValue();
      if (specialSearchValue != null) {
        BaseModelData m = new BaseModelData();
        m.set(SPECIAL_FIELD, specialSearchValue);
        queryFieldList.add(m);
      }

      Map<String, String> searchMap = parser.getSearchMap();
      for (String key : parser.getSearchMap().keySet()) {
        for (String k : this.fieldNames) {
          if (k.equalsIgnoreCase(key)) {
            BaseModelData m = new BaseModelData();
            m.set(key, searchMap.get(key));
            queryFieldList.add(m);
          }
        }
      }
    }

    this.searchParams.setFieldsValuesList(queryFieldList);
    this.searchParams.setAccurate(this.accurate.getValue());
    Dispatcher.forwardEvent(EventList.Search, this.searchParams);
  }
}
