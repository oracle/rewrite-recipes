# Upgrade to WebLogic Server 14.1.2

The following procedures will upgrade your applications to WebLogic 14.1.2 and Java 21, and comment out deprecated and removed APIs.

## Prerequisites

The following is required to build and run this project:

- JDK (version 8 or later)
- Maven (version 3.2+) or Gradle (version 4.0+)
- Your code

## Get Your Code

Clone your project to your local machine.

**Important**

Note that the `run` command will make changes to your code. Always make sure you have a backup of your code. When using git, create a branch and use `git diff` to see the differences.

# Examples

The following example sections illustrate the methods for upgrading your applications to WebLogic 14.1.2 and Java 21.

You can run OpenRewrite recipes on your code folder using the Maven or Gradle CLI, or include them as a build plug-in in your `pom.xml` file

## Run Using the Maven CLI

**NOTE**: You will need to have [Maven](https://maven.apache.org/download.cgi) installed on your machine before you can run the following command.

```
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST \
  -Drewrite.activeRecipes=com.oracle.weblogic.rewrite.UpgradeTo1412,org.openrewrite.java.migrate.UpgradeToJava21
```

## Run Using Maven and Adding `<plugin>` in the `pom.xml` File

1. Add the plug-in to your `pom.xml` file and activate the recipes. The following example illustrates the Upgrade to WebLogic 14.1.2 and Migrate to Java 21 recipes.

```
    <plugin>
        <groupId>org.openrewrite.maven</groupId>
        <artifactId>rewrite-maven-plugin</artifactId>
        <version>5.43.0</version>
        <configuration>
            <exportDatatables>true</exportDatatables>
            <activeRecipes>
                <recipe>com.oracle.weblogic.rewrite.UpgradeTo1412</recipe>
                <recipe>org.openrewrite.java.migrate.UpgradeToJava21</recipe>
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
```

2. To run the recipe: `mvn rewrite:run`

### Run Using Gradle

1. Add the following to your `build.gradle` file:

```
plugins {
    id("org.openrewrite.rewrite") version("7.1.4")
}

rewrite {
    activeRecipe("com.oracle.weblogic.rewrite.UpgradeTo1412")
    activeRecipe("org.openrewrite.java.migrate.UpgradeToJava21")
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
