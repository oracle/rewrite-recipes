# Rewrite WebLogic

[![Maven Central Version](https://img.shields.io/maven-central/v/com.oracle.weblogic.rewrite/rewrite-weblogic)](https://search.maven.org/artifact/com.oracle.weblogic.rewrite/rewrite-weblogic)
[Development version: 0.7.0-SNAPSHOT](pom.xml)
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
- [Migrate WebLogic Cafe to WLS 15.1.1](https://github.com/oracle-samples/weblogic-examples/blob/main/tutorials/migrate/weblogic-cafe-15.1.1/README.md)
- [Migrate Spring Framework PetClinic to WLS 15.1.1](https://github.com/oracle-samples/weblogic-examples/blob/main/tutorials/migrate/spring-framework-petclinic-15.1.1/README.md)

## Recipes for common use cases

Use these `rewrite-weblogic` recipes to migrate WebLogic Server applications to newer versions of WebLogic Server, Java, Jakarta EE, and related versions of Jakarta Server Faces and Spring Framework.

> [!NOTE]
> These recipes do not address _any_ other third-party dependencies. For all other third-party dependencies, you _must_ find or create an additional [recipe](https://docs.openrewrite.org/recipes) to do the migration, or make manual code changes after the migration.

| Use Cases | Recipes |
| --- | ------ |
| Migrate to WebLogic 26.1.0, Jakarta EE 11, and Java 25 | [com.oracle.weblogic.rewrite.UpgradeBuildToJava25ForWebLogic2610](./src/main/resources/META-INF/rewrite/weblogic-java-21-25.yaml) <br/> [com.oracle.weblogic.rewrite.jakarta.MigrateToJakartaEE11](./src/main/resources/META-INF/rewrite/jakarta-ee-11.yaml) <br/> [com.oracle.weblogic.rewrite.UpgradeTo2610](./src/main/resources/META-INF/rewrite/weblogic-26.1.0.yaml) |
| Migrate to WebLogic 26.1.0, Jakarta EE 11, and Java 21 | [com.oracle.weblogic.rewrite.UpgradeBuildToJava21ForWebLogic2610](./src/main/resources/META-INF/rewrite/weblogic-java-21-25.yaml) <br/> [com.oracle.weblogic.rewrite.jakarta.MigrateToJakartaEE11](./src/main/resources/META-INF/rewrite/jakarta-ee-11.yaml) <br/> [com.oracle.weblogic.rewrite.UpgradeTo2610](./src/main/resources/META-INF/rewrite/weblogic-26.1.0.yaml) |
| Add Spring Framework 7 to a WebLogic 26.1.0 migration | [com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_7_0ForWebLogic2610](./src/main/resources/META-INF/rewrite/spring-framework-7.yaml) |
| Add Jakarta Persistence 3.2 and Hibernate 7 to a WebLogic 26.1.0 migration | [com.oracle.weblogic.rewrite.UpgradeJPATo32HibernateTo70ForWebLogic2610](./src/main/resources/META-INF/rewrite/jakarta-jpa-3.2-hibernate-7.0.yaml) |
| Migrate to WebLogic 15.1.1, Jakarta EE 9.1, and Java 21 | [com.oracle.weblogic.rewrite.UpgradeTo1511](./src/main/resources/META-INF/rewrite/weblogic-15.1.1.yaml) <br/> [org.openrewrite.java.migrate.UpgradeToJava21](https://docs.openrewrite.org/recipes/java/migrate/upgradetojava21) </br> [com.oracle.weblogic.rewrite.JakartaEE9_1](./src/main/resources/META-INF/rewrite/jakarta-ee-9.1.yaml) |
| Migrate to WebLogic 15.1.1, Jakarta EE 9.1, and Java 17 | [com.oracle.weblogic.rewrite.UpgradeTo1511](./src/main/resources/META-INF/rewrite/weblogic-15.1.1.yaml) <br/> [org.openrewrite.java.migrate.UpgradeToJava17](https://docs.openrewrite.org/recipes/java/migrate/upgradetojava17) </br> [com.oracle.weblogic.rewrite.JakartaEE9_1](./src/main/resources/META-INF/rewrite/jakarta-ee-9.1.yaml) |
| Migrate to WebLogic 15.1.1, Jakarta EE 9.1, Java 21, and Spring Framework 6.2.x and Hibernate | [com.oracle.weblogic.rewrite.UpgradeTo1511](./src/main/resources/META-INF/rewrite/weblogic-15.1.1.yaml) <br/> [org.openrewrite.java.migrate.UpgradeToJava21](https://docs.openrewrite.org/recipes/java/migrate/upgradetojava21) </br> [com.oracle.weblogic.rewrite.JakartaEE9_1](./src/main/resources/META-INF/rewrite/jakarta-ee-9.1.yaml) <br/> [com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/spring-framework-6.2.yaml) <br/> [com.oracle.weblogic.rewrite.hibernate.MigrateHibernateToJakartaEE9](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/hibernate.yaml) |
| Migrate to WebLogic 15.1.1, Jakarta EE 9.1, Java 17, and Spring Framework 6.2.x and Hibernate | [com.oracle.weblogic.rewrite.UpgradeTo1511](./src/main/resources/META-INF/rewrite/weblogic-15.1.1.yaml) <br/> [org.openrewrite.java.migrate.UpgradeToJava17](https://docs.openrewrite.org/recipes/java/migrate/upgradetojava17) </br> [com.oracle.weblogic.rewrite.JakartaEE9_1](./src/main/resources/META-INF/rewrite/jakarta-ee-9.1.yaml) <br/> [com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/spring-framework-6.2.yaml) <br/> [com.oracle.weblogic.rewrite.hibernate.MigrateHibernateToJakartaEE9](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/hibernate.yaml) |
| Migrate to WebLogic 15.1.1, Jakarta EE 9.1, Java 17, Spring Framework 6.2.x, JPA 3.1, and Hibernate 6.6 | [com.oracle.weblogic.rewrite.UpgradeTo1511](./src/main/resources/META-INF/rewrite/weblogic-15.1.1.yaml) <br/> [org.openrewrite.java.migrate.UpgradeToJava17](https://docs.openrewrite.org/recipes/java/migrate/upgradetojava17) </br> [com.oracle.weblogic.rewrite.JakartaEE9_1](./src/main/resources/META-INF/rewrite/jakarta-ee-9.1.yaml) <br/> [com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/spring-framework-6.2.yaml) <br/> [com.oracle.weblogic.rewrite.UpgradeJPATo31HibernateTo66](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/jakarta-jpa-3.1-hibernate-6.6.yaml) |
| Migrate to WebLogic 14.1.2 and Java 21 | [com.oracle.weblogic.rewrite.UpgradeTo1412](./src/main/resources/META-INF/rewrite/weblogic-14.1.2.yaml) <br/>  [org.openrewrite.java.migrate.UpgradeToJava21](https://docs.openrewrite.org/recipes/java/migrate/upgradetojava21) |
| Migrate to WebLogic 14.1.2 and Java 17 | [com.oracle.weblogic.rewrite.UpgradeTo1412](./src/main/resources/META-INF/rewrite/weblogic-14.1.2.yaml) <br/> [org.openrewrite.java.migrate.UpgradeToJava17](https://docs.openrewrite.org/recipes/java/migrate/upgradetojava17) |

<!-- | Migrate to WebLogic 14.1.1 and Java 11 | [com.oracle.weblogic.rewrite.UpgradeTo1411](resources/META-INF/rewrite/weblogic-14.1.1.yaml) <br/> [org.openrewrite.java.migrate.Java8toJava11](https://docs.openrewrite.org/recipes/java/migrate/java8tojava11) <br/> [org.openrewrite.maven.ChangePropertyValue](https://docs.openrewrite.org/recipes/maven/changepropertyvalue) <br/> [org.openrewrite.maven.UpgradeParentVersion](https://docs.openrewrite.org/recipes/maven/upgradeparentversion) | - Applies changes required for migrating apps to WebLogic 14.1.1 <br/> - Upgrade Java version to 11 <br/> - Change property to weblogic 14.1.1 <br/> - Upgrade WebLogic Version on parent| -->

See these recipes for [WebLogic 26.1.0](#weblogic-server-2610-recipes) and [WebLogic 15.1.1](#weblogic-server-1511-recipes). For more recipes, see [Rewrite WebLogic recipes](./docs/recipes/README.md).

## How do I use it?

You can call OpenRewrite on your code folder using the Maven or Gradle CLI, or include it as a build plug-in in your `pom.xml` or `build.gradle` file.

For more details on running the recipes with Maven and Gradle, see the [OpenRewrite Quickstart](https://docs.openrewrite.org/running-recipes/getting-started) documentation.

### Prerequisites

The following tools are required to build and run this project:

- JDK 21 or JDK 25 for WebLogic 26.1 development and validation. Use the target JDK when building the migrated application; a Java 25 build requires JDK 25.
- Maven 3.9.16 for the documented Maven workflow. For Gradle applications, use a Gradle version compatible with the JDK running Gradle and the OpenRewrite plugin.
- Your code

### Install the development recipes

The examples in this checkout use the development artifact `com.oracle.weblogic.rewrite:rewrite-weblogic:0.7.0-SNAPSHOT`. Build and install it before running any example against an application:

```shell
git clone --branch owls-137354-weblogic-2610-recipes https://github.com/oracle/rewrite-recipes.git
cd rewrite-recipes
mvn clean install
```

For an existing checkout on this branch, run `git pull --ff-only` and then `mvn clean install`. Use `git rev-parse HEAD` to record the revision being tested. The build already contains the required version settings; no edits to this repository are needed to test it. Installation and the application run must use the same Maven local repository (the same user and settings, including any `maven.repo.local` override).

`0.7.0-SNAPSHOT` is a development coordinate, not a published release. Do not substitute `RELEASE`, `LATEST`, or published `0.6.3`: those do not identify this build. When the 26.1 recipes are released, update the artifact version and these examples together.

Use the following aligned tool and recipe versions in the application configuration:

| Component | Version |
| --- | --- |
| WebLogic recipes | `0.7.0-SNAPSHOT`, installed from this checkout |
| OpenRewrite Maven plugin | `6.27.0` |
| OpenRewrite Gradle plugin | `7.19.0`, with `setRewriteVersion("8.71.0")` |
| Java migration recipes | `3.25.0` |
| Spring recipes, when selected | `6.22.0` |
| Hibernate recipes, when selected | `2.17.0` |
| OpenRewrite core | `8.71.0`, supplied by the aligned Maven plugin |

The recipe dependency versions come from `rewrite-recipe-bom:3.22.0`, imported in [pom.xml](pom.xml). Maven does not inherit a dependency's build plugin configuration: installing this JAR does not update the rewrite plugin in your application's POM.

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
mvn -U org.openrewrite.maven:rewrite-maven-plugin:6.27.0:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:3.25.0,com.oracle.weblogic.rewrite:rewrite-weblogic:0.7.0-SNAPSHOT \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava21,com.oracle.weblogic.rewrite.UpgradeTo1412
```

#### Upgrade applications to WebLogic 14.1.2 and Java 17

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:6.27.0:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:3.25.0,com.oracle.weblogic.rewrite:rewrite-weblogic:0.7.0-SNAPSHOT \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava17,com.oracle.weblogic.rewrite.UpgradeTo1412
```

### Run using Maven with the `<plugin>` in the `pom.xml` file

1. After [installing the development recipes](#install-the-development-recipes), add or replace the rewrite plugin in the **application's** `pom.xml` under `<build><plugins>`. The following configuration targets WebLogic 26.1.0, Jakarta EE 11, and Java 21. For Java 25, replace the first recipe with `com.oracle.weblogic.rewrite.UpgradeBuildToJava25ForWebLogic2610`.

    ```xml
        <plugin>
            <groupId>org.openrewrite.maven</groupId>
            <artifactId>rewrite-maven-plugin</artifactId>
            <version>6.27.0</version>
            <configuration>
                <exportDatatables>true</exportDatatables>
                <activeRecipes>
                    <recipe>com.oracle.weblogic.rewrite.UpgradeBuildToJava21ForWebLogic2610</recipe>
                    <recipe>com.oracle.weblogic.rewrite.jakarta.MigrateToJakartaEE11</recipe>
                    <recipe>com.oracle.weblogic.rewrite.UpgradeTo2610</recipe>
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
    ```

2. From the application's directory, preview the migration with `mvn -e -X rewrite:dryRun`. Confirm that the log names plugin `6.27.0`, Java recipes `3.25.0`, and WebLogic recipes `0.7.0-SNAPSHOT`. Review `target/rewrite/rewrite.patch` and the exported data tables. Use `mvn rewrite:run` to apply the migration after reviewing it.

If the log still names `rewrite-maven-plugin:6.3.1`, the application is still selecting the older plugin; check its POM, parent POM, and active profiles. The `ScanningRecipe cannot be used as Preconditions` error can result from mixing that old plugin with newer recipe artifacts. Use the aligned versions above instead of disabling a nested recipe. If the failure remains, include the complete command, `mvn -version`, recipe repository commit, and full `-e -X` log in the report.

> [!TIP]  
> If you just want to dry run the recipe without changing the code, use `mvn rewrite:dryRun`. For more details when using Maven, see [here](https://docs.openrewrite.org/reference/rewrite-maven-plugin).

- If you are using `git` for your repository, you can see the differences by using `git diff` or other diff viewers. This option will show you the differences between your original and migrated code.

### Run using Gradle

The following example illustrates using Gradle to run the Upgrade to WebLogic 14.1.2 and Upgrade to Java 21 recipes.

First [install the development recipes](#install-the-development-recipes) with Maven. The `mavenLocal()` entry below allows Gradle to load that snapshot; it must point to the same local repository used by `mvn install`. The examples explicitly select OpenRewrite core `8.71.0` to match the recipe BOM.

1. Add the following to your `build.gradle` file:

```
plugins {
    id("org.openrewrite.rewrite") version("7.19.0")
}

rewrite {
    setRewriteVersion("8.71.0")
    activeRecipe("org.openrewrite.java.migrate.UpgradeToJava21")
    activeRecipe("com.oracle.weblogic.rewrite.UpgradeTo1412")
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

> [!NOTE]
> You can run `rewrite` on a Gradle project without modifying the build, similar to using the `mvn` CLI command. For more information, see https://docs.openrewrite.org/running-recipes/running-rewrite-on-a-gradle-project-without-modifying-the-build.

## WebLogic Server 26.1.0 recipes

First [install the development recipes](#install-the-development-recipes). Run these commands from the application directory. They preview changes; replace `:dryRun` with `:run` when ready to apply them. The commands use Bash-style line continuation; in Windows Command Prompt or PowerShell, enter the command on one line or use that shell's continuation syntax. If the application already declares the rewrite plugin, update its plugin dependencies to the same versions shown here before running the CLI examples.

> [!WARNING]
> Before deploying a migrated application, make sure that you are using an approved WebLogic Server 26.1.0 installation.

> [!NOTE]
> Run the Java build-target recipe first, followed by the Jakarta EE 11 recipe and then `UpgradeTo2610`. Choose either the Java 21 or Java 25 build-target recipe. These Java recipes update the application build target without otherwise modernizing application source code.

> [!IMPORTANT]
> Keep `rewrite.exportDatatables` enabled and review the exported `SearchResults` data table before building or deploying the migrated application. Some application-specific findings require manual review instead of an automatic source change, including Security Manager usage, WebLogic classloading overrides, non-XA JDBC transaction settings, removed APIs, and Spring migrations that do not have a safe one-to-one replacement.

> [!NOTE]
> The WebLogic 26.1.0 core recipe updates Maven build configuration. Gradle projects can run the source and descriptor recipes, but equivalent WebLogic dependency, plug-in, and build configuration changes may need to be made manually in Gradle files.

#### Upgrade applications to WebLogic Server 26.1.0, Jakarta EE 11, and Java 25

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:6.27.0:dryRun \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:3.25.0,com.oracle.weblogic.rewrite:rewrite-weblogic:0.7.0-SNAPSHOT \
  -Drewrite.activeRecipes=com.oracle.weblogic.rewrite.UpgradeBuildToJava25ForWebLogic2610,com.oracle.weblogic.rewrite.jakarta.MigrateToJakartaEE11,com.oracle.weblogic.rewrite.UpgradeTo2610 \
  -Drewrite.exportDatatables=true
```

#### Upgrade applications to WebLogic Server 26.1.0, Jakarta EE 11, and Java 21

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:6.27.0:dryRun \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:3.25.0,com.oracle.weblogic.rewrite:rewrite-weblogic:0.7.0-SNAPSHOT \
  -Drewrite.activeRecipes=com.oracle.weblogic.rewrite.UpgradeBuildToJava21ForWebLogic2610,com.oracle.weblogic.rewrite.jakarta.MigrateToJakartaEE11,com.oracle.weblogic.rewrite.UpgradeTo2610 \
  -Drewrite.exportDatatables=true
```

The `UpgradeTo2610` recipe updates WebLogic dependencies, plug-ins, and deployment descriptors. If the application already has a Maven Wrapper, the recipe updates it to Maven 3.9.16; it does not create a wrapper when one is absent. The recipe also reports deprecated or removed APIs and migration risks through OpenRewrite data tables.

#### Add Spring Framework 7 and Hibernate 7

The Spring Framework 7 and Hibernate 7 recipes are optional and independent. Activate either recipe or both according to the application's dependencies. These recipes keep upgrades within the versions and release lines configured for this WebLogic migration lane—Spring Framework 7.0.x, Spring Data 2025.1.0, and Hibernate ORM 7.0.8.Final—instead of selecting arbitrary major releases. The Spring recipe imports the Spring Framework BOM before the Spring Data BOM so direct and transitive Spring Framework modules use the same 7.0 patch version.

For a Maven project with `war` packaging, the Spring recipe creates `src/main/webapp/WEB-INF/weblogic.xml` when it is absent or merges with the existing descriptor. It adds selective application preferences for Jackson packages and for the Jackson module service-provider resource, preventing WebLogic's legacy `javax.xml.bind` Jackson module from being discovered by the migrated application while leaving JAXB under WebLogic's classloading. Existing descriptor entries, including application-specific package preferences, are preserved. For a non-Maven build or a nonstandard web-resource layout, the recipe updates an existing `weblogic.xml` but does not create one automatically.

For applications that use both Spring Framework and Hibernate, add both recipe artifacts and activate their WebLogic 26.1.0 recipes after the core migration recipes. The following example targets Java 21; replace the first active recipe with `UpgradeBuildToJava25ForWebLogic2610` to target Java 25.

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:6.27.0:dryRun \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:3.25.0,org.openrewrite.recipe:rewrite-spring:6.22.0,org.openrewrite.recipe:rewrite-hibernate:2.17.0,com.oracle.weblogic.rewrite:rewrite-weblogic:0.7.0-SNAPSHOT \
  -Drewrite.activeRecipes=com.oracle.weblogic.rewrite.UpgradeBuildToJava21ForWebLogic2610,com.oracle.weblogic.rewrite.jakarta.MigrateToJakartaEE11,com.oracle.weblogic.rewrite.UpgradeTo2610,com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_7_0ForWebLogic2610,com.oracle.weblogic.rewrite.UpgradeJPATo32HibernateTo70ForWebLogic2610 \
  -Drewrite.exportDatatables=true
```

## WebLogic Server 15.1.1 recipes

> [!WARNING]  
> Before deploying a migrated application, make sure that you are using an approved WebLogic Server 15.1.1 installation.

> [!NOTE]
> When upgrading to WebLogic Server 15.1.1, the `UpgradeTo1511` recipe must be run _after_ the Java and Jakarta recipes.

#### Upgrade applications to WebLogic Server 15.1.1, Jakarta EE 9.1, and Java 21

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:6.27.0:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:3.25.0,com.oracle.weblogic.rewrite:rewrite-weblogic:0.7.0-SNAPSHOT \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava21,com.oracle.weblogic.rewrite.JakartaEE9_1,com.oracle.weblogic.rewrite.UpgradeTo1511 \
  -Drewrite.exportDatatables=true
```

#### Upgrade applications to WebLogic Server 15.1.1, Jakarta EE 9.1, and Java 17

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:6.27.0:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:3.25.0,com.oracle.weblogic.rewrite:rewrite-weblogic:0.7.0-SNAPSHOT \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava17,com.oracle.weblogic.rewrite.JakartaEE9_1,com.oracle.weblogic.rewrite.UpgradeTo1511 \
  -Drewrite.exportDatatables=true
```


#### **Spring Framework upgrade:** Upgrade applications to WebLogic Server 15.1.1, Jakarta EE 9.1, Java 21, and Spring Framework 6.2.x

If your application is running with Spring Framework 5.x on WebLogic 12.x or 14.x, and you want to migrate to Spring Framework 6.2.x, add the Spring Framework recipe to the command:

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:6.27.0:run \
      -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:3.25.0,com.oracle.weblogic.rewrite:rewrite-weblogic:0.7.0-SNAPSHOT,org.openrewrite.recipe:rewrite-spring:6.22.0 \
      -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava21,com.oracle.weblogic.rewrite.JakartaEE9_1,com.oracle.weblogic.rewrite.UpgradeTo1511,com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2 \
      -Drewrite.exportDatatables=true
```

> [!TIP]  
> When upgrading your application to Spring Framework 6.2.x, you may have other libraries that need to be updated. You can include the recipe in the same command, like `org.hibernate`, or run other third-party libraries separately. For other examples, see the [tutorials](https://github.com/oracle-samples/weblogic-examples).

#### **Spring Framework upgrade:** Upgrade applications to WebLogic Server 15.1.1, Jakarta EE 9.1, Java 17, and Spring Framework 6.2.x

If your application is running with Spring Framework 5.x on WebLogic 12.x or 14.x, and you want to migrate to Spring Framework 6.2.x, add the Spring Framework recipe to the command:

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:6.27.0:run \
      -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:3.25.0,com.oracle.weblogic.rewrite:rewrite-weblogic:0.7.0-SNAPSHOT,org.openrewrite.recipe:rewrite-spring:6.22.0 \
      -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava17,com.oracle.weblogic.rewrite.JakartaEE9_1,com.oracle.weblogic.rewrite.UpgradeTo1511,com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2 \
      -Drewrite.exportDatatables=true
```

> [!TIP]  
> When upgrading your application to Spring Framework 6.2.x, you may have other libraries that need to be updated. You can include the recipe in the same command, like `org.hibernate`, or run other third-party libraries separately. For other examples, see the [tutorials](https://github.com/oracle-samples/weblogic-examples).


#### Spring Framework and Hibernate upgrade

This example includes the Hibernate recipe running with the WebLogic 15.1.1, Java 21, and Spring Framework 6.2 recipes.

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:6.27.0:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:3.25.0,com.oracle.weblogic.rewrite:rewrite-weblogic:0.7.0-SNAPSHOT,org.openrewrite.recipe:rewrite-spring:6.22.0,org.openrewrite.recipe:rewrite-hibernate:2.17.0 \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava21,com.oracle.weblogic.rewrite.JakartaEE9_1,com.oracle.weblogic.rewrite.UpgradeTo1511,com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2,com.oracle.weblogic.rewrite.hibernate.MigrateHibernateToJakartaEE9 \
  -Drewrite.exportDatatables=true
```

This example includes the Hibernate recipe running with the WebLogic 15.1.1, Java 17, and Spring Framework 6.2 recipes.

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:6.27.0:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:3.25.0,com.oracle.weblogic.rewrite:rewrite-weblogic:0.7.0-SNAPSHOT,org.openrewrite.recipe:rewrite-spring:6.22.0,org.openrewrite.recipe:rewrite-hibernate:2.17.0 \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava17,com.oracle.weblogic.rewrite.JakartaEE9_1,com.oracle.weblogic.rewrite.UpgradeTo1511,com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2,com.oracle.weblogic.rewrite.hibernate.MigrateHibernateToJakartaEE9 \
  -Drewrite.exportDatatables=true
```


## Developing, contributing, and testing this recipe locally
[Development version: 0.7.0-SNAPSHOT](pom.xml)
[![GitHub issues](https://img.shields.io/github/issues-raw/oracle/rewrite-recipes)](https://github.com/oracle/rewrite-recipes/issues)
[![Contributing Guide](https://img.shields.io/badge/Contributing-Guide-informational)](https://github.com/oracle/rewrite-recipes/blob/main/CONTRIBUTING.md)
[![Code of Conduct](https://img.shields.io/badge/Code%20of%20Conduct-Oracle%20Community%20Code%20of%20Conduct-informational)](https://www.oracle.com/corporate/community/code-of-conduct.html)

> [!IMPORTANT]
> Follow [Install the development recipes](#install-the-development-recipes) after each recipe update, then run the documented configuration from the application directory. A successful recipe build tests and installs the recipes; it does not migrate the application.

### Prerequisites

The following tools are required to build and run this project:

- JDK 21 or JDK 25
- Maven 3.9.16
- Your code

#### Testing

Run the recipe unit tests from this repository:

```shell
mvn test
```

Use the [application POM configuration](#run-using-maven-with-the-plugin-in-the-pomxml-file) or the [WebLogic 26.1 CLI examples](#weblogic-server-2610-recipes) to test the installed recipes against an application. `dryRun` produces a proposed patch; success does not establish that the migrated application builds or deploys. Review the data tables and validate the application on its target JDK and WebLogic release.

## License

Copyright (c) 2025 Oracle and/or its affiliates.

Released under the Universal Permissive License v1.0 as shown at
<https://oss.oracle.com/licenses/upl/>.
