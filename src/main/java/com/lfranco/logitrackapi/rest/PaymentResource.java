package com.lfranco.logitrackapi.rest;

import com.lfranco.logitrackapi.entity.Payment;
import com.lfranco.logitrackapi.requets.PaymentRequest;
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

    // 👇 Instanciación manual — sin CDI, sin @Inject
    private final PaymentService service = new PaymentService();

    @POST
    public Response create(PaymentRequest request) {
        Payment created = service.registerPayment(
                request.getOrderId(),
                request.getAmount(),
                request.getMethod()
        );
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }

    @GET
    @Path("/by-order/{orderId}")
    public List<Payment> listByOrder(@PathParam("orderId") Long orderId) {
        return service.findByOrder(orderId);
    }
}