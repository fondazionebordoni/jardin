package it.fub.jardin.client.mvc;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.Jardin;
import it.fub.jardin.client.ManagerServiceAsync;
import it.fub.jardin.client.SearchStringParser;
import it.fub.jardin.client.model.Credentials;
import it.fub.jardin.client.model.EventTypeSerializable;
import it.fub.jardin.client.model.FieldsMatrix;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.IncomingForeignKeyInformation;
import it.fub.jardin.client.model.Message;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.Template;
import it.fub.jardin.client.model.User;
import it.fub.jardin.client.widget.JardinGrid;
import it.fub.jardin.client.widget.JardinTabItem;
import it.fub.jardin.client.widget.Jungle;
import it.fub.jardin.client.widget.UploadDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.BarDataProvider;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.PieDataProvider;
import com.extjs.gxt.charts.client.model.ScaleProvider;
import com.extjs.gxt.charts.client.model.charts.FilledBarChart;
import com.extjs.gxt.charts.client.model.charts.PieChart;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author acozzolino
 */
public class JardinController extends Controller {

	private static String webKitSuggest = "Jardin è ottimizzato per <i>WebKit</i>."
			+ " Per ottenere prestazioni migliori si consiglia di usare"
			+ " <a href=\"http://www.google.com/chrome/\">Chrome</a></li>"
			+ " o Safari";

	private User user;
	private JardinView view;

	private enum ChartType {
		PIE, BAR;
	}

	private static final String[] chartColors = { "#204a87", "#4e9a06",
			"#cc0000", "#75507b", "#f57900", "#edd400" };

	/**
	 * Creazione del controller generale per l'applicazione. Vengono registrati
	 * gli eventi che saranno gestiti da questo controller.
	 */
	public JardinController() {
		registerEventTypes(EventList.Login);
		registerEventTypes(EventList.Error);
		registerEventTypes(EventList.CheckUser);
		registerEventTypes(EventList.LoginError);
		registerEventTypes(EventList.Refresh);
		registerEventTypes(EventList.Init);
		registerEventTypes(EventList.CreateUI);
		registerEventTypes(EventList.Search);
		registerEventTypes(EventList.CommitChanges);
		registerEventTypes(EventList.AddRow);
		registerEventTypes(EventList.RemoveRows);
		registerEventTypes(EventList.ExportAllStoreAllColumns);
		registerEventTypes(EventList.ExportAllStoreSomeColumns);
		registerEventTypes(EventList.ExportSomeStoreAllColumns);
		registerEventTypes(EventList.ExportSomeStoreSomeColumns);
		registerEventTypes(EventList.ShowAllColumns);
		registerEventTypes(EventList.SaveGridView);
		registerEventTypes(EventList.GetGridViews);
		registerEventTypes(EventList.UploadTemplate);
		registerEventTypes(EventList.UploadImport);
		registerEventTypes(EventList.UpdateColumnModel);
		registerEventTypes(EventList.Jungle);
		registerEventTypes(EventList.ShowPieChart);
		registerEventTypes(EventList.ShowBarChart);
		registerEventTypes(EventList.SendMessage);
		registerEventTypes(EventList.NewMessage);
		registerEventTypes(EventList.ViewLinkedTable);
		registerEventTypes(EventList.ViewPopUpDetail);		
		registerEventTypes(EventList.UpdateCorrelatedResultset);
		registerEventTypes(EventList.SearchAllCorrelatedResultSet);
	}

	public void initialize() {
		view = new JardinView(this);
	}

	/**
	 * Gestione degli eventi. (non-Javadoc)
	 * 
	 * @see com.extjs.gxt.ui.client.mvc.Controller#handleEvent(com.extjs.gxt.ui.client.mvc.AppEvent)
	 */
	public void handleEvent(AppEvent event) {
		EventType t = event.getType();

		if (t == EventList.Login) {
			forwardToView(view, EventList.Login, loginMessage());
		} else if (t == EventList.CheckUser) {
			if (event.getData() instanceof Credentials) {
				Credentials credentials = (Credentials) event.getData();
				onCheckUser(credentials);
			} else {
				// TODO Gestire errore nei dati di EventList.CheckUser
			}
		} else if (t == EventList.Init) {
			if (event.getData() instanceof User) {
				User user = (User) event.getData();
				onInit(user);
			} else {
				// TODO Gestire errore nei dati di EventList.Init
			}
		} else if (t == EventList.Refresh) {
			onRefresh(event);
		} else if (t == EventList.Error) {
			if (event.getData() instanceof String) {
				onError((String) event.getData());
			} else {
				// TODO Gestire errore nei dati di EventList.Error
			}
		} else if (t == EventList.LoginError) {
			onLoginError(event);
		} else if (t == EventList.CreateUI) {
			onCreateUI();
		} else if (t == EventList.Search) {
			if (event.getData() instanceof SearchParams) {
				//System.out.println("evento search!!!");
				SearchParams searchParams = (SearchParams) event.getData();
				onSearch(searchParams);
			} else {
				// TODO Gestire errore nei dati di EventList.Search
			}
		} else if (t == EventList.CommitChanges) {
			if (event.getData() instanceof JardinGrid) {
				JardinGrid grid = (JardinGrid) event.getData();
				onCommitChanges(grid);
			} else {
				// TODO Gestire errore nei dati di EventList.CommitChanges
			}
			/*
			 * ------------------------------------------------------------------
			 * ----- Gestione eventi della toolbar
			 * ------------------------------
			 * -----------------------------------------
			 */
		} else if (t == EventList.AddRow) {
			if (event.getData() instanceof Integer) {
				onAddRow((Integer) event.getData());
			} else {
				// TODO Gestire errore nei dati di EventList.AddRow
				Log.error("Errore nei dati di EventList.AddRow");
			}
		} else if (t == EventList.ViewPopUpDetail) {
			if (event.getData() instanceof BaseModelData) {
				onViewPopUpDetail((BaseModelData) event.getData());
			} else {
				// TODO Gestire errore nei dati di EventList.ViewPopUpDetail
				Log.error("Errore nei dati di EventList.ViewPopUpDetail");
			}
		} else if (t == EventList.RemoveRows) {
			if (event.getData() instanceof Integer) {
				onRemoveRows((Integer) event.getData());
			} else {
				// TODO Gestire errore nei dati di EventList.RemoveRows
				Log.error("Errore nei dati di EventList.RemoveRows");
			}
		} else if (t == EventList.ExportAllStoreAllColumns) {
			if (event.getData() instanceof Integer) {
				onExport((Integer) event.getData(), true, true);
			} else {
				// TODO Gestire errore nei dati di
				// EventList.ExportAllStoreAllColumns
				Log
						.error("Errore nei dati di EventList.ExportAllStoreAllColumns");
			}
		} else if (t == EventList.ExportAllStoreSomeColumns) {
			if (event.getData() instanceof Integer) {
				onExport((Integer) event.getData(), true, false);
			} else {
				// TODO Gestire errore nei dati di
				// EventList.ExportAllStoreSomeColumns
				Log
						.error("Errore nei dati di EventList.ExportAllStoreSomeColumns");
			}
		} else if (t == EventList.ExportSomeStoreAllColumns) {
			if (event.getData() instanceof Integer) {
				onExport((Integer) event.getData(), false, true);
			} else {
				// TODO Gestire errore nei dati di
				// EventList.ExportAllStoreAllColumns
				Log
						.error("Errore nei dati di EventList.ExportAllStoreAllColumns");
			}
		} else if (t == EventList.ExportSomeStoreSomeColumns) {
			if (event.getData() instanceof Integer) {
				onExport((Integer) event.getData(), false, false);
			} else {
				// TODO Gestire errore nei dati di
				// EventList.ExportSomeStoreSomeColumns
				Log
						.error("Errore nei dati di EventList.ExportAllStoreSomeColumns");
			}
		} else if (t == EventList.ShowAllColumns) {
			if (event.getData() instanceof Integer) {
				onShowAllColumns((Integer) event.getData());
			} else {
				// TODO Gestire errore nei dati di EventList.ShowAllColumns
				Log.error("Errore nei dati di EventList.ShowAllColumns");
			}
		} else if (t == EventList.SaveGridView) {
			if (event.getData() instanceof Integer) {
				onSaveGridView((Integer) event.getData());
			} else {
				// TODO Gestire errore nei dati di EventList.SaveGridView
				Log.error("Errore nei dati di EventList.SaveGridView");
			}
		} else if (t == EventList.GetGridViews) {
			if (event.getData() instanceof Integer) {
				onGetGridViews((Integer) event.getData());
			} else {
				// TODO Gestire errore nei dati di EventList.GetGridViews
				Log.error("Errore nei dati di EventList.GetGridViews");
			}
		} else if (t == EventList.UploadTemplate) {
			if (event.getData() instanceof Integer) {
				onUploadTemplate((Integer) event.getData());
			} else {
				// TODO Gestire errore nei dati di EventList.UploadTemplate
				Log.error("Errore nei dati di EventList.UploadTemplate");
			}
		} else if (t == EventList.UploadImport) {
			if (event.getData() instanceof Integer) {
				onUploadImport((Integer) event.getData());
			} else {
				// TODO Gestire errore nei dati di EventList.UploadImport
				Log.error("Errore nei dati di EventList.UploadImport");
			}
		} else if (t == EventList.UpdateColumnModel) {
			// TODO CAMBIARE!!!
			if (event.getData() instanceof JardinGrid) {
				onUpdateColumnModel((JardinGrid) event.getData());
			} else {
				// TODO Gestire errore nei dati di EventList.UpdateColumnModel
				Log.error("Errore nei dati di EventList.UpdateColumnModel");
			}
		} else if (t == EventList.Jungle) {
			if (event.getData() instanceof Integer) {
				onJungle((Integer) event.getData());
			} else {
				// TODO Gestire errore nei dati di EventList.Jungle
				Log.error("Errore nei dati di EventList.Jungle");
			}
		} else if (t == EventList.ShowPieChart) {
			if (event.getData() instanceof Integer) {
				onShowChart(ChartType.PIE, (Integer) event.getData());
			} else {
				// TODO Gestire errore nei dati di EventList.ShowPieChart
				Log.error("Errore nei dati di EventList.ShowPieChart");
			}
		} else if (t == EventList.ShowBarChart) {
			if (event.getData() instanceof Integer) {
				onShowChart(ChartType.BAR, (Integer) event.getData());
			} else {
				// TODO Gestire errore nei dati di EventList.ShowBarChart
				Log.error("Errore nei dati di EventList.ShowBarChart");
			}
			/*
			 * ------------------------------------------------------------------
			 * ----- Altri eventi
			 * ------------------------------------------------
			 * -----------------------
			 */
		} else if (t == EventList.UpdateTemplates) {
			if (event.getData() instanceof Integer) {
				onUpdateTemplates((Integer) event.getData());
			} else {
				// TODO Gestire errore nei dati di EventList.UpdateTemplates
				Log.error("Errore nei dati di EventList.UpdateTemplates");
			}
		} else if (t == EventList.SendMessage) {
			if (event.getData() instanceof Message) {
				onSendMessage((Message) event.getData());
			} else {
				// TODO Gestire errore nei dati di EventList.SendMessage
				Log.error("Errore nei dati di EventList.SendMessage");
			}
		} else if (t.getEventCode() == EventList.NewMessage.getEventCode()) {
			onNewMessage();
		} else if (t == EventList.ViewLinkedTable) {
			onViewLinkedResultsetForNewTab((IncomingForeignKeyInformation) event
					.getData());
		} else if (t == EventList.UpdateCorrelatedResultset) {
			onViewLinkedResultsetForCorrelatedResultset((IncomingForeignKeyInformation) event
					.getData());
		} 
	}
	
	private void onNewMessage() {
		final ManagerServiceAsync service = (ManagerServiceAsync) Registry
				.get(Jardin.SERVICE);

		/* Set up the callback */
		AsyncCallback<List<Message>> callback = new AsyncCallback<List<Message>>() {

			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			public void onSuccess(List<Message> messages) {
				user.setMessages(messages);
				Info.display("Informazione", "Hai ricevuto un nuovo messaggio");
				// forwardToView(view, EventList.NewMessage, null);
			}
		};

		/* Make the call */
		service.getUserMessages(user.getUid(), callback);
	}

	private void onSendMessage(Message message) {

		/* Fill message with sender id */
		if (message.getSender() < 0) {
			message.setSender(user.getUid());
		}

		final ManagerServiceAsync service = (ManagerServiceAsync) Registry
				.get(Jardin.SERVICE);

		/* Set up the callback */
		AsyncCallback<?> callback = new AsyncCallback<Object>() {

			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			public void onSuccess(Object result) {
				Info
						.display("Informazione",
								"Messaggio inviato e memorizzato.");
				// TODO Auto-generated method stub

			}
		};

		/* Make the call */
		service.sendMessage(message, callback);
	}

	public User getUser() {
		return user;
	}

	private String loginMessage() {
		if (GXT.isWebKit) {
			return null;
		} else {
			return webKitSuggest;
		}
	}

	private void onError(String error) {
		MessageBox.alert("Errore", error, null);
	}

	private void onCheckUser(Credentials credentials) {

		final ManagerServiceAsync service = (ManagerServiceAsync) Registry
				.get(Jardin.SERVICE);

		/* Set up the callback */
		AsyncCallback<User> callback = new AsyncCallback<User>() {
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent(EventList.LoginError, caught
						.getLocalizedMessage());
			}

			public void onSuccess(User user) {
				Dispatcher.forwardEvent(EventList.Init, user);
			}
		};

		/* Make the call */
		service.getUser(credentials, callback);
	}

	private void onLoginError(AppEvent event) {
		forwardToView(view, event);
	}

	private void onRefresh(AppEvent event) {
		forwardToView(view, event);
	}

	private void onInit(User user) {
		this.user = user;
		user.addEvent(EventList.NewMessage);

		final ManagerServiceAsync service = (ManagerServiceAsync) Registry
				.get(Jardin.SERVICE);

		AsyncCallback<List<EventTypeSerializable>> callback = new AsyncCallback<List<EventTypeSerializable>>() {

			public void onSuccess(List<EventTypeSerializable> eventTypes) {

				for (EventTypeSerializable eventType : eventTypes) {
					handleEvent(new AppEvent(eventType));
				}
				service.getEvents(this);
			}

			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				System.out.println( "JardinView onInit onFailure");
			}
		};

		service.getEvents(callback);

		forwardToView(view, EventList.Init, user);
	}

		
	
	private void onCreateUI() {
		/* Vedi sequence diagram init_sd.pic */
		/* Per ogni resultset carica da service le sue proprietà */
		//view.createUI(user);
		//GotValuesOfFields();
		gotValuesOfForeignKeys();
	}

//	private void allNewResultset() {
//		/* Vedi sequence diagram init_sd.pic */
//		/* Per ogni resultset carica da service le sue proprietà */
//		for (ResultsetImproved resultset : this.user.getResultsets()) {
//			final Integer resultsetId = resultset.getId();
//			forwardToView(view, EventList.NewResultset, resultsetId);
//		}
//	}

//	private void GotValuesOfFields(){
//		for (ResultsetImproved resultset : this.user.getResultsets()) {
//			final Integer resultsetId = resultset.getId();
//			/* Avvisa la view che si sta creando un nuovo resultset */
////			forwardToView(view, EventList.NewResultset, resultsetId);
//			forwardToView(view, EventList.GotValuesOfFields, resultsetId);
//		}
//	}
	
	private void gotValuesOfForeignKeys(){
		for (ResultsetImproved resultset : this.user.getResultsets()) {
			final Integer resultsetId = resultset.getId();
			/* Avvisa la view che si sta creando un nuovo resultset */
//			forwardToView(view, EventList.NewResultset, resultsetId);
			//forwardToView(view, EventList.GotValuesOfFields, resultsetId);
			// forwardToView(view, EventList.gotValuesOfForeignKeys,
			// resultsetId);
			/*
			 * Carica i valori dei vincoli di integrità referenziale attualmente
			 * presenti: sever per il binding dei campi del dettaglio e per il
			 * row editor
			 */
			final ManagerServiceAsync service = (ManagerServiceAsync) Registry
			.get(Jardin.SERVICE);

			AsyncCallback<FieldsMatrix> callbackValuesOfForeignKeys = new AsyncCallback<FieldsMatrix>() {
				public void onFailure(Throwable caught) {
					Dispatcher.forwardEvent(EventList.Error, caught
							.getLocalizedMessage());
				}
				public void onSuccess(FieldsMatrix fieldsMatrix) {
					ResultsetImproved rs = user.getResultsetFromId(resultsetId);
					rs.setForeignKeyList(fieldsMatrix);
					forwardToView(view, EventList.GotValuesOfForeignKeys,
							resultsetId);
				}
			};
			service.getValuesOfForeignKeys(resultsetId,
					callbackValuesOfForeignKeys);
		}
	}

	private void onSearch(SearchParams searchParams) {
		// TODO Modificare per gestire il solo resultsetId come parametro
		if (user.getResultsetFromId(searchParams.getResultsetId()).isRead()) {
			forwardToView(view, EventList.Search, searchParams);
		} else {
			Dispatcher.forwardEvent(EventList.Error,
					"L'utente non dispone dei permessi di lettura");
		}
	}

	private void onCommitChanges(final JardinGrid grid) {

		// TODO Modificare per gestire il solo resultsetID come parametro
		ResultsetImproved resultset = grid.getResultset();
		if (resultset.isInsert() || resultset.isModify()) {

			List<BaseModelData> newItemList = new ArrayList<BaseModelData>();
			for (Record rec : grid.getStore().getModifiedRecords()) {
				newItemList.add((BaseModelData) rec.getModel());
			}

			if (newItemList.size() > 0) {
				final MessageBox waitbox = MessageBox.wait("Attendere",
						"Salvataggio modifiche in corso...", "");

				/* Create the service proxy class */
				final ManagerServiceAsync service = (ManagerServiceAsync) Registry
						.get(Jardin.SERVICE);

				/* Set up the callback */
				AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
					public void onFailure(Throwable caught) {
						waitbox.close();
						grid.getStore().rejectChanges();
						Dispatcher.forwardEvent(EventList.Error, caught
								.getLocalizedMessage());
					}

					public void onSuccess(Integer retCode) {
						waitbox.close();
						grid.getStore().commitChanges();
						Info.display("Informazione",
								"Modifiche salvate sul Database");
					}
				};

				/* Make the call */
				service.setObjects(resultset.getId(), newItemList, callback);
			} else {
				Info.display("Informazione", "Nessuna modifica da salvare", "");
			}
		} else {
			Dispatcher.forwardEvent(EventList.Error,
					"L'utente non dispone dei permessi di modifica");
		}
	}

	private void onAddRow(int resultset) {
		if (user.getResultsetFromId(resultset).isInsert()) {
			forwardToView(view, EventList.AddRow, resultset);
		} else {
			Dispatcher.forwardEvent(EventList.Error,
					"L'utente non dispone dei permessi di inserimento");
		}
	}

	private void onViewPopUpDetail(final BaseModelData data) {
		if (((ResultsetImproved) data.get("RSLINKED")).isRead()) {
			// restituisce il BaseModelData con la riga interessata

			final ManagerServiceAsync service = (ManagerServiceAsync) Registry
					.get(Jardin.SERVICE);

			AsyncCallback<ArrayList<BaseModelData>> callbackPopUpDetailEntry = new AsyncCallback<ArrayList<BaseModelData>>() {
				public void onFailure(Throwable caught) {
					Dispatcher.forwardEvent(EventList.Error, caught
							.getLocalizedMessage());
				}

				public void onSuccess(
						ArrayList<BaseModelData> infoToView) {
					forwardToView(view, EventList.ViewPopUpDetail, infoToView);
				}
			};

			service.getPopUpDetailEntry(data, callbackPopUpDetailEntry);

		} else {
			Dispatcher.forwardEvent(EventList.Error,
					"L'utente non dispone dei permessi di visualizzazione");
		}
	}

	private void onRemoveRows(int resultset) {
		if (user.getResultsetFromId(resultset).isDelete()) {
			for (final JardinGrid grid : view.getGridListByResultSetId(resultset)){
	//			final JardinGrid grid = view.getItemByResultsetId(resultset)
	//					.getGrid(resultset);
				final List<BaseModelData> selectedRows = grid.getSelectionModel()
						.getSelection();
	
				if (selectedRows.size() > 0) {
					final MessageBox waitbox = MessageBox.wait("Attendere",
							"Eliminazione righe in corso...", "");
	
					/* Create the service proxy class */
					final ManagerServiceAsync service = (ManagerServiceAsync) Registry
							.get(Jardin.SERVICE);
	
					/* Set up the callback */
					AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
						public void onFailure(Throwable caught) {
							waitbox.close();
							Dispatcher.forwardEvent(EventList.Error, caught
									.getLocalizedMessage());
						}
	
						public void onSuccess(Integer result) {
							waitbox.close();
							if (result.intValue() <= 0) {
								Dispatcher.forwardEvent(EventList.Error,
										"Nessuna riga eliminata");
							} else {
								ListStore<BaseModelData> store = grid.getStore();
								for (BaseModelData row : selectedRows) {
									store.remove(row);
								}
								store.commitChanges();
								Info.display("Informazione", "Dati cancellati", "");
							}
						}
					};
	
					/* Make the call */
					service.removeObjects(resultset, selectedRows, callback);
					
				} else {
					Info.display("Informazione", "Selezionare almeno una riga", "");
				}
			}
		} else {
			Dispatcher.forwardEvent(EventList.Error,
					"L'utente non dispone dei permessi di eliminazione");
		}
	}

	/**
	 * Esporta tutti i dati contenuti nella griglia in formato CSV
	 * 
	 * @param grid
	 *            la griglia che contiene i dati da esportare
	 * @param allStore
	 *            se esportare o no tutti i record dello store o solo quelli
	 *            visualizzati nella griglia
	 * @param allColumns
	 *            se esportare o no tutte le colonne dello store o solo quelle
	 *            visualizate nella griglia
	 */
	private void onExport(int resultset, boolean allStore, boolean allColumns) {

		/* Nome del file da creare */
		String filename = user.getResultsetFromId(resultset).getAlias();

		/*
		 * Prendi il tabItem per recuperare la toolbar (formato d'esportazione)
		 * e la grid (config dei record da esportare, colonne visibili e criteri
		 * di ricerca)
		 */
		JardinTabItem item = view.getItemByResultsetId(resultset);

		/* Prendi il formato di esportazione */
		Template template = item.getToolbar(resultset).getTemplate();

		/* Prendi la griglia */
		JardinGrid grid = item.getGridFromResultSetId(resultset);

		/* Config dei record da esportare (tutti se allStore = true) */
		PagingLoadConfig config = null;
		if (!allStore) {
			config = (PagingLoadConfig) grid.getStore().getLoadConfig();
		}

		/* Colonne visibili (tutte se allColumns = true) */
		ColumnModel cm = grid.getColumnModel();
		List<String> columns = new ArrayList<String>();
		for (int i = 0; i < cm.getColumnCount(); i++) {
			if (!cm.getColumn(i).isHidden() || allColumns) {
				columns.add(cm.getColumn(i).getId());
			}
		}

		/* Criteri di ricerca */
		SearchParams searchParams = grid.getSearchparams();

		/* Effettua la chiamata RPC */
		final ManagerServiceAsync service = (ManagerServiceAsync) Registry
				.get(Jardin.SERVICE);

		AsyncCallback<String> callback = new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent(EventList.Error, caught
						.getLocalizedMessage());
			}

			public void onSuccess(String result) {
				if (result != null && result.length() > 0) {
					Log.debug("Export file: " + result);
					String url = GWT.getModuleBaseURL() + "download?file="
							+ result;
					Window.open(url, "Download", null);
				} else {
					Log.warn("File d'esportazione vuoto");
				}
			}
		};

		service.createReport("/tmp/" + filename, template, config, columns,
				searchParams, callback);
	}

	private void onShowAllColumns(int resultset) {
		forwardToView(view, EventList.ShowAllColumns, resultset);
	}

	private void onSaveGridView(int resultset) {
		forwardToView(view, EventList.SaveGridView, resultset);
	}

	private void onGetGridViews(int resultset) {
		final ManagerServiceAsync service = (ManagerServiceAsync) Registry
				.get(Jardin.SERVICE);

		AsyncCallback<HeaderPreferenceList> callback = new AsyncCallback<HeaderPreferenceList>() {
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent(EventList.Error, caught
						.getLocalizedMessage());
			}

			public void onSuccess(HeaderPreferenceList result) {
				Info.display("Informazione", "Lista delle preferenze caricata");
				forwardToView(view, EventList.GotHeaderPreference, result);
			}
		};

		service.getGridViews(user.getUid(), resultset, callback);
	}

	private void onUploadTemplate(int resultset) {
		UploadDialog d = new UploadDialog(user, UploadDialog.TYPE_TEMPLATE,
				resultset);
		d.show();
	}

	private void onUploadImport(int resultset) {
		UploadDialog d = new UploadDialog(user, UploadDialog.TYPE_IMPORT,
				resultset);
		d.show();
	}

	private void onUpdateColumnModel(final JardinGrid grid) {
		final ManagerServiceAsync service = (ManagerServiceAsync) Registry
				.get(Jardin.SERVICE);

		AsyncCallback<List<Integer>> callback = new AsyncCallback<List<Integer>>() {
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent(EventList.Error, caught
						.getLocalizedMessage());
			}

			public void onSuccess(List<Integer> result) {
				grid.updateGridHeader(result);
			}
		};

		service.getHeaderUserPreference(user.getUid(), grid
				.getUserPreferenceHeaderId(), callback);
	}

	private void onJungle(final int resultset) {

		/* Nome del file da creare */
		String filename = user.getResultsetFromId(resultset).getAlias();

		/*
		 * Prendi il tabItem per recuperare la toolbar (formato d'esportazione)
		 * e la grid (config dei record da esportare, colonne visibili e criteri
		 * di ricerca)
		 */
		JardinTabItem item = view.getItemByResultsetId(resultset);
		
		/* Prendi la griglia */
		JardinGrid grid = item.getGridFromResultSetId(resultset);		
			/* Colonne */
		ColumnModel cm = grid.getColumnModel();
		List<String> columns = new ArrayList<String>();
		for (int i = 0; i < cm.getColumnCount(); i++) {
			columns.add(cm.getColumn(i).getId());
		}	

		/* Criteri di ricerca */
		SearchParams searchParams = grid.getSearchparams();
				
		/* Effettua la chiamata RPC */
		final ManagerServiceAsync service = (ManagerServiceAsync) Registry
				.get(Jardin.SERVICE);

		AsyncCallback<String> callback = new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent(EventList.Error, caught
						.getLocalizedMessage());
			}

			public void onSuccess(String result) {
				if (result != null && result.length() > 0) {
					Jungle j = new Jungle(user.getResultsetFromId(resultset),
							result);
					j.show();
				} else {
					Log.warn("File d'esportazione vuoto");
				}
			}
		};

		service.createReport("/tmp/" + filename, Template.XML, null, columns,
				searchParams, callback);

	}

	/**
	 * Genera un grafico prendendo le prime due colonne visibili della griglia.
	 * La prima deve essere di tipo stringa o numerico, la seconda deve essere
	 * di tipo numerico
	 * 
	 * @param type
	 * @param resultset
	 */
	private void onShowChart(ChartType type, Integer resultset) {

		/*
		 * Prendi il tabItem per recuperare la toolbar (formato d'esportazione)
		 * e la grid (config dei record da esportare, colonne visibili e criteri
		 * di ricerca)
		 */
		JardinTabItem item = view.getItemByResultsetId(resultset);

		/* Prendi la griglia */
		JardinGrid grid = item.getGridFromResultSetId(resultset);

		/* Prendi gli ID delle prime due colonne visibili */
		ColumnModel columnModel = grid.getColumnModel();
		String cx = null;
		String cy = null;
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			ColumnConfig cf = columnModel.getColumn(i);
			if (!cf.isHidden()) {
				if (cx == null) {
					cx = cf.getId();
				} else {
					cy = cf.getId();
					break;
				}
			}
		}

		String url = "resources/chart/open-flash-chart.swf";

		Chart chart = new Chart(url);
		chart.setBorders(false);

		String resultsetAlias = user.getResultsetFromId(resultset).getAlias();
		ChartModel cm = new ChartModel(resultsetAlias);
		cm.setBackgroundColour("#ffffff");

		switch (type) {
		case BAR:
			FilledBarChart bar = new FilledBarChart();
			bar.setAnimateOnShow(true);
			bar.setTooltip("#val#");
			BarDataProvider bdp = new BarDataProvider(cy, cx);
			bdp.bind(grid.getStore());
			bar.setDataProvider(bdp);
			cm.setScaleProvider(ScaleProvider.ROUNDED_NEAREST_SCALE_PROVIDER);
			cm.addChartConfig(bar);
			break;
		case PIE:
		default:
			PieChart pie = new PieChart();
			pie.setAlpha(0.5f);
			pie.setNoLabels(false);
			pie.setTooltip("#label# #val#<br>#percent#");
			pie.setGradientFill(true);
			pie.setColours(chartColors);
			PieDataProvider pdp = new PieDataProvider(cy, cx);
			pdp.bind(grid.getStore());
			pie.setDataProvider(pdp);
			cm.addChartConfig(pie);
			break;
		}
		chart.setChartModel(cm);

		Dialog d = new Dialog();
		d.getButtonBar().removeAll();
		d.setMaximizable(true);
		d.setHeading("Grafico " + resultsetAlias);
		d.setIconStyle("icon-chart");
		d.setLayout(new FitLayout());
		d.setSize(500, 500);
		d.add(chart);
		d.show();
	}

	private void onUpdateTemplates(int resultset) {
		// TODO Auto-generated method stub
	}

	private SearchParams onViewLinkedResultset(IncomingForeignKeyInformation ifki) {
		//String linkedTable = ifki.getLinkingTable();
		ResultsetImproved rs = ifki.getInterestedResultset();
		SearchParams searchParams = new SearchParams(rs.getId());
		searchParams.setAccurate(true);
		List<BaseModelData> queryFieldList = new ArrayList<BaseModelData>();

		SearchStringParser parser = new SearchStringParser(ifki
				.getLinkingField()
				+ ":" + ifki.getFieldValue());

		Map<String, String> searchMap = parser.getSearchMap();
		for (String key : parser.getSearchMap().keySet()) {
			// TODO migliorare la gestione per il case insensitive
			key = key.toLowerCase();
			BaseModelData m = new BaseModelData();
			m.set(key, searchMap.get(key));
			queryFieldList.add(m);
		}
		searchParams.setFieldsValuesList(queryFieldList);
		return searchParams;
		///Dispatcher.forwardEvent(EventList.Search, searchParams);
	}
	
	private void onViewLinkedResultsetForNewTab(IncomingForeignKeyInformation ifki) {
		SearchParams searchParams = onViewLinkedResultset( ifki);
		Dispatcher.forwardEvent(EventList.Search, searchParams);
	}

	
	private void onViewLinkedResultsetForCorrelatedResultset(IncomingForeignKeyInformation ifki) {
			SearchParams searchParams = onViewLinkedResultset( ifki);
			Dispatcher.forwardEvent(EventList.Search, searchParams);			
	}

	
}
