package controllers;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import dao.DAOAdmins;
import entities.Admins;
import entities.Operators;
import services.IServiceAdmin;
import services.ServiceAdmin;

@Stateless
@LocalBean
@Path("/admin")
public class ControllerAdmins {

	private IServiceAdmin service;
	
	public ControllerAdmins() {
		service = new ServiceAdmin(new DAOAdmins());
	}
	
	@POST
	@Path("/adminLogin")
	@Consumes("application/json")
	@Produces("text/json")
	public Response login(@Context HttpServletRequest request, Admins admin) {
		String response = this.service.login(admin, request.getSession());
		return Response.ok(response).build();
	}
	
	@POST
	@Path("/addOperator")
	@Consumes("application/json")
	@Produces("text/json")
	public Response addOperator(@Context HttpServletRequest request, Operators operator) {
		String response = this.service.addOperator(operator, request.getSession());
		return Response.ok(response).build();
	}
	
	@GET
	@Path("/removeOperator/{id}")
	@Produces("text/json")
	public boolean removeOperator(@Context HttpServletRequest request, @PathParam("id")int operator_id) {
		return this.service.removeOperator(operator_id, request.getSession());
	}
	
	@GET
	@Path("/getOperators")
	@Produces("text/json")
	public List<Operators> getOperators(@Context HttpServletRequest request){
		return this.service.getOperators(request.getSession());
	}
	
	@GET
	@Path("/addCategory/{name}")
	@Produces("text/json")
	public Response addCategory(@Context HttpServletRequest request, @PathParam("name")String name) {
		String response = this.service.addCategory(name, request.getSession());
		return Response.ok(response).build();
	}
	
}
