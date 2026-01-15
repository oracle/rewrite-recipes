/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite;

import org.junit.jupiter.api.Test;
import org.openrewrite.ExecutionContext;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.config.Environment;
import org.openrewrite.maven.MavenExecutionContextView;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.maven.Assertions.pomXml;

/**
 * Verifies the composite OpenRewrite recipe com.oracle.weblogic.rewrite.UpgradeJPATo31HibernateTo66.
 * The tests run offline using a local Maven repository under src/test/resources/test-repo to provide synthetic
 * artifact metadata, making results deterministic.
 *
 * Specifically, the assertions check that the recipe:
 * - Does not change a POM with only org.slf4j:slf4j-api:1.0.0.
 * - Rewrites org.hibernate:hibernate-core:5.6.15.Final to org.hibernate.orm:hibernate-core:6.6.999.Final.
 * - Rewrites org.hibernate:hibernate-core-jakarta:5.6.12.Final to org.hibernate.orm:hibernate-core:6.6.999.Final.
 * - Rewrites org.hibernate:hibernate-ehcache:5.4.36.Final to org.hibernate.orm:hibernate-jcache:6.6.999.Final.
 * - Upgrades jakarta.persistence:jakarta.persistence-api from 2.2.0 to 3.1.0.
 * - Upgrades org.hibernate.search artifacts (hibernate-search-engine, hibernate-search-util-common)
 *   from 6.0.11.Final to 6.6.999.Final.
 * - Upgrades org.hibernate.validator:hibernate-validator from 6.0.11.Final to 6.6.999.Final.
 */
public class UpgradeJPATo31HibernateTo66Test implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        // Activate the composite recipe under test
        spec.recipe(loadRecipe());

        // Configure a local repository to use our fake artifact metadata - this makes the
        // tests completely deterministic and they won't depend on a network or remote repository
        ExecutionContext ec = new InMemoryExecutionContext(t -> {
            throw new RuntimeException("Rewrite error", t);
        });

        MavenExecutionContextView mctx = MavenExecutionContextView.view(ec);

        org.openrewrite.maven.tree.MavenRepository localRepo = org.openrewrite.maven.tree.MavenRepository.builder()
                .id("central")
                .uri(new java.io.File("src/test/resources/test-repo").getAbsoluteFile().toURI().toString())
                .build();
        mctx.setRepositories(java.util.Collections.singletonList(localRepo));

        spec.executionContext(mctx);
    }

    private static Recipe loadRecipe() {
        return Environment.builder()
                .scanRuntimeClasspath()
                .build()
                .activateRecipes("com.oracle.weblogic.rewrite.UpgradeJPATo31HibernateTo66");
    }

    @Test
    void validateRecipeDoesNotAlterNonMatchingPom() {
        rewriteRun(
            //language=xml
            pomXml(
                """
                <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>demo</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                    <dependency>
                      <groupId>org.slf4j</groupId>
                      <artifactId>slf4j-api</artifactId>
                      <version>1.0.0</version>
                    </dependency>
                  </dependencies>
                </project>
                """
            )
        );
    }

    @Test
    void changeHibernateCoreGroupIdToOrm() {
        rewriteRun(
            //language=xml
            pomXml(
                """
                <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>demo</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                    <dependency>
                      <groupId>org.hibernate</groupId>
                      <artifactId>hibernate-core</artifactId>
                      <version>5.6.15.Final</version>
                    </dependency>
                  </dependencies>
                </project>
                """,
                """
                <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>demo</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                    <dependency>
                      <groupId>org.hibernate.orm</groupId>
                      <artifactId>hibernate-core</artifactId>
                      <version>6.6.999.Final</version>
                    </dependency>
                  </dependencies>
                </project>
                """
            )
        );
    }

    @Test
    void changeHibernateCoreJakartaToCoreUnderOrm() {
        rewriteRun(
            //language=xml
            pomXml(
                """
                <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>demo</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                    <dependency>
                      <groupId>org.hibernate</groupId>
                      <artifactId>hibernate-core-jakarta</artifactId>
                      <version>5.6.12.Final</version>
                    </dependency>
                  </dependencies>
                </project>
                """,
                """
                <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>demo</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                    <dependency>
                      <groupId>org.hibernate.orm</groupId>
                      <artifactId>hibernate-core</artifactId>
                      <version>6.6.999.Final</version>
                    </dependency>
                  </dependencies>
                </project>
                """
            )
        );
    }

    @Test
    void changeHibernateEhcacheToJcacheUnderOrm() {
        rewriteRun(
            //language=xml
            pomXml(
                """
                <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>demo</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                    <dependency>
                      <groupId>org.hibernate</groupId>
                      <artifactId>hibernate-ehcache</artifactId>
                      <version>5.4.36.Final</version>
                    </dependency>
                  </dependencies>
                </project>
                """,
                """
                <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>demo</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                    <dependency>
                      <groupId>org.hibernate.orm</groupId>
                      <artifactId>hibernate-jcache</artifactId>
                      <version>6.6.999.Final</version>
                    </dependency>
                  </dependencies>
                </project>
                """
            )
        );
    }

    @Test
    void upgradeJpaTo32() {
        rewriteRun(
          //language=xml
          pomXml(
            """
            <project>
              <modelVersion>4.0.0</modelVersion>
              <groupId>com.example</groupId>
              <artifactId>demo</artifactId>
              <version>1.0.0</version>
              <dependencies>
                <dependency>
                  <groupId>jakarta.persistence</groupId>
                  <artifactId>jakarta.persistence-api</artifactId>
                  <version>2.2.0</version>
                </dependency>
              </dependencies>
            </project>
            """,
            """
            <project>
              <modelVersion>4.0.0</modelVersion>
              <groupId>com.example</groupId>
              <artifactId>demo</artifactId>
              <version>1.0.0</version>
              <dependencies>
                <dependency>
                  <groupId>jakarta.persistence</groupId>
                  <artifactId>jakarta.persistence-api</artifactId>
                  <version>3.1.0</version>
                </dependency>
              </dependencies>
            </project>
            """
          )
        );
    }

    @Test
    void upgradeHibernateSearchTo66() {
        rewriteRun(
          //language=xml
          pomXml(
            """
            <project>
              <modelVersion>4.0.0</modelVersion>
              <groupId>com.example</groupId>
              <artifactId>demo</artifactId>
              <version>1.0.0</version>
              <dependencies>
                <dependency>
                  <groupId>org.hibernate.search</groupId>
                  <artifactId>hibernate-search-engine</artifactId>
                  <version>6.0.11.Final</version>
                </dependency>
                <dependency>
                  <groupId>org.hibernate.search</groupId>
                  <artifactId>hibernate-search-util-common</artifactId>
                  <version>6.0.11.Final</version>
                </dependency>
              </dependencies>
            </project>
            """,
            """
            <project>
              <modelVersion>4.0.0</modelVersion>
              <groupId>com.example</groupId>
              <artifactId>demo</artifactId>
              <version>1.0.0</version>
              <dependencies>
                <dependency>
                  <groupId>org.hibernate.search</groupId>
                  <artifactId>hibernate-search-engine</artifactId>
                  <version>6.6.999.Final</version>
                </dependency>
                <dependency>
                  <groupId>org.hibernate.search</groupId>
                  <artifactId>hibernate-search-util-common</artifactId>
                  <version>6.6.999.Final</version>
                </dependency>
              </dependencies>
            </project>
            """
          )
        );
    }

    @Test
    void upgradeHibernateValidatorTo66() {
        rewriteRun(
          //language=xml
          pomXml(
            """
            <project>
              <modelVersion>4.0.0</modelVersion>
              <groupId>com.example</groupId>
              <artifactId>demo</artifactId>
              <version>1.0.0</version>
              <dependencies>
                <dependency>
                  <groupId>org.hibernate.validator</groupId>
                  <artifactId>hibernate-validator</artifactId>
                  <version>6.0.11.Final</version>
                </dependency>
              </dependencies>
            </project>
            """,
            """
            <project>
              <modelVersion>4.0.0</modelVersion>
              <groupId>com.example</groupId>
              <artifactId>demo</artifactId>
              <version>1.0.0</version>
              <dependencies>
                <dependency>
                  <groupId>org.hibernate.validator</groupId>
                  <artifactId>hibernate-validator</artifactId>
                  <version>6.6.999.Final</version>
                </dependency>
              </dependencies>
            </project>
            """
          )
        );
    }
}
