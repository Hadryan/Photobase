package entities;

import utils.UtilsMethods;

public class Admins extends BasicEntity{

	private static final long serialVersionUID = 1L;

	private String username;
	private String password;
	
	public static final String USERNAME = "USERNAME";
	public static final String PASSWORD = "PASSWORD";
	
	public Admins() {
		columnsName.add(USERNAME);
		columnsName.add(PASSWORD);
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
	
}
