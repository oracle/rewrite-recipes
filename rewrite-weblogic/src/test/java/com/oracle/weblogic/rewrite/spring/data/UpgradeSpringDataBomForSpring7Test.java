/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite.spring.data;

import org.junit.jupiter.api.Test;
import org.openrewrite.ExecutionContext;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.config.Environment;
import org.openrewrite.maven.MavenExecutionContextView;
import org.openrewrite.maven.tree.MavenRepository;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import java.io.File;
import java.util.Collections;

import static org.openrewrite.maven.Assertions.pomXml;

class UpgradeSpringDataBomForSpring7Test implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        ExecutionContext executionContext = new InMemoryExecutionContext(t -> {
            throw new RuntimeException("Rewrite error", t);
        });
        MavenExecutionContextView mavenExecutionContext = MavenExecutionContextView.view(executionContext);
        MavenRepository localRepository = MavenRepository.builder()
                .id("central")
                .uri(new File("src/test/resources/test-repo").getAbsoluteFile().toURI().toString())
                .build();
        mavenExecutionContext.setRepositories(Collections.singletonList(localRepository));

        spec.recipe(Environment.builder()
                .scanRuntimeClasspath()
                .build()
                .activateRecipes(
                        "com.oracle.weblogic.rewrite.spring.data.UpgradeSpringDataBomForSpring7"))
                .executionContext(mavenExecutionContext);
    }

    @Test
    void upgradesPropertyBackedSpringDataBomIdempotently() {
        rewriteRun(spec -> spec
                .cycles(2)
                .expectedCyclesThatMakeChanges(1),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>spring-data-app</artifactId>
                  <version>1.0.0</version>
                  <properties>
                      <data.release.train>2021.1.0-RC1</data.release.train>
                  </properties>
                  <dependencyManagement>
                      <dependencies>
                          <dependency>
                              <groupId>org.springframework.data</groupId>
                              <artifactId>spring-data-bom</artifactId>
                              <version>${data.release.train}</version>
                              <type>pom</type>
                              <scope>import</scope>
                          </dependency>
                      </dependencies>
                  </dependencyManagement>
              </project>
              """,
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>spring-data-app</artifactId>
                  <version>1.0.0</version>
                  <properties>
                      <data.release.train>2025.1.0</data.release.train>
                  </properties>
                  <dependencyManagement>
                      <dependencies>
                          <dependency>
                              <groupId>org.springframework.data</groupId>
                              <artifactId>spring-data-bom</artifactId>
                              <version>${data.release.train}</version>
                              <type>pom</type>
                              <scope>import</scope>
                          </dependency>
                      </dependencies>
                  </dependencyManagement>
              </project>
              """
          )
        );
    }
}
