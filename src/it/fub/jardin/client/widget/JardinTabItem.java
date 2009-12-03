package it.fub.jardin.client.widget;

import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.IncomingForeignKeyInformation;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;

import java.util.ArrayList;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class JardinTabItem extends TabItem {
	private ArrayList<JardinMultiRsSingularCenter> childrenList;
	ResultsetImproved resultset;
	private static final int MARGIN = 2;
	private ContentPanel otherChildrenRowContentPanel;

	public JardinTabItem(ResultsetImproved resultset) {
		super(resultset.getAlias());
		this.resultset = resultset;
		childrenList = new ArrayList<JardinMultiRsSingularCenter>();
		this.setClosable(false);

		// this.setLayout(new RowLayout(Orientation.HORIZONTAL));
		this.setLayout(new BorderLayout());
		// this.setLayout(new FlowLayout());
		//rowContentPanel.setWidth("100%");

		ArrayList<IncomingForeignKeyInformation> foreignKeyBMD = resultset.getForeignKeyIn();
		int numFigli = foreignKeyBMD.size();

//		double percFirstChild = 1;
//		double percOtherChildren = 0;
//		if (numFigli >= 1 ) {
//			percFirstChild = 50D / 100D ;
//			percOtherChildren = ((50D) / numFigli) / 100D;
//		} 

		// First Child
		JardinMultiRsSingularCenter firstChild = new JardinMultiRsSingularCenter(
				resultset, null);
		//rowContentPanel.add(firstChild, new RowData (1, percFirstChild) );	
		BorderLayoutData borderLayoutData = new BorderLayoutData(LayoutRegion.CENTER);
		borderLayoutData.setSplit(true);
		borderLayoutData.setCollapsible(true);
		borderLayoutData.setMargins(new Margins(MARGIN));

		this.add(firstChild, borderLayoutData);	
	    //this.add(firstChild, new FlowData (MARGIN,MARGIN,MARGIN,MARGIN));	
		childrenList.add(firstChild);
		
		if (numFigli >= 1 ) {
			otherChildrenRowContentPanel = new ContentPanel(new RowLayout(Orientation.VERTICAL));
			otherChildrenRowContentPanel.setHeaderVisible(false);
			otherChildrenRowContentPanel.setScrollMode(Scroll.AUTO);	
			BorderLayoutData borderLayoutData2 = new BorderLayoutData(LayoutRegion.SOUTH);
			borderLayoutData2.setSplit(true);
			borderLayoutData2.setCollapsible(true);
			borderLayoutData2.setMargins(new Margins(MARGIN));
			this.add(otherChildrenRowContentPanel, borderLayoutData2);	
			// otherChild
			for (IncomingForeignKeyInformation foreignKey : foreignKeyBMD) {
				ResultsetImproved rsLinked_i = foreignKey.getInterestedResultset();
				JardinMultiRsSingularCenter newChild = new JardinMultiRsSingularCenter(
						rsLinked_i, foreignKey);
				otherChildrenRowContentPanel.add(newChild , new RowData (1, 300));				
				//rowContentPanel.add(newChild, new RowData (1,-1) );				
				childrenList.add(newChild);
				//this.layout();
			}
		}
		
//		for (JardinMultiRsSingularCenter currChild : childrenList){
//			currChild.expand();
//		}
//		this.layout();
		this.addListener( Events.Select ,
				new Listener<TabPanelEvent>() {
					public void handleEvent(TabPanelEvent tpe) {
						collapseAdvSearchAndDetailArea();
					}
		});
	}

//	public void addDetail(JardinDetail detail, Integer resultsetId) {
//		JardinMultiRsSingularCenter currChild  = findInterestedJardinMultiRsSingularCenter (resultsetId) ;	
//		currChild.addDetail(detail);
//	}

	
//	public void addSearchAreaBase(SearchAreaBase searchAreaBase, Integer resultsetId) {
//		JardinMultiRsSingularCenter currChild  = findInterestedJardinMultiRsSingularCenter (resultsetId) ;	
//		currChild.addSearchAreaBase(searchAreaBase);
//	}

//	public void addSearchAreaAdvanced(SearchAreaAdvanced searchAreaAdvanced, Integer resultsetId) {
//		JardinMultiRsSingularCenter currChild  = findInterestedJardinMultiRsSingularCenter (resultsetId) ;	
//		currChild.addSearchAreaAdvanced(searchAreaAdvanced);
//	}

//	public void setGrid(JardinGrid grid, Integer resultsetId) {
//		JardinMultiRsSingularCenter currChild  = findInterestedJardinMultiRsSingularCenter (resultsetId) ;	
//		currChild.setGrid(grid);
//		//createAllOtherChildren();
//	}

	public void collapseAdvSearchAndDetailArea(){
		for (JardinMultiRsSingularCenter currChild : childrenList){
			currChild.collapseAdvSearchAndDetailArea();									
		}

	}

	public void updatePreference(HeaderPreferenceList data, Integer resultsetId) {
		JardinMultiRsSingularCenter currChild  = findInterestedJardinMultiRsSingularCenter (resultsetId) ;	
		currChild.updatePreference(data);
	}

//	public void updateStore(final ListStore<BaseModelData> store, Integer resultsetId) {
//		JardinMultiRsSingularCenter currChild  = findInterestedJardinMultiRsSingularCenter (resultsetId) ;	
//		currChild.updateStore(store);
//	}
	
//	public JardinGrid getGrid( Integer resultsetId) {
//		///// DA CAMBIARE !!!!!!!!!!!!!!!!!!!!
//		JardinMultiRsSingularCenter currChild  = findInterestedJardinMultiRsSingularCenter (resultsetId) ;	
//		return currChild.getGrid();
//	}
	
	
//	public FormPanel getDetail() {
//		return childrenList.get(0).getDetail();
//	}

//	public JardinGridToolBar getToolbar() {
//		return childrenList.get(0).getToolbar();
//	}

	public JardinGridToolBar getToolbar(int resultsetId) {
		JardinMultiRsSingularCenter currChild  = findInterestedJardinMultiRsSingularCenter (new Integer (resultsetId)) ;	
		return currChild.getToolbar();
	}
	
	public JardinGrid getGridFromResultSetId (int resultSetId) {
		for (JardinMultiRsSingularCenter currChild : childrenList){
			int currentResultsetId = currChild.getResultSetId();			
			if (currentResultsetId == resultSetId) {
				return currChild.getGrid();				
			}			
		}
		return null;
	}
	
//	public JardinMultiRsSingularCenter getJardinMultiRsSingularCenterFromResultSetId (int resultSetId) {
//		for (JardinMultiRsSingularCenter currChild : childrenList){
//			int currentResultsetId = currChild.getResultSetId();			
//			if (currentResultsetId == resultSetId) {
//				return currChild;		
//			}			
//		}
//		return null;
//	}

	public JardinMultiRsSingularCenter findInterestedJardinMultiRsSingularCenter (Integer resultsetId){
		for (JardinMultiRsSingularCenter currChild : childrenList){
			int currentResultsetId = currChild.getResultSetId();			
			if (currentResultsetId == resultsetId.intValue()) {
				return currChild;				
			}			
		}
		return null;
	}
	
	public void setSearchOfOtherChildren (SearchParams searchParams){
		int i=0;
		for (JardinMultiRsSingularCenter currChild : childrenList){
			if (i>0) {
				currChild.setSearchparams(searchParams);
			}
			i++;
		}
	}

	public boolean isFather (JardinMultiRsSingularCenter jardinMultiRsSingularCenter){
		for (JardinMultiRsSingularCenter currChild : childrenList){
			if (currChild == jardinMultiRsSingularCenter){
				return true;
			}
		}			
		return false;
	}
}
