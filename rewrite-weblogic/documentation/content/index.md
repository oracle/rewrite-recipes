# Welcome to Rewrite WebLogic

Rewrite WebLogic employs [OpenRewrite](https://github.com/openrewrite/rewrite) recipes for upgrading applications to new WebLogic and Java versions. Here, you'll find recipes that perform all the functions you need to successfully upgrade your applications. Use [Rewrite WebLogic](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/README.md) recipes to migrate your applications to WebLogic 14.1.2 and Java 17 or Java 21 or to WebLogic 15.1.1 Beta, Jakarta EE 9.1, and Java 17 or Java 21.

Rewrite WebLogic is part of a larger, open source project, in which there are lots of community recipes; recipes are customizable and extensible. Recipes from Oracle are supported by Oracle.


## Rewrite WebLogic Recipes

Look here for detailed information about each of the following Rewrite WebLogic composite recipes (recipes that consists of a list of other recipes).

| Composite Recipe | Description |
| --- | --- |
| [Migrate to WebLogic 15.1.1](./recipes/migrate-to-weblogic-151100.md) | This recipe will update the WebLogic version to 15.1.1 for the Maven build, migrate `javax` packages to `jakarta`, and comment out deprecated and removed APIs. |
| [Migrate to WebLogic 14.1.2](./recipes/migrate-to-weblogic-141200.md) | This recipe will upgrade the WebLogic version to 14.1.2 for the Maven build, and comment out deprecated and removed APIs. |
| [Migrate to WebLogic 14.1.1](./recipes/migrate-to-weblogic-141100.md) | This recipe will update the WebLogic version to 14.1.2 for the Maven build and will apply changes commonly needed when upgrading to Java 11. |
| [Migrate to Jakarta EE 9.1](./recipes/migrate-to-jakarta-EE-9_1.md) | This recipe will migrate `javax` packages to `jakarta` and update Jakarta EE platform dependencies to 9.1.0. |
| [Migrate From JSF 2.x to Jakarta Server Faces 3.x](./recipes/jakarta-server-faces-3x.md) | This recipe will find and replace legacy JSF namespaces and `javax` references with Jakarta Server Faces values. |
| [Identify and Comment Out Deprecations](./recipes/identify-deprecations.md) | This recipe will identify, and comment out deprecated and removed APIs. |


## Running Recipes

Depending on your application dependencies and WebLogic Server and JDK source versions, you can use one or more recipes to accomplish your goals. Recipes are stackable; you can run more than one recipe at a time, within a single command or Maven configuration; recipes are run in order.

You can call OpenRewrite on your code folder using the Maven or Gradle CLI or include it as a build plug-in in your `pom.xml` file. For more details on running the recipes with Maven and Gradle, see the [OpenRewrite Quickstart](https://docs.openrewrite.org/running-recipes/getting-started) documentation.

## Examples

The following example sections illustrate the methods for upgrading your applications to WebLogic 14.1.2 and Java 21.

### Prerequisites

The following is required to build and run this project:

- JDK (version 1.8 or later)
- Maven (version 3.2+) or Gradle (version 4.0+)
- Your code

### Get Your Code

Clone your project to your local machine.

**Important**

Note that the `run` command will make changes to your code. Always make sure you have a backup of your code. When using git, create a branch and use `git diff` to see the differences.

### Run Using the Maven CLI

To upgrade applications to WebLogic 14.1.2 and Java 21:
```
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST \
  -Drewrite.activeRecipes=com.oracle.weblogic.rewrite.UpgradeTo1412,org.openrewrite.java.migrate.UpgradeToJava21
```

### Run Using Maven and Adding `<plugin>` in the `pom.xml` File

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

**Tip**

If you just want to dry run the recipe without changing the code, use `mvn rewrite:DryRun`. For more details when using Maven, see [here](https://docs.openrewrite.org/reference/rewrite-maven-plugin).

**Tip**

The goals `rewrite:run` and `rewrite:dryRun` are configured to fork Maven's life cycle and are a better choice when running recipes using a standalone goal (`mvn rewrite:run`) because this will trigger all the necessary life-cycle goals before running the rewrite plug-in. However, when using rewrite within the context of an integration build (`mvn deploy rewrite:run`), it may be more efficient to use the non-forking variants, as these will not cause duplicate life cycle phases to be called.

    If you are using git for your repository, you can see the differences by using `git diff` or other diff viewers. This option will show you the differences between your original and migrated code.

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


## More Examples and Tutorials

Use the WebLogic Rewrite recipes in the examples and tutorials [repository](https://github.com/oracle-samples/weblogic-examples) to migrate applications to updated versions of WebLogic, Java, and to Jakarta EE 9.
