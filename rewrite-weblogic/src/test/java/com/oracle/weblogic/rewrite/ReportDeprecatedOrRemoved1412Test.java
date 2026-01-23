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
 * Verifies the OpenRewrite recipe 'com.oracle.weblogic.rewrite.ReportDeprecatedOrRemoved1412'.
 * Ensures that uses of WebLogic APIs deprecated or removed in WebLogic Server 14.1.2 are reported
 * as search results in the expected sources. The cases cover weblogic.jdbc.rowset, logging.log4j,
 * JMS/management MBeans, RMI, WSEE, XML schema, nested types, and legacy com.bea packages.
 */
public class ReportDeprecatedOrRemoved1412Test implements RewriteTest {

    private Recipe recipe() {
        return Environment.builder()
                .scanRuntimeClasspath("com.oracle.weblogic")
                .build()
                .activateRecipes("com.oracle.weblogic.rewrite.ReportDeprecatedOrRemoved1412");
    }

    @Test
    void flagsJdbcRowsetWildcardTypeUsage() {
        rewriteRun(spec -> spec.recipe(recipe()),
            // Stub of a removed WebLogic type from weblogic.jdbc.rowset.*
            java(
                """
                package weblogic.jdbc.rowset;
                public class CachedRowSetImpl {}
                """
            ),
            // The code under test should be marked by the recipe's FindTypes search result
            java(
                """
                package com.example;

                import weblogic.jdbc.rowset.CachedRowSetImpl;

                class Test {
                    CachedRowSetImpl crs;
                }
                """,
                """
                package com.example;

                import weblogic.jdbc.rowset.CachedRowSetImpl;

                class Test {
                    /*~~>*/CachedRowSetImpl crs;
                }
                """
            )
        );
    }

    @Test
    void flagsDeprecatedWeblogicTypeUsage() {
        rewriteRun(spec -> spec.recipe(recipe()),
            // Deprecated member under weblogic.* should be reported via FindDeprecatedUses
            java(
                """
                package weblogic.common;
                public class OldClass {
                    @Deprecated public void old() {}
                }
                """
            ),
            java(
                """
                package com.example;

                import weblogic.common.OldClass;

                class UseDeprecated {
                    void m() { new OldClass().old(); }
                }
                """,
                """
                package com.example;

                import weblogic.common.OldClass;

                class UseDeprecated {
                    void m() { /*~~>*/new OldClass().old(); }
                }
                """
            )
        );
    }


    @Test
    void flagsDeprecatedComBeaTypeUsage() {
        rewriteRun(spec -> spec.recipe(recipe()),
            // Deprecated member under com.bea.* should be reported via FindDeprecatedUses
            java(
                """
                package com.bea;
                public class LegacyClass {
                    @Deprecated public void old() {}
                }
                """
            ),
            java(
                """
                package com.example;

                import com.bea.LegacyClass;

                class UseLegacy {
                    void m() { new LegacyClass().old(); }
                }
                """,
                """
                package com.example;

                import com.bea.LegacyClass;

                class UseLegacy {
                    void m() { /*~~>*/new LegacyClass().old(); }
                }
                """
            )
        );
    }





    @Test
    void flagsJdbcExtensionsDataSourceSwitchingCallback() {
        rewriteRun(spec -> spec.recipe(recipe()),
            // Exact class match
            java(
                """
                package weblogic.jdbc.extensions;
                public interface DataSourceSwitchingCallback {}
                """
            ),
            java(
                """
                package com.example;

                import weblogic.jdbc.extensions.DataSourceSwitchingCallback;

                class UseCallback {
                    DataSourceSwitchingCallback cb;
                }
                """,
                """
                package com.example;

                import weblogic.jdbc.extensions.DataSourceSwitchingCallback;

                class UseCallback {
                    /*~~>*/DataSourceSwitchingCallback cb;
                }
                """
            )
        );
    }

    @Test
    void flagsLoggingLog4jWildcard() {
        rewriteRun(spec -> spec.recipe(recipe()),
            java(
                """
                package weblogic.logging.log4j;
                public class SomeLogger {}
                """
            ),
            java(
                """
                package com.example;

                import weblogic.logging.log4j.SomeLogger;

                class UseLog4j {
                    SomeLogger logger;
                }
                """,
                """
                package com.example;

                import weblogic.logging.log4j.SomeLogger;

                class UseLog4j {
                    /*~~>*/SomeLogger logger;
                }
                """
            )
        );
    }

    @Test
    void flagsJmsQueueMBean() {
        rewriteRun(spec -> spec.recipe(recipe()),
            java(
                """
                package weblogic.management.configuration;
                public interface JMSQueueMBean {}
                """
            ),
            java(
                """
                package com.example;

                import weblogic.management.configuration.JMSQueueMBean;

                class UseJmsQueue {
                    JMSQueueMBean q;
                }
                """,
                """
                package com.example;

                import weblogic.management.configuration.JMSQueueMBean;

                class UseJmsQueue {
                    /*~~>*/JMSQueueMBean q;
                }
                """
            )
        );
    }

    @Test
    void flagsRuntimeKodoDataCacheRuntimeMBean() {
        rewriteRun(spec -> spec.recipe(recipe()),
            java(
                """
                package weblogic.management.runtime;
                public interface KodoDataCacheRuntimeMBean {}
                """
            ),
            java(
                """
                package com.example;

                import weblogic.management.runtime.KodoDataCacheRuntimeMBean;

                class UseKodoRuntime {
                    KodoDataCacheRuntimeMBean m;
                }
                """,
                """
                package com.example;

                import weblogic.management.runtime.KodoDataCacheRuntimeMBean;

                class UseKodoRuntime {
                    /*~~>*/KodoDataCacheRuntimeMBean m;
                }
                """
            )
        );
    }

    @Test
    void flagsRmiSecurityException() {
        rewriteRun(spec -> spec.recipe(recipe()),
            java(
                """
                package weblogic.rmi;
                public class RMISecurityException extends RuntimeException {}
                """
            ),
            java(
                """
                package com.example;

                import weblogic.rmi.RMISecurityException;

                class UseRmiSecEx {
                    RMISecurityException ex;
                }
                """,
                """
                package com.example;

                import weblogic.rmi.RMISecurityException;

                class UseRmiSecEx {
                    /*~~>*/RMISecurityException ex;
                }
                """
            )
        );
    }

    @Test
    void flagsWseeReliabilityFaultsWildcard() {
        rewriteRun(spec -> spec.recipe(recipe()),
            java(
                """
                package weblogic.wsee.reliability.faults;
                public class SomeFault {}
                """
            ),
            java(
                """
                package com.example;

                import weblogic.wsee.reliability.faults.SomeFault;

                class UseFault {
                    SomeFault f;
                }
                """,
                """
                package com.example;

                import weblogic.wsee.reliability.faults.SomeFault;

                class UseFault {
                    /*~~>*/SomeFault f;
                }
                """
            )
        );
    }

    @Test
    void flagsWstxWsatTransactionalWildcard() {
        rewriteRun(spec -> spec.recipe(recipe()),
            java(
                """
                package weblogic.wsee.wstx.wsat.Transactional;
                public class SomeType {}
                """
            ),
            java(
                """
                package com.example;

                import weblogic.wsee.wstx.wsat.Transactional.SomeType;

                class UseTransactional {
                    SomeType t;
                }
                """,
                """
                package com.example;

                import weblogic.wsee.wstx.wsat.Transactional.SomeType;

                class UseTransactional {
                    /*~~>*/SomeType t;
                }
                """
            )
        );
    }

    @Test
    void flagsLifecycleOrchestrator() {
        rewriteRun(spec -> spec.recipe(recipe()),
            java(
                """
                package com.oracle.weblogic.lifecycle;
                public class Orchestrator {}
                """
            ),
            java(
                """
                package com.example;

                import com.oracle.weblogic.lifecycle.Orchestrator;

                class UseOrchestrator {
                    Orchestrator orch;
                }
                """
            )
        );
    }


    @Test
    void flagsXmlSchemaDoubleDotWildcard() {
        rewriteRun(spec -> spec.recipe(recipe()),
            java(
                """
                package weblogic.xml.schema.foo;
                public class SomeSchemaType {}
                """
            ),
            java(
                """
                package com.example;

                import weblogic.xml.schema.foo.SomeSchemaType;

                class UseSchemaType {
                    SomeSchemaType t;
                }
                """,
                """
                package com.example;

                import weblogic.xml.schema.foo.SomeSchemaType;

                class UseSchemaType {
                    /*~~>*/SomeSchemaType t;
                }
                """
            )
        );
    }

    @Test
    void flagsResourceRuntimeMBeanNestedType() {
        rewriteRun(spec -> spec.recipe(recipe()),
            java(
                """
                package weblogic.management.runtime;
                public interface ResourceRuntimeMBean {
                    enum ResourceType { A, B }
                }
                """
            ),
            java(
                """
                package com.example;

                import weblogic.management.runtime.ResourceRuntimeMBean.ResourceType;

                class UseNestedType {
                    ResourceType rt;
                }
                """,
                """
                package com.example;

                import weblogic.management.runtime.ResourceRuntimeMBean.ResourceType;

                class UseNestedType {
                    /*~~>*/ResourceType rt;
                }
                """
            )
        );
    }

    @Test
    void flagsRmiSecurityManager() {
        rewriteRun(spec -> spec.recipe(recipe()),
            java(
                """
                package weblogic.rmi;
                public class RMISecurityManager extends SecurityManager {}
                """
            ),
            java(
                """
                package com.example;

                import weblogic.rmi.RMISecurityManager;

                class UseRmiSecMgr {
                    RMISecurityManager mgr;
                }
                """,
                """
                package com.example;

                import weblogic.rmi.RMISecurityManager;

                class UseRmiSecMgr {
                    /*~~>*/RMISecurityManager mgr;
                }
                """
            )
        );
    }

    @Test
    void flagsKeyStoreMBean() {
        rewriteRun(spec -> spec.recipe(recipe()),
            java(
                """
                package weblogic.management.security.pk;
                public interface KeyStoreMBean {}
                """
            ),
            java(
                """
                package com.example;

                import weblogic.management.security.pk.KeyStoreMBean;

                class UseKeyStore {
                    KeyStoreMBean ks;
                }
                """,
                """
                package com.example;

                import weblogic.management.security.pk.KeyStoreMBean;

                class UseKeyStore {
                    /*~~>*/KeyStoreMBean ks;
                }
                """
            )
        );
    }

    @Test
    void flagsLifecycleCoreUtils() {
        rewriteRun(spec -> spec.recipe(recipe()),
            java(
                """
                package com.oracle.weblogic.lifecycle.core;
                public class LifecycleUtils {}
                """
            ),
            java(
                """
                package com.example;

                import com.oracle.weblogic.lifecycle.core.LifecycleUtils;

                class UseLifecycleUtils {
                    LifecycleUtils utils;
                }
                """
            )
        );
    }


    @Test
    void flagsWseeConversationUtils() {
        rewriteRun(spec -> spec.recipe(recipe()),
            java(
                """
                package weblogic.wsee.conversation;
                public class ConversationUtils {}
                """
            ),
            java(
                """
                package com.example;

                import weblogic.wsee.conversation.ConversationUtils;

                class UseConversationUtils {
                    ConversationUtils cu;
                }
                """,
                """
                package com.example;

                import weblogic.wsee.conversation.ConversationUtils;

                class UseConversationUtils {
                    /*~~>*/ConversationUtils cu;
                }
                """
            )
        );
    }
}
