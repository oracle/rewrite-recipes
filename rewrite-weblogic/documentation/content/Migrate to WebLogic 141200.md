# Migrate to WebLogic 14.1.2
**com.oracle.weblogic.rewrite.UpgradeTo1412**

This recipe applies the changes required for upgrading to WebLogic Server 14.1.2.0.0.

### Tags:
  - weblogic
  - java

### Recipe Source

[weblogic-14.1.2.yaml](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/weblogic-14.1.2.yaml)

### Recipe List:
  - com.oracle.weblogic.rewrite.UpdateBuildToWebLogic1412
  - [com.oracle.weblogic.rewrite.CheckAndCommentOutDeprecations1412](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/weblogic-deprecations.yaml)

### Usage

This recipe will upgrade the WebLogic version to 14.1.2 for the Maven build, and comment out deprecated and removed APIs.

#### Maven POM

1. Add the following to your Maven POM file.
```
<plugin>
    <groupId>org.openrewrite.maven</groupId>
    <artifactId>rewrite-maven-plugin</artifactId>
    <version>5.43.0</version>
    <configuration>
        <exportDatatables>true</exportDatatables>
        <activeRecipes>
            <recipe>com.oracle.weblogic.rewrite.UpgradeTo1412</recipe>
        </activeRecipes>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>com.oracle.weblogic.rewrite</groupId>
            <artifactId>rewrite-weblogic</artifactId>
            <version>0.4.2</version>
        </dependency>
        <dependency>
            <groupId>org.openrewrite.recipe</groupId>
            <artifactId>rewrite-migrate-java</artifactId>
            <version>2.28.0</version>
        </dependency>
    </dependencies>
</plugin>
...
```
2. Run `mvn rewrite:run` to run the recipe.

#### Maven Command Line

**NOTE**: You will need to have [Maven](https://maven.apache.org/download.cgi) installed on your machine before you can run the following command.

```
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST \
  -Drewrite.activeRecipes=com.oracle.weblogic.rewrite.UpgradeTo1412 \
  -Drewrite.exportDatatables=true
  ```
