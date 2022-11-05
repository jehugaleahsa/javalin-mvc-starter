package com.truncon.modeling;

public interface DbContextFactory extends AutoCloseable {
    DbContext createContext() throws DbException;

    @Override
    void close() throws DbException;
}
