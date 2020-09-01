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

import dao.DAOOperators;
import entities.Authors;
import entities.Categories;
import entities.Firm;
import entities.Images;
import entities.Operators;
import entities.Users;
import services.IServiceOperator;
import services.ServiceOperators;

@Stateless
@LocalBean
@Path("/operator")
public class ControllerOperators {

	private IServiceOperator service;

	public ControllerOperators() {
		service = new ServiceOperators(new DAOOperators());
	}
	
	@POST
	@Path("/login")
	@Consumes("application/json")
	@Produces("text/json")
	public Response login(@Context HttpServletRequest request, Operators operator) {
		String response = this.service.login(request.getSession(), operator);
		return Response.ok(response).build();
	}
	
	@POST
	@Path("/changePassword")
	@Consumes("application/json")
	@Produces("text/json")
	public boolean changePassword(@Context HttpServletRequest  request, Operators operator) {
		return this.service.changePassword(request.getSession(), operator);
	}
	
	@GET
	@Path("/getPendingImages/{init}/{reverse}")
	@Produces("text/json")
	public List<Images> getPendingImages(@Context HttpServletRequest request, @PathParam("init")String init, @PathParam("reverse")String reverse){
		return this.service.getPendingImages(request.getSession(), init, reverse);
	}
	
	@POST
	@Path("/approveImage")
	@Consumes("application/json")
	@Produces("text/json")
	public boolean approveImage(@Context HttpServletRequest request, Images image) {
		return this.service.approveImage(request.getSession(), image);
	}
	
	@GET
	@Path("/getApplicants")
	@Produces("text/json")
	public List<Users> getApplicants(@Context HttpServletRequest request){
		return this.service.getApplicants(request.getSession());
	}
	
	@GET
	@Path("/getApplicantImages/{id}/{index}")
	@Produces("text/json")
	public String getApplicantImages(@Context HttpServletRequest request, @PathParam("id")int id, @PathParam("index")int index){
		return this.service.getApplicantImage(request.getSession(), id, index);
	}
	
	@POST
	@Path("/rateApplicant")
	@Produces("text/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response rateApplicant(@Context HttpServletRequest request, @FormParam("user_id")int user_id, @FormParam("grade")int grade) {
		String response = this.service.rateApplicant(request.getSession(), user_id, grade);
		return Response.ok(response).build();
	}
	
	@POST
	@Path("/banUser")
	@Produces("text/json")
	@Consumes("application/json")
	public Response banUser(@Context HttpServletRequest request, Users user) {
		String response = this.service.banUser(request.getSession(), user);
		return Response.ok(response).build();
	}
	
	@POST
	@Path("/numberSoldImagesFromUser")
	@Consumes("application/json")
	@Produces("text/json")
	public int numberOfSoldImagesFromUser(@Context HttpServletRequest request, Users user) {
		return this.service.numberOfSoldImagesFromUser(request.getSession(), user);
	}
	
	@POST
	@Path("/numberSoldImagesFromCategory")
	@Consumes("application/json")
	@Produces("text/json")
	public int numberOfSoldImagesFromCategory(@Context HttpServletRequest request, Categories category) {
		return this.service.numberOfSoldImagesFromCategory(request.getSession(), category);
	}
	
	@POST
	@Path("/mostFrequentlyBoughtImageResolutionsByUser")
	@Consumes("application/json")
	@Produces("text/json")
	public List<String> mostFrequentlyBoughtImagesByUser(@Context HttpServletRequest request, Users user){
		return this.service.mostFrequentlyBoughtImagesByUser(request.getSession(), user);
	}
	
	@POST
	@Path("/mostFrequentlyBoughtImageResolutionsInCategory")
	@Consumes("application/json")
	@Produces("text/json")
	public List<String> MostFrequentlyBoughtImagesInCategory(@Context HttpServletRequest request, Categories category){
		return this.service.MostFrequentlyBoughtImagesInCategory(request.getSession(), category);
	}
	
	@POST
	@Path("/averagePriceInCategory")
	@Consumes("application/json")
	@Produces("text/json")
	public double averagePriceInCategory(@Context HttpServletRequest request, Categories category) {
		return this.service.averagePriceInCategory(request.getSession(), category);
	}
	
	@POST
	@Path("/averagePriceByAuthor")
	@Consumes("application/json")
	@Produces("text/json")
	public double averagePriceByAuthor(@Context HttpServletRequest request, Users user) {
		return this.service.averagePriceByAuthor(request.getSession(), user);
	}
	
	@GET
	@Path("/getAppliedFirms")
	@Produces("text/json")
	public List<Firm> getAppliedFirms(@Context HttpServletRequest request){
		return this.service.getAppliedFirms(request.getSession());
	}
	
	@POST
	@Path("/approveFirm")
	@Produces("text/json")
	@Consumes("application/json")
	public Response approveFirm(@Context HttpServletRequest request, Firm firm) {
		String response = this.service.approveFirm(request.getSession(), firm);
		return Response.ok(response).build();
	}
	
	@POST
	@Path("/addFirmOperator")
	@Produces("text/json")
	@Consumes("application/json")
	public Response addFirmOperator(@Context HttpServletRequest request,	Operators operator) {
		String response = this.service.addFirmOperator(request.getSession(), operator);
		return Response.ok(response).build();
	}
	
	@GET
	@Path("/getAppliedAuthors")
	@Produces("text/json")
	public List<Authors> getAppliedAuthors(@Context HttpServletRequest request){
		return this.service.getAppliedAuthors(request.getSession());
	}
	
	@POST
	@Path("/approveFirmAuthor")
	@Produces("text/json")
	@Consumes("application/json")
	public Response approveFirmAuthor(@Context HttpServletRequest request,	Authors author) {
		String response = this.service.approveFirmAuthor(request.getSession(), author);
		return Response.ok(response).build();
	}
	
	@GET
	@Path("/getAllUsers")
	@Produces("text/json")
	public List<Users> getAllUsers(){
		return this.service.getAllUsers();
	}
	
	@GET
	@Path("/getFirmOperators")
	@Produces("text/json")
	public List<Operators> getFirmOperators(@Context HttpServletRequest request){
		return this.service.getFirmOperators(request.getSession());
	}
	
}
