package controllers;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import dao.DAOImage;
import dao.DAOImage_Resolutions;
import entities.Categories;
import entities.Image_Resolutions;
import entities.Images;
import entities.Images_has_Image_Resolutions;
import services.IServiceImage;
import services.ServiceImage;
import utils.MultipartFile;

@Stateless
@LocalBean
@Path("/image")
public class ControllerImages {

	private IServiceImage service;

	public ControllerImages() {
		service = new ServiceImage(new DAOImage());
	}

	@POST
	@Path("/upload")
	@Produces("text/json")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadImage(@MultipartForm MultipartFile file, @Context HttpServletRequest request) {
		String response = service.uploadImage(file, request.getSession());
		return Response.ok(response).build();
	}
	
	@POST
	@Path("/getImageThumbnail")
	@Produces("text/plain")
	@Consumes("application/json")
	public String getImageThumbnail(Images image) {
		return this.service.getImageThumbnail(image);
	}
	
	@POST
	@Path("/getImagePreview")
	@Produces("text/plain")
	@Consumes("application/json")
	public String getImagePreview(Images image) {
		return this.service.getImagePreview(image);
	}
	
	@POST
	@Path("/searchByKeyword")
	@Produces("text/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public List<Images> searchByKeyword(@Context HttpServletRequest request, @FormParam("keywords")String entry, @FormParam("init")String init,
			@FormParam("reverse")String reverse){
		return this.service.searchByKeyword(request.getSession(), entry, init, reverse);
	}
	
	@GET
	@Path("/searchBy/{criteria}/{text}/{init}/{reverse}")
	@Produces("text/json")
	public List<Images> searchBy(@Context HttpServletRequest request, @PathParam("criteria")String criteria, @PathParam("text")String text,
			@PathParam("init")String init, @PathParam("reverse")String reverse){
		return this.service.searchBy(request.getSession(), criteria, text, init, reverse);
	}
	
	@GET
	@Path("/sortedBy/{criteria}/{order}/{init}/{reverse}")
	@Produces("text/json")
	public List<Images> sortBy(@Context HttpServletRequest request, @PathParam("criteria")String criteria, @PathParam("order")int order,
			@PathParam("init")String init, @PathParam("reverse")String reverse){
		return this.service.sortBy(request.getSession(), criteria, order, init, reverse);
	}
	
	@GET
	@Path("/{id}")
	@Produces("text/json")
	public Images getById(@PathParam("id") int id) {
		return this.service.getById(id);
	}

	@POST
	@Path("/add")
	@Produces("text/json")
	@Consumes("application/json")
	public boolean addImage(Images image) {
		return service.add(image);
	}
	
	@GET
	@Path("/getCategories")
	@Produces("text/json")
	public List<Categories> getCategories(){
		return this.service.getCategories();
	}
	
	@GET
	@Path("/getResolutions")
	@Produces("text/json")
	public List<Image_Resolutions> getResolutions(){
		return this.service.getResolutions();
	}
	
	@GET
	@Path("/getCategoriesOfImage/{id}")
	@Produces("text/json")
	public List<Categories> getCategoriesOfImage(@PathParam("id")int id){
		return this.service.getCategoriesOfImage(id);
	}
	
	@GET
	@Path("/getAvailableResolutions/{id}")
	@Produces("text/json")
	public List<Images_has_Image_Resolutions> getAvailableResolutions(@PathParam("id")int id){
		return this.service.getAvailableResolutions(id);
	}
	
	@GET
	@Path("/getResolutionsOfImage/{id}")
	@Produces("text/json")
	public List<Image_Resolutions> getResolutionsOfImage(@PathParam("id")int id){
		return this.service.getResolutionsOfImage(id);
	}
	
	@GET
	@Path("/getResolutionById/{id}")
	@Produces("text/json")
	public Image_Resolutions getResolutionById(@PathParam("id")int id){
		DAOImage_Resolutions resolutionService = new DAOImage_Resolutions();
		return resolutionService.getById(id);
	}

}
