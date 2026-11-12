/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite.jakarta;

import org.junit.jupiter.api.Test;
import org.openrewrite.Recipe;
import org.openrewrite.config.Environment;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.xml.Assertions.xml;

class PreserveLegacyBeansXmlDiscoveryModeTest implements RewriteTest {

    private Recipe recipe() {
        return Environment.builder()
                .scanRuntimeClasspath()
                .build()
                .activateRecipes("com.oracle.weblogic.rewrite.jakarta.PreserveLegacyBeansXmlDiscoveryMode");
    }

    @Test
    void preservesEmptyLegacyDescriptorAsAll() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_3_0.xsd"/>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_3_0.xsd" bean-discovery-mode="all" version="3.0"/>
              """,
            source -> source.path("src/main/webapp/WEB-INF/beans.xml")
          )
        );
    }

    @Test
    void preservesVersionedCdi3DescriptorAsAll() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_3_0.xsd"
                     version="3.0"/>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_3_0.xsd"
                     version="3.0" bean-discovery-mode="all"/>
              """,
            source -> source.path("src/main/webapp/WEB-INF/beans.xml")
          )
        );
    }

    @Test
    void leavesCdi4DefaultAnnotated() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_4_0.xsd"
                     version="4.0"/>
              """,
            source -> source.path("src/main/webapp/WEB-INF/beans.xml")
          )
        );
    }

    @Test
    void leavesCdi41DefaultAnnotated() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_4_1.xsd"
                     version="4.1"/>
              """,
            source -> source.path("src/main/webapp/WEB-INF/beans.xml")
          )
        );
    }

    @Test
    void preservesExplicitDiscoveryMode() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <beans xmlns="https://jakarta.ee/xml/ns/jakartaee"
                     version="3.0"
                     bean-discovery-mode="annotated"/>
              """,
            source -> source.path("src/main/webapp/WEB-INF/beans.xml")
          )
        );
    }

    @Test
    void doesNotCreateBeansXmlWhenAbsent() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <web-app xmlns="https://jakarta.ee/xml/ns/jakartaee" version="6.1"/>
              """,
            source -> source.path("src/main/webapp/WEB-INF/web.xml")
          )
        );
    }
}
