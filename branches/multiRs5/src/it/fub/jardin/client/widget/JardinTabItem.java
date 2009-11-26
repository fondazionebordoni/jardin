package it.fub.jardin.client.widget;

import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.IncomingForeignKeyInformation;
import it.fub.jardin.client.model.ResultsetImproved;
import java.util.ArrayList;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class JardinTabItem extends TabItem {
	private ArrayList<JardinMultiRsSingularCenter> childrenList;
	ResultsetImproved resultset;
	private static final int MARGIN = 2;
	private ContentPanel rowContentPanel;

	public JardinTabItem(ResultsetImproved resultset) {
		super(resultset.getAlias());
		this.resultset = resultset;
		childrenList = new ArrayList<JardinMultiRsSingularCenter>();
		this.setClosable(false);

		// this.setLayout(new RowLayout(Orientation.HORIZONTAL));
		this.setLayout(new BorderLayout());
		// this.setLayout(new FlowLayout());
		rowContentPanel = new ContentPanel(new RowLayout(Orientation.VERTICAL));
		rowContentPanel.setHeaderVisible(false);
		this.add(rowContentPanel, new BorderLayoutData(LayoutRegion.CENTER));
		createFirstChild(resultset);
		createAllOtherChildren();
		//this.layout();		
	}

	private void createFirstChild(ResultsetImproved resultset) {
		JardinMultiRsSingularCenter firstChild = new JardinMultiRsSingularCenter(
				resultset, null);
		rowContentPanel.add(firstChild, new RowData (1,300) );	
	    //this.add(firstChild, new BorderLayoutData(LayoutRegion.CENTER));	
	    //this.add(firstChild, new FlowData (MARGIN,MARGIN,MARGIN,MARGIN));	
		childrenList.add(firstChild);
	}

	private void createAllOtherChildren() {
		ArrayList<IncomingForeignKeyInformation> foreignKeyBMD = resultset.getForeignKeyIn();
		if (foreignKeyBMD != null) {
			for (IncomingForeignKeyInformation foreignKey : foreignKeyBMD) {
				ResultsetImproved rsLinked_i = foreignKey.getInterestedResultset();
				JardinMultiRsSingularCenter newChild = new JardinMultiRsSingularCenter(
						rsLinked_i, foreignKey);
				rowContentPanel.add(newChild , new RowData (1,300));				
				//rowContentPanel.add(newChild, new RowData (1,-1) );				
				childrenList.add(newChild);
				//this.layout();
			}
		}
		
//		for (JardinMultiRsSingularCenter currChild : childrenList){
//			currChild.expand();
//		}
//		this.layout();
	}

	public void addDetail(JardinDetail detail, Integer resultsetId) {
		JardinMultiRsSingularCenter currChild  = findInterestedJardinMultiRsSingularCenter (resultsetId) ;	
		currChild.addDetail(detail);
	}

	private JardinMultiRsSingularCenter findInterestedJardinMultiRsSingularCenter (Integer resultsetId){
		for (JardinMultiRsSingularCenter currChild : childrenList){
			int currentResultsetId = currChild.getResultSetId();			
			if (currentResultsetId == resultsetId.intValue()) {
				return currChild;				
			}			
		}
		return null;
	}
	
	public void addSearchAreaBase(SearchAreaBase searchAreaBase, Integer resultsetId) {
		JardinMultiRsSingularCenter currChild  = findInterestedJardinMultiRsSingularCenter (resultsetId) ;	
		currChild.addSearchAreaBase(searchAreaBase);
	}

	public void addSearchAreaAdvanced(SearchAreaAdvanced searchAreaAdvanced, Integer resultsetId) {
		JardinMultiRsSingularCenter currChild  = findInterestedJardinMultiRsSingularCenter (resultsetId) ;	
		currChild.addSearchAreaAdvanced(searchAreaAdvanced);
	}

	public void setGrid(JardinGrid grid, Integer resultsetId) {
		JardinMultiRsSingularCenter currChild  = findInterestedJardinMultiRsSingularCenter (resultsetId) ;	
		currChild.setGrid(grid);
		//createAllOtherChildren();
	}


	public void updatePreference(HeaderPreferenceList data, Integer resultsetId) {
		JardinMultiRsSingularCenter currChild  = findInterestedJardinMultiRsSingularCenter (resultsetId) ;	
		currChild.updatePreference(data);
	}

	public void updateStore(final ListStore<BaseModelData> store, Integer resultsetId) {
		JardinMultiRsSingularCenter currChild  = findInterestedJardinMultiRsSingularCenter (resultsetId) ;	
		currChild.updateStore(store);
	}
	
	public JardinGrid getGrid( Integer resultsetId) {
		///// DA CAMBIARE !!!!!!!!!!!!!!!!!!!!
		JardinMultiRsSingularCenter currChild  = findInterestedJardinMultiRsSingularCenter (resultsetId) ;	
		return currChild.getGrid();
	}
	
	
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
	
	public JardinMultiRsSingularCenter getJardinMultiRsSingularCenterFromResultSetId (int resultSetId) {
		for (JardinMultiRsSingularCenter currChild : childrenList){
			int currentResultsetId = currChild.getResultSetId();			
			if (currentResultsetId == resultSetId) {
				return currChild;		
			}			
		}
		return null;
	}

	
}
