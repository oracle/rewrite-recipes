# Migrate From JSF 2.x to Jakarta Server Faces 3.x
**com.oracle.weblogic.rewrite.Faces2xMigrationToJakarta3x**

Jakarta EE 9 uses Jakarta Server Faces 3.0, a major upgrade to Jakarta packages and XML namespaces. This recipe applies the changes required for migrating from JSF 2.x to Jakarta Server Faces 3.x.

### Tags:
- jakarta
- jakartaee
- faces
- jsf

### Recipe source

[jakarta-faces-3.yaml](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/jakarta-faces-3.yaml)

### Recipe list:

- org.openrewrite.java.migrate.jakarta.JakartaFacesEcmaScript
- org.openrewrite.java.migrate.jakarta.RemovedUIComponentConstant
- com.oracle.weblogic.rewrite.jakarta.JakartaFaces3WebXml
- com.oracle.weblogic.rewrite.jakarta.JakartaFaces3Xhtml
- com.oracle.weblogic.rewrite.jakarta.JavaxFacesConfigXmlToJakartaFaces3ConfigXml
- com.oracle.weblogic.rewrite.jakarta.JavaxFacesTagLibraryXmlToJakartaFaces3TagLibraryXml
- com.oracle.weblogic.rewrite.jakarta.JavaxWebFragmentXmlToJakartaWebFragmentXml5
- com.oracle.weblogic.rewrite.jakarta.JavaxWebXmlToJakartaWebXml5
- com.oracle.weblogic.rewrite.jakarta.FacesJNDINamesChanged3
- com.oracle.weblogic.rewrite.jakarta.RemovedJakartaFaces3ExpressionLanguageClasses
- com.oracle.weblogic.rewrite.jakarta.RemovedJakartaFaces3ResourceResolver
- com.oracle.weblogic.rewrite.jakarta.RemovedStateManagerMethods3
- com.oracle.weblogic.rewrite.jakarta.FacesManagedBeansRemoved3
- com.oracle.weblogic.rewrite.jakarta.UpgradeFacesOpenSourceLibraries3

### Usage

This recipe will find and replace legacy JSF namespaces and `javax` references with Jakarta Server Faces values.

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
            <recipe>com.oracle.weblogic.rewrite.Faces2xMigrationToJakarta3x</recipe>
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
    activeRecipe("com.oracle.weblogic.rewrite.Faces2xMigrationToJakarta3x")
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
  -Drewrite.activeRecipes=com.oracle.weblogic.rewrite.Faces2xMigrationToJakarta3x \
  -Drewrite.exportDatatables=true
  ```
