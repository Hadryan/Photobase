package utils;

import java.util.ArrayList;

import javax.ws.rs.FormParam;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import entities.Categories;
import entities.Images;
import entities.Images_has_Image_Resolutions;

public class MultipartFile {

	private Images image;
	private ResolutionInput resolutions;
	private CategoryInput categories;
	private byte[] data;
	
	public byte[] getData() {
		return data;
	}
	
	@FormParam("data")
	@PartType("application/octet-stream")
	public void setData(byte[] data) {
		System.out.println("data: "+data.length);
		this.data = data;
	}
	
	@FormParam("image")
	@PartType("application/json")
	public void setImage(String string) {
		System.out.println("image: "+string);
		Gson gson = new GsonBuilder().create();
		image = gson.fromJson(string, Images.class);
		System.out.println(image.getName());
	}
	
	@FormParam("resolutions")
	@PartType("application/json")
	public void setResolutions(String string) {
		System.out.println("resolutions" + string);
		Gson gson = new GsonBuilder().create();
		resolutions = gson.fromJson(string, ResolutionInput.class);
	}

	@FormParam("categories")
	@PartType("application/json")
	public void setCategories(String string) {
		System.out.println("categories " + string);
		Gson gson = new GsonBuilder().create();
		categories = gson.fromJson(string, CategoryInput.class);
	}
	
	public class ResolutionInput{
		ArrayList<Images_has_Image_Resolutions> items;
		
		public ArrayList<Images_has_Image_Resolutions> getItems() {
			return items;
		}
	}
	
	public class CategoryInput{
		ArrayList<Categories> items;
		
		public ArrayList<Categories> getItems() {
			return items;
		}
	}
	
	public Images getImage() {
		return image;
	}
	
	public ResolutionInput getResolutions() {
		return resolutions;
	}
	
	public CategoryInput getCategories() {
		return categories;
	}
	
}
