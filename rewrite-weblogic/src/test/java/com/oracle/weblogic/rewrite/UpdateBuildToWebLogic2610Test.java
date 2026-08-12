/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite;

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

import static org.openrewrite.gradle.Assertions.buildGradle;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openrewrite.maven.Assertions.pomXml;
import static org.openrewrite.properties.Assertions.properties;

class UpdateBuildToWebLogic2610Test implements RewriteTest {

    private Recipe recipe() {
        return Environment.builder()
                .scanRuntimeClasspath("com.oracle.weblogic")
                .build()
                .activateRecipes("com.oracle.weblogic.rewrite.UpdateBuildToWebLogic2610");
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

    @DocumentExample
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
    void updatesDirectWebLogicDependencyVersion() {
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
                  <dependencies>
                      <dependency>
                          <groupId>com.oracle.weblogic</groupId>
                          <artifactId>weblogic-server-pom</artifactId>
                          <version>15.1.1-0-0</version>
                      </dependency>
                  </dependencies>
              </project>
              """,
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.mycompany.app</groupId>
                  <artifactId>my-app</artifactId>
                  <version>1</version>
                  <dependencies>
                      <dependency>
                          <groupId>com.oracle.weblogic</groupId>
                          <artifactId>weblogic-server-pom</artifactId>
                          <version>26.1.0-0-0</version>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void updatesDirectGradleWebLogicDependencyVersion() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .executionContext(localMavenExecutionContext()),
          buildGradle(
            """
              plugins {
                  id 'java'
              }

              repositories {
                  mavenCentral()
              }

              dependencies {
                  implementation 'com.oracle.weblogic:weblogic-server-pom:15.1.1-0-0'
              }
              """,
            """
              plugins {
                  id 'java'
              }

              repositories {
                  mavenCentral()
              }

              dependencies {
                  implementation 'com.oracle.weblogic:weblogic-server-pom:26.1.0-0-0'
              }
              """
          )
        );
    }

    @Test
    void updatesGradlePropertyReferencedByWebLogicDependency() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .executionContext(localMavenExecutionContext()),
          properties(
            "weblogicVersion=15.1.1-0-0",
            "weblogicVersion=26.1.0-0-0",
            source -> source.path("gradle.properties")
          ),
          buildGradle(
            """
              plugins {
                  id 'java'
              }

              repositories {
                  mavenCentral()
              }

              dependencies {
                  implementation "com.oracle.weblogic:weblogic-server-pom:${weblogicVersion}"
              }
              """
          )
        );
    }

    @Test
    void updatesManagedWebLogicDependencyVersion() {
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
                  <dependencyManagement>
                      <dependencies>
                          <dependency>
                              <groupId>com.oracle.weblogic</groupId>
                              <artifactId>weblogic-server-pom</artifactId>
                              <version>15.1.1-0-0</version>
                          </dependency>
                      </dependencies>
                  </dependencyManagement>
              </project>
              """,
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.mycompany.app</groupId>
                  <artifactId>my-app</artifactId>
                  <version>1</version>
                  <dependencyManagement>
                      <dependencies>
                          <dependency>
                              <groupId>com.oracle.weblogic</groupId>
                              <artifactId>weblogic-server-pom</artifactId>
                              <version>26.1.0-0-0</version>
                          </dependency>
                      </dependencies>
                  </dependencyManagement>
              </project>
              """
          )
        );
    }

    @Test
    void updatesCustomPropertyReferencedByWebLogicDependency() {
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
                      <wls.server.version>15.1.1-0-0</wls.server.version>
                  </properties>
                  <dependencies>
                      <dependency>
                          <groupId>com.oracle.weblogic</groupId>
                          <artifactId>weblogic-server-pom</artifactId>
                          <version>${wls.server.version}</version>
                      </dependency>
                  </dependencies>
              </project>
              """,
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.mycompany.app</groupId>
                  <artifactId>my-app</artifactId>
                  <version>1</version>
                  <properties>
                      <wls.server.version>26.1.0-0-0</wls.server.version>
                  </properties>
                  <dependencies>
                      <dependency>
                          <groupId>com.oracle.weblogic</groupId>
                          <artifactId>weblogic-server-pom</artifactId>
                          <version>${wls.server.version}</version>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void updatesParentPropertyUsedByChildModule() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .executionContext(localMavenExecutionContext()),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.mycompany.app</groupId>
                  <artifactId>my-parent</artifactId>
                  <version>1</version>
                  <packaging>pom</packaging>
                  <properties>
                      <weblogic.version>15.1.1-0-0</weblogic.version>
                  </properties>
                  <modules>
                      <module>service</module>
                  </modules>
              </project>
              """,
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.mycompany.app</groupId>
                  <artifactId>my-parent</artifactId>
                  <version>1</version>
                  <packaging>pom</packaging>
                  <properties>
                      <weblogic.version>26.1.0-0-0</weblogic.version>
                  </properties>
                  <modules>
                      <module>service</module>
                  </modules>
              </project>
              """
          ),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                      <groupId>com.mycompany.app</groupId>
                      <artifactId>my-parent</artifactId>
                      <version>1</version>
                  </parent>
                  <artifactId>service</artifactId>
                  <dependencies>
                      <dependency>
                          <groupId>com.oracle.weblogic</groupId>
                          <artifactId>weblogic-server-pom</artifactId>
                          <version>${weblogic.version}</version>
                      </dependency>
                  </dependencies>
              </project>
              """,
            source -> source.path("service/pom.xml")
          )
        );
    }

    @Test
    void updatesWebLogicMavenPluginVersion() {
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
                  <build>
                      <plugins>
                          <plugin>
                              <groupId>com.oracle.weblogic</groupId>
                              <artifactId>weblogic-maven-plugin</artifactId>
                              <version>15.1.1-0-0</version>
                          </plugin>
                      </plugins>
                  </build>
              </project>
              """,
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.mycompany.app</groupId>
                  <artifactId>my-app</artifactId>
                  <version>1</version>
                  <build>
                      <plugins>
                          <plugin>
                              <groupId>com.oracle.weblogic</groupId>
                              <artifactId>weblogic-maven-plugin</artifactId>
                              <version>26.1.0-0-0</version>
                          </plugin>
                      </plugins>
                  </build>
              </project>
              """
          )
        );
    }

    @Test
    void updatesWebLogicArchetypeParentVersion() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .executionContext(localMavenExecutionContext()),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                      <groupId>com.oracle.weblogic.archetype</groupId>
                      <artifactId>wls-common</artifactId>
                      <version>15.1.1-0-0</version>
                  </parent>
                  <artifactId>my-app</artifactId>
              </project>
              """,
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                      <groupId>com.oracle.weblogic.archetype</groupId>
                      <artifactId>wls-common</artifactId>
                      <version>26.1.0-0-0</version>
                  </parent>
                  <artifactId>my-app</artifactId>
              </project>
              """
          )
        );
    }

    @Test
    void doesNotChangeUnrelatedOracleDependency() {
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
                  <dependencies>
                      <dependency>
                          <groupId>com.oracle.database.jdbc</groupId>
                          <artifactId>ojdbc11</artifactId>
                          <version>23.5.0.24.07</version>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void includesExistingMavenWrapperUpdate() {
        assertTrue(recipe().getRecipeList().stream()
                .anyMatch(childRecipe -> childRecipe.getName()
                        .equals("com.oracle.weblogic.rewrite.UpdateExistingMavenWrapperFor2610")));
    }
}
