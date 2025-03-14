# How to run recipes

Depending on your application dependencies and WebLogic Server and JDK source versions, you can use one or more recipes to accomplish your goals. Recipes are stackable meaning that you can run more than one recipe at a time, within a single command or Maven configuration. Recipes are run in order.

You can run Rewrite WebLogic recipes on your code folder using the Maven or Gradle CLI, or include them as a build plug-in in your `pom.xml` file. After running recipes, you are notified of all of the files that have been changed. To see what's changed in the code, run `git diff` or use your preferred IDE's diff viewer. From there, you can commit the changes or add additional recipes based on your needs.

> [!TIP]  
> When using Maven to run the recipe, you need the current version of the WebLogic Maven Plug-in (for example, 14.1.1). After migrating (for example, to 15.1.1), then you need to install the 15.1.1 plug-in version (and sync) to be able to build, compile, or package the migrated application. 

# Procedures

The following procedures illustrate the methods to upgrade your applications, WebLogic Server, and Java versions:

- [Upgrade to WebLogic Server 14.1.2](upgrade-141200.md)
- [Upgrade to WebLogic Server 15.1.1 (BETA)](upgrade-151100.md)
- [Upgrade to WebLogic Server 15.1.1 with Spring Framework](https://github.com/oracle-samples/weblogic-examples/blob/main/tutorials/migrate/spring-framework-petclinic-15.1.1/README.md)
