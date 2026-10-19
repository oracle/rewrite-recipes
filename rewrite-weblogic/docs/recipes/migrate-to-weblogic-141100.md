# Migrate to WebLogic 14.1.1
**com.oracle.weblogic.rewrite.UpgradeTo1411**

> [!IMPORTANT]
> These examples use the development artifact `0.7.0-SNAPSHOT`. First [build and install the recipes and check the aligned versions](../../README.md#install-the-development-recipes). Apply the plugin configuration to your application, not to the recipe repository.

These recipes apply the changes required for upgrading to WebLogic Server 14.1.1.0.0 and for upgrading from Java 8 to Java 11.

### Tags:
  - weblogic
  - java

### Recipe source

[weblogic-14.1.1.yaml](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/weblogic-14.1.1.yaml)

### Recipe list:
- org.openrewrite.java.migrate.Java8toJava11
- org.openrewrite.maven.ChangePropertyValue: </br>
    key: weblogic.version </br>
    newValue: 14.1.1-0-0 </br>
- org.openrewrite.maven.UpgradeParentVersion: </br>
    groupId: com.oracle.weblogic.archetype </br>
    artifactId: wls-common </br>
    newVersion: 14.1.1-0-0 </br>
- org.openrewrite.maven.UpgradePluginVersion: </br>
    groupId: com.oracle.weblogic </br>
    artifactId: org.glassfish.javax.json </br>
    newVersion: 14.1.1-0-0 </br>
    trustParent: "True" </br>
- org.openrewrite.maven.UpgradePluginVersion: </br>
    groupId: com.oracle.weblogic </br>
    artifactId: javax.javaee-api </br>
    newVersion: 14.1.1-0-0 </br>
    trustParent: "True" </br>
- org.openrewrite.maven.UpgradePluginVersion: </br>
    groupId: com.oracle.weblogic </br>
    artifactId: org.codehaus.jettison.jettison </br>
    newVersion: 14.1.1-0-0 </br>
    trustParent: "True" </br>
- org.openrewrite.maven.UpgradePluginVersion: </br>
    groupId: com.oracle.weblogic </br>
    artifactId: javax.javaee-api </br>
    newVersion: 14.1.1-0-0 </br>
    trustParent: "True" </br>

### Usage

These recipes will update the WebLogic version to 14.1.1 for the Maven build and will apply changes commonly needed when upgrading to Java 11. Specifically, for those applications that are built on Java 8, the recipe will update and add dependencies on Java EE libraries that are no longer directly bundled with the JDK and will also replace deprecated API with equivalents when there is a clear migration strategy.

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
            <recipe>org.openrewrite.java.migrate.Java8toJava11</recipe>
            <recipe>com.oracle.weblogic.rewrite.UpgradeTo1411</recipe>
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
    activeRecipe("org.openrewrite.java.migrate.Java8toJava11")
    activeRecipe("com.oracle.weblogic.rewrite.UpgradeTo1411")
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
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:3.25.0,com.oracle.weblogic.rewrite:rewrite-weblogic:0.7.0-SNAPSHOT \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.Java8toJava11,com.oracle.weblogic.rewrite.UpgradeTo1411 \
  -Drewrite.exportDatatables=true
  ```
