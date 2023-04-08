package com.truncon.modeling;

import jakarta.persistence.Query;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public final class JooqQueries {
    private JooqQueries() {
    }

    public static Query createQuery(
            HibernateDbContext dbContext,
            Class<?> clz,
            Function<DSLContext, org.jooq.Query> queryBuilder) {
        DSLContext context = DSL.using(SQLDialect.POSTGRES);
        org.jooq.Query jooqQuery = queryBuilder.apply(context);
        Query query = dbContext.createNativeQuery(jooqQuery.getSQL(), clz);
        setBindParameters(query, jooqQuery);
        return query;
    }

    private static void setBindParameters(Query query, org.jooq.Query jooqQuery) {
        List<Object> values = jooqQuery.getBindValues();
        for (int i = 0; i < values.size(); i++) {
            query.setParameter(i + 1, values.get(i));
        }
    }

    public static <T> Stream<T> queryStream(
            HibernateDbContext dbContext,
            Class<T> clz,
            Function<DSLContext, org.jooq.Query> queryBuilder) {
        Query query = createQuery(dbContext, clz, queryBuilder);
        return asStream(query, clz);
    }

    public static <T> List<T> queryList(
            HibernateDbContext dbContext,
            Class<T> clz,
            Function<DSLContext, org.jooq.Query> queryBuilder) {
        Query query = createQuery(dbContext, clz, queryBuilder);
        try (Stream<T> queryStream = asStream(query, clz)) {
            return queryStream.toList();
        }
    }

    public static <T> Optional<T> queryFirst(
            HibernateDbContext dbContext,
            Class<T> clz,
            Function<DSLContext, org.jooq.Query> queryBuilder) {
        Query query = createQuery(dbContext, clz, queryBuilder);
        try (Stream<T> queryStream = asStream(query, clz)) {
            return queryStream.findFirst();
        }
    }

    private static <T> Stream<T> asStream(Query query, @SuppressWarnings("unused") Class<T> clz) {
        @SuppressWarnings("unchecked")
        Stream<T> typed = query.getResultStream();
        return typed;
    }
}
