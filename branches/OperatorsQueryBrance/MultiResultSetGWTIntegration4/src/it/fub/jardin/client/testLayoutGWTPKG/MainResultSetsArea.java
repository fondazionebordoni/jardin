package it.fub.jardin.client.testLayoutGWTPKG;

import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.SearchResult;

import java.util.ArrayList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

public class MainResultSetsArea extends DockLayoutPanel {
	ArrayList<MultiResGui> multiResultSetGuiArray = new ArrayList<MultiResGui>();
	ArrayList<SplitLayoutPanel> multiResultSetGuiSplitLayoutPanelArray = new ArrayList<SplitLayoutPanel>();
	boolean likeRowLayout = true;
	
	public MainResultSetsArea(boolean likeRowLayout){
		super(Unit.EM);
		this.likeRowLayout=likeRowLayout;
		updateGUI();
	}

	public void updateGUI(){
		removeAllObjects();
		placeObjects ();
	}

	public boolean isShownResultSetbyIncomingKeysRelativeResultSetId(int resultSetId){
		for (int i = 0 ; i < multiResultSetGuiArray.size(); i++) {
			MultiResGui currResultSetGui = multiResultSetGuiArray.get(i);		
			if (currResultSetGui.getMainResultSetGui().getResultSetId()  == resultSetId) {
				return true;
			} 
		}
		return false;
	}

	public void updateStore(SearchResult result){
		ResultSetGui rsGui = getResultSetGui(new RsIdAndParentRsId(result.getResultSetId(), result.getParentResultSetId()));
		rsGui.updateStore(result);		
		updateGUI();		
	}

	public void setSearchParams(SearchParams searchParams){
		ResultSetGui rsGui = getResultSetGui(new RsIdAndParentRsId(searchParams.getResultSetId(), searchParams.getParentResultSetId()));
		rsGui.setSearchParams(searchParams );
		updateGUI();		
	}

	private MultiResGui getMultiResultSetGuiByMainResultSetId( int parentResultSetId ){
		int l = multiResultSetGuiArray.size();
		for (int i = 0 ; i < l ; i++) {
			MultiResGui currMultiResultSetGui = multiResultSetGuiArray.get(i);		
			if (currMultiResultSetGui.getMainResultSetGui().getResultSetId() == parentResultSetId) {
				return currMultiResultSetGui;
			} 
		}
		return null;
	}

	private ResultSetGui getResultSetGuiByMainResultSetId( int parentResultSetId ){
		int l = multiResultSetGuiArray.size();
		for (int i = 0 ; i < l ; i++) {
			MultiResGui currMultiResultSetGui = multiResultSetGuiArray.get(i);		
			if (currMultiResultSetGui.getMainResultSetGui().getResultSetId() == parentResultSetId) {
				return currMultiResultSetGui.getMainResultSetGui();
			} 
		}
		return null;
	}

	public ResultSetGui getResultSetGui (int resultSetId){
			ResultSetGui rs1 = getResultSetGuiByMainResultSetId( resultSetId );
			return rs1;
	}
	
	public ResultSetGui getResultSetGui(RsIdAndParentRsId rsIds){
		if (rsIds.getParentResultSetId() == 0 ) {
			ResultSetGui rsGui = getResultSetGui (rsIds.getResultSetId());
			return rsGui;
		} else {
			MultiResGui multiResultSetGui = getMultiResultSetGuiByMainResultSetId( rsIds.getParentResultSetId() );		
			int resultSetId = rsIds.getResultSetId();
			ResultSetGui rsGui = multiResultSetGui.getResultSetGuiByResultSetIdInCorrelatedResultSets(resultSetId);
			return	rsGui;	
		}
	}

	
	
	
	public void removeResultSetbyIncomingKeysRelativeResultSetId(int resultSetId){
		removeAllObjects();;
		int l = multiResultSetGuiArray.size();
		for (int i = 0 ; i < l ; i++) {
			MultiResGui currResultSetGui = multiResultSetGuiArray.get(i);		
			if (currResultSetGui.getMainResultSetGui().getResultSetId() == resultSetId) {
				currResultSetGui.removeFromParent();
				multiResultSetGuiArray.remove(i);
				break;
			} 
		}
		updateGUI();
	}
	
	public boolean isEmpty (){
		return (multiResultSetGuiArray.size() == 0);
	}
	
	private void removeAllObjects(){
		for (MultiResGui currRes : multiResultSetGuiArray) {
			currRes.removeFromParent();
		}	
		int l = multiResultSetGuiSplitLayoutPanelArray.size();
		for (int i = l - 1; i >= 0 ; i--) {
			multiResultSetGuiSplitLayoutPanelArray.get(i).removeFromParent();
			multiResultSetGuiSplitLayoutPanelArray.remove(i);
		}
	}
		
	public void insertNonExistentNewResultSetGui (MultiResGui newResultSetGui){
		removeAllObjects();
		boolean inserted = false;
		if (multiResultSetGuiArray.size()== 0){
			multiResultSetGuiArray.add(newResultSetGui);
		} else {
			ArrayList<MultiResGui> resultSetGuiArrayNew = new ArrayList<MultiResGui>();
			for (int i = 0 ; i < multiResultSetGuiArray.size(); i++) {
				MultiResGui currResultSetGui = multiResultSetGuiArray.get(i);		
				if (currResultSetGui.getMainResultSetGui().getResultSetId() < newResultSetGui.getMainResultSetGui().getResultSetId()) {
					resultSetGuiArrayNew.add(currResultSetGui);
				} else {
					if (inserted == false){
						resultSetGuiArrayNew.add(newResultSetGui);
						inserted = true;
					}
					resultSetGuiArrayNew.add(currResultSetGui);
				}
			}
			if (inserted == false){
				resultSetGuiArrayNew.add(newResultSetGui);
				inserted = true;
			}
			multiResultSetGuiArray = resultSetGuiArrayNew;
		}
		updateGUI();
	}


	private void placeObjects(){	
		if (multiResultSetGuiArray.size()>0)  {
			if (multiResultSetGuiArray.size() == 1)  {
				MultiResGui resultSetGui = multiResultSetGuiArray.get(0);			
				add(resultSetGui);
			} else {  
				if (multiResultSetGuiArray.size()==2){
					MultiResGui prevResultSetGui = multiResultSetGuiArray.get(0);
					MultiResGui lastResultSetGui = multiResultSetGuiArray.get(1);
					SplitLayoutPanel lastSplitLayoutPanel = new SplitLayoutPanel();
					if (likeRowLayout){
						lastSplitLayoutPanel.addNorth(prevResultSetGui,400);
					} else {
						lastSplitLayoutPanel.addWest(prevResultSetGui,500); 								
					}
					lastSplitLayoutPanel.add(lastResultSetGui);
					multiResultSetGuiSplitLayoutPanelArray.add(lastSplitLayoutPanel);
					add(lastSplitLayoutPanel);
				} else {
					try { 
						SplitLayoutPanel nextSplitLayoutPanel = null;
						SplitLayoutPanel currSplitLayoutPanel = null;
						for (int i = multiResultSetGuiArray.size()-2; i >= 0 ; i--) {
							MultiResGui currResultSetGui = multiResultSetGuiArray.get(i);								
							currSplitLayoutPanel = new SplitLayoutPanel();
							if (likeRowLayout){
								currSplitLayoutPanel.addNorth(currResultSetGui,400);
 							} else {
								currSplitLayoutPanel.addWest(currResultSetGui,500); 								
 							}
							if (i == multiResultSetGuiArray.size()-2){
								MultiResGui lastResultSetGui = multiResultSetGuiArray.get(i+1);
								currSplitLayoutPanel.add (lastResultSetGui);
							} else { 							
								if (nextSplitLayoutPanel!= null){
									currSplitLayoutPanel.add(nextSplitLayoutPanel);
								}
							} 	
							if (i == 0){
								add(currSplitLayoutPanel);
							}
							nextSplitLayoutPanel = currSplitLayoutPanel;
							multiResultSetGuiSplitLayoutPanelArray.add(currSplitLayoutPanel);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}	
}
