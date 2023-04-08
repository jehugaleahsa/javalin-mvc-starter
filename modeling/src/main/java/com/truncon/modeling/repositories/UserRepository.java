package com.truncon.modeling.repositories;

import com.truncon.modeling.DbException;
import com.truncon.modeling.HibernateDbContext;
import com.truncon.modeling.JooqQueries;
import com.truncon.models.User;
import com.truncon.models.jooq.generated.tables.JUser;

import javax.inject.Inject;
import java.util.Optional;

public final class UserRepository {
    private final HibernateDbContext dbContext;

    @Inject
    public UserRepository(HibernateDbContext dbContext) {
        this.dbContext = dbContext;
    }

    public Optional<User> getUserByEmail(String email) throws DbException {
        // Criteria builder example
        //CriteriaBuilder cb = dbContext.createCriteriaBuilder();
        //CriteriaQuery<User> cbQuery = cb.createQuery(User.class);
        //Root<User> cbUser = cbQuery.from(User.class);
        //cbQuery
        //    .where(cb.equal(cbUser.get(User_.email), email))
        //    .select(cbUser);
        //TypedQuery<User> query = dbContext.createQuery(cbQuery);
        //query.setParameter("email", email);
        //User user = query.getSingleResult();
        //return Optional.ofNullable(user);

        // JPQL example
        //TypedQuery<User> query = dbContext.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        //query.setParameter("email", email);
        //User user = query.getSingleResult();
        //return Optional.ofNullable(user);

        // Querydsl example
//        QUser quser = QUser.user;
//        User user = dbContext.createQuery(quser)
//            .where(quser.email.eq(email))
//            .fetchOne();
//        return Optional.ofNullable(user);

        return JooqQueries.queryFirst(dbContext, User.class, ctx -> ctx.select()
            .from(JUser.USER)
            .where(JUser.USER.EMAIL.eq(email))
        );
    }
}
