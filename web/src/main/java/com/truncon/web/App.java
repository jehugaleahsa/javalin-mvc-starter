package com.truncon.web;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.truncon.migrations.MigrationException;
import com.truncon.web.settings.ApplicationSettings;
import io.javalin.Javalin;
import com.truncon.migrations.Migrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.file.Path;

public final class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private final Javalin app;
    private final ApplicationSettings settings;

    @Inject
    public App(JavalinFactory javalinFactory, ApplicationSettings settings) {
        this.app = javalinFactory.create();
        this.settings = settings;
    }

    public static void main(String[] args) {
        try {
            Injector injector = Guice.createInjector(new AppModule());
            Migrator migrator = injector.getInstance(Migrator.class);
            migrator.migrate(Path.of("config", "migrations"));

            App app = injector.getInstance(App.class);
            app.start();
        } catch (MigrationException exception) {
            LOGGER.error("The database migration failed, preventing startup.", exception);
        } catch (Throwable throwable) {
            LOGGER.error("An uncaught exception terminated the application.", throwable);
        }
    }

    public void start() {
        app.start(settings.getPort());
    }

    public void stop() {
        app.stop();
    }
}
