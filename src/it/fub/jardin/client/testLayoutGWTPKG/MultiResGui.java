package it.fub.jardin.client.testLayoutGWTPKG;

import it.fub.jardin.client.model.IncomingForeignKeyInformation;
import it.fub.jardin.client.model.ResultsetImproved;
import java.util.ArrayList;
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
	//ResultSetSilly mainResultSet;
	//ResultsetImproved mainResultSet;
	Random random = new Random();
	
	public MultiResGui(ResultsetImproved mainResultSet){
		super(Unit.EM);	
		//mainResultSet = new ResultSetSilly(0, random.nextBoolean());
		//this.mainResultSet = mainResultSet;
		//createCorrelatedRs();
		mainResultSetGui = new ResultSetGui(mainResultSet,null, true);
		correlatedResultSetButtonsFlowPanel = createMultiRsButtons();
		vertCorrelatedResulSetsArea = new CorrelatedResulSetsArea (false);
		horizCorrelatedResulSetsArea = new CorrelatedResulSetsArea (true);
		this.setStylePrimaryName("MultiResGui");
		updateLayout ();
	}

	public ResultSetGui getMainResultSetGui() {
		return mainResultSetGui;
	}

	public ResultSetGui getResultSetGuiByResultSetIdInCorrelatedResultSets(int resultSetId){
		ResultSetGui rsGui1 = vertCorrelatedResulSetsArea.getResultSetGuiByResultSetId(resultSetId);
		ResultSetGui rsGui2 = horizCorrelatedResulSetsArea.getResultSetGuiByResultSetId(resultSetId);
		
		if (rsGui1 != null ) {
			return rsGui1; 
		} else {
			return rsGui2; 
		}		
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


	
	private void createNewCorrelatedResultSetGui (ResultsetImproved rs){
		//ResultSetSilly rs = mainResultSet.getCorrelatedResultSet(incomingKeysRelativeResultSetId);
		ResultSetGui newResultSetGui = new ResultSetGui(rs ,mainResultSetGui.getResultSetImproved(), false);			
		if (newResultSetGui.isLarge) {
			horizCorrelatedResulSetsArea.insertNonExistentNewResultSetGui(newResultSetGui);
		} else {
			vertCorrelatedResulSetsArea.insertNonExistentNewResultSetGui(newResultSetGui);
		}
		updateLayout();
	}
	
	
	
	private FlowPanel createMultiRsButtons(){
		FlowPanel fp = new FlowPanel();
		ArrayList<IncomingForeignKeyInformation> foreignKeyInArrayList =mainResultSetGui.getResultSetImproved().getForeignKeyIn();
//		int incomingKeysNumber = foreignKeyInArrayList.size();
//		int incomingKeysNumber = mainResultSet.correlatedResultSetSillyArrayList.size();
		for (IncomingForeignKeyInformation incomingForeignKeyInformation : foreignKeyInArrayList) {
			//for (int i = 1; i <  incomingKeysNumber + 1  ; i++) {
			ResultsetImproved resultsetImproved =  incomingForeignKeyInformation.getInterestedResultset();
			//ResultSetSilly rs = mainResultSet.correlatedResultSetSillyArrayList.get(i-1);
//			int resulSetId = resultsetImproved.getId();
			String alias = resultsetImproved.getAlias();
			Button srsB = new Button(alias);			
			srsB.setStylePrimaryName("unselectedButton");
			srsB.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					Object obj = event.getSource();
					Button sourceButton = (Button)obj;
					String buttonName = sourceButton.getText();
					//System.out.println(buttonName);
//					String resultSetIdstring = buttonName.substring(11, buttonName.length());	
					try {
						// int resultSetId = Integer.parseInt(resultSetIdstring) ;	
						ResultsetImproved rs = mainResultSetGui.getResultSetImproved().getIncomingKeyResultsetFromAlias(buttonName);
						int resultSetId = rs.getId();
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
							createNewCorrelatedResultSetGui(rs);	
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
