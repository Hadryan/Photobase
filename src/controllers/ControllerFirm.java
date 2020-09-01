package controllers;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import dao.DAOFirm;
import entities.Firm;
import services.IServiceFirm;
import services.ServiceFirm;

@Stateless
@LocalBean
@Path("/firm")
public class ControllerFirm {

	private IServiceFirm service;
	
	public ControllerFirm() {
		service = new ServiceFirm(new DAOFirm());
	}
	
	@POST
	@Path("/register")
	@Consumes("application/json")
	@Produces("text/json")
	public boolean register(Firm firm) {
		return this.service.register(firm);
	}
	
	@GET
	@Path("/getById/{id}")
	@Produces("text/json")
	public Firm getById(@PathParam("id")int id) {
		return this.service.getById(id);
	}
	
	@GET
	@Path("/getAll")
	@Produces("text/json")
	public List<Firm> getAll(){
		return this.service.getApprovedFirms();
	}
	
}
