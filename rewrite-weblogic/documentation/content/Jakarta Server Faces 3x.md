# Migrate From JSF 2.x to Jakarta Server Faces 3.x
**com.oracle.weblogic.rewrite.Faces2xMigrationToJakarta3x**

Jakarta EE 9 uses Jakarta Server Faces 3.0, a major upgrade to Jakarta packages and XML namespaces. This recipe applies the changes required for migrating from JSF 2.x to Jakarta Server Faces 3.x.

### Tags:
- jakarta
- jakartaee
- faces
- jsf

### Recipe Source

[jakarta-faces-3.yaml](https://github.com/oracle/rewrite-recipes/blob/main/rewrite-weblogic/src/main/resources/META-INF/rewrite/jakarta-faces-3.yaml)

### Recipe List:

- [org.openrewrite.java.migrate.jakarta.JakartaFacesEcmaScript](https://docs.openrewrite.org/recipes/java/migrate/jakarta/jakartafacesecmascript)
- [org.openrewrite.java.migrate.jakarta.RemovedUIComponentConstant](https://docs.openrewrite.org/recipes/java/migrate/jakarta/removeduicomponentconstant)
- com.oracle.weblogic.rewrite.jakarta.JakartaFaces3WebXml
- com.oracle.weblogic.rewrite.jakarta.JakartaFaces3Xhtml
- com.oracle.weblogic.rewrite.jakarta.JavaxFacesConfigXmlToJakartaFaces3ConfigXml
- com.oracle.weblogic.rewrite.jakarta.JavaxFacesTagLibraryXmlToJakartaFaces3TagLibraryXml
- com.oracle.weblogic.rewrite.jakarta.JavaxWebFragmentXmlToJakartaWebFragmentXml5
- com.oracle.weblogic.rewrite.jakarta.JavaxWebXmlToJakartaWebXml5
- com.oracle.weblogic.rewrite.jakarta.FacesJNDINamesChanged3
- com.oracle.weblogic.rewrite.jakarta.RemovedJakartaFaces3ExpressionLanguageClasses
- com.oracle.weblogic.rewrite.jakarta.RemovedJakartaFaces3ResourceResolver
- com.oracle.weblogic.rewrite.jakarta.RemovedStateManagerMethods3
- com.oracle.weblogic.rewrite.jakarta.FacesManagedBeansRemoved3
- com.oracle.weblogic.rewrite.jakarta.UpgradeFacesOpenSourceLibraries3

### Usage

This recipe will find and replace legacy JSF namespaces and `javax` references with Jakarta Server Faces values.

#### Maven POM

1. Add the following to your Maven POM file.
```
<plugin>
    <groupId>org.openrewrite.maven</groupId>
    <artifactId>rewrite-maven-plugin</artifactId>
    <version>5.43.0</version>
    <configuration>
        <exportDatatables>true</exportDatatables>
        <activeRecipes>
            <recipe>com.oracle.weblogic.rewrite.Faces2xMigrationToJakarta3x</recipe>
        </activeRecipes>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>com.oracle.weblogic.rewrite</groupId>
            <artifactId>rewrite-weblogic</artifactId>
            <version>0.4.2</version>
        </dependency>
        <dependency>
            <groupId>org.openrewrite.recipe</groupId>
            <artifactId>rewrite-migrate-java</artifactId>
            <version>2.28.0</version>
        </dependency>
    </dependencies>
</plugin>
...
```
2. Run `mvn rewrite:run` to run the recipe.

#### Maven Command Line

**NOTE**: You will need to have [Maven](https://maven.apache.org/download.cgi) installed on your machine before you can run the following command.

```
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=com.oracle.weblogic.rewrite:rewrite-weblogic:LATEST \
  -Drewrite.activeRecipes=com.oracle.weblogic.rewrite.Faces2xMigrationToJakarta3x \
  -Drewrite.exportDatatables=true
  ```
