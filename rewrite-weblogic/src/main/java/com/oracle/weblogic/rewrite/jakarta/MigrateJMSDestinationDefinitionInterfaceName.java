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
 * Migrates legacy fully qualified JMS destination interface names stored in
 * {@code JMSDestinationDefinition.interfaceName} string attributes.
 */
public class MigrateJMSDestinationDefinitionInterfaceName extends Recipe {

    private static final String JMS_DESTINATION_DEFINITION =
            "jakarta.jms.JMSDestinationDefinition";
    private static final String LEGACY_QUEUE = "javax.jms.Queue";
    private static final String LEGACY_TOPIC = "javax.jms.Topic";
    private static final String JAKARTA_QUEUE = "jakarta.jms.Queue";
    private static final String JAKARTA_TOPIC = "jakarta.jms.Topic";

    @Override
    public String getDisplayName() {
        return "Migrate Jakarta Messaging destination definition interface names";
    }

    @Override
    public String getDescription() {
        return "Replace legacy `javax.jms.Queue` and `javax.jms.Topic` string values used by " +
                "`JMSDestinationDefinition.interfaceName` with their Jakarta Messaging equivalents.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public J.Annotation visitAnnotation(J.Annotation annotation, ExecutionContext ctx) {
                J.Annotation a = super.visitAnnotation(annotation, ctx);
                if (!TypeUtils.isOfClassType(a.getType(), JMS_DESTINATION_DEFINITION)) {
                    return a;
                }

                return a.withArguments(ListUtils.map(a.getArguments(), this::migrateInterfaceName));
            }

            private Expression migrateInterfaceName(Expression argument) {
                if (!(argument instanceof J.Assignment)) {
                    return argument;
                }

                J.Assignment assignment = (J.Assignment) argument;
                if (!isAttribute(assignment, "interfaceName") ||
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
