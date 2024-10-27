/*
 * Copyright (c) 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic;

import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.maven.MavenDownloadingException;

public final class WeblogicRewriteTestVersions {
    private static final String WLS_1411_VERSION;
    private static final String WLS_1412_VERSION;
    private static final String WLS_1511_VERSION;


    static {
        WLS_1411_VERSION = resolveVersion("14.1.1", "14.1.1-0-0");
        WLS_1412_VERSION = resolveVersion("14.1.2", "14.1.2-0-0");
        WLS_1511_VERSION = resolveVersion("15.1.1", "15.1.1-0-0");
    }

    public static String getLatestWLS1411Version() {
        return WLS_1411_VERSION;
    }

    public static String getLatestWLS1412Version() {
        return WLS_1412_VERSION;
    }

    public static String getLatestWLS1511Version() {
        return WLS_1511_VERSION;
    }

    private static String resolveVersion(String versionPattern, String currentVersion) {
        try {
            return WeblogicVersionHelper.getNewerVersion(versionPattern, currentVersion, new InMemoryExecutionContext()).orElse(currentVersion);
        } catch (MavenDownloadingException e) {
            throw new IllegalStateException("Failed to resolve latest Weblogic %s version".formatted(versionPattern), e);
        }
    }

    private WeblogicRewriteTestVersions() {
    }
}
