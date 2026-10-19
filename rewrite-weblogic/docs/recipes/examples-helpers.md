# Examples Helpers
**com.oracle.weblogic.rewrite.examples.spring.MigratedPetClinicExtrasFor1511**

> [!IMPORTANT]
> These examples use the development artifact `0.7.0-SNAPSHOT`. First [build and install the recipes and check the aligned versions](../../README.md#install-the-development-recipes). Apply the plugin configuration to your application, not to the recipe repository.

This recipe adds migration extras for migrating the Spring Framework PetClinic example to run on WebLogic 15.1.1.

### Tags:
- springframework
- examples
- petclinic
- weblogic

### Recipe source

[examples-helpers.yaml](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/examples-helpers.yaml)

### Recipe list:
- com.oracle.weblogic.rewrite.examples.spring.ChangeCacheManagerToSimpleCacheManager
- com.oracle.weblogic.rewrite.examples.AddImplicitTldFileWithTaglib3_0

### Usage

This recipe will help you run the migrated Spring Framework PetClinic example on WebLogic 15.1.1.

#### Maven POM

1. Add the following to your Maven POM file.
```
<plugin>
    <groupId>org.openrewrite.maven</groupId>
    <artifactId>rewrite-maven-plugin</artifactId>
    <version>6.27.0</version>
    <configuration>
        <exportDatatables>true</exportDatatables>
        <activeRecipes>
            <recipe>com.oracle.weblogic.rewrite.examples.spring.MigratedPetClinicExtrasFor1511</recipe>
        </activeRecipes>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>org.openrewrite.recipe</groupId>
            <artifactId>rewrite-migrate-java</artifactId>
            <version>3.25.0</version>
        </dependency>
        <dependency>
            <groupId>com.oracle.weblogic.rewrite</groupId>
            <artifactId>rewrite-weblogic</artifactId>
            <version>0.7.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
</plugin>
...
```
2. Run `mvn rewrite:run` to run the recipe.

> [!TIP]  
> If you just want to dry run the recipe without changing the code, use `mvn rewrite:dryRun`. For more details when using Maven, see [here](https://docs.openrewrite.org/reference/rewrite-maven-plugin).

#### Gradle

1. Add the following to your `build.gradle` file:

```
plugins {
    id("org.openrewrite.rewrite") version("7.19.0")
}

rewrite {
    setRewriteVersion("8.71.0")
    activeRecipe("com.oracle.weblogic.rewrite.examples.spring.MigratedPetClinicExtrasFor1511")
    setExportDatatables(true)
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    rewrite("org.openrewrite.recipe:rewrite-migrate-java:3.25.0")
    rewrite("com.oracle.weblogic.rewrite:rewrite-weblogic:0.7.0-SNAPSHOT")
}
```
2. Run `gradle rewriteRun` to run the recipe.

> [!TIP]  
> If you just want to dry run the recipe without changing the code, use `gradle rewriteDryRun`. For more details about the "dryRun" task, see [here](https://docs.openrewrite.org/reference/gradle-plugin-configuration#the-dryrun-task).

#### Maven command line

> [!NOTE]
> You will need to have [Maven](https://maven.apache.org/download.cgi) installed on your machine before you can run the following command.

```
mvn -U org.openrewrite.maven:rewrite-maven-plugin:6.27.0:run \
  -Drewrite.recipeArtifactCoordinates=com.oracle.weblogic.rewrite:rewrite-weblogic:0.7.0-SNAPSHOT,org.openrewrite.recipe:rewrite-hibernate:2.17.0 \
  -Drewrite.activeRecipes=com.oracle.weblogic.rewrite.examples.spring.MigratedPetClinicExtrasFor1511 \
  -Drewrite.exportDatatables=true
  ```
