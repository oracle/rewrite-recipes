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
import org.openrewrite.marker.SearchResult;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.maven.tree.ResolvedPom;
import org.openrewrite.maven.tree.Scope;
import org.openrewrite.xml.XPathMatcher;
import org.openrewrite.xml.tree.Xml;

/**
 * Finds projects that directly depend on a Java EE platform API with an
 * effective Maven scope of {@code provided}.
 */
public class FindProvidedJavaEEPlatformDependency extends Recipe {

    private static final String PLATFORM_GROUP_ID = "javax";
    private static final String PLATFORM_ARTIFACT_ID = "javaee-api";
    private static final String WEB_PLATFORM_ARTIFACT_ID = "javaee-web-api";
    private static final XPathMatcher DIRECT_DEPENDENCY = new XPathMatcher("/project/dependencies/dependency");

    @Override
    public String getDisplayName() {
        return "Find a provided Java EE platform dependency";
    }

    @Override
    public String getDescription() {
        return "Find projects that directly depend on a Java EE platform API when its effective Maven scope " +
                "is provided.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MavenIsoVisitor<ExecutionContext>() {
            @Override
            public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext ctx) {
                Xml.Tag t = super.visitTag(tag, ctx);
                if (!DIRECT_DEPENDENCY.matches(getCursor())) {
                    return t;
                }

                ResolvedPom pom = getResolutionResult().getPom();
                String groupId = t.getChildValue("groupId").map(pom::getValue).orElse(null);
                String artifactId = t.getChildValue("artifactId").map(pom::getValue).orElse(null);
                if (!PLATFORM_GROUP_ID.equals(groupId) ||
                        (!PLATFORM_ARTIFACT_ID.equals(artifactId) &&
                                !WEB_PLATFORM_ARTIFACT_ID.equals(artifactId))) {
                    return t;
                }

                String type = t.getChildValue("type").map(pom::getValue).orElse(null);
                String classifier = t.getChildValue("classifier").map(pom::getValue).orElse(null);
                Scope effectiveScope = t.getChildValue("scope")
                        .map(pom::getValue)
                        .map(Scope::fromName)
                        .orElseGet(() -> pom.getManagedScope(groupId, artifactId, type, classifier));
                return effectiveScope == Scope.Provided ? SearchResult.found(t) : t;
            }
        };
    }
}
