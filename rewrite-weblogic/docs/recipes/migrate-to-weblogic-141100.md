# Migrate to WebLogic 14.1.1
**com.oracle.weblogic.rewrite.UpgradeTo1411**

These recipes apply the changes required for upgrading to WebLogic Server 14.1.1.0.0 and for upgrading from Java 8 to Java 11.

### Tags:
  - weblogic
  - java

### Recipe source

[weblogic-14.1.1.yaml](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/weblogic-14.1.1.yaml)

### Recipe list:
- org.openrewrite.java.migrate.Java8toJava11
- org.openrewrite.maven.ChangePropertyValue:
    key: weblogic.version
    newValue: 14.1.1-0-0
- org.openrewrite.maven.UpgradeParentVersion:
    groupId: com.oracle.weblogic.archetype
    artifactId: wls-common
    newVersion: 14.1.1-0-0
- org.openrewrite.maven.UpgradePluginVersion:
    groupId: com.oracle.weblogic
    artifactId: org.glassfish.javax.json
    newVersion: 14.1.1-0-0
    trustParent: "True"
- org.openrewrite.maven.UpgradePluginVersion:
    groupId: com.oracle.weblogic
    artifactId: javax.javaee-api
    newVersion: 14.1.1-0-0
    trustParent: "True"
- org.openrewrite.maven.UpgradePluginVersion:
    groupId: com.oracle.weblogic
    artifactId: org.codehaus.jettison.jettison
    newVersion: 14.1.1-0-0
    trustParent: "True"
- org.openrewrite.maven.UpgradePluginVersion:
    groupId: com.oracle.weblogic
    artifactId: javax.javaee-api
    newVersion: 14.1.1-0-0
    trustParent: "True"

### Usage

These recipes will update the WebLogic version to 14.1.1 for the Maven build and will apply changes commonly needed when upgrading to Java 11. Specifically, for those applications that are built on Java 8, the recipe will update and add dependencies on Java EE libraries that are no longer directly bundled with the JDK and will also replace deprecated API with equivalents when there is a clear migration strategy.

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
            <recipe>org.openrewrite.java.migrate.Java8toJava11</recipe>
            <recipe>com.oracle.weblogic.rewrite.UpgradeTo1411</recipe>
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
    activeRecipe("org.openrewrite.java.migrate.Java8toJava11")
    activeRecipe("com.oracle.weblogic.rewrite.UpgradeTo1411")
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
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:RELEASE,com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.Java8toJava11,com.oracle.weblogic.rewrite.UpgradeTo1411 \
  -Drewrite.exportDatatables=true
  ```
