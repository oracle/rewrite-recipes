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
import org.openrewrite.table.SearchResults;
import org.openrewrite.test.RewriteTest;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openrewrite.java.Assertions.java;
import static org.openrewrite.maven.Assertions.pomXml;
import static org.openrewrite.xml.Assertions.xml;

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
                        "com.oracle.weblogic.rewrite.ReportDeprecatedOrRemoved2610",
                        "com.oracle.weblogic.rewrite.FindWebLogic2610MigrationRisks"),
                recipe().getRecipeList().stream()
                        .map(Recipe::getName)
                        .collect(Collectors.toList()));
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
