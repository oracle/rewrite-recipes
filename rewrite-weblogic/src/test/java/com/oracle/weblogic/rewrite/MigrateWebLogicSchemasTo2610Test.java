/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite;

import org.junit.jupiter.api.Test;
import org.openrewrite.Recipe;
import org.openrewrite.config.Environment;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.xml.Assertions.xml;

class MigrateWebLogicSchemasTo2610Test implements RewriteTest {

    private Recipe recipe() {
        return Environment.builder()
                .scanRuntimeClasspath("com.oracle.weblogic")
                .build()
                .activateRecipes("com.oracle.weblogic.rewrite.MigrateWebLogicSchemasTo2610");
    }

    @Test
    void updatesWebLogicXmlSchemaTo20() {
        rewriteRun(spec -> spec.recipe(recipe()),
          xml(
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <weblogic-web-app xmlns="http://xmlns.oracle.com/weblogic/weblogic-web-app"
                                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                xsi:schemaLocation="http://xmlns.oracle.com/weblogic/weblogic-web-app http://xmlns.oracle.com/weblogic/weblogic-web-app/1.9/weblogic-web-app.xsd">
                  <context-root>my-app</context-root>
              </weblogic-web-app>
              """,
            """
              <?xml version="1.0" encoding="UTF-8"?>
              <weblogic-web-app xmlns="http://xmlns.oracle.com/weblogic/weblogic-web-app"
                                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                xsi:schemaLocation="http://xmlns.oracle.com/weblogic/weblogic-web-app http://xmlns.oracle.com/weblogic/weblogic-web-app/2.0/weblogic-web-app.xsd">
                  <context-root>my-app</context-root>
              </weblogic-web-app>
              """,
            sourceSpec -> sourceSpec.path("src/main/webapp/WEB-INF/weblogic.xml")
          )
        );
    }

    @Test
    void updatesRemainingWebLogicDescriptorSchemas() {
        assertDescriptorMigration(
          "src/main/resources/META-INF/weblogic-ejb-jar.xml",
          "weblogic-ejb-jar",
          "http://xmlns.oracle.com/weblogic/weblogic-ejb-jar",
          "http://xmlns.oracle.com/weblogic/weblogic-ejb-jar http://xmlns.oracle.com/weblogic/weblogic-ejb-jar/1.7/weblogic-ejb-jar.xsd",
          "http://xmlns.oracle.com/weblogic/weblogic-ejb-jar http://xmlns.oracle.com/weblogic/weblogic-ejb-jar/2.0/weblogic-ejb-jar.xsd"
        );
        assertDescriptorMigration(
          "src/main/resources/META-INF/weblogic-rdbms-jar.xml",
          "weblogic-rdbms-jar",
          "http://xmlns.oracle.com/weblogic/weblogic-rdbms-jar",
          "http://xmlns.oracle.com/weblogic/weblogic-rdbms-jar http://xmlns.oracle.com/weblogic/weblogic-rdbms-jar/1.2/weblogic-rdbms-jar.xsd",
          "http://xmlns.oracle.com/weblogic/weblogic-rdbms-jar http://xmlns.oracle.com/weblogic/weblogic-rdbms-jar/2.0/weblogic-rdbms20-persistence.xsd"
        );
        assertDescriptorMigration(
          "src/main/resources/META-INF/persistence-configuration.xml",
          "persistence-configuration",
          "http://xmlns.oracle.com/weblogic/persistence-configuration",
          "http://xmlns.oracle.com/weblogic/persistence-configuration http://xmlns.oracle.com/weblogic/persistence-configuration/0.9/persistence-configuration.xsd",
          "http://xmlns.oracle.com/weblogic/persistence-configuration http://xmlns.oracle.com/weblogic/persistence-configuration/1.0/persistence-configuration.xsd"
        );
        assertDescriptorMigration(
          "src/main/resources/META-INF/weblogic-webservices.xml",
          "weblogic-webservices",
          "http://xmlns.oracle.com/weblogic/weblogic-webservices",
          "http://xmlns.oracle.com/weblogic/weblogic-webservices http://xmlns.oracle.com/weblogic/weblogic-webservices/1.1/weblogic-webservices.xsd",
          "http://xmlns.oracle.com/weblogic/weblogic-webservices http://xmlns.oracle.com/weblogic/weblogic-webservices/2.0/weblogic-webservices.xsd"
        );
        assertDescriptorMigration(
          "src/main/resources/META-INF/weblogic-wsee-clientHandlerChain.xml",
          "weblogic-wsee-clientHandlerChain",
          "http://xmlns.oracle.com/weblogic/weblogic-wsee-clientHandlerChain",
          "http://xmlns.oracle.com/weblogic/weblogic-wsee-clientHandlerChain http://xmlns.oracle.com/weblogic/weblogic-wsee-clientHandlerChain/1.0/weblogic-wsee-clientHandlerChain.xsd",
          "http://xmlns.oracle.com/weblogic/weblogic-wsee-clientHandlerChain http://xmlns.oracle.com/weblogic/weblogic-wsee-clientHandlerChain/2.0/weblogic-wsee-clientHandlerChain.xsd"
        );
        assertDescriptorMigration(
          "src/main/resources/META-INF/weblogic-webservices-policy.xml",
          "webservice-policy-ref",
          "http://xmlns.oracle.com/weblogic/webservice-policy-ref",
          "http://xmlns.oracle.com/weblogic/webservice-policy-ref http://xmlns.oracle.com/weblogic/webservice-policy-ref/1.1/webservice-policy-ref.xsd",
          "http://xmlns.oracle.com/weblogic/webservice-policy-ref http://xmlns.oracle.com/weblogic/webservice-policy-ref/2.0/webservice-policy-ref.xsd"
        );
        assertDescriptorMigration(
          "src/main/resources/META-INF/weblogic-wsee-standaloneclient.xml",
          "weblogic-wsee-standaloneclient",
          "http://xmlns.oracle.com/weblogic/weblogic-wsee-standaloneclient",
          "http://xmlns.oracle.com/weblogic/weblogic-wsee-standaloneclient http://xmlns.oracle.com/weblogic/weblogic-wsee-standaloneclient/1.0/weblogic-wsee-standaloneclient.xsd",
          "http://xmlns.oracle.com/weblogic/weblogic-wsee-standaloneclient http://xmlns.oracle.com/weblogic/weblogic-wsee-standaloneclient/2.0/weblogic-wsee-standaloneclient.xsd"
        );
        assertDescriptorMigration(
          "src/main/resources/META-INF/weblogic-ra.xml",
          "weblogic-connector",
          "http://xmlns.oracle.com/weblogic/weblogic-connector",
          "http://xmlns.oracle.com/weblogic/weblogic-connector http://xmlns.oracle.com/weblogic/weblogic-connector/1.5/weblogic-connector.xsd",
          "http://xmlns.oracle.com/weblogic/weblogic-connector http://xmlns.oracle.com/weblogic/weblogic-connector/2.0/weblogic-connector.xsd"
        );
        assertDescriptorMigration(
          "src/main/resources/META-INF/weblogic-application.xml",
          "weblogic-application",
          "http://xmlns.oracle.com/weblogic/weblogic-application",
          "http://xmlns.oracle.com/weblogic/weblogic-application http://xmlns.oracle.com/weblogic/weblogic-application/1.8/weblogic-application.xsd",
          "http://xmlns.oracle.com/weblogic/weblogic-application http://xmlns.oracle.com/weblogic/weblogic-application/2.0/weblogic-application.xsd"
        );
        assertDescriptorMigration(
          "src/main/resources/META-INF/weblogic-application-client.xml",
          "weblogic-application-client",
          "http://xmlns.oracle.com/weblogic/weblogic-application-client",
          "http://xmlns.oracle.com/weblogic/weblogic-application-client http://xmlns.oracle.com/weblogic/weblogic-application-client/1.6/weblogic-application-client.xsd",
          "http://xmlns.oracle.com/weblogic/weblogic-application-client http://xmlns.oracle.com/weblogic/weblogic-application-client/2.0/weblogic-application-client.xsd"
        );
        assertDescriptorMigration(
          "src/main/resources/META-INF/application-client.xml",
          "weblogic-application-client",
          "http://xmlns.oracle.com/weblogic/weblogic-application-client",
          "http://xmlns.oracle.com/weblogic/weblogic-application-client http://xmlns.oracle.com/weblogic/weblogic-application-client/1.6/weblogic-application-client.xsd",
          "http://xmlns.oracle.com/weblogic/weblogic-application-client http://xmlns.oracle.com/weblogic/weblogic-application-client/2.0/weblogic-application-client.xsd"
        );
        assertDescriptorMigration(
          "src/main/resources/WEB-INF/weblogic-pubsub.xml",
          "weblogic-pubsub",
          "http://xmlns.oracle.com/weblogic/weblogic-pubsub",
          "http://xmlns.oracle.com/weblogic/weblogic-pubsub http://xmlns.oracle.com/weblogic/weblogic-pubsub/0.9/weblogic-pubsub.xsd",
          "http://xmlns.oracle.com/weblogic/weblogic-pubsub http://xmlns.oracle.com/weblogic/weblogic-pubsub/1.0/weblogic-pubsub.xsd"
        );
        assertDescriptorMigration(
          "src/main/resources/jms/example-jms.xml",
          "weblogic-jms",
          "http://xmlns.oracle.com/weblogic/weblogic-jms",
          "http://xmlns.oracle.com/weblogic/jms http://xmlns.oracle.com/weblogic/weblogic-jms/1.8/weblogic-jms.xsd",
          "http://xmlns.oracle.com/weblogic/weblogic-jms http://xmlns.oracle.com/weblogic/weblogic-jms/1.1/weblogic-jms.xsd"
        );
        assertDescriptorMigration(
          "src/main/resources/jdbc/example-jdbc.xml",
          "jdbc-data-source",
          "http://xmlns.oracle.com/weblogic/jdbc-data-source",
          "http://xmlns.oracle.com/weblogic/jdbc-data-source http://xmlns.oracle.com/weblogic/jdbc-data-source/1.6/jdbc-data-source.xsd",
          "http://xmlns.oracle.com/weblogic/jdbc-data-source http://xmlns.oracle.com/weblogic/jdbc-data-source/2.0/jdbc-data-source.xsd"
        );
        assertDescriptorMigration(
          "src/main/resources/META-INF/plan.xml",
          "deployment-plan",
          "http://xmlns.oracle.com/weblogic/deployment-plan",
          "http://xmlns.oracle.com/weblogic/deployment-plan http://xmlns.oracle.com/weblogic/deployment-plan/1.01/deployment-plan.xsd",
          "http://xmlns.oracle.com/weblogic/deployment-plan http://xmlns.oracle.com/weblogic/deployment-plan/2.0/deployment-plan.xsd"
        );
        assertDescriptorMigration(
          "src/main/resources/META-INF/resource-deployment-plan.xml",
          "resource-deployment-plan",
          "http://xmlns.oracle.com/weblogic/resource-deployment-plan",
          "http://xmlns.oracle.com/weblogic/weblogic-diagnostics http://xmlns.oracle.com/weblogic/weblogic-diagnostics/2.0/weblogic-diagnostics.xsd",
          "http://xmlns.oracle.com/weblogic/resource-deployment-plan http://xmlns.oracle.com/weblogic/resource-deployment-plan/1.0/resource-deployment-plan.xsd"
        );
    }

    private void assertDescriptorMigration(
      String path,
      String rootElement,
      String namespace,
      String oldSchemaLocation,
      String newSchemaLocation
    ) {
        String before = """
          <?xml version="1.0" encoding="UTF-8"?>
          <%s xmlns="%s"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="%s">
              <implementation-class>javax.example.Type</implementation-class>
          </%s>
          """.formatted(rootElement, namespace, oldSchemaLocation, rootElement);
        String after = """
          <?xml version="1.0" encoding="UTF-8"?>
          <%s xmlns="%s"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="%s">
              <implementation-class>jakarta.example.Type</implementation-class>
          </%s>
          """.formatted(rootElement, namespace, newSchemaLocation, rootElement);

        rewriteRun(
          spec -> spec.recipe(recipe()),
          xml(before, after, sourceSpec -> sourceSpec.path(path))
        );
    }
}
