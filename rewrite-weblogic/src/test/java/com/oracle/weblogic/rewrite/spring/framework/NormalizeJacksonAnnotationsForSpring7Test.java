/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite.spring.framework;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.ExecutionContext;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.config.Environment;
import org.openrewrite.maven.MavenExecutionContextView;
import org.openrewrite.maven.tree.MavenRepository;
import org.openrewrite.test.RewriteTest;

import java.io.File;
import java.util.Collections;

import static org.openrewrite.maven.Assertions.pomXml;

class NormalizeJacksonAnnotationsForSpring7Test implements RewriteTest {

    private Recipe spring7Composite() {
        return Environment.builder()
                .scanRuntimeClasspath()
                .build()
                .activateRecipes(
                        "com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_7_0ForWebLogic2610");
    }

    @DocumentExample
    @Test
    void spring7CompositeSeparatesSourceSharedJacksonVersion() {
        rewriteRun(
          spec -> spec
                  .recipe(spring7Composite())
                  .executionContext(localMavenExecutionContext()),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>example-app</artifactId>
                  <version>1.0.0</version>
                  <properties>
                      <jackson.version>2.7.6</jackson.version>
                      <jackson.version>2.7.6</jackson.version>
                  </properties>
                  <dependencies>
                      <dependency>
                          <groupId>com.fasterxml.jackson.core</groupId>
                          <artifactId>jackson-annotations</artifactId>
                          <version>${jackson.version}</version>
                      </dependency>
                      <dependency>
                          <groupId>com.fasterxml.jackson.core</groupId>
                          <artifactId>jackson-databind</artifactId>
                          <version>${jackson.version}</version>
                      </dependency>
                  </dependencies>
              </project>
              """,
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>example-app</artifactId>
                  <version>1.0.0</version>
                  <properties>
                      <jackson.version>3.0.4</jackson.version>
                  </properties>
                  <dependencies>
                      <dependency>
                          <groupId>com.fasterxml.jackson.core</groupId>
                          <artifactId>jackson-annotations</artifactId>
                          <version>2.20</version>
                      </dependency>
                      <dependency>
                          <groupId>tools.jackson.core</groupId>
                          <artifactId>jackson-databind</artifactId>
                          <version>${jackson.version}</version>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void separatesSharedJacksonVersion() {
        rewriteRun(
          spec -> spec
                  .recipe(new NormalizeJacksonAnnotationsForSpring7())
                  .executionContext(localMavenExecutionContext()),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>example-app</artifactId>
                  <version>1.0.0</version>
                  <properties>
                      <jackson.version>3.0.4</jackson.version>
                  </properties>
                  <dependencies>
                      <dependency>
                          <groupId>com.fasterxml.jackson.core</groupId>
                          <artifactId>jackson-annotations</artifactId>
                          <version>${jackson.version}</version>
                      </dependency>
                      <dependency>
                          <groupId>tools.jackson.core</groupId>
                          <artifactId>jackson-databind</artifactId>
                          <version>${jackson.version}</version>
                      </dependency>
                  </dependencies>
              </project>
              """,
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>example-app</artifactId>
                  <version>1.0.0</version>
                  <properties>
                      <jackson.version>3.0.4</jackson.version>
                  </properties>
                  <dependencies>
                      <dependency>
                          <groupId>com.fasterxml.jackson.core</groupId>
                          <artifactId>jackson-annotations</artifactId>
                          <version>2.20</version>
                      </dependency>
                      <dependency>
                          <groupId>tools.jackson.core</groupId>
                          <artifactId>jackson-databind</artifactId>
                          <version>${jackson.version}</version>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void separatesSharedManagedJacksonVersion() {
        rewriteRun(
          spec -> spec
                  .recipe(new NormalizeJacksonAnnotationsForSpring7())
                  .executionContext(localMavenExecutionContext()),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>example-app</artifactId>
                  <version>1.0.0</version>
                  <properties>
                      <jackson.version>3.0.4</jackson.version>
                  </properties>
                  <dependencyManagement>
                      <dependencies>
                          <dependency>
                              <groupId>com.fasterxml.jackson.core</groupId>
                              <artifactId>jackson-annotations</artifactId>
                              <version>${jackson.version}</version>
                          </dependency>
                          <dependency>
                              <groupId>tools.jackson.core</groupId>
                              <artifactId>jackson-databind</artifactId>
                              <version>${jackson.version}</version>
                          </dependency>
                      </dependencies>
                  </dependencyManagement>
              </project>
              """,
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>example-app</artifactId>
                  <version>1.0.0</version>
                  <properties>
                      <jackson.version>3.0.4</jackson.version>
                  </properties>
                  <dependencyManagement>
                      <dependencies>
                          <dependency>
                              <groupId>com.fasterxml.jackson.core</groupId>
                              <artifactId>jackson-annotations</artifactId>
                              <version>2.20</version>
                          </dependency>
                          <dependency>
                              <groupId>tools.jackson.core</groupId>
                              <artifactId>jackson-databind</artifactId>
                              <version>${jackson.version}</version>
                          </dependency>
                      </dependencies>
                  </dependencyManagement>
              </project>
              """
          )
        );
    }

    @Test
    void preservesAlreadySeparatedVersions() {
        rewriteRun(
          spec -> spec
                  .recipe(new NormalizeJacksonAnnotationsForSpring7())
                  .executionContext(localMavenExecutionContext()),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>example-app</artifactId>
                  <version>1.0.0</version>
                  <properties>
                      <jackson.annotations.version>2.20</jackson.annotations.version>
                      <jackson.version>3.0.4</jackson.version>
                  </properties>
                  <dependencies>
                      <dependency>
                          <groupId>com.fasterxml.jackson.core</groupId>
                          <artifactId>jackson-annotations</artifactId>
                          <version>${jackson.annotations.version}</version>
                      </dependency>
                      <dependency>
                          <groupId>tools.jackson.core</groupId>
                          <artifactId>jackson-databind</artifactId>
                          <version>${jackson.version}</version>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
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
        return mavenExecutionContext;
    }
}
