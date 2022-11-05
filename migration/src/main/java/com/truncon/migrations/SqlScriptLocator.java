package com.truncon.migrations;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SqlScriptLocator {
    public SqlScriptLocator() {
    }

    public Collection<MigrationSqlScript> findScripts(Path root) {
        Collection<Path> releaseDirectories = getReleaseDirectories(root);
        return getScripts(releaseDirectories);
    }

    private Collection<Path> getReleaseDirectories(Path root) {
        try (Stream<Path> pathStream = Files.walk(root, 1)) {
            return pathStream
                .filter(Files::isDirectory)
                .collect(Collectors.toList());
        } catch (IOException exception) {
            throw new MigrationException("Failed to locate the release directories.", exception);
        }
    }

    private Collection<MigrationSqlScript> getScripts(Collection<Path> releaseDirectories) {
        return releaseDirectories.stream()
            .map(this::getScripts)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private Collection<MigrationSqlScript> getScripts(Path releaseDirectory) {
        try (Stream<Path> pathStream = Files.walk(releaseDirectory, 1)) {
            return pathStream
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".sql"))
                .map(p -> createMigrationSqlScript(releaseDirectory, p))
                .collect(Collectors.toList());
        } catch (IOException exception) {
            String message = "Failed to locate the SQL scripts under a release: " + releaseDirectory + ".";
            throw new MigrationException(message, exception);
        }
    }

    private static MigrationSqlScript createMigrationSqlScript(Path releaseDirectory, Path scriptPath) {
        MigrationSqlScript script = new MigrationSqlScript();
        String fileName = scriptPath.getFileName().toString();
        // Remove .sql extension
        fileName = fileName.substring(0, fileName.length() - ".sql".length());
        String[] parts = fileName.split("-", 2);
        String version = parts[0].trim();
        String description = parts.length == 1 ? null : parts[1].trim();
        script.setRelease(releaseDirectory.getFileName().toString());
        script.setVersion(version);
        script.setDescription(description);
        script.setPath(scriptPath);
        return script;
    }

    public String getScript(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            String message = "Failed to retrieve the migration script contents: " + path + ".";
            throw new MigrationException(message, exception);
        }
    }
}
