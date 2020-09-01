package entities;

import utils.UtilsMethods;

public class Users extends BasicEntity{

	private static final long serialVersionUID = 1L;
	
	private String username;
	private String password;
	private String email;
	private String state;
	private String role;
	private boolean banned;
	private boolean deleted;
	private String token;
	
	public static final String USERNAME = "USERNAME";
	public static final String PASSWORD = "PASSWORD";
	public static final String EMAIL = "EMAIL";
	public static final String STATE = "STATE";
	public static final String ROLE = "ROLE";
	public static final String BANNED = "BANNED";
	public static final String DELETED = "DELETED";
	public static final String TOKEN = "TOKEN";
	
	public Users() {
		super();
		this.columnsName.add(USERNAME);
		this.columnsName.add(PASSWORD);
		this.columnsName.add(EMAIL);
		this.columnsName.add(STATE);
		this.columnsName.add(ROLE);
		this.columnsName.add(BANNED);
		this.columnsName.add(DELETED);
		this.columnsName.add(TOKEN);
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
		if (EMAIL.equals(columnName)) {
			setEmail(UtilsMethods.convertToString(value));
			return;
		}
		if (STATE.equals(columnName)) {
			setState(UtilsMethods.convertToString(value));
			return;
		}
		if (ROLE.equals(columnName)) {
			setRole(UtilsMethods.convertToString(value));
			return;
		}
		if (BANNED.equals(columnName)) {
			setBanned(UtilsMethods.convertTinyInt(value));
			return;
		}
		if (DELETED.equals(columnName)) {
			setDeleted(UtilsMethods.convertTinyInt(value));
			return;
		}
		if (TOKEN.equals(columnName)) {
			setToken(UtilsMethods.convertToString(value));
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
		if (EMAIL.equals(columnName)) {
			return getEmail();
		}
		if (STATE.equals(columnName)) {
			return getState();
		}
		if (ROLE.equals(columnName)) {
			return getRole();
		}
		if (BANNED.equals(columnName)) {
			return isBanned();
		}
		if (DELETED.equals(columnName)) {
			return isDeleted();
		}
		if (TOKEN.equals(columnName)) {
			return getToken();
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isBanned() {
		return banned;
	}

	public void setBanned(boolean banned) {
		this.banned = banned;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getToken() {
		return token;
	}

}
