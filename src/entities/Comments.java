package entities;

import java.sql.Timestamp;

import utils.UtilsMethods;

public class Comments extends BasicEntity {

	private static final long serialVersionUID = 1L;
	
	private int users_id;
	private int authors_users_id;
	private String text;
	private Timestamp date;

	public static final String Users_ID = "Users_ID";
	public static final String Authors_Users_ID = "Authors_Users_ID";
	public static final String TEXT = "TEXT";
	public static final String DATE = "DATE";

	public Comments() {
		super();
		columnsName.add(Users_ID);
		columnsName.add(Authors_Users_ID);
		columnsName.add(TEXT);
		columnsName.add(DATE);
	}

	public void setValueForColumnName(String columnName, Object value) {
		if (Users_ID.equals(columnName)) {
			setUsers_id(UtilsMethods.convertInt(value));
			return;
		}
		if (Authors_Users_ID.equals(columnName)) {
			setAuthors_users_id(UtilsMethods.convertInt(value));
			return;
		}
		if (TEXT.equals(columnName)) {
			setText(UtilsMethods.convertToString(value));
			return;
		}
		if (DATE.equals(columnName)) {
			setDate((Timestamp)value);
			return;
		}
	}

	public Object getValueForColumnName(String columnName) {
		if (Users_ID.equals(columnName)) {
			return getUsers_id();
		}
		if (Authors_Users_ID.equals(columnName)) {
			return getAuthors_users_id();
		}
		if (TEXT.equals(columnName)) {
			return getText();
		}
		if (DATE.equals(columnName)) {
			return getDate();
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + authors_users_id;
		result = prime * result + users_id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Comments other = (Comments) obj;
		if (authors_users_id != other.authors_users_id)
			return false;
		if (users_id != other.users_id)
			return false;
		return true;
	}

	public int getUsers_id() {
		return users_id;
	}

	public void setUsers_id(int users_id) {
		this.users_id = users_id;
	}

	public int getAuthors_users_id() {
		return authors_users_id;
	}

	public void setAuthors_users_id(int authors_users_id) {
		this.authors_users_id = authors_users_id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

}
