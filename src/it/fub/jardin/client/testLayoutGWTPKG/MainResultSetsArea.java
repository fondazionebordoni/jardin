package it.fub.jardin.client.testLayoutGWTPKG;

import java.util.ArrayList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

public class MainResultSetsArea extends DockLayoutPanel {
	ArrayList<MultiResGui> resultSetGuiArray = new ArrayList<MultiResGui>();
	ArrayList<SplitLayoutPanel> resultSetGuiSplitLayoutPanelArray = new ArrayList<SplitLayoutPanel>();
	boolean likeRowLayout = true;
	
	public MainResultSetsArea(boolean likeRowLayout){
		super(Unit.EM);
		this.likeRowLayout=likeRowLayout;
		updateGUI();
	}

	void updateGUI(){
		removeAllObjects();
		placeObjects ();
	}

	boolean isShownResultSetbyIncomingKeysRelativeResultSetId(int resultSetId){
		for (int i = 0 ; i < resultSetGuiArray.size(); i++) {
			MultiResGui currResultSetGui = resultSetGuiArray.get(i);		
			if (currResultSetGui.mainResultSet.id  == resultSetId) {
				return true;
			} 
		}
		return false;
	}

	
	void removeResultSetbyIncomingKeysRelativeResultSetId(int resultSetId){
		removeAllObjects();;
		int l = resultSetGuiArray.size();
		for (int i = 0 ; i < l ; i++) {
			MultiResGui currResultSetGui = resultSetGuiArray.get(i);		
			if (currResultSetGui.mainResultSet.id == resultSetId) {
				currResultSetGui.removeFromParent();
				resultSetGuiArray.remove(i);
				break;
			} 
		}
		updateGUI();
	}
	
	public boolean isEmpty (){
		return (resultSetGuiArray.size() == 0);
	}
	
	private void removeAllObjects(){
		for (MultiResGui currRes : resultSetGuiArray) {
			currRes.removeFromParent();
		}	
		int l = resultSetGuiSplitLayoutPanelArray.size();
		for (int i = l - 1; i >= 0 ; i--) {
			resultSetGuiSplitLayoutPanelArray.get(i).removeFromParent();
			resultSetGuiSplitLayoutPanelArray.remove(i);
		}
	}
		
	void insertNonExistentNewResultSetGui (MultiResGui newResultSetGui){
		removeAllObjects();
		boolean inserted = false;
		if (resultSetGuiArray.size()== 0){
			resultSetGuiArray.add(newResultSetGui);
		} else {
			ArrayList<MultiResGui> resultSetGuiArrayNew = new ArrayList<MultiResGui>();
			for (int i = 0 ; i < resultSetGuiArray.size(); i++) {
				MultiResGui currResultSetGui = resultSetGuiArray.get(i);		
				if (currResultSetGui.mainResultSet.id < newResultSetGui.mainResultSet.id) {
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
			resultSetGuiArray = resultSetGuiArrayNew;
		}
		updateGUI();
	}


	private void placeObjects(){	
		if (resultSetGuiArray.size()>0)  {
			if (resultSetGuiArray.size() == 1)  {
				MultiResGui resultSetGui = resultSetGuiArray.get(0);			
				add(resultSetGui);
			} else {  
				if (resultSetGuiArray.size()==2){
					MultiResGui prevResultSetGui = resultSetGuiArray.get(0);
					MultiResGui lastResultSetGui = resultSetGuiArray.get(1);
					SplitLayoutPanel lastSplitLayoutPanel = new SplitLayoutPanel();
					if (likeRowLayout){
						lastSplitLayoutPanel.addNorth(prevResultSetGui,300);
					} else {
						lastSplitLayoutPanel.addWest(prevResultSetGui,200); 								
					}
					lastSplitLayoutPanel.add(lastResultSetGui);
					resultSetGuiSplitLayoutPanelArray.add(lastSplitLayoutPanel);
					add(lastSplitLayoutPanel);
				} else {
					try { 
						SplitLayoutPanel nextSplitLayoutPanel = null;
						SplitLayoutPanel currSplitLayoutPanel = null;
						for (int i = resultSetGuiArray.size()-2; i >= 0 ; i--) {
							MultiResGui currResultSetGui = resultSetGuiArray.get(i);								
							currSplitLayoutPanel = new SplitLayoutPanel();
							if (likeRowLayout){
								currSplitLayoutPanel.addNorth(currResultSetGui,300);
 							} else {
								currSplitLayoutPanel.addWest(currResultSetGui,200); 								
 							}
							if (i == resultSetGuiArray.size()-2){
								MultiResGui lastResultSetGui = resultSetGuiArray.get(i+1);
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
							resultSetGuiSplitLayoutPanelArray.add(currSplitLayoutPanel);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}	
}
