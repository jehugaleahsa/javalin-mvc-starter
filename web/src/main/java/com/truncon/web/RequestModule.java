package com.truncon.web;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.truncon.auditing.AuditContext;
import com.truncon.javalin.mvc.api.MvcModule;
import com.truncon.modeling.DbChangeTracker;
import com.truncon.modeling.HibernateDbContext;
import com.truncon.modeling.repositories.UserRepository;
import com.truncon.web.authentication.AppConfigFactory;
import com.truncon.web.authentication.AppConfigProvider;
import com.truncon.web.controllers.api.UserController;
import com.truncon.web.handlers.Authorizer;
import com.truncon.web.authentication.FormAuthenticator;
import com.truncon.web.controllers.api.HomeController;
import com.truncon.web.controllers.ui.SessionController;
import com.truncon.modeling.DbContext;
import com.truncon.modeling.HibernateDbContextProvider;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;

@MvcModule
public final class RequestModule extends AbstractModule {
    @Override
    protected void configure() {
        super.configure();

        // Controllers
        bind(HomeController.class);
        bind(UserController.class);
        bind(SessionController.class);

        // Security
        bind(AuditContext.class).toInstance(AuditContext.getCurrent());
        bind(FormAuthenticator.class);
        bind(ConfigFactory.class).to(AppConfigFactory.class).in(Scopes.SINGLETON);
        bind(Config.class)
            .toProvider(AppConfigProvider.class)
            .in(Scopes.SINGLETON);
        bind(Authorizer.class);

        // Database
        bind(DbChangeTracker.class).to(DbContext.class);
        bind(DbContext.class).to(HibernateDbContext.class);
        bind(HibernateDbContext.class).toProvider(HibernateDbContextProvider.class).in(Scopes.SINGLETON);
        bind(UserRepository.class);
    }
}
