package it.fub.jardin.client.testLayoutGWTPKG;

import java.util.Random;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

public class MultiResGui extends DockLayoutPanel{
	ResultSetGui mainResultSetGui;
	//DockLayoutPanel dlpMain;
	boolean multiRsShown = false; 
	FlowPanel correlatedResultSetButtonsFlowPanel;
	CorrelatedResulSetsArea vertCorrelatedResulSetsArea;
	CorrelatedResulSetsArea horizCorrelatedResulSetsArea;
	SplitLayoutPanel correlatedResulSetsAreaSplitPanel;
	SplitLayoutPanel mainMainSplitLayoutPanel;
	DockLayoutPanel mainMainDockLayoutPanel;
	SplitLayoutPanel mainMainSplitLayoutPanel2;
	boolean putBottom = false;
	ResultSetSilly mainResultSet;		
	Random random = new Random();
	
	public MultiResGui(ResultSetSilly mainResultSet){
		super(Unit.EM);	
		//mainResultSet = new ResultSetSilly(0, random.nextBoolean());
		this.mainResultSet = mainResultSet;
		//createCorrelatedRs();
		mainResultSetGui = new ResultSetGui(mainResultSet);
		correlatedResultSetButtonsFlowPanel = createMultiRsButtons();
		vertCorrelatedResulSetsArea = new CorrelatedResulSetsArea (false);
		horizCorrelatedResulSetsArea = new CorrelatedResulSetsArea (true);
		this.setStylePrimaryName("MultiResGui");
		updateLayout ();
	}

	

	private void placeObjectsMultiRsButtoBarBottom(){			
		addSouth(correlatedResultSetButtonsFlowPanel,2);					
		if ( (horizCorrelatedResulSetsArea.isEmpty() ) && 
				 (vertCorrelatedResulSetsArea.isEmpty() )      ){
				add(mainResultSetGui);
		} else {
//			if (!correlatedResultSetSillyArrayList.get(0).isLarge){
//			if (!mainResultSet.isLarge){
				mainMainSplitLayoutPanel = new SplitLayoutPanel ();		
				if ( 	(!horizCorrelatedResulSetsArea.isEmpty() ) && 
						(vertCorrelatedResulSetsArea.isEmpty() )      ) {								
					mainMainSplitLayoutPanel.addSouth(horizCorrelatedResulSetsArea,200);
				} else {
					if ( 	(horizCorrelatedResulSetsArea.isEmpty() ) && 
							(!vertCorrelatedResulSetsArea.isEmpty() )      ){
						mainMainSplitLayoutPanel.addSouth(vertCorrelatedResulSetsArea,400 );
					} else {
						correlatedResulSetsAreaSplitPanel =  new SplitLayoutPanel();
						correlatedResulSetsAreaSplitPanel.addWest(horizCorrelatedResulSetsArea, 400);
						correlatedResulSetsAreaSplitPanel.add(vertCorrelatedResulSetsArea);						
						mainMainSplitLayoutPanel.addSouth(correlatedResulSetsAreaSplitPanel,400);
					}
				}
				mainMainSplitLayoutPanel.add(mainResultSetGui);
				add (mainMainSplitLayoutPanel);			
//			} else {	//  resultSetSillyArrayList.get(0).isLarge
//				mainMainSplitLayoutPanel = new SplitLayoutPanel ();		
//				if ( 	(!horizCorrelatedResulSetsArea.isEmpty() ) && 
//						(vertCorrelatedResulSetsArea.isEmpty() )      ) {								
//					mainMainSplitLayoutPanel.addSouth(horizCorrelatedResulSetsArea,200);
//				} else {
//					if ( 	(horizCorrelatedResulSetsArea.isEmpty() ) && 
//							(!vertCorrelatedResulSetsArea.isEmpty() )      ){
//						mainMainSplitLayoutPanel.addSouth(vertCorrelatedResulSetsArea,400 );
//					} else {
//						mainMainSplitLayoutPanel2 = new SplitLayoutPanel ();
//						mainMainSplitLayoutPanel2.addWest(horizCorrelatedResulSetsArea,400);
//						mainMainSplitLayoutPanel2.add(vertCorrelatedResulSetsArea );
//						mainMainSplitLayoutPanel.addSouth(mainMainSplitLayoutPanel2,400);
//					}
//				}
//				mainMainSplitLayoutPanel.add(mainResultSetGui);
//				add (mainMainSplitLayoutPanel);
//			}
		}
	}	
	
	
	private void updateLayout(){
		removeAllObjects();
		placeObjectsMultiRsButtoBarBottom();
	}

	private void removeAllObjects() {
		if (mainResultSetGui!=null)
			mainResultSetGui.removeFromParent();
		if (correlatedResultSetButtonsFlowPanel!=null)
			correlatedResultSetButtonsFlowPanel.removeFromParent();		
		if (horizCorrelatedResulSetsArea!=null)
			horizCorrelatedResulSetsArea.removeFromParent();
		if (vertCorrelatedResulSetsArea!=null)
			vertCorrelatedResulSetsArea.removeFromParent();
		if (correlatedResulSetsAreaSplitPanel!=null)
			correlatedResulSetsAreaSplitPanel.removeFromParent();
		if (mainMainSplitLayoutPanel2!=null)
			mainMainSplitLayoutPanel2.removeFromParent();
		if (mainMainSplitLayoutPanel!=null)
			mainMainSplitLayoutPanel.removeFromParent();
		if (mainMainDockLayoutPanel!=null)
			mainMainDockLayoutPanel.removeFromParent();
	}


	
	private void createNewCorrelatedResultSetGui (int incomingKeysRelativeResultSetId){
		ResultSetSilly rs = mainResultSet.getCorrelatedResultSet(incomingKeysRelativeResultSetId);
		ResultSetGui newResultSetGui = new ResultSetGui(rs );			
		if (rs.isLarge) {
			horizCorrelatedResulSetsArea.insertNonExistentNewResultSetGui(newResultSetGui);
		} else {
			vertCorrelatedResulSetsArea.insertNonExistentNewResultSetGui(newResultSetGui);
		}
		updateLayout();
	}
	
	
	
	private FlowPanel createMultiRsButtons(){
		FlowPanel fp = new FlowPanel();
		int incomingKeysNumber = mainResultSet.correlatedResultSetSillyArrayList.size();
		for (int i = 1; i <  incomingKeysNumber + 1  ; i++) {
			ResultSetSilly rs = mainResultSet.correlatedResultSetSillyArrayList.get(i-1);
			int resulSetId = rs.id;
			Button srsB = new Button("sottoRes - " + resulSetId);			
			srsB.setStylePrimaryName("unselectedButton");
			srsB.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					Object obj = event.getSource();
					Button sourceButton = (Button)obj;
					String buttonName = sourceButton.getText();
					//System.out.println(buttonName);
					String resultSetIdstring = buttonName.substring(11, buttonName.length());	
					try {
						int resultSetId = Integer.parseInt(resultSetIdstring) ;	
						
						boolean removed = false;
						if ( horizCorrelatedResulSetsArea.isShownResultSetbyIncomingKeysRelativeResultSetId(resultSetId) ) {
							horizCorrelatedResulSetsArea.removeResultSetbyIncomingKeysRelativeResultSetId(resultSetId);
							removed = true;
						} 
						if ( vertCorrelatedResulSetsArea.isShownResultSetbyIncomingKeysRelativeResultSetId(resultSetId) ) {
							vertCorrelatedResulSetsArea.removeResultSetbyIncomingKeysRelativeResultSetId(resultSetId);
							removed = true;
						} 						
						if ( !removed) {
							createNewCorrelatedResultSetGui(resultSetId);	
							sourceButton.setStylePrimaryName("selectedButton");
						} else {
							sourceButton.setStylePrimaryName("unselectedButton");
						}
						updateLayout ();
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}					
				}
			  });
		fp.add(srsB);
		}
		return fp;
	}

}
