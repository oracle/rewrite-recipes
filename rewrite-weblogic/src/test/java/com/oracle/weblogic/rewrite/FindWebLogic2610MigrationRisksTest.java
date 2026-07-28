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
import java.util.Collections;

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
        rewriteRun(spec -> spec.recipe(recipe()),
          text(
            """
              JAVA_OPTIONS="${JAVA_OPTIONS} -Djava.security.manager"
              """,
            """
              JAVA_OPTIONS="${JAVA_OPTIONS} ~~>-Djava.security.manager"
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
        rewriteRun(spec -> spec.recipe(recipe()),
          text(
            """
              JAVA_OPTIONS="${JAVA_OPTIONS} -Djava.security.policy=/opt/app/security/application.policy"
              """,
            """
              JAVA_OPTIONS="${JAVA_OPTIONS} ~~>-Djava.security.policy=/opt/app/security/application.policy"
              """,
            source -> source.path("bin/startWebLogic.sh")
          )
        );
    }

    @Test
    void reportsApplicationSecurityPolicyFile() {
        rewriteRun(spec -> spec.recipe(recipe()),
          text(
            """
              grant {
                  permission java.io.FilePermission "/opt/app/-", "read";
              };
              """,
            """
              ~~>grant {
                  permission java.io.FilePermission "/opt/app/-", "read";
              };
              """,
            source -> source.path("config/application.policy")
          )
        );
    }

    @Test
    void reportsCustomSecurityManagerImplementation() {
        rewriteRun(spec -> spec.recipe(recipe()),
          java(
            """
              package com.example.security;

              class ApplicationSecurityManager extends SecurityManager {
              }
              """,
            """
              package com.example.security;

              /*~~>*/class ApplicationSecurityManager extends SecurityManager {
              }
              """
          )
        );
    }

    @Test
    void reportsCompileScopedWebLogicDependency() {
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
                      <!--~~(com.oracle.weblogic:weblogic-server-pom:26.1.0-0-0)~~>--><dependency>
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
                          <scope>runtime</scope>
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
                      <!--~~(com.oracle.weblogic:weblogic-server-pom:26.1.0-0-0)~~>--><dependency>
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
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <weblogic-web-app>
                  <container-descriptor>
                      <prefer-web-inf-classes>true</prefer-web-inf-classes>
                  </container-descriptor>
              </weblogic-web-app>
              """,
            """
              <weblogic-web-app>
                  <container-descriptor>
                      <!--~~>--><prefer-web-inf-classes>true</prefer-web-inf-classes>
                  </container-descriptor>
              </weblogic-web-app>
              """,
            source -> source.path("src/main/webapp/WEB-INF/weblogic.xml")
          )
        );
    }

    @Test
    void reportsPreferApplicationPackagesOverride() {
        rewriteRun(spec -> spec.recipe(recipe()),
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
            """
              <weblogic-web-app>
                  <container-descriptor>
                      <!--~~>--><prefer-application-packages>
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
        rewriteRun(spec -> spec.recipe(recipe()),
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
            """
              <weblogic-web-app>
                  <container-descriptor>
                      <!--~~>--><prefer-application-resources>
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
    void reportsNonTransactionalJdbcDataSource() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <jdbc-data-source>
                  <name>ApplicationDataSource</name>
                  <jdbc-data-source-params>
                      <global-transactions-protocol>None</global-transactions-protocol>
                  </jdbc-data-source-params>
              </jdbc-data-source>
              """,
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
    void reportsOnePhaseCommitJdbcDataSource() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <jdbc-data-source>
                  <name>ApplicationDataSource</name>
                  <jdbc-data-source-params>
                      <global-transactions-protocol>OnePhaseCommit</global-transactions-protocol>
                  </jdbc-data-source-params>
              </jdbc-data-source>
              """,
            """
              <jdbc-data-source>
                  <name>ApplicationDataSource</name>
                  <jdbc-data-source-params>
                      <!--~~>--><global-transactions-protocol>OnePhaseCommit</global-transactions-protocol>
                  </jdbc-data-source-params>
              </jdbc-data-source>
              """,
            source -> source.path("src/main/resources/ApplicationDataSource-jdbc.xml")
          )
        );
    }

    @Test
    void reportsEmulateTwoPhaseCommitJdbcDataSource() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <jdbc-data-source>
                  <name>ApplicationDataSource</name>
                  <jdbc-data-source-params>
                      <global-transactions-protocol>EmulateTwoPhaseCommit</global-transactions-protocol>
                  </jdbc-data-source-params>
              </jdbc-data-source>
              """,
            """
              <jdbc-data-source>
                  <name>ApplicationDataSource</name>
                  <jdbc-data-source-params>
                      <!--~~>--><global-transactions-protocol>EmulateTwoPhaseCommit</global-transactions-protocol>
                  </jdbc-data-source-params>
              </jdbc-data-source>
              """,
            source -> source.path("src/main/resources/ApplicationDataSource-jdbc.xml")
          )
        );
    }

    @Test
    void reportsLoggingLastResourceJdbcDataSource() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <jdbc-data-source>
                  <name>ApplicationDataSource</name>
                  <jdbc-data-source-params>
                      <global-transactions-protocol>LoggingLastResource</global-transactions-protocol>
                  </jdbc-data-source-params>
              </jdbc-data-source>
              """,
            """
              <jdbc-data-source>
                  <name>ApplicationDataSource</name>
                  <jdbc-data-source-params>
                      <!--~~>--><global-transactions-protocol>LoggingLastResource</global-transactions-protocol>
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
    void reportsJavaxManagedBeanAnnotation() {
        rewriteRun(spec -> spec.recipe(recipe()),
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
              """,
            """
              package com.example;

              import javax.annotation.ManagedBean;

              @/*~~>*/ManagedBean
              class LegacyBean {
              }
              """
          )
        );
    }

    @Test
    void reportsJakartaManagedBeanAnnotation() {
        rewriteRun(spec -> spec.recipe(recipe()),
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
              """,
            """
              package com.example;

              import jakarta.annotation.ManagedBean;

              @/*~~>*/ManagedBean
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
}
