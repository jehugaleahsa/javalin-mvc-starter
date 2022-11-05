package com.truncon.modeling;

public interface DbContext extends DbChangeTracker, AutoCloseable {
    TransactionManager getTransactionManager();

    void saveChanges() throws DbException;

    @Override
    void close() throws DbException;
}
