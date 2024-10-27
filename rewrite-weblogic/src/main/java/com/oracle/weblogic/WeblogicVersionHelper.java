/*
 * Copyright (c) 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic;

import org.openrewrite.ExecutionContext;
import org.openrewrite.maven.MavenDownloadingException;
import org.openrewrite.maven.internal.MavenPomDownloader;
import org.openrewrite.maven.tree.GroupArtifact;
import org.openrewrite.maven.tree.MavenMetadata;
import org.openrewrite.maven.tree.MavenRepository;
import org.openrewrite.semver.LatestRelease;
import org.openrewrite.semver.Semver;
import org.openrewrite.semver.VersionComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static java.util.Collections.*;
import static org.openrewrite.Tree.randomId;

public final class WeblogicVersionHelper {

    private static final String GROUP_ID = "com.oracle.weblogic";
    private static final String ARTIFACT_ID = "weblogic";
    private static final LatestRelease LATEST_RELEASE = new LatestRelease(null);

    public static final MavenRepository GRADLE_PLUGIN_REPO = new MavenRepository("gradle-plugins", "https://plugins.gradle.org/m2/", "true", "false", true, null, null, true);


    public static Optional<String> getNewerVersion(String versionPattern, String currentVersion, ExecutionContext ctx) throws MavenDownloadingException {
        VersionComparator versionComparator = Semver.validate(versionPattern, null).getValue();
        assert versionComparator != null;

        MavenMetadata mavenMetadata = new MavenPomDownloader(emptyMap(), ctx)
                .downloadMetadata(new GroupArtifact(GROUP_ID, ARTIFACT_ID), null, emptyList());

        Collection<String> availableVersions = new ArrayList<>();
        for (String v : mavenMetadata.getVersioning().getVersions()) {
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
