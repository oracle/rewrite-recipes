# Migrate to Jakarta EE 9.1 Namespaces
**com.oracle.weblogic.rewrite.jakarta.JakartaEeNamespaces9_1**

Java EE has been rebranded to Jakarta EE, necessitating an XML namespace relocation. This recipe helps you migrate from `javax` to `jakarta` EE 9.1 namespaces.

### Tags:
  - weblogic
  - jakarta
  - jakartaee
  - migration
  - namespaces

### Recipe source

[jakarta-ee-namespaces-9.1.yaml](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/jakarta-ee-namespaces-9.1.yaml)

### Recipe list:
- com.oracle.weblogic.rewrite.jakarta.JavaxWebHandlerXmlToJakarta9HandlerXml
- com.oracle.weblogic.rewrite.jakarta.JavaxBatchXmlToJakarta9BatchXml
- com.oracle.weblogic.rewrite.jakarta.JavaxBeansXmlToJakarta9BeansXml
- com.oracle.weblogic.rewrite.jakarta.JavaxBatchJobsXmlsToJakarta9BatchJobsXmls
- com.oracle.weblogic.rewrite.jakarta.JavaxApplicationXmlToJakarta9ApplicationXml
- com.oracle.weblogic.rewrite.jakarta.JavaxApplicationClientXmlToJakarta9ApplicationClientXml
- com.oracle.weblogic.rewrite.jakarta.JavaxRaXmlToJakarta9RaXml
- com.oracle.weblogic.rewrite.jakarta.JavaxEjbJarXmlToJakarta9EjbJarXml
- com.oracle.weblogic.rewrite.jakarta.JavaxWebServicesXmlToJakarta9WebServicesXml
- com.oracle.weblogic.rewrite.jakarta.JavaxPermissionsXmlToJakarta9PermissionsXml
- com.oracle.weblogic.rewrite.jakarta.JavaxWebJspTagLibraryTldsToJakarta9WebJspTagLibraryTlds
- com.oracle.weblogic.rewrite.jakarta.JavaxBindingsSchemaXjbsToJakarta9BindingsSchemaXjbs
- com.oracle.weblogic.rewrite.jakarta.JavaxValidationMappingXmlsToJakarta9ValidationMappingXmls
- org.openrewrite.java.migrate.jakarta.JavaxBeanValidationXmlToJakartaBeanValidationXml
- com.oracle.weblogic.rewrite.jakarta.JavaxTestXmlsToJakartaTestsXmls

### Usage

This recipe will help you migrate `javax` packages to `jakarta` EE 9.1.0.

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
            <recipe>com.oracle.weblogic.rewrite.jakarta.JakartaEeNamespaces9_1</recipe>
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
    id("org.openrewrite.rewrite") version("7.1.4")
}

rewrite {
    activeRecipe("com.oracle.weblogic.rewrite.jakarta.JakartaEeNamespaces9_1")
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
  -Drewrite.recipeArtifactCoordinates=com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST,org.openrewrite.recipe:rewrite-migrate-java:RELEASE \
  -Drewrite.activeRecipes=com.oracle.weblogic.rewrite.jakarta.JakartaEeNamespaces9_1 \
  -Drewrite.exportDatatables=true
  ```
