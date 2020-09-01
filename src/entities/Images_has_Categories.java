package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import utils.UtilsMethods;

public class Images_has_Categories extends NoIdEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int imagesId;
	private int categoriesId;
	
	public static final String Images_ID = "Images_ID";
	public static final String Categories_ID = "Categories_ID";
	
	public Images_has_Categories() {
		super();
		columnsName.add(Images_ID);
		columnsName.add(Categories_ID);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + categoriesId;
		result = prime * result + imagesId;
		return result;
	}
	
	public void setValueForColumnName(String columnName, Object value) {
		if (Images_ID.equals(columnName)) {
			setImagesId(UtilsMethods.convertInt(value));
			return;
		}
		if (Categories_ID.equals(columnName)) {
			setCategoriesId(UtilsMethods.convertInt(value));
			return;
		}
	}
	
	public Object getValueForColumnName(String columnName) {
		if (Images_ID.equals(columnName)) {
			return getImagesId();
		}
		if (Categories_ID.equals(columnName)) {
			return getCategoriesId();
		}
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Images_has_Categories other = (Images_has_Categories) obj;
		if (categoriesId != other.categoriesId)
			return false;
		if (imagesId != other.imagesId)
			return false;
		return true;
	}

	public int getCategoriesId() {
		return categoriesId;
	}
	
	public int getImagesId() {
		return imagesId;
	}
	
	public void setCategoriesId(int categoriesId) {
		this.categoriesId = categoriesId;
	}
	
	public void setImagesId(int imagesId) {
		this.imagesId = imagesId;
	}
	
}
