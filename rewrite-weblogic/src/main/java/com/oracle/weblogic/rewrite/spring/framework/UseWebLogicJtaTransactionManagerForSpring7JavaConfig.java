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
import org.openrewrite.java.ChangeType;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.TypeUtils;

/**
 * Replaces direct construction of Spring's generic JTA transaction manager with
 * Spring's WebLogic-specific transaction manager. Custom subclasses and
 * parameterized constructions are intentionally left unchanged.
 */
public class UseWebLogicJtaTransactionManagerForSpring7JavaConfig extends Recipe {

    private static final String JTA_TRANSACTION_MANAGER =
            "org.springframework.transaction.jta.JtaTransactionManager";
    private static final String WEBLOGIC_JTA_TRANSACTION_MANAGER =
            "org.springframework.transaction.jta.WebLogicJtaTransactionManager";

    @Override
    public String getDisplayName() {
        return "Use the Spring WebLogic JTA transaction manager in Java configuration";
    }

    @Override
    public String getDescription() {
        return "Replace direct no-argument construction of Spring's generic `JtaTransactionManager` with " +
                "Spring's `WebLogicJtaTransactionManager` for WebLogic-specific Spring 7 migrations.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public J.NewClass visitNewClass(J.NewClass newClass, ExecutionContext ctx) {
                J.NewClass n = super.visitNewClass(newClass, ctx);
                if (!TypeUtils.isOfClassType(n.getType(), JTA_TRANSACTION_MANAGER) || hasConstructorArguments(n)) {
                    return n;
                }

                J.NewClass replacement = (J.NewClass) new ChangeType(
                        JTA_TRANSACTION_MANAGER, WEBLOGIC_JTA_TRANSACTION_MANAGER, true)
                        .getVisitor()
                        .visit(n, ctx, getCursor().getParentOrThrow());
                maybeAddImport(WEBLOGIC_JTA_TRANSACTION_MANAGER, false);
                maybeRemoveImport(JTA_TRANSACTION_MANAGER);
                return replacement;
            }

            private boolean hasConstructorArguments(J.NewClass newClass) {
                return !newClass.getArguments().isEmpty() &&
                        !(newClass.getArguments().size() == 1 && newClass.getArguments().get(0) instanceof J.Empty);
            }
        };
    }
}
