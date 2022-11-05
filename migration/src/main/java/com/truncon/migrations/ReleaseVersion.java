package com.truncon.migrations;

import java.util.Comparator;
import java.util.Objects;

public final class ReleaseVersion {
    public static final Comparator<ReleaseVersion> COMPARATOR = Comparator
        .comparing(ReleaseVersion::getRelease)
        .thenComparing(ReleaseVersion::getVersion);

    private final String release;
    private final String version;

    public ReleaseVersion(String release, String version) {
        this.release = release;
        this.version = version;
    }

    public String getRelease() {
        return release;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReleaseVersion that = (ReleaseVersion) o;
        return release.equals(that.release) && version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(release, version);
    }
}
