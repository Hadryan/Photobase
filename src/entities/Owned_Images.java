package entities;

import java.util.ArrayList;
import java.util.List;

import utils.UtilsMethods;

public class Owned_Images extends NoIdEntity{
	
	private int users_id;
	private int images_id;
	private boolean voted;
	
	public static final String Users_ID = "Users_ID";
	public static final String Images_ID = "Images_ID";
	public static final String VOTED = "VOTED";
	
	public Owned_Images() {
		super();
		columnsName.add(Users_ID);
		columnsName.add(Images_ID);
		columnsName.add(VOTED);
	}
	
	public void setValueForColumnName(String columnName, Object value) {
		if (Users_ID.equals(columnName)) {
			setUsers_id(UtilsMethods.convertInt(value));
			return;
		}
		if (Images_ID.equals(columnName)) {
			setImages_id(UtilsMethods.convertInt(value));
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
		if (Images_ID.equals(columnName)) {
			return getImages_id();
		}
		if (VOTED.equals(columnName)) {
			return isVoted();
		}
		return null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + images_id;
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
		Owned_Images other = (Owned_Images) obj;
		if (images_id != other.images_id)
			return false;
		if (users_id != other.users_id)
			return false;
		return true;
	}
	
	public boolean isVoted() {
		return voted;
	}
	
	public void setVoted(boolean voted) {
		this.voted = voted;
	}
	
	public int getUsers_id() {
		return users_id;
	}
	
	public void setUsers_id(int users_id) {
		this.users_id = users_id;
	}
	
	public int getImages_id() {
		return images_id;
	}
	
	public void setImages_id(int images_id) {
		this.images_id = images_id;
	}
	
}
