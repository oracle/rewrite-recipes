/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite.jakarta;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.ListUtils;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.TypeUtils;

/**
 * Migrates legacy JMS destination type names in Jakarta Enterprise Beans
 * message-driven bean activation configuration.
 */
public class MigrateActivationConfigPropertyDestinationType extends Recipe {

    private static final String ACTIVATION_CONFIG_PROPERTY = "jakarta.ejb.ActivationConfigProperty";
    private static final String DESTINATION_TYPE = "destinationType";
    private static final String LEGACY_QUEUE = "javax.jms.Queue";
    private static final String LEGACY_TOPIC = "javax.jms.Topic";
    private static final String JAKARTA_QUEUE = "jakarta.jms.Queue";
    private static final String JAKARTA_TOPIC = "jakarta.jms.Topic";

    @Override
    public String getDisplayName() {
        return "Migrate MDB activation configuration destination types to Jakarta Messaging";
    }

    @Override
    public String getDescription() {
        return "Replace legacy `javax.jms.Queue` and `javax.jms.Topic` string values used by the " +
                "`destinationType` MDB activation property with their Jakarta Messaging equivalents.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public J.Annotation visitAnnotation(J.Annotation annotation, ExecutionContext ctx) {
                J.Annotation a = super.visitAnnotation(annotation, ctx);
                if (!TypeUtils.isOfClassType(a.getType(), ACTIVATION_CONFIG_PROPERTY) ||
                        !DESTINATION_TYPE.equals(stringAttributeValue(a, "propertyName"))) {
                    return a;
                }

                return a.withArguments(ListUtils.map(a.getArguments(), argument ->
                        migratePropertyValue(argument)));
            }

            private Expression migratePropertyValue(Expression argument) {
                if (!(argument instanceof J.Assignment)) {
                    return argument;
                }

                J.Assignment assignment = (J.Assignment) argument;
                if (!isAttribute(assignment, "propertyValue") ||
                        !(assignment.getAssignment() instanceof J.Literal)) {
                    return argument;
                }

                J.Literal value = (J.Literal) assignment.getAssignment();
                String replacement = replacementFor(value.getValue());
                if (replacement == null) {
                    return argument;
                }

                return assignment.withAssignment(value
                        .withValue(replacement)
                        .withValueSource("\"" + replacement + "\""));
            }

            private String stringAttributeValue(J.Annotation annotation, String attributeName) {
                for (Expression argument : annotation.getArguments()) {
                    if (argument instanceof J.Assignment) {
                        J.Assignment assignment = (J.Assignment) argument;
                        if (isAttribute(assignment, attributeName) &&
                                assignment.getAssignment() instanceof J.Literal) {
                            Object value = ((J.Literal) assignment.getAssignment()).getValue();
                            return value instanceof String ? (String) value : null;
                        }
                    }
                }
                return null;
            }

            private boolean isAttribute(J.Assignment assignment, String attributeName) {
                return assignment.getVariable() instanceof J.Identifier &&
                        attributeName.equals(((J.Identifier) assignment.getVariable()).getSimpleName());
            }

            private String replacementFor(Object value) {
                if (LEGACY_QUEUE.equals(value)) {
                    return JAKARTA_QUEUE;
                }
                if (LEGACY_TOPIC.equals(value)) {
                    return JAKARTA_TOPIC;
                }
                return null;
            }
        };
    }
}
