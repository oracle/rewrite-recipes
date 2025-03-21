/*
 * Copyright (c) 2025 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite;

import org.openrewrite.*;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.xml.tree.Xml;

/**
 * A recipe that outputs the version of the recipe after it runs.
 * This is useful for tracking and debugging purposes.
 * Currently only works with recipes called using Maven. Gradle TBD.
 */
public class OutputRecipeVersion extends Recipe {

    @Override
    public String getDisplayName() {
        return "Output Recipe Version";
    }

    @Override
    public String getDescription() {
        return "Displays the version of the recipe after it runs.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MavenIsoVisitor<ExecutionContext>() {
            @Override
            public Xml.Document visitDocument(Xml.Document document, ExecutionContext ctx) {
                Xml.Document d = super.visitDocument(document, ctx);

                // Display the title and version of the recipe
                displayRecipeInfo();

                return d;
            }
        };
    }

    private void displayRecipeInfo() {
        Package pkg = OutputRecipeVersion.class.getPackage();
        String title = pkg.getImplementationTitle();
        String version = pkg.getImplementationVersion();
        if (title == null) {
            title = "Title information not available";
        }
        if (version == null) {
            version = "Version information not available";
        }
        System.out.println(String.format("--- Running recipe(s) from %s:%s", title, version));
    }
}
