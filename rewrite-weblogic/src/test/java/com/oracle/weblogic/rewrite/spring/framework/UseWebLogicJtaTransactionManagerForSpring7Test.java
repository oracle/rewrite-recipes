/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite.spring.framework;

import org.junit.jupiter.api.Test;
import org.openrewrite.Recipe;
import org.openrewrite.config.Environment;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.dependencies.UpgradeDependencyVersion;
import org.openrewrite.test.RewriteTest;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.openrewrite.java.Assertions.java;
import static org.openrewrite.xml.Assertions.xml;

class UseWebLogicJtaTransactionManagerForSpring7Test implements RewriteTest {

    private static final String GENERIC_TRANSACTION_MANAGER = """
      package org.springframework.transaction.jta;
      public class JtaTransactionManager {
          public JtaTransactionManager() {}
          public JtaTransactionManager(Object transactionManager) {}
      }
      """;

    private static final String WEBLOGIC_TRANSACTION_MANAGER = """
      package org.springframework.transaction.jta;
      public class WebLogicJtaTransactionManager extends JtaTransactionManager {
          public WebLogicJtaTransactionManager() {}
      }
      """;

    private Recipe recipe(String name) {
        return Environment.builder()
                .scanRuntimeClasspath()
                .build()
                .activateRecipes(name);
    }

    @Test
    void spring7CompositeIncludesUpstreamAndWebLogicTransactionRecipes() {
        Recipe composite = recipe(
                "com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_7_0ForWebLogic2610");

        assertEquals(
                Arrays.asList(
                        "com.oracle.weblogic.rewrite.spring.framework.NormalizeJacksonAnnotationsForSpring7",
                        "org.openrewrite.java.spring.framework.UpgradeSpringFramework_7_0",
                        "com.oracle.weblogic.rewrite.spring.data.UpgradeSpringDataBomForSpring7",
                        "org.openrewrite.java.dependencies.UpgradeDependencyVersion",
                        "com.oracle.weblogic.rewrite.spring.framework.UseWebLogicJtaTransactionManagerForSpring7"),
                composite.getRecipeList().stream().map(Recipe::getName).collect(Collectors.toList()));

        UpgradeDependencyVersion springVersionPin = assertInstanceOf(
                UpgradeDependencyVersion.class, composite.getRecipeList().get(3));
        assertEquals("org.springframework", springVersionPin.getGroupId());
        assertEquals("*", springVersionPin.getArtifactId());
        assertEquals("7.0.8", springVersionPin.getNewVersion());
    }

    @Test
    void usesWebLogicManagerForDirectJavaConstruction() {
        rewriteRun(spec -> spec
                .recipe(recipe("com.oracle.weblogic.rewrite.spring.framework.UseWebLogicJtaTransactionManagerForSpring7"))
                .parser(JavaParser.fromJavaVersion().dependsOn(
                        GENERIC_TRANSACTION_MANAGER, WEBLOGIC_TRANSACTION_MANAGER)),
          java(
            """
              import org.springframework.transaction.jta.JtaTransactionManager;

              class TransactionConfiguration {
                  JtaTransactionManager transactionManager() {
                      return new JtaTransactionManager();
                  }
              }
              """,
            """
              import org.springframework.transaction.jta.JtaTransactionManager;
              import org.springframework.transaction.jta.WebLogicJtaTransactionManager;

              class TransactionConfiguration {
                  JtaTransactionManager transactionManager() {
                      return new WebLogicJtaTransactionManager();
                  }
              }
              """
          )
        );
    }

    @Test
    void usesWebLogicManagerForExactSpringXmlBean() {
        rewriteRun(spec -> spec.recipe(
                recipe("com.oracle.weblogic.rewrite.spring.framework.UseWebLogicJtaTransactionManagerForSpring7")),
          xml(
            """
              <beans xmlns="http://www.springframework.org/schema/beans">
                  <bean id="transactionManager"
                        class="org.springframework.transaction.jta.JtaTransactionManager"/>
              </beans>
              """,
            """
              <beans xmlns="http://www.springframework.org/schema/beans">
                  <bean id="transactionManager"
                        class="org.springframework.transaction.jta.WebLogicJtaTransactionManager"/>
              </beans>
              """,
            source -> source.path("src/main/resources/applicationContext.xml")
          )
        );
    }

    @Test
    void preservesExistingWebLogicManager() {
        rewriteRun(spec -> spec
                .recipe(recipe("com.oracle.weblogic.rewrite.spring.framework.UseWebLogicJtaTransactionManagerForSpring7"))
                .parser(JavaParser.fromJavaVersion().dependsOn(
                        GENERIC_TRANSACTION_MANAGER, WEBLOGIC_TRANSACTION_MANAGER)),
          java(
            """
              import org.springframework.transaction.jta.WebLogicJtaTransactionManager;

              class TransactionConfiguration {
                  WebLogicJtaTransactionManager transactionManager() {
                      return new WebLogicJtaTransactionManager();
                  }
              }
              """
          )
        );
    }

    @Test
    void preservesCustomSubclassAndParameterizedConstruction() {
        rewriteRun(spec -> spec
                .recipe(recipe("com.oracle.weblogic.rewrite.spring.framework.UseWebLogicJtaTransactionManagerForSpring7"))
                .parser(JavaParser.fromJavaVersion().dependsOn(
                        GENERIC_TRANSACTION_MANAGER, WEBLOGIC_TRANSACTION_MANAGER)),
          java(
            """
              import org.springframework.transaction.jta.JtaTransactionManager;

              class CustomTransactionManager extends JtaTransactionManager {
              }

              class TransactionConfiguration {
                  JtaTransactionManager transactionManager(Object provider) {
                      return new JtaTransactionManager(provider);
                  }
              }
              """
          )
        );
    }

    @Test
    void leavesUnrelatedXmlBeanUnchanged() {
        rewriteRun(spec -> spec.recipe(
                recipe("com.oracle.weblogic.rewrite.spring.framework.UseWebLogicJtaTransactionManagerForSpring7")),
          xml(
            """
              <beans xmlns="http://www.springframework.org/schema/beans">
                  <bean id="transactionManager" class="com.example.CustomTransactionManager"/>
              </beans>
              """,
            source -> source.path("src/main/resources/applicationContext.xml")
          )
        );
    }
}
