/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite.spring.framework;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.xml.XPathMatcher;
import org.openrewrite.xml.tree.Content;
import org.openrewrite.xml.tree.Xml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Separates Jackson 2 annotations from a shared version declaration before a
 * Spring 7 migration moves other Jackson dependencies to Jackson 3.
 */
public class NormalizeJacksonAnnotationsForSpring7 extends Recipe {

    private static final String JACKSON_ANNOTATIONS_GROUP_ID = "com.fasterxml.jackson.core";
    private static final String JACKSON_ANNOTATIONS_ARTIFACT_ID = "jackson-annotations";
    private static final String JACKSON_ANNOTATIONS_VERSION = "2.20";

    private static final XPathMatcher DIRECT_DEPENDENCY =
            new XPathMatcher("/project/dependencies/dependency");
    private static final XPathMatcher MANAGED_DEPENDENCY =
            new XPathMatcher("/project/dependencyManagement/dependencies/dependency");
    private static final XPathMatcher PROPERTIES = new XPathMatcher("/project/properties");

    @Override
    public String getDisplayName() {
        return "Normalize Jackson annotations for Spring 7";
    }

    @Override
    public String getDescription() {
        return "Keep `jackson-annotations` on its compatible Jackson 2 version when it shares a Maven version " +
                "declaration with dependencies migrated to Jackson 3.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MavenIsoVisitor<ExecutionContext>() {
            private final Set<String> jackson3VersionDeclarations = new HashSet<>();
            private final Set<String> sharedVersionProperties = new HashSet<>();

            @Override
            public Xml.Document visitDocument(Xml.Document document, ExecutionContext ctx) {
                jackson3VersionDeclarations.clear();
                sharedVersionProperties.clear();
                collectJackson3VersionDeclarations(document.getRoot());
                collectSharedVersionProperties(document.getRoot());
                return super.visitDocument(document, ctx);
            }

            @Override
            public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext ctx) {
                Xml.Tag t = super.visitTag(tag, ctx);
                if (PROPERTIES.matches(getCursor())) {
                    return removeDuplicateSharedVersionProperties(t);
                }
                if (!isProjectDependency() ||
                        !JACKSON_ANNOTATIONS_GROUP_ID.equals(t.getChildValue("groupId").orElse(null)) ||
                        !JACKSON_ANNOTATIONS_ARTIFACT_ID.equals(t.getChildValue("artifactId").orElse(null))) {
                    return t;
                }

                String declaredVersion = t.getChildValue("version").orElse(null);
                if (declaredVersion != null && jackson3VersionDeclarations.contains(declaredVersion)) {
                    return t.withChildValue("version", JACKSON_ANNOTATIONS_VERSION);
                }
                return t;
            }

            private boolean isProjectDependency() {
                return DIRECT_DEPENDENCY.matches(getCursor()) || MANAGED_DEPENDENCY.matches(getCursor());
            }

            private void collectJackson3VersionDeclarations(Xml.Tag project) {
                forEachProjectDependencies(project, dependencies ->
                        collectJackson3VersionDeclarations(dependencies, jackson3VersionDeclarations));
            }

            private void collectJackson3VersionDeclarations(Xml.Tag dependencies, Set<String> declarations) {
                for (Xml.Tag dependency : dependencies.getChildren("dependency")) {
                    String groupId = dependency.getChildValue("groupId").orElse("");
                    String artifactId = dependency.getChildValue("artifactId").orElse("");
                    boolean jackson3Dependency = "tools.jackson".equals(groupId) ||
                            groupId.startsWith("tools.jackson.") ||
                            (("com.fasterxml.jackson".equals(groupId) ||
                                    groupId.startsWith("com.fasterxml.jackson.")) &&
                                    !(JACKSON_ANNOTATIONS_GROUP_ID.equals(groupId) &&
                                            JACKSON_ANNOTATIONS_ARTIFACT_ID.equals(artifactId)));
                    if (jackson3Dependency) {
                        dependency.getChildValue("version").ifPresent(declarations::add);
                    }
                }
            }

            private void collectSharedVersionProperties(Xml.Tag project) {
                forEachProjectDependencies(project, dependencies -> {
                    for (Xml.Tag dependency : dependencies.getChildren("dependency")) {
                        if (!JACKSON_ANNOTATIONS_GROUP_ID.equals(
                                dependency.getChildValue("groupId").orElse(null)) ||
                                !JACKSON_ANNOTATIONS_ARTIFACT_ID.equals(
                                        dependency.getChildValue("artifactId").orElse(null))) {
                            continue;
                        }
                        String declaredVersion = dependency.getChildValue("version").orElse(null);
                        if (declaredVersion != null && jackson3VersionDeclarations.contains(declaredVersion) &&
                                declaredVersion.startsWith("${") && declaredVersion.endsWith("}")) {
                            sharedVersionProperties.add(
                                    declaredVersion.substring(2, declaredVersion.length() - 1));
                        }
                    }
                });
            }

            private void forEachProjectDependencies(Xml.Tag project, Consumer<Xml.Tag> consumer) {
                project.getChild("dependencies").ifPresent(consumer);
                project.getChild("dependencyManagement")
                        .flatMap(dependencyManagement -> dependencyManagement.getChild("dependencies"))
                        .ifPresent(consumer);
            }

            private Xml.Tag removeDuplicateSharedVersionProperties(Xml.Tag properties) {
                Set<String> seenProperties = new HashSet<>();
                Set<UUID> retainedProperties = new HashSet<>();
                List<? extends Content> content = properties.getContent();
                for (int i = content.size() - 1; i >= 0; i--) {
                    if (content.get(i) instanceof Xml.Tag property &&
                            sharedVersionProperties.contains(property.getName()) &&
                            seenProperties.add(property.getName())) {
                        retainedProperties.add(property.getId());
                    }
                }

                List<Content> normalizedContent = new ArrayList<>(content.size());
                for (Content item : content) {
                    if (!(item instanceof Xml.Tag property) ||
                            !sharedVersionProperties.contains(property.getName()) ||
                            retainedProperties.contains(property.getId())) {
                        normalizedContent.add(item);
                    }
                }
                return normalizedContent.size() == content.size() ?
                        properties : properties.withContent(normalizedContent);
            }
        };
    }
}
