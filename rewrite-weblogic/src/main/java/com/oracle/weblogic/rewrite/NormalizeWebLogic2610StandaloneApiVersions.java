/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.weblogic.rewrite;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.maven.tree.ResolvedPom;
import org.openrewrite.xml.XPathMatcher;
import org.openrewrite.xml.tree.Xml;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Aligns direct standalone Jakarta API dependencies with WebLogic Server 26.1,
 * removes an application-level JAX-WS runtime supplied by the server, and
 * reconciles duplicate JAXB runtime declarations produced by composed
 * Java/Jakarta migration recipes.
 */
public class NormalizeWebLogic2610StandaloneApiVersions extends Recipe {

    private static final XPathMatcher DIRECT_DEPENDENCY =
            new XPathMatcher("/project/dependencies/dependency");

    private static final String JAXB_RUNTIME_GROUP_ID = "org.glassfish.jaxb";
    private static final String JAXB_RUNTIME_ARTIFACT_ID = "jaxb-runtime";

    private static final Map<String, String> SUPPORTED_VERSIONS = Map.of(
            "jakarta.xml.bind:jakarta.xml.bind-api", "4.0.2",
            "jakarta.xml.soap:jakarta.xml.soap-api", "3.0.2",
            "jakarta.jws:jakarta.jws-api", "3.0.0",
            "jakarta.xml.ws:jakarta.xml.ws-api", "4.0.2");

    @Override
    public String getDisplayName() {
        return "Normalize WebLogic 26.1 standalone Jakarta API versions";
    }

    @Override
    public String getDescription() {
        return "Align direct standalone Jakarta API dependencies with the exact " +
                "versions supplied by WebLogic Server 26.1 and reconcile duplicate " +
                "JAXB runtime declarations.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MavenIsoVisitor<ExecutionContext>() {
            private final Set<String> foundJaxbRuntimes = new HashSet<>();

            @Override
            public Xml.Document visitDocument(Xml.Document document, ExecutionContext ctx) {
                foundJaxbRuntimes.clear();
                return super.visitDocument(document, ctx);
            }

            @Override
            public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext ctx) {
                Xml.Tag t = super.visitTag(tag, ctx);
                if (!DIRECT_DEPENDENCY.matches(getCursor())) {
                    return t;
                }

                ResolvedPom pom = getResolutionResult().getPom();
                String groupId = t.getChildValue("groupId")
                        .map(pom::getValue)
                        .orElse(null);
                String artifactId = t.getChildValue("artifactId")
                        .map(pom::getValue)
                        .orElse(null);
                if ("com.sun.xml.ws".equals(groupId) && "jaxws-rt".equals(artifactId)) {
                    return null;
                }
                if (JAXB_RUNTIME_GROUP_ID.equals(groupId) && JAXB_RUNTIME_ARTIFACT_ID.equals(artifactId)) {
                    String type = t.getChildValue("type").map(pom::getValue).orElse("jar");
                    String classifier = t.getChildValue("classifier").map(pom::getValue).orElse("");
                    if (!foundJaxbRuntimes.add(type + ":" + classifier)) {
                        return null;
                    }
                }
                String supportedVersion = SUPPORTED_VERSIONS.get(
                        groupId + ":" + artifactId);
                if (supportedVersion == null) {
                    return t;
                }

                String declaredVersion = t.getChildValue("version").orElse(null);
                if (declaredVersion != null &&
                        !supportedVersion.equals(pom.getValue(declaredVersion))) {
                    return changeChildTagValue(t, "version", supportedVersion, ctx);
                }
                return t;
            }
        };
    }
}
