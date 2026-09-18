/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite.hibernate;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.maven.tree.ResolvedPom;
import org.openrewrite.xml.XPathMatcher;
import org.openrewrite.xml.tree.Content;
import org.openrewrite.xml.tree.Xml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Aligns Hibernate ORM dependencies after the upstream Hibernate 7 migration,
 * which intentionally selects the newest available 7.0.x release.
 */
public class NormalizeHibernateOrm70Versions extends Recipe {

    private static final String HIBERNATE_ORM_GROUP_ID = "org.hibernate.orm";
    private static final String HIBERNATE_ORM_VERSION = "7.0.8.Final";

    private static final XPathMatcher DIRECT_DEPENDENCY =
            new XPathMatcher("/project/dependencies/dependency");
    private static final XPathMatcher MANAGED_DEPENDENCY =
            new XPathMatcher("/project/dependencyManagement/dependencies/dependency");
    private static final XPathMatcher PROPERTIES = new XPathMatcher("/project/properties");

    @Override
    public String getDisplayName() {
        return "Normalize Hibernate ORM 7.0 versions for WebLogic 26.1.0";
    }

    @Override
    public String getDescription() {
        return "Align explicitly versioned Hibernate ORM dependencies with 7.0.8.Final, " +
                "the version exercised with WebLogic Server 26.1.0 on Java 21 and Java 25.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MavenIsoVisitor<ExecutionContext>() {
            private final Set<String> localProperties = new HashSet<>();
            private final Set<String> hibernateVersionProperties = new HashSet<>();

            @Override
            public Xml.Document visitDocument(Xml.Document document, ExecutionContext ctx) {
                localProperties.clear();
                hibernateVersionProperties.clear();
                collectVersionProperties(document.getRoot());
                return super.visitDocument(document, ctx);
            }

            @Override
            public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext ctx) {
                Xml.Tag t = super.visitTag(tag, ctx);
                if (PROPERTIES.matches(getCursor())) {
                    return normalizeVersionProperties(t);
                }
                if (!isProjectDependency()) {
                    return t;
                }

                ResolvedPom pom = getResolutionResult().getPom();
                String groupId = t.getChildValue("groupId").map(pom::getValue).orElse(null);
                if (!HIBERNATE_ORM_GROUP_ID.equals(groupId)) {
                    return t;
                }

                String declaredVersion = t.getChildValue("version").orElse(null);
                if (declaredVersion == null) {
                    return t;
                }
                String propertyName = propertyName(declaredVersion);
                if (propertyName != null && localProperties.contains(propertyName)) {
                    return t;
                }
                return HIBERNATE_ORM_VERSION.equals(pom.getValue(declaredVersion)) ?
                        t : changeChildTagValue(t, "version", HIBERNATE_ORM_VERSION, ctx);
            }

            private void collectVersionProperties(Xml.Tag project) {
                project.getChild("properties").ifPresent(properties -> {
                    for (Xml.Tag property : properties.getChildren()) {
                        localProperties.add(property.getName());
                    }
                });
                forEachProjectDependencies(project, dependencies -> {
                    for (Xml.Tag dependency : dependencies.getChildren("dependency")) {
                        ResolvedPom pom = getResolutionResult().getPom();
                        String groupId = dependency.getChildValue("groupId")
                                .map(pom::getValue)
                                .orElse(null);
                        if (!HIBERNATE_ORM_GROUP_ID.equals(groupId)) {
                            continue;
                        }
                        dependency.getChildValue("version")
                                .map(NormalizeHibernateOrm70Versions::propertyName)
                                .filter(localProperties::contains)
                                .ifPresent(hibernateVersionProperties::add);
                    }
                });
            }

            private boolean isProjectDependency() {
                return DIRECT_DEPENDENCY.matches(getCursor()) || MANAGED_DEPENDENCY.matches(getCursor());
            }

            private void forEachProjectDependencies(Xml.Tag project, Consumer<Xml.Tag> consumer) {
                project.getChild("dependencies").ifPresent(consumer);
                project.getChild("dependencyManagement")
                        .flatMap(dependencyManagement -> dependencyManagement.getChild("dependencies"))
                        .ifPresent(consumer);
            }

            private Xml.Tag normalizeVersionProperties(Xml.Tag properties) {
                List<Content> normalizedContent = new ArrayList<>(properties.getContent().size());
                boolean changed = false;
                for (Content item : properties.getContent()) {
                    if (item instanceof Xml.Tag property &&
                            hibernateVersionProperties.contains(property.getName()) &&
                            !HIBERNATE_ORM_VERSION.equals(property.getValue().orElse(null))) {
                        normalizedContent.add(property.withValue(HIBERNATE_ORM_VERSION));
                        changed = true;
                    } else {
                        normalizedContent.add(item);
                    }
                }
                return changed ? properties.withContent(normalizedContent) : properties;
            }
        };
    }

    private static String propertyName(String version) {
        return version.startsWith("${") && version.endsWith("}") ?
                version.substring(2, version.length() - 1) : null;
    }
}
