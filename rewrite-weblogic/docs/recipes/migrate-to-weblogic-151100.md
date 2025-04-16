# Migrate to WebLogic 15.1.1
**com.oracle.weblogic.rewrite.UpgradeTo1511**

This recipe applies the changes required for migrating applications to WebLogic Server 15.1.1.0.0.

### Tags:
  - weblogic
  - java

### Recipe source

[weblogic-15.1.1.yaml](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/weblogic-15.1.1.yaml)

### Recipe list:
    - com.oracle.weblogic.rewrite.OutputRecipeVersion
    - com.oracle.weblogic.rewrite.UpdateBuildToWebLogic1511
    - [com.oracle.weblogic.rewrite.CheckAndCommentOutDeprecations1511](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/weblogic-deprecations.yaml)
    - com.oracle.weblogic.rewrite.MigrateWebLogicSchemasTo1511
    - com.oracle.weblogic.rewrite.ChangeJakartaInjectAPIDependencyScope
    - com.oracle.weblogic.rewrite.ChangeJAXBBindAPIDependencyScope

### Usage

This recipe will update the WebLogic version to 15.1.1 for the Maven build and comment out deprecated and removed APIs.

**NOTE**: The UpgradeTo1511 recipe must be run **after** the Java and Jakarta recipes.

#### Maven POM

1. Add the following to your Maven POM file.
```
<plugin>
    <groupId>org.openrewrite.maven</groupId>
    <artifactId>rewrite-maven-plugin</artifactId>
    <version>6.3.1</version>
    <configuration>
        <exportDatatables>true</exportDatatables>
        <activeRecipes>
            <recipe>com.oracle.weblogic.rewrite.UpgradeTo1511</recipe>
        </activeRecipes>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>org.openrewrite.recipe</groupId>
            <artifactId>rewrite-migrate-java</artifactId>
            <version>3.4.0</version>
        </dependency>
        <dependency>
            <groupId>com.oracle.weblogic.rewrite</groupId>
            <artifactId>rewrite-weblogic</artifactId>
            <version>[0.4.5,)</version>
        </dependency>
    </dependencies>
</plugin>
...
```
2. Run `mvn rewrite:run` to run the recipe.

> [!TIP]  
> If you just want to dry run the recipe without changing the code, use `mvn rewrite:DryRun`. For more details when using Maven, see [here](https://docs.openrewrite.org/reference/rewrite-maven-plugin).

#### Gradle

1. Add the following to your `build.gradle` file:

```
plugins {
    id("org.openrewrite.rewrite") version("7.2.0")
}

rewrite {
    activeRecipe("com.oracle.weblogic.rewrite.UpgradeTo1511")
    setExportDatatables(true)
}

repositories {
    mavenCentral()
}

dependencies {
    rewrite("org.openrewrite.recipe:rewrite-migrate-java:3.4.0")
    rewrite("com.oracle.weblogic.rewrite:rewrite-weblogic:+")
}
```
2. Run `gradle rewriteRun` to run the recipe.

> [!TIP]  
> If you just want to dry run the recipe without changing the code, use `gradle rewriteDryRun`. For more details about the "dryRun" task, see [here](https://docs.openrewrite.org/reference/gradle-plugin-configuration#the-dryrun-task).

#### Maven command line

> [!NOTE]
> You will need to have [Maven](https://maven.apache.org/download.cgi) installed on your machine before you can run the following command.

```
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST \
  -Drewrite.activeRecipes=com.oracle.weblogic.rewrite.UpgradeTo1511 \
  -Drewrite.exportDatatables=true
  ```
