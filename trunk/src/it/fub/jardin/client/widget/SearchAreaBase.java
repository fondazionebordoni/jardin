package it.fub.jardin.client.widget;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.SearchStringParser;
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;

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
  private CheckBox accurate = new CheckBox();
  private List<String> fieldNames;
  private String searchId;
  private SearchParams searchParams;
  private String tooltip;
  private String tooltipAccurate;

  public SearchAreaBase(ResultsetImproved resultset) {

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
        "<div id='" + searchId + "-box' class='search-box'>" + "<div id='"
            + searchId + "-text'  class='search-text'></div>" + "<div id='"
            + searchId + "-toolbar'  class='search-button'></div>" + "</div>";
    this.setHtml(header);
    this.setWidth("100%");

    this.searchParams = new SearchParams(resultset.getId());

    setFields();
    setButtons();
  }

  private void setFields() {
    this.field = new TextField<String>();
    this.field.setBorders(true);
    this.field.setName(SPECIAL_FIELD);
    this.field.setFieldLabel("Ricerca");
    this.field.setEmptyText("inserisci il testo...");
    this.field.setToolTip(tooltip);
    this.field.addKeyListener(new KeyListener() {
      @Override
      public void componentKeyPress(final ComponentEvent event) {
        if (event.getKeyCode() == KeyCodes.KEY_ENTER) {
          search(false);
        }
      }

    });

    this.add(field, "#" + searchId + "-text");

  }

  private void setButtons() {

    final String SEARCH = searchId + "-search";
    final String SEARCH_ALL = searchId + "-searchall";

    SelectionListener<ButtonEvent> listener =
        new SelectionListener<ButtonEvent>() {
          @Override
          public void componentSelected(ButtonEvent te) {
            if (te.getButton().getId().compareTo(SEARCH) == 0) {
              search(false);
            } else if (te.getButton().getId().compareTo(SEARCH_ALL) == 0) {
              search(true);
            }
          }
        };

    HorizontalPanel hp = new HorizontalPanel();

    this.accurate.setValue(true);
    this.accurate.setStyleAttribute("margin-top", "10px");
    this.accurate.setToolTip(tooltipAccurate);
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

    this.add(hp, "#" + searchId + "-toolbar");
  }

  /**
   * Avvia la ricerca su database nel resultset associato alla tabella
   * 
   * @param searchAll
   *          se avviare o no la ricerca su tutti i valori del resultset.
   *          Impostare a true per visualizzare tutti i record del resultset
   */
  private void search(boolean searchAll) {
    String s = field.getRawValue().trim();

    List<BaseModelData> queryFieldList = new ArrayList<BaseModelData>();

    if (!searchAll && s.length() > 0) {
      SearchStringParser parser = new SearchStringParser(s);

      String specialSearchValue = parser.getSpecialSearchValue();
      if (specialSearchValue != null) {
        BaseModelData m = new BaseModelData();
        m.set(SPECIAL_FIELD, specialSearchValue);
        queryFieldList.add(m);
      }

      Map<String, String> searchMap = parser.getSearchMap();
      for (String key : parser.getSearchMap().keySet()) {
        for (String k : fieldNames) {
          if (k.equalsIgnoreCase(key)) {
            BaseModelData m = new BaseModelData();
            m.set(key, searchMap.get(key));
            queryFieldList.add(m);
          }
        }
      }
    }

    searchParams.setFieldsValuesList(queryFieldList);
    if (accurate.getValue() == true) {
      searchParams.setAccurate(true);
    } else {
      searchParams.setAccurate(false);
    }
    Dispatcher.forwardEvent(EventList.Search, searchParams);
  }
}