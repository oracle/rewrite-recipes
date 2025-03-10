# Upgrade to WebLogic Server 15.1.1 (BETA)

The following procedure will apply the changes required for migrating applications to WebLogic Server 15.1.1.0.0 and Jakarta EE 9.1.

## Prerequisites

The following is required to build and run this project:

- JDK (version 8 or later)
- Maven (version 3.2+) or Gradle (version 4.0+)
- Your code

## Get your code

Clone your project to your local machine.

> [!IMPORTANT]
> Note that the `run` command will make changes to your code. Always make sure you have a backup of your code. When using git, create a branch and use `git diff` to see the differences.

# Examples

The following example sections illustrate the methods for upgrading your applications to WebLogic 15.1.1 and Jakarta EE 9.1.

You can run OpenRewrite recipes on your code folder using the Maven or Gradle CLI, or include them as a build plug-in in your `pom.xml` file.

## Run using the Maven CLI

> [!NOTE]
> You will need to have [Maven](https://maven.apache.org/download.cgi) installed on your machine before you can run the following command.

```
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST \
  -Drewrite.activeRecipes=com.oracle.weblogic.rewrite.UpgradeTo1511
```

## Run using Maven with the `<plugin>` in the `pom.xml` file

1. Add the plug-in to your `pom.xml` file and activate the recipes. The following example illustrates the Upgrade to WebLogic 15.1.1 recipe.

```
<plugin>
    <groupId>org.openrewrite.maven</groupId>
    <artifactId>rewrite-maven-plugin</artifactId>
    <version>5.43.0</version>
    <configuration>
        <exportDatatables>true</exportDatatables>
        <activeRecipes>
            <recipe>com.oracle.weblogic.rewrite.UpgradeTo1511</recipe>
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

2. To run the recipe: `mvn rewrite:run`

> [!TIP]  
> If you just want to dry run the recipe without changing the code, use `mvn rewrite:DryRun`. For more details when using Maven, see [here](https://docs.openrewrite.org/reference/rewrite-maven-plugin).

### Run using Gradle

1. Add the following to your `build.gradle` file:

```
plugins {
    id("org.openrewrite.rewrite") version("7.1.4")
}

rewrite {
    activeRecipe("com.oracle.weblogic.rewrite.UpgradeTo1511")
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
