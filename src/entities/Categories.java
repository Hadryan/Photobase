package entities;

import utils.UtilsMethods;

public class Categories extends BasicEntity{

	private static final long serialVersionUID = 1L;

	private String name;
	
	public static final String NAME = "NAME";
	
	public Categories() {
		columnsName.add(NAME);
	}
	
	@Override
	public void setValueForColumnName(String columnName, Object value) {
		if (NAME.equals(columnName)) {
			setName(UtilsMethods.convertToString(value));
		}
		super.setValueForColumnName(columnName, value);
	}
	
	@Override
	public Object getValueForColumnName(String columnName) {
		if (NAME.equals(columnName)) {
			return getName();
		}
		return super.getValueForColumnName(columnName);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
