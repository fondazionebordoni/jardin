package it.fub.jardin.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

//import java.util.List;
//import java.util.ArrayList;;

public class SearchOperator implements IsSerializable {

	//private List<String>  valueOperator;
	private String key;
	private String value;
	private String operator;
	private String search;
	public SearchOperator(String key, String value, String search){
		this.key=key;
		this.value = value;
		this.search = search;
	//	this.valueOperator = new ArrayList();
		operator();	
	}
//inserisce il valore e lìoperatore nella lista
	//private void setList(String value, String operator){
	//	valueOperator.add(value);
	//	valueOperator.add(operator);
	//}
	//public List getList(){
	//return valueOperator;
	//}
	public String getOperator(){
		return operator;
	}
	public String getValue(){
		return value;
	}
	private void operator()
	{
        int lenKey = key.length();
        int indexKey = search.indexOf(key);
        System.out.println("lunghezza della chiave" + lenKey);
        System.out.println("lunghezza della dell'indice della chiave " + indexKey);
        int lenValue = value.length();
        int indexValue = search.indexOf(value);
        System.out.println("lunghezza della del valore" + lenValue);
        System.out.println("lunghezza della dell'indice del valore " + indexValue);
        String operator = search.substring(lenKey - indexKey, indexValue);
        System.out.println("operatore " + operator);
        //setList(value,operator);
        this.operator = operator;
	
	}
}
