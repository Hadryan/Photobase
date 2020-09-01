package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import utils.UtilsMethods;

public class NoIdEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected List<String> columnsName; 
	
	public NoIdEntity() {
		columnsName = new ArrayList<>();
	}
	
	@Override
	public int hashCode() {
		return -1;
	}

	@Override
	public boolean equals(Object obj) {
		return false;
	}
	
	public List<String> columnsName(){
		return this.columnsName;
	}

	public void setValueForColumnName(String columnName, Object value) {
	}
	
	public Object getValueForColumnName(String columnName) {
		return null;
	}
	
}
