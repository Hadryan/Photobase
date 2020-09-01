package entities;

import utils.UtilsMethods;

public class Operators extends BasicEntity{

	private static final long serialVersionUID = 1L;

	private String username;
	private String password;
	private boolean neww;
	private int firmId;
	
	public static final String USERNAME = "USERNAME";
	public static final String PASSWORD = "PASSWORD";
	public static final String NEW = "NEW";
	public static final String Firm_ID = "Firm_ID";
	
	public Operators() {
		super();
		this.columnsName.add(USERNAME);
		this.columnsName.add(PASSWORD);
		this.columnsName.add(NEW);
		this.columnsName.add(Firm_ID);
	}
	
	@Override
	public void setValueForColumnName(String columnName, Object value) {
		if (USERNAME.equals(columnName)) {
			setUsername(UtilsMethods.convertToString(value));
			return;
		}
		if (PASSWORD.equals(columnName)) {
			setPassword(UtilsMethods.convertToString(value));
			return;
		}
		if (NEW.equals(columnName)) {
			setNeww(UtilsMethods.convertTinyInt(value));
			return;
		}
		if (Firm_ID.equals(columnName)) {
			setFirmId(UtilsMethods.convertInt(value));
			return;
		}
		super.setValueForColumnName(columnName, value);
	}
	
	@Override
	public Object getValueForColumnName(String columnName) {
		if (USERNAME.equals(columnName)) {
			return getUsername();
		}
		if (PASSWORD.equals(columnName)) {
			return getPassword();
		}
		if (NEW.equals(columnName)) {
			return isNeww();
		}
		if (Firm_ID.equals(columnName)) {
			return getFirmId();
		}
		return super.getValueForColumnName(columnName);
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isNeww() {
		return neww;
	}
	public void setNeww(boolean neww) {
		this.neww = neww;
	}
	public int getFirmId() {
		return firmId;
	}
	public void setFirmId(int firmId) {
		this.firmId = firmId;
	}
	
}
