package utils;

import java.io.Serializable;
import java.util.ArrayList;

import entities.Images_has_Image_Resolutions;

public class ShoppingCart implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Images_has_Image_Resolutions> items;
	private double price;
	
	public ShoppingCart() {
		items = new ArrayList<>();
	}
	
	public boolean add(Images_has_Image_Resolutions image) {
		if (image == null) return false;
		if (!items.contains(image)) {
			items.add(image);
			calculatePrice();
			return true;
		}
		return false;
	}
	
	public boolean remove(Images_has_Image_Resolutions image) {
		if (image == null) return false;
		if (items.contains(image)) {
			items.remove(image);
			calculatePrice();
			return true;
		}
		return false;
	}
	
	private void calculatePrice() {
		price = 0;
		int div = items.size() / 4;
		if (div == 0) {
			for (Images_has_Image_Resolutions image:items)
				price += image.getPrice();
			return;
		}
		ArrayList<Images_has_Image_Resolutions> lowest = new ArrayList<>();
		for (int i = 0; i < div; i++) {
			double min = Integer.MAX_VALUE;
			Images_has_Image_Resolutions imageToAdd = null;
			for (Images_has_Image_Resolutions image:items) {
				if (!lowest.contains(image) && image.getPrice() < min) {
					min = image.getPrice();
					imageToAdd = image;
				}
			}
			lowest.add(imageToAdd);
		}
		for (Images_has_Image_Resolutions image:items) {
			if (lowest.contains(image)) {
				price += image.getPrice() * 0.95;
			} else
				price += image.getPrice();
		}
	}
	
	public double getPrice() {
		return price;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public ArrayList<Images_has_Image_Resolutions> getItems() {
		return items;
	}
	
	public void setItems(ArrayList<Images_has_Image_Resolutions> items) {
		this.items = items;
	}

}
