/*
 * Copyright (c) 2025 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.test.RewriteTest;
import static org.openrewrite.maven.Assertions.pomXml;

public class OutputRecipeVersionTest implements RewriteTest {
    
    @DocumentExample
    @Test
    void testOutputRecipeVersion() {
        rewriteRun(spec -> spec.recipe(new OutputRecipeVersion()),
          //language=xml
          pomXml(
            """
              <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.mycompany.app</groupId>
                    <artifactId>my-app</artifactId>
                    <version>1.0.0</version>
                    <build>
                        <plugins>
                            <plugin>
                                <groupId>org.apache.maven.plugins</groupId>
                                <artifactId>maven-jar-plugin</artifactId>
                                <version>3.4.2</version>
                                <configuration>
                                    <archive>
                                        <manifestEntries>
                                            <Implementation-Title>${project.name}</Implementation-Title>
                                            <Implementation-Version>${project.version}</Implementation-Version>
                                        </manifestEntries>
                                    </archive>
                                </configuration>
                            </plugin>
                        </plugins>
                    </build>
                    </project>
              """
          )
        );
    }
}
