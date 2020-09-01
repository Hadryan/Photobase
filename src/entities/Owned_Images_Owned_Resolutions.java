package entities;

import java.sql.Timestamp;
import java.util.Date;

import utils.UtilsMethods;

public class Owned_Images_Owned_Resolutions extends NoIdEntity{
	
	private int owned_images_users_id;
	private int owned_images_images_id;
	private int image_resolutions_id;
	private Timestamp transaction_date;
	
	public static final String Owned_Images_Users_ID = "Owned_Images_Users_ID";
	public static final String Owned_Images_Images_ID = "Owned_Images_Images_ID";
	public static final String Image_Resolutions_ID = "Image_Resolutions_ID";
	public static final String TRANSACTION_DATE = "TRANSACTION_DATE";
	
	public Owned_Images_Owned_Resolutions() {
		super();
		columnsName.add(Owned_Images_Users_ID);
		columnsName.add(Owned_Images_Images_ID);
		columnsName.add(Image_Resolutions_ID);
		columnsName.add(TRANSACTION_DATE);
	}
	
	public void setValueForColumnName(String columnName, Object value) {
		if (Owned_Images_Users_ID.equals(columnName)) {
			setOwned_images_users_id(UtilsMethods.convertInt(value));
		}
		if (Owned_Images_Images_ID.equals(columnName)) {
			setOwned_images_images_id(UtilsMethods.convertInt(value));
		}
		if (Image_Resolutions_ID.equals(columnName)) {
			setImage_resolutions_id(UtilsMethods.convertInt(value));
		}
		if (TRANSACTION_DATE.equals(columnName)) {
			setTransaction_date((Timestamp)value);
		}
	}
	
	public Object getValueForColumnName(String columnName) {
		if (Owned_Images_Users_ID.equals(columnName)) {
			return getOwned_images_users_id();
		}
		if (Owned_Images_Images_ID.equals(columnName)) {
			return getOwned_images_images_id();
		}
		if (Image_Resolutions_ID.equals(columnName)) {
			return getImage_resolutions_id();
		}
		if (TRANSACTION_DATE.equals(columnName)) {
			return getTransaction_date();
		}
		return null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + image_resolutions_id;
		result = prime * result + owned_images_images_id;
		result = prime * result + owned_images_users_id;
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
		Owned_Images_Owned_Resolutions other = (Owned_Images_Owned_Resolutions) obj;
		if (image_resolutions_id != other.image_resolutions_id)
			return false;
		if (owned_images_images_id != other.owned_images_images_id)
			return false;
		if (owned_images_users_id != other.owned_images_users_id)
			return false;
		return true;
	}

	public int getOwned_images_users_id() {
		return owned_images_users_id;
	}

	public void setOwned_images_users_id(int owned_images_users_id) {
		this.owned_images_users_id = owned_images_users_id;
	}

	public int getOwned_images_images_id() {
		return owned_images_images_id;
	}

	public void setOwned_images_images_id(int owned_images_images_id) {
		this.owned_images_images_id = owned_images_images_id;
	}

	public int getImage_resolutions_id() {
		return image_resolutions_id;
	}

	public void setImage_resolutions_id(int image_resolutions_id) {
		this.image_resolutions_id = image_resolutions_id;
	}

	public Timestamp getTransaction_date() {
		return transaction_date;
	}

	public void setTransaction_date(Timestamp transaction_date) {
		this.transaction_date = transaction_date;
	}

}
