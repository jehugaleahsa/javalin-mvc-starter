package com.truncon.modeling;

import jakarta.persistence.EntityManager;

public final class HibernateTransactionManager implements TransactionManager {
    private final EntityManager entityManager;

    public HibernateTransactionManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public TransactionScope createTransaction() throws DbException {
        try {
            return new HibernateTransactionScope(entityManager.getTransaction());
        } catch (Exception exception) {
            throw new DbException("Failed to create a transaction.", exception);
        }
    }
}
