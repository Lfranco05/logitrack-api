package com.lfranco.logitrackapi.service;

import com.lfranco.logitrackapi.entity.Product;
import com.lfranco.logitrackapi.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ProductService {

    public Product create(Product p) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            p.setActive(true);
            em.persist(p);
            em.getTransaction().commit();
            return p;
        } finally {
            em.close();
        }
    }

    public Product findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Product.class, id);
        } finally {
            em.close();
        }
    }

    public List<Product> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p", Product.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Product> findByCategory(String category) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p WHERE p.category = :cat", Product.class)
                    .setParameter("cat", category)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Product update(Long id, Product data) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Product existing = em.find(Product.class, id);
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }
            existing.setName(data.getName());
            existing.setDescription(data.getDescription());
            existing.setPrice(data.getPrice());
            existing.setCategory(data.getCategory());
            existing.setActive(data.getActive());
            em.getTransaction().commit();
            return existing;
        } finally {
            em.close();
        }
    }

    public Product changeActive(Long id, boolean active) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Product existing = em.find(Product.class, id);
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }
            existing.setActive(active);
            em.getTransaction().commit();
            return existing;
        } finally {
            em.close();
        }
    }
}