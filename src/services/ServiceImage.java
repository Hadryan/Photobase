package services;

import java.util.List;

import javax.servlet.http.HttpSession;

import dao.IDAOImage;
import entities.Categories;
import entities.Image_Resolutions;
import entities.Images;
import entities.Images_has_Image_Resolutions;
import utils.MultipartFile;

public class ServiceImage extends ServiceAbstract<Images, IDAOImage> implements IServiceImage{

	public ServiceImage(IDAOImage dao) {
		super(dao);
	}

	@Override
	public String uploadImage(MultipartFile file, HttpSession session) {
		return dao.uploadImage(file, session);
	}
	
	@Override
	public Images getById(int id) {
		return super.getById(id);
	}
	
	@Override
	public boolean add(Images object) {
		return super.add(object);
	}

	@Override
	public List<Images> searchByKeyword(HttpSession session, String entry, String init, String reverse) {
		return dao.searchByKeyword(session, entry, init, reverse);
	}

	@Override
	public List<Images> sortBy(HttpSession session, String criteria, int order, String init, String reverse) {
		return dao.sortBy(session, criteria, order, init, reverse);
	}

	@Override
	public List<Images> searchBy(HttpSession session, String criteria, String text, String init, String reverse) {
		return dao.searchBy(session, criteria, text, init, reverse);
	}

	@Override
	public String getImageThumbnail(Images image) {
		return this.dao.getImageThumbnail(image);
	}

	@Override
	public String getImagePreview(Images image) {
		return this.dao.getImagePreview(image);
	}

	@Override
	public List<Categories> getCategories() {
		return this.dao.getCategories();
	}

	@Override
	public List<Image_Resolutions> getResolutions() {
		return this.dao.getResolutions();
	}

	@Override
	public List<Categories> getCategoriesOfImage(int id) {
		return this.dao.getCategoriesOfImage(id);
	}

	@Override
	public List<Images_has_Image_Resolutions> getAvailableResolutions(int id) {
		return this.dao.getAvailableResolutions(id);
	}

	@Override
	public List<Image_Resolutions> getResolutionsOfImage(int id) {
		return this.dao.getResolutionsOfImage(id);
	}

}
