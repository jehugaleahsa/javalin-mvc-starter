package com.truncon.modeling;

import java.util.Collection;

public interface DbChangeTracker {
    <T> T getReference(Class<T> entityClass, long id);

    <T> void markAdded(T entity);

    <T> void markAdded(Collection<T> entities);

    <T> void markUpdated(T entity);

    <T> void markUpdated(Collection<T> entities);

    <T> void markRemoved(T entity);

    <T> void markRemoved(Collection<T> entities);
}
