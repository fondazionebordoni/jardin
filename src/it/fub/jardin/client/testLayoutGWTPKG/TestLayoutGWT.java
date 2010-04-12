package it.fub.jardin.client.testLayoutGWTPKG;

import it.fub.jardin.client.Jardin;
import it.fub.jardin.client.ManagerService;
import it.fub.jardin.client.ManagerServiceAsync;
import it.fub.jardin.client.model.Credentials;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.User;
import it.fub.jardin.client.mvc.JardinController;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TestLayoutGWT implements EntryPoint {
	DockLayoutPanel mainDlp;
	DockLayoutPanel allTablesDlp;
	FlowPanel mainTablesButtonBar;	
	//DBSchema dbSchema ;
	//MainResultSetsArea horizMainResulSetsArea;
	MainResultSetsArea vertMainResulSetsArea;
	User user;

	public static final String SERVICE = "service";


    public static final String SERVER_ERROR =
	      "An error occurred while attempting to contact the server. "
	          + "Please check your network connection and try again.";

	  
	 private void onCheckUser(Credentials credentials) {

		    final ManagerServiceAsync service =
		        (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

		    /* Set up the callback */
		    AsyncCallback<User> callback = new AsyncCallback<User>() {
		      public void onFailure(Throwable caught) {
		        //Dispatcher.forwardEvent(EventList.LoginError,
		          //  caught.getLocalizedMessage());
		    	  System.out.println( "Failed call" + caught.getLocalizedMessage() );
		    	   caught.printStackTrace();
		    	  
		    	  
		      }

		      public void onSuccess(User remUser) {
		        //Dispatcher.forwardEvent(EventList.Init, user);
		    	  user = remUser;
		    	  guiCreate();
		      }
		    };

		    /* Make the call */
		    service.getUser(credentials, callback);
		  }

	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
	    ManagerServiceAsync service = GWT.create(ManagerService.class);
	    ServiceDefTarget endpoint = (ServiceDefTarget) service;
	    //String moduleRelativeURL = GWT.getModuleBaseURL() + SERVICE;
	    String moduleRelativeURL = "/jardin/" + SERVICE;
	    endpoint.setServiceEntryPoint(moduleRelativeURL);
	    Registry.register(SERVICE, service);

	    /*
	     * Creazione Dispatcher. Il dispacher si prende cura di instanziare i
	     * controller
	     */
	    Dispatcher dispatcher = Dispatcher.get();
	    dispatcher.addController(new JardinController());

	    GXT.hideLoadingPanel("loading");
		
		//onCheckUser (new Credentials("sportello1", "fub206"));
		onCheckUser (new Credentials("backoffice1", "fub206"));
	}

	
	void guiCreate(){
		//dbSchema =  new DBSchema();
		mainDlp = new DockLayoutPanel(Unit.EM);
		mainDlp.addNorth(new HTML("My Header"),2);		
		mainTablesButtonBar = createAllTablesButtons();		
		allTablesDlp = new DockLayoutPanel(Unit.EM);	
		allTablesDlp.addNorth(mainTablesButtonBar, 3);
		// MultiResGui multiResGui = new MultiResGui(dbSchema.mainResultSetSillyArrayList.get(0));
		vertMainResulSetsArea = new MainResultSetsArea(false);
		allTablesDlp.add(vertMainResulSetsArea);
		mainDlp.add(allTablesDlp);
		RootLayoutPanel.get().add(mainDlp);		
	
	}
	
	private void updateLayout(){
		vertMainResulSetsArea.updateGUI();
	}

		
	private FlowPanel createAllTablesButtons(){
		FlowPanel fp = new FlowPanel();
	    
		for (ResultsetImproved resultset : this.user.getResultsets()) {
	       // final Integer resultsetId = resultset.getId();
//		int allTablesNumber = dbSchema.mainResultSetSillyArrayList.size();
//		for (int i = 1; i <  allTablesNumber  + 1  ; i++) {
	        String resulSetAlias = resultset.getAlias();
	        Button srsB = new Button(resulSetAlias);
			srsB.setStylePrimaryName("unselectedButton");
			srsB.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					Object obj = event.getSource();
					Button sourceButton = (Button)obj;
					String buttonName = sourceButton.getText();
					//System.out.println(buttonName);
					//String resultSetIAliaString = buttonName.substring(6, buttonName.length());	
					try {
						//int resultSetId = Integer.parseInt(resultSetIdstring) ;	
						ResultsetImproved rs = user.getResultsetFromAlias(buttonName);
						int resultSetId = rs.getId();
						boolean removed = false;
//						if ( horizMainResulSetsArea.isShownResultSetbyIncomingKeysRelativeResultSetId(resultSetId) ) {
//							horizMainResulSetsArea.removeResultSetbyIncomingKeysRelativeResultSetId(resultSetId);
//							removed = true;
//						} 
						if ( vertMainResulSetsArea.isShownResultSetbyIncomingKeysRelativeResultSetId(resultSetId) ) {
							vertMainResulSetsArea.removeResultSetbyIncomingKeysRelativeResultSetId(resultSetId);
							removed = true;
						} 						
						if ( !removed) {
							createNewMainResultSetGui(rs);	
							sourceButton.setStylePrimaryName("selectedButton");
						} else {
							sourceButton.setStylePrimaryName("unselectedButton");
						}
						vertMainResulSetsArea.updateGUI();
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}					
				}
			  });
		fp.add(srsB);
		}
		return fp;
	}

	private void createNewMainResultSetGui (ResultsetImproved rs){
//		if (mainResultSet.correlatedResultSetSillyArrayList.get(incomingKeysRelativeResultSetId -1).isLarge) {
//			ResultSetSilly rs = new ResultSetSilly( incomingKeysRelativeResultSetId,
//					mainResultSet.correlatedResultSetSillyArrayList.get(incomingKeysRelativeResultSetId -1 ).isLarge);
//			ResultSetGui newResultSetGui = new ResultSetGui(rs );			
//			horizCorrelatedResulSetsArea.insertNonExistentNewResultSetGui(newResultSetGui);
//		} else {
		// ResultSetSilly rs = dbSchema.mainResultSetSillyArrayList.get(resultSetId -1); 
			MultiResGui newMultiResultSetGui = new MultiResGui(rs );			
			vertMainResulSetsArea.insertNonExistentNewResultSetGui(newMultiResultSetGui);
//		}
		updateLayout();
	}
}

