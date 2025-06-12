package com.oracle.weblogic.rewrite.jakarta;

import static org.openrewrite.internal.StringUtils.matchesGlob;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

import org.openrewrite.*;
import org.openrewrite.maven.MavenIsoVisitor;
//import org.openrewrite.maven.table.MavenMetadataFailures;
import org.openrewrite.semver.Semver;
import org.openrewrite.xml.XPathMatcher;
import org.openrewrite.xml.tree.Xml;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * UpgradePluginConfigurationArtifactItems updates the groupId, artifactId and version under
 * plugin/executions/execution/configuration/artifactItems/artifactItem
 */

@Value
@EqualsAndHashCode(callSuper = false)
public class UpgradePluginConfigurationArtifactItems extends Recipe {

    //@EqualsAndHashCode.Exclude
    //transient MavenMetadataFailures metadataFailures = new MavenMetadataFailures(this);

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

    @JsonCreator
    public UpgradePluginConfigurationArtifactItems(@JsonProperty("oldGroupId") String oldGroupId,
                                                   @JsonProperty("oldArtifactId") String oldArtifactId,
                                                   @JsonProperty("newGroupId") String newGroupId,
                                                   @JsonProperty("newArtifactId") String newArtifactId,
                                                   @JsonProperty("newVersion") @org.jetbrains.annotations.Nullable String newVersion) {
        this.oldGroupId = oldGroupId;
        this.oldArtifactId = oldArtifactId;
        this.newGroupId = newGroupId;
        this.newArtifactId = newArtifactId;
        this.newVersion = newVersion;
    }

    @Override
    public Validated validate() {
        return super.validate();
    }

    // This will be added to MavenVisitor
    private static final XPathMatcher PLUGIN_ARTIFACT_ITEM_MATCHER = new XPathMatcher("/project/build/plugins/plugin/executions/execution/configuration/artifactItems/artifactItem");

    private static final String ARTIFACTITEM_GROUP_ID = "groupId";
    private static final String ARTIFACTITEM_ARTIFACT_ID = "artifactId";
    private static final String ARTIFACTITEM_ARTIFACT_VERSION = "version";

    @Override
    public String getDescription() {
        return "Change the groupId and/or the artifactId of an artifactItem in the configuration section of a plugin's execution. " +
                "This recipe does not perform any validation and assumes all values passed are valid.";
    }

    @Override
    public String getDisplayName() {
        return "Upgrade group, artifact ID and version of an artifactItem, of a maven plugin execution configuration.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MavenIsoVisitor<ExecutionContext>() {
            @Override
            public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext ctx) {
                Xml.Tag t = super.visitTag(tag, ctx);
                if (PLUGIN_ARTIFACT_ITEM_MATCHER.matches(getCursor())) {
                    // Find out whether oldGroupId really exists, consider the value defined as a project property as well
                    boolean isGroupIdFound = tag.getChildValue(ARTIFACTITEM_GROUP_ID)
                            .map(a -> matchesGlob(a, oldGroupId))
                            .orElse(oldGroupId == null);
                    if (!isGroupIdFound && getResolutionResult().getPom().getProperties() != null) {
                        if (tag.getChildValue("groupId").isPresent() && tag.getChildValue(ARTIFACTITEM_GROUP_ID).get().trim().startsWith("${")) {
                            String propertyKey = tag.getChildValue(ARTIFACTITEM_GROUP_ID).get().trim();
                            String value = getResolutionResult().getPom().getValue(propertyKey);
                            isGroupIdFound = value != null && matchesGlob(value, oldGroupId);
                        }
                    }

                    // Find out whether oldArtifactId really exists, consider the value defined as a project property as well
                    boolean isArtifactIdFound = tag.getChildValue(ARTIFACTITEM_ARTIFACT_ID)
                            .map(a -> matchesGlob(a, oldArtifactId))
                            .orElse(oldArtifactId == null);
                    if (!isArtifactIdFound && getResolutionResult().getPom().getProperties() != null) {
                        if (tag.getChildValue("artifactId").isPresent() && tag.getChildValue(ARTIFACTITEM_ARTIFACT_ID).get().trim().startsWith("${")) {
                            String propertyKey = tag.getChildValue(ARTIFACTITEM_ARTIFACT_ID).get().trim();
                            String value = getResolutionResult().getPom().getValue(propertyKey);
                            isArtifactIdFound = value != null && matchesGlob(value, oldArtifactId);
                        }
                    }

                    // Change the child tag value only when oldGroupId and oldArtifactId is found
                    if (isGroupIdFound && isArtifactIdFound) {
                        if (newGroupId != null) {
                            t = changeChildTagValue(t, ARTIFACTITEM_GROUP_ID, newGroupId, ctx);
                        }

                        if (newArtifactId != null) {
                            t = changeChildTagValue(t, ARTIFACTITEM_ARTIFACT_ID, newArtifactId, ctx);
                        }

                        if (newVersion != null) {
                            t = changeChildTagValue(t, ARTIFACTITEM_ARTIFACT_VERSION, newVersion, ctx);
                        }
                    }
                }
                return t;
            }
        };
    }
}