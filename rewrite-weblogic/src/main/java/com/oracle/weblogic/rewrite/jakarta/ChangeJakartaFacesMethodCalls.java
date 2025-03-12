/*
 * Copyright (c) 2025 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite.jakarta;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.openrewrite.ExecutionContext;
import org.openrewrite.NlsRewrite;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;

import java.util.regex.Pattern;

/**
 * ChangeJakartaFacesMethodCalls updates the following deprecated method calls
 * 1. Application.createValueBinding("#{somestringexpression}") =>
 *       Application.createValueExpression(FacesContext.getCurrentInstance().getELContext(), "#{somestringexpression}", Object.class)
 * 2. ValueExpression.getValue(someFacesContext) => ValueExpression.getValue(someFacesContext.getELContext())
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class ChangeJakartaFacesMethodCalls extends Recipe {

    private static final String GET_EXPRESSION_FACTORY_METHOD_NAME = "getExpressionFactory";
    private static final String CREATE_VALUE_EXPRESSION_METHOD_NAME = "createValueExpression";
    private static final String GET_EL_CONTEXT_METHOD_CALL = "FacesContext.getCurrentInstance().getELContext()";

    private static final MethodMatcher createValueBindingMatcher = new MethodMatcher(
            "*.faces.application.Application createValueBinding(java.lang.String)");
    private static final MethodMatcher getValueMatcher = new MethodMatcher(
            "jakarta.el.ValueExpression getValue(jakarta.faces.context.FacesContext)");
    private static final Pattern VALUE_EXPRESSION_PATTERN
            = Pattern.compile("jakarta.el.ValueExpression");
    private static final Pattern FACES_CONTEXT_PATTERN = Pattern.compile("jakarta.faces.context.FacesContext");

    @Override
    public @NlsRewrite.DisplayName @NotNull String getDisplayName() {
        return "ChangeJakartaFacesMethodCalls";
    }

    @Override
    public @NlsRewrite.Description @NotNull String getDescription() {
        return "Handle method call changes for deprecated Faces/EL methods with different arguments from the original.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public J.MethodInvocation visitMethodInvocation(J.MethodInvocation methodCall, ExecutionContext ctx) {
                J.MethodInvocation m = methodCall;
                // Find out whether method invocation matches
                if (createValueBindingMatcher.matches(methodCall)) {
                    m = replaceCreateValueBinding(methodCall);
                } else if (isGetValueMethod(methodCall)) {
                    m = changeGetValueArgument(methodCall);
                }
                return super.visitMethodInvocation(m, ctx);
            }

            private J.MethodInvocation changeGetValueArgument(J.MethodInvocation methodCall) {
                J.Identifier objectIdentifier = (J.Identifier) methodCall.getSelect();
                if (objectIdentifier == null) {
                    return methodCall;
                }
                Expression firstArg = methodCall.getArguments().get(0);
                if (firstArg.getType() != null && firstArg.getType().isAssignableFrom(FACES_CONTEXT_PATTERN)) {
                    final String newArg = methodCall.getArguments().get(0) + ".getELContext()";
                    final String newGetValueTemplateStr = String.format("%s.%s(%s)",
                            objectIdentifier.getSimpleName(),
                            methodCall.getSimpleName(),
                            newArg);
                    JavaTemplate newGetValueTemplate = JavaTemplate
                            .builder(newGetValueTemplateStr).build();
                    return newGetValueTemplate.apply(getCursor(), methodCall.getCoordinates().replace());
                }
                return methodCall;
            }

            private J.MethodInvocation replaceCreateValueBinding(J.MethodInvocation methodCall) {
                J.Identifier objectIdentifier = (J.Identifier) methodCall.getSelect();
                if (objectIdentifier == null) {
                    return methodCall;
                }
                final String newMethodCallTemplate = String.format(
                        "%s.%s().%s(%s, #{any(java.lang.String)}, Object.class)",
                        objectIdentifier.getSimpleName(),
                        GET_EXPRESSION_FACTORY_METHOD_NAME,
                        CREATE_VALUE_EXPRESSION_METHOD_NAME,
                        // 1st argument is an EL context
                        GET_EL_CONTEXT_METHOD_CALL
                        // 2nd argument will be filled in with the String argument from the original method call
                        // 3rd argument is already inlined in the format string above.
                );
                JavaTemplate createValueExpressionTemplate = JavaTemplate
                        .builder(newMethodCallTemplate)
                        .imports("jakarta.faces.context.FacesContext")
                        .build();

                // apply the template and substitute with the argument from the original method call
                return createValueExpressionTemplate.apply(
                        getCursor(), methodCall.getCoordinates().replace(), methodCall.getArguments().get(0));
            }
        };
    }

    private boolean isGetValueMethod(J.MethodInvocation methodCall) {
        if (methodCall.getMethodType() != null) {
            return getValueMatcher.matches(methodCall);
        }
        Expression objForMethodCall = methodCall.getSelect();
        // in unit tests, the method type comes through as null, so the matcher does not work.
        return (objForMethodCall != null && objForMethodCall.getType() != null &&
                objForMethodCall.getType().isAssignableFrom(VALUE_EXPRESSION_PATTERN) &&
                methodCall.getArguments().size() == 1 &&
                methodCall.getArguments().get(0).getType() != null &&
                methodCall.getArguments().get(0).getType().isAssignableFrom(FACES_CONTEXT_PATTERN)
        );
    }
}
