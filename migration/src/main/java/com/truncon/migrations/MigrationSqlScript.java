package com.truncon.migrations;

import java.nio.file.Path;
import java.util.Comparator;

public final class MigrationSqlScript {
    public static final Comparator<MigrationSqlScript> COMPARATOR = Comparator
        .comparing((MigrationSqlScript s) -> new ReleaseVersion(s.getRelease(), s.getVersion()), ReleaseVersion.COMPARATOR);

    private String release;
    private String version;
    private String description;
    private Path path;
    private Migration migration;

    public MigrationSqlScript() {
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Migration getMigration() {
        return migration;
    }

    public void setMigration(Migration migration) {
        this.migration = migration;
    }

    public String getDisplayName() {
        StringBuilder builder = new StringBuilder();
        builder.append(release);
        builder.append('/');
        builder.append(version);
        if (description != null) {
            builder.append(" - ");
            builder.append(description);
        }
        return builder.toString();
    }
}
