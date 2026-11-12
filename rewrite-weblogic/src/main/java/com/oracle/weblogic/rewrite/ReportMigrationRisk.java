/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.SourceFile;
import org.openrewrite.Tree;
import org.openrewrite.TreeVisitor;
import org.openrewrite.table.SearchResults;

/**
 * Records a migration risk in the standard OpenRewrite search-results table
 * without adding a persistent search marker to application source.
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class ReportMigrationRisk extends Recipe {

    @Option(displayName = "Result",
            description = "The concise result recorded for the matching source file.",
            example = "-Djava.security.manager")
    String result;

    @Option(displayName = "Risk description",
            description = "The actionable migration-risk description.",
            example = "Review this application configuration before migrating to WebLogic 26.1.0.")
    String riskDescription;

    @Option(displayName = "Reporting recipe",
            description = "The public recipe name responsible for the migration-risk result.",
            example = "com.oracle.weblogic.rewrite.FindJavaSecurityManagerStartupOption2610")
    String recipeName;

    @EqualsAndHashCode.Exclude
    transient SearchResults searchResults = new SearchResults(this);

    @JsonCreator
    public ReportMigrationRisk(@JsonProperty("result") String result,
                               @JsonProperty("riskDescription") String riskDescription,
                               @JsonProperty("recipeName") String recipeName) {
        this.result = result;
        this.riskDescription = riskDescription;
        this.recipeName = recipeName;
    }

    @Override
    public String getDisplayName() {
        return "Report a WebLogic 26.1 migration risk";
    }

    @Override
    public String getDescription() {
        return "Record a matched WebLogic 26.1 migration risk without modifying application source.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new TreeVisitor<Tree, ExecutionContext>() {
            @Override
            public Tree preVisit(Tree tree, ExecutionContext ctx) {
                if (tree instanceof SourceFile) {
                    String sourcePath = ((SourceFile) tree).getSourcePath().toString();
                    searchResults.insertRow(ctx, new SearchResults.Row(
                            sourcePath,
                            sourcePath,
                            result,
                            riskDescription,
                            recipeName));
                    stopAfterPreVisit();
                }
                return tree;
            }
        };
    }
}
