# OpenRewrite Recipes

> [!WARNING]
> This is a ***pre-release project***. Some of the information and commands may change in the near future

This project implements [OpenRewrite](https://docs.openrewrite.org) recipes on Oracle products.

Each folder of this project contains a group recipe that can contain multiple composite recipes. Each composite recipe is a set of recipes that can be used to migrate a specific version of a product.

Example: `rewrite-weblogic` is a group recipe that contains composite recipes, like the one used to migrate WebLogic 14.1.1 and Java 17. With each new version of WebLogic, a new composite recipe is added, like the migrate to WebLogic 14.1.2 and Java 21 and migrate to WebLogic 14.1.2 and Java 17.

## Getting Started

Before getting started, select which recipe you would like to use. Each recipe has its own README file that explains how to use it.

| Recipe | Description |
| :--- | --- |
| <code><span style="white-space: nowrap;">[rewrite-weblogic](rewrite-weblogic/README.md)</span></code> | Migrate your WebLogic applications to the latest version of WebLogic and Java with OpenRewrite. Automatically. |

> [!TIP]
> Click on the recipe name to get more details about a specific recipe (e.g., `rewrite-weblogic`) and how to use it

## Get Help

We have a closely monitored public Slack channel where you can contact us to ask questions about using WebLogic or give us feedback or suggestions about what features and improvements you would like to see.
We would love to hear from you.

For an invitation to join our public channel, please visit this [site](https://weblogic-slack-inviter.herokuapp.com/). The invitation email will include details of how to access our Slack workspace.

## Contributing

This project welcomes contributions from the community. Before submitting a pull request, please [review our contribution guide](./CONTRIBUTING.md)

## Security

Please consult the [security guide](./SECURITY.md) for our responsible security vulnerability disclosure process

## License

Copyright (c) 2024 Oracle and/or its affiliates.

Released under the Universal Permissive License v1.0 as shown at
<https://oss.oracle.com/licenses/upl/>.
