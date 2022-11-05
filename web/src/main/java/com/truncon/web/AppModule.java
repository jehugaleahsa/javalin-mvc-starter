package com.truncon.web;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.truncon.javalin.mvc.api.Injector;
import com.truncon.migrations.MigrationRepository;
import com.truncon.modeling.DbContextFactory;
import com.truncon.settings.DbSettings;
import com.truncon.web.settings.ApplicationSettings;
import com.truncon.web.settings.HoconSettings;
import com.truncon.migrations.MigrationRepositoryProvider;
import com.truncon.migrations.SqlScriptLocator;
import com.truncon.migrations.SqlScriptSelector;
import com.truncon.modeling.HibernateDbContextFactory;

public final class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        super.configure();
        bind(Injector.class).toProvider(RequestInjectorProvider.class);
        bind(JavalinFactory.class);
        bind(App.class);

        // Settings
        bind(DbSettings.class).to(HoconSettings.class);
        bind(ApplicationSettings.class).to(HoconSettings.class);
        bind(HoconSettings.class).toProvider(HoconSettings::newInstance).in(Scopes.SINGLETON);

        // Database
        bind(DbContextFactory.class).to(HibernateDbContextFactory.class);
        bind(HibernateDbContextFactory.class).in(Scopes.SINGLETON);

        // Migrations
        bind(MigrationRepository.class).toProvider(MigrationRepositoryProvider.class);
        bind(SqlScriptLocator.class);
        bind(SqlScriptSelector.class);
    }
}
