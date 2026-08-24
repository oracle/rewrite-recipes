/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.search.FindTypes;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaType;
import org.openrewrite.java.tree.NameTree;
import org.openrewrite.java.tree.TypeUtils;
import org.openrewrite.table.SearchResults;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Reports uses of APIs removed from the Jakarta EE 11 aggregate and identifies
 * the standalone API supplied by WebLogic Server 26.1.
 */
public class FindRemovedJakartaEE11Apis2610 extends Recipe {

    private static final List<RemovedApi> REMOVED_APIS = Arrays.asList(
            new RemovedApi(
                    "jakarta.xml.bind..*",
                    "jakarta.xml.bind",
                    "Jakarta XML Binding",
                    "jakarta.xml.bind:jakarta.xml.bind-api:4.0.2"),
            new RemovedApi(
                    "jakarta.xml.soap..*",
                    "jakarta.xml.soap",
                    "Jakarta SOAP with Attachments",
                    "jakarta.xml.soap:jakarta.xml.soap-api:3.0.2"),
            new RemovedApi(
                    "jakarta.jws..*",
                    "jakarta.jws",
                    "Jakarta Web Services Metadata",
                    "jakarta.jws:jakarta.jws-api:3.0.0"),
            new RemovedApi(
                    "jakarta.xml.ws..*",
                    "jakarta.xml.ws",
                    "Jakarta XML Web Services",
                    "jakarta.xml.ws:jakarta.xml.ws-api:4.0.2"));

    private final transient SearchResults searchResults = new SearchResults(this);

    @Override
    public String getDisplayName() {
        return "Find Jakarta EE 11 removed API usage";
    }

    @Override
    public String getDescription() {
        return "Report uses of APIs removed from the Jakarta EE 11 aggregate and identify the standalone APIs supplied " +
                "by WebLogic Server 26.1.";
    }

    @Override
    public Set<String> getTags() {
        return new HashSet<>(Arrays.asList("weblogic", "jakarta", "dependencies", "migration"));
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public J.CompilationUnit visitCompilationUnit(J.CompilationUnit compilationUnit, ExecutionContext ctx) {
                J.CompilationUnit cu = super.visitCompilationUnit(compilationUnit, ctx);
                for (RemovedApi removedApi : REMOVED_APIS) {
                    Set<String> usedTypes = findUsedTypes(cu, removedApi);
                    if (usedTypes.isEmpty()) {
                        continue;
                    }

                    String sourcePath = cu.getSourcePath().toString();
                    searchResults.insertRow(ctx, new SearchResults.Row(
                            sourcePath,
                            sourcePath,
                            String.join(", ", usedTypes),
                            removedApi.technology + " was removed from the Jakarta EE 11 platform aggregate. " +
                                    "For WebLogic Server 26.1, declare `" + removedApi.coordinate +
                                    "` with `provided` scope, or migrate this usage away from the removed technology.",
                            getName()));
                }
                return cu;
            }

            private Set<String> findUsedTypes(J.CompilationUnit cu, RemovedApi removedApi) {
                Set<String> usedTypes = new TreeSet<>();
                for (NameTree typeUse : FindTypes.find(cu, removedApi.typePattern)) {
                    JavaType.FullyQualified type = TypeUtils.asFullyQualified(typeUse.getType());
                    if (type != null && type.getFullyQualifiedName().startsWith(removedApi.packagePrefix + ".")) {
                        usedTypes.add(type.getFullyQualifiedName());
                    }
                }

                for (J.Import anImport : cu.getImports()) {
                    String importedType = anImport.getQualid().printTrimmed(getCursor());
                    if (importedType.startsWith(removedApi.packagePrefix + ".")) {
                        usedTypes.add(importedType);
                    }
                }
                return usedTypes;
            }
        };
    }

    private static final class RemovedApi {
        private final String typePattern;
        private final String packagePrefix;
        private final String technology;
        private final String coordinate;

        private RemovedApi(String typePattern, String packagePrefix, String technology, String coordinate) {
            this.typePattern = typePattern;
            this.packagePrefix = packagePrefix;
            this.technology = technology;
            this.coordinate = coordinate;
        }
    }
}
