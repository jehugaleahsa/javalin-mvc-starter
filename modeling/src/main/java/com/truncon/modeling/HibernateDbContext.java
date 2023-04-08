package com.truncon.modeling;

import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAInsertClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.Collection;

public final class HibernateDbContext implements DbContext {
    private final EntityManager entityManager;
    private final TransactionManager transactionManager;

    public HibernateDbContext(EntityManager entityManager, TransactionManager transactionManager) {
        this.entityManager = entityManager;
        this.transactionManager = transactionManager;
    }

    @Override
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Override
    public <T> T getReference(Class<T> entityClass, long id) {
        return this.entityManager.getReference(entityClass, id);
    }

    public CriteriaBuilder createCriteriaBuilder() {
        return entityManager.getCriteriaBuilder();
    }

    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> query) {
        return entityManager.createQuery(query);
    }

    public <T> TypedQuery<T> createQuery(String qlQuery, Class<T> clz) {
        return entityManager.createQuery(qlQuery, clz);
    }

    public Query createNativeQuery(String query, Class<?> clz) {
        return entityManager.createNativeQuery(query, clz);
    }

    public <T> JPAQuery<T> createQuery(EntityPath<T> entityPath) {
        JPAQueryFactory factory = new JPAQueryFactory(entityManager);
        return factory.selectFrom(entityPath);
    }

    public JPAInsertClause createInsert(EntityPath<?> entityPath) {
        JPAQueryFactory factory = new JPAQueryFactory(entityManager);
        return factory.insert(entityPath);
    }

    public JPAUpdateClause createUpdate(EntityPath<?> entityPath) {
        JPAQueryFactory factory = new JPAQueryFactory(entityManager);
        return factory.update(entityPath);
    }

    public JPADeleteClause createDelete(EntityPath<?> entityPath) {
        JPAQueryFactory factory = new JPAQueryFactory(entityManager);
        return factory.delete(entityPath);
    }

    @Override
    public <T> void markAdded(T entity) {
        entityManager.persist(entity);
    }

    @Override
    public <T> void markAdded(Collection<T> entities) {
        for (T entity : entities) {
            entityManager.persist(entity);
        }
    }

    @Override
    public <T> void markUpdated(T entity) {
        entityManager.merge(entity);
    }

    @Override
    public <T> void markUpdated(Collection<T> entities) {
        for (T entity : entities) {
            entityManager.merge(entity);
        }
    }

    @Override
    public <T> void markRemoved(T entity) {
        entityManager.remove(entity);
    }

    @Override
    public <T> void markRemoved(Collection<T> entities) {
        for (T entity : entities) {
            entityManager.remove(entity);
        }
    }

    @Override
    public void saveChanges() throws DbException {
        try (TransactionScope scope = transactionManager.beginTransaction()) {
            entityManager.flush();
            scope.commit();
        } catch (Exception exception) {
            throw new DbException("Failed to save the database changes.", exception);
        }
    }

    @Override
    public void close() throws DbException {
        try {
            entityManager.close();
        } catch (Exception exception) {
            throw new DbException("Failed to close the database context.", exception);
        }
    }
}
