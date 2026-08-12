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
import org.openrewrite.java.dependencies.UpgradeDependencyVersion;
import org.openrewrite.maven.MavenExecutionContextView;
import org.openrewrite.maven.tree.MavenRepository;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.openrewrite.maven.Assertions.pomXml;

class UpgradeJPATo32HibernateTo70Test implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(recipe("com.oracle.weblogic.rewrite.UpgradeJPATo32HibernateTo70ForWebLogic2610"));

        ExecutionContext executionContext = new InMemoryExecutionContext(t -> {
            throw new RuntimeException("Rewrite error", t);
        });
        MavenExecutionContextView mavenExecutionContext = MavenExecutionContextView.view(executionContext);
        MavenRepository localRepository = MavenRepository.builder()
                .id("central")
                .uri(new File("src/test/resources/test-repo").getAbsoluteFile().toURI().toString())
                .build();
        mavenExecutionContext.setRepositories(Collections.singletonList(localRepository));
        spec.executionContext(mavenExecutionContext);
    }

    private static Recipe recipe(String name) {
        return Environment.builder()
                .scanRuntimeClasspath()
                .build()
                .activateRecipes(name);
    }

    @Test
    void compositeKeepsJpaAndHibernateMigrationsSeparateAndOrdered() {
        Recipe composite = recipe("com.oracle.weblogic.rewrite.UpgradeJPATo32HibernateTo70ForWebLogic2610");

        assertEquals(
                Arrays.asList(
                        "com.oracle.weblogic.rewrite.jakarta.UpdateJakartaPersistenceTo32",
                        "com.oracle.weblogic.rewrite.hibernate.UpgradeHibernateTo70ForWebLogic2610"),
                composite.getRecipeList().stream().map(Recipe::getName).collect(Collectors.toList()));
    }

    @Test
    void hibernateRecipeUsesUpstreamMigrationAndPinsTestedVersion() {
        Recipe hibernate = recipe(
                "com.oracle.weblogic.rewrite.hibernate.UpgradeHibernateTo70ForWebLogic2610");

        assertEquals(
                Arrays.asList(
                        "org.openrewrite.hibernate.MigrateToHibernate70",
                        "org.openrewrite.java.dependencies.UpgradeDependencyVersion"),
                hibernate.getRecipeList().stream().map(Recipe::getName).collect(Collectors.toList()));

        UpgradeDependencyVersion versionPin = assertInstanceOf(
                UpgradeDependencyVersion.class, hibernate.getRecipeList().get(1));
        assertEquals("org.hibernate.orm", versionPin.getGroupId());
        assertEquals("*", versionPin.getArtifactId());
        assertEquals("7.0.8.Final", versionPin.getNewVersion());
    }

    @Test
    void upgradesJpaHibernateCoreAndMetamodelProcessorIdempotently() {
        rewriteRun(spec -> spec
                .cycles(2)
                .expectedCyclesThatMakeChanges(1),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>persistence-app</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                      <dependency>
                          <groupId>jakarta.persistence</groupId>
                          <artifactId>jakarta.persistence-api</artifactId>
                          <version>3.1.0</version>
                      </dependency>
                      <dependency>
                          <groupId>org.hibernate.orm</groupId>
                          <artifactId>hibernate-core</artifactId>
                          <version>6.6.18.Final</version>
                      </dependency>
                      <dependency>
                          <groupId>org.hibernate.orm</groupId>
                          <artifactId>hibernate-jpamodelgen</artifactId>
                          <version>6.6.18.Final</version>
                      </dependency>
                  </dependencies>
              </project>
              """,
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>persistence-app</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                      <dependency>
                          <groupId>jakarta.persistence</groupId>
                          <artifactId>jakarta.persistence-api</artifactId>
                          <version>3.2.0</version>
                      </dependency>
                      <dependency>
                          <groupId>org.hibernate.orm</groupId>
                          <artifactId>hibernate-core</artifactId>
                          <version>7.0.8.Final</version>
                      </dependency>
                      <dependency>
                          <groupId>org.hibernate.orm</groupId>
                          <artifactId>hibernate-processor</artifactId>
                          <version>7.0.8.Final</version>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void leavesNonHibernateProjectUnchanged() {
        rewriteRun(
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>unrelated-app</artifactId>
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
}
