package com.truncon.modeling;

import jakarta.persistence.EntityTransaction;

public final class HibernateTransactionScope implements TransactionScope {
    private final EntityTransaction transaction;

    HibernateTransactionScope(EntityTransaction transaction) {
        this.transaction = transaction;
    }

    public EntityTransaction getHandle() {
        return this.transaction;
    }

    @Override
    public void begin() throws DbException {
        try {
            transaction.begin();
        } catch (Exception exception) {
            throw new DbException("Failed to begin the transaction.", exception);
        }
    }

    @Override
    public void commit() throws DbException {
        try {
            transaction.commit();
        } catch (Exception exception) {
            throw new DbException("Failed to commit the transaction.", exception);
        }
    }

    @Override
    public void rollback() throws DbException {
        try {
            transaction.rollback();
        } catch (Exception exception) {
            throw new DbException("Failed to roll back the transaction.", exception);
        }
    }

    @Override
    public void close() throws DbException {
        try {
            // We got to the end of a scope without commit or rollback
            // being called. We call rollback to properly dispose any changes.
            if (transaction.isActive()) {
                transaction.rollback();
            }
        } catch (Exception exception) {
            throw new DbException("Failed to close the transaction.", exception);
        }
    }
}
