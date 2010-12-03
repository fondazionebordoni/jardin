/**
 * 
 */
package it.fub.jardin.client.widget;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.Template;
import it.fub.jardin.client.model.Tool;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

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
  }

  @SuppressWarnings("unchecked")
  private void addModifyActions() {

    // TODO Non inserire il bottone se l'azione non è consentita

    if (grid.getResultset().isInsert()) {
      this.add(new Button("Aggiungi riga", IconHelper.createStyle("icon-add"),
          getListenerWithGrid(EventList.AddRow)));
    } else {
      //this.add(new Button("Aggiungi riga", IconHelper.createStyle("icon-add-disabled")));
    }

    if (grid.getResultset().isDelete()) {
      this.add(new Button("Rimuovi righe",
          IconHelper.createStyle("icon-delete"),
          getListenerWithGrid(EventList.RemoveRows)));
    } else {
      //this.add(new Button("Rimuovi righe", IconHelper.createStyle("icon-delete-disabled")));
    }
  }

//  private void addViewActions() {
//	      this.add(new Button("Dettaglio", IconHelper.createStyle("icon-add"),
//	    		new SelectionListener() {
//			          public void componentSelected(ComponentEvent e) {
//			        	  Dispatcher.forwardEvent(EventList.UpdateColumnModel, resultset);
//			          }
//	      		}
//	      ));
//  }

  
  @SuppressWarnings("unchecked")
  private void addExportActions() {

    Template defaultTemplate = new Template(Template.DEFAULT);
    defaultTemplate.setXsl(resultset + "_default.xsl");

    /* Combo box per la selezione del formato di esportazione */
    this.comboTemplate = new SimpleComboBox<Template>();
    this.comboTemplate.setForceSelection(true);
    this.comboTemplate.setEditable(false);
    this.comboTemplate.setWidth(80);
    this.comboTemplate.add(Template.CSV);
    this.comboTemplate.add(Template.XML);
    this.comboTemplate.add(defaultTemplate);
    this.comboTemplate.setSimpleValue(Template.CSV);
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
    Button b =
        new Button("Importa", IconHelper.createStyle("icon-import"),
            getListenerWithGrid(EventList.UploadImport));
    b.setToolTip("Se si carica un file contente uno più record già presenti "
        + "nel DB, il sistema aggiornerà tali record con i nuovi valori "
        + "contenuti nel file stesso.\n"
        + "<b>La coincidenza deve sussistere a livello di chiave primaria</b>");

    this.add(b);
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

}
