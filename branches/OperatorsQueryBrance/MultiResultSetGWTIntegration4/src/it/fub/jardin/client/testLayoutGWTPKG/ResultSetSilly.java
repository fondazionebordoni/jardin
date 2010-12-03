package it.fub.jardin.client.testLayoutGWTPKG;

import java.util.ArrayList;

public class ResultSetSilly {
	int id;
	boolean isLarge;	
	boolean isShownAsMainTables = false;
	ArrayList<ResultSetSilly> correlatedResultSetSillyArrayList = new ArrayList<ResultSetSilly>();

	public ResultSetSilly(int id, boolean isLarge) {
		super();
		this.id = id;
		this.isLarge = isLarge;
	}

	public void addCorrelatedResultSet(ResultSetSilly rs){
		if (rs.id != this.id){
			if (!correlatedResultSetListContains(rs)){
				correlatedResultSetSillyArrayList.add(rs);
			}
		}
	}
	
	public boolean correlatedResultSetListContains(ResultSetSilly rs){
		int l = correlatedResultSetSillyArrayList.size();
		for (int i = 0; i < l ; i++) {
			ResultSetSilly currTestResultSetSilly = correlatedResultSetSillyArrayList.get(i);
			if (rs.id == currTestResultSetSilly.id) {				
				return true;
			}
		}
		return false;
	}
	
	public ResultSetSilly getCorrelatedResultSet(int rsId){
		int l = correlatedResultSetSillyArrayList.size();
		for (int i = 0; i < l ; i++) {
			ResultSetSilly currTestResultSetSilly = correlatedResultSetSillyArrayList.get(i);
			if ( currTestResultSetSilly.id == rsId ) {				
				return currTestResultSetSilly;
			}
		}
		return null;
	}
	

}
