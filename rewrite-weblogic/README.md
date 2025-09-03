# Rewrite WebLogic

[![Maven Central Version](https://img.shields.io/maven-central/v/com.oracle.weblogic.rewrite/rewrite-weblogic)](https://search.maven.org/artifact/com.oracle.weblogic.rewrite/rewrite-weblogic)
![Dev Version](https://img.shields.io/badge/dynamic/xml?url=https%3A%2F%2Fraw.githubusercontent.com%2Foracle%2Frewrite-recipes%2Frefs%2Fheads%2Fmain%2Frewrite-weblogic%2Fpom.xml&query=%2F*%5Blocal-name()%3D'project'%5D%2F*%5Blocal-name()%3D'version'%5D&prefix=v&label=Dev%20Version)
[![License](https://img.shields.io/badge/License-UPL%201.0-blue.svg)](https://oss.oracle.com/licenses/upl/)
[![GitHub issues](https://img.shields.io/github/issues-raw/oracle/rewrite-recipes)](https://github.com/oracle/rewrite-recipes/issues)
[![Contributing Guide](https://img.shields.io/badge/Contributing-Guide-informational)](https://github.com/oracle/rewrite-recipes/blob/main/CONTRIBUTING.md)
[![Code of Conduct](https://img.shields.io/badge/Code%20of%20Conduct-Oracle%20Community%20Code%20of%20Conduct-informational)](https://www.oracle.com/corporate/community/code-of-conduct.html)

Migrate your WebLogic applications to the latest version of WebLogic and Java with OpenRewrite. Automatically.

## What is this?

This project implements a [Rewrite module](https://github.com/openrewrite/rewrite) that performs common tasks when
migrating your [WebLogic](https://www.oracle.com/java/weblogic/) applications to a new version of WebLogic Server and Java.

WebLogic Server application upgrade tooling makes upgrading your WebLogic applications easy. To learn all about it, see the [**Documentation**](./docs/README.md).

To jump start your hands-on learning, see these **Tutorials**:

- [Migrate WebLogic Cafe to WLS 14.1.2](https://github.com/oracle-samples/weblogic-examples/blob/main/tutorials/migrate/weblogic-cafe-14.1.2/README.md)
- [Migrate WebLogic Cafe to WLS 15.1.1 (BETA)](https://github.com/oracle-samples/weblogic-examples/blob/main/tutorials/migrate/weblogic-cafe-15.1.1/README.md)
- [Migrate Spring Framework PetClinic to WLS 15.1.1 (BETA)](https://github.com/oracle-samples/weblogic-examples/blob/main/tutorials/migrate/spring-framework-petclinic-15.1.1/README.md)

## Recipes for common use cases

Use these `rewrite-weblogic` recipes to migrate WebLogic Server applications to newer versions of WebLogic Server, Java, Jakarta EE, and related versions of Jakarta Server Faces and Spring Framework.

> [!NOTE]
> These recipes do not address _any_ other third-party dependencies. For all other third-party dependencies, you _must_ find or create an additional [recipe](https://docs.openrewrite.org/recipes) to do the migration, or make manual code changes after the migration.

| Use Cases | Recipes |
| --- | ------ |
| **BETA:** Migrate to WebLogic 15.1.1 BETA, Jakarta EE 9.1, and Java 21 | [com.oracle.weblogic.rewrite.UpgradeTo1511](./src/main/resources/META-INF/rewrite/weblogic-15.1.1.yaml) <br/> [org.openrewrite.java.migrate.UpgradeToJava21](https://docs.openrewrite.org/recipes/java/migrate/upgradetojava21) </br> [com.oracle.weblogic.rewrite.JakartaEE9_1](./src/main/resources/META-INF/rewrite/jakarta-ee-9.1.yaml) |
| **BETA:** Migrate to WebLogic 15.1.1 BETA, Jakarta EE 9.1, and Java 17 | [com.oracle.weblogic.rewrite.UpgradeTo1511](./src/main/resources/META-INF/rewrite/weblogic-15.1.1.yaml) <br/> [org.openrewrite.java.migrate.UpgradeToJava17](https://docs.openrewrite.org/recipes/java/migrate/upgradetojava21) </br> [com.oracle.weblogic.rewrite.JakartaEE9_1](./src/main/resources/META-INF/rewrite/jakarta-ee-9.1.yaml) |
| **BETA:** Migrate to WebLogic 15.1.1 BETA, Jakarta EE 9.1, Java 21, and Spring Framework 6.2.x and Hibernate | [com.oracle.weblogic.rewrite.UpgradeTo1511](./src/main/resources/META-INF/rewrite/weblogic-15.1.1.yaml) <br/> [org.openrewrite.java.migrate.UpgradeToJava21](https://docs.openrewrite.org/recipes/java/migrate/upgradetojava21) </br> [com.oracle.weblogic.rewrite.JakartaEE9_1](./src/main/resources/META-INF/rewrite/jakarta-ee-9.1.yaml) <br/> [com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/spring-framework-6.2.yaml) <br/> [com.oracle.weblogic.rewrite.hibernate.MigrateHibernateToJakartaEE9](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/hibernate.yaml) |
| **BETA:** Migrate to WebLogic 15.1.1 BETA, Jakarta EE 9.1, Java 17, and Spring Framework 6.2.x and Hibernate | [com.oracle.weblogic.rewrite.UpgradeTo1511](./src/main/resources/META-INF/rewrite/weblogic-15.1.1.yaml) <br/> [org.openrewrite.java.migrate.UpgradeToJava17](https://docs.openrewrite.org/recipes/java/migrate/upgradetojava21) </br> [com.oracle.weblogic.rewrite.JakartaEE9_1](./src/main/resources/META-INF/rewrite/jakarta-ee-9.1.yaml) <br/> [com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/spring-framework-6.2.yaml) <br/> [com.oracle.weblogic.rewrite.hibernate.MigrateHibernateToJakartaEE9](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/hibernate.yaml) |
| Migrate to WebLogic 14.1.2 and Java 21 | [com.oracle.weblogic.rewrite.UpgradeTo1412](./src/main/resources/META-INF/rewrite/weblogic-14.1.2.yaml) <br/>  [org.openrewrite.java.migrate.UpgradeToJava21](https://docs.openrewrite.org/recipes/java/migrate/upgradetojava21) |
| Migrate to WebLogic 14.1.2 and Java 17 | [com.oracle.weblogic.rewrite.UpgradeTo1412](./src/main/resources/META-INF/rewrite/weblogic-14.1.2.yaml) <br/> [org.openrewrite.java.migrate.UpgradeToJava17](https://docs.openrewrite.org/recipes/java/migrate/upgradetojava17) |

<!-- | Migrate to WebLogic 14.1.1 and Java 11 | [com.oracle.weblogic.rewrite.UpgradeTo1411](resources/META-INF/rewrite/weblogic-14.1.1.yaml) <br/> [org.openrewrite.java.migrate.Java8toJava11](https://docs.openrewrite.org/recipes/java/migrate/java8tojava11) <br/> [org.openrewrite.maven.ChangePropertyValue](https://docs.openrewrite.org/recipes/maven/changepropertyvalue) <br/> [org.openrewrite.maven.UpgradeParentVersion](https://docs.openrewrite.org/recipes/maven/upgradeparentversion) | - Applies changes required for migrating apps to WebLogic 14.1.1 <br/> - Upgrade Java version to 11 <br/> - Change property to weblogic 14.1.1 <br/> - Upgrade WebLogic Version on parent| -->

See these recipes for [WebLogic 15.1.1 BETA](#weblogic-server-1511-beta-recipes). For more recipes, see [Rewrite WebLogic recipes](./docs/recipes/README.md).

## How do I use it?

You can call OpenRewrite on your code folder using the Maven or Gradle CLI, or include it as a build plug-in in your `pom.xml` or `build.gradle` file.

For more details on running the recipes with Maven and Gradle, see the [OpenRewrite Quickstart](https://docs.openrewrite.org/running-recipes/getting-started) documentation.

### Prerequisites

The following tools are required to build and run this project:

- Java 17 or later
- Maven (minimum version 3.6, with recommended version, 3.9.9) or Gradle (version 4.0+)
- Your code

### Get your code

Clone your project to your local machine.

> [!IMPORTANT]  
> Note that the `run` command will make changes to your code. Always make sure you have a backup of your code. When using `git`, create a branch and use `git diff` to see the differences.

### Run using the Maven CLI

> [!TIP]  
> If you're using WebLogic artifacts, you need to install the corresponding [WebLogic Maven plug-in](https://docs.oracle.com/en/middleware/fusion-middleware/weblogic-server/14.1.2/wlprg/maven.html#GUID-7759C76C-D6E9-4A7E-BE99-96787814576D) version (for example, 14.1.1). After migration (for example, to 15.1.1), update and sync the plug-in version accordingly, before building the migrated application.

#### Upgrade applications to WebLogic 14.1.2 and Java 21

> [!NOTE]
> The `UpgradeTo1412` recipe must be run _after_ the Java recipe.

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:RELEASE,com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava21,com.oracle.weblogic.rewrite.UpgradeTo1412
```

#### Upgrade applications to WebLogic 14.1.2 and Java 17

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:RELEASE,com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava17,com.oracle.weblogic.rewrite.UpgradeTo1412
```

### Run using Maven with the `<plugin>` in the `pom.xml` file

1. Add the plug-in to your `pom.xml` file and activate the desired recipe from the `rewrite-weblogic` group recipe. The following example illustrates Upgrade to WebLogic 14.1.2 and Upgrade to Java 21.

    ```xml
        <plugin>
            <groupId>org.openrewrite.maven</groupId>
            <artifactId>rewrite-maven-plugin</artifactId>
            <version>6.3.1</version>
            <configuration>
                <exportDatatables>true</exportDatatables>
                <activeRecipes>
                    <recipe>org.openrewrite.java.migrate.UpgradeToJava21</recipe>
                    <recipe>com.oracle.weblogic.rewrite.UpgradeTo1412</recipe>
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
    ```

2. Run the recipe with
    `mvn rewrite:run`.

> [!TIP]  
> If you just want to dry run the recipe without changing the code, use `mvn rewrite:DryRun`. For more details when using Maven, see [here](https://docs.openrewrite.org/reference/rewrite-maven-plugin).

- If you are using `git` for your repository, you can see the differences by using `git diff` or other diff viewers. This option will show you the differences between your original and migrated code.

### Run using Gradle

The following example illustrates using Gradle to run the Upgrade to WebLogic 14.1.2 and Upgrade to Java 21 recipes.

1. Add the following to your `build.gradle` file:

```
plugins {
    id("org.openrewrite.rewrite") version("7.2.0")
}

rewrite {
    activeRecipe("org.openrewrite.java.migrate.UpgradeToJava21")
    activeRecipe("com.oracle.weblogic.rewrite.UpgradeTo1412")
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

> [!NOTE]
> You can run `rewrite` on a Gradle project without modifying the build, similar to using the `mvn` CLI command. For more information, see https://docs.openrewrite.org/running-recipes/running-rewrite-on-a-gradle-project-without-modifying-the-build.

## WebLogic Server 15.1.1 BETA recipes

> [!WARNING]  
> Before deploying a migrated application, make sure that you are using an approved WebLogic Server 15.1.1 BETA installation.

> [!NOTE]
> When upgrading to WebLogic Server 15.1.1 BETA, the `UpgradeTo1511` recipe must be run _after_ the Java and Jakarta recipes.

#### Upgrade applications to WebLogic Server 15.1.1 BETA, Jakarta EE 9.1, and Java 21

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:RELEASE,com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava21,com.oracle.weblogic.rewrite.JakartaEE9_1,com.oracle.weblogic.rewrite.UpgradeTo1511 \
  -Drewrite.exportDatatables=true
```

#### Upgrade applications to WebLogic Server 15.1.1 BETA, Jakarta EE 9.1, and Java 17

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:RELEASE,com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava17,com.oracle.weblogic.rewrite.JakartaEE9_1,com.oracle.weblogic.rewrite.UpgradeTo1511 \
  -Drewrite.exportDatatables=true
```


#### **Spring Framework upgrade:** Upgrade applications to WebLogic Server 15.1.1 BETA, Jakarta EE 9.1, Java 21, and Spring Framework 6.2.x

If your application is running with Spring Framework 5.x on WebLogic 12.x or 14.x, and you want to migrate to Spring Framework 6.2.x, add the Spring Framework recipe to the command:

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
      -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:RELEASE,com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST,org.openrewrite.recipe:rewrite-spring:RELEASE \
      -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava21,com.oracle.weblogic.rewrite.JakartaEE9_1,com.oracle.weblogic.rewrite.UpgradeTo1511,com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2 \
      -Drewrite.exportDatatables=true
```

> [!TIP]  
> When upgrading your application to Spring Framework 6.2.x, you may have other libraries that need to be updated. You can include the recipe in the same command, like `org.hibernate`, or run other third-party libraries separately. For other examples, see the [tutorials](https://github.com/oracle-samples/weblogic-examples).

#### **Spring Framework upgrade:** Upgrade applications to WebLogic Server 15.1.1 BETA, Jakarta EE 9.1, Java 17, and Spring Framework 6.2.x

If your application is running with Spring Framework 5.x on WebLogic 12.x or 14.x, and you want to migrate to Spring Framework 6.2.x, add the Spring Framework recipe to the command:

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
      -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:RELEASE,com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST,org.openrewrite.recipe:rewrite-spring:RELEASE \
      -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava17,com.oracle.weblogic.rewrite.JakartaEE9_1,com.oracle.weblogic.rewrite.UpgradeTo1511,com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2 \
      -Drewrite.exportDatatables=true
```

> [!TIP]  
> When upgrading your application to Spring Framework 6.2.x, you may have other libraries that need to be updated. You can include the recipe in the same command, like `org.hibernate`, or run other third-party libraries separately. For other examples, see the [tutorials](https://github.com/oracle-samples/weblogic-examples).


#### Spring Framework and Hibernate upgrade

This example includes the Hibernate recipe running with the WebLogic 15.1.1 BETA, Java 21, and Spring Framework 6.2 recipes.

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:RELEASE,com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST,org.openrewrite.recipe:rewrite-spring:RELEASE,org.openrewrite.recipe:rewrite-hibernate:RELEASE \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava21,com.oracle.weblogic.rewrite.JakartaEE9_1,com.oracle.weblogic.rewrite.UpgradeTo1511,com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2,com.oracle.weblogic.rewrite.hibernate.MigrateHibernateToJakartaEE9 \
  -Drewrite.exportDatatables=true
```

This example includes the Hibernate recipe running with the WebLogic 15.1.1 BETA, Java 17, and Spring Framework 6.2 recipes.

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:RELEASE,com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST,org.openrewrite.recipe:rewrite-spring:RELEASE,org.openrewrite.recipe:rewrite-hibernate:RELEASE \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava17,com.oracle.weblogic.rewrite.JakartaEE9_1,com.oracle.weblogic.rewrite.UpgradeTo1511,com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2,com.oracle.weblogic.rewrite.hibernate.MigrateHibernateToJakartaEE9 \
  -Drewrite.exportDatatables=true
```


## Developing, contributing, and testing this recipe locally
![Dev Version](https://img.shields.io/badge/dynamic/xml?url=https%3A%2F%2Fraw.githubusercontent.com%2Foracle%2Frewrite-recipes%2Frefs%2Fheads%2Fmain%2Frewrite-weblogic%2Fpom.xml&query=%2F*%5Blocal-name()%3D'project'%5D%2F*%5Blocal-name()%3D'version'%5D&prefix=v&label=Dev%20Version)
[![GitHub issues](https://img.shields.io/github/issues-raw/oracle/rewrite-recipes)](https://github.com/oracle/rewrite-recipes/issues)
[![Contributing Guide](https://img.shields.io/badge/Contributing-Guide-informational)](https://github.com/oracle/rewrite-recipes/blob/main/CONTRIBUTING.md)
[![Code of Conduct](https://img.shields.io/badge/Code%20of%20Conduct-Oracle%20Community%20Code%20of%20Conduct-informational)](https://www.oracle.com/corporate/community/code-of-conduct.html)

### Prerequisites

The following tools are required to build and run this project:

- JDK (version 1.8 or later)
- Maven (version 3.2+) or Gradle (version 4.0+ )
- Your code

#### Dry run

```shell
mvn org.openrewrite.maven:rewrite-maven-plugin:dryRun
```

#### Run (apply changes)

```shell
mvn org.openrewrite.maven:rewrite-maven-plugin:run
```

#### Testing

```shell
mvn test
```

## License

Copyright (c) 2025 Oracle and/or its affiliates.

Released under the Universal Permissive License v1.0 as shown at
<https://oss.oracle.com/licenses/upl/>.
