/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite.spring.framework;

import org.junit.jupiter.api.Test;
import org.openrewrite.Recipe;
import org.openrewrite.config.Environment;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.maven.Assertions.pomXml;
import static org.openrewrite.xml.Assertions.xml;

class ConfigureJacksonClassloadingForSpring7Test implements RewriteTest {

    private Recipe recipe() {
        return Environment.builder()
                .scanRuntimeClasspath()
                .build()
                .activateRecipes(
                        "com.oracle.weblogic.rewrite.spring.framework.ConfigureJacksonClassloadingForSpring7");
    }

    @Test
    void createsWebLogicDescriptorForWarProject() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .cycles(2)
                .expectedCyclesThatMakeChanges(1),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>org.springframework.samples</groupId>
                  <artifactId>spring-framework-petclinic</artifactId>
                  <version>5.3.22</version>
                  <packaging>war</packaging>
              </project>
              """
          ),
          xml(
            null,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <weblogic-web-app xmlns="http://xmlns.oracle.com/weblogic/weblogic-web-app"
                                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                xsi:schemaLocation="http://xmlns.oracle.com/weblogic/weblogic-web-app http://xmlns.oracle.com/weblogic/weblogic-web-app/2.0/weblogic-web-app.xsd">
                  <container-descriptor>
                      <prefer-application-packages>
                          <package-name>com.fasterxml.jackson.*</package-name>
                          <package-name>jakarta.xml.bind.*</package-name>
                          <package-name>org.glassfish.jaxb.*</package-name>
                      </prefer-application-packages>
                      <prefer-application-resources>
                          <resource-name>META-INF/services/com.fasterxml.jackson.databind.Module</resource-name>
                      </prefer-application-resources>
                  </container-descriptor>
              </weblogic-web-app>
              """,
            source -> source.path("src/main/webapp/WEB-INF/weblogic.xml")
          )
        );
    }

    @Test
    void mergesPreferencesIntoExistingDescriptor() {
        rewriteRun(spec -> spec
                .recipe(recipe())
                .cycles(2)
                .expectedCyclesThatMakeChanges(1),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>existing-web-app</artifactId>
                  <version>1.0.0</version>
                  <packaging>war</packaging>
              </project>
              """
          ),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <weblogic-web-app xmlns="http://xmlns.oracle.com/weblogic/weblogic-web-app"
                                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                xsi:schemaLocation="http://xmlns.oracle.com/weblogic/weblogic-web-app http://xmlns.oracle.com/weblogic/weblogic-web-app/2.0/weblogic-web-app.xsd">
                  <context-root>existing</context-root>
                  <container-descriptor>
                      <prefer-application-packages>
                          <package-name>com.example.*</package-name>
                          <package-name>com.fasterxml.jackson.*</package-name>
                      </prefer-application-packages>
                      <prefer-application-resources>
                          <resource-name>META-INF/services/com.example.Service</resource-name>
                      </prefer-application-resources>
                  </container-descriptor>
              </weblogic-web-app>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <weblogic-web-app xmlns="http://xmlns.oracle.com/weblogic/weblogic-web-app"
                                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                xsi:schemaLocation="http://xmlns.oracle.com/weblogic/weblogic-web-app http://xmlns.oracle.com/weblogic/weblogic-web-app/2.0/weblogic-web-app.xsd">
                  <context-root>existing</context-root>
                  <container-descriptor>
                      <prefer-application-packages>
                          <package-name>com.example.*</package-name>
                          <package-name>com.fasterxml.jackson.*</package-name>
                          <package-name>jakarta.xml.bind.*</package-name>
                          <package-name>org.glassfish.jaxb.*</package-name>
                      </prefer-application-packages>
                      <prefer-application-resources>
                          <resource-name>META-INF/services/com.example.Service</resource-name>
                          <resource-name>META-INF/services/com.fasterxml.jackson.databind.Module</resource-name>
                      </prefer-application-resources>
                  </container-descriptor>
              </weblogic-web-app>
              """,
            source -> source.path("src/main/webapp/WEB-INF/weblogic.xml")
          )
        );
    }

    @Test
    void doesNotCreateDescriptorForJarProject() {
        rewriteRun(spec -> spec.recipe(recipe()),
          pomXml(
            """
              <project>
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>com.example</groupId>
                  <artifactId>spring-library</artifactId>
                  <version>1.0.0</version>
              </project>
              """
          )
        );
    }
}
