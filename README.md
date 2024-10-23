# OpenReWrite Recipes

Note > *** This is a pre-release project, some of the information and commands may change in a near future ***

This project implements OpenRewrite recipes on Oracle products.

Each folder of this project contains a group recipe that can container multiple composite recipes. Each composite recipe is a set of recipes that can be used to migrate a specific version of a product.

Example: `rewrite-weblogic` is a group recipe that contains composite recipes like the one that can be used to migrate WebLogic 14.1.1 and Java 17. With a new version of WebLogic, a new composite recipe is added, like the migrate to WebLogic 14.1.2 and Java 21 and migrate to WebLogic 14.1.2 and Java 17.

## Getting Started

Before getting started, select which recipe you would like to use. Each recipe has its own README file that explains how to use it.

### Getting started with [Rewrite WebLogic](rewrite-weblogic/README.md)

#### Prerequisites

The following tools are required to build and execute this project:

- JDK (version 1.8 or higher)
- Maven (version 3.2+)
- Your code

#### Get your code

Clone your project to your local machine. Note that the run command will make changes to your code, always make sure you have a backup of your code.

#### Run the recipe

To run the recipe, execute the following command:

```shell
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-weblogic:LATEST \
  -Drewrite.activeRecipes=com.oracle.weblogic.upgradeTo1411
```

## Get Help

We have a closely monitored public Slack channel where you can get in touch with us to ask questions about using the WebLogic or give us feedback or suggestions about what features and improvements you would like to see.
We would love to hear from you.

To join our public channel, please visit this [site](https://weblogic-slack-inviter.herokuapp.com/) to get an invitation. The invitation email will include details of how to access our Slack workspace.

## Contributing

This project welcomes contributions from the community. Before submitting a pull request, please [review our contribution guide](./CONTRIBUTING.md)

## Security

Please consult the [security guide](./SECURITY.md) for our responsible security vulnerability disclosure process

## License

Copyright (c) 2024 Oracle and/or its affiliates.

Released under the Universal Permissive License v1.0 as shown at
<https://oss.oracle.com/licenses/upl/>.
