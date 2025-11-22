package com.lfranco.logitrackapi.rest;

import com.lfranco.logitrackapi.entity.Product;
import com.lfranco.logitrackapi.service.ProductService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    private final ProductService service = new ProductService();

    @GET
    public List<Product> getAll() {
        return service.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Product p = service.findById(id);
        if (p == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(p).build();
    }

    @GET
    @Path("/category/{category}")
    public List<Product> getByCategory(@PathParam("category") String category) {
        return service.findByCategory(category);
    }

    @POST
    public Response create(Product p) {
        Product created = service.create(p);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Product p) {
        Product updated = service.update(id, p);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @PUT
    @Path("/{id}/status")
    public Response changeStatus(@PathParam("id") Long id,
                                 @QueryParam("active") boolean active) {
        Product updated = service.changeActive(id, active);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }
}