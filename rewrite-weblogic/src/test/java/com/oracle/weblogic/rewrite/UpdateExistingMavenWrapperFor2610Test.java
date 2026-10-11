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
import org.openrewrite.Tree;
import org.openrewrite.config.Environment;
import org.openrewrite.marker.BuildTool;
import org.openrewrite.maven.UpdateMavenWrapper;
import org.openrewrite.test.RewriteTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openrewrite.maven.Assertions.pomXml;
import static org.openrewrite.properties.Assertions.properties;

class UpdateExistingMavenWrapperFor2610Test implements RewriteTest {

    private Recipe recipe() {
        return Environment.builder()
                .scanRuntimeClasspath("com.oracle.weblogic")
                .build()
                .activateRecipes("com.oracle.weblogic.rewrite.UpdateExistingMavenWrapperFor2610");
    }

    @Test
    void configuresWrapperRecipeNotToAddMissingWrapper() {
        Recipe childRecipe = recipe().getRecipeList().get(0);
        UpdateMavenWrapper updateMavenWrapper = assertInstanceOf(UpdateMavenWrapper.class, childRecipe);

        assertEquals(false, updateMavenWrapper.getAddIfMissing());
    }

    @Test
    void updatesExistingMavenDistribution() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .executionContext(wrapperExecutionContext())
                .allSources(source -> source.markers(
                        new BuildTool(Tree.randomId(), BuildTool.Type.Maven, "3.8.8"))),
          properties(
            """
              distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.8.8/apache-maven-3.8.8-bin.zip
              wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
              """,
            sourceSpec -> sourceSpec
                    .path(".mvn/wrapper/maven-wrapper.properties")
                    .after(actual -> {
                        assertTrue(actual.contains(
                                "distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.16/apache-maven-3.9.16-bin.zip"));
                        return actual;
                    })
          )
        );
    }

    @Test
    void updatesExistingMavenWrapperVersion() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .executionContext(wrapperExecutionContext())
                .allSources(source -> source.markers(
                        new BuildTool(Tree.randomId(), BuildTool.Type.Maven, "3.9.16"))),
          properties(
            """
              distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.16/apache-maven-3.9.16-bin.zip
              wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
              """,
            sourceSpec -> sourceSpec
                    .path(".mvn/wrapper/maven-wrapper.properties")
                    .after(actual -> {
                        assertTrue(actual.contains(
                                "wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.4/maven-wrapper-3.3.4.jar"));
                        return actual;
                    })
          )
        );
    }

    @Test
    void doesNotAddMavenWrapperWhenMissing() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .executionContext(wrapperExecutionContext()),
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

    private ExecutionContext wrapperExecutionContext() {
        ExecutionContext executionContext = new InMemoryExecutionContext(t -> {
            throw new RuntimeException("Rewrite error", t);
        });
        return MavenWrapperTestExecutionContext.configure(executionContext);
    }
}
