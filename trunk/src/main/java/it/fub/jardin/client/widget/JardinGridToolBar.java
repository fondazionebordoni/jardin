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
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.Template;
import it.fub.jardin.client.model.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.KeyCodes;

public class JardinGridToolBar extends ToolBar {

  private ResultsetImproved resultset;
  // TODO Eliminare riferimenti a grid: dovrebbe bastare il resultset
  private JardinGrid grid;
  private SimpleComboBox<String> searchfield;
  private static final String SPECIAL_FIELD = "searchField";
  private List<String> fieldNames;
  private MenuItem preferenceMenuItem;
  private SimpleComboBox<Template> comboTemplate;
  private final TextField<String> fs = new TextField<String>();
  private final SimpleComboBox<String> ts = new SimpleComboBox<String>();
  private Button buttonMenuPlugins =
      new Button("Plugin", IconHelper.createStyle("icon-plugin"),
          this.getListenerWithGrid(EventList.GetPlugins));
  private SearchParams searchParams;
  private String tooltip;
  private String searchId;
  private CheckMenuItem accurate;
  private String operator = "";
  private String valueOperator = "";
  private String keyOperator = "";

  // TODO Modificare il costruttore: passare solo l'id del resultset
  public void setGrid(final JardinGrid grid) {
    this.resultset = grid.getResultset();
    this.searchId = "grid-toolbar-" + this.resultset.getId();
    this.searchParams = new SearchParams(this.resultset.getId());
    this.grid = grid;

    this.fieldNames = new ArrayList<String>();
    this.tooltip =
        "Per una ricerca avanzata &egrave; possibile usare le seguenti parole chiave:<br/>";
    for (ResultsetField f : this.resultset.getFields()) {
      String fieldName = f.getName();
      this.fieldNames.add(fieldName);
      this.tooltip += "<b>" + fieldName + "</b>; ";
    }
    this.tooltip +=
        "<br>Costruire un testo per la ricerca usando parole "
            + "chiave e valori da cercare in questo modo:<br>"
            + "[parola_chiave:]valore [parola_chiave:valore ...]";
    this.addSearch();
    this.add(new SeparatorToolItem());

    ArrayList<Tool> tools = grid.getResultset().getTools();
    if (tools.contains(Tool.ALL)) {
      this.addModifyActions();
      this.add(new SeparatorToolItem());
      this.addExportActions();
      this.add(new SeparatorToolItem());
      this.addImportActions();
      this.add(new SeparatorToolItem());
      this.addPreferenceActions();
      this.add(new SeparatorToolItem());
      this.addAnalisysButton();
      this.addChartButton();
      this.add(new SeparatorToolItem());
    } else {
      if (tools.contains(Tool.MODIFY)) {
        this.addModifyActions();
        this.add(new SeparatorToolItem());
      }
      if (tools.contains(Tool.EXPORT)) {
        this.addExportActions();
        this.add(new SeparatorToolItem());
      }
      if (tools.contains(Tool.IMPORT)) {
        this.addImportActions();
        this.add(new SeparatorToolItem());
      }
      if (tools.contains(Tool.PREFERENCE)) {
        this.addPreferenceActions();
        this.add(new SeparatorToolItem());
      }
      if (tools.contains(Tool.ANALISYS)) {
        this.addAnalisysButton();
        this.addChartButton();
        this.add(new SeparatorToolItem());
      }
    }

    this.add(this.buttonMenuPlugins);
  }

  private void addSearch() {
    this.searchfield = new SimpleComboBox<String>() {
      @Override
      public void doQuery(String q, final boolean forceAll) {
        int index = q.lastIndexOf(' ', q.length() - 2);
        q = q.substring(index + 1);
        super.doQuery(q, forceAll);
      }

      @Override
      protected void onSelect(final SimpleComboValue<String> model,
          final int index) {
        String value = this.getRawValue();
        int i = value.lastIndexOf(' ', value.length() - 2);
        value = value.substring(0, i + 1);
        super.onSelect(model, index);
        this.setRawValue(value + model.getValue() + "=");
      }
    };
    this.searchfield.setHideTrigger(true);
    this.searchfield.setEditable(true);
    this.searchfield.setTypeAhead(false);
    this.searchfield.add(this.fieldNames);
    this.searchfield.setName(SPECIAL_FIELD);
    this.searchfield.setFieldLabel("Cerca");
    // this.searchfield.setEmptyText("Cerca");
    this.searchfield.setToolTip(this.tooltip);
    this.searchfield.setWidth("24em");
    this.searchfield.addKeyListener(new KeyListener() {
      @Override
      public void componentKeyPress(final ComponentEvent event) {
        if (event.getKeyCode() == KeyCodes.KEY_ENTER) {
          JardinGridToolBar.this.search(false);
        }
      }
    });

    this.add(this.searchfield);
    this.addSearchButtons();
  }

  private void addSearchButtons() {

    final String SEARCH = this.searchId + "-search";
    final String SEARCH_ALL = this.searchId + "-searchall";

    SelectionListener<ButtonEvent> listener =
        new SelectionListener<ButtonEvent>() {
          @Override
          public void componentSelected(final ButtonEvent te) {
            if (te.getButton().getId().compareTo(SEARCH) == 0) {
              JardinGridToolBar.this.search(false);
            } else if (te.getButton().getId().compareTo(SEARCH_ALL) == 0) {
              JardinGridToolBar.this.search(true);
            }
          }
        };

    HorizontalPanel searchPanel = new HorizontalPanel();

    Button search = new Button("Cerca", IconHelper.createStyle("icon-search"));
    search.addSelectionListener(listener);
    search.setId(SEARCH);
    searchPanel.add(search);

    Button searchOptions = new Button("Opzioni");

    Menu menu = new Menu();
    String tooltipAccurate =
        "La selezione del checkbox permette di fare una ricerca pi&ugrave; accurata della parola.";
    this.accurate = new CheckMenuItem("Ricerca accurata");
    this.accurate.setChecked(true);
    this.accurate.setToolTip(tooltipAccurate);
    menu.add(this.accurate);

    searchOptions.setMenu(menu);
    searchPanel.add(searchOptions);

    this.add(searchPanel);
  }

  /**
   * Avvia la ricerca su database nel resultset associato alla tabella
   * 
   * @param searchAll
   *          se avviare o no la ricerca su tutti i valori del resultset.
   *          Impostare a true per visualizzare tutti i record del resultset
   */
  private void search(final boolean searchAll) {
		 keyOperator = "";
	     operator="";
		 valueOperator="";
		 String s = this.searchfield.getRawValue().trim();
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
	            operator(key,searchMap.get(key),s);
  	           m.set(keyOperator, valueOperator);
	            queryFieldList.add(m);
	          }
	        }
	      }
	    }

	  this.searchParams.setFieldsValuesList(queryFieldList);
    this.searchParams.setAccurate(this.accurate.isChecked());
    Dispatcher.forwardEvent(EventList.Search, this.searchParams);
  }

  @SuppressWarnings("unchecked")
  private void addModifyActions() {

    // TODO Non inserire il bottone se l'azione non è consentita

    if (this.resultset.isInsert()) {
      this.add(new Button("Aggiungi riga", IconHelper.createStyle("icon-add"),
          this.getListenerWithGrid(EventList.AddRow)));
    }

    if (this.resultset.isDelete()) {
      this.add(new Button("Rimuovi righe",
          IconHelper.createStyle("icon-delete"),
          this.getListenerWithGrid(EventList.RemoveRows)));
    }
  }

  @SuppressWarnings("unchecked")
  private void addExportActions() {

    Template defaultTemplate = new Template(Template.DEFAULT);
    defaultTemplate.setXsl(this.resultset.getId() + "_default.xsl");

    // campi dei separatori per l'export
    this.fs.setName("fs");
    this.fs.setToolTip("Separatore campo");
    this.fs.setWidth(15);
    this.fs.setMaxLength(1);
    this.fs.setValue(";");

    this.ts.setName("ts");
    this.ts.setToolTip("Separatore testo");
    this.ts.setWidth(30);

    List<String> values = new ArrayList<String>();
    values.add("\"");
    values.add("'");
    this.ts.add(values);
    this.ts.setEditable(false);
    this.ts.setTriggerAction(TriggerAction.ALL);
    this.ts.setForceSelection(true);
    this.ts.setSimpleValue("\"");

    this.add(this.fs);
    this.add(this.ts);

    /* Combo box per la selezione del formato di esportazione */
    this.comboTemplate = new SimpleComboBox<Template>();

    this.comboTemplate.setWidth(80);
    this.comboTemplate.add(Template.CSV);
    this.comboTemplate.add(Template.XML);
    this.comboTemplate.add(defaultTemplate);
    this.comboTemplate.setSimpleValue(Template.CSV);
    this.comboTemplate.setEditable(false);
    this.comboTemplate.setTriggerAction(TriggerAction.ALL);
    this.comboTemplate.setForceSelection(true);

    this.comboTemplate.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<Template>>() {

      @Override
      public void selectionChanged(
          final SelectionChangedEvent<SimpleComboValue<Template>> se) {
        // System.out.println("template->"+se.getSelectedItem().getValue());
        if (se.getSelectedItem().getValue().toString().compareToIgnoreCase(
            "CSV") == 0) {
          // abilito i campi per la specifica dei separatori
          JardinGridToolBar.this.ts.setEnabled(true);
          JardinGridToolBar.this.fs.setEnabled(true);
        } else {
          // disabilito i campi per la specifica dei separatori
          JardinGridToolBar.this.ts.setEnabled(false);
          JardinGridToolBar.this.fs.setEnabled(false);
        }
      }

    });

    this.add(this.comboTemplate);

    Menu menu = new Menu();

    menu.add(new MenuItem("Tutta la griglia, tutte le colonne",
        IconHelper.createStyle("icon-table-save"),
        this.getListenerWithGrid(EventList.ExportAllStoreAllColumns)));

    menu.add(new MenuItem("Tutta la griglia, le colonne visibili",
        IconHelper.createStyle("icon-table-save"),
        this.getListenerWithGrid(EventList.ExportAllStoreSomeColumns)));

    menu.add(new MenuItem("La griglia visibile, tutte le colonne",
        IconHelper.createStyle("icon-table-save"),
        this.getListenerWithGrid(EventList.ExportSomeStoreAllColumns)));

    menu.add(new MenuItem("La griglia visibile, le colonne visibili",
        IconHelper.createStyle("icon-table-save"),
        this.getListenerWithGrid(EventList.ExportSomeStoreSomeColumns)));

    menu.add(new MenuItem("Le righe selezionate, tutte le colonne",
        IconHelper.createStyle("icon-table-save"),
        this.getListenerWithGrid(EventList.ExportSomeRowsAllColumns)));

    menu.add(new MenuItem("Le righe selezionate, le colonne visibili",
        IconHelper.createStyle("icon-table-save"),
        this.getListenerWithGrid(EventList.ExportSomeRowsSomeColumns)));

    menu.add(new SeparatorMenuItem());

    menu.add(new MenuItem("Aggiungi template (file XSL)",
        IconHelper.createStyle("icon-add"),
        this.getListenerWithGrid(EventList.UploadTemplate)));

    Button button =
        new Button("Esporta", IconHelper.createStyle("icon-export"));
    button.setMenu(menu);
    this.add(button);

  }

  private void addImportActions() {

    Button b = new Button("Importa", IconHelper.createStyle("icon-import"));

    Menu menu = new Menu();

    MenuItem update =
        new MenuItem("Aggiorna", IconHelper.createStyle("icon-table-update"),
            this.getListenerWithGrid(EventList.UploadImport));
    update.setToolTip("Se si carica un file contente uno più record già presenti "
        + "nel DB, il sistema aggiornerà tali record con i nuovi valori "
        + "contenuti nel file stesso.\n"
        + "<b>La coincidenza deve sussistere a livello di chiave primaria o chiave unique.</b><BR />"
        + "La prima riga del file da importare deve contenere i nomi delle colonne!");
    menu.add(update);

    MenuItem add =
        new MenuItem("Inserisci", IconHelper.createStyle("icon-table-insert"),
            this.getListenerWithGrid(EventList.UploadInsert));
    menu.add(add);

    b.setMenu(menu);
    this.add(b);
  }

  private void addPreferenceActions() {

    Button preferenceButton =
        new Button("Preferenze griglia",
            IconHelper.createStyle("icon-table-preference"));

    Menu menu = new Menu();

    menu.add(new MenuItem("Mostra tutte le colonne",
        IconHelper.createStyle("icon-table-show-all"),
        this.getListenerWithGrid(EventList.ShowAllColumns)));

    menu.add(new MenuItem("Salva visualizzazione",
        IconHelper.createStyle("icon-table-save-view"),
        this.getListenerWithGrid(EventList.SaveGridView)));

    this.preferenceMenuItem =
        new MenuItem("Visualizzazioni d'utente",
            IconHelper.createStyle("icon-table-user"),
            this.getListenerWithGrid(EventList.GetGridViews));
    // preferenceMenuItem.disable();
    menu.add(this.preferenceMenuItem);

    preferenceButton.setMenu(menu);
    this.add(preferenceButton);
  }

  @SuppressWarnings("unchecked")
  private SelectionListener getListenerWithGrid(final EventType e) {
    SelectionListener listener = new SelectionListener() {
      @Override
      public void componentSelected(final ComponentEvent ce) {
        Dispatcher.forwardEvent(e, JardinGridToolBar.this.resultset.getId());
      }
    };
    return listener;
  }

  public void updatePreferenceButton(final HeaderPreferenceList data) {
    Menu userPreferenceMenu = new Menu();
    this.preferenceMenuItem.enable();
    this.preferenceMenuItem.setSubMenu(userPreferenceMenu);

    for (int i = 0; i < data.getUserPref().size(); i++) {
      final String prefName =
          data.getUserPref().get(i).getPropertyNames().iterator().next();
      final Integer prefId = (Integer) data.getUserPref().get(i).get(prefName);

      CheckMenuItem r = new CheckMenuItem(prefName);
      r.setGroup("preferenceMenuItem");
      r.addSelectionListener(new SelectionListener<MenuEvent>() {

        @Override
        public void componentSelected(final MenuEvent ce) {
          JardinGridToolBar.this.grid.setUserPreferenceHeaderId(prefId);
          Dispatcher.forwardEvent(EventList.UpdateColumnModel,
              JardinGridToolBar.this.grid);
        }

      });
      userPreferenceMenu.add(r);

    }
  }

  public Template getTemplate() {
    return this.comboTemplate.getSimpleValue();
  }

  public char getTextSeparator() {
    String sep = this.ts.getSimpleValue();
    if ((sep != null) && (sep.compareTo("") != 0)) {
      return this.ts.getSimpleValue().charAt(0);
    } else {
      return '\0';
    }
  }

  public char getFieldSeparator() {
    String sep = this.fs.getValue();
    if ((sep != null) && (sep.compareTo("") != 0)) {
      return this.fs.getValue().charAt(0);
    } else {
      return '\0';
    }
  }

  private void addAnalisysButton() {
    this.add(new Button("Analisi", IconHelper.createStyle("icon-grid"),
        this.getListenerWithGrid(EventList.Jungle)));
  }

  private void addChartButton() {
    Button button = new Button("Grafici", IconHelper.createStyle("icon-chart"));

    Menu menu = new Menu();

    menu.add(new MenuItem("Torta", IconHelper.createStyle("icon-chart-pie"),
        this.getListenerWithGrid(EventList.ShowPieChart)));

    menu.add(new MenuItem("Istogramma",
        IconHelper.createStyle("icon-chart-bar"),
        this.getListenerWithGrid(EventList.ShowBarChart)));

    button.setMenu(menu);
    this.add(button);

  }

  public Button getButtonMenuPlugins() {
    return this.buttonMenuPlugins;
  }

  public void setButtonMenuPlugins(final Button buttonMenuPlugins) {
    this.buttonMenuPlugins = buttonMenuPlugins;
  }
	private void operator(String key, String value, String search)
	{
      int lenKey = key.length();  
      int indexKey = search.indexOf(key); 
      int lenValue = 0;
      int indexValue = search.indexOf(value); 
      if (indexValue!=-1){						
      	lenValue = value.length();
      	String operator = search.substring(lenKey + indexKey, indexValue);
      	int lenOperator = operator.length();
      	int indexOperator = operator.indexOf(operator);
      	String prova = operator.substring(lenOperator - 1, lenOperator);
      	if(prova.equals("\""))
      		operator = operator.substring(indexOperator, lenOperator-1);
      	this.operator = operator;
      	this.valueOperator = value;
        this.keyOperator = key+"_operator_"+operator;
      }
      else{ // caso in cui c'è un OR con la stringa di valore col pipe
      	String[] token =  value.split("\\|"); 
      	String sValueFirstToken = token[0];
      	//System.out.println("Primo Token  " + sValueFirstToken);
      	int indexValueFirstToken = search.indexOf(sValueFirstToken); 
      	String operatorOr = search.substring(lenKey + indexKey, indexValueFirstToken);
      	//System.out.println("OperatorOR   " +  operatorOr);
      	this.valueOperator = value;
      	this.keyOperator  = key + "_operatorOr_" + operatorOr;
     }
	}
}
