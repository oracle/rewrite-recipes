/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite.jakarta;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openrewrite.maven.Assertions.pomXml;
import static org.openrewrite.xml.Assertions.xml;

class MigrateToJakartaEE11Test implements RewriteTest {

    private Recipe recipe() {
        return Environment.builder()
                .scanRuntimeClasspath()
                .build()
                .activateRecipes("com.oracle.weblogic.rewrite.jakarta.MigrateToJakartaEE11");
    }

    private Recipe legacyHibernateMetamodelProcessorRecipe() {
        return Environment.builder()
                .scanRuntimeClasspath()
                .build()
                .activateRecipes("com.oracle.weblogic.rewrite.jakarta.MigrateLegacyHibernateMetamodelProcessorToJakartaEE11");
    }

    private Recipe preserveProvidedJavaEEPlatformDependencyScopeRecipe() {
        return Environment.builder()
                .scanRuntimeClasspath()
                .build()
                .activateRecipes(
                        "com.oracle.weblogic.rewrite.jakarta.PreserveProvidedJavaEEPlatformDependencyScope");
    }

    @Test
    void ordersDescriptorMigrationsAroundUpstreamJakartaEE11Migration() {
        Recipe preserveBeanDiscoveryMode = recipe().getRecipeList().get(0);
        Recipe webXmlMigration = recipe().getRecipeList().get(1);
        Recipe preserveProvidedPlatformScope = recipe().getRecipeList().get(2);
        Recipe upstreamJakartaMigration = recipe().getRecipeList().get(3);
        Recipe beansXmlMigration = recipe().getRecipeList().get(4);
        Recipe activationConfigMigration = recipe().getRecipeList().get(5);
        Recipe destinationDefinitionMigration = recipe().getRecipeList().get(6);

        assertEquals("com.oracle.weblogic.rewrite.jakarta.PreserveLegacyBeansXmlDiscoveryMode",
                preserveBeanDiscoveryMode.getName());
        assertEquals("com.oracle.weblogic.rewrite.jakarta.MigrateWebXmlToJakartaEE11",
                webXmlMigration.getName());
        assertEquals("com.oracle.weblogic.rewrite.jakarta.PreserveProvidedJavaEEPlatformDependencyScope",
                preserveProvidedPlatformScope.getName());
        assertEquals("org.openrewrite.java.migrate.jakarta.JakartaEE11", upstreamJakartaMigration.getName());
        assertEquals("com.oracle.weblogic.rewrite.jakarta.MigrateBeansXmlToJakartaEE11",
                beansXmlMigration.getName());
        assertEquals("com.oracle.weblogic.rewrite.jakarta.MigrateActivationConfigPropertyDestinationType",
                activationConfigMigration.getName());
        assertEquals("com.oracle.weblogic.rewrite.jakarta.MigrateJMSDestinationDefinitionInterfaceName",
                destinationDefinitionMigration.getName());
    }

    @Test
    void materializesInheritedProvidedJavaEEPlatformScopeBeforeMigration() {
        rewriteRun(spec -> spec
                .recipe(preserveProvidedJavaEEPlatformDependencyScopeRecipe())
                .executionContext(localMavenExecutionContext()),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>application</artifactId>
                  <version>1.0.0</version>
                  <packaging>pom</packaging>
                  <modules>
                      <module>module-web</module>
                  </modules>
                  <dependencyManagement>
                      <dependencies>
                          <dependency>
                              <groupId>javax</groupId>
                              <artifactId>javaee-api</artifactId>
                              <version>8.0</version>
                              <scope>provided</scope>
                          </dependency>
                      </dependencies>
                  </dependencyManagement>
              </project>
              """
          ),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                      <groupId>com.example</groupId>
                      <artifactId>application</artifactId>
                      <version>1.0.0</version>
                  </parent>
                  <artifactId>module-web</artifactId>
                  <dependencies>
                      <dependency>
                          <groupId>javax</groupId>
                          <artifactId>javaee-api</artifactId>
                      </dependency>
                  </dependencies>
              </project>
              """,
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                      <groupId>com.example</groupId>
                      <artifactId>application</artifactId>
                      <version>1.0.0</version>
                  </parent>
                  <artifactId>module-web</artifactId>
                  <dependencies>
                      <dependency>
                          <groupId>javax</groupId>
                          <artifactId>javaee-api</artifactId>
                          <scope>provided</scope>
                      </dependency>
                  </dependencies>
              </project>
              """,
            spec -> spec.path("module-web/pom.xml")
          )
        );
    }

    @Test
    void doesNotMaterializeInheritedCompileJavaEEPlatformScope() {
        rewriteRun(spec -> spec
                .recipe(preserveProvidedJavaEEPlatformDependencyScopeRecipe())
                .executionContext(localMavenExecutionContext()),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>application</artifactId>
                  <version>1.0.0</version>
                  <packaging>pom</packaging>
                  <modules>
                      <module>module-web</module>
                  </modules>
                  <dependencyManagement>
                      <dependencies>
                          <dependency>
                              <groupId>javax</groupId>
                              <artifactId>javaee-api</artifactId>
                              <version>8.0</version>
                              <scope>compile</scope>
                          </dependency>
                      </dependencies>
                  </dependencyManagement>
              </project>
              """
          ),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                      <groupId>com.example</groupId>
                      <artifactId>application</artifactId>
                      <version>1.0.0</version>
                  </parent>
                  <artifactId>module-web</artifactId>
                  <dependencies>
                      <dependency>
                          <groupId>javax</groupId>
                          <artifactId>javaee-api</artifactId>
                      </dependency>
                  </dependencies>
              </project>
              """,
            spec -> spec.path("module-web/pom.xml")
          )
        );
    }

    @Test
    void upgradesJakartaPlatformDependencyTo11() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .expectedCyclesThatMakeChanges(1)
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
                          <groupId>jakarta.platform</groupId>
                          <artifactId>jakarta.jakartaee-api</artifactId>
                          <version>10.0.0</version>
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
                          <groupId>jakarta.platform</groupId>
                          <artifactId>jakarta.jakartaee-api</artifactId>
                          <version>11.0.0</version>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void upgradesJavaEEApiInMavenPluginConfigurationToJakartaEE11() {
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
                              <groupId>org.apache.maven.plugins</groupId>
                              <artifactId>maven-dependency-plugin</artifactId>
                              <version>3.9.0</version>
                              <executions>
                                  <execution>
                                      <goals>
                                          <goal>copy</goal>
                                      </goals>
                                      <configuration>
                                          <artifactItems>
                                              <artifactItem>
                                                  <groupId>javax</groupId>
                                                  <artifactId>javaee-api</artifactId>
                                                  <version>11.0.0</version>
                                                  <type>jar</type>
                                              </artifactItem>
                                          </artifactItems>
                                      </configuration>
                                  </execution>
                              </executions>
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
                              <groupId>org.apache.maven.plugins</groupId>
                              <artifactId>maven-dependency-plugin</artifactId>
                              <version>3.9.0</version>
                              <executions>
                                  <execution>
                                      <goals>
                                          <goal>copy</goal>
                                      </goals>
                                      <configuration>
                                          <artifactItems>
                                              <artifactItem>
                                                  <groupId>jakarta.platform</groupId>
                                                  <artifactId>jakarta.jakartaee-api</artifactId>
                                                  <version>11.0.0</version>
                                                  <type>jar</type>
                                              </artifactItem>
                                          </artifactItems>
                                      </configuration>
                                  </execution>
                              </executions>
                          </plugin>
                      </plugins>
                  </build>
              </project>
              """
          )
        );
    }

    @Test
    void upgradesLegacyHibernateMetamodelProcessorForJakartaEE11Idempotently() {
        rewriteRun(spec -> spec
                .recipe(legacyHibernateMetamodelProcessorRecipe())
                .cycles(2)
                .expectedCyclesThatMakeChanges(1)
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
                              <groupId>org.wildfly.bom</groupId>
                              <artifactId>jboss-javaee-7.0-wildfly-with-tools</artifactId>
                              <version>9.0.0.Final</version>
                              <type>pom</type>
                              <scope>import</scope>
                          </dependency>
                      </dependencies>
                  </dependencyManagement>
                  <dependencies>
                      <dependency>
                          <groupId>org.hibernate</groupId>
                          <artifactId>hibernate-jpamodelgen</artifactId>
                          <version>4.3.10.Final</version>
                          <scope>provided</scope>
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
                  <dependencyManagement>
                      <dependencies>
                          <dependency>
                              <groupId>org.jboss.logging</groupId>
                              <artifactId>jboss-logging</artifactId>
                              <version>3.6.1.Final</version>
                          </dependency>
                          <dependency>
                              <groupId>org.wildfly.bom</groupId>
                              <artifactId>jboss-javaee-7.0-wildfly-with-tools</artifactId>
                              <version>9.0.0.Final</version>
                              <type>pom</type>
                              <scope>import</scope>
                          </dependency>
                      </dependencies>
                  </dependencyManagement>
                  <dependencies>
                      <dependency>
                          <groupId>org.hibernate.orm</groupId>
                          <artifactId>hibernate-processor</artifactId>
                          <version>7.0.8.Final</version>
                          <scope>provided</scope>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void upgradesDirectJbossLoggingWhenMigratingLegacyHibernateMetamodelProcessor() {
        rewriteRun(spec -> spec
                .recipe(legacyHibernateMetamodelProcessorRecipe())
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
                          <groupId>org.hibernate</groupId>
                          <artifactId>hibernate-jpamodelgen</artifactId>
                          <version>4.3.10.Final</version>
                          <scope>provided</scope>
                      </dependency>
                      <dependency>
                          <groupId>org.jboss.logging</groupId>
                          <artifactId>jboss-logging</artifactId>
                          <version>3.2.1.Final</version>
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
                          <groupId>org.hibernate.orm</groupId>
                          <artifactId>hibernate-processor</artifactId>
                          <version>7.0.8.Final</version>
                          <scope>provided</scope>
                      </dependency>
                      <dependency>
                          <groupId>org.jboss.logging</groupId>
                          <artifactId>jboss-logging</artifactId>
                          <version>3.6.1.Final</version>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void doesNotUpgradeJbossLoggingWithoutLegacyHibernateMetamodelProcessor() {
        rewriteRun(spec -> spec
                .recipe(legacyHibernateMetamodelProcessorRecipe())
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
                          <groupId>org.jboss.logging</groupId>
                          <artifactId>jboss-logging</artifactId>
                          <version>3.2.1.Final</version>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void doesNotDowngradeNewerManagedJbossLoggingWhenMigratingLegacyHibernateMetamodelProcessor() {
        rewriteRun(spec -> spec
                .recipe(legacyHibernateMetamodelProcessorRecipe())
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
                              <groupId>org.jboss.logging</groupId>
                              <artifactId>jboss-logging</artifactId>
                              <version>3.6.2.Final</version>
                          </dependency>
                      </dependencies>
                  </dependencyManagement>
                  <dependencies>
                      <dependency>
                          <groupId>org.hibernate</groupId>
                          <artifactId>hibernate-jpamodelgen</artifactId>
                          <version>4.3.10.Final</version>
                          <scope>provided</scope>
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
                  <dependencyManagement>
                      <dependencies>
                          <dependency>
                              <groupId>org.jboss.logging</groupId>
                              <artifactId>jboss-logging</artifactId>
                              <version>3.6.2.Final</version>
                          </dependency>
                      </dependencies>
                  </dependencyManagement>
                  <dependencies>
                      <dependency>
                          <groupId>org.hibernate.orm</groupId>
                          <artifactId>hibernate-processor</artifactId>
                          <version>7.0.8.Final</version>
                          <scope>provided</scope>
                      </dependency>
                  </dependencies>
              </project>
              """
          )
        );
    }

    @Test
    void upgradesWebXmlToServlet61() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
                       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                       xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
                       version="6.0">
                  <display-name>example</display-name>
              </web-app>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
                       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                       xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_6_1.xsd"
                       version="6.1">
                  <display-name>example</display-name>
              </web-app>
              """,
            sourceSpec -> sourceSpec.path("src/main/webapp/WEB-INF/web.xml")
          )
        );
    }

    @Test
    void upgradesLegacyServlet30WebXmlToServlet61InOneCycle() {
        assertLegacyWebXmlUpgradesToServlet61(
                "http://java.sun.com/xml/ns/javaee", "3.0", "servlet-30");
    }

    @Test
    void upgradesLegacyServlet31WebXmlToServlet61InOneCycle() {
        assertLegacyWebXmlUpgradesToServlet61(
                "http://xmlns.jcp.org/xml/ns/javaee", "3.1", "servlet-31");
    }

    @Test
    void upgradesLegacyServlet40WebXmlToServlet61InOneCycle() {
        assertLegacyWebXmlUpgradesToServlet61(
                "http://xmlns.jcp.org/xml/ns/javaee", "4.0", "servlet-40");
    }

    @Test
    void keepsServlet61WebXmlUnchanged() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
                       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                       xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_6_1.xsd"
                       version="6.1">
                  <display-name>servlet-61</display-name>
                  <context-param>
                      <param-name>jakarta.faces.FACELETS_LIBRARIES</param-name>
                      <param-value>/WEB-INF/example.taglib.xml</param-value>
                  </context-param>
              </web-app>
              """,
            sourceSpec -> sourceSpec.path("src/main/webapp/WEB-INF/web.xml")
          )
        );
    }

    @Test
    void upgradesWebFragmentXmlToServlet61() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .expectedCyclesThatMakeChanges(2),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <web-fragment xmlns="https://jakarta.ee/xml/ns/jakartaee"
                            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                            xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-fragment_6_0.xsd"
                            version="6.0">
                  <name>example-fragment</name>
              </web-fragment>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <web-fragment xmlns="https://jakarta.ee/xml/ns/jakartaee"
                            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                            xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-fragment_6_1.xsd"
                            version="6.1">
                  <name>example-fragment</name>
              </web-fragment>
              """,
            sourceSpec -> sourceSpec.path("src/main/resources/META-INF/web-fragment.xml")
          )
        );
    }

    @Test
    void upgradesApplicationXmlToJakartaEE11() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <application xmlns="https://jakarta.ee/xml/ns/jakartaee"
                           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/application_10.xsd"
                           version="10">
                  <display-name>example</display-name>
              </application>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <application xmlns="https://jakarta.ee/xml/ns/jakartaee"
                           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/application_11.xsd"
                           version="11">
                  <display-name>example</display-name>
              </application>
              """,
            sourceSpec -> sourceSpec.path("src/main/resources/META-INF/application.xml")
          )
        );
    }

    @Test
    void upgradesApplicationClientXmlToJakartaEE11() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <application-client xmlns="https://jakarta.ee/xml/ns/jakartaee"
                                  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                  xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/application-client_9.xsd"
                                  version="9">
                  <display-name>example-client</display-name>
              </application-client>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <application-client xmlns="https://jakarta.ee/xml/ns/jakartaee"
                                  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                  xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/application-client_11.xsd"
                                  version="10">
                  <display-name>example-client</display-name>
              </application-client>
              """,
            sourceSpec -> sourceSpec.path("src/main/resources/META-INF/application-client.xml")
          )
        );
    }

    @Test
    void upgradesJavaEE8ApplicationClientXmlToJakartaEE11Idempotently() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .cycles(2)
                .expectedCyclesThatMakeChanges(1),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <application-client xmlns="http://xmlns.jcp.org/xml/ns/javaee"
                                  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                  xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/application-client_8.xsd"
                                  version="8">
                  <display-name>java-ee-8-client</display-name>
              </application-client>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <application-client xmlns="https://jakarta.ee/xml/ns/jakartaee"
                                  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                  xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/application-client_11.xsd"
                                  version="10">
                  <display-name>java-ee-8-client</display-name>
              </application-client>
              """,
            sourceSpec -> sourceSpec.path("src/main/resources/META-INF/application-client.xml")
          )
        );
    }

    @Test
    void upgradesEjbJarXmlToJakartaEE11() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <ejb-jar xmlns="http://xmlns.jcp.org/xml/ns/javaee"
                       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                       xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/ejb-jar_3_2.xsd"
                       version="3.2">
                  <display-name>example-ejb</display-name>
              </ejb-jar>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <ejb-jar xmlns="https://jakarta.ee/xml/ns/jakartaee"
                       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                       xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/ejb-jar_4_0.xsd"
                       version="4.0">
                  <display-name>example-ejb</display-name>
              </ejb-jar>
              """,
            sourceSpec -> sourceSpec.path("src/main/resources/META-INF/ejb-jar.xml")
          )
        );
    }

    @Test
    void upgradesRaXmlToJakartaConnectors21() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <connector xmlns="https://jakarta.ee/xml/ns/jakartaee"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/connector_2_0.xsd"
                         version="2.0">
                  <display-name>example-adapter</display-name>
              </connector>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <connector xmlns="https://jakarta.ee/xml/ns/jakartaee"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/connector_2_1.xsd"
                         version="2.1">
                  <display-name>example-adapter</display-name>
              </connector>
              """,
            sourceSpec -> sourceSpec.path("src/main/resources/META-INF/ra.xml")
          )
        );
    }

    @Test
    void upgradesPersistenceXmlToJakartaPersistence32() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .expectedCyclesThatMakeChanges(2),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <persistence xmlns="https://jakarta.ee/xml/ns/persistence"
                           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_1.xsd"
                           version="3.1">
                  <persistence-unit name="example">
                      <shared-cache-mode>NONE</shared-cache-mode>
                  </persistence-unit>
              </persistence>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <persistence xmlns="https://jakarta.ee/xml/ns/persistence"
                           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_2.xsd"
                           version="3.2">
                  <persistence-unit name="example">
                      <shared-cache-mode>NONE</shared-cache-mode>
                  </persistence-unit>
              </persistence>
              """,
            sourceSpec -> sourceSpec.path("src/main/resources/META-INF/persistence.xml")
          )
        );
    }

    @Test
    void upgradesOrmXmlToJakartaPersistence32() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .expectedCyclesThatMakeChanges(2),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <entity-mappings xmlns="https://jakarta.ee/xml/ns/persistence/orm"
                               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                               xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence/orm https://jakarta.ee/xml/ns/persistence/orm/orm_3_1.xsd"
                               version="3.1">
                  <description>example mappings</description>
              </entity-mappings>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <entity-mappings xmlns="https://jakarta.ee/xml/ns/persistence/orm"
                               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                               xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence/orm https://jakarta.ee/xml/ns/persistence/orm/orm_3_2.xsd"
                               version="3.2">
                  <description>example mappings</description>
              </entity-mappings>
              """,
            sourceSpec -> sourceSpec.path("src/main/resources/META-INF/orm.xml")
          )
        );
    }

    @Test
    void upgradesValidationXmlToBeanValidation31() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .expectedCyclesThatMakeChanges(2),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <validation-config xmlns="https://jakarta.ee/xml/ns/validation/configuration"
                                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                 xsi:schemaLocation="https://jakarta.ee/xml/ns/validation/configuration https://jakarta.ee/xml/ns/validation/validation-configuration-3.0.xsd"
                                 version="3.0">
                  <default-provider>org.hibernate.validator.HibernateValidator</default-provider>
              </validation-config>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <validation-config xmlns="https://jakarta.ee/xml/ns/validation/configuration"
                                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                 xsi:schemaLocation="https://jakarta.ee/xml/ns/validation/configuration https://jakarta.ee/xml/ns/validation/validation-configuration-3.1.xsd"
                                 version="3.1">
                  <default-provider>org.hibernate.validator.HibernateValidator</default-provider>
              </validation-config>
              """,
            sourceSpec -> sourceSpec.path("src/main/resources/META-INF/validation.xml")
          )
        );
    }

    @Test
    void upgradesValidationMappingXmlToBeanValidation31() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <constraint-mappings xmlns="https://jakarta.ee/xml/ns/validation/mapping"
                                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                   xsi:schemaLocation="https://jakarta.ee/xml/ns/validation/mapping https://jakarta.ee/xml/ns/validation/validation-mapping-3.0.xsd"
                                   version="3.0">
                  <default-package>com.example.model</default-package>
              </constraint-mappings>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <constraint-mappings xmlns="https://jakarta.ee/xml/ns/validation/mapping"
                                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                   xsi:schemaLocation="https://jakarta.ee/xml/ns/validation/mapping https://jakarta.ee/xml/ns/validation/validation-mapping-3.1.xsd"
                                   version="3.1">
                  <default-package>com.example.model</default-package>
              </constraint-mappings>
              """,
            sourceSpec -> sourceSpec.path("src/main/resources/META-INF/validation/constraints.xml")
          )
        );
    }

    @Test
    void upgradesFacesConfigXmlToJakartaFaces41() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .expectedCyclesThatMakeChanges(2),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <faces-config xmlns="https://jakarta.ee/xml/ns/jakartaee"
                            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                            xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-facesconfig_4_0.xsd"
                            version="4.0">
                  <name>example-faces</name>
              </faces-config>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <faces-config xmlns="https://jakarta.ee/xml/ns/jakartaee"
                            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                            xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-facesconfig_4_1.xsd"
                            version="4.1">
                  <name>example-faces</name>
              </faces-config>
              """,
            sourceSpec -> sourceSpec.path("src/main/webapp/WEB-INF/faces-config.xml")
          )
        );
    }

    @Test
    void upgradesFaceletTagLibraryXmlToJakartaFaces41() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .expectedCyclesThatMakeChanges(2),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <facelet-taglib xmlns="https://jakarta.ee/xml/ns/jakartaee"
                              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                              xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-facelettaglibrary_4_0.xsd"
                              version="4.0">
                  <namespace>https://example.com/tags</namespace>
              </facelet-taglib>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <facelet-taglib xmlns="https://jakarta.ee/xml/ns/jakartaee"
                              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                              xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-facelettaglibrary_4_1.xsd"
                              version="4.1">
                  <namespace>https://example.com/tags</namespace>
              </facelet-taglib>
              """,
            sourceSpec -> sourceSpec.path("src/main/resources/META-INF/example.taglib.xml")
          )
        );
    }

    @Test
    void upgradesJspTagLibraryToJakartaPages40() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <taglib xmlns="https://jakarta.ee/xml/ns/jakartaee"
                      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                      xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-jsptaglibrary_3_0.xsd"
                      version="3.0">
                  <tlib-version>1.0</tlib-version>
                  <short-name>example</short-name>
                  <uri>https://example.com/tags</uri>
              </taglib>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <taglib xmlns="https://jakarta.ee/xml/ns/jakartaee"
                      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                      xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-jsptaglibrary_4_0.xsd"
                      version="4.0">
                  <tlib-version>1.0</tlib-version>
                  <short-name>example</short-name>
                  <uri>https://example.com/tags</uri>
              </taglib>
              """,
            sourceSpec -> sourceSpec.path("src/main/resources/META-INF/example.tld")
          )
        );
    }

    @Test
    void upgradesBatchXmlToJakartaBatch20Descriptor() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <batch-artifacts xmlns="http://xmlns.jcp.org/xml/ns/javaee"
                               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                               xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/batchXML_1_0.xsd"
                               version="1.0">
                  <ref id="exampleBatchlet" class="com.example.ExampleBatchlet"/>
              </batch-artifacts>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <batch-artifacts xmlns="https://jakarta.ee/xml/ns/jakartaee"
                               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                               xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/batchXML_2_0.xsd"
                               version="2.0">
                  <ref id="exampleBatchlet" class="com.example.ExampleBatchlet"/>
              </batch-artifacts>
              """,
            sourceSpec -> sourceSpec.path("src/main/resources/META-INF/batch.xml")
          )
        );
    }

    @Test
    void upgradesBatchJobXmlToJakartaBatch20Descriptor() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <job xmlns="http://xmlns.jcp.org/xml/ns/javaee"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/jobXML_1_0.xsd"
                   id="example-job"
                   version="1.0">
                  <step id="example-step"/>
              </job>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <job xmlns="https://jakarta.ee/xml/ns/jakartaee"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/jobXML_2_0.xsd"
                   id="example-job"
                   version="2.0">
                  <step id="example-step"/>
              </job>
              """,
            sourceSpec -> sourceSpec.path("src/main/resources/META-INF/batch-jobs/example-job.xml")
          )
        );
    }

    @Test
    void upgradesPermissionsXmlToJakartaEE10Descriptor() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <permissions xmlns="https://jakarta.ee/xml/ns/jakartaee"
                           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/permissions_9.xsd"
                           version="9">
                  <permission>
                      <class-name>java.io.FilePermission</class-name>
                      <name>/tmp/example</name>
                      <actions>read</actions>
                  </permission>
              </permissions>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <permissions xmlns="https://jakarta.ee/xml/ns/jakartaee"
                           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/permissions_10.xsd"
                           version="10">
                  <permission>
                      <class-name>java.io.FilePermission</class-name>
                      <name>/tmp/example</name>
                      <actions>read</actions>
                  </permission>
              </permissions>
              """,
            sourceSpec -> sourceSpec.path("src/main/resources/META-INF/permissions.xml")
          )
        );
    }

    @Test
    void upgradesWebServicesXmlToJakartaWebServices20() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <webservices xmlns="http://xmlns.jcp.org/xml/ns/javaee"
                           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/javaee_web_services_1_4.xsd"
                           version="1.4">
                  <webservice-description>
                      <webservice-description-name>ExampleService</webservice-description-name>
                  </webservice-description>
              </webservices>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <webservices xmlns="https://jakarta.ee/xml/ns/jakartaee"
                           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/jakartaee_web_services_2_0.xsd"
                           version="2.0">
                  <webservice-description>
                      <webservice-description-name>ExampleService</webservice-description-name>
                  </webservice-description>
              </webservices>
              """,
            sourceSpec -> sourceSpec.path("src/main/resources/META-INF/webservices.xml")
          )
        );
    }

    @Test
    void upgradesHandlerXmlToJakartaWebServicesMetadata30() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <handler-chains xmlns="http://xmlns.jcp.org/xml/ns/javaee"
                              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                              xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/javaee_web_services_metadata_handler_2_0.xsd"
                              version="2.0">
                  <handler-chain>
                      <handler>
                          <handler-name>ExampleHandler</handler-name>
                          <handler-class>javax.xml.ws.handler.Handler</handler-class>
                      </handler>
                  </handler-chain>
              </handler-chains>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <handler-chains xmlns="https://jakarta.ee/xml/ns/jakartaee"
                              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                              xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/jakartaee_web_services_metadata_handler_3_0.xsd"
                              version="3.0">
                  <handler-chain>
                      <handler>
                          <handler-name>ExampleHandler</handler-name>
                          <handler-class>jakarta.xml.ws.handler.Handler</handler-class>
                      </handler>
                  </handler-chain>
              </handler-chains>
              """,
            sourceSpec -> sourceSpec.path("src/main/resources/META-INF/handler.xml")
          )
        );
    }

    @Test
    void upgradesLegacyBeansXmlToCdi41AndPreservesDiscoveryMode() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .expectedCyclesThatMakeChanges(2),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_3_0.xsd">
                  <interceptors>
                      <class>com.example.LoggingInterceptor</class>
                  </interceptors>
              </beans>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_4_1.xsd" bean-discovery-mode="all" version="4.1">
                  <interceptors>
                      <class>com.example.LoggingInterceptor</class>
                  </interceptors>
              </beans>
              """,
            sourceSpec -> sourceSpec.path("src/main/webapp/WEB-INF/beans.xml")
          )
        );
    }

    @Test
    void upgradesCdi40AndPreservesExplicitAnnotatedBeanDiscoveryMode() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .expectedCyclesThatMakeChanges(2),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_4_0.xsd"
                     version="4.0"
                     bean-discovery-mode="annotated">
                  <interceptors>
                      <class>com.example.LoggingInterceptor</class>
                  </interceptors>
              </beans>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_4_1.xsd"
                     version="4.1"
                     bean-discovery-mode="annotated">
                  <interceptors>
                      <class>com.example.LoggingInterceptor</class>
                  </interceptors>
              </beans>
              """,
            sourceSpec -> sourceSpec.path("src/main/webapp/WEB-INF/beans.xml")
          )
        );
    }

    @Test
    void upgradesCdi40AndPreservesExplicitAllBeanDiscoveryMode() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .expectedCyclesThatMakeChanges(2),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_4_0.xsd"
                     version="4.0"
                     bean-discovery-mode="all">
                  <interceptors>
                      <class>com.example.LoggingInterceptor</class>
                  </interceptors>
              </beans>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_4_1.xsd"
                     version="4.1"
                     bean-discovery-mode="all">
                  <interceptors>
                      <class>com.example.LoggingInterceptor</class>
                  </interceptors>
              </beans>
              """,
            sourceSpec -> sourceSpec.path("src/main/webapp/WEB-INF/beans.xml")
          )
        );
    }

    @Test
    void upgradesCdi40AndPreservesExplicitNoneBeanDiscoveryMode() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .expectedCyclesThatMakeChanges(2),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_4_0.xsd"
                     version="4.0"
                     bean-discovery-mode="none">
                  <interceptors>
                      <class>com.example.LoggingInterceptor</class>
                  </interceptors>
              </beans>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_4_1.xsd"
                     version="4.1"
                     bean-discovery-mode="none">
                  <interceptors>
                      <class>com.example.LoggingInterceptor</class>
                  </interceptors>
              </beans>
              """,
            sourceSpec -> sourceSpec.path("src/main/webapp/WEB-INF/beans.xml")
          )
        );
    }

    @Test
    void leavesCdi41BeansXmlUnchanged() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .expectedCyclesThatMakeChanges(2),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_4_1.xsd"
                     version="4.1"
                     bean-discovery-mode="annotated">
              </beans>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_4_1.xsd"
                     version="4.1"
                     bean-discovery-mode="annotated">
              </beans>
              """,
            sourceSpec -> sourceSpec.path("src/main/webapp/WEB-INF/beans.xml")
          )
        );
    }

    private void assertLegacyWebXmlUpgradesToServlet61(String namespace, String version, String displayName) {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .cycles(1)
                .expectedCyclesThatMakeChanges(1),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <web-app xmlns="%s"
                       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                       xsi:schemaLocation="%s %s/web-app_%s.xsd"
                       version="%s">
                  <display-name>%s</display-name>
                  <context-param>
                      <param-name>javax.faces.FACELETS_LIBRARIES</param-name>
                      <param-value>/WEB-INF/example.taglib.xml</param-value>
                  </context-param>
              </web-app>
              """.formatted(namespace, namespace, namespace, version.replace('.', '_'), version, displayName),
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
                       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                       xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_6_1.xsd"
                       version="6.1">
                  <display-name>%s</display-name>
                  <context-param>
                      <param-name>jakarta.faces.FACELETS_LIBRARIES</param-name>
                      <param-value>/WEB-INF/example.taglib.xml</param-value>
                  </context-param>
              </web-app>
              """.formatted(displayName),
            sourceSpec -> sourceSpec.path("src/main/webapp/WEB-INF/web.xml")
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
