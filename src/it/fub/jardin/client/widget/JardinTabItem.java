package it.fub.jardin.client.widget;

import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.IncomingForeignKeyInformation;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;
import java.util.ArrayList;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class JardinTabItem extends TabItem {
	private ArrayList<JardinMultiRsSingularCenter> childrenList;
	ResultsetImproved resultset;
	private static final int MARGIN = 2;
	private ContentPanel otherChildrenContentPanel;
	//private ContentPanel main;

	public JardinTabItem(ResultsetImproved resultset) {
		super(resultset.getAlias());
		this.resultset = resultset;
		childrenList = new ArrayList<JardinMultiRsSingularCenter>();
		this.setAutoHeight(true);
		//this.setAutoWidth(true);
		//BorderLayout borderLayout = new BorderLayout();
		//this.setLayout(new FitLayout());
		//this.setLayout(new BorderLayout());
		//this.setClosable(false);
		//main =  new ContentPanel(new BorderLayout()	);	

		//main.setExpanded(true);
		// main.setWidth(this.getParent().getOffsetWidth() - 5);
		//main.
		//setHeight(this.getParent().getOffsetHeight()- 20);
		setHeight("100%");
		//setHeight(500);
		setScrollMode(Scroll.AUTO);
		// this.setLayout(new RowLayout(Orientation.HORIZONTAL));
		//this.setLayout(new BorderLayout());
		// this.setLayout(new FlowLayout());
		//rowContentPanel.setWidth("100%");
		//this.setScrollMode(Scroll.AUTO);		
		ArrayList<IncomingForeignKeyInformation> foreignKeyBMD = resultset.getForeignKeyIn();
		int numFigli = foreignKeyBMD.size();
		double percFirstChild = 1;
		double percOtherChildren = 0;
		if (numFigli >= 1 ) {
			percFirstChild = 50D / 100D ;
			percOtherChildren = ((50D) / numFigli) / 100D;
		} 
		// First Child
		JardinMultiRsSingularCenter firstChild = new JardinMultiRsSingularCenter(
				resultset, null);
		//firstChild.setHeaderVisible(false);
		firstChild.setHeaderVisible(true);
		firstChild.setHeading(resultset.getAlias());	
		firstChild.setCollapsible(true);
		//firstChild.setAutoHeight(true);
		firstChild.setAutoWidth(true);
		firstChild.setHeight(500);

		//firstChild.setWidth("100%");
		//firstChild.setBorders(true);
		//firstChild.setHeight(500);
		//firstChild.setHeight("100%");
		//rowContentPanel.add(firstChild, new RowData (1, percFirstChild) );	
		BorderLayoutData borderLayoutData = new BorderLayoutData(LayoutRegion.NORTH,300, 100, 400);
		borderLayoutData.setSplit(true);
		//borderLayoutData.setCollapsible(true);
		borderLayoutData.setMargins(new Margins(MARGIN));
//		TextField<String> testTextField = new TextField<String>();
//		testTextField.setEmptyText("testmain\nbla\nblaaasfgasegwe");
		this.add(firstChild, borderLayoutData);
		//this.add(testTextField, borderLayoutData);
		
	    //this.add(firstChild, new FlowData (MARGIN,MARGIN,MARGIN,MARGIN));	
		childrenList.add(firstChild);
		
		//numFigli = 0;
		if (numFigli >= 1 ) {
			TabPanel otherChildrenTabPanel = new TabPanel ();
			otherChildrenTabPanel.setHeight(32);			
			//otherChildrenRowContentPanel = new ContentPanel(new RowLayout(Orientation.VERTICAL));
			otherChildrenContentPanel = new ContentPanel(new ColumnLayout());
			otherChildrenContentPanel.setHeaderVisible(false);
			otherChildrenContentPanel.setScrollMode(Scroll.AUTO);	
			otherChildrenContentPanel.setAutoHeight(true);
			otherChildrenContentPanel.setAutoWidth(true);
			// otherChild
			for (IncomingForeignKeyInformation foreignKey : foreignKeyBMD) {
				ResultsetImproved rsLinked_i = foreignKey.getInterestedResultset();
				JardinMultiRsSingularCenter newChild = new JardinMultiRsSingularCenter(
						rsLinked_i, foreignKey);
				newChild.setHeaderVisible(true);
				String name = foreignKey.getLinkingTable();
				newChild.setHeading(name);
				newChild.setCollapsible(false);
				//newChild.collapse();
				newChild.setBorders(true);
				//newChild.setAutoHeight(true);
				String sizeStr = "" + percOtherChildren + "%";
				//newChild.setHeight(sizeStr);
				newChild.setHeight (400);
				newChild.setWidth(350);
				
				//newChild.setAutoWidth(true);
				
//				TextField<String> testTextFieldi_esimo = new TextField<String>();
//				testTextFieldi_esimo.setEmptyText("testi_esimo chiave ---> " + foreignKey.getLinkingTable() );
				//this.add(firstChild, borderLayoutData);
				//otherChildrenRowContentPanel.add(testTextFieldi_esimo, new RowData (1, 300));
				otherChildrenTabPanel.add(new TabItem(name));

				//otherChildrenContentPanel.add(newChild , new RowData (1, -1));
				otherChildrenContentPanel.add(newChild  );
				
				//rowContentPanel.add(newChild, new RowData (1,-1) );				
				childrenList.add(newChild);
				//this.layout();
			}
			//otherChildrenContentPanel.layout();
			BorderLayoutData borderLayoutData2 = new BorderLayoutData(LayoutRegion.CENTER, 32,32,32);
			borderLayoutData2.setSplit(true);
			//borderLayoutData2.setCollapsible(false);
			//borderLayoutData2.setMargins(new Margins(MARGIN));
			this.add(otherChildrenTabPanel, borderLayoutData2);	

			BorderLayoutData borderLayoutData3 = new BorderLayoutData(LayoutRegion.SOUTH,500,100,500 );
			borderLayoutData3.setSplit(true);
			//borderLayoutData2.setCollapsible(false);
			//borderLayoutData3.setMargins(new Margins(MARGIN));
			this.add(otherChildrenContentPanel, borderLayoutData3);	
			this.layout(); 
		}
		
//		for (JardinMultiRsSingularCenter currChild : childrenList){
//			currChild.expand();
//		}
		//this.layout();
		this.addListener( Events.Select ,
				new Listener<TabPanelEvent>() {
					public void handleEvent(TabPanelEvent tpe) {
						collapseAdvSearchAndDetailArea();
					}
		});
//		BorderLayoutData borderLayoutData3 = new BorderLayoutData(LayoutRegion.CENTER);
//		borderLayoutData3.setSplit(true);
//		borderLayoutData3.setCollapsible(true);
//		borderLayoutData3.setMargins(new Margins(MARGIN));

		//this.adjustSize = true;
		//layout();
		//this.add(main,borderLayoutData3);		
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
