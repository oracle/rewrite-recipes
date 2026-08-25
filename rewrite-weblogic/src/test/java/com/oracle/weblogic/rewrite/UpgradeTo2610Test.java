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
import org.openrewrite.java.JavaParser;
import org.openrewrite.maven.MavenExecutionContextView;
import org.openrewrite.maven.MavenParser;
import org.openrewrite.maven.tree.MavenRepository;
import org.openrewrite.table.SearchResults;
import org.openrewrite.test.RewriteTest;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openrewrite.java.Assertions.java;
import static org.openrewrite.java.Assertions.mavenProject;
import static org.openrewrite.java.Assertions.srcMainJava;
import static org.openrewrite.maven.Assertions.pomXml;
import static org.openrewrite.xml.Assertions.xml;

class UpgradeTo2610Test implements RewriteTest {

    private Recipe recipe() {
        return Environment.builder()
                .scanRuntimeClasspath("com.oracle.weblogic")
                .build()
                .activateRecipes("com.oracle.weblogic.rewrite.UpgradeTo2610");
    }

    private Recipe jakartaEE11ToWebLogic2610Recipe() {
        return Environment.builder()
                .scanRuntimeClasspath("com.oracle.weblogic")
                .build()
                .activateRecipes(
                        "com.oracle.weblogic.rewrite.jakarta.MigrateToJakartaEE11",
                        "com.oracle.weblogic.rewrite.UpgradeTo2610");
    }

    private Recipe standaloneApiNormalizer() {
        return Environment.builder()
                .scanRuntimeClasspath("com.oracle.weblogic")
                .build()
                .activateRecipes(
                        "com.oracle.weblogic.rewrite.NormalizeWebLogic2610StandaloneJakartaApiDependencies");
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
                        "com.oracle.weblogic.rewrite.AddWebLogic2610StandaloneJakartaApiDependencies",
                        "com.oracle.weblogic.rewrite.MigrateWebLogicSchemasTo2610",
                        "com.oracle.weblogic.rewrite.NormalizeWebLogic2610DependencyScopes",
                        "com.oracle.weblogic.rewrite.NormalizeWebLogic2610StandaloneJakartaApiDependencies",
                        "com.oracle.weblogic.rewrite.ReportDeprecatedOrRemoved2610",
                        "com.oracle.weblogic.rewrite.FindWebLogic2610MigrationRisks"),
                recipe().getRecipeList().stream()
                        .map(Recipe::getName)
                        .collect(Collectors.toList()));
    }

    @Test
    void normalizesServerApiScopesWhenProvidedPlatformScopeIsManaged() {
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
                          <groupId>jakarta.xml.bind</groupId>
                          <artifactId>jakarta.xml.bind-api</artifactId>
                          <version>4.0.2</version>
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
                          <groupId>jakarta.xml.bind</groupId>
                          <artifactId>jakarta.xml.bind-api</artifactId>
                          <version>4.0.2</version>
                          <scope>provided</scope>
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
    void migratesJakartaEE9_1StandaloneApisAndReportsRisks() {
        rewriteRun(spec -> spec
                .recipe(jakartaEE11ToWebLogic2610Recipe())
                .executionContext(localMavenExecutionContext())
                .cycles(1)
                .expectedCyclesThatMakeChanges(1)
                .parser(JavaParser.fromJavaVersion().dependsOn(
                        "package jakarta.xml.bind; public abstract class JAXBContext {}",
                        "package jakarta.xml.soap; public abstract class MessageFactory {}",
                        "package jakarta.jws; public @interface WebService {}",
                        "package jakarta.xml.ws; public abstract class Endpoint {}"))
                .dataTable(SearchResults.Row.class, rows -> {
                    assertEquals(4, rows.size());
                    assertEquals(Set.of(
                                    "jakarta.jws.WebService",
                                    "jakarta.xml.bind.JAXBContext",
                                    "jakarta.xml.soap.MessageFactory",
                                    "jakarta.xml.ws.Endpoint"),
                            rows.stream().map(SearchResults.Row::getResult).collect(Collectors.toSet()));
                    assertTrue(rows.stream().anyMatch(row -> row.getDescription().contains(
                            "jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")));
                    assertTrue(rows.stream().anyMatch(row -> row.getDescription().contains(
                            "jakarta.xml.soap:jakarta.xml.soap-api:3.0.2")));
                    assertTrue(rows.stream().anyMatch(row -> row.getDescription().contains(
                            "jakarta.jws:jakarta.jws-api:3.0.0")));
                    assertTrue(rows.stream().anyMatch(row -> row.getDescription().contains(
                            "jakarta.xml.ws:jakarta.xml.ws-api:4.0.2")));
                }),
          mavenProject("removed-api-app",
            srcMainJava(
              java(
                """
                  package com.example;

                  import jakarta.jws.WebService;
                  import jakarta.xml.bind.JAXBContext;
                  import jakarta.xml.soap.MessageFactory;
                  import jakarta.xml.ws.Endpoint;

                  @WebService
                  class OptionalXmlServicesUsage {
                      JAXBContext bindingContext;
                      MessageFactory messageFactory;
                      Endpoint endpoint;
                  }
                  """
              )
            ),
            pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>removed-api-app</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                      <dependency>
                          <groupId>jakarta.platform</groupId>
                          <artifactId>jakarta.jakartaee-api</artifactId>
                          <version>9.1.0</version>
                          <scope>provided</scope>
                      </dependency>
                  </dependencies>
              </project>
              """,
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>removed-api-app</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                      <dependency>
                          <groupId>jakarta.jws</groupId>
                          <artifactId>jakarta.jws-api</artifactId>
                          <version>3.0.0</version>
                          <scope>provided</scope>
                      </dependency>
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
                          <scope>provided</scope>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.xml.soap</groupId>
                          <artifactId>jakarta.xml.soap-api</artifactId>
                          <version>3.0.2</version>
                          <scope>provided</scope>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.xml.ws</groupId>
                          <artifactId>jakarta.xml.ws-api</artifactId>
                          <version>4.0.2</version>
                          <scope>provided</scope>
                      </dependency>
                  </dependencies>
              </project>
              """
            )
          )
        );
    }

    @Test
    void restoresWebLogicStandaloneApisAfterGenericSecondPass() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .parser(MavenParser.builder().skipDependencyResolution(true)),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>removed-api-app</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                      <dependency>
                          <groupId>com.sun.xml.ws</groupId>
                          <artifactId>jaxws-rt</artifactId>
                          <version>4.0.5</version>
                          <scope>provided</scope>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.jws</groupId>
                          <artifactId>jakarta.jws-api</artifactId>
                          <version>3.0.0</version>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.platform</groupId>
                          <artifactId>jakarta.jakartaee-api</artifactId>
                          <version>11.0.0</version>
                          <scope>provided</scope>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.xml.bind</groupId>
                          <artifactId>jakarta.xml.bind-api</artifactId>
                          <version>4.0.5</version>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.xml.soap</groupId>
                          <artifactId>jakarta.xml.soap-api</artifactId>
                          <version>3.0.2</version>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.xml.ws</groupId>
                          <artifactId>jakarta.xml.ws-api</artifactId>
                          <version>4.0.3</version>
                      </dependency>
                  </dependencies>
              </project>
              """,
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>removed-api-app</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                      <dependency>
                          <groupId>jakarta.jws</groupId>
                          <artifactId>jakarta.jws-api</artifactId>
                          <version>3.0.0</version>
                          <scope>provided</scope>
                      </dependency>
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
                          <scope>provided</scope>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.xml.soap</groupId>
                          <artifactId>jakarta.xml.soap-api</artifactId>
                          <version>3.0.2</version>
                          <scope>provided</scope>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.xml.ws</groupId>
                          <artifactId>jakarta.xml.ws-api</artifactId>
                          <version>4.0.2</version>
                          <scope>provided</scope>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void leavesApplicationPackagedApisAndRuntimeUnchanged() {
        rewriteRun(spec -> spec
                .recipe(standaloneApiNormalizer())
                .parser(MavenParser.builder().skipDependencyResolution(true)),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>packaged-platform-app</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                      <dependency>
                          <groupId>jakarta.platform</groupId>
                          <artifactId>jakarta.jakartaee-api</artifactId>
                          <version>11.0.0</version>
                      </dependency>
                      <dependency>
                          <groupId>com.sun.xml.ws</groupId>
                          <artifactId>jaxws-rt</artifactId>
                          <version>4.0.5</version>
                      </dependency>
                      <dependency>
                          <groupId>jakarta.xml.bind</groupId>
                          <artifactId>jakarta.xml.bind-api</artifactId>
                          <version>4.0.5</version>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void reportsManagedBeanRiskWithoutChangingSource() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .executionContext(localMavenExecutionContext())
                .dataTable(SearchResults.Row.class, rows -> {
                    assertEquals(1, rows.size());
                    assertEquals("jakarta.annotation.ManagedBean", rows.get(0).getResult());
                    assertEquals("com.oracle.weblogic.rewrite.FindManagedBeanAnnotations2610",
                            rows.get(0).getRecipe());
                }),
          java(
            """
              package jakarta.annotation;

              public @interface ManagedBean {
                  String value() default "";
              }
              """
          ),
          java(
            """
              package com.example;

              import jakarta.annotation.ManagedBean;

              @ManagedBean("cart")
              class ManagedCart {
              }
              """
          )
        );
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
    void isIdempotentAcrossBuildAndDescriptorMigration() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .executionContext(localMavenExecutionContext())
                .cycles(2)
                .expectedCyclesThatMakeChanges(1),
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
          ),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <weblogic-web-app xmlns="http://xmlns.oracle.com/weblogic/weblogic-web-app"
                                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                xsi:schemaLocation="http://xmlns.oracle.com/weblogic/weblogic-web-app http://xmlns.oracle.com/weblogic/weblogic-web-app/1.9/weblogic-web-app.xsd">
                  <context-root>my-app</context-root>
              </weblogic-web-app>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <weblogic-web-app xmlns="http://xmlns.oracle.com/weblogic/weblogic-web-app"
                                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                xsi:schemaLocation="http://xmlns.oracle.com/weblogic/weblogic-web-app http://xmlns.oracle.com/weblogic/weblogic-web-app/2.0/weblogic-web-app.xsd">
                  <context-root>my-app</context-root>
              </weblogic-web-app>
              """,
            source -> source.path("src/main/webapp/WEB-INF/weblogic.xml")
          )
        );
    }

    @Test
    void changesOnlyTargetedFilesInMixedProject() {
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
                  <dependencies>
                      <dependency>
                          <groupId>com.oracle.database.jdbc</groupId>
                          <artifactId>ojdbc11</artifactId>
                          <version>23.5.0.24.07</version>
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
                      <weblogic.version>26.1.0-0-0</weblogic.version>
                  </properties>
                  <dependencies>
                      <dependency>
                          <groupId>com.oracle.database.jdbc</groupId>
                          <artifactId>ojdbc11</artifactId>
                          <version>23.5.0.24.07</version>
                      </dependency>
                  </dependencies>
              </project>
              """
          ),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <application-configuration>
                  <product-version>15.1.1-0-0</product-version>
                  <implementation-class>javax.example.Type</implementation-class>
              </application-configuration>
              """,
            source -> source.path("src/main/resources/application-configuration.xml")
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
