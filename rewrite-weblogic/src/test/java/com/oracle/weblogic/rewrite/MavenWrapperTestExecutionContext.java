/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite;

import org.openrewrite.ExecutionContext;
import org.openrewrite.HttpSenderExecutionContextView;
import org.openrewrite.ipc.http.HttpSender;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

final class MavenWrapperTestExecutionContext {

    static ExecutionContext configure(ExecutionContext executionContext) {
        HttpSender httpSender = request -> {
            String url = request.getUrl().toString();
            if (url.contains("/org/apache/maven/wrapper/maven-wrapper-distribution/maven-metadata.xml")) {
                return response(metadata("org.apache.maven.wrapper", "maven-wrapper-distribution", "3.3.4"));
            }
            if (url.contains("/org/apache/maven/apache-maven/maven-metadata.xml")) {
                return response(metadata("org.apache.maven", "apache-maven", "3.9.16"));
            }
            if (url.endsWith("/maven-wrapper-3.3.4.jar")) {
                return response("test Maven Wrapper jar");
            }
            if (url.endsWith("/apache-maven-3.9.16-bin.zip")) {
                return response("test Maven distribution");
            }
            return new HttpSender.Response(404, new ByteArrayInputStream(new byte[0]), () -> {
            });
        };
        return HttpSenderExecutionContextView.view(executionContext)
                .setHttpSender(httpSender)
                .setLargeFileHttpSender(httpSender);
    }

    private static HttpSender.Response response(String body) {
        return new HttpSender.Response(
                200,
                new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)),
                () -> {
                });
    }

    private static String metadata(String groupId, String artifactId, String version) {
        return """
          <metadata>
              <groupId>%s</groupId>
              <artifactId>%s</artifactId>
              <versioning>
                  <latest>%s</latest>
                  <release>%s</release>
                  <versions>
                      <version>%s</version>
                  </versions>
              </versioning>
          </metadata>
          """.formatted(groupId, artifactId, version, version, version);
    }

    private MavenWrapperTestExecutionContext() {
    }
}
