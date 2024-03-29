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

package it.fub.jardin.client.mvc;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.model.ForeignKey;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.Plugin;
import it.fub.jardin.client.model.ResourcePermissions;
import it.fub.jardin.client.model.Resultset;
import it.fub.jardin.client.model.ResultsetFieldGroupings;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.SearchResult;
import it.fub.jardin.client.model.User;
import it.fub.jardin.client.tools.PopupOperations;
import it.fub.jardin.client.widget.AddRowForm;
import it.fub.jardin.client.widget.ChangePasswordDialog;
import it.fub.jardin.client.widget.HeaderArea;
import it.fub.jardin.client.widget.JardinAddingPopUp;
import it.fub.jardin.client.widget.JardinColumnModel;
import it.fub.jardin.client.widget.JardinDetail;
import it.fub.jardin.client.widget.JardinDetailPopUp;
import it.fub.jardin.client.widget.JardinEditorPopUp;
import it.fub.jardin.client.widget.JardinFormPopup;
import it.fub.jardin.client.widget.JardinGrid;
import it.fub.jardin.client.widget.JardinGridToolBar;
import it.fub.jardin.client.widget.JardinTabItem;
import it.fub.jardin.client.widget.LoginDialog;
import it.fub.jardin.client.widget.MassiveUpdateDialog;
import it.fub.jardin.client.widget.RegistrationForm;
import it.fub.jardin.client.widget.SearchAreaAdvanced;
import it.fub.jardin.client.widget.SearchAreaBase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.fx.Draggable;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class JardinView extends View {

  public static final String HEADER_AREA = "header-area";
  public static final String MAIN_AREA = "main-area";
  public static final String DETAIL_AREA = "detail-area";
  public static final String SEARCH_AREA = "search-area";
  public static final String SEARCH_AREA_ADVANCED = "search-area-advanced";
  private static final String ITEM_PREFIX = "item-id-";

  private JardinController controller;
  private Viewport viewport;
  // private ContentPanel search;
  private TabPanel main;
  private HeaderArea header;
  private LoginDialog dialog;
  private ChangePasswordDialog pwDialog;
  private RegistrationForm regForm;
  private User user;
  private List<Resultset> resultSetList; // listsa dei resultset almeno
                                         // leggibili dall'utente

  // private DialogBox newResultsetProgressMsg;<>

  public JardinView(final Controller controller) {
    super(controller);
    if (controller instanceof JardinController) {
      this.controller = (JardinController) controller;
    }
  }

  @Override
  protected void handleEvent(final AppEvent event) {
    EventType t = event.getType();
    if (t == EventList.Login) {
      if (event.getData() instanceof String) {
        this.login((String) event.getData());
      } else {
        this.login(null);
      }
    } else if (t == EventList.Init) {
      this.dialog.hide();
      if (pwDialog != null)
        this.pwDialog.hide();
      this.initUI();
    } else if (t == EventList.Logout) {
      this.viewport.removeAll();
      Dispatcher.forwardEvent(new AppEvent(EventList.Login));
    } else if (t == EventList.LoginError) {
      this.dialog.hide();
      String message;
      if (event.getData() instanceof String) {
        message = (String) event.getData();
      } else {
        message = "Login - Errore durante l'accesso";
      }
      this.loginError(message);
    } else if (t == EventList.OpenRegistrationForm) {
      regForm = new RegistrationForm();
    } else if (t == EventList.RegistrationSuccessfull) {
      Integer output = event.getData();
      regForm.hide();
      if (output == 2 || output == 3) {
        MessageBox.info(
            "Registrazione utente",
            "utente registrato con successo! controllare l'indirizzo email fornito per proseguire",
            null);
      } else
        MessageBox.alert(
            "Registrazione utente",
            "IMPOSSIBILE registrare utente!!! controllare che i dati inseriti siano quelli pre registrati",
            null);
    } else if (t == EventList.RegistrationError) {
      regForm.hide();
      MessageBox.info("Registrazione utente",
          "ERRORE: impossibile registrare l'utente", null);
    } else if (t == EventList.initialChangePassword) {
      this.dialog.hide();
      this.forcePasswordChange((User) event.getData());
      // String message;
      // if (event.getData() instanceof String) {
      // message = (String) event.getData();
      // } else {
      // message = "Errore durante l'accesso";
      // }
      // this.loginError(message);
    } else if (t == EventList.changePasswordError) {
      this.pwDialog.hide();
      String message;
      if (event.getData() instanceof User) {
        message =
            "errore di accesso per l'utente: "
                + ((User) event.getData()).getUsername();
      } else {
        message = "Cambio password - Errore durante l'accesso";
      }
      this.changePasswordError(message);
    } else if (t == EventList.GotResultsetList) {
      user = event.getData();
      createFirstTab(user);
      // } else if (t == EventList.NewResultset) {
      // if (event.getData() instanceof Integer) {
      // this.newResultset((Integer) event.getData());
      // }
    }
    // else if (t == EventList.GotValuesOfFields) {
    // if (event.getData() instanceof Integer) {
    // this.gotValuesOfFields((Integer) event.getData());
    // }
    // } else if (t == EventList.GotValuesOfForeignKeys) {
    // if (event.getData() instanceof Integer) {
    // this.gotValuesOfForeignKeys((Integer) event.getData());
    // }
    // }
    else if (t == EventList.Searched) {
      if (event.getData() instanceof SearchResult) {
        this.onSearched((SearchResult) event.getData());
      }
      /*
       * Gestione eventi della toolbar
       */
    } else if (t == EventList.Search) {
      if (event.getData() instanceof SearchParams) {
        this.onSearch((SearchParams) event.getData());
      }
      /*
       * Gestione eventi della toolbar
       */
    }
    // else if (t == EventList.AddRow) {
    // if (event.getData() instanceof Integer) {
    // this.onAddRow((Integer) event.getData());
    // }
    // }
    else if (t == EventList.ViewPopUpDetail) {
      if (event.getData() instanceof ArrayList<?>) {
        this.onViewPopUpDetail((ArrayList<BaseModelData>) event.getData());
      }
    } else if (t == EventList.ShowAllColumns) {
      if (event.getData() instanceof Integer) {
        this.onShowAllColumns((Integer) event.getData());
      }
    } else if (t == EventList.SaveGridView) {
      if (event.getData() instanceof Integer) {
        this.onSaveGridView((Integer) event.getData());
      }
    } else if (t == EventList.GotHeaderPreference) {
      this.updatePreferenceListMenu((HeaderPreferenceList) event.getData());
    } else if (t == EventList.GotPlugins) {
      this.updatePluginsMenu((ArrayList<Plugin>) event.getData());
    } else if (t == EventList.ViewPlugin) {
      this.viewPlugin((String) event.getData());
      /*
       * Altri eventi
       */
    } else if (t == EventList.gotResultsetImproved) {
      // System.out.println("evento arrivato!!!!");
      if (!user.getResultsets().contains((ResultsetImproved) event.getData())) {
        user.addResultsetToList((ResultsetImproved) event.getData());
      }
      this.addJardinTabItem((ResultsetImproved) event.getData());
    } else if (t == EventList.FkValuesRetrieved) {
      // System.out.println("evento arrivato!!!!");
      MassiveUpdateDialog mud =
          new MassiveUpdateDialog(
              ((SearchResult) event.getData()).getStore().getModels(),
              ((SearchResult) event.getData()).getSearchParams(),
              user.getResultsetImprovedFromId(((SearchResult) event.getData()).getResultsetId()));
      ((JardinGrid) this.getItemByResultsetId(
          ((SearchResult) event.getData()).getResultsetId()).getGrid()).setMassiveUpdateDialog(mud);
    } else if (t == EventList.GotValuesOfAField) {

      // System.out.println("sorgente: " + event.getData("source"));

      if (event.getData("source").toString().compareToIgnoreCase("detail") == 0) {
        String interestedFieldName =
            ((ForeignKey) event.getData("object")).getPointingFieldName();
        List valuesList = ((ForeignKey) event.getData("object")).getValues();
        JardinDetail interestedDetail =
            (JardinDetail) this.getItemByResultsetId(
                ((ForeignKey) event.getData("object")).getPointingResultsetId()).getDetail();

        ((SimpleComboBox) interestedDetail.getFieldByName(interestedFieldName)).add(valuesList);

      } else if (event.getData("source").toString().compareToIgnoreCase(
          "addingrowpopup") == 0) {
        String interestedFieldName =
            ((ForeignKey) event.getData("object")).getPointingFieldName();
        List valuesList = ((ForeignKey) event.getData("object")).getValues();
        AddRowForm interestedRF =
            (AddRowForm) this.getItemByResultsetId(
                ((ForeignKey) event.getData("object")).getPointingResultsetId()).getGrid().getAddRowForm();

        // ((SimpleComboBox)
        // interestedRF.getFieldByName(interestedFieldName)).removeAll();
        ((SimpleComboBox) interestedRF.getFieldByName(interestedFieldName)).add(valuesList);

      } else if (event.getData("source").toString().compareToIgnoreCase("grid") == 0) {
        String interestedFieldName =
            ((ForeignKey) event.getData("object")).getPointingFieldName();
        List valuesList = ((ForeignKey) event.getData("object")).getValues();
        JardinGrid interestedGrid =
            (JardinGrid) this.getItemByResultsetId(
                ((ForeignKey) event.getData("object")).getPointingResultsetId()).getGrid();

        ((SimpleComboBox) interestedGrid.getColumnModel().getColumnById(
            interestedFieldName).getEditor().getField()).add(valuesList);

      } else if (event.getData("source").toString().compareToIgnoreCase(
          "searcharea") == 0) {
        String interestedFieldName =
            ((SearchResult) event.getData("object")).getSearchParams().getFieldsValuesList().get(
                0).get("field");
        List<String> valuesList = new ArrayList<String>();
        for (int i = 0; i < ((SearchResult) event.getData("object")).getStore().getCount(); i++) {
          valuesList.add((String) ((SearchResult) event.getData("object")).getStore().getAt(
              i).get(interestedFieldName));
        }
        SearchAreaAdvanced interestedSA =
            (SearchAreaAdvanced) this.getItemByResultsetId(
                ((SearchResult) event.getData("object")).getResultsetId()).getSearchAreaAdvanced();

        ((SimpleComboBox) interestedSA.getFieldByName(interestedFieldName)).setToolTip("iniziare a scrivere...");
        // ((SimpleComboBox)
        // interestedSA.getFieldByName(interestedFieldName)).removeAll();
        ((SimpleComboBox) interestedSA.getFieldByName(interestedFieldName)).add(valuesList);

      } else if (event.getData("source").toString().compareToIgnoreCase(
          "addingpopup") == 0) {

        JardinAddingPopUp interestedJAP =
            ((JardinTabItem) this.main.getSelectedItem()).getGrid().getJardinAddingPopUp();

        String interestedFieldName =
            ((ForeignKey) event.getData("object")).getPointingFieldName();

        ((SimpleComboBox) interestedJAP.getFieldByName(interestedFieldName)).add(((ForeignKey) event.getData("object")).getValues());

      } else if (event.getData("source").toString().compareToIgnoreCase(
          "detailpopup") == 0) {
//        JardinDetailPopUp interestedJDP =
//            ((JardinTabItem) this.main.getSelectedItem()).getGrid().getJardinDetailPopup();
        JardinFormPopup interestedJDP =
            ((JardinTabItem) this.main.getSelectedItem()).getGrid().getJardinFormPopUp();

        String interestedFieldName =
            ((ForeignKey) event.getData("object")).getPointingFieldName();

        ((SimpleComboBox) interestedJDP.getFieldByName(interestedFieldName)).add(((ForeignKey) event.getData("object")).getValues());

      } else if (event.getData("source").toString().compareToIgnoreCase(
          "massupdatepopup") == 0) {
//        MassiveUpdateDialog interestedMUD =
//            ((JardinTabItem) this.main.getSelectedItem()).getGrid().getMassiveUpdateDialog();
        JardinFormPopup interestedMUD =
            ((JardinTabItem) this.main.getSelectedItem()).getGrid().getJardinFormPopUp();

        String interestedFieldName =
            ((ForeignKey) event.getData("object")).getPointingFieldName();

        ((SimpleComboBox) interestedMUD.getFieldByName(interestedFieldName)).add(((ForeignKey) event.getData("object")).getValues());

      } else if (event.getData("source").toString().compareToIgnoreCase(
          "editorpopup") == 0) {
//        JardinEditorPopUp interestedJEP =
//            ((JardinTabItem) this.main.getSelectedItem()).getGrid().getJardinEditorPopUp();
        JardinFormPopup interestedJEP =
            ((JardinTabItem) this.main.getSelectedItem()).getGrid().getJardinFormPopUp();

        String interestedFieldName =
            ((ForeignKey) event.getData("object")).getPointingFieldName();

        ((SimpleComboBox) interestedJEP.getFieldByName(interestedFieldName)).add(((ForeignKey) event.getData("object")).getValues());
        // gestire
      }
    } else if (t == EventList.refreshStore){
//      getItemByResultsetId(Integer.parseInt(event.getData().toString())).getGrid().getStore().
    }
  }

  private void forcePasswordChange(User user) {
    this.pwDialog = new ChangePasswordDialog(user);
    this.pwDialog.setClosable(false);
    this.pwDialog.show();

    // if (loginMessage != null) {
    // MessageBox.alert("Attenzione", loginMessage, null);
    // }

  }

  private void addJardinTabItem(ResultsetImproved resultset) {
    // TODO Auto-generated method stub
    // System.out.println("creazione nuovo tab per " + resultset.getName());
    JardinTabItem newTab = new JardinTabItem(resultset);
    newTab.setId(ITEM_PREFIX + resultset.getId());

    this.main.add(newTab);

    /* Creazione dell'area di ricerca avanzata */
    SearchAreaAdvanced searchAreaAdvanced =
        new SearchAreaAdvanced(resultset, user);

    /* Creazione della griglia */
    ListStore<BaseModelData> store = new ListStore<BaseModelData>();
    JardinColumnModel cm = new JardinColumnModel(resultset);
    JardinGrid grid = new JardinGrid(store, cm, resultset, user);

    new SearchAreaBase(resultset, user);
    /* Creazione del dettaglio */
    JardinDetail detail = new JardinDetail(resultset);

    if (this.getItemByResultsetId(resultset.getId()) != null) {
      FormBinding formBinding = new FormBinding(detail, false);
      newTab.setFormBinding(formBinding);
      grid.setFormBinding(formBinding);
      /* Aggiungere la griglia al tabItem */
      newTab.addGrid(grid);
      /* Aggiungere il dettaglio al tabitem */
      newTab.addDetail(detail);
      /* Aggiungere la ricerca avanzata al tabitem */
      newTab.addSearchAreaAdvanced(searchAreaAdvanced);
      
      
    } else
      System.out.println("tabitem nullo...impossibile aggiungere griglia e dettaglio");

  }

  private void createFirstTab(User user) {
    // TODO Auto-generated method stub
    this.resultSetList = user.getResultsetList();
    TabItem firstTab = new TabItem("Menù Principale");
    // AbsolutePanel con = new AbsolutePanel();
    SimplePanel firstTabPanel = new SimplePanel();
    FlexTable mainTable = new FlexTable();

    // int i = 0;
    int resultSetNumber = resultSetList.size();
    // System.out.println("lista rs:" + resultSetNumber);
    // impostiamo 5 colonne
    int numRows = 1;
    // int lastRowNumCols = resultSetNumber;
    int lastRowNumCols = 0;
    if (resultSetNumber > 5) {
      numRows = (int) Math.rint(resultSetNumber / 5);
      lastRowNumCols = resultSetNumber - (numRows * 5); // numero colonne
                                                        // dell'ultima riga
    }

    // System.out.println("numero righe: " + numRows);

    int index = 0;
    Resultset resultset = new Resultset();
    for (int x = 1; x <= numRows; x++) {

      for (int y = 1; y <= 5 && y <= resultSetNumber; y++) {

        // index = index + ((x * y) - 1);
        resultset = resultSetList.get(index);
//         System.out.println("set RS " + index + ": " + resultset.getAlias());
        ContentPanel cp = new ContentPanel();
        cp.setCollapsible(false);
        cp.setWidth(200);
        cp.setLayout(new RowLayout(Orientation.VERTICAL));
        cp.setFrame(true);
        cp.setHeading(resultset.getAlias());

        Label body = new Label(resultset.getNote());
        final int resId = resultset.getId();
        Button openButton = new Button("Apri Tab");

        openButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

          @Override
          public void componentSelected(ButtonEvent ce) {
            // TODO Auto-generated method stub
            JardinTabItem jti = getItemByResultsetId(resId);
            if (jti == null) {
              Dispatcher.forwardEvent(EventList.getResultsetImproved, resId);
            } else
              main.setSelection(jti);
          }
        });

        // cp.add(body);
        // cp.add(openButton);
        cp.add(body, new RowData(1, -1, new Margins(4)));
        cp.add(openButton, new RowData(1, -1, new Margins(4)));

        Draggable d = new Draggable(cp);
        d.setEnabled(Boolean.FALSE);

        mainTable.setWidget(x, y, cp);
        index++;

      }
    }

    if (lastRowNumCols > 0) {
      for (int y = 1; y <= lastRowNumCols; y++) {
        resultset = resultSetList.get(index + y - 1);
        // System.out.println("set RS " + index + ": " + resultset.getAlias());
        ContentPanel cp = new ContentPanel();
        cp.setCollapsible(false);
        cp.setWidth(200);
        cp.setLayout(new RowLayout(Orientation.VERTICAL));
        cp.setFrame(true);
        cp.setHeading(resultset.getAlias());
        Label body = new Label(resultset.getNote());
        final int resId = resultset.getId();
        Button openButton = new Button("Apri Tab");

        openButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

          @Override
          public void componentSelected(ButtonEvent ce) {
            // TODO Auto-generated method stub
            JardinTabItem jti = getItemByResultsetId(resId);
            if (jti == null) {
              Dispatcher.forwardEvent(EventList.getResultsetImproved, resId);
            } else
              main.setSelection(jti);
          }
        });
        cp.add(body, new RowData(1, -1, new Margins(4)));
        cp.add(openButton, new RowData(1, -1, new Margins(4)));

        Draggable d = new Draggable(cp);
        mainTable.setWidget(numRows + 1, y, cp);
      }
      // System.out.println("numero righe: " + (numRows+1));
      // System.out.println("num colonne ultima riga: " + lastRowNumCols);
    }

    // firstTab.add(con);
    mainTable.setCellPadding(10);
    mainTable.setCellSpacing(10);
    firstTabPanel.add(mainTable);
    firstTab.add(firstTabPanel);
    main.add(firstTab);
  }

  private void viewPlugin(final String data) {
    Window w = new Window();
    w.setTitle("Jardin Plugin");
    w.setModal(false);
    w.setResizable(true);
    w.setSize(800, 600);
    w.setMaximizable(true);
    // w.setToolTip("The Rnf Manager");
    w.setUrl(data);
    w.show();

  }

  private void updatePluginsMenu(final ArrayList<Plugin> plugins) {
    if ((plugins != null) && (plugins.size() > 0)) {
      JardinTabItem item = this.getItemByResultsetId(plugins.get(0).getRsId());
      JardinGridToolBar jgtb = item.getToolbar();
      Menu menuPlugins = new Menu();

      for (final Plugin plugin : plugins) {
        MenuItem mi = new MenuItem(plugin.getPluginName());

        mi.addSelectionListener(new SelectionListener() {
          @Override
          public void componentSelected(final ComponentEvent ce) {
            Dispatcher.forwardEvent(EventList.ViewPlugin,
                plugin.getConfigFile());
          }
        });
        menuPlugins.add(mi);
      }
      jgtb.getButtonMenuPlugins().setMenu(menuPlugins);
      jgtb.getButtonMenuPlugins().showMenu();
    }
  }

  private void onShowAllColumns(final int resultset) {
    JardinTabItem item = this.getItemByResultsetId(resultset);
    item.getGrid().showAllColumns();
  }

  // private void onAddRow(final int resultset) {
  // JardinTabItem item = this.getItemByResultsetId(resultset);
  // item.getGrid().addRow();
  // }

  // private void onViewPopUpDetail(final ArrayList<BaseModelData> infoToView) {
  // BaseModelData data = infoToView.get(0);
  // Integer rsId = data.get("RSID");
  // JardinTabItem item = this.getItemByResultsetId(rsId);
  // item.getGrid().viewDetailPopUp(infoToView);
  // }
  private void onViewPopUpDetail(final ArrayList<BaseModelData> infoToView) {
    BaseModelData data = infoToView.get(0);
    Integer rsId = data.get("RSID");
    JardinTabItem item = this.getItemByResultsetId(rsId);
    // fk.set("VALUE", selectedRow.get(field.getName()));
    // fk.set("RSLINKED", rs);
    Resultset res = (Resultset) data.get("RSLINKED");

    ResultsetImproved resImp =
        new ResultsetImproved(res.getId(), res.getName(), res.getAlias(),
            res.getStatement(), res.getPermissions().isReadperm(),
            res.getPermissions().isDeleteperm(),
            res.getPermissions().isModifyperm(),
            res.getPermissions().isInsertperm(), null);
    resImp.setFields(res.getResultsetListField());
    List<ResultsetFieldGroupings> resfg =
        new ArrayList<ResultsetFieldGroupings>();
    Iterator it = res.getFieldGroupings().entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pairs = (Map.Entry) it.next();
      resfg.add((ResultsetFieldGroupings) pairs.getValue()); // avoids a ConcurrentModificationException
    }
    resImp.setFieldGroupings(resfg);

    // System.out.println("rs linkato: " + res.getAlias());
    item.getGrid().setJardinFormPopUp(
        new JardinFormPopup(resImp, infoToView.get(1), (String) data.get("FK"),
            user.getUsername(), PopupOperations.ADDFKRECORD));
    // item.getGrid().viewDetailPopUp(infoToView);
  }

  private void initUI() {
    this.viewport = new Viewport();
    this.viewport.setLayout(new RowLayout(Orientation.VERTICAL));

    this.createHeader();
    this.createMain();

    RootPanel.get().add(this.viewport);
    // Dispatcher.forwardEvent(EventList.CreateUI);
    Dispatcher.forwardEvent(EventList.CreateFirstTab);
  }

  private void createHeader() {
    this.header = new HeaderArea(this.controller.getUser());
    this.header.setId(JardinView.HEADER_AREA);

    RowData rd = new RowData(1, 32);
    rd.setMargins(new Margins(0));
    this.viewport.add(this.header, rd);
  }

  private void createMain() {
    this.main = new TabPanel();
    this.main.setId(JardinView.MAIN_AREA);
    this.main.setAnimScroll(true);
    this.main.setTabScroll(true);

    RowData rd = new RowData(1, 1);
    this.viewport.add(this.main, rd);
  }

  /**
   * Restituisce il tabItem che rappresenta il resultset in base all'id o null
   * se non esiste
   * 
   * @param resultsetId
   *          l'id del resultset
   * @return un TabItem o null se non esiste
   */
  public synchronized JardinTabItem getItemByResultsetId(
      final Integer resultsetId) {
    // System.out.println("selezionato item: " + ITEM_PREFIX + resultsetId);
    if (this.main.getItemByItemId(ITEM_PREFIX + resultsetId) instanceof JardinTabItem) {
      JardinTabItem item =
          (JardinTabItem) this.main.getItemByItemId(ITEM_PREFIX + resultsetId);
      // TODO Verificare se è possibile togliere questa chiamata
      this.main.setSelection(item);
      return item;
    } else {
      return null;
    }
  }

  private void login(final String loginMessage) {

    this.dialog = new LoginDialog();
    this.dialog.setClosable(false);
    this.dialog.show();

    if (loginMessage != null) {
      MessageBox.alert("Attenzione", loginMessage, null);
    }
  }

  private void loginError(final String message) {
    final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
      public void handleEvent(final MessageBoxEvent we) {
        Dispatcher.forwardEvent(new AppEvent(EventList.Login));
      }
    };
    MessageBox.alert("Errore", message, l);
  }

  private void changePasswordError(final String message) {
    final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
      public void handleEvent(final MessageBoxEvent we) {
        Dispatcher.forwardEvent(new AppEvent(
            EventList.CheckCredentialAndChangePassword));
      }
    };
    MessageBox.alert("Errore", message, l);
  }

  private void onSearch(final SearchParams searchParams) {
    // TODO Auto-generated method stub
    JardinTabItem item =
        this.getItemByResultsetId(searchParams.getResultsetId());
    if (item != null) {
      if (item.getGrid() != null) {
        item.getGrid().setSearchparams(searchParams);
      }
    } else {
      addJardinTabItem(user.getResultsetImprovedFromId(searchParams.getResultsetId()));
      Dispatcher.forwardEvent(EventList.Search, searchParams);
    }
  }

  private void onSearched(final SearchResult result) {
    JardinTabItem item = this.getItemByResultsetId(result.getResultsetId());

    if (item != null) {
      if (item.getGrid() != null) {
        /* Aggiornamento dello store della griglia del tabItem */
        // item.updateStore(this.getStore(searchParams, true));
        item.updateStore(result.getStore());
        // item.getGrid().setSearchparams(searchParams);
      }
    } else {
      System.out.println("tabitem non ancora creato!!!!");
    }
  }

  private void onSaveGridView(final int resultset) {
    final JardinGrid grid = this.getItemByResultsetId(resultset).getGrid();
    final MessageBox box = MessageBox.prompt("Nome", "Salva visualizzazione");
    box.addCallback(new Listener<MessageBoxEvent>() {
      public void handleEvent(final MessageBoxEvent be) {
        grid.saveGridView(JardinView.this.controller.getUser(), be.getValue());
      }
    });
  }

  private void updatePreferenceListMenu(final HeaderPreferenceList data) {
    JardinTabItem item = this.getItemByResultsetId(data.getResultsetId());
    if (item != null) {
      /* Aggiungere la ricerca avanzata al tabitem */
      item.updatePreference(data);
    }
  }
}
