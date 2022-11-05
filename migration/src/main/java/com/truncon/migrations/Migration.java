package com.truncon.migrations;

import java.time.Duration;
import java.time.Instant;

public final class Migration {
    private Long id;
    private String type; // SQL or Java
    private String script; // Path to SQL or Java migration file
    private String release; // Which release of the software was the migration associated with
    private String version; // Timestamp or ordering key
    private String description;
    private Long checksum; // The file checksum when the SQL migration ran
    private Instant installationDateTime; // When was the migration installed
    private Long installedByUserId; // Who installed it
    private Duration executionDuration; // How long did the migration take to run?
    private boolean success; // Was the migration successful?

    public Migration() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
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

    public Long getChecksum() {
        return checksum;
    }

    public void setChecksum(Long checksum) {
        this.checksum = checksum;
    }

    public Instant getInstallationDateTime() {
        return installationDateTime;
    }

    public void setInstallationDateTime(Instant installationDateTime) {
        this.installationDateTime = installationDateTime;
    }

    public Long getInstalledByUserId() {
        return installedByUserId;
    }

    public void setInstalledByUserId(Long installedByUserId) {
        this.installedByUserId = installedByUserId;
    }

    public Duration getExecutionDuration() {
        return executionDuration;
    }

    public void setExecutionDuration(Duration executionDuration) {
        this.executionDuration = executionDuration;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
