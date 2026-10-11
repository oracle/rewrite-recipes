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
import org.openrewrite.config.CompositeRecipe;
import org.openrewrite.config.Environment;
import org.openrewrite.marker.BuildTool;
import org.openrewrite.maven.UpdateMavenWrapper;
import org.openrewrite.test.RewriteTest;

import java.util.Arrays;

import static org.openrewrite.maven.Assertions.pomXml;
import static org.openrewrite.properties.Assertions.properties;

class UpgradeJavaWithMavenWrapperTest implements RewriteTest {

    private Recipe recipe(String javaUpgradeRecipe) {
        Recipe upgradeJava = Environment.builder()
                .scanRuntimeClasspath("org.openrewrite")
                .build()
                .activateRecipes(javaUpgradeRecipe);

        return new CompositeRecipe(Arrays.asList(
                upgradeJava,
                new UpdateMavenWrapper("3.3.4", null, "3.9.16", null, false, null)));
    }

    @Test
    void upgradesJava17AndExistingMavenWrapperTogether() {
        rewriteRun(spec -> spec
                .recipe(recipe("org.openrewrite.java.migrate.UpgradeToJava17"))
                .executionContext(wrapperExecutionContext())
                .allSources(source -> source.markers(
                        new BuildTool(Tree.randomId(), BuildTool.Type.Maven, "3.8.8"))),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>example-app</artifactId>
                  <version>1.0.0</version>
                  <properties>
                      <maven.compiler.release>11</maven.compiler.release>
                  </properties>
              </project>
              """,
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>example-app</artifactId>
                  <version>1.0.0</version>
                  <properties>
                      <maven.compiler.release>17</maven.compiler.release>
                  </properties>
              </project>
              """
          ),
          properties(
            """
              distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.8.8/apache-maven-3.8.8-bin.zip
              wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
              """,
            """
              distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.16/apache-maven-3.9.16-bin.zip
              wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.4/maven-wrapper-3.3.4.jar
              distributionSha256Sum=01eae159e1a67072b752fd12e9877f815a600fe0d24c0d81ae29e6160a875a33
              """,
            source -> source.path(".mvn/wrapper/maven-wrapper.properties")
          )
        );
    }

    @Test
    void upgradesJava21AndExistingMavenWrapperTogether() {
        rewriteRun(spec -> spec
                .recipe(recipe("org.openrewrite.java.migrate.UpgradeToJava21"))
                .executionContext(wrapperExecutionContext())
                .allSources(source -> source.markers(
                        new BuildTool(Tree.randomId(), BuildTool.Type.Maven, "3.8.8"))),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>example-app</artifactId>
                  <version>1.0.0</version>
                  <properties>
                      <maven.compiler.release>17</maven.compiler.release>
                  </properties>
              </project>
              """,
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>example-app</artifactId>
                  <version>1.0.0</version>
                  <properties>
                      <maven.compiler.release>21</maven.compiler.release>
                  </properties>
              </project>
              """
          ),
          properties(
            """
              distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.8.8/apache-maven-3.8.8-bin.zip
              wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
              """,
            """
              distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.16/apache-maven-3.9.16-bin.zip
              wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.4/maven-wrapper-3.3.4.jar
              distributionSha256Sum=01eae159e1a67072b752fd12e9877f815a600fe0d24c0d81ae29e6160a875a33
              """,
            source -> source.path(".mvn/wrapper/maven-wrapper.properties")
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
