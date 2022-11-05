package com.truncon.modeling;

public interface TransactionScope extends AutoCloseable {
    void begin() throws DbException;
    void commit() throws DbException;
    void rollback() throws DbException;
    void close() throws DbException;
}
