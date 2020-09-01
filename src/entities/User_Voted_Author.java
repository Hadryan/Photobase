package entities;

import utils.UtilsMethods;

public class User_Voted_Author extends NoIdEntity{

	private static final long serialVersionUID = 1L;
	
	private int users_id;
	private int authors_users_id;
	boolean voted;
	
	public static final String Users_ID = "Users_ID";
	public static final String Authors_Users_ID = "Authors_Users_ID";
	public static final String VOTED = "VOTED";
	
	public User_Voted_Author() {
		super();
		columnsName.add(Users_ID);
		columnsName.add(Authors_Users_ID);
		columnsName.add(VOTED);
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
		if (VOTED.equals(columnName)) {
			setVoted(UtilsMethods.convertTinyInt(value));
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
		if (VOTED.equals(columnName)) {
			return isVoted();
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + authors_users_id;
		result = prime * result + users_id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		User_Voted_Author other = (User_Voted_Author) obj;
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

	public boolean isVoted() {
		return voted;
	}

	public void setVoted(boolean voted) {
		this.voted = voted;
	}

}
