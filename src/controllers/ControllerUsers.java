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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import dao.DAOComments;
import dao.DAOUser;
import entities.Authors;
import entities.Comments;
import entities.Creditcard;
import entities.Firm;
import entities.Images;
import entities.Images_has_Image_Resolutions;
import entities.Users;
import services.IServiceUser;
import services.ServiceUser;
import utils.MultipartApplicationFile;
import utils.ShoppingCart;

@Stateless
@LocalBean
@Path("/user")
public class ControllerUsers {

	private IServiceUser service;
	
	public ControllerUsers() {
		this.service = new ServiceUser(new DAOUser());
	}
	
	@POST
	@Path("/register")
	@Produces("text/json")
	@Consumes("application/json")
	public boolean register(Users user) {
		return this.service.register(user);
	}
	
	@GET
	@Path("/registerConfirmation")
	@Produces("text/json")
	@Consumes("application/json")
	public boolean registerConfirmation(@QueryParam("token") String token) {
		return this.service.confirmRegistration(token);
	}
	
	@POST
	@Path("/login")
	@Produces("text/json")
	@Consumes("application/json")
	public Response login(@Context HttpServletRequest request, Users user) {
		String response = this.service.login(user, request.getSession());
		return Response.ok(response).build();
	}
	
	@GET
	@Path("/logout")
	@Produces("text/json")
	public boolean logout(@Context HttpServletRequest request) {
		request.getSession().invalidate();
		return true;
	}
	
	@GET
	@Path("/forgotPassword/{username}")
	@Produces("text/json")
	@Consumes("application/json")
	public boolean forgotPassword(@PathParam("username")String username, @Context HttpServletRequest request) {
		return this.service.forgotPassword(request.getSession(), username);
	}
	
	@POST
	@Path("/resetPassword")
	@Produces("text/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public boolean resetPassword(@Context HttpServletRequest request, @FormParam("token") String token, @FormParam("password")String password) {
		return this.service.resetPassword(request.getSession(), token, password);
	}
	
	@POST
	@Path("/changePassword")
	@Produces("text/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response changePassword(@Context HttpServletRequest request, @FormParam("currentPassword") String currentPassword, @FormParam("newPassword") String newPassword) {
		String response = this.service.changePassword(request.getSession(), currentPassword, newPassword);
		return Response.ok(response).build();
	}
	
	@GET
	@Path("/deleteAccount")
	@Produces("text/json")
	public boolean deleteAccount(@Context HttpServletRequest request) {
		return this.service.deleteAccount(request.getSession());
	}
	
	@POST
	@Path("/addToShoppingCart")
	@Produces("text/json")
	@Consumes("application/json")
	public boolean addToShoppingCart(@Context HttpServletRequest request, Images_has_Image_Resolutions image) {
		return this.service.addToShoppingCart(request.getSession(), image);
	}
	
	@POST
	@Path("/removeFromShoppingCart")
	@Produces("text/json")
	@Consumes("application/json")
	public boolean removeFromShoppingCart(@Context HttpServletRequest request, Images_has_Image_Resolutions image) {
		return this.service.removeFromShoppingCart(request.getSession(), image);
	}
	
	@GET
	@Path("/getShoppingCart")
	@Produces("text/json")
	public ShoppingCart getShoppingCart(@Context HttpServletRequest request) {
		return this.service.getShoppingCart(request.getSession());
	}
	
	@POST
	@Path("/addCreditcard")
	@Produces("text/json")
	@Consumes("application/json")
	public Response addCreditcard(@Context HttpServletRequest request, Creditcard card) {
		String response = this.service.addCreditcard(request.getSession(), card);
		return Response.ok(response).build();
	}
	
	@GET
	@Path("/getCreditcards")
	@Produces("text/json")
	public List<Creditcard> getCreditcards(@Context HttpServletRequest request){
		return this.service.getCreditcards(request.getSession());
	}
	
	@POST
	@Path("/buy")
	@Produces("text/json")
	@Consumes("application/json")
	public Response buy(@Context HttpServletRequest request, Creditcard card) {
		String response = this.service.buy(request.getSession(), card);
		return Response.ok(response).build();
	}
	
	@POST
	@Path("/rateImage")
	@Produces("text/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response rateImage(@Context HttpServletRequest request, @FormParam("image_id")int image_id, @FormParam("value")int value) {
		String response = service.rateImage(request.getSession(), image_id, value);
		return Response.ok(response).build();
	}
	
	@POST
	@Path("/rateAuthor")
	@Produces("text/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response rateAuthor(@Context HttpServletRequest request, @FormParam("author_id")int author_id, @FormParam("value")int value) {
		String response = this.service.rateAuthor(request.getSession(), author_id, value);
		return Response.ok(response).build();
	}
	
	@POST
	@Path("/comment")
	@Produces("text/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response comment(@Context HttpServletRequest request, @FormParam("author_id")int author_id, @FormParam("text")String text) {
		String response = this.service.comment(request.getSession(), author_id, text);
		return Response.ok(response).build();
	}
	
	@GET
	@Path("/getComments/{id}")
	@Produces("text/json")
	public List<Comments> getComments(@PathParam("id")int author_id){
		DAOComments comments = new DAOComments();
		return comments.getAllFromAuthor(author_id);
	}
	
	@POST
	@Path("/applyForAuthor")
	@Produces("text/json")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response applyForAuthor(@MultipartForm MultipartApplicationFile file, @Context HttpServletRequest request) {
		String response = this.service.applyForAuthor(request.getSession(), file);
		return Response.ok(response).build();
	}
	
	@GET
	@Path("/setAuthorCard/{id}")
	@Produces("text/json")
	public boolean setAuthorCard(@Context HttpServletRequest request, @PathParam("id")int card_id) {
		return this.service.setAuthorCreditcard(request.getSession(), card_id);
	}
	
	@POST
	@Path("/deleteImage")
	@Produces("text/json")
	@Consumes("application/json")
	public boolean deleteImage(@Context HttpServletRequest request, Images image) {
		return this.service.deleteImage(request.getSession(), image);
	}
	
	@GET
	@Path("/getFirms")
	@Produces("text/json")
	public List<Firm> getFirms(@Context HttpServletRequest request){
		return this.service.getFirms(request.getSession());
	}
	
	@POST
	@Path("/applyToFirm")
	@Produces("text/json")
	@Consumes("application/json")
	public Response applyToFirm(@Context HttpServletRequest request, Firm firm) {
		String response = this.service.applyToFirm(request.getSession(), firm);
		return Response.ok(response).build();
	}
	
	@GET
	@Path("/leaveFirm")
	@Produces("text/json")
	public Response leaveFirm(@Context HttpServletRequest request) {
		String response = this.service.leaveFirm(request.getSession());
		return Response.ok(response).build();
	}
	
	@GET
	@Path("getById/{id}")
	@Produces("text/json")
	public Users getById(@PathParam("id")int id) {
		return this.service.getById(id);
	}
	
	@GET
	@Path("getAuthorById/{id}")
	@Produces("text/json")
	public Authors getAuthorById(@PathParam("id")int id) {
		return this.service.getAuthorById(id);
	}
	
	@GET
	@Path("getAuthorFirmId")
	@Produces("text/json")
	public int getAuthorFirmId(@Context HttpServletRequest request) {
		return this.service.getFirmId(request.getSession());
	}
	
	@GET
	@Path("/checkIfAppliedToFirm")
	@Produces("text/json")
	public boolean checkIfAppliedToFirm(@Context HttpServletRequest request) {
		return this.service.checkIfAppliedForFirm(request.getSession());
	}
	
	@GET
	@Path("/getImagesFromUser")
	@Produces("text/json")
	public List<Images> getImagesFromUser(@Context HttpServletRequest request){
		return this.service.getImagesFromUser(request.getSession());
	}
	
	@POST
	@Path("/sendImage")
	@Produces("text/json")
	@Consumes("application/json")
	public boolean sendImage(@Context HttpServletRequest request, Images image) {
		return this.service.send(request.getSession(), image);
	}
	
	@GET
	@Path("/getOwnedImages")
	@Produces("text/json")
	public List<Images> getOwnedImages(@Context HttpServletRequest request){
		return this.service.getOwnedImages(request.getSession());
	}
	
}
