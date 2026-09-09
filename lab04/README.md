# Lab 04 - Maven and JavaFX

## Requirements
JDK 23 and Maven 3.9.x. Run the commands below from this folder.

## Exercise 1: Maven dependencies
The application uses JavaFX Controls 23 and the JavaFX Maven plugin 0.0.8.
Its entry point is `edu.psu.se411.lab04.Main`.

```cmd
mvn clean javafx:run
```

A window titled **SE411 Lab 04** displays **SE411 Lab 04 - Maven and JavaFX**.
Close the window to finish the Maven command.

## Exercise 2: Project documentation
The POM includes project, license, organization, and developer metadata.
The site includes the overview, summary, dependencies, license, and team reports.

```cmd
mvn clean site
start target\site\index.html
```

The generated site stays in `target/site` locally and is ignored by Git.
To compile and package separately, run `mvn clean package`.

The agreed lab artifact is `lab04`, with package `edu.psu.se411.lab04`.
The handout's main-project/team naming can be adapted when a course team project is selected.
