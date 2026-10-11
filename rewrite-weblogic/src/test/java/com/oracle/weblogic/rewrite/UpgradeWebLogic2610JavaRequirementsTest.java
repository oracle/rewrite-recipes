/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite;

import org.junit.jupiter.api.Test;
import org.openrewrite.Recipe;
import org.openrewrite.config.Environment;
import org.openrewrite.java.migrate.UpgradeJavaVersion;
import org.openrewrite.test.RewriteTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.openrewrite.maven.Assertions.pomXml;

class UpgradeWebLogic2610JavaRequirementsTest implements RewriteTest {

    private static Recipe recipe(String name) {
        return Environment.builder()
                .scanRuntimeClasspath()
                .build()
                .activateRecipes(name);
    }

    @Test
    void java21RecipeContainsOnlyTheBuildVersionMigration() {
        Recipe java21 = recipe("com.oracle.weblogic.rewrite.UpgradeBuildToJava21ForWebLogic2610");

        assertEquals(1, java21.getRecipeList().size());
        UpgradeJavaVersion upgrade = assertInstanceOf(UpgradeJavaVersion.class, java21.getRecipeList().get(0));
        assertEquals(21, upgrade.getVersion());
    }

    @Test
    void java25RecipeContainsOnlyTheBuildVersionMigration() {
        Recipe java25 = recipe("com.oracle.weblogic.rewrite.UpgradeBuildToJava25ForWebLogic2610");

        assertEquals(1, java25.getRecipeList().size());
        UpgradeJavaVersion upgrade = assertInstanceOf(UpgradeJavaVersion.class, java25.getRecipeList().get(0));
        assertEquals(25, upgrade.getVersion());
    }

    @Test
    void upgradesMavenBuildTargetToJava21() {
        rewriteRun(spec -> spec.recipe(
                recipe("com.oracle.weblogic.rewrite.UpgradeBuildToJava21ForWebLogic2610")),
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
          )
        );
    }

    @Test
    void upgradesMavenBuildTargetToJava25() {
        rewriteRun(spec -> spec.recipe(
                recipe("com.oracle.weblogic.rewrite.UpgradeBuildToJava25ForWebLogic2610")),
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
                      <maven.compiler.release>25</maven.compiler.release>
                  </properties>
              </project>
              """
          )
        );
    }
}
