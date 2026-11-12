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
import org.openrewrite.test.TypeValidation;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openrewrite.java.Assertions.java;
import static org.openrewrite.maven.Assertions.pomXml;
import static org.openrewrite.test.SourceSpecs.text;
import static org.openrewrite.xml.Assertions.xml;

class FindWebLogic2610MigrationRisksTest implements RewriteTest {

    private Recipe recipe() {
        return Environment.builder()
                .scanRuntimeClasspath("com.oracle.weblogic")
                .build()
                .activateRecipes("com.oracle.weblogic.rewrite.FindWebLogic2610MigrationRisks");
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

    @Test
    void reportsJavaSecurityManagerStartupOption() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .dataTable(SearchResults.Row.class,
                        rows -> assertRisk(rows,
                                "-Djava.security.manager",
                                "com.oracle.weblogic.rewrite.FindJavaSecurityManagerStartupOption2610")),
          text(
            """
              JAVA_OPTIONS="${JAVA_OPTIONS} -Djava.security.manager"
              """,
            source -> source.path("bin/startWebLogic.sh")
          )
        );
    }

    @Test
    void doesNotReportUnrelatedStartupOptions() {
        rewriteRun(spec -> spec.recipe(recipe()),
          text(
            """
              JAVA_OPTIONS="${JAVA_OPTIONS} -Xms512m -Xmx1024m"
              """,
            source -> source.path("bin/startWebLogic.sh")
          )
        );
    }

    @Test
    void reportsJavaSecurityPolicyStartupOption() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .dataTable(SearchResults.Row.class,
                        rows -> assertRisk(rows,
                                "-Djava.security.policy",
                                "com.oracle.weblogic.rewrite.FindJavaSecurityPolicyStartupOption2610")),
          text(
            """
              JAVA_OPTIONS="${JAVA_OPTIONS} -Djava.security.policy=/opt/app/security/application.policy"
              """,
            source -> source.path("bin/startWebLogic.sh")
          )
        );
    }

    @Test
    void reportsApplicationSecurityPolicyFile() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .dataTable(SearchResults.Row.class,
                        rows -> assertRisk(rows,
                                "Application security policy file",
                                "com.oracle.weblogic.rewrite.FindApplicationSecurityPolicyFiles2610")),
          text(
            """
              grant {
                  permission java.io.FilePermission "/opt/app/-", "read";
              };
              """,
            source -> source.path("config/application.policy")
          )
        );
    }

    @Test
    void doesNotReportPolicyLikeTextInNonPolicyFile() {
        rewriteRun(spec -> spec.recipe(recipe()),
          text(
            """
              grant {
                  permission java.io.FilePermission "/opt/app/-", "read";
              };
              """,
            source -> source.path("config/application-policy.txt")
          )
        );
    }

    @Test
    void reportsCustomSecurityManagerImplementation() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .dataTable(SearchResults.Row.class,
                        rows -> assertRisk(rows,
                                "java.lang.SecurityManager",
                                "com.oracle.weblogic.rewrite.FindCustomSecurityManagerImplementations2610")),
          java(
            """
              package com.example.security;

              class ApplicationSecurityManager extends SecurityManager {
              }
              """
          )
        );
    }

    @Test
    void reportsCompileScopedWebLogicDependency() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .executionContext(localMavenExecutionContext())
                .dataTable(SearchResults.Row.class,
                        rows -> assertRisk(rows,
                                "Compile-scoped WebLogic dependency",
                                "com.oracle.weblogic.rewrite.FindCompileScopedWebLogicDependencies2610")),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>example-app</artifactId>
                  <version>1.0.0</version>
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
    void reportsRuntimeScopedWebLogicDependency() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .executionContext(localMavenExecutionContext())
                .dataTable(SearchResults.Row.class,
                        rows -> assertRisk(rows,
                                "Runtime-scoped WebLogic dependency",
                                "com.oracle.weblogic.rewrite.FindRuntimeScopedWebLogicDependencies2610")),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>example-app</artifactId>
                  <version>1.0.0</version>
                  <dependencies>
                      <dependency>
                          <groupId>com.oracle.weblogic</groupId>
                          <artifactId>weblogic-server-pom</artifactId>
                          <version>26.1.0-0-0</version>
                          <scope>runtime</scope>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void doesNotReportProvidedWebLogicDependencyAsPackaged() {
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
                          <groupId>com.oracle.weblogic</groupId>
                          <artifactId>weblogic-server-pom</artifactId>
                          <version>26.1.0-0-0</version>
                          <scope>provided</scope>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void reportsPreferWebInfClassesOverride() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .dataTable(SearchResults.Row.class,
                        rows -> assertRisk(rows,
                                "prefer-web-inf-classes=true",
                                "com.oracle.weblogic.rewrite.FindPreferWebInfClassesOverride2610")),
          xml(
            """
              <weblogic-web-app>
                  <container-descriptor>
                      <prefer-web-inf-classes>true</prefer-web-inf-classes>
                  </container-descriptor>
              </weblogic-web-app>
              """,
            source -> source.path("src/main/webapp/WEB-INF/weblogic.xml")
          )
        );
    }

    @Test
    void doesNotReportDisabledPreferWebInfClassesOverride() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <weblogic-web-app>
                  <container-descriptor>
                      <prefer-web-inf-classes>false</prefer-web-inf-classes>
                  </container-descriptor>
              </weblogic-web-app>
              """,
            source -> source.path("src/main/webapp/WEB-INF/weblogic.xml")
          )
        );
    }

    @Test
    void reportsPreferApplicationPackagesOverride() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .dataTable(SearchResults.Row.class,
                        rows -> assertRisk(rows,
                                "prefer-application-packages",
                                "com.oracle.weblogic.rewrite.FindPreferApplicationPackagesOverride2610")),
          xml(
            """
              <weblogic-web-app>
                  <container-descriptor>
                      <prefer-application-packages>
                          <package-name>org.example.*</package-name>
                      </prefer-application-packages>
                  </container-descriptor>
              </weblogic-web-app>
              """,
            source -> source.path("src/main/webapp/WEB-INF/weblogic.xml")
          )
        );
    }

    @Test
    void reportsPreferApplicationResourcesOverride() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .dataTable(SearchResults.Row.class,
                        rows -> assertRisk(rows,
                                "prefer-application-resources",
                                "com.oracle.weblogic.rewrite.FindPreferApplicationResourcesOverride2610")),
          xml(
            """
              <weblogic-web-app>
                  <container-descriptor>
                      <prefer-application-resources>
                          <resource-name>META-INF/services/*</resource-name>
                      </prefer-application-resources>
                  </container-descriptor>
              </weblogic-web-app>
              """,
            source -> source.path("src/main/webapp/WEB-INF/weblogic.xml")
          )
        );
    }

    @Test
    void doesNotReportAbsentPreferApplicationOverrides() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <weblogic-web-app>
                  <container-descriptor>
                      <prefer-web-inf-classes>false</prefer-web-inf-classes>
                  </container-descriptor>
              </weblogic-web-app>
              """,
            source -> source.path("src/main/webapp/WEB-INF/weblogic.xml")
          )
        );
    }

    @Test
    void reportsNonTransactionalJdbcDataSource() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .dataTable(SearchResults.Row.class,
                        rows -> assertRisk(rows,
                                "global-transactions-protocol=None",
                                "com.oracle.weblogic.rewrite.FindNonTransactionalJdbcDataSources2610")),
          xml(
            """
              <jdbc-data-source>
                  <name>ApplicationDataSource</name>
                  <jdbc-data-source-params>
                      <global-transactions-protocol>None</global-transactions-protocol>
                  </jdbc-data-source-params>
              </jdbc-data-source>
              """,
            source -> source.path("src/main/resources/ApplicationDataSource-jdbc.xml")
          )
        );
    }

    @Test
    void reportsOnePhaseCommitJdbcDataSource() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .dataTable(SearchResults.Row.class,
                        rows -> assertRisk(rows,
                                "global-transactions-protocol=OnePhaseCommit",
                                "com.oracle.weblogic.rewrite.FindOnePhaseCommitJdbcDataSources2610")),
          xml(
            """
              <jdbc-data-source>
                  <name>ApplicationDataSource</name>
                  <jdbc-data-source-params>
                      <global-transactions-protocol>OnePhaseCommit</global-transactions-protocol>
                  </jdbc-data-source-params>
              </jdbc-data-source>
              """,
            source -> source.path("src/main/resources/ApplicationDataSource-jdbc.xml")
          )
        );
    }

    @Test
    void reportsEmulateTwoPhaseCommitJdbcDataSource() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .dataTable(SearchResults.Row.class,
                        rows -> assertRisk(rows,
                                "global-transactions-protocol=EmulateTwoPhaseCommit",
                                "com.oracle.weblogic.rewrite.FindEmulateTwoPhaseCommitJdbcDataSources2610")),
          xml(
            """
              <jdbc-data-source>
                  <name>ApplicationDataSource</name>
                  <jdbc-data-source-params>
                      <global-transactions-protocol>EmulateTwoPhaseCommit</global-transactions-protocol>
                  </jdbc-data-source-params>
              </jdbc-data-source>
              """,
            source -> source.path("src/main/resources/ApplicationDataSource-jdbc.xml")
          )
        );
    }

    @Test
    void reportsLoggingLastResourceJdbcDataSource() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .dataTable(SearchResults.Row.class,
                        rows -> assertRisk(rows,
                                "global-transactions-protocol=LoggingLastResource",
                                "com.oracle.weblogic.rewrite.FindLoggingLastResourceJdbcDataSources2610")),
          xml(
            """
              <jdbc-data-source>
                  <name>ApplicationDataSource</name>
                  <jdbc-data-source-params>
                      <global-transactions-protocol>LoggingLastResource</global-transactions-protocol>
                  </jdbc-data-source-params>
              </jdbc-data-source>
              """,
            source -> source.path("src/main/resources/ApplicationDataSource-jdbc.xml")
          )
        );
    }

    @Test
    void doesNotReportTwoPhaseCommitJdbcDataSource() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <jdbc-data-source>
                  <name>ApplicationDataSource</name>
                  <jdbc-data-source-params>
                      <global-transactions-protocol>TwoPhaseCommit</global-transactions-protocol>
                  </jdbc-data-source-params>
              </jdbc-data-source>
              """,
            source -> source.path("src/main/resources/ApplicationDataSource-jdbc.xml")
          )
        );
    }

    @Test
    void doesNotDuplicateSerializedMigrationRiskMarkers() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .dataTable(SearchResults.Row.class, rows -> {
                    assertRisk(rows,
                            "-Djava.security.manager",
                            "com.oracle.weblogic.rewrite.FindJavaSecurityManagerStartupOption2610");
                    assertRisk(rows,
                            "java.lang.SecurityManager",
                            "com.oracle.weblogic.rewrite.FindCustomSecurityManagerImplementations2610");
                    assertRisk(rows,
                            "global-transactions-protocol=None",
                            "com.oracle.weblogic.rewrite.FindNonTransactionalJdbcDataSources2610");
                }),
          text(
            """
              JAVA_OPTIONS="${JAVA_OPTIONS} ~~>-Djava.security.manager"
              """,
            source -> source.path("bin/startWebLogic.sh")
          ),
          java(
            """
              package com.example.security;

              /*~~>*/class ApplicationSecurityManager extends SecurityManager {
              }
              """
          ),
          xml(
            """
              <jdbc-data-source>
                  <name>ApplicationDataSource</name>
                  <jdbc-data-source-params>
                      <!--~~>--><global-transactions-protocol>None</global-transactions-protocol>
                  </jdbc-data-source-params>
              </jdbc-data-source>
              """,
            source -> source.path("src/main/resources/ApplicationDataSource-jdbc.xml")
          )
        );
    }

    @Test
    void reportsJavaxManagedBeanAnnotation() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .dataTable(SearchResults.Row.class,
                        rows -> assertManagedBeanRisk(rows, "javax.annotation.ManagedBean")),
          java(
            """
              package javax.annotation;

              public @interface ManagedBean {
              }
              """
          ),
          java(
            """
              package com.example;

              import javax.annotation.ManagedBean;

              @ManagedBean
              class LegacyBean {
              }
              """
          )
        );
    }

    @Test
    void reportsJakartaManagedBeanAnnotation() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .dataTable(SearchResults.Row.class,
                        rows -> assertManagedBeanRisk(rows, "jakarta.annotation.ManagedBean")),
          java(
            """
              package jakarta.annotation;

              public @interface ManagedBean {
              }
              """
          ),
          java(
            """
              package com.example;

              import jakarta.annotation.ManagedBean;

              @ManagedBean
              class LegacyBean {
              }
              """
          )
        );
    }

    @Test
    void reportsUnresolvedJakartaManagedBeanAnnotation() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .typeValidationOptions(TypeValidation.none())
                .dataTable(SearchResults.Row.class,
                        rows -> assertManagedBeanRisk(rows, "jakarta.annotation.ManagedBean")),
          java(
            """
              package com.example;

              import jakarta.annotation.ManagedBean;

              @ManagedBean
              class LegacyBean {
              }
              """
          )
        );
    }

    @Test
    void doesNotDuplicateSerializedManagedBeanSearchMarker() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .dataTable(SearchResults.Row.class,
                        rows -> assertManagedBeanRisk(rows, "jakarta.annotation.ManagedBean")),
          java(
            """
              package jakarta.annotation;

              public @interface ManagedBean {
              }
              """
          ),
          java(
            """
              package com.example;

              import jakarta.annotation.ManagedBean;

              @/*~~>*/ManagedBean("cart")
              class LegacyBean {
              }
              """
          )
        );
    }

    @Test
    void doesNotReportCdiBeanAnnotation() {
        rewriteRun(spec -> spec.recipe(recipe()),
          java(
            """
              package jakarta.enterprise.context;

              public @interface ApplicationScoped {
              }
              """
          ),
          java(
            """
              package com.example;

              import jakarta.enterprise.context.ApplicationScoped;

              @ApplicationScoped
              class CdiBean {
              }
              """
          )
        );
    }

    private void assertManagedBeanRisk(List<SearchResults.Row> rows, String annotationType) {
        assertEquals(1, rows.size());
        SearchResults.Row risk = rows.get(0);
        assertEquals(annotationType, risk.getResult());
        assertEquals("com.oracle.weblogic.rewrite.FindManagedBeanAnnotations2610", risk.getRecipe());
        assertEquals(risk.getSourcePath(), risk.getAfterSourcePath());
    }

    private void assertRisk(List<SearchResults.Row> rows, String result, String recipeName) {
        List<SearchResults.Row> matchingRows = rows.stream()
                .filter(row -> recipeName.equals(row.getRecipe()))
                .collect(Collectors.toList());
        assertEquals(1, matchingRows.size());
        SearchResults.Row risk = matchingRows.get(0);
        assertEquals(result, risk.getResult());
        assertEquals(risk.getSourcePath(), risk.getAfterSourcePath());
    }
}
