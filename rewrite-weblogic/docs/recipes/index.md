# Rewrite WebLogic recipes

Look here for detailed information about each of the following Rewrite WebLogic composite recipes (recipes that consist of a list of other recipes). Depending on your application dependencies and WebLogic Server and JDK source versions, you can use one or more recipes to accomplish your goals.

For detailed information about running recipes, see [How to run recipes](../procedures/index.md) and the [WebLogic Examples and Tutorials](https://github.com/oracle-samples/weblogic-examples) repository.

> [!NOTE]
> These recipes do not address _any_ other third-party dependencies. For all other third-party dependencies, you _must_ find or create an additional [recipe](https://docs.openrewrite.org/recipes) to do the migration, or make manual code changes after the migration.

| Composite Recipes | Descriptions |
| --- | --- |
| [Migrate to WebLogic 15.1.1](migrate-to-weblogic-151100.md) | This recipe will update the WebLogic version to 15.1.1 for the Maven build and comment out deprecated and removed APIs. </br> **NOTE**: The UpgradeTo1511 recipe must be run **after** the Java and Jakarta recipes. |
| [Migrate to WebLogic 14.1.2](migrate-to-weblogic-141200.md) | This recipe will upgrade the WebLogic version to 14.1.2 for the Maven build, and comment out deprecated and removed APIs. |
| [Migrate to WebLogic 14.1.1](migrate-to-weblogic-141100.md) | This recipe will update the WebLogic version to 14.1.1 for the Maven build and will apply changes commonly needed when upgrading to Java 11. |
| [Migrate to Jakarta EE 9.1](migrate-to-jakarta-EE-9_1.md) | This recipe will update Jakarta EE platform dependencies to 9.1.0, and flag and update deprecated methods.  |
| [Migrate to Jakarta EE 9.1 Namespaces](migrate-to-jakarta-EE-9_1-namespaces.md) | This recipe will migrate `javax` packages to `jakarta` EE 9.1.0. |
| [Migrate From JSF 2.x to Jakarta Server Faces 3.x](jakarta-server-faces-3x.md) | This recipe will find and replace legacy JSF namespaces and `javax` references with Jakarta Server Faces values. |
| [Migrate From JSF 1.x to Jakarta Server Faces 2.3 on WebLogic 14.1.2 or older](jakarta-server-faces-2x.md) | This recipe will upgrade PrimeFaces, OmniFaces, and MyFaces libraries to Jakarta EE9 versions. |
| [Migrate to Spring Framework 6.2 for WebLogic 15.1.1](migrate-to-spring-6_2.md) | This recipe migrates applications to the Spring Framework 6.2 release and to compatibility with WebLogic 15.1.1. </br> **NOTE**: This recipe is intended for migration from Spring Framework version 5.3 or later. We have not tested and we don’t support updating from earlier versions of Spring Framework.|
| [Migrate to Hibernate for Jakarta EE 9](migrate-to-hibernate.md) | This recipe upgrades Hibernate libraries to versions compatible with Jakarta EE 9. |
| [Identify and Comment Out Deprecations](identify-deprecations.md) | This recipe will identify, and comment out deprecated and removed APIs. |
