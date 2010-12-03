package it.fub.jardin.client.testLayoutGWTPKG;

import java.util.ArrayList;
import java.util.Random;

public class DBSchema {
	int allTablesNumber  = 10;
	ArrayList<ResultSetSilly> mainResultSetSillyArrayList = new ArrayList<ResultSetSilly>();
	Random random = new Random();
	
	public DBSchema() {
		createAllRandomRs();
		createRsRelationsOnAllTables();
	}

	private void createAllRandomRs(){
		for (int i = 0; i <  allTablesNumber; i++) {
			ResultSetSilly rs = createNewCorrelatedResultSetBlind (i+1);
			mainResultSetSillyArrayList.add(rs);			
		}
	}

	private ResultSetSilly createNewCorrelatedResultSetBlind (int incomingKeysRelativeResultSetId){
		boolean isLarge;
		if (random.nextInt(2) == 1) {
			isLarge = true;
		} else {
			isLarge = false;
		}
		ResultSetSilly resultSetSilly = new ResultSetSilly(incomingKeysRelativeResultSetId, isLarge);
		return resultSetSilly;
	}

	private void createRsRelationsOnATable(ResultSetSilly resultSetSilly){
		for (int i = 0; i < allTablesNumber ; i++) {
			ResultSetSilly currTestResultSetSilly = mainResultSetSillyArrayList.get(i);
			if (resultSetSilly.id != currTestResultSetSilly.id) {				
				if (random.nextInt(3) == 1) {
					resultSetSilly.addCorrelatedResultSet(currTestResultSetSilly);
				}
			}
		}
	}


	private void createRsRelationsOnAllTables(){
		for (int i = 0; i < allTablesNumber ; i++) {
			ResultSetSilly currTestResultSetSilly = mainResultSetSillyArrayList.get(i);
			if (random.nextInt(2) == 1) {
				createRsRelationsOnATable(currTestResultSetSilly );
			} 
		}
	}

}
