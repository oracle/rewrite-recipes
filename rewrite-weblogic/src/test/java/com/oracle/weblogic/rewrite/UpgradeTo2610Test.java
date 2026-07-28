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
import org.openrewrite.maven.tree.MavenRepository;
import org.openrewrite.test.RewriteTest;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openrewrite.maven.Assertions.pomXml;

class UpgradeTo2610Test implements RewriteTest {

    private Recipe recipe() {
        return Environment.builder()
                .scanRuntimeClasspath("com.oracle.weblogic")
                .build()
                .activateRecipes("com.oracle.weblogic.rewrite.UpgradeTo2610");
    }

    private ExecutionContext localMavenExecutionContext() {
        ExecutionContext executionContext = new InMemoryExecutionContext(t -> {
            throw new RuntimeException("Rewrite error", t);
        });
        MavenExecutionContextView mavenExecutionContext = MavenExecutionContextView.view(executionContext);
        MavenRepository localRepository = MavenRepository.builder()
                .id("central")
                .uri(new File("src/test/resources/test-repo").getAbsoluteFile().toURI().toString())
                .build();
        mavenExecutionContext.setRepositories(Collections.singletonList(localRepository));
        return MavenWrapperTestExecutionContext.configure(mavenExecutionContext);
    }

    @Test
    void includesCoreMigrationRecipes() {
        assertEquals(
                Arrays.asList(
                        "com.oracle.weblogic.rewrite.OutputRecipeVersion",
                        "com.oracle.weblogic.rewrite.UpdateBuildToWebLogic2610",
                        "com.oracle.weblogic.rewrite.MigrateWebLogicSchemasTo2610",
                        "com.oracle.weblogic.rewrite.NormalizeWebLogic2610DependencyScopes",
                        "com.oracle.weblogic.rewrite.ReportDeprecatedOrRemoved2610"),
                recipe().getRecipeList().stream()
                        .map(Recipe::getName)
                        .collect(Collectors.toList()));
    }

    @Test
    void updatesOlderWebLogicVersionProperty() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .executionContext(localMavenExecutionContext()),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.mycompany.app</groupId>
                  <artifactId>my-app</artifactId>
                  <version>1</version>
                  <properties>
                      <weblogic.version>15.1.1-0-0</weblogic.version>
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
                      <weblogic.version>26.1.0-0-0</weblogic.version>
                  </properties>
              </project>
              """
          )
        );
    }

    @Test
    void doesNotChangeUnrelatedMavenProject() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .executionContext(localMavenExecutionContext()),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.mycompany.app</groupId>
                  <artifactId>my-app</artifactId>
                  <version>1</version>
              </project>
              """
          )
        );
    }
}
