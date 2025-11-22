package com.lfranco.logitrackapi.rest;

import com.lfranco.logitrackapi.entity.Order;
import com.lfranco.logitrackapi.entity.OrderItem;
import com.lfranco.logitrackapi.service.OrderService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    private final OrderService service = new OrderService();

    // Crear una orden para un cliente
    @POST
    public Response createOrder(@QueryParam("customerId") Long customerId) {
        try {
            Order created = service.createOrder(customerId);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Order o = service.findById(id);
        if (o == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(o).build();
    }

    @GET
    public List<Order> getByFilters(@QueryParam("customerId") Long customerId,
                                    @QueryParam("status") String status) {
        // Simple: si llega customerId, filtramos por cliente, si no por status
        if (customerId != null) {
            return service.findByCustomer(customerId);
        } else if (status != null && !status.isBlank()) {
            return service.findByStatus(status);
        } else {
            // podría devolverse todo, pero lo dejamos simple por ahora
            return service.findByStatus("Pending");
        }
    }

    // Agregar item a una orden
    @POST
    @Path("/{orderId}/items")
    public Response addItem(@PathParam("orderId") Long orderId,
                            @QueryParam("productId") Long productId,
                            @QueryParam("quantity") Integer quantity) {
        try {
            OrderItem item = service.addItem(orderId, productId, quantity);
            return Response.status(Response.Status.CREATED).entity(item).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    // Cambiar estado de la orden
    @PUT
    @Path("/{orderId}/status")
    public Response changeStatus(@PathParam("orderId") Long orderId,
                                 @QueryParam("status") String status) {
        Order updated = service.changeStatus(orderId, status);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    // Listar órdenes incompletas
    @GET
    @Path("/incomplete")
    public List<Order> getIncompleteOrders() {
        return service.findIncompleteOrders();
    }
}