- To use the Synthetic Data Generator in your Java code, see the file "Example.java" located in solorio.dataGenerators package.

Building and running Maven Project from Command Line

1. #From  the main directory of the project (i.e., where the pom.xml file is), compile the project and generate target folder.
    mvn compile
    Note: If changes are made to the code, mvn compile needs to be executed again before calling exec.

2. #Builds the maven project, cleans the target/ folder, and installs it into local maven repository.
    mvn clean install

3. In order to execute the Examples.java class, and in general, any main class generated in the target folder, the  Maven's exec plugin can be used as follows.
    mvn exec:java -Dexec.mainClass=solorio.dataGenerators.Examples
    Note: You must run the previous command from the main directory of the project, that is, where the pom.xml file is located

4. To run the Graphical User Interface (GUI), execute the SyntheticMDG.java file located in solorio.gui package with the following command.
    mvn exec:java -Dexec.mainClass=solorio.gui.SyntheticMDG