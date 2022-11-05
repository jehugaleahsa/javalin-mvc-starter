package com.truncon.migrations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.nio.file.Path;
import java.util.Collection;

public final class Migrator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Migrator.class);
    private final Provider<MigrationRepository> repositoryProvider;
    private final SqlScriptLocator locator;
    private final SqlScriptSelector selector;

    @Inject
    public Migrator(
            Provider<MigrationRepository> repositoryProvider,
            SqlScriptLocator locator,
            SqlScriptSelector selector) {
        this.repositoryProvider = repositoryProvider;
        this.locator = locator;
        this.selector = selector;
    }

    public void migrate(Path migrationDirectory) {
        try (MigrationRepository repository = this.repositoryProvider.get()) {
            repository.createTableIfMissing();
            Collection<MigrationSqlScript> scripts = locator.findScripts(migrationDirectory);
            Collection<MigrationSqlScript> applicable = selector.getMigrationsToApply(scripts);
            if (applicable.isEmpty()) {
                LOGGER.info("No migrations to apply.");
                return;
            }
            runMigrations(repository, applicable);
        } catch (MigrationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new MigrationException("Failed to apply migrations.", exception); // This should prevent the application from starting
        }
    }

    private void runMigrations(MigrationRepository repository, Collection<MigrationSqlScript> scripts) {
        for (MigrationSqlScript script : scripts) {
            LOGGER.info("Applying migration: " + script.getDisplayName() + ".");
            String contents = locator.getScript(script.getPath());
            repository.runMigration(script, contents);
        }
    }
}
