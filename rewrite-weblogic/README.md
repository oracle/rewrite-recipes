# Rewrite WebLogic

Migrate your WebLogic applications to the latest version of WebLogic and Java with OpenRewrite. Automatically.

## What is this?

This project implements a [Rewrite module](https://github.com/openrewrite/rewrite) that performs common tasks when
migrating your [WebLogic](https://www.oracle.com/java/weblogic/) applications to a new version of WebLogic Server and Java.

WebLogic Server application upgrade tooling makes upgrading your WebLogic applications easy. To learn all about it, see the [**Documentation**](./docs/index.md).

To jump start your hands-on learning, see these [**Examples and Tutorials**](https://github.com/oracle-samples/weblogic-examples):

- [Example Applications](https://github.com/oracle-samples/weblogic-examples/blob/main/README.md#examples)
- [Migrate WebLogic Cafe to WLS 14.1.2](https://github.com/oracle-samples/weblogic-examples/blob/main/tutorials/migrate/weblogic-cafe-14.1.2/README.md)
- [Migrate WebLogic Cafe to WLS 15.1.1 (BETA)](https://github.com/oracle-samples/weblogic-examples/blob/main/tutorials/migrate/weblogic-cafe-15.1.1/README.md)
- [Migrate Spring Framework PetClinic to WLS 15.1.1 (BETA)](https://github.com/oracle-samples/weblogic-examples/blob/main/tutorials/migrate/spring-framework-petclinic-15.1.1/README.md)

## Recipes

Use these `rewrite-weblogic` recipes to migrate WebLogic Server applications to newer versions of WebLogic Server, Java, Jakarta EE, and related versions of Jakarta Server Faces and Spring Framework.

> [!NOTE]
> These recipes do not address _any_ other third-party dependencies. For all other third-party dependencies, you _must_ find or create an additional [recipe](https://docs.openrewrite.org/recipes) to do the migration, or make manual code changes after the migration.

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
> Note that the `run` command will make changes to your code. Always make sure you have a backup of your code. When using `git`, create a branch and use `git diff` to see the differences.

### Run using the Maven CLI

> [!TIP]  
> If you're using WebLogic artifacts, you need to install the corresponding [WebLogic Maven plug-in](https://docs.oracle.com/en/middleware/fusion-middleware/weblogic-server/14.1.2/wlprg/maven.html#GUID-7759C76C-D6E9-4A7E-BE99-96787814576D) version (for example, 14.1.1). After migration (for example, to 15.1.1), update and sync the plug-in version accordingly, before building the migrated application.

#### Upgrade applications to WebLogic 14.1.2 and Java 21

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

#### **WebLogic Server 15.1.1 BETA recipes**

> [!WARNING]  
> Before deploying a migrated application, make sure that you are using an approved WebLogic Server 15.1.1 BETA installation.

##### Upgrade applications to WebLogic Server 15.1.1 BETA, Jakarta EE 9.1, and Java 21

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:RELEASE,com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava21,com.oracle.weblogic.rewrite.JakartaEE9_1,com.oracle.weblogic.rewrite.UpgradeTo1511 \
  -Drewrite.exportDatatables=true
```

##### Upgrade applications to WebLogic Server 15.1.1 BETA, Jakarta EE 9.1, and Java 17

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:RELEASE,com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava17,com.oracle.weblogic.rewrite.JakartaEE9_1,com.oracle.weblogic.rewrite.UpgradeTo1511 \
  -Drewrite.exportDatatables=true
```
> [!NOTE]
> When upgrading to WebLogic Server 15.1.1 BETA, the `UpgradeTo1511` recipe must be run _after_ the Java and Jakarta recipes.


##### **Spring Framework upgrade:** Upgrade applications to WebLogic Server 15.1.1 BETA, Jakarta EE 9.1, Java 21, and Spring Framework 6.2.x

If your application is running with Spring Framework 5.x on WebLogic 12.x or 14.x, and you want to migrate to Spring Framework 6.2.x, add the Spring Framework recipe to the command:

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
      -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:RELEASE,com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST,org.openrewrite.recipe:rewrite-spring:RELEASE \
      -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava21,com.oracle.weblogic.rewrite.JakartaEE9_1,com.oracle.weblogic.rewrite.UpgradeTo1511,com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2 \
      -Drewrite.exportDatatables=true
```

> [!TIP]  
> When upgrading your application to Spring Framework 6.2.x, you may have other libraries that need to be updated. You can include the recipe in the same command, like `org.hibernate`, or run other third-party libraries separately. For other examples, see the [tutorials](https://github.com/oracle-samples/weblogic-examples).

Example that includes the Hibernate recipe running with the WebLogic 15.1.1 BETA, Java 21, and Spring Framework 6.2 recipes.

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:RELEASE,com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST,org.openrewrite.recipe:rewrite-spring:RELEASE,org.openrewrite.recipe:rewrite-hibernate:RELEASE \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava21,com.oracle.weblogic.rewrite.JakartaEE9_1,com.oracle.weblogic.rewrite.UpgradeTo1511,com.oracle.weblogic.rewrite.spring.framework.UpgradeToSpringFramework_6_2,com.oracle.weblogic.rewrite.hibernate.MigrateHibernate4JakartaEE9 \
  -Drewrite.exportDatatables=true
```


### Run using Maven with the `<plugin>` in the `pom.xml` file

- Add the plug-in to your `pom.xml` file and activate the desired recipe from the `rewrite-weblogic` group recipe. The following example illustrates Upgrade to WebLogic 14.1.2 and Upgrade to Java 21.

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

- Run the recipe
    `mvn rewrite:run`

> [!TIP]  
> If you just want to dry run the recipe without changing the code, use `mvn rewrite:DryRun`. For more details when using Maven, see [here](https://docs.openrewrite.org/reference/rewrite-maven-plugin).

> [!TIP]  
> The goals `rewrite:run` and `rewrite:dryRun` are configured to fork Maven's life cycle and are a better choice when running recipes using a standalone goal (`mvn rewrite:run`) because this will trigger all the necessary lifecycle goals before running rewrite's plug-in. However, when using `rewrite` within the context of an integration build (`mvn deploy rewrite:run`), it may be more efficient to use the non-forking variants, as these will not cause duplicate lifecycle phases to be called.

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
