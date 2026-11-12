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
import org.openrewrite.test.RewriteTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openrewrite.java.Assertions.java;

class ReportDeprecatedOrRemoved2610Test implements RewriteTest {

    private Recipe recipe() {
        return Environment.builder()
                .scanRuntimeClasspath("com.oracle.weblogic")
                .build()
                .activateRecipes("com.oracle.weblogic.rewrite.ReportDeprecatedOrRemoved2610");
    }

    @Test
    void includesPreviousWebLogicReports() {
        assertTrue(recipe().getRecipeList().stream()
                .anyMatch(child -> "com.oracle.weblogic.rewrite.ReportDeprecatedOrRemoved1511"
                        .equals(child.getName())));
    }

    @Test
    void reportsPreviouslyRemovedFederationServicesMBean() {
        rewriteRun(spec -> spec.recipe(recipe()),
          java(
            """
              package weblogic.management.configuration;
              public interface FederationServicesMBean {
              }
              """
          ),
          java(
            """
              package com.example;

              import weblogic.management.configuration.FederationServicesMBean;

              class FederationConfiguration {
                  FederationServicesMBean federationServices;
              }
              """,
            """
              package com.example;

              import weblogic.management.configuration.FederationServicesMBean;

              class FederationConfiguration {
                  /*~~>*/FederationServicesMBean federationServices;
              }
              """
          )
        );
    }

    @Test
    void reportsDeprecatedWebLogicApiUsage() {
        rewriteRun(spec -> spec.recipe(recipe()),
          java(
            """
              package weblogic.example;

              public class DeprecatedWebLogicApi {
                  @Deprecated
                  public void legacyOperation() {
                  }
              }
              """
          ),
          java(
            """
              package com.example;

              import weblogic.example.DeprecatedWebLogicApi;

              class WebLogicClient {
                  void invoke(DeprecatedWebLogicApi api) {
                      api.legacyOperation();
                  }
              }
              """,
            """
              package com.example;

              import weblogic.example.DeprecatedWebLogicApi;

              class WebLogicClient {
                  void invoke(DeprecatedWebLogicApi api) {
                      /*~~>*/api.legacyOperation();
                  }
              }
              """
          )
        );
    }

    @Test
    void doesNotReportUnrelatedDeprecatedApiUsage() {
        rewriteRun(spec -> spec.recipe(recipe()),
          java(
            """
              package org.example.library;

              public class DeprecatedLibraryApi {
                  @Deprecated
                  public void legacyOperation() {
                  }
              }
              """
          ),
          java(
            """
              package com.example;

              import org.example.library.DeprecatedLibraryApi;

              class LibraryClient {
                  void invoke(DeprecatedLibraryApi api) {
                      api.legacyOperation();
                  }
              }
              """
          )
        );
    }
}
