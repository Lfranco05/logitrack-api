package com.lfranco.logitrackapi.service;

import com.lfranco.logitrackapi.entity.Order;
import com.lfranco.logitrackapi.entity.Payment;
import com.lfranco.logitrackapi.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PaymentService {

    public Payment registerPayment(Long orderId, BigDecimal amount, String method) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Order order = em.find(Order.class, orderId);
            if (order == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Order no encontrada");
            }

            // Total pagado hasta ahora
            BigDecimal totalPaid = em.createQuery(
                            "SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.order = :ord",
                            BigDecimal.class)
                    .setParameter("ord", order)
                    .getSingleResult();

            BigDecimal totalOrder = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
            BigDecimal pending = totalOrder.subtract(totalPaid);

            if (amount.compareTo(pending) > 0) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("El pago excede el saldo pendiente");
            }

            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setAmount(amount);
            payment.setMethod(method);

            em.persist(payment);
            em.getTransaction().commit();
            return payment;
        } finally {
            em.close();
        }
    }

    public List<Payment> findByOrder(Long orderId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Payment p WHERE p.order.orderId = :oid",
                            Payment.class)
                    .setParameter("oid", orderId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public BigDecimal getTotalPendingByCustomer(Long customerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Suma (totalOrden - pagos) por cada orden del cliente
            List<Order> orders = em.createQuery(
                            "SELECT o FROM Order o WHERE o.customer.customerId = :cid",
                            Order.class)
                    .setParameter("cid", customerId)
                    .getResultList();

            BigDecimal totalPending = BigDecimal.ZERO;

            for (Order o : orders) {
                BigDecimal totalOrder = o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO;
                BigDecimal paid = em.createQuery(
                                "SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.order = :ord",
                                BigDecimal.class)
                        .setParameter("ord", o)
                        .getSingleResult();
                totalPending = totalPending.add(totalOrder.subtract(paid));
            }

            return totalPending;
        } finally {
            em.close();
        }
    }
}