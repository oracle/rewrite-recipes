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

class MigrateActivationConfigPropertyDestinationTypeTest implements RewriteTest {

    private static final String LEGACY_ACTIVATION_CONFIG_PROPERTY = """
      package javax.ejb;
      public @interface ActivationConfigProperty {
          String propertyName();
          String propertyValue();
      }
      """;

    private static final String LEGACY_MESSAGE_DRIVEN = """
      package javax.ejb;
      public @interface MessageDriven {
          ActivationConfigProperty[] activationConfig() default {};
      }
      """;

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new MigrateActivationConfigPropertyDestinationType())
                .parser(JavaParser.fromJavaVersion().classpath("jakarta.jakartaee-api"));
    }

    private Recipe jakartaEE11Recipe() {
        return Environment.builder()
                .scanRuntimeClasspath()
                .build()
                .activateRecipes("com.oracle.weblogic.rewrite.jakarta.MigrateToJakartaEE11");
    }

    @Test
    void jakartaEE11CompositeMigratesLegacyMdbDestinationTypeInOneCycle() {
        rewriteRun(spec -> spec
                .recipe(jakartaEE11Recipe())
                .cycles(2)
                .expectedCyclesThatMakeChanges(1)
                .parser(JavaParser.fromJavaVersion()
                        .classpath("jakarta.jakartaee-api")
                        .dependsOn(LEGACY_ACTIVATION_CONFIG_PROPERTY, LEGACY_MESSAGE_DRIVEN)),
          java(
            """
              import javax.ejb.ActivationConfigProperty;
              import javax.ejb.MessageDriven;

              @MessageDriven(activationConfig = {
                      @ActivationConfigProperty(
                              propertyName = "destinationType",
                              propertyValue = "javax.jms.Queue")
              })
              class CargoHandledConsumer {
              }
              """,
            """
              import jakarta.ejb.ActivationConfigProperty;
              import jakarta.ejb.MessageDriven;

              @MessageDriven(activationConfig = {
                      @ActivationConfigProperty(
                              propertyName = "destinationType",
                              propertyValue = "jakarta.jms.Queue")
              })
              class CargoHandledConsumer {
              }
              """
          )
        );
    }

    @Test
    void migratesQueueAndTopicDestinationTypesIdempotently() {
        rewriteRun(spec -> spec.cycles(2).expectedCyclesThatMakeChanges(1),
          java(
            """
              import jakarta.ejb.ActivationConfigProperty;
              import jakarta.ejb.MessageDriven;

              @MessageDriven(activationConfig = {
                      @ActivationConfigProperty(
                              propertyName = "destinationType",
                              propertyValue = "javax.jms.Queue")
              })
              class QueueConsumer {
              }

              @MessageDriven(activationConfig = {
                      @ActivationConfigProperty(
                              propertyValue = "javax.jms.Topic",
                              propertyName = "destinationType")
              })
              class TopicConsumer {
              }
              """,
            """
              import jakarta.ejb.ActivationConfigProperty;
              import jakarta.ejb.MessageDriven;

              @MessageDriven(activationConfig = {
                      @ActivationConfigProperty(
                              propertyName = "destinationType",
                              propertyValue = "jakarta.jms.Queue")
              })
              class QueueConsumer {
              }

              @MessageDriven(activationConfig = {
                      @ActivationConfigProperty(
                              propertyValue = "jakarta.jms.Topic",
                              propertyName = "destinationType")
              })
              class TopicConsumer {
              }
              """
          )
        );
    }

    @Test
    void leavesUnrelatedActivationPropertiesAndStringLiteralsUnchanged() {
        rewriteRun(
          java(
            """
              import jakarta.ejb.ActivationConfigProperty;
              import jakarta.ejb.MessageDriven;

              @MessageDriven(activationConfig = {
                      @ActivationConfigProperty(
                              propertyName = "destinationLookup",
                              propertyValue = "javax.jms.Queue"),
                      @ActivationConfigProperty(
                              propertyName = "destinationType",
                              propertyValue = "com.example.CustomDestination")
              })
              class Consumer {
                  private static final String LEGACY_QUEUE_DOCUMENTATION = "javax.jms.Queue";
              }
              """
          )
        );
    }
}
