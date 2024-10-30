/*
 * Copyright (c) 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.maven.Assertions.pomXml;

class UpgradeWeblogicMavenPropertyVersionTest implements RewriteTest {

    @DocumentExample
    @Test
    void changeMavenWeblogicVersion1411() {
        String latestWeblogicVersion = WeblogicRewriteTestVersions.getLatestWLS1411Version();

        rewriteRun(spec -> spec.recipe(new UpgradeWeblogicMavenPropertyVersion("14.1.1-0-0")),
            pomXml(
                """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.mycompany.app</groupId>
                    <artifactId>my-app</artifactId>
                    <version>1</version>
                    <properties>
                        <weblogic.version>12.1.3-0-0</weblogic.version>
                    </properties>
                </project>
                """,
                """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.mycompany.app</groupId>
                    <artifactId>my-app</artifactId>
                    <version>1</version>
                    <properties>
                        <weblogic.version>%s</weblogic.version>
                    </properties>
                </project>
                """.formatted(latestWeblogicVersion))
        );
    }

    @Test
    void changeMavenWeblogicVersion1412() {
        String latestWeblogicVersion = WeblogicRewriteTestVersions.getLatestWLS1412Version();

        rewriteRun(spec -> spec.recipe(new UpgradeWeblogicMavenPropertyVersion("14.1.2-0-0")),
            pomXml(
                """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.mycompany.app</groupId>
                    <artifactId>my-app</artifactId>
                    <version>1</version>
                    <properties>
                        <weblogic.version>12.1.3-0-0</weblogic.version>
                    </properties>
                </project>
                """,
                """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.mycompany.app</groupId>
                    <artifactId>my-app</artifactId>
                    <version>1</version>
                    <properties>
                        <weblogic.version>%s</weblogic.version>
                    </properties>
                </project>
                """.formatted(latestWeblogicVersion))
        );
    }

    @Test
    void changeMavenWeblogicVersion1511() {
        String latestWeblogicVersion = WeblogicRewriteTestVersions.getLatestWLS1511Version();

        rewriteRun(spec -> spec.recipe(new UpgradeWeblogicMavenPropertyVersion("15.1.1-0-0")),
            pomXml(
                """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.mycompany.app</groupId>
                    <artifactId>my-app</artifactId>
                    <version>1</version>
                    <properties>
                        <weblogic.version>14.1.2-0-0</weblogic.version>
                    </properties>
                </project>
                """,
                """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.mycompany.app</groupId>
                    <artifactId>my-app</artifactId>
                    <version>1</version>
                    <properties>
                        <weblogic.version>%s</weblogic.version>
                    </properties>
                </project>
                """.formatted(latestWeblogicVersion))
        );
    }
}
