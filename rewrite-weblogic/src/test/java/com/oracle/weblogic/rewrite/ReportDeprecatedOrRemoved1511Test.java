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

import static org.openrewrite.java.Assertions.java;

/**
 * Verifies the OpenRewrite recipe 'com.oracle.weblogic.rewrite.ReportDeprecatedOrRemoved1511'.
 * Ensures that uses of WebLogic APIs deprecated or removed in WebLogic Server 15.1.1 are reported
 * as search results in the expected sources, including federation/SAML providers and relevant
 * management MBeans, as well as general deprecations under weblogic.*.
 */
public class ReportDeprecatedOrRemoved1511Test implements RewriteTest {

    private Recipe recipe() {
        return Environment.builder()
                .scanRuntimeClasspath("com.oracle.weblogic")
                .build()
                .activateRecipes("com.oracle.weblogic.rewrite.ReportDeprecatedOrRemoved1511");
    }

    @Test
    void flagsDeprecatedWeblogicTypeUsage() {
        rewriteRun(spec -> spec.recipe(recipe()),
            // Deprecated member under weblogic.* should be reported via FindDeprecatedUses
            java(
                """
                package weblogic;
                public class OldClass1511 {
                    @Deprecated public void old() {}
                }
                """
            ),
            java(
                """
                package com.example;

                import weblogic.OldClass1511;

                class UseDeprecated1511 {
                    void m() { new OldClass1511().old(); }
                }
                """,
                """
                package com.example;

                import weblogic.OldClass1511;

                class UseDeprecated1511 {
                    void m() { /*~~>*/new OldClass1511().old(); }
                }
                """
            )
        );
    }

    @Test
    void flagsFederationServicesMBean() {
        rewriteRun(spec -> spec.recipe(recipe()),
            java(
                """
                package weblogic.management.configuration;
                public interface FederationServicesMBean {}
                """
            ),
            java(
                """
                package com.example;

                import weblogic.management.configuration.FederationServicesMBean;

                class UseFederationServicesMBean {
                    FederationServicesMBean m;
                }
                """,
                """
                package com.example;

                import weblogic.management.configuration.FederationServicesMBean;

                class UseFederationServicesMBean {
                    /*~~>*/FederationServicesMBean m;
                }
                """
            )
        );
    }

    @Test
    void flagsSecurityProvidersSamlDoubleDotWildcard() {
        rewriteRun(spec -> spec.recipe(recipe()),
            // Any type in weblogic.security.providers.saml..* should be flagged
            java(
                """
                package weblogic.security.providers.saml.v2;
                public class SomeSamlType {}
                """
            ),
            java(
                """
                package com.example;

                import weblogic.security.providers.saml.v2.SomeSamlType;

                class UseSamlType {
                    SomeSamlType t;
                }
                """,
                """
                package com.example;

                import weblogic.security.providers.saml.v2.SomeSamlType;

                class UseSamlType {
                    /*~~>*/SomeSamlType t;
                }
                """
            )
        );
    }

    @Test
    void flagsWseeSamlPKISAMLCredentialProvider() {
        rewriteRun(spec -> spec.recipe(recipe()),
            java(
                """
                package weblogic.wsee.security.saml;
                public interface PKISAMLCredentialProvider {}
                """
            ),
            java(
                """
                package com.example;

                import weblogic.wsee.security.saml.PKISAMLCredentialProvider;

                class UsePKIProvider {
                    PKISAMLCredentialProvider p;
                }
                """,
                """
                package com.example;

                import weblogic.wsee.security.saml.PKISAMLCredentialProvider;

                class UsePKIProvider {
                    /*~~>*/PKISAMLCredentialProvider p;
                }
                """
            )
        );
    }

    @Test
    void flagsWseeSamlSAMLCredentialProvider() {
        rewriteRun(spec -> spec.recipe(recipe()),
            java(
                """
                package weblogic.wsee.security.saml;
                public interface SAMLCredentialProvider {}
                """
            ),
            java(
                """
                package com.example;

                import weblogic.wsee.security.saml.SAMLCredentialProvider;

                class UseSamlProvider {
                    SAMLCredentialProvider p;
                }
                """,
                """
                package com.example;

                import weblogic.wsee.security.saml.SAMLCredentialProvider;

                class UseSamlProvider {
                    /*~~>*/SAMLCredentialProvider p;
                }
                """
            )
        );
    }
}
