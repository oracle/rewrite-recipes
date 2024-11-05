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

public final class WeblogicVersionHelper {

    private static final LatestRelease LATEST_RELEASE = new LatestRelease(null);

    // Local supported versions list of Weblogic
    public static final List<String> LOCAL_VERSIONS = List.of(
            "14.1.1-0-0", "14.1.2-0-0", "15.1.1-0-0"
    );

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
