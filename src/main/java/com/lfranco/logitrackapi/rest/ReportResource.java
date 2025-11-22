package com.lfranco.logitrackapi.rest;

import com.lfranco.logitrackapi.service.PaymentService;
import com.lfranco.logitrackapi.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.math.BigDecimal;
import java.util.List;

@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
public class ReportResource {

    private final PaymentService paymentService = new PaymentService();

    // Total adeudado por cliente
    @GET
    @Path("/customer/{customerId}/pending")
    public BigDecimal getTotalPending(@PathParam("customerId") Long customerId) {
        return paymentService.getTotalPendingByCustomer(customerId);
    }

    // Productos más vendidos (simple: productoId + cantidad total)
    @GET
    @Path("/top-products")
    public List<Object[]> getTopProducts() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT oi.product.productId, oi.product.name, SUM(oi.quantity) " +
                                    "FROM OrderItem oi " +
                                    "GROUP BY oi.product.productId, oi.product.name " +
                                    "ORDER BY SUM(oi.quantity) DESC",
                            Object[].class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}