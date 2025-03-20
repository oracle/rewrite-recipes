# What is WebLogic Server application upgrade tooling?

WebLogic Server application upgrade tooling makes it easy for you to upgrade your WebLogic Server applications to a new WebLogic version and an updated JDK. WebLogic Server application upgrade tooling employs [Rewrite WebLogic](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/README.md#recipes) recipes that perform all the functions you need to successfully upgrade your applications. Rewrite WebLogic recipes automate application upgrades, such as the changes needed for WebLogic version upgrades, JDK upgrades, Jakarta EE upgrades, and Spring Framework upgrades. Application upgrades can be complex. By using automated code updates, you reduce the risk of human error.

Rewrite WebLogic is part of a larger, [OpenRewrite](https://github.com/openrewrite/rewrite) project, a popular open source code refactoring automation framework. There you'll find lots of community recipes, recipes that are customizable and extensible for your own use. Recipes from Oracle are supported by Oracle.

## How it works

[OpenRewrite](https://docs.openrewrite.org/) scans your source code and builds an in-memory [Lossless Semantic Tree](https://docs.openrewrite.org/concepts-and-explanations/lossless-semantic-trees) (LST) representation of all of the application structures. LSTs offer a unique set of characteristics that make it possible to perform accurate transformations and searches across a repository.

OpenRewite employs an auto-refactoring engine that works by makes changes to the LSTs that represent your source code. Each LST retains the required information and formatting of your original code. The application code refactoring logic is stored in recipes. Transformation by OpenRewrite recipes makes minimally invasive changes to your source code and prints the modified trees back into the source code. The formatting is preserved; the whitespace is preserved. You can review the changes to your code and, if satisfied, commit the results.   

## About recipes

OpenRewrite [recipes](https://docs.openrewrite.org/concepts-and-explanations/recipes) comprise search and refactoring operations that can be applied to LSTs. A recipe can represent a single, standalone operation or it can be linked together with other recipes to accomplish a larger goal. WebLogic Rewrite recipes are composite recipes (recipes that consist of a list of other recipes). Recipes can be further composable - that is, a composite recipe can include composite recipes. In addition, recipes are stackable meaning that you can run more than one recipe at a time, within a single command or Maven configuration; the recipes are run in order.

The rewrite process driven by Maven or Gradle. You call [Rewrite WebLogic recipes](../../docs/recipes/index.md) on your code folder using the Maven or Gradle CLI, or by including them as a build plug-in in your `pom.xml` file. Recipes are activated, marked as `activeRecipes`, on the command line or in the `pom.xml` file, then you trigger running the activated recipe with the appropriate Maven or Gradle `run` command.

For example:
```
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \ <- Command to launch OpenRewrite tool

  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:RELEASE,com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST \ <- Recipe source

  -Drewrite.activeRecipes=,org.openrewrite.java.migrate.UpgradeToJava21,com.oracle.weblogic.rewrite.JakartaEE9_1,com.oracle.weblogic.rewrite.UpgradeTo1511 \ <- Recipe list
  -Drewrite.exportDatatables=true
```
For more information, see [How to run recipes](../../docs/procedures/index.md).

## Troubleshooting

Here are some issues associated with OpenRewrite recipes that you may encounter in the WebLogic Server 15.1.1.0.0 beta release.

**Issue** </br>
WebLogic OpenRewrite recipes do not support the transformation of descriptors that use DTD references.
<!-- Bug #37639800 -->

**Issue** </br>
To migrate Spring Framework applications to WebLogic Server version 15.1.1.0.0 (BETA) and Jakarta EE 9.1, using either JDK17 or JDK21, you should have:
* Spring Framework version 5.3 or later. Though not supported, older versions of Spring Framework may work.
* A supported Hibernate version for the Spring version. Make sure that the Spring Framework application compiles and runs.
* Run both Hibernate and Spring recipes together.
<!-- Bug #37642918 -->

**Issue** </br>
For application migration to be successful, all dependencies, including fourth-party dependencies, must be included in the `pom.xml` file, _before_ migration.
<!-- Bugs #37644007, #37638545 , #37642398 -->

**Issue** </br>
The `weblogic open-rewrite` command fails to run on a multi-module Maven project when dependent modules are delayed in running. The issue is solved by specifying the module dependencies using the `--projects` option. For example:
```
mvn -U
   ...
   --projects <module_project> --also-make
```
For more information, see the [Guide to Working with Multiple Subprojects in Maven 4](https://maven.apache.org/guides/mini/guide-multiple-subprojects-4.html).
<!-- Bug #37638183 -->
