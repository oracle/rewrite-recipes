/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite.spring.framework;

import org.junit.jupiter.api.Test;
import org.openrewrite.ExecutionContext;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.config.Environment;
import org.openrewrite.maven.AddManagedDependency;
import org.openrewrite.maven.MavenExecutionContextView;
import org.openrewrite.maven.MavenParser;
import org.openrewrite.maven.tree.MavenRepository;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import java.io.File;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.openrewrite.maven.Assertions.pomXml;

class AlignSpringFrameworkBomForSpring7Test implements RewriteTest {

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

        spec.recipe(new AddManagedDependency(
                        "org.springframework",
                        "spring-framework-bom",
                        "7.0.9",
                        "import",
                        "pom",
                        null,
                        null,
                        true,
                        null,
                        true,
                        null))
                .parser(MavenParser.builder().skipDependencyResolution(true))
                .executionContext(mavenExecutionContext);
    }

    @Test
    void productionRecipeUsesLatestSpring7ReleaseOnlyForSpringProjects() {
        Recipe recipe = Environment.builder()
                .scanRuntimeClasspath()
                .build()
                .activateRecipes(
                        "com.oracle.weblogic.rewrite.spring.framework.AlignSpringFrameworkBomForSpring7");

        assertEquals(1, recipe.getRecipeList().size());
        AddManagedDependency addBom = assertInstanceOf(
                AddManagedDependency.class, recipe.getRecipeList().get(0));
        assertEquals("org.springframework", addBom.getGroupId());
        assertEquals("spring-framework-bom", addBom.getArtifactId());
        assertEquals("7.0.x", addBom.getVersion());
        assertEquals("pom", addBom.getType());
        assertEquals("import", addBom.getScope());
        assertEquals(Boolean.TRUE, addBom.getReleasesOnly());
        assertEquals("org.springframework:*", addBom.getOnlyIfUsing());
        assertEquals(Boolean.TRUE, addBom.getAddToRootPom());
    }

    @Test
    void importsFrameworkBomBeforeSpringDataBom() {
        rewriteRun(spec -> spec
                .cycles(1)
                .expectedCyclesThatMakeChanges(1),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>org.springframework.samples</groupId>
                  <artifactId>spring-framework-petclinic</artifactId>
                  <version>5.3.22</version>
                  <properties>
                      <spring-framework.version>7.0.9</spring-framework.version>
                      <spring-data.version>2025.1.0</spring-data.version>
                  </properties>
                  <dependencyManagement>
                      <dependencies>
                          <dependency>
                              <groupId>org.springframework.data</groupId>
                              <artifactId>spring-data-bom</artifactId>
                              <version>${spring-data.version}</version>
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
                  <groupId>org.springframework.samples</groupId>
                  <artifactId>spring-framework-petclinic</artifactId>
                  <version>5.3.22</version>
                  <properties>
                      <spring-framework.version>7.0.9</spring-framework.version>
                      <spring-data.version>2025.1.0</spring-data.version>
                  </properties>
                  <dependencyManagement>
                      <dependencies>
                          <dependency>
                              <groupId>org.springframework</groupId>
                              <artifactId>spring-framework-bom</artifactId>
                              <version>7.0.9</version>
                              <type>pom</type>
                              <scope>import</scope>
                          </dependency>
                          <dependency>
                              <groupId>org.springframework.data</groupId>
                              <artifactId>spring-data-bom</artifactId>
                              <version>${spring-data.version}</version>
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
