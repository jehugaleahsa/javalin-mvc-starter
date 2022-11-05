package com.truncon.migrations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class MigrationRepository implements AutoCloseable {
    private final Connection connection;

    public MigrationRepository(Connection connection) {
        this.connection = connection;
    }

    public void createTableIfMissing() {
        String createSql = "CREATE TABLE IF NOT EXISTS migration (\n"
            + " id BIGSERIAL NOT NULL PRIMARY KEY,\n"
            + " type TEXT NOT NULL,\n"
            + " script TEXT NOT NULL,\n"
            + " release TEXT NOT NULL,\n"
            + " version TEXT NOT NULL,\n"
            + " description TEXT NULL,\n"
            + " checksum BIGINT NULL,\n"
            + " installation_date_time TIMESTAMP NOT NULL,\n"
            + " installed_by_user_id BIGINT NULL,\n"
            + " execution_duration BIGINT NOT NULL,\n"
            + " is_success BOOLEAN NOT NULL\n"
            + ")";
        try (PreparedStatement statement = connection.prepareStatement(createSql)) {
            statement.execute();
        } catch (SQLException exception) {
            throw new MigrationException("Failed to create the migration table.", exception);
        }
    }

    public Collection<Migration> getAllMigrations() {
        String selectSql = "SELECT\n"
            + " id, type, script, release, version, description, checksum,\n"
            + " installation_date_time, installed_by_user_id, execution_duration, is_success\n"
            + "FROM migration m";
        try (PreparedStatement statement = connection.prepareStatement(selectSql)) {
            List<Migration> migrations = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Migration migration = new Migration();
                    migration.setId(resultSet.getLong(1));
                    migration.setType(resultSet.getString(2));
                    migration.setScript(resultSet.getString(3));
                    migration.setRelease(resultSet.getString(4));
                    migration.setVersion(resultSet.getString(5));
                    migration.setDescription(resultSet.getString(6));
                    migration.setChecksum(resultSet.getLong(7));
                    migration.setInstallationDateTime(resultSet.getTimestamp(8).toInstant());
                    long installedByUserId = resultSet.getLong(9);
                    migration.setInstalledByUserId(resultSet.wasNull() ? null : installedByUserId);
                    long executionDuration = resultSet.getLong(10);
                    migration.setExecutionDuration(Duration.ofNanos(executionDuration));
                    migration.setSuccess(resultSet.getBoolean(11));
                    migrations.add(migration);
                }
            }
            return migrations;
        } catch (SQLException exception) {
            throw new MigrationException("Failed to retrieve the installed migrations.", exception);
        }
    }

    public void insertMigration(Migration migration) {
        String insertSql = "INSERT INTO migration\n"
            + "(\n"
            + "   type, script, release, version, description, checksum,\n"
            + "   installation_date_time, installed_by_user_id, execution_duration, is_success\n"
            + ")\n"
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            statement.setString(1, migration.getType());
            statement.setString(2, migration.getScript());
            statement.setString(3, migration.getRelease());
            statement.setString(4, migration.getVersion());
            if (migration.getDescription() == null) {
                statement.setNull(5, Types.VARCHAR);
            } else {
                statement.setString(5, migration.getDescription());
            }
            if (migration.getChecksum() == null) {
                statement.setNull(6, Types.BIGINT);
            } else {
                statement.setLong(6, migration.getChecksum());
            }
            statement.setTimestamp(7, Timestamp.from(migration.getInstallationDateTime()));
            if (migration.getInstalledByUserId() == null) {
                statement.setNull(8, Types.BIGINT);
            } else {
                statement.setLong(8, migration.getInstalledByUserId());
            }
            statement.setLong(9, migration.getExecutionDuration().toNanos());
            statement.setBoolean(10, migration.isSuccess());

            statement.execute();
        } catch (SQLException exception) {
            throw new MigrationException("Failed to insert a migration.", exception);
        }
    }

    public void updateMigration(Migration migration) {
        String updateSql = "UPDATE migration SET\n"
            + "   type = ?,\n"
            + "   script = ?,\n"
            + "   release = ?,\n"
            + "   version = ?,\n"
            + "   description = ?,\n"
            + "   checksum = ?,\n"
            + "   installation_date_time = ?,\n"
            + "   installed_by_user_id = ?,\n"
            + "   execution_duration = ?,\n"
            + "   is_success = ?\n"
            + "WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setString(1, migration.getType());
            statement.setString(2, migration.getScript());
            statement.setString(3, migration.getRelease());
            statement.setString(4, migration.getVersion());
            if (migration.getDescription() == null) {
                statement.setNull(5, Types.VARCHAR);
            } else {
                statement.setString(5, migration.getDescription());
            }
            if (migration.getChecksum() == null) {
                statement.setNull(6, Types.BIGINT);
            } else {
                statement.setLong(6, migration.getChecksum());
            }
            statement.setTimestamp(7, Timestamp.from(migration.getInstallationDateTime()));
            if (migration.getInstalledByUserId() == null) {
                statement.setNull(8, Types.BIGINT);
            } else {
                statement.setLong(8, migration.getInstalledByUserId());
            }
            statement.setLong(9, migration.getExecutionDuration().toNanos());
            statement.setBoolean(10, migration.isSuccess());
            statement.setLong(11, migration.getId());

            statement.execute();
        } catch (SQLException exception) {
            throw new MigrationException("Failed to insert a migration.", exception);
        }
    }

    public void runMigration(MigrationSqlScript script, String sql) {
        try {
            // Setting auto-commit to false simulates a transaction.
            boolean previousAutoCommit = connection.getAutoCommit();
            try {
                connection.setAutoCommit(false);
                long startTime = System.nanoTime();
                Exception caught = null;
                try (Statement statement = connection.createStatement()) {
                    statement.execute(sql);
                } catch (Exception exception) {
                    caught = exception;
                }
                long endTime = System.nanoTime();
                Duration executionTime = Duration.ofNanos(endTime - startTime);
                if (caught != null) {
                    connection.rollback();
                }

                if (script.getMigration() == null) {
                    Migration migration = createMigration(script, sql, executionTime, caught);
                    insertMigration(migration);
                } else {
                    Migration migration = script.getMigration();
                    updateMigration(migration, script, sql, executionTime, caught);
                    updateMigration(migration);
                }
                connection.commit();

                if (caught != null) {
                    throw caught;
                }
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        } catch (Exception exception) {
            String message = "Failed to run the migration: " + script.getDisplayName() + ".";
            throw new MigrationException(message, exception);
        }
    }

    private static Migration createMigration(
            MigrationSqlScript script,
            String sql,
            Duration duration,
            Exception caught) {
        Migration migration = new Migration();
        updateMigration(migration, script, sql, duration, caught);
        return migration;
    }

    private static void updateMigration(
            Migration migration,
            MigrationSqlScript script,
            String sql,
            Duration duration,
            Exception caught) {
        migration.setType("SQL");
        migration.setScript(script.getRelease() + "/" + script.getPath().getFileName().toString());
        migration.setRelease(script.getRelease());
        migration.setVersion(script.getVersion());
        migration.setDescription(script.getDescription());
        migration.setChecksum(Checksums.getChecksum(sql));
        migration.setInstallationDateTime(Instant.now());
        migration.setInstalledByUserId(-1L); // System user
        migration.setExecutionDuration(duration);
        migration.setSuccess(caught == null);
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException exception) {
            throw new MigrationException("Failed to close the connection used for migrations.", exception);
        }
    }
}
