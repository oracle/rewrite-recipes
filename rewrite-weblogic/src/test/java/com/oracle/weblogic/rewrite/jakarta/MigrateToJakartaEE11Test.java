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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openrewrite.maven.Assertions.pomXml;
import static org.openrewrite.xml.Assertions.xml;

class MigrateToJakartaEE11Test implements RewriteTest {

    private Recipe recipe() {
        return Environment.builder()
                .scanRuntimeClasspath()
                .build()
                .activateRecipes("com.oracle.weblogic.rewrite.jakarta.MigrateToJakartaEE11");
    }

    @Test
    void includesUpstreamJakartaEE11Migration() {
        Recipe childRecipe = recipe().getRecipeList().get(0);

        assertEquals("org.openrewrite.java.migrate.jakarta.JakartaEE11", childRecipe.getName());
    }

    @Test
    void upgradesJakartaPlatformDependencyTo11() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .expectedCyclesThatMakeChanges(2)
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
    void preservesLegacyBeanDiscoveryMode() {
        rewriteRun(spec -> spec.recipe(recipe()),
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
            sourceSpec -> sourceSpec
                    .path("src/main/webapp/WEB-INF/beans.xml")
                    .after(actual -> {
                        assertTrue(actual.contains("bean-discovery-mode=\"all\""));
                        return actual;
                    })
          )
        );
    }

    @Test
    void preservesExplicitAnnotatedBeanDiscoveryMode() {
        rewriteRun(spec -> spec.recipe(recipe()),
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
            sourceSpec -> sourceSpec.path("src/main/webapp/WEB-INF/beans.xml")
          )
        );
    }

    @Test
    void preservesExplicitAllBeanDiscoveryMode() {
        rewriteRun(spec -> spec.recipe(recipe()),
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
            sourceSpec -> sourceSpec.path("src/main/webapp/WEB-INF/beans.xml")
          )
        );
    }

    @Test
    void preservesExplicitNoneBeanDiscoveryMode() {
        rewriteRun(spec -> spec.recipe(recipe()),
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
            sourceSpec -> sourceSpec.path("src/main/webapp/WEB-INF/beans.xml")
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
