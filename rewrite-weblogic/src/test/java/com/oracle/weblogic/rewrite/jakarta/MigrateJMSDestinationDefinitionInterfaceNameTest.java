/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite.jakarta;

import org.junit.jupiter.api.Test;
import org.openrewrite.Recipe;
import org.openrewrite.config.Environment;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class MigrateJMSDestinationDefinitionInterfaceNameTest implements RewriteTest {

    private static final String LEGACY_JMS_DESTINATION_DEFINITION = """
      package javax.jms;
      public @interface JMSDestinationDefinition {
          String name();
          String interfaceName();
          String destinationName() default "";
      }
      """;

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new MigrateJMSDestinationDefinitionInterfaceName())
                .parser(JavaParser.fromJavaVersion().classpath("jakarta.jakartaee-api"));
    }

    private Recipe jakartaEE11Recipe() {
        return Environment.builder()
                .scanRuntimeClasspath()
                .build()
                .activateRecipes("com.oracle.weblogic.rewrite.jakarta.MigrateToJakartaEE11");
    }

    @Test
    void jakartaEE11CompositeMigratesLegacyDestinationInterfaceInOneCycle() {
        rewriteRun(spec -> spec
                .recipe(jakartaEE11Recipe())
                .cycles(2)
                .expectedCyclesThatMakeChanges(1)
                .parser(JavaParser.fromJavaVersion()
                        .classpath("jakarta.jakartaee-api")
                        .dependsOn(LEGACY_JMS_DESTINATION_DEFINITION)),
          java(
            """
              import javax.jms.JMSDestinationDefinition;

              @JMSDestinationDefinition(
                      name = "java:global/jms/orders",
                      interfaceName = "javax.jms.Queue",
                      destinationName = "orders")
              class OrderManager {
              }
              """,
            """
              import jakarta.jms.JMSDestinationDefinition;

              @JMSDestinationDefinition(
                      name = "java:global/jms/orders",
                      interfaceName = "jakarta.jms.Queue",
                      destinationName = "orders")
              class OrderManager {
              }
              """
          )
        );
    }

    @Test
    void migratesQueueAndTopicInterfaceNamesIdempotently() {
        rewriteRun(spec -> spec.cycles(2).expectedCyclesThatMakeChanges(1),
          java(
            """
              import jakarta.jms.JMSDestinationDefinition;

              @JMSDestinationDefinition(
                      name = "java:global/jms/orders",
                      interfaceName = "javax.jms.Queue")
              class QueueDestination {
              }

              @JMSDestinationDefinition(
                      interfaceName = "javax.jms.Topic",
                      name = "java:global/jms/events")
              class TopicDestination {
              }
              """,
            """
              import jakarta.jms.JMSDestinationDefinition;

              @JMSDestinationDefinition(
                      name = "java:global/jms/orders",
                      interfaceName = "jakarta.jms.Queue")
              class QueueDestination {
              }

              @JMSDestinationDefinition(
                      interfaceName = "jakarta.jms.Topic",
                      name = "java:global/jms/events")
              class TopicDestination {
              }
              """
          )
        );
    }

    @Test
    void leavesAlreadyMigratedAndUnrelatedStringsUnchanged() {
        rewriteRun(
          java(
            """
              import jakarta.jms.JMSDestinationDefinition;

              @JMSDestinationDefinition(
                      name = "java:global/jms/orders",
                      interfaceName = "jakarta.jms.Queue",
                      description = "javax.jms.Topic")
              class Destination {
                  private static final String LEGACY_QUEUE_DOCUMENTATION = "javax.jms.Queue";
              }
              """
          )
        );
    }
}
