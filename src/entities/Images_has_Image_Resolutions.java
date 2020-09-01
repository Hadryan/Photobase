package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import utils.UtilsMethods;

public class Images_has_Image_Resolutions extends NoIdEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int images_id;
	private int image_resolutions_id;
	private double price;
	
	public static final String Images_ID = "Images_ID";
	public static final String Image_Resolutions_ID = "Image_Resolutions_ID";
	public static final String PRICE = "PRICE";
	
	public Images_has_Image_Resolutions() {
		super();
		columnsName.add(Images_ID);
		columnsName.add(Image_Resolutions_ID);
		columnsName.add(PRICE);
	}
	
	public void setValueForColumnName(String columnName, Object value) {
		if (Images_ID.equals(columnName)) {
			setImages_id(UtilsMethods.convertInt(value));
			return;
		}
		if (Image_Resolutions_ID.equals(columnName)) {
			setImage_resolutions_id(UtilsMethods.convertInt(value));
			return;
		}
		if (PRICE.equals(columnName)) {
			setPrice(UtilsMethods.convertDouble(value));
			return;
		}
	}
	
	public Object getValueForColumnName(String columnName) {
		if (Images_ID.equals(columnName)) {
			return getImages_id();
		}
		if (Image_Resolutions_ID.equals(columnName)) {
			return getImage_resolutions_id();
		}
		if (PRICE.equals(columnName)) {
			return getPrice();
		}
		return null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + image_resolutions_id;
		result = prime * result + images_id;
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
		Images_has_Image_Resolutions other = (Images_has_Image_Resolutions) obj;
		if (image_resolutions_id != other.image_resolutions_id)
			return false;
		if (images_id != other.images_id)
			return false;
		return true;
	}

	public int getImages_id() {
		return images_id;
	}
	
	public int getImage_resolutions_id() {
		return image_resolutions_id;
	}
	
	public double getPrice() {
		return price;
	}
	
	public void setImages_id(int images_id) {
		this.images_id = images_id;
	}
	
	public void setImage_resolutions_id(int image_resolutions_id) {
		this.image_resolutions_id = image_resolutions_id;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}

}
