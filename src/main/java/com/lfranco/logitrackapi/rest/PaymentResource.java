package com.lfranco.logitrackapi.rest;

import com.lfranco.logitrackapi.entity.Payment;
import com.lfranco.logitrackapi.service.PaymentService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.List;

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentResource {

    private final PaymentService service = new PaymentService();

    // Registrar un pago
    @POST
    public Response register(@QueryParam("orderId") Long orderId,
                             @QueryParam("amount") BigDecimal amount,
                             @QueryParam("method") String method) {
        try {
            Payment p = service.registerPayment(orderId, amount, method);
            return Response.status(Response.Status.CREATED).entity(p).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    // Listar pagos de una orden
    @GET
    @Path("/order/{orderId}")
    public List<Payment> getByOrder(@PathParam("orderId") Long orderId) {
        return service.findByOrder(orderId);
    }
}