/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaType;
import org.openrewrite.java.tree.TypeUtils;
import org.openrewrite.table.SearchResults;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Reports removed Java EE and Jakarta Managed Beans annotations without
 * adding persistent search-marker comments to application source.
 */
public class FindManagedBeanAnnotations2610 extends Recipe {

    private static final Set<String> MANAGED_BEAN_TYPES = new HashSet<>(Arrays.asList(
            "javax.annotation.ManagedBean",
            "jakarta.annotation.ManagedBean"));
    private static final String RISK_DESCRIPTION =
            "The Managed Beans technology was removed from Jakarta EE 11. Replace this annotation with a supported " +
                    "bean-defining annotation, such as a CDI scope and `jakarta.inject.Named`.";

    private final transient SearchResults searchResults = new SearchResults(this);

    @Override
    public String getDisplayName() {
        return "Find removed Managed Beans annotations";
    }

    @Override
    public String getDescription() {
        return "Report Java EE and Jakarta Managed Beans annotations that require migration to CDI for Jakarta EE 11 " +
                "without changing application source.";
    }

    @Override
    public Set<String> getTags() {
        return new HashSet<>(Arrays.asList("weblogic", "jakarta", "managed-beans", "cdi"));
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public J.Annotation visitAnnotation(J.Annotation annotation, ExecutionContext ctx) {
                J.Annotation a = super.visitAnnotation(annotation, ctx);
                JavaType.FullyQualified type = TypeUtils.asFullyQualified(a.getType());
                if (type == null || !MANAGED_BEAN_TYPES.contains(type.getFullyQualifiedName())) {
                    return a;
                }

                J.CompilationUnit source = getCursor().firstEnclosingOrThrow(J.CompilationUnit.class);
                String sourcePath = source.getSourcePath().toString();
                searchResults.insertRow(ctx, new SearchResults.Row(
                        sourcePath,
                        sourcePath,
                        type.getFullyQualifiedName(),
                        RISK_DESCRIPTION,
                        getName()));
                return a;
            }
        };
    }
}
