# Rewrite WebLogic

> [!WARNING]
> This is a ***pre-release project***. Some of the information and commands may change in the near future

Migrate your WebLogic applications to the latest version of WebLogic and Java with OpenRewrite. Automatically.

## What is this?

This project implements a [Rewrite module](https://github.com/openrewrite/rewrite) that performs common tasks when
migrating your [WebLogic](https://www.oracle.com/java/weblogic/) applications to a new version of WebLogic and Java.

## How do I use it?

You can call OpenRewrite on your code folder using the maven or gradle cli or include it as a build plugin on your pom.xml.

The [OpenRewrite getting started documentation](https://docs.openrewrite.org/running-recipes/getting-started) provides more details on running the recipes with Maven and Gradle.

### Prerequisites

The following tools are required to build and execute this project:

- Java
- Maven or Gradle
- Your code

### Get your code

Clone your project to your local machine. 

> [!IMPORTANT]  
> Note that the run command will make changes to your code. Always make sure you have a backup of your code. When using git, create a branch and use git diff to see the differences

### Running using the maven cli


```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-weblogic:LATEST \
  -Drewrite.activeRecipes=com.oracle.weblogic.rewrite.upgradeTo1411
```

mvn -U org.openrewrite.maven:rewrite-maven-plugin:dryRunNoFork \
  -Drewrite.activeRecipes=com.oracle.weblogic.rewrite.upgradeTo1411


### Running using maven and adding `<plugin>` on the `pom.xml`

- Add the plugin to your `pom.xml` and activate the desired recipe from the rewrite-weblogic group recipe. In this example, using the Upgrade to Weblogic 14.1.2

    ```xml
        <plugin>
            <groupId>org.openrewrite.maven</groupId>
            <artifactId>rewrite-maven-plugin</artifactId>
            <version>5.43.0</version>
            <configuration>
                <activeRecipes>
                    <recipe>com.oracle.weblogic.rewrite.upgradeTo1412</recipe>
                </activeRecipes>
            </configuration>
            <dependencies>
                <dependency>
                    <groupId>com.oracle.weblogic.rewrite</groupId>
                    <artifactId>rewrite-weblogic</artifactId>
                    <version>0.1.0</version>
                </dependency>
            </dependencies>
        </plugin>
    ```

- Run the recipe
    `mvn rewrite:run`

    > [!TIP]  
    > If you just want to dry run the recipe without change the code, use `mvn rewrite:DryRun`. More details when using maven can be found [here](https://docs.openrewrite.org/reference/rewrite-maven-plugin)

    > [!TIP]  
    > The goals `rewrite:run` and `rewrite:dryRun` are configured to fork Maven's life cycle and are a better choice when running recipes via a stand-alone goal (`mvn rewrite:run`) because this will trigger all the necessary life-cycle goals prior to running rewrite's plugin. However, when using rewrite within the context of an integration build (`mvn deploy rewrite:run`) it may be more efficient to use the non-forking variants, as these will not cause duplicate life cycle phases to be called.

- You can see the differences by using `git diff` or other diff viewer

## Developing, contributing and testing this recipe locally

### Prerequisites

The following tools are required to build and execute this project:

- JDK (version 1.8 or higher)
- Maven (version 3.2+) or Gradle (version 4.0+ )
- Your code

#### Dry Run

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

## Recipes

| Composite Recipes | Recipes | Description |
| --- | --- | --- |
| Migrate to WebLogic 14.1.2 and Java 21 | [com.oracle.weblogic.rewrite.upgradeTo1412](resources/META-INF/rewrite/weblogic-14.1.2.yaml) <br/> com.oracle.weblogic.rewrite.UpgradeWeblogicMavenPropertyVersion <br/> [org.openrewrite.java.migrate.UpgradeToJava21](https://docs.openrewrite.org/recipes/java/migrate/upgradetojava21) | - Applies changes required for migrating apps to WebLogic 14.1.2 <br/> - Upgrade WebLogic Version <br/> - Upgrade Java version to 21|
| Migrate to WebLogic 14.1.2 and Java 17 | [com.oracle.weblogic.rewrite.upgradeTo1412](resources/META-INF/rewrite/weblogic-14.1.2.yaml) <br/> com.oracle.weblogic.rewrite.UpgradeWeblogicMavenPropertyVersion <br/> [org.openrewrite.java.migrate.UpgradeToJava17](https://docs.openrewrite.org/recipes/java/migrate/upgradetojava17) | - Applies changes required for migrating apps to WebLogic 14.1.2 <br/> - Upgrade WebLogic Version <br/> - Upgrade Java version to 17|
| Migrate to WebLogic 14.1.1 and Java 11 | [com.oracle.weblogic.rewrite.upgradeTo1411](resources/META-INF/rewrite/weblogic-14.1.1.yaml) <br/> [org.openrewrite.java.migrate.Java8toJava11](https://docs.openrewrite.org/recipes/java/migrate/java8tojava11) <br/> [org.openrewrite.maven.ChangePropertyValue](https://docs.openrewrite.org/recipes/maven/changepropertyvalue) <br/> [org.openrewrite.maven.UpgradeParentVersion](https://docs.openrewrite.org/recipes/maven/upgradeparentversion) | - Applies changes required for migrating apps to WebLogic 14.1.1 <br/> - Upgrade Java version to 11 <br/> - Change property to weblogic 14.1.1 <br/> - Upgrade WebLogic Version on parent|

