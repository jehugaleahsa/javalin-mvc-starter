package com.truncon.migrations;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class SqlScriptSelector {
    private final MigrationRepository repository;
    private final SqlScriptLocator locator;

    @Inject
    public SqlScriptSelector(MigrationRepository repository, SqlScriptLocator locator) {
        this.repository = repository;
        this.locator = locator;
    }

    public Collection<MigrationSqlScript> getMigrationsToApply(Collection<MigrationSqlScript> scripts) {
        Collection<Migration> installed = repository.getAllMigrations();
        Map<ReleaseVersion, Migration> migrationLookup = installed.stream()
            .collect(Collectors.toMap(m -> new ReleaseVersion(m.getRelease(), m.getVersion()), m -> m));
        List<MigrationSqlScript> sortedScripts = scripts.stream()
            .sorted(MigrationSqlScript.COMPARATOR)
            .collect(Collectors.toList());
        List<MigrationSqlScript> results = new ArrayList<>();
        for (MigrationSqlScript script : sortedScripts) {
            ReleaseVersion releaseVersion = new ReleaseVersion(script.getRelease(), script.getVersion());
            Migration migration = migrationLookup.get(releaseVersion);
            if (migration == null) {
                // This script has never been run before.
                results.add(script);
            } else if (!migration.isSuccess()) {
                // This script previously failed to run.
                script.setMigration(migration);
                results.add(script);
            } else {
                long newCheckSum = Checksums.getChecksum(locator.getScript(script.getPath()));
                if (migration.getChecksum() != newCheckSum) {
                    // This script has changed since it was successfully run.
                    String message = "The migration has changed since it last ran: "
                        + script.getDisplayName()
                        + ". The previous checksum was " + migration.getChecksum()
                        + " but the current checksum is " + newCheckSum;
                    throw new MigrationException(message);
                }
            }
        }
        return results;
    }
}
