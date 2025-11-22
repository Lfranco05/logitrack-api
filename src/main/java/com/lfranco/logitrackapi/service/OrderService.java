package com.lfranco.logitrackapi.service;

import com.lfranco.logitrackapi.entity.Customer;
import com.lfranco.logitrackapi.entity.Order;
import com.lfranco.logitrackapi.entity.OrderItem;
import com.lfranco.logitrackapi.entity.Product;
import com.lfranco.logitrackapi.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderService {

    private final CustomerService customerService = new CustomerService();
    private final ProductService productService = new ProductService();

    public Order createOrder(Long customerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Customer customer = customerService.findById(customerId);
            if (customer == null || !Boolean.TRUE.equals(customer.getActive())) {
                throw new IllegalArgumentException("Customer inválido o inactivo");
            }

            em.getTransaction().begin();
            Order order = new Order();
            order.setCustomer(customer);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("Pending");
            order.setTotalAmount(BigDecimal.ZERO);
            em.persist(order);
            em.getTransaction().commit();
            return order;
        } finally {
            em.close();
        }
    }

    public Order findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Order.class, id);
        } finally {
            em.close();
        }
    }

    public List<Order> findByCustomer(Long customerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT o FROM Order o WHERE o.customer.customerId = :cid",
                            Order.class)
                    .setParameter("cid", customerId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Order> findByStatus(String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT o FROM Order o WHERE o.status = :st",
                            Order.class)
                    .setParameter("st", status)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Order> findIncompleteOrders() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT o FROM Order o " +
                                    "WHERE (o.totalAmount IS NULL OR o.totalAmount = 0) " +
                                    "AND o.status IN ('Pending','Processing')",
                            Order.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public OrderItem addItem(Long orderId, Long productId, Integer quantity) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Order order = em.find(Order.class, orderId);
            if (order == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Order no encontrada");
            }

            Product product = em.find(Product.class, productId);
            if (product == null || !Boolean.TRUE.equals(product.getActive())) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Product inválido o inactivo");
            }

            if (quantity == null || quantity <= 0) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Cantidad inválida");
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setUnitPrice(product.getPrice());

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(quantity));
            item.setSubtotal(subtotal);

            em.persist(item);

            // Recalcular totalAmount
            BigDecimal total = em.createQuery(
                            "SELECT COALESCE(SUM(oi.subtotal), 0) FROM OrderItem oi WHERE oi.order = :ord",
                            BigDecimal.class)
                    .setParameter("ord", order)
                    .getSingleResult();

            order.setTotalAmount(total);

            em.getTransaction().commit();
            return item;
        } finally {
            em.close();
        }
    }

    public Order changeStatus(Long orderId, String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Order order = em.find(Order.class, orderId);
            if (order == null) {
                em.getTransaction().rollback();
                return null;
            }
            order.setStatus(status);
            em.getTransaction().commit();
            return order;
        } finally {
            em.close();
        }
    }
}