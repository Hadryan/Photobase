package dao;

import java.util.List;

import javax.servlet.http.HttpSession;

import entities.Categories;
import entities.Image_Resolutions;
import entities.Images;
import entities.Images_has_Image_Resolutions;
import utils.MultipartFile;

public interface IDAOImage extends IDAOAbstract<Images>{
	
	public String uploadImage(MultipartFile file, HttpSession session);
	public List<Images> searchByKeyword(HttpSession session, String entry, String init, String reverse);
	public List<Images> searchBy(HttpSession session, String criteria, String text, String init, String reverse);
	public List<Images> sortBy(HttpSession session, String criteria, int order, String init, String reverse);
	public String getImageThumbnail(Images image);
	public String getImagePreview(Images image);
	public List<Categories> getCategories();
	public List<Image_Resolutions> getResolutions();
	public List<Categories> getCategoriesOfImage(int id);
	public List<Images_has_Image_Resolutions> getAvailableResolutions(int id);
	public List<Image_Resolutions> getResolutionsOfImage(int id);

}
