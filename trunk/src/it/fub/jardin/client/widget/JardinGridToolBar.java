/**
 * 
 */
package it.fub.jardin.client.widget;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.Template;
import it.fub.jardin.client.model.Tool;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.IconHelper;
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
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author gpantanetti
 * 
 */
public class JardinGridToolBar extends ToolBar {

  private int resultset;
  // TODO Eliminare riferimenti a grid: dovrebbe bastare il resultset
  private JardinGrid grid;
  private MenuItem preferenceMenuItem;
  private SimpleComboBox<Template> comboTemplate;
  private TextField<String> fs = new TextField<String>();
  private SimpleComboBox<String> ts = new SimpleComboBox<String>();
  private Button buttonMenuPlugins = new Button("Plugins", getListenerWithGrid(EventList.GetPlugins) );

  // TODO Modificare il costruttore: passare solo l'id del resultset
  public void setGrid(JardinGrid grid) {
    this.resultset = grid.getResultset().getId();
    this.grid = grid;

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
      }
    }
    this.add(new SeparatorToolItem());
    
    this.add(buttonMenuPlugins);
  }

  @SuppressWarnings("unchecked")
  private void addModifyActions() {

    // TODO Non inserire il bottone se l'azione non è consentita

    if (grid.getResultset().isInsert()) {
      this.add(new Button("Aggiungi riga", IconHelper.createStyle("icon-add"),
          getListenerWithGrid(EventList.AddRow)));
    } else {
      // this.add(new Button("Aggiungi riga",
      // IconHelper.createStyle("icon-add-disabled")));
    }

    if (grid.getResultset().isDelete()) {
      this.add(new Button("Rimuovi righe",
          IconHelper.createStyle("icon-delete"),
          getListenerWithGrid(EventList.RemoveRows)));
    } else {
      // this.add(new Button("Rimuovi righe",
      // IconHelper.createStyle("icon-delete-disabled")));
    }
  }

  @SuppressWarnings("unchecked")
  private void addExportActions() {

    Template defaultTemplate = new Template(Template.DEFAULT);
    defaultTemplate.setXsl(resultset + "_default.xsl");

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
    
    this.comboTemplate.addSelectionChangedListener(
        new SelectionChangedListener<SimpleComboValue<Template>>() {

          @Override
          public void selectionChanged(
              SelectionChangedEvent<SimpleComboValue<Template>> se) {
            //System.out.println("template->"+se.getSelectedItem().getValue());
            if (se.getSelectedItem().getValue().toString().compareToIgnoreCase("CSV")==0){
              // abilito i campi per la specifica dei separatori
              ts.setEnabled(true);
              fs.setEnabled(true);
            }else{
              // disabilito i campi per la specifica dei separatori
              ts.setEnabled(false);
              fs.setEnabled(false);
            }
          }
          
        }
    );

    this.add(this.comboTemplate);

    Menu menu = new Menu();

    menu.add(new MenuItem("Tutta la griglia, tutte le colonne",
        IconHelper.createStyle("icon-table-save"),
        getListenerWithGrid(EventList.ExportAllStoreAllColumns)));

    menu.add(new MenuItem("Tutta la griglia, le colonne visibili",
        IconHelper.createStyle("icon-table-save"),
        getListenerWithGrid(EventList.ExportAllStoreSomeColumns)));

    menu.add(new MenuItem("La griglia visibile, tutte le colonne",
        IconHelper.createStyle("icon-table-save"),
        getListenerWithGrid(EventList.ExportSomeStoreAllColumns)));

    menu.add(new MenuItem("La griglia visibile, le colonne visibili",
        IconHelper.createStyle("icon-table-save"),
        getListenerWithGrid(EventList.ExportSomeStoreSomeColumns)));

    menu.add(new MenuItem("Le righe selezionate, tutte le colonne",
        IconHelper.createStyle("icon-table-save"),
        getListenerWithGrid(EventList.ExportSomeRowsAllColumns)));

    menu.add(new MenuItem("Le righe selezionate, le colonne visibili",
        IconHelper.createStyle("icon-table-save"),
        getListenerWithGrid(EventList.ExportSomeRowsSomeColumns)));

    menu.add(new SeparatorMenuItem());

    menu.add(new MenuItem("Aggiungi template (file XSL)",
        IconHelper.createStyle("icon-add"),
        getListenerWithGrid(EventList.UploadTemplate)));

    Button button =
        new Button("Esporta", IconHelper.createStyle("icon-export"));
    button.setMenu(menu);
    this.add(button);

  }

  private void addImportActions() {
    Button importButton =
        new Button("Aggiorna", IconHelper.createStyle("icon-import"),
            getListenerWithGrid(EventList.UploadImport));
    importButton.setToolTip("Se si carica un file contente uno più record già presenti "
        + "nel DB, il sistema aggiornerà tali record con i nuovi valori "
        + "contenuti nel file stesso.\n"
        + "<b>La coincidenza deve sussistere a livello di chiave primaria o chiave unique.</b><BR />"
        + "La prima riga del file da importare deve contenere i nomi delle colonne!");

    this.add(importButton);
    
    Button insertButton =
      new Button("Inserisci", IconHelper.createStyle("icon-import"),
          getListenerWithGrid(EventList.UploadInsert));
//    insertButton.setToolTip("Se si carica un file contente uno più record già presenti "
//      + "nel DB, il sistema aggiornerà tali record con i nuovi valori "
//      + "contenuti nel file stesso.\n"
//      + "<b>La coincidenza deve sussistere a livello di chiave primaria o chiave unique.</b><BR />"
//      + "La prima riga del file da importare deve contenere i nomi delle colonne!");

  this.add(insertButton);
  }

  private void addPreferenceActions() {

    Button preferenceButton =
        new Button("Preferenze griglia",
            IconHelper.createStyle("icon-table-preference"));

    Menu menu = new Menu();

    menu.add(new MenuItem("Mostra tutte le colonne",
        IconHelper.createStyle("icon-table-show-all"),
        getListenerWithGrid(EventList.ShowAllColumns)));

    menu.add(new MenuItem("Salva visualizzazione",
        IconHelper.createStyle("icon-table-save-view"),
        getListenerWithGrid(EventList.SaveGridView)));

    preferenceMenuItem =
        new MenuItem("Visualizzazioni d'utente",
            IconHelper.createStyle("icon-table-user"),
            getListenerWithGrid(EventList.GetGridViews));
    // preferenceMenuItem.disable();
    menu.add(preferenceMenuItem);

    preferenceButton.setMenu(menu);
    this.add(preferenceButton);
  }

  private SelectionListener getListenerWithGrid(final EventType e) {
    SelectionListener listener = new SelectionListener() {
      @Override
      public void componentSelected(ComponentEvent ce) {
        Dispatcher.forwardEvent(e, resultset);
      }
    };
    return listener;
  }

  public void updatePreferenceButton(final HeaderPreferenceList data) {
    Menu userPreferenceMenu = new Menu();
    preferenceMenuItem.enable();
    preferenceMenuItem.setSubMenu(userPreferenceMenu);

    for (int i = 0; i < data.getUserPref().size(); i++) {
      final String prefName =
          data.getUserPref().get(i).getPropertyNames().iterator().next();
      final Integer prefId = (Integer) data.getUserPref().get(i).get(prefName);

      CheckMenuItem r = new CheckMenuItem(prefName);
      r.setGroup("preferenceMenuItem");
      r.addSelectionListener(new SelectionListener<MenuEvent>() {

        @Override
        public void componentSelected(MenuEvent ce) {
          grid.setUserPreferenceHeaderId(prefId);
          Dispatcher.forwardEvent(EventList.UpdateColumnModel, grid);
        }

      });
      userPreferenceMenu.add(r);

    }
  }

  public Template getTemplate() {
    return this.comboTemplate.getSimpleValue();
  }
  
  public char getTextSeparator() {
    String sep = ts.getSimpleValue();
    if ((sep != null) && (sep.compareTo("")!=0)){
      return ts.getSimpleValue().charAt(0);
    }else{
      return '\0';
    }
  }
  
  public char getFieldSeparator() {
    String sep = fs.getValue();
    if ((sep != null) && (sep.compareTo("")!=0)){
      return fs.getValue().charAt(0);
    }else{
      return '\0';
    }
  }

  private void addAnalisysButton() {
    this.add(new Button("Analisi", IconHelper.createStyle("icon-grid"),
        getListenerWithGrid(EventList.Jungle)));
  }

  private void addChartButton() {
    Button button = new Button("Grafici", IconHelper.createStyle("icon-chart"));

    Menu menu = new Menu();

    menu.add(new MenuItem("Torta", IconHelper.createStyle("icon-chart-pie"),
        getListenerWithGrid(EventList.ShowPieChart)));

    menu.add(new MenuItem("Istogramma",
        IconHelper.createStyle("icon-chart-bar"),
        getListenerWithGrid(EventList.ShowBarChart)));

    button.setMenu(menu);
    this.add(button);

  }

  public Button getButtonMenuPlugins() {
    return buttonMenuPlugins;
  }

  public void setButtonMenuPlugins(Button buttonMenuPlugins) {
    this.buttonMenuPlugins = buttonMenuPlugins;
  }
  
}
