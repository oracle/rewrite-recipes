/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite.spring.framework;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaType;
import org.openrewrite.java.tree.Javadoc;
import org.openrewrite.java.tree.TypeTree;
import org.openrewrite.java.tree.TypeUtils;
import org.openrewrite.table.SearchResults;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Migrates documentation-only references to Spring's removed native Hibernate
 * open-session-in-view filter. Executable uses are reported for manual review
 * because the JPA replacement has different configuration semantics.
 */
public class MigrateOpenSessionInViewFilterForSpring7 extends Recipe {

    private static final String HIBERNATE_FILTER =
            "org.springframework.orm.hibernate5.support.OpenSessionInViewFilter";
    private static final String JPA_FILTER =
            "org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter";

    private final transient SearchResults migrationRisks = new SearchResults(this);

    @Override
    public String getDisplayName() {
        return "Migrate Spring open-session-in-view filter references";
    }

    @Override
    public String getDescription() {
        return "Replace documentation-only references to Spring's removed Hibernate 5 " +
                "`OpenSessionInViewFilter` with the JPA `OpenEntityManagerInViewFilter`. " +
                "Report executable uses for manual review because their configuration semantics differ.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public J.CompilationUnit visitCompilationUnit(J.CompilationUnit compilationUnit,
                                                           ExecutionContext ctx) {
                if (hasExecutableUse(compilationUnit)) {
                    String sourcePath = compilationUnit.getSourcePath().toString();
                    migrationRisks.insertRow(ctx, new SearchResults.Row(
                            sourcePath,
                            sourcePath,
                            HIBERNATE_FILTER,
                            "Spring 7 removed the native Hibernate OpenSessionInViewFilter. Review this " +
                                    "executable use before migrating to OpenEntityManagerInViewFilter because " +
                                    "SessionFactory and EntityManagerFactory configuration semantics differ.",
                            MigrateOpenSessionInViewFilterForSpring7.class.getName()));
                    return compilationUnit;
                }

                maybeRemoveImport(HIBERNATE_FILTER);
                return super.visitCompilationUnit(compilationUnit, ctx);
            }

            @Override
            public J.FieldAccess visitFieldAccess(J.FieldAccess fieldAccess, ExecutionContext ctx) {
                if (isInJavadoc() && fieldAccess.isFullyQualifiedClassReference(HIBERNATE_FILTER)) {
                    return (J.FieldAccess) TypeTree.build(JPA_FILTER).withPrefix(fieldAccess.getPrefix());
                }
                return super.visitFieldAccess(fieldAccess, ctx);
            }

            @Override
            public J.Identifier visitIdentifier(J.Identifier identifier, ExecutionContext ctx) {
                J.Identifier i = super.visitIdentifier(identifier, ctx);
                if (isInJavadoc() && TypeUtils.isOfClassType(i.getType(), HIBERNATE_FILTER)) {
                    maybeAddImport(JPA_FILTER, false);
                    return i.withSimpleName("OpenEntityManagerInViewFilter")
                            .withType(JavaType.ShallowClass.build(JPA_FILTER));
                }
                return i;
            }

            private boolean hasExecutableUse(J.CompilationUnit cu) {
                AtomicBoolean executableUse = new AtomicBoolean();
                new JavaIsoVisitor<AtomicBoolean>() {
                    @Override
                    public J.Identifier visitIdentifier(J.Identifier identifier, AtomicBoolean found) {
                        if (!found.get() &&
                                getCursor().firstEnclosing(J.Import.class) == null &&
                                getCursor().firstEnclosing(Javadoc.class) == null &&
                                TypeUtils.isOfClassType(identifier.getType(), HIBERNATE_FILTER)) {
                            found.set(true);
                        }
                        return identifier;
                    }
                }.visit(cu, executableUse);
                return executableUse.get();
            }

            private boolean isInJavadoc() {
                return getCursor().firstEnclosing(Javadoc.class) != null;
            }
        };
    }
}
