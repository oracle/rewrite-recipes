/*
 * Copyright (c) 2025 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite.jakarta;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;
import org.openrewrite.test.TypeValidation;

import java.io.File;
import java.nio.file.Files;

import static org.openrewrite.java.Assertions.java;

public class ChangeJakartaFacesMethodCallsTest implements RewriteTest {
    private static final String JAKARTA_EE9_API_JAR_NAME = "jakarta.jakartaee-api";
    private static final String JAKARTA_WEBAPI9_JAR_NAME = "jakarta.jakartaee-web-api";
    @Override
    public void defaults(RecipeSpec spec) {
        try {
            spec.parser(JavaParser.fromJavaVersion()
                .classpath(JAKARTA_EE9_API_JAR_NAME, JAKARTA_WEBAPI9_JAR_NAME))
                    .recipe(new ChangeJakartaFacesMethodCalls())
                    // the code provided for the "before" case is after partial transformation by other Rewrite recipes,
                    // before it gets to this recipe. That code is not expected to compile, so we skip type validations.
                    .typeValidationOptions(TypeValidation.none());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testValueExpressionAndBinding() {
        rewriteRun(
                java(
                """
                            package com.mypackage;

                            import jakarta.el.ValueExpression;
                            import jakarta.faces.application.Application;
                            import jakarta.faces.context.FacesContext;

                            public class MyClass {
                                public void getStockNews() {
                                    FacesContext facesContext = FacesContext.getCurrentInstance();
                                    Application application = facesContext.getApplication();
                                    ValueExpression binding = application.createValueBinding("#{stockTickerBeanVal}");
                                    Object b = binding.getValue(facesContext);
                                }
                            }
                        """,
                  """
                            package com.mypackage;

                            import jakarta.el.ValueExpression;
                            import jakarta.faces.application.Application;
                            import jakarta.faces.context.FacesContext;

                            public class MyClass {
                                public void getStockNews() {
                                    FacesContext facesContext = FacesContext.getCurrentInstance();
                                    Application application = facesContext.getApplication();
                                    ValueExpression binding = application.getExpressionFactory().createValueExpression(FacesContext.getCurrentInstance().getELContext(), "#{stockTickerBeanVal}", Object.class);
                                    Object b = binding.getValue(facesContext.getELContext());
                                }
                            }
                        """)
        );
    }

    /**
     * Test a case that has references to other methods which have the same name as the ones the recipe matches for,
     * but not on the same data types. These should not match and there should be no change to the code.
     */
    @Test
    public void testShouldNotMakeChanges() {
        rewriteRun(java(
                """
                            package com.mypackage;

                            public class MyClass {
                                public void getStockNews() {
                                    Application application = new Application();
                                    ValueExpression ve = application.createValueBinding("#{stockTickerBeanVal}");
                                    String s = ve.getValue();
                                }
                            }

                            class Application {
                                public ValueExpression createValueBinding(String val) {
                                    return new ValueExpression("dummy return value");
                                }
                            }

                            class ValueExpression {
                                private String value;
                                public ValueExpression(String value) {
                                    this.value = value;
                                }
                                public String getValue() {
                                    return this.value;
                                }
                            }
                        """
        ));
    }
}
