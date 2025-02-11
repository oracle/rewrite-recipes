package com.oracle.weblogic.rewrite;

import org.junit.jupiter.api.Test;
import org.openrewrite.ExecutionContext;
import org.openrewrite.properties.tree.Properties;
import org.openrewrite.DocumentExample;
import org.openrewrite.test.RewriteTest;
import org.openrewrite.yaml.tree.Yaml;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openrewrite.properties.Assertions.properties;
import static org.openrewrite.yaml.Assertions.yaml;

class CommentOutPropertyKeyTest implements RewriteTest {

    @Test
    public void testGetDisplayName() {
        CommentOutPropertyKey recipe = new CommentOutPropertyKey("com.oracle.weblogic.lifecycle.Orchestrator", "This property is deprecated and no longer applicable starting from WebLogic Server 14.1.2");
        assertEquals("Comment out WebLogic properties", recipe.getDisplayName());
    }

    @Test
    public void testGetDescription() {
        CommentOutPropertyKey recipe = new CommentOutPropertyKey("com.oracle.weblogic.lifecycle.Orchestrator", "This property is deprecated and no longer applicable starting from WebLogic Server 14.1.2");
        assertEquals("Add comment to specified WebLogic properties, and comment out the property.", recipe.getDescription());
    }

    @DocumentExample
    @Test
    void yamlComment() {
        rewriteRun(
          spec -> spec.recipe(new CommentOutPropertyKey("weblogic.zac.publishRoot", "This property has been removed.")),
          //language=yaml
          yaml(
            "weblogic.zac.publishRoot: myserver/zac",
            """
              # This property has been removed.
              # weblogic.zac.publishRoot: myserver/zac
              """)
        );
    }

    @Test
    void multipleYamlComments() {
        rewriteRun(
          spec -> spec.recipes(
            new CommentOutPropertyKey("test.propertyKey1", "my comment 1"),
            new CommentOutPropertyKey("test.propertyKey2", "my comment 2")
          ),
          //language=yaml
          yaml(
            """
              test:
                propertyKey1: xxx
                propertyKey2: yyy
                propertyKey3: zzz
              """,
            """
              test:
                # my comment 2
                # my comment 1
                # propertyKey1: xxx
                # propertyKey2: yyy
                propertyKey3: zzz
              """,
            spec -> spec.path("application.yaml")
              .afterRecipe(file ->
                assertThat(
                  ((Yaml.Mapping)
                    ((Yaml.Mapping) file.getDocuments().get(0)
                      .getBlock()).getEntries().get(0)
                      .getValue()).getEntries().get(0)
                    .getPrefix())
                  .isEqualTo(
                    """

                        # my comment 2
                        # my comment 1
                        # propertyKey1: xxx
                        # propertyKey2: yyy
                        \
                      """
                  )
              )
          )
        );
    }

    @Test
    void multiplePropertiesComments() {
        rewriteRun(
          spec -> spec.recipes(
            new CommentOutPropertyKey("test.propertyKey1", "my comment 1"),
            new CommentOutPropertyKey("test.propertyKey2", "my comment 2")
          ),
          //language=properties
          properties(
            """
              test.propertyKey1=xxx
              test.propertyKey2=yyy
              test.propertyKey3=zzz
              """,
            """
              # my comment 1
              # test.propertyKey1=xxx
              # my comment 2
              # test.propertyKey2=yyy
              test.propertyKey3=zzz
              """,
            spec -> spec.path("application.properties")
              .afterRecipe(file ->
                assertThat(((Properties.Comment) file.getContent().get(3)).getMessage())
                  .isEqualTo(" test.propertyKey2=yyy"))
          )
        );
    }
}
