/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite.spring.framework;

import org.openrewrite.Cursor;
import org.openrewrite.ExecutionContext;
import org.openrewrite.ScanningRecipe;
import org.openrewrite.SourceFile;
import org.openrewrite.TreeVisitor;
import org.openrewrite.xml.AddToTagVisitor;
import org.openrewrite.xml.MapTagChildrenVisitor;
import org.openrewrite.xml.XmlIsoVisitor;
import org.openrewrite.xml.XmlParser;
import org.openrewrite.xml.tree.Xml;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Adds the selective WebLogic classloading preferences needed to keep a
 * Spring Framework 7 application from discovering the server's legacy
 * Jackson module for {@code javax.xml.bind} annotations.
 */
public class ConfigureJacksonClassloadingForSpring7
        extends ScanningRecipe<ConfigureJacksonClassloadingForSpring7.Accumulator> {

    private static final Path WEBLOGIC_XML = Paths.get("src/main/webapp/WEB-INF/weblogic.xml");

    private static final List<String> PREFERRED_PACKAGES = List.of("com.fasterxml.jackson.*");

    private static final String JACKSON_MODULE_RESOURCE =
            "META-INF/services/com.fasterxml.jackson.databind.Module";

    private static final String WEBLOGIC_XML_CONTENT = """
            <?xml version="1.0" encoding="UTF-8"?>
            <weblogic-web-app xmlns="http://xmlns.oracle.com/weblogic/weblogic-web-app"
                              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                              xsi:schemaLocation="http://xmlns.oracle.com/weblogic/weblogic-web-app http://xmlns.oracle.com/weblogic/weblogic-web-app/2.0/weblogic-web-app.xsd">
                <container-descriptor>
                    <prefer-application-packages>
                        <package-name>com.fasterxml.jackson.*</package-name>
                    </prefer-application-packages>
                    <prefer-application-resources>
                        <resource-name>META-INF/services/com.fasterxml.jackson.databind.Module</resource-name>
                    </prefer-application-resources>
                </container-descriptor>
            </weblogic-web-app>
            """;

    private static final String PREFERRED_PACKAGES_TAG = """
            <prefer-application-packages>
                <package-name>com.fasterxml.jackson.*</package-name>
            </prefer-application-packages>
            """;

    private static final String PREFERRED_RESOURCES_TAG = """
            <prefer-application-resources>
                <resource-name>META-INF/services/com.fasterxml.jackson.databind.Module</resource-name>
            </prefer-application-resources>
            """;

    private static final String CONTAINER_DESCRIPTOR_TAG = """
            <container-descriptor>
                <prefer-application-packages>
                    <package-name>com.fasterxml.jackson.*</package-name>
                </prefer-application-packages>
                <prefer-application-resources>
                    <resource-name>META-INF/services/com.fasterxml.jackson.databind.Module</resource-name>
                </prefer-application-resources>
            </container-descriptor>
            """;

    @Override
    public String getDisplayName() {
        return "Configure Jackson classloading for Spring Framework 7 on WebLogic";
    }

    @Override
    public String getDescription() {
        return "Prefer application Jackson packages and Jackson module service providers so WebLogic does not " +
                "load an incompatible legacy JAXB module without overriding the server-managed JAXB runtime.";
    }

    @Override
    public Accumulator getInitialValue(ExecutionContext ctx) {
        return new Accumulator();
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getScanner(Accumulator accumulator) {
        return new XmlIsoVisitor<ExecutionContext>() {
            @Override
            public Xml.Document visitDocument(Xml.Document document, ExecutionContext ctx) {
                Path sourcePath = document.getSourcePath().normalize();
                accumulator.existingSourcePaths.add(sourcePath);
                if (isWarPom(document)) {
                    Path moduleDirectory = sourcePath.getParent();
                    accumulator.warDescriptors.add((moduleDirectory == null ? WEBLOGIC_XML :
                            moduleDirectory.resolve(WEBLOGIC_XML)).normalize());
                }
                return document;
            }
        };
    }

    @Override
    public Collection<? extends SourceFile> generate(Accumulator accumulator, ExecutionContext ctx) {
        List<SourceFile> generated = new ArrayList<>();
        for (Path descriptor : accumulator.warDescriptors) {
            if (accumulator.existingSourcePaths.contains(descriptor)) {
                continue;
            }
            SourceFile sourceFile = XmlParser.builder()
                    .build()
                    .parse(ctx, WEBLOGIC_XML_CONTENT)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Unable to parse generated weblogic.xml"));
            generated.add(sourceFile.withSourcePath(descriptor));
        }
        return generated;
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor(Accumulator accumulator) {
        return new XmlIsoVisitor<ExecutionContext>() {
            @Override
            public Xml.Document visitDocument(Xml.Document document, ExecutionContext ctx) {
                Xml.Document d = super.visitDocument(document, ctx);
                if (!isWebLogicWebDescriptor(d)) {
                    return d;
                }
                Xml.Tag configured = configure(d.getRoot(), getCursor());
                return configured == d.getRoot() ? d : d.withRoot(configured);
            }
        };
    }

    private static boolean isWarPom(Xml.Document document) {
        Path fileName = document.getSourcePath().getFileName();
        return fileName != null && "pom.xml".equals(fileName.toString()) &&
                "project".equals(document.getRoot().getName()) &&
                "war".equals(document.getRoot().getChildValue("packaging")
                        .map(String::trim)
                        .orElse("jar"));
    }

    private static boolean isWebLogicWebDescriptor(Xml.Document document) {
        Path fileName = document.getSourcePath().getFileName();
        return fileName != null && "weblogic.xml".equals(fileName.toString()) &&
                "weblogic-web-app".equals(document.getRoot().getName());
    }

    private static Xml.Tag configure(Xml.Tag root, Cursor documentCursor) {
        Xml.Tag container = root.getChild("container-descriptor").orElse(null);
        if (container == null) {
            return AddToTagVisitor.addToTag(
                    root, Xml.Tag.build(CONTAINER_DESCRIPTOR_TAG), documentCursor);
        }

        Cursor rootCursor = new Cursor(documentCursor, root);
        Xml.Tag configuredContainer = configureContainer(container, rootCursor);
        if (configuredContainer == container) {
            return root;
        }
        return replaceDirectChild(root, container, configuredContainer);
    }

    private static Xml.Tag configureContainer(Xml.Tag container, Cursor rootCursor) {
        Xml.Tag configured = container;
        Xml.Tag packages = configured.getChild("prefer-application-packages").orElse(null);
        if (packages == null) {
            configured = AddToTagVisitor.addToTag(
                    configured, Xml.Tag.build(PREFERRED_PACKAGES_TAG), rootCursor);
        } else {
            Cursor containerCursor = new Cursor(rootCursor, configured);
            Xml.Tag configuredPackages = addMissingValues(
                    packages, "package-name", PREFERRED_PACKAGES, containerCursor);
            if (configuredPackages != packages) {
                configured = replaceDirectChild(configured, packages, configuredPackages);
            }
        }

        Xml.Tag resources = configured.getChild("prefer-application-resources").orElse(null);
        if (resources == null) {
            configured = AddToTagVisitor.addToTag(
                    configured, Xml.Tag.build(PREFERRED_RESOURCES_TAG), rootCursor);
        } else {
            Cursor containerCursor = new Cursor(rootCursor, configured);
            Xml.Tag configuredResources = addMissingValues(
                    resources, "resource-name", List.of(JACKSON_MODULE_RESOURCE), containerCursor);
            if (configuredResources != resources) {
                configured = replaceDirectChild(configured, resources, configuredResources);
            }
        }
        return configured;
    }

    private static Xml.Tag addMissingValues(
            Xml.Tag parent, String childName, List<String> requiredValues, Cursor parentCursor) {
        Set<String> existingValues = new HashSet<>();
        for (Xml.Tag child : parent.getChildren(childName)) {
            child.getValue().map(String::trim).ifPresent(existingValues::add);
        }

        Xml.Tag configured = parent;
        for (String requiredValue : requiredValues) {
            if (existingValues.add(requiredValue)) {
                configured = AddToTagVisitor.addToTag(
                        configured,
                        Xml.Tag.build("<" + childName + ">" + requiredValue + "</" + childName + ">"),
                        parentCursor);
            }
        }
        return configured;
    }

    private static Xml.Tag replaceDirectChild(Xml.Tag parent, Xml.Tag original, Xml.Tag replacement) {
        return MapTagChildrenVisitor.mapTagChildren(parent,
                child -> child.getId().equals(original.getId()) ? replacement : child);
    }

    static final class Accumulator {
        private final Set<Path> warDescriptors = new LinkedHashSet<>();
        private final Set<Path> existingSourcePaths = new HashSet<>();
    }
}
