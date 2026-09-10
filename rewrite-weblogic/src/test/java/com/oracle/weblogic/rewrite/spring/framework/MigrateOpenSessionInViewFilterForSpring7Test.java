/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite.spring.framework;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.java.JavaParser;
import org.openrewrite.table.SearchResults;
import org.openrewrite.test.RewriteTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openrewrite.java.Assertions.java;

class MigrateOpenSessionInViewFilterForSpring7Test implements RewriteTest {

    private static final String HIBERNATE_FILTER =
            "org.springframework.orm.hibernate5.support.OpenSessionInViewFilter";

    private static final String LEGACY_FILTER_SOURCE =
            """
              package org.springframework.orm.hibernate5.support;

              public class OpenSessionInViewFilter {
              }
              """;

    private static final String JPA_FILTER_SOURCE =
            """
              package org.springframework.orm.jpa.support;

              public class OpenEntityManagerInViewFilter {
              }
              """;

    @Override
    public void defaults(org.openrewrite.test.RecipeSpec spec) {
        spec.recipe(new MigrateOpenSessionInViewFilterForSpring7())
                .parser(JavaParser.fromJavaVersion().dependsOn(
                        LEGACY_FILTER_SOURCE, JPA_FILTER_SOURCE));
    }

    @DocumentExample
    @Test
    void migratesPetClinicDocumentationOnlyReference() {
        rewriteRun(
          java(
            """
              package org.springframework.samples.petclinic.owner;

              import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;

              class JpaOwnerRepositoryImpl {
                  /**
                   * Retrieve an owner with pets and visits, either by:
                   * - Turning on lazy-loading and using {@link OpenSessionInViewFilter}
                   * - Using a single query with join fetch.
                   */
                  void findById() {
                  }
              }
              """,
            """
              package org.springframework.samples.petclinic.owner;

              import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

              class JpaOwnerRepositoryImpl {
                  /**
                   * Retrieve an owner with pets and visits, either by:
                   * - Turning on lazy-loading and using {@link OpenEntityManagerInViewFilter}
                   * - Using a single query with join fetch.
                   */
                  void findById() {
                  }
              }
              """
          )
        );
    }

    @Test
    void reportsExecutableUseWithoutChangingSource() {
        rewriteRun(
          spec -> spec.dataTable(SearchResults.Row.class, rows -> {
              assertEquals(1, rows.size());
              assertEquals(HIBERNATE_FILTER, rows.get(0).getResult());
              assertEquals(MigrateOpenSessionInViewFilterForSpring7.class.getName(),
                      rows.get(0).getRecipe());
          }),
          java(
            """
              package com.example;

              import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;

              class WebConfiguration {
                  private final OpenSessionInViewFilter filter = new OpenSessionInViewFilter();
              }
              """
          )
        );
    }

    @Test
    void migratesFullyQualifiedDocumentationReference() {
        rewriteRun(
          java(
            """
              package com.example;

              class JpaRepository {
                  /**
                   * See {@link org.springframework.orm.hibernate5.support.OpenSessionInViewFilter}.
                   */
                  void findById() {
                  }
              }
              """,
            """
              package com.example;

              class JpaRepository {
                  /**
                   * See {@link org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter}.
                   */
                  void findById() {
                  }
              }
              """
          )
        );
    }

    @Test
    void removesUnusedLegacyImport() {
        rewriteRun(
          java(
            """
              package com.example;

              import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;

              class JpaRepository {
              }
              """,
            """
              package com.example;

              class JpaRepository {
              }
              """
          )
        );
    }

    @Test
    void leavesUnrelatedFilterReferencesUnchanged() {
        rewriteRun(
          java(
            """
              package com.example;

              class WebConfiguration {
                  /** Uses the application's own open-session strategy. */
                  void configure() {
                  }
              }
              """
          )
        );
    }
}
