# Migrate to Spring Framework 6.2 for WebLogic 15.1.1
**com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2**

This recipe migrates applications to the Spring Framework 6.2 release and to compatibility with WebLogic 15.1.1.

### Tags:
  - weblogic
  - springframework

### Recipe source

[spring-framework-6.2.yaml](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/spring-framework-6.2.yaml)

### Recipe list:
- org.openrewrite.java.spring.framework.UpgradeSpringFramework_6_1
- org.openrewrite.java.dependencies.UpgradeDependencyVersion:
    groupId: org.springframework
    artifactId: '*'
    newVersion: 6.2.x
- com.oracle.weblogic.rewrite.spring.framework.DefaultServletHandler
- com.oracle.weblogic.rewrite.spring.framework.ReplaceWebLogicJtaTransactionManager
- com.oracle.weblogic.rewrite.spring.framework.ReplaceWebLogicLoadTimeWeaver
- org.openrewrite.java.spring.data.UpgradeSpringData_2_7
- com.oracle.weblogic.rewrite.spring.data.UpgradeSpringDataBom

### Usage

This recipe will migrate applications to the Spring Framework 6.2 release and to compatibility with WebLogic 15.1.1.

**NOTE**: This recipe is intended for migration from Spring Framework version 5.3 or later. We have not tested and we don’t support updating from earlier versions of Spring Framework.

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
            <recipe>com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2</recipe>
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
    activeRecipe("com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2")
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
  -Drewrite.recipeArtifactCoordinates=com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST,org.openrewrite.recipe:rewrite-spring:RELEASE \
  -Drewrite.activeRecipes=com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2 \
  -Drewrite.exportDatatables=true
  ```
