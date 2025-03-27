# Upgrade to WebLogic Server 15.1.1 (BETA), Spring Framework 6.2.x, and Hibernate

The following procedure will apply the changes required for migrating applications to WebLogic Server 15.1.1.0.0, Jakarta EE 9.1, Spring Framework 6.2.x, and Hibernate.

## Prerequisites

The following is required to build and run this project:

- JDK - For migration, version 8 or later; post-migration, you need the target JDK installed.
- Maven (version 3.2+) or Gradle (version 4.0+)
- Your code

## Get your code

Clone your project to your local machine.

> [!IMPORTANT]
> Note that the `run` command will make changes to your code. Always make sure you have a backup of your code. When using git, create a branch and use `git diff` to see the differences.

# Examples

The following example sections illustrate the methods for upgrading your applications to WebLogic 15.1.1, Jakarta EE 9.1, Spring Framework 6.2.x, and Hibernate.

You can run OpenRewrite recipes on your code folder using the Maven or Gradle CLI, or include them as a build plug-in in your `pom.xml` file.

## Run using the Maven CLI

> [!NOTE]
> You will need to have [Maven](https://maven.apache.org/download.cgi) installed on your machine before you can run the following command.

### Sync Maven dependencies

For OpenRewrite to run, Maven dependencies must be resolved. If needed, run `mvn clean install` for missing dependencies.

1. Open a terminal at the root of the project.

1. Run the following command for missing dependencies:

   ```shell
   mvn clean install
   ```

   Or, you can use other commands as well, such as `mvn dependency:resolve`.

1. Run the following Maven command to run OpenRewrite:

   ```shell
   mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
     -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:RELEASE,com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST,org.openrewrite.recipe:rewrite-spring:RELEASE,org.openrewrite.recipe:rewrite-hibernate:RELEASE \
     -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava21,com.oracle.weblogic.rewrite.JakartaEE9_1,com.oracle.weblogic.rewrite.UpgradeTo1511,com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2,com.oracle.weblogic.rewrite.hibernate.MigrateHibernateToJakartaEE9 \
    -Drewrite.exportDatatables=true
   ```

> [!NOTE]
> When upgrading to WebLogic Server 15.1.1 BETA, the `UpgradeTo1511` recipe must be run _after_ the Java and Jakarta recipes. </br>
> Note that this command updates the application to use Java 21. If you want to upgrade to Java 17 instead, replace UpgradeToJava21 with UpgradeToJava17.

## Run using Maven with the `<plugin>` in the `pom.xml` file

1. Add the plug-in to your `pom.xml` file and activate the recipes. The following example illustrates the recipes.

```
<plugin>
    <groupId>org.openrewrite.maven</groupId>
    <artifactId>rewrite-maven-plugin</artifactId>
    <version>6.31</version>
    <configuration>
        <exportDatatables>true</exportDatatables>
        <activeRecipes>
            <recipe>org.openrewrite.java.migrate.UpgradeToJava21</recipe>
            <recipe>com.oracle.weblogic.rewrite.JakartaEE9_1</recipe>
            <recipe>com.oracle.weblogic.rewrite.UpgradeTo1511</recipe>
            <recipe>com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2</recipe>
            <recipe>com.oracle.weblogic.rewrite.hibernate.MigrateHibernateToJakartaEE9</recipe>
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
            <version>[0.6.0,)</version>
        </dependency>
        <dependency>
            <groupId>org.openrewrite.recipe</groupId>
            <artifactId>rewrite-spring</artifactId>
            <version>6.3.0</version>
        </dependency>
        <dependency>
            <groupId>org.openrewrite.recipe</groupId>
            <artifactId>rewrite-hibernate</artifactId>
            <version>2.4.0</version>
        </dependency>
    </dependencies>
</plugin>
```

2. To run the recipe: `mvn rewrite:run`

> [!TIP]  
> If you just want to dry run the recipe without changing the code, use `mvn rewrite:DryRun`. For more details when using Maven, see [here](https://docs.openrewrite.org/reference/rewrite-maven-plugin).

### Run using Gradle

1. Add the following to your `build.gradle` file:

```
plugins {
    id("org.openrewrite.rewrite") version("7.2.0")
}

rewrite {
    activeRecipe("org.openrewrite.java.migrate.UpgradeToJava21")
    activeRecipe("com.oracle.weblogic.rewrite.JakartaEE9_1")
    activeRecipe("com.oracle.weblogic.rewrite.UpgradeTo1511")
    activeRecipe("com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2")
    activeRecipe("com.oracle.weblogic.rewrite.hibernate.MigrateHibernateToJakartaEE9")
    setExportDatatables(true)
}

repositories {
    mavenCentral()
}

dependencies {
    rewrite("org.openrewrite.recipe:rewrite-migrate-java:3.4.0")
    rewrite("com.oracle.weblogic.rewrite:rewrite-weblogic:+")
    rewrite("org.openrewrite.recipe:rewrite-spring:6.3.0")
    rewrite("org.openrewrite.recipe:rewrite-hibernate:2.4.0")
}
```
2. Run `gradle rewriteRun` to run the recipe.

> [!TIP]  
> If you just want to dry run the recipe without changing the code, use `gradle rewriteDryRun`. For more details about the "dryRun" task, see [here](https://docs.openrewrite.org/reference/gradle-plugin-configuration#the-dryrun-task).

> [!NOTE]
> You can run Rewrite on a Gradle project without modifying the build, similar to using the `mvn` CLI command. For more information, see https://docs.openrewrite.org/running-recipes/running-rewrite-on-a-gradle-project-without-modifying-the-build.
