# Migrate to Jakarta EE 9.1
**com.oracle.weblogic.rewrite.JakartaEE9_1**

This recipe applies the changes required for migrating to Jakarta EE 9.1.

### Tags:
  - weblogic
  - jakarta
  - jakartaee

### Recipe source

[jakarta-ee-9.1.yaml](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/jakarta-ee-9.1.yaml)

### Recipe list:

  - org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
  - com.oracle.weblogic.rewrite.jakarta.Faces2xMigrationToJakartaFaces3x
  - com.oracle.weblogic.rewrite.jakarta.JakartaEeNamespaces9_1
  - com.oracle.weblogic.rewrite.jakarta.RemovalsServletJakarta9
  - com.oracle.weblogic.rewrite.jakarta.JavaxToJakartaCdiExtensions
  - com.oracle.weblogic.rewrite.jakarta.MigrateTagLibsToJakartaEE9
  - com.oracle.weblogic.rewrite.jakarta.MigrateJavaxMVCToJakartaEE9
  - com.oracle.weblogic.rewrite.jakarta.MigrateJavaxWebToJakartaWeb9
  - com.oracle.weblogic.rewrite.jakarta.JavaxAnnotationMigrationToJakarta9Annotation
  - com.oracle.weblogic.rewrite.jakarta.OrgGlassfishJavaxElToJakartaEl
  - com.oracle.weblogic.rewrite.jakarta.UpdateJakartaPlatform9_1
  - com.oracle.weblogic.rewrite.jakarta.MitigateUnaffectedNonEEJakarta9Packages
  - com.oracle.weblogic.rewrite.jakarta.UpgradeCommonOpenSourceLibraries

### Usage

This recipe will migrate `javax` packages to `jakarta` and update Jakarta EE platform dependencies to 9.1.0.

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
            <recipe>com.oracle.weblogic.rewrite.JakartaEE9_1</recipe>
        </activeRecipes>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>com.oracle.weblogic.rewrite</groupId>
            <artifactId>rewrite-weblogic</artifactId>
            <version>[0.4.5,)</version>
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

> [!TIP]  
> If you just want to dry run the recipe without changing the code, use `mvn rewrite:DryRun`. For more details when using Maven, see [here](https://docs.openrewrite.org/reference/rewrite-maven-plugin).

#### Gradle

1. Add the following to your `build.gradle` file:

```
plugins {
    id("org.openrewrite.rewrite") version("7.1.4")
}

rewrite {
    activeRecipe("com.oracle.weblogic.rewrite.JakartaEE9_1")
    setExportDatatables(true)
}

repositories {
    mavenCentral()
}

dependencies {
    rewrite("org.openrewrite.recipe:rewrite-migrate-java:3.2.0")
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
  -Drewrite.activeRecipes=com.oracle.weblogic.rewrite.JakartaEE9_1 \
  -Drewrite.exportDatatables=true
  ```
