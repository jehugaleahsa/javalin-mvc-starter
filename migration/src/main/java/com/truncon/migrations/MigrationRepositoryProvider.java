package com.truncon.migrations;

import com.truncon.settings.DbSettings;

import javax.inject.Inject;
import javax.inject.Provider;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class MigrationRepositoryProvider implements Provider<MigrationRepository> {
    private final DbSettings settings;

    @Inject
    public MigrationRepositoryProvider(DbSettings settings) {
        this.settings = settings;
    }

    @Override
    public MigrationRepository get() {
        String url = settings.getUrl();
        Properties properties = new Properties();
        properties.setProperty("user", settings.getUsername());
        properties.setProperty("password", settings.getPassword());
        properties.setProperty("currentSchema", settings.getSchema());

        try {
            Connection connection = DriverManager.getConnection(url, properties);
            return new MigrationRepository(connection);
        } catch (SQLException exception) {
            throw new MigrationException("Failed to connect to the database for applying migrations.", exception);
        }
    }
}
