/*
 * Copyright (c) 2025 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite.jakarta;

import static org.openrewrite.internal.StringUtils.matchesGlob;

import org.jspecify.annotations.Nullable;

import org.openrewrite.ExecutionContext;
import org.openrewrite.maven.MavenDownloadingException;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;

import org.openrewrite.maven.table.MavenMetadataFailures;
import org.openrewrite.maven.tree.MavenMetadata;
import org.openrewrite.semver.Semver;
import org.openrewrite.semver.VersionComparator;
import org.openrewrite.xml.XPathMatcher;
import org.openrewrite.xml.tree.Xml;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;


/**
 * UpgradePluginConfigurationArtifactItems updates the groupId, artifactId and version under
 * plugin/executions/execution/configuration/artifactItems/artifactItem
 */

@Value
@EqualsAndHashCode(callSuper = false)
public class UpgradePluginConfigurationArtifactItems extends Recipe {

    @EqualsAndHashCode.Exclude
    transient MavenMetadataFailures metadataFailures = new MavenMetadataFailures(this);

    @Option(displayName = "Old group ID",
            description = "The old group ID to replace.",
            example = "javax")
    String oldGroupId;

    @Option(displayName = "Old artifact ID",
            description = "The old artifact ID to replace.",
            example = "javax")
    String oldArtifactId;

    @Option(displayName = "New group ID",
            description = "The new group ID to use.",
            example = "jakarta.platform")
    String newGroupId;

    @Option(displayName = "New artifact ID",
            description = "The new artifact ID to use.",
            example = "javaee-api")
    String newArtifactId;

    @Option(displayName = "New version",
            description = "An exact version number.",
            example = "9.1",
            required = false)
    @Nullable
    String newVersion;

    private static final XPathMatcher PLUGIN_ARTIFACT_ITEM_MATCHER = new XPathMatcher("/project/build/plugins/plugin/executions/execution/configuration/artifactItems/artifactItem");

    private static final String GROUP_ID = "groupId";
    private static final String ARTIFACT_ID = "artifactId";
    private static final String ARTIFACT_VERSION = "version";

    @Override
    public String getDescription() {
        return "Change the groupId and the artifactId of an artifactItem in the configuration section of a plugin's execution. " +
                "This recipe does not perform any validation and assumes all values passed are valid.";
    }

    @Override
    public String getDisplayName() {
        return "Upgrade group, artifact ID and version of an artifactItem, of a maven plugin execution configuration";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MavenIsoVisitor<ExecutionContext>() {
            @Nullable
            final VersionComparator versionComparator = newVersion != null ? Semver.validate(newVersion, null).getValue() : null;
            @Nullable
            private Collection<String> availableVersions;

            @Override
            public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext ctx) {
                Xml.Tag t = super.visitTag(tag, ctx);
                if (PLUGIN_ARTIFACT_ITEM_MATCHER.matches(getCursor())) {
                    // Find out whether oldGroupId really exists, consider the value defined as a project property as well
                    boolean isGroupIdFound = tag.getChildValue(GROUP_ID)
                            .map(a -> matchesGlob(a, oldGroupId))
                            .orElse(oldGroupId == null);
                    if (!isGroupIdFound && getResolutionResult().getPom().getProperties() != null) {
                        if (tag.getChildValue(GROUP_ID).isPresent() && tag.getChildValue(GROUP_ID).get().trim().startsWith("${")) {
                            String propertyKey = tag.getChildValue(GROUP_ID).get().trim();
                            String value = getResolutionResult().getPom().getValue(propertyKey);
                            isGroupIdFound = value != null && matchesGlob(value, oldGroupId);
                        }
                    }

                    // Find out whether oldArtifactId really exists, consider the value defined as a project property as well
                    boolean isArtifactIdFound = tag.getChildValue(ARTIFACT_ID)
                            .map(a -> matchesGlob(a, oldArtifactId))
                            .orElse(oldArtifactId == null);
                    if (!isArtifactIdFound && getResolutionResult().getPom().getProperties() != null) {
                        if (tag.getChildValue(ARTIFACT_ID).isPresent() && tag.getChildValue(ARTIFACT_ID).get().trim().startsWith("${")) {
                            String propertyKey = tag.getChildValue(ARTIFACT_ID).get().trim();
                            String value = getResolutionResult().getPom().getValue(propertyKey);
                            isArtifactIdFound = value != null && matchesGlob(value, oldArtifactId);
                        }
                    }

                    // Change the child tag value only when oldGroupId and oldArtifactId is found
                    if (isGroupIdFound && isArtifactIdFound) {
                        if (newGroupId != null) {
                            t = changeChildTagValue(t, GROUP_ID, newGroupId, ctx);
                        }

                        if (newArtifactId != null) {
                            t = changeChildTagValue(t, ARTIFACT_ID, newArtifactId, ctx);
                        }

                        String currentVersion = t.getChildValue(ARTIFACT_VERSION).orElse(null);
                        if (newVersion != null) {
                            try {
                                String resolvedNewVersion = resolveSemverVersion(ctx, GROUP_ID, ARTIFACT_ID, currentVersion);
                                Optional<Xml.Tag> versionTag = t.getChild("version");
                                boolean versionTagPresent = versionTag.isPresent();
                                if (versionTagPresent) {
                                    t = changeChildTagValue(t, ARTIFACT_VERSION, resolvedNewVersion, ctx);
                                }
                            } catch (MavenDownloadingException e) {
                                return e.warn(tag);
                            }
                        }
                    }
                }
                return t;
            }

            @SuppressWarnings("ConstantConditions")
            private String resolveSemverVersion(ExecutionContext ctx, String groupId, String artifactId, @Nullable String currentVersion) throws MavenDownloadingException {
                if (versionComparator == null) {
                    return newVersion;
                }
                String finalCurrentVersion = currentVersion != null ? currentVersion : newVersion;
                if (availableVersions == null) {
                    availableVersions = new ArrayList<>();
                    MavenMetadata mavenMetadata = metadataFailures.insertRows(ctx, () -> downloadMetadata(groupId, artifactId, ctx));
                    for (String v : mavenMetadata.getVersioning().getVersions()) {
                        if (versionComparator.isValid(finalCurrentVersion, v)) {
                            availableVersions.add(v);
                        }
                    }

                }
                return availableVersions.isEmpty() ? newVersion : Collections.max(availableVersions, versionComparator);
            }
        };
    }
}