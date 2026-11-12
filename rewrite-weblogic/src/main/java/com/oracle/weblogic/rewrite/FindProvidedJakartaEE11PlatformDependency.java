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
import org.openrewrite.semver.Semver;
import org.openrewrite.semver.VersionComparator;
import org.openrewrite.xml.XPathMatcher;
import org.openrewrite.xml.tree.Xml;

/**
 * Finds projects that directly depend on the Jakarta EE 11 platform API with an
 * effective Maven scope of {@code provided}.
 */
public class FindProvidedJakartaEE11PlatformDependency extends Recipe {

    private static final String JAKARTA_PLATFORM_GROUP_ID = "jakarta.platform";
    private static final String JAKARTA_PLATFORM_ARTIFACT_ID = "jakarta.jakartaee-api";
    private static final XPathMatcher DIRECT_DEPENDENCY = new XPathMatcher("/project/dependencies/dependency");

    @Override
    public String getDisplayName() {
        return "Find a provided Jakarta EE 11 platform dependency";
    }

    @Override
    public String getDescription() {
        return "Find projects that directly depend on the Jakarta EE 11 platform API when its effective " +
                "Maven scope is provided.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MavenIsoVisitor<ExecutionContext>() {
            private final VersionComparator jakartaEE11 = Semver.validate("11.x", null).getValue();

            @Override
            public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext ctx) {
                Xml.Tag t = super.visitTag(tag, ctx);
                if (!DIRECT_DEPENDENCY.matches(getCursor())) {
                    return t;
                }

                ResolvedPom pom = getResolutionResult().getPom();
                String groupId = t.getChildValue("groupId").map(pom::getValue).orElse(null);
                String artifactId = t.getChildValue("artifactId").map(pom::getValue).orElse(null);
                if (!JAKARTA_PLATFORM_GROUP_ID.equals(groupId) ||
                        !JAKARTA_PLATFORM_ARTIFACT_ID.equals(artifactId)) {
                    return t;
                }

                String type = t.getChildValue("type").map(pom::getValue).orElse(null);
                String classifier = t.getChildValue("classifier").map(pom::getValue).orElse(null);
                Scope effectiveScope = t.getChildValue("scope")
                        .map(pom::getValue)
                        .map(Scope::fromName)
                        .orElseGet(() -> pom.getManagedScope(
                                JAKARTA_PLATFORM_GROUP_ID, JAKARTA_PLATFORM_ARTIFACT_ID, type, classifier));
                if (effectiveScope != Scope.Provided) {
                    return t;
                }

                String effectiveVersion = t.getChildValue("version")
                        .map(pom::getValue)
                        .orElseGet(() -> pom.getManagedVersion(
                                JAKARTA_PLATFORM_GROUP_ID, JAKARTA_PLATFORM_ARTIFACT_ID, type, classifier));
                if (effectiveVersion != null && jakartaEE11.isValid(null, effectiveVersion)) {
                    return SearchResult.found(t);
                }
                return t;
            }
        };
    }
}
