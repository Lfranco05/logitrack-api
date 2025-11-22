package com.lfranco.logitrackapi.service;

import com.lfranco.logitrackapi.entity.Customer;
import com.lfranco.logitrackapi.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CustomerService {

    public Customer create(Customer c) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            c.setActive(true);
            em.persist(c);
            em.getTransaction().commit();
            return c;
        } finally {
            em.close();
        }
    }

    public Customer findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Customer.class, id);
        } finally {
            em.close();
        }
    }

    public Customer findByTaxId(String taxId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Customer> list = em.createQuery(
                            "SELECT c FROM Customer c WHERE c.taxId = :tax", Customer.class)
                    .setParameter("tax", taxId)
                    .getResultList();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            em.close();
        }
    }

    public List<Customer> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Customer c", Customer.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Customer update(Long id, Customer data) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Customer existing = em.find(Customer.class, id);
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }
            existing.setFullName(data.getFullName());
            existing.setTaxId(data.getTaxId());
            existing.setEmail(data.getEmail());
            existing.setAddress(data.getAddress());
            existing.setActive(data.getActive());
            em.getTransaction().commit();
            return existing;
        } finally {
            em.close();
        }
    }

    public Customer deactivate(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Customer existing = em.find(Customer.class, id);
            if (existing == null) {
                em.getTransaction().rollback();
                return null;
            }
            existing.setActive(false);
            em.getTransaction().commit();
            return existing;
        } finally {
            em.close();
        }
    }
}