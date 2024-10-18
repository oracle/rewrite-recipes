# Rewrite WebLogic

Migrate your WebLogic applications to the latest version of WebLogic and Java with OpenRewrite. Automatically.

## What is this?

This project implements a [Rewrite module](https://github.com/openrewrite/rewrite) that performs common tasks when
migrating your [WebLogic](https://www.oracle.com/java/weblogic/) applications to a new version of WebLogic and Java.

## How do I use it?

### Prerequisites

The following tools are required to build and execute this project:

- Java (version 8 or higher)
- Maven

### Execution

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
