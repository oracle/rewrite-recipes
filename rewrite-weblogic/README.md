# Rewrite WebLogic

Migrate your WebLogic applications to the latest version of WebLogic and Java with OpenRewrite. Automatically.

## What is this?

This project implements a [Rewrite module](https://github.com/openrewrite/rewrite) that performs common tasks when
migrating your [WebLogic](https://www.oracle.com/java/weblogic/) applications to a new version of WebLogic and Java.

Elevate your WebLogic applications effortlessly! Upgrade to the latest version of WebLogic and ensure compatibility with supported Java and Jakarta EE, all thanks to OpenRewrite recipes.

Experience automatic migration like never before! Migrate your WebLogic applications to the latest version of WebLogic and supported Java, Jakarta EE with OpenRewrite. Automatically.

To learn all about WebLogic Server application upgrade tooling, see the [docs](./docs/index.md).

To jump start your hands on learning, see the [examples and tutorials](https://github.com/oracle-samples/weblogic-examples).

## Recipes

| Composite Recipes | Recipes | Description |
| --- | --- | --- |
| **BETA:** Migrate to WebLogic 15.1.1 BETA, Jakarta EE 9.1 and Java 21 | [com.oracle.weblogic.rewrite.UpgradeTo1511](./src/main/resources/META-INF/rewrite/weblogic-15.1.1.yaml) <br/> [org.openrewrite.java.migrate.UpgradeToJava21](https://docs.openrewrite.org/recipes/java/migrate/upgradetojava21) </br> [com.oracle.weblogic.rewrite.JakartaEE9_1](./src/main/resources/META-INF/rewrite/jakarta-ee-9.1.yaml) | - Applies changes required for migrating applications to WebLogic 15.1.1 and Jakarta EE 9.1 <br/> - Upgrades Java version to 21|
| **BETA:** Migrate to WebLogic 15.1.1 BETA, Jakarta EE 9.1 and Java 17 | [com.oracle.weblogic.rewrite.UpgradeTo1511](./src/main/resources/META-INF/rewrite/weblogic-15.1.1.yaml) <br/> [org.openrewrite.java.migrate.UpgradeToJava17](https://docs.openrewrite.org/recipes/java/migrate/upgradetojava21) </br> [com.oracle.weblogic.rewrite.JakartaEE9_1](./src/main/resources/META-INF/rewrite/jakarta-ee-9.1.yaml) | - Applies changes required for migrating applications to WebLogic 15.1.1 and Jakarta EE 9.1 <br/> - Upgrades Java version to 17|
| Migrate to WebLogic 14.1.2 and Java 21 | [com.oracle.weblogic.rewrite.UpgradeTo1412](./src/main/resources/META-INF/rewrite/weblogic-14.1.2.yaml) <br/>  [org.openrewrite.java.migrate.UpgradeToJava21](https://docs.openrewrite.org/recipes/java/migrate/upgradetojava21) | - Applies changes required for migrating applications to WebLogic 14.1.2 <br/> - Upgrades Java version to 21|
| Migrate to WebLogic 14.1.2 and Java 17 | [com.oracle.weblogic.rewrite.UpgradeTo1412](./src/main/resources/META-INF/rewrite/weblogic-14.1.2.yaml) <br/> [org.openrewrite.java.migrate.UpgradeToJava17](https://docs.openrewrite.org/recipes/java/migrate/upgradetojava17) | - Applies changes required for migrating applications to WebLogic 14.1.2 <br/> - Upgrades Java version to 17|
<!-- | Migrate to WebLogic 14.1.1 and Java 11 | [com.oracle.weblogic.rewrite.UpgradeTo1411](resources/META-INF/rewrite/weblogic-14.1.1.yaml) <br/> [org.openrewrite.java.migrate.Java8toJava11](https://docs.openrewrite.org/recipes/java/migrate/java8tojava11) <br/> [org.openrewrite.maven.ChangePropertyValue](https://docs.openrewrite.org/recipes/maven/changepropertyvalue) <br/> [org.openrewrite.maven.UpgradeParentVersion](https://docs.openrewrite.org/recipes/maven/upgradeparentversion) | - Applies changes required for migrating apps to WebLogic 14.1.1 <br/> - Upgrade Java version to 11 <br/> - Change property to weblogic 14.1.1 <br/> - Upgrade WebLogic Version on parent| -->

## How do I use it?

You can call OpenRewrite on your code folder using the Maven or Gradle CLI or include it as a build plug-in in your `pom.xml` file.

For more details on running the recipes with Maven and Gradle, see the [OpenRewrite Quickstart](https://docs.openrewrite.org/running-recipes/getting-started) documentation.

### Prerequisites

The following tools are required to build and run this project:

- Java
- Maven or Gradle
- Your code

### Get your code

Clone your project to your local machine.

> [!IMPORTANT]  
> Note that the run command will make changes to your code. Always make sure you have a backup of your code. When using `git`, create a branch and use `git diff` to see the differences.

### Running using the Maven CLI

#### Upgrading applications to WebLogic 14.1.2 and Java 21

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST \
  -Drewrite.activeRecipes=com.oracle.weblogic.rewrite.UpgradeTo1412,org.openrewrite.java.migrate.UpgradeToJava21
```

#### Upgrading applications to WebLogic 14.1.2 and Java 17

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST \
  -Drewrite.activeRecipes=com.oracle.weblogic.rewrite.UpgradeTo1412,org.openrewrite.java.migrate.UpgradeToJava17
```

#### **WebLogic Server 15.1.1 BETA recipes**

> [!WARNING]  
> Make sure you are using an approved WebLogic Server 15.1.1 BETA installation before deploying the migrated app

##### Upgrading applications to WebLogic Server 15.1.1 BETA, Jakarta EE 9.1 and Java 21

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST \
  -Drewrite.activeRecipes=com.oracle.weblogic.rewrite.UpgradeTo1511,org.openrewrite.java.migrate.UpgradeToJava21,com.oracle.weblogic.rewrite.JakartaEE9_1 \
  -Drewrite.exportDatatables=true
```

##### Upgrading applications to WebLogic Server 15.1.1 BETA, Jakarta EE 9.1 and Java 17

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST \
  -Drewrite.activeRecipes=com.oracle.weblogic.rewrite.UpgradeTo1511,org.openrewrite.java.migrate.UpgradeToJava17,com.oracle.weblogic.rewrite.JakartaEE9_1 \
  -Drewrite.exportDatatables=true
```

##### Spring Framework upgrade
If your application is running with Spring Framework 5.x on WebLogic 12.x or 14.x and you want to migrate to Spring Framework 6.x, upgrade the application to WebLogic 15.1.1, and then run this command:

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-spring:RELEASE \
  -Drewrite.activeRecipes=org.openrewrite.java.spring.framework.UpgradeSpringFramework_6_1 -Drewrite.exportDatatables=true
```

### Running using Maven and adding `<plugin>` in the `pom.xml` file

- Add the plug-in to your `pom.xml` file and activate the desired recipe from the `rewrite-weblogic` group recipe. The following example illustrates Upgrade to WebLogic 14.1.2 and Migrate to Java 21.

    ```xml
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

- Run the recipe
    `mvn rewrite:run`

> [!TIP]  
> If you just want to dry run the recipe without changing the code, use `mvn rewrite:DryRun`. For more details when using Maven, see [here](https://docs.openrewrite.org/reference/rewrite-maven-plugin).

> [!TIP]  
> The goals `rewrite:run` and `rewrite:dryRun` are configured to fork Maven's life cycle and are a better choice when running recipes using a standalone goal (`mvn rewrite:run`) because this will trigger all the necessary life-cycle goals before running rewrite's plug-in. However, when using rewrite within the context of an integration build (`mvn deploy rewrite:run`), it may be more efficient to use the non-forking variants, as these will not cause duplicate life cycle phases to be called.

- If you are using `git` for your repository, you can see the differences by using `git diff` or other diff viewers. This option will show you the differences between your original and migrated code.

## Developing, contributing, and testing this recipe locally

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
