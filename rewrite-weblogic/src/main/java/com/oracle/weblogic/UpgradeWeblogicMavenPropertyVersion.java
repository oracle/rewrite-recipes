/*
 * Copyright (c) 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.*;
import org.openrewrite.marker.Markup;
import org.openrewrite.maven.ChangePropertyValue;
import org.openrewrite.maven.MavenDownloadingException;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.maven.tree.MavenResolutionResult;
import org.openrewrite.semver.Semver;
import org.openrewrite.xml.tree.Xml;

@Value
@EqualsAndHashCode(callSuper = false)
public class UpgradeWeblogicMavenPropertyVersion extends Recipe {
    @Option(displayName = "New version",
            description = "An exact version number, or node-style semver selector used to select the version number.",
            example = "14.1.2-0-0")
    String newVersion;

    @Override
    public Validated validate() {
        Validated validated = super.validate();
        //noinspection ConstantConditions
        if (newVersion != null) {
            validated = validated.and(Semver.validate(newVersion, null));
        }
        return validated;
    }

    @Override
    public String getDisplayName() {
        return "Upgrade `weblogic.version` Maven property";
    }

    @Override
    public String getDescription() {
        return "Set the maven weblogic.version property according to a node-style semver selector or to a specific version number.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MavenIsoVisitor<ExecutionContext>() {
            @Override
            public Xml.Document visitDocument(Xml.Document document, ExecutionContext ctx) {
                Xml.Document d = super.visitDocument(document, ctx);
                MavenResolutionResult model = getResolutionResult();
                String currentVersion = model.getPom().getProperties().get("weblogic.version");
                if (currentVersion != null && !currentVersion.isEmpty()) {
                    try {
                        WeblogicVersionHelper.getNewerVersion(newVersion, currentVersion, ctx)
                                .ifPresent(latestVersion -> doAfterVisit(new ChangePropertyValue("weblogic.version", latestVersion, false, true).getVisitor()));
                    } catch (MavenDownloadingException e) {
                        return Markup.warn(document, e);
                    }
                }
                return d;
            }
        };
    }
}
