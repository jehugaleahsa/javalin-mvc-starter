package com.truncon.modeling;

import com.truncon.settings.DbSettings;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.reflections.Reflections;

import javax.inject.Inject;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.function.Function;

public final class HibernateDbContextFactory implements DbContextFactory {
    private final SessionFactory sessionFactory;
    private final Function<EntityManager, TransactionManager> transactionManagerFactory;

    @Inject
    public HibernateDbContextFactory(DbSettings settings) {
        Configuration configuration = new Configuration();
        // Hibernate settings equivalent to hibernate.cfg.xml properties
        configuration.setProperty(AvailableSettings.DRIVER, settings.getDriver());
        configuration.setProperty(AvailableSettings.URL, settings.getUrl());
        configuration.setProperty(AvailableSettings.USER, settings.getUsername());
        configuration.setProperty(AvailableSettings.PASS, settings.getPassword());
        configuration.setProperty(AvailableSettings.DEFAULT_SCHEMA, settings.getSchema());
        configuration.setProperty(AvailableSettings.CONNECTION_PROVIDER, "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");

        addAnnotatedEntityClasses(configuration);

        this.sessionFactory = configuration.buildSessionFactory();
        this.transactionManagerFactory = settings.isRollbackOnly()
            ? (e) -> new RollbackOnlyTransactionManager(new HibernateTransactionManager(e))
            : HibernateTransactionManager::new;
    }

    @Override
    public HibernateDbContext createContext() throws DbException {
        EntityManager entityManager = sessionFactory.createEntityManager();
        TransactionManager transactionManager = this.transactionManagerFactory.apply(entityManager);
        return new HibernateDbContext(entityManager, transactionManager);
    }

    private void addAnnotatedEntityClasses(Configuration configuration) {
        Reflections reflections = new Reflections("com.truncon.models");
        Set<Class<?>> annotatedTypes = reflections.getTypesAnnotatedWith(jakarta.persistence.Entity.class);
        annotatedTypes.stream()
            .filter(c -> !Modifier.isAbstract(c.getModifiers()))
            .filter(c -> !Modifier.isInterface(c.getModifiers()))
            .forEach(configuration::addAnnotatedClass);
    }

    public void close() throws DbException {
        try {
            sessionFactory.close();
        } catch (Exception exception) {
            throw new DbException("Failed to close the context factory.", exception);
        }
    }
}
