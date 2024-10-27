/*
 * Copyright (c) 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openrewrite.maven.MavenDownloadingException;
import org.openrewrite.test.RewriteTest;
import org.openrewrite.test.SourceSpecs;

import static org.openrewrite.properties.Assertions.properties;

public abstract class WeblogicRewriteTest implements RewriteTest {
    protected static String latestApplicationPluginVersion;
    protected static String latestWeblogicVersion;
    private SourceSpecs gradleProperties;

    @BeforeAll
    static void init() throws MavenDownloadingException {
        latestWeblogicVersion = WeblogicRewriteTestVersions.getLatestWLS1412Version();
    }

    @BeforeEach
    void initGradleProperties() {
       gradleProperties = properties("weblogicVersion=" + latestWeblogicVersion, s -> s.path("gradle.properties"));
    }

    protected SourceSpecs getGradleProperties() {
        return gradleProperties;
    }
}
