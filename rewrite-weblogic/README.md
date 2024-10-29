# Rewrite WebLogic

> [!WARNING]
> This is a ***pre-release project***, some of the information and commands may change in a near future

Migrate your WebLogic applications to the latest version of WebLogic and Java with OpenRewrite. Automatically.

## What is this?

This project implements a [Rewrite module](https://github.com/openrewrite/rewrite) that performs common tasks when
migrating your [WebLogic](https://www.oracle.com/java/weblogic/) applications to a new version of WebLogic and Java.

## How do I use it?

On your code folder, you call the OpenRewrite using the maven or gradle cli or include as a build pluing on your pom.xml.

More details on how to run the recipes with maven and gradle can be found on the [OpenRewrite getting started documentation](https://docs.openrewrite.org/running-recipes/getting-started).

### Prerequisites

The following tools are required to build and execute this project:

- JDK (version 1.8 or higher)
- Maven (version 3.2+)
- Your code

### Get your code

Clone your project to your local machine. 

> [!IMPORTANT]  
> Note that the run command will make changes to your code, always make sure you have a backup of your code. When using git, create a branch and use git diff to see the differences.

### Running using the maven cli



### Running using the pom.xml

- Add the plugin to your pom.xml and activate the desired recipe from the rewrite-weblogic group recipe. In this example using the Upgrade to Weblogic 14.1.2

    ```xml
        <plugin>
            <groupId>org.openrewrite.maven</groupId>
            <artifactId>rewrite-maven-plugin</artifactId>
            <version>5.43.0</version>
            <configuration>
                <activeRecipes>
                    <recipe>com.oracle.weblogic.upgradeTo1412</recipe>
                </activeRecipes>
            </configuration>
            <dependencies>
                <dependency>
                    <groupId>com.oracle.weblogic</groupId>
                    <artifactId>rewrite-weblogic</artifactId>
                    <version>0.1.0</version>
                </dependency>
            </dependencies>
        </plugin>
    ```

## Developing and testing this recipe locally

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

## Composite Recipes

- Migrate to WebLogic 14.1.1 and Java 17
