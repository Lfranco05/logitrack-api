package com.lfranco.logitrackapi.rest;

import com.lfranco.logitrackapi.entity.Customer;
import com.lfranco.logitrackapi.service.CustomerService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    private final CustomerService service = new CustomerService();

    @GET
    public List<Customer> getAll() {
        return service.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Customer c = service.findById(id);
        if (c == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(c).build();
    }

    @GET
    @Path("/tax/{taxId}")
    public Response getByTaxId(@PathParam("taxId") String taxId) {
        Customer c = service.findByTaxId(taxId);
        if (c == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(c).build();
    }

    @POST
    public Response create(Customer c) {
        Customer created = service.create(c);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Customer c) {
        Customer updated = service.update(id, c);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @PUT
    @Path("/{id}/deactivate")
    public Response deactivate(@PathParam("id") Long id) {
        Customer updated = service.deactivate(id);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }
}