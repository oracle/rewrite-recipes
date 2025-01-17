/*
 * Copyright (c) 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.openrewrite.*;
import org.openrewrite.properties.tree.Properties;
import org.openrewrite.yaml.tree.Yaml;

@EqualsAndHashCode(callSuper = false)
@Value
public class CommentOutPropertyKey extends Recipe {

    @Override
    public String getDisplayName() {
        return "Comment out WebLogic properties";
    }

    @Override
    public String getDescription() {
        return "Add comment to specified WebLogic properties, and comment out the property.";
    }

    @Option(displayName = "Property key",
            description = "The name of the property key to comment out.",
            example = "com.oracle.weblogic.lifecycle.Orchestrator")
    String propertyKey;

    @Option(displayName = "Comment",
            description = "Comment to replace the property key.",
            example = "This property is deprecated and no longer applicable starting from WebLogic Server 14.1.2")
    String comment;

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        Recipe changeProperties = new org.openrewrite.properties.AddPropertyComment(propertyKey, comment, true);
        Recipe changeYaml = new org.openrewrite.yaml.CommentOutProperty(propertyKey, comment, true);
        return new TreeVisitor<Tree, ExecutionContext>() {
            @Override
            public @Nullable Tree preVisit(@NonNull Tree tree, ExecutionContext ctx) {
                stopAfterPreVisit();
                if (tree instanceof Properties.File) {
                    return changeProperties.getVisitor().visit(tree, ctx);
                } else if (tree instanceof Yaml.Documents) {
                    return changeYaml.getVisitor().visit(tree, ctx);
                }
                return tree;
            }
        };
    }
}