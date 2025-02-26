# What is WebLogic Server application upgrade tooling?

WebLogic Server application upgrade tooling makes it easy for you to upgrade your WebLogic Server applications to a new WebLogic version and an updated JDK. WebLogic Server application upgrade tooling employs [Rewrite WebLogic](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/README.md#recipes) recipes that perform all the functions you need to successfully upgrade your applications. Rewrite WebLogic is part of a larger, [OpenRewrite](https://github.com/openrewrite/rewrite) project, in which you'll find lots of community recipes; recipes that are customizable and extensible for your own use. Recipes from Oracle are supported by Oracle.

## About OpenRewrite

[OpenRewrite](https://docs.openrewrite.org/) creates a special representation of your source code called a [Lossless Semantic Tree](https://docs.openrewrite.org/concepts-and-explanations/lossless-semantic-trees) (LST). LSTs offer a unique set of characteristics that make it possible to perform accurate transformations and searches across a repository.

OpenRewite employs an auto-refactoring engine that works by makes changes to the LSTs that represent your source code. Each LST retains the required information and formatting of your original code. Transformation by OpenRewrite recipes make minimally invasive changes to your source code and prints the modified trees back into the source code. Then, you can review the changes in your code and, if satisfied, commit the results.   

## How it works

OpenRewrite [recipes](https://docs.openrewrite.org/concepts-and-explanations/recipes) comprise search and refactoring operations that can be applied to LSTs. A recipe can represent a single, standalone operation or it can be linked together with other recipes to accomplish a larger goal. WebLogic Rewrite recipes are composite recipes (recipes that consists of a list of other recipes). Recipes can be further composable - that is, a composite recipe can include composite recipes. In addition, recipes are stackable meaning that you can run more than one recipe at a time, within a single command or Maven configuration; the recipes are run in order.

You call [Rewrite WebLogic recipes](../recipes/index.md) on your code folder using the Maven or Gradle CLI, or by including them as a build plug-in in your `pom.xml` file. Recipes are activated, marked as `activeRecipes`, on the command line or in the `pom.xml` file. You trigger running the activated recipe with the appropriate Maven or Gradle `run` command.

You can use one or more recipes to accomplish your goals. Recipes are stackable, meaning that you can run more than one recipe at a time, within a single command or Maven configuration. The active recipes are run in order.


## Troubleshooting
