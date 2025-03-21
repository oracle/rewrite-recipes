/*
 * Copyright (c) 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite;

import org.openrewrite.ExecutionContext;
import org.openrewrite.semver.LatestRelease;
import org.openrewrite.semver.Semver;
import org.openrewrite.semver.VersionComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for WebLogic version management.
 */
public final class WeblogicVersionHelper {

    private static final LatestRelease LATEST_RELEASE = new LatestRelease(null);

    /**
     * A list of locally supported WebLogic versions.
     */
    public static final List<String> LOCAL_VERSIONS = List.of(
            "14.1.1-0-0", "14.1.2-0-0", "15.1.1-0-0"
    );

    /**
     * Finds a newer version of WebLogic that matches the given version pattern and is newer than the current version.
     *
     * @param versionPattern   A semver pattern to match the desired version.
     * @param currentVersion   The current version of WebLogic.
     * @param ctx              The execution context for logging or additional processing.
     * @return An {@link Optional} containing the newest matching version, or empty if no newer version is found.
     */
    public static Optional<String> getNewerVersion(String versionPattern, String currentVersion, ExecutionContext ctx) {
        VersionComparator versionComparator = Semver.validate(versionPattern, null).getValue();
        assert versionComparator != null;

        Collection<String> availableVersions = new ArrayList<>();
        for (String v : LOCAL_VERSIONS) {
            if (versionComparator.isValid(null, v)) {
                availableVersions.add(v);
            }
        }

        return availableVersions.stream()
                .filter(v -> LATEST_RELEASE.compare(null, currentVersion, v) < 0)
                .max(LATEST_RELEASE);
    }

    private WeblogicVersionHelper() {
    }
}
