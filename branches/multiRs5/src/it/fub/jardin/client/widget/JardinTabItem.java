package it.fub.jardin.client.widget;

import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.IncomingForeignKeyInformation;
import it.fub.jardin.client.model.ResultsetImproved;
import java.util.ArrayList;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;

public class JardinTabItem extends TabItem {
	private ArrayList<JardinMultiRsSingularCenter> childrenList;
	ResultsetImproved resultset;

	public JardinTabItem(ResultsetImproved resultset) {
		super(resultset.getAlias());
		childrenList = new ArrayList<JardinMultiRsSingularCenter>();
		//System.out.println("no break no");
		this.setClosable(false);
		this.setLayout(new FlowLayout());
		this.resultset = resultset;
		this.createFirstChild(resultset);
	    this.setVisible(true);

		//createAllOtherChildren();
		
	}

	private void createFirstChild(ResultsetImproved resultset) {
		JardinMultiRsSingularCenter firstChild = new JardinMultiRsSingularCenter(
				resultset, this, null);
		//firstChild.collapse();
		childrenList.add(firstChild);
	}

	public void createAllOtherChildren() {
		//((ContentPanel)  (this.getElement().getFirstChild())).collapse();
		//childrenList.get(0).collapse();
		ArrayList<IncomingForeignKeyInformation> foreignKeyBMD = resultset.getForeignKeyIn();
		if (foreignKeyBMD != null) {
			for (IncomingForeignKeyInformation foreignKey : foreignKeyBMD) {
				ResultsetImproved rsLinked_i = foreignKey.getInterestedResultset();
				JardinMultiRsSingularCenter newChild = new JardinMultiRsSingularCenter(
						rsLinked_i, this, foreignKey);
				//newChild.collapse();
				childrenList.add(newChild);				
			}
		}
		this.layout();
//		for (JardinMultiRsSingularCenter currChild : childrenList){
//			currChild.expand();
//		}
//		this.layout();

	}

	public void addDetail(JardinDetail detail) {
		childrenList.get(0).addDetail(detail);
	}

	public void addSearchAreaAdvanced(SearchAreaAdvanced searchAreaAdvanced) {
		childrenList.get(0).addSearchAreaAdvanced(searchAreaAdvanced);
	}

	public void addSearchAreaBase(SearchAreaBase searchAreaBase) {
		childrenList.get(0).addSearchAreaBase(searchAreaBase);
	}

	public JardinGrid getGrid() {
		return childrenList.get(0).getGrid();
	}

	public FormPanel getDetail() {
		return childrenList.get(0).getDetail();
	}

	public JardinGridToolBar getToolbar() {
		return childrenList.get(0).getToolbar();
	}

	public void setGrid(JardinGrid grid) {
		childrenList.get(0).setGrid(grid);
		//createAllOtherChildren();
	}

	public void updatePreference(HeaderPreferenceList data) {
		childrenList.get(0).updatePreference(data);
	}

	public void updateStore(final ListStore<BaseModelData> store) {
		childrenList.get(0).updateStore(store);
	}
}
