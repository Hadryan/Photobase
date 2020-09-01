package entities;

import utils.UtilsMethods;

public class Image_Resolutions extends BasicEntity{

	private static final long serialVersionUID = 1L;

	private int width;
	private int height;
	private double max_price;
	private double min_price;
	
	public static final String WIDTH = "WIDTH";
	public static final String HEIGHT = "HEIGHT";
	public static final String MAX_PRICE = "MAX_PRICE";
	public static final String MIN_PRICE = "MIN_PRICE";
	
	public Image_Resolutions() {
		columnsName.add(WIDTH);
		columnsName.add(HEIGHT);
		columnsName.add(MAX_PRICE);
		columnsName.add(MIN_PRICE);
	}
	
	@Override
	public void setValueForColumnName(String columnName, Object value) {
		if (WIDTH.equals(columnName)) {
			setWidth(UtilsMethods.convertInt(value));
			return;
		}
		if (HEIGHT.equals(columnName)) {
			setHeight(UtilsMethods.convertInt(value));
			return;
		}
		if (MAX_PRICE.equals(columnName)) {
			setMax_price(UtilsMethods.convertDouble(value));
			return;
		}
		if (MIN_PRICE.equals(columnName)) {
			setMin_price(UtilsMethods.convertDouble(value));
			return;
		}
		super.setValueForColumnName(columnName, value);
	}
	
	@Override
	public Object getValueForColumnName(String columnName) {
		if (WIDTH.equals(columnName)) {
			return getWidth();
		}
		if (HEIGHT.equals(columnName)) {
			return getHeight();
		}
		if (MAX_PRICE.equals(columnName)) {
			return getMax_price();
		}
		if (MIN_PRICE.equals(columnName)) {
			return getMin_price();
		}
		return super.getValueForColumnName(columnName);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setMax_price(double max_price) {
		this.max_price = max_price;
	}
	
	public double getMax_price() {
		return max_price;
	}
	
	public void setMin_price(double min_price) {
		this.min_price = min_price;
	}
	
	public double getMin_price() {
		return min_price;
	}
	
}
