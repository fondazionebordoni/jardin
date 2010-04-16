package it.fub.jardin.client.testLayoutGWTPKG;

import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetImproved;

import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

public class ResultSetGui extends DockLayoutPanel {
	FlowPanel toolbar ;	
	SplitLayoutPanel gridAndDetailSplitPanel;
	SplitLayoutPanel navAndOthersSplitPanel;
	Grid navHTML;
	Grid detailHTML;
	Grid grid;
	boolean toolbarShown = true;;
	boolean navShown = false;
	boolean detailShown = false;
	boolean gridShown = true;
	//ResultSetSilly resultSetSilly;
	ResultsetImproved resultSetImproved;
	boolean  isRootResultSet;
	boolean  isLarge;	
	int rowNumber ; 
	int colNumber ; 
	Button gridButton;
	Button navButton;
	Button detailButton;
	Random random = new Random();
	
	String poolString = "singleton that serves as a root container and a dira, Cadore, Cadorna, Cafiera, Calcedonia, Calista, " +
			"Calliope, Callista, Calogera, Calvina, Camelia,  Cleopatra, Cleope, " +
			"Cleria, Clerice, Cleta, Clia, Cliceria, Climene, Clio, Clita, Clizia, Clodia, Clodomira, Clodovea, Cloe, Clora, Clori, " +
			"Cloride, Clorinda, Clotilde, Coletta,Derno, Diletto, Dilio, Dillo, Dilo Colomba, Colombina, Coltura, Comasia, Comincio," +
			" Comita, Comunarda, Cona, Concepita, " +
			"Concessa, Concessina, Concetta, Concettina,  Bernardo, Berniero, Bernino, Bertillo, Bertino, Berto Conchita, Concita, Concordia, " +
			"Conetta, Conforta, Conina," +
			" Consalva, ConsigliaFeria, Feriana, Ferida, Feride, Feridia, Feridie, Ferina, Ferma, Fermana, Fermentina, Fermilde, Fermilia," +
			" Fermilide, Fermina, Ferminia,Tolmino, Tolomeo, Tomasino, Tomaso, Tomassino, Tomasso, Tomeo, Tommasino, Tommaso, Tommassino," +
			" Tonello, Tonino, Tonio, Tonuccio, Tore, Torello Fernalda, Fernanda, , Leonia, Leonice, Leonida, LeonideFernella, Fernice, Fernida," +
			" Ferranda, " +
			"Ferrandina, Ferrara, Ferrarina, " +
			"Ferrea, Ferrera, Ferriana, Ferrida, Ferrina, Ferronia, Fertilia, Fervida, Festilia, Festina, Festosa, Fetina, Fiametta, " +
			"Fiamma, Fiammella, Fiammetta, Fiammina, Fiandra, ahead of a scheduled press conference in Brussels, the three companies " +
			"also said they would explore working together on technologies relating to electric vehicles and batteries to which all " +
			"other layout panels should be attached test text ";
	
	public ResultSetGui(ResultsetImproved resultSetSilly, boolean  itsRootResultSet){
		super(Unit.EM);	
		this.resultSetImproved = resultSetSilly;
		this.isRootResultSet = itsRootResultSet;
//		if (resultSetSilly.id == 0){
		if (itsRootResultSet){
			this.setStylePrimaryName("mainArea");
		}
		int a = random.nextInt(15); 
		int b = random.nextInt(15); 
		if (isLarge) {
			rowNumber = 2 + Math.min(a, b); 
			colNumber = 1 + Math.max(a, b);;
		} else {
			rowNumber = 2 + Math.max(a, b); 
			colNumber = 1 + Math.min(a, b);;
		} 
 
		createAndPlaceObjects ();
	}

	void checkIfLarge(){
		if (resultSetImproved.getFields().size()> 3){
			this.isLarge = true;
		} else {
			this.isLarge = false;			
		}
	}
	
	
	Grid createNavAndDetail(){
		Grid grid = new Grid(colNumber,2);
		for (int i = 0; i < colNumber; i++) {
			int casuale = random.nextInt(3);
			Label label = new Label();
			label.setText("Campo numero " + i + ":  ");
			grid.setWidget(i, 0, label);
			if (casuale == 0 ){
				final TextBox nameField = new TextBox();
				nameField.setText("GWT UserThis panel is a singleton that serves as a root container to which all other layout panels should be attached (see RequiresResize and ProvidesResize  ");
				grid.setWidget(i, 1, nameField);
			} else if (casuale == 1 ){
				ListBox listBox = new ListBox();
				for (int j = 0; j < 5; j++) {
					listBox.addItem ("item" + j);				
				}			
				grid.setWidget(i, 1, listBox);
			} else if (casuale == 2 ){
				CheckBox checkBox =  new CheckBox();
				checkBox.setValue(random.nextBoolean());
				grid.setWidget(i, 1, checkBox);
			}
			
		}
//		if (resultSetSilly.id == 0){
		if (isRootResultSet){
			grid.setStylePrimaryName("mainNavAndDetail");
		} else {
			grid.setStylePrimaryName("navAndDetail");			
		}
		return grid;
	}

//	FlowPanel createNavAndDetail(){		
//		FlowPanel flowPanel = new FlowPanel();
//		for (int i = 0; i < 6; i++) {
//			final TextBox nameField = new TextBox();
//			nameField.setText("GWT UserThis panel is a singleton that serves as a root container to which all other layout panels should be attached (see RequiresResize and ProvidesResize  ");
//			flowPanel.add(nameField);
//			ListBox listBox = new ListBox();
//			for (int j = 0; j < 5; j++) {
//				listBox.addItem ("item" + j);				
//			}
//			flowPanel.add(listBox);			
//		}
//		if (resultSetSilly.id == 0){
//			flowPanel.setStylePrimaryName("mainNavAndDetail");
//		}
//		return flowPanel;
//	}
	
	
	
	private void createAndPlaceObjects (){
		removeAllObjects();
		createObjects ();
		placeObjects();
	}

	private void removeAllObjects(){
		if (detailHTML != null) {
			detailHTML.removeFromParent();
		}		    	
		if (grid != null) {
			grid.removeFromParent();
		}				    	
		if (gridAndDetailSplitPanel != null) {
			gridAndDetailSplitPanel.removeFromParent();
		}		    	
		if (navHTML != null) {
			navHTML.removeFromParent();
		}
		if (navAndOthersSplitPanel != null) {
			navAndOthersSplitPanel.removeFromParent();
		}		    	
		if (toolbar != null) {
			toolbar.removeFromParent();
		} 
 
	}
	
	private void placeObjects(){
		if (isRootResultSet) {
		//if (resultSetSilly.id ==0){
			if(grid!= null){
				grid.setStylePrimaryName("mainResultSet");
			}
		}
		if (toolbarShown) {
			addNorth(toolbar, 3);			
		} 		
		if (gridShown) {
			if ( (!detailShown) && (!navShown) ) {
				add(grid);	
			} else {		
				if (navShown) {
					navAndOthersSplitPanel = new SplitLayoutPanel();
//					if (resultSetSilly.id == 0){
					if (isRootResultSet){
						navAndOthersSplitPanel.setStylePrimaryName("mainNavAndOthersSplitPanel");
					}
					navAndOthersSplitPanel.addWest(navHTML, 200);		
					if (detailShown) {					
						gridAndDetailSplitPanel = new SplitLayoutPanel();
						gridAndDetailSplitPanel.setStylePrimaryName("gridAndDetailSplitPanel");
 						//if (resultSetSilly.id == 0){
 	 						if (isRootResultSet){
							gridAndDetailSplitPanel.setStyleName("mainGridAndDetailSplitPanel");
						}
						gridAndDetailSplitPanel.addSouth(detailHTML, 200);
						gridAndDetailSplitPanel.add(grid);
						navAndOthersSplitPanel.add(gridAndDetailSplitPanel);
					} else {
						navAndOthersSplitPanel.add(grid);
					}
					add (navAndOthersSplitPanel);
				} else {					
						gridAndDetailSplitPanel = new SplitLayoutPanel();
						gridAndDetailSplitPanel.addSouth(detailHTML, 200);
						gridAndDetailSplitPanel.add(grid);
						add(gridAndDetailSplitPanel);
				}
			}
		}
	}
		
	private void createObjects (){
		if (toolbarShown) {
			if (toolbar == null) {
				toolbar = createToolbarButtons();	
			} 		
		} else {
			toolbar = null;
		}		
		if (detailShown) {
			if (detailHTML == null){
				//detailHTML = new HTML ("detail - " + "Main");
				detailHTML = createNavAndDetail();
			}
		} else {
			detailHTML = null;
		}
		if (navShown) {
			if (navHTML == null){
				//navHTML = new HTML ("Nav - " + "Main");
				navHTML = createNavAndDetail();
			}
		} else {
			navHTML = null;
		}
		if (gridShown){
			if (grid == null){
				grid = createCentralGrid();
			}
		} else {
			grid = null;
		}
	}
	
	private Grid createCentralGrid(){
		List <ResultsetField> fieldsList = resultSetImproved.getFields();
		colNumber = fieldsList.size();
		Grid grid = new Grid(rowNumber,colNumber );
		int j = 0;
		for (ResultsetField currField :  fieldsList){			
		//for (int j = 0; j < colNumber; j++) {
			String colName = currField.getName();
			boolean colIsNumeric = random.nextBoolean();		
			boolean colIsMoney = random.nextBoolean();		
			for (int i = 0; i < rowNumber; i++) {
				String newRandomString = generateRandomString ( );				
				if (i == 0){
					grid.setHTML(i, j, "<b>" + colName  + "</b>");				
				} else {
					if (colIsNumeric) {
						if (colIsMoney) {
							newRandomString = "" + random.nextInt(20000) + "," + random.nextInt(100) + " E";
						} else {
						newRandomString = "" + random.nextInt(2000000);
						}
					} 
					grid.setText(i, j, newRandomString );					
					grid.setCellSpacing(0);
				}	
			}
			j++;
		}
		grid.setStylePrimaryName("dummyGrid");
//		if (incomingKeysRelativeResultSetId == 0){
//			grid.setStylePrimaryName("mainGrid");
//		}
		return grid ;
	}

	private Grid createCentralGridSilly(){
		Grid grid = new Grid(rowNumber,colNumber );
		for (int j = 0; j < colNumber; j++) {
			boolean colIsNumeric = random.nextBoolean();		
			boolean colIsMoney = random.nextBoolean();		
			for (int i = 0; i < rowNumber; i++) {
				String newRandomString = generateRandomString ( );				
				if (i == 0){
					grid.setHTML(i, j, "<b>" + generateColTitleString()  + "</b>");				
				} else {
					if (colIsNumeric) {
						if (colIsMoney) {
							newRandomString = "" + random.nextInt(20000) + "," + random.nextInt(100) + " E";
						} else {
						newRandomString = "" + random.nextInt(2000000);
						}
					} 
					grid.setText(i, j, newRandomString );					
					grid.setCellSpacing(0);
				}	
			}
		}
		grid.setStylePrimaryName("grid");
//		if (incomingKeysRelativeResultSetId == 0){
//			grid.setStylePrimaryName("mainGrid");
//		}
		return grid ;
	}

	private String generateRandomString (){
		int newRandomStringLenght = poolString.length();
		int start = random.nextInt(newRandomStringLenght);				
		int end = start + Math.min(15, random.nextInt(newRandomStringLenght - start) );
		String newRandomString= ""; 
		if (start < end) {
			newRandomString = poolString.substring(start, end);
		} else {
			newRandomString = poolString.substring(end, start);
		}
		if (newRandomString.length() == 0){
			newRandomString = " - ";
		}
		return newRandomString;
	}

	private String generateColTitleString (){
		String titlePoolString = "Le proprietà fisiche vengono generalmente divise " +
				"in classi di equivalenza, in base alla possibilità di essere confrontate fra di loro, ovvero di essere misurate. " +
				"Spesso si usa l'espressione proprietà fisica per indicare l'intera classe di equivalenza " +
				"corrispondente ad una particolare proprietà di un sistema." +
				" Se la proprietà di un sistema può essere confrontata con quella di un altro e il confronto corrisponde  " ;
		String[] stringTokens = titlePoolString.split("\\s");
		String newToken = "surname";	
	    for (int i=0; i < stringTokens.length; i++){
			int index = random.nextInt(stringTokens.length);
	    	newToken =  stringTokens[index];
			if ((newToken.length()> 4 ) && (newToken.length()< 12  )) {
				break;
			} 
	    }
		return newToken;		
	}

	
	private void updateButtonState(Button button,  boolean isPressed){
		if (isPressed) {
			button.setStylePrimaryName("selectedButton");		
		} else {
			button.setStylePrimaryName("unselectedButton");
		}
	}

	private void updateGridNavAndDetailButtonsState(){
		updateButtonState (gridButton, gridShown);	
		updateButtonState (navButton, navShown);	
		updateButtonState (detailButton, detailShown);	
	}

	
	private FlowPanel createToolbarButtons(){
		FlowPanel fp = new FlowPanel();
		
		gridButton = new Button("Grid");
		gridButton.setStylePrimaryName("unselectedButton");
		gridButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				gridShown = !gridShown;
				createAndPlaceObjects();
				updateGridNavAndDetailButtonsState();
		    }
		  });
		fp.add(gridButton);	
		
		navButton  = new Button("Nav");			
		navButton.setStylePrimaryName("unselectedButton");
		navButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				navShown = !navShown;
				createAndPlaceObjects();
				updateGridNavAndDetailButtonsState();
		    }
		});
		fp.add(navButton);
		
		detailButton = new Button("Detail");
		detailButton.setStylePrimaryName("unselectedButton");
		detailButton.addClickHandler(new ClickHandler() {
		    public void onClick(ClickEvent event) {
				detailShown = !detailShown;
				createAndPlaceObjects();
				updateGridNavAndDetailButtonsState();
		    }
		  });
		fp.add(detailButton);	
		updateGridNavAndDetailButtonsState();
		
		TextBox textBox = new TextBox();
		textBox.setText("Rs - " + this.resultSetImproved.getId());
		textBox.setWidth("60px");
		textBox.setEnabled(false);
		
		fp.add(textBox);
		if (isRootResultSet){
			fp.setStylePrimaryName("mainMultiRsToolbar");
		}
		return fp;
	}	

}
