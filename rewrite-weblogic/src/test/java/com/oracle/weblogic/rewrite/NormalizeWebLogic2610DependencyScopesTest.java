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

import static org.openrewrite.maven.Assertions.pomXml;

class NormalizeWebLogic2610DependencyScopesTest implements RewriteTest {

    private Recipe recipe() {
        return Environment.builder()
                .scanRuntimeClasspath("com.oracle.weblogic")
                .build()
                .activateRecipes("com.oracle.weblogic.rewrite.NormalizeWebLogic2610DependencyScopes");
    }

    @DocumentExample
    @Test
    void changesJakartaInjectApiToProvidedForJakartaEE11() {
        rewriteRun(spec -> spec.recipe(recipe()),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>example-app</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                      <dependency>
                          <groupId>jakarta.platform</groupId>
                          <artifactId>jakarta.jakartaee-api</artifactId>
                          <version>11.0.0</version>
                          <scope>provided</scope>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.inject</groupId>
                          <artifactId>jakarta.inject-api</artifactId>
                          <version>2.0.1</version>
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
                  <dependencies>
                      <dependency>
                          <groupId>jakarta.platform</groupId>
                          <artifactId>jakarta.jakartaee-api</artifactId>
                          <version>11.0.0</version>
                          <scope>provided</scope>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.inject</groupId>
                          <artifactId>jakarta.inject-api</artifactId>
                          <version>2.0.1</version>
                          <scope>provided</scope>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void changesServerApiScopesWhenJakartaEE11ProvidedScopeIsManaged() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .executionContext(localMavenExecutionContext()),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>managed-platform-app</artifactId>
                  <version>1.0.0</version>
                  <properties>
                      <javaee-api.version>11.0.0</javaee-api.version>
                  </properties>
                  <dependencyManagement>
                      <dependencies>
                          <dependency>
                              <groupId>jakarta.platform</groupId>
                              <artifactId>jakarta.jakartaee-api</artifactId>
                              <version>${javaee-api.version}</version>
                              <scope>provided</scope>
                          </dependency>
                      </dependencies>
                  </dependencyManagement>
                  <dependencies>
                      <dependency>
                          <groupId>jakarta.platform</groupId>
                          <artifactId>jakarta.jakartaee-api</artifactId>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.inject</groupId>
                          <artifactId>jakarta.inject-api</artifactId>
                          <version>2.0.1</version>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.ws.rs</groupId>
                          <artifactId>jakarta.ws.rs-api</artifactId>
                          <version>3.1.0</version>
                      </dependency>
                  </dependencies>
              </project>
              """,
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>managed-platform-app</artifactId>
                  <version>1.0.0</version>
                  <properties>
                      <javaee-api.version>11.0.0</javaee-api.version>
                  </properties>
                  <dependencyManagement>
                      <dependencies>
                          <dependency>
                              <groupId>jakarta.platform</groupId>
                              <artifactId>jakarta.jakartaee-api</artifactId>
                              <version>${javaee-api.version}</version>
                              <scope>provided</scope>
                          </dependency>
                      </dependencies>
                  </dependencyManagement>
                  <dependencies>
                      <dependency>
                          <groupId>jakarta.platform</groupId>
                          <artifactId>jakarta.jakartaee-api</artifactId>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.inject</groupId>
                          <artifactId>jakarta.inject-api</artifactId>
                          <version>2.0.1</version>
                          <scope>provided</scope>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.ws.rs</groupId>
                          <artifactId>jakarta.ws.rs-api</artifactId>
                          <version>3.1.0</version>
                          <scope>provided</scope>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void doesNotChangeServerApiScopesWhenManagedPlatformScopeIsCompile() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .executionContext(localMavenExecutionContext()),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>managed-platform-app</artifactId>
                  <version>1.0.0</version>
                  <dependencyManagement>
                      <dependencies>
                          <dependency>
                              <groupId>jakarta.platform</groupId>
                              <artifactId>jakarta.jakartaee-api</artifactId>
                              <version>11.0.0</version>
                              <scope>compile</scope>
                          </dependency>
                      </dependencies>
                  </dependencyManagement>
                  <dependencies>
                      <dependency>
                          <groupId>jakarta.platform</groupId>
                          <artifactId>jakarta.jakartaee-api</artifactId>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.inject</groupId>
                          <artifactId>jakarta.inject-api</artifactId>
                          <version>2.0.1</version>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.ws.rs</groupId>
                          <artifactId>jakarta.ws.rs-api</artifactId>
                          <version>3.1.0</version>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void doesNotChangeJakartaInjectApiWhenJakartaEE11IsNotProvided() {
        rewriteRun(spec -> spec.recipe(recipe()),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>example-app</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                      <dependency>
                          <groupId>jakarta.platform</groupId>
                          <artifactId>jakarta.jakartaee-api</artifactId>
                          <version>11.0.0</version>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.inject</groupId>
                          <artifactId>jakarta.inject-api</artifactId>
                          <version>2.0.1</version>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void changesJakartaRestApiToProvidedForJakartaEE11() {
        rewriteRun(spec -> spec.recipe(recipe()),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>example-app</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                      <dependency>
                          <groupId>jakarta.platform</groupId>
                          <artifactId>jakarta.jakartaee-api</artifactId>
                          <version>11.0.0</version>
                          <scope>provided</scope>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.ws.rs</groupId>
                          <artifactId>jakarta.ws.rs-api</artifactId>
                          <version>4.0.0</version>
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
                  <dependencies>
                      <dependency>
                          <groupId>jakarta.platform</groupId>
                          <artifactId>jakarta.jakartaee-api</artifactId>
                          <version>11.0.0</version>
                          <scope>provided</scope>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.ws.rs</groupId>
                          <artifactId>jakarta.ws.rs-api</artifactId>
                          <version>4.0.0</version>
                          <scope>provided</scope>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void doesNotChangeJakartaRestApiWhenJakartaEE11IsNotProvided() {
        rewriteRun(spec -> spec.recipe(recipe()),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>example-app</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                      <dependency>
                          <groupId>jakarta.platform</groupId>
                          <artifactId>jakarta.jakartaee-api</artifactId>
                          <version>11.0.0</version>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.ws.rs</groupId>
                          <artifactId>jakarta.ws.rs-api</artifactId>
                          <version>4.0.0</version>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void doesNotChangeJakartaXmlBindApiForJakartaEE11() {
        rewriteRun(spec -> spec.recipe(recipe()),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>example-app</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                      <dependency>
                          <groupId>jakarta.platform</groupId>
                          <artifactId>jakarta.jakartaee-api</artifactId>
                          <version>11.0.0</version>
                          <scope>provided</scope>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.xml.bind</groupId>
                          <artifactId>jakarta.xml.bind-api</artifactId>
                          <version>4.0.2</version>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void doesNotChangeJakartaXmlWebServicesApiForJakartaEE11() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .executionContext(localMavenExecutionContext()),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>example-app</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                      <dependency>
                          <groupId>jakarta.platform</groupId>
                          <artifactId>jakarta.jakartaee-api</artifactId>
                          <version>11.0.0</version>
                          <scope>provided</scope>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.xml.ws</groupId>
                          <artifactId>jakarta.xml.ws-api</artifactId>
                          <version>4.0.2</version>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void doesNotChangeJakartaSoapApiForJakartaEE11() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .executionContext(localMavenExecutionContext()),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>example-app</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                      <dependency>
                          <groupId>jakarta.platform</groupId>
                          <artifactId>jakarta.jakartaee-api</artifactId>
                          <version>11.0.0</version>
                          <scope>provided</scope>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.xml.soap</groupId>
                          <artifactId>jakarta.xml.soap-api</artifactId>
                          <version>3.0.2</version>
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
