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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class represents a recipe that comments out specified WebLogic properties, such as deprecated ones.
 * It extends the Recipe class and provides functionality to add comments to specified
 * WebLogic properties and comment out the property.
 * 
 * <p>Annotations:</p>
 * <ul>
 *   <li>@EqualsAndHashCode(callSuper = false): Generates equals and hashCode methods without calling superclass methods.</li>
 *   <li>@Value: Lombok annotation to generate a value class with final fields, getters, and other utility methods.</li>
 * </ul>
 * 
 * <p>Options:</p>
 * <ul>
 *   <li>propertyKey: The name of the property key to comment out. Example: "com.oracle.weblogic.lifecycle.Orchestrator".</li>
 *   <li>comment: The comment to replace the property key. Example: "This property is deprecated and no longer applicable starting from WebLogic Server 14.1.2".</li>
 * </ul>
 * 
 * <p>Methods:</p>
 * <ul>
 *   <li>getDisplayName(): Returns the display name of the recipe.</li>
 *   <li>getDescription(): Returns the description of the recipe.</li>
 *   <li>getVisitor(): Returns a TreeVisitor that applies the recipe to comment out the specified property key in properties and YAML files.</li>
 * </ul>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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