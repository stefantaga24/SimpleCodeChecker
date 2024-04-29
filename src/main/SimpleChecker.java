package main;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class SimpleChecker {
    private static final String directoryPath = "src/main/resources";

    private static final String camelCasePattern = "^[a-z]+([A-Z][a-z0-9]+)+";

    public static void main(String[] args) throws IOException {
        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
        File folder = new File(directoryPath);
        evaluateFileOrFolder(folder);
    }

    /**
     * Takes a given file or a given folder and evaluates code metrics for all the java
     * files inside
     * @param file The given file or folder to evaluate
     * @throws IOException If a I/O exception happens
     */
    private static void evaluateFileOrFolder(File file) throws IOException
    {
        if (file.isFile())
        {
            int nameLength = file.getName().length();

            // We only check java files
            if (nameLength< 5 || !file.getName().substring(nameLength-4).equals("java"))
                return;

            printPercentageOfIncorrectMethodNames(file);
            List<Pair<String,Long>> worstThreeMethods =
                    new ArrayList<>();
            printThreeWorstMethods(file, worstThreeMethods);
            return;
        }
        File[] listOfFiles = file.listFiles();
        if (listOfFiles!= null)
        {
            for (File currentFile : listOfFiles)
            {
                evaluateFileOrFolder(currentFile);
            }
        }
    }

    /**
     * Utilitary function that takes a given file and outputs the names of the
     * three worst methods in terms of complexity score and their complexity score
     * @param file The given file
     * @throws IOException if an I/O occurs due to the Files.newInputStream function
     */
    private static void printThreeWorstMethods(File file,
                                               List<Pair<String, Long>> worstThreeMethods)
                                                            throws IOException {
        final String path = directoryPath+"/"+ file.getName();
        CompilationUnit cu = StaticJavaParser.parse(
                Files.newInputStream(Paths.get(path)));
        // Counting the methods that have the worst complexity score
        // Taking into account all the statements that might have an important impact
        cu.findAll(MethodDeclaration.class).forEach(method->{
            long complexityScore =
                    method.findAll(Statement.class, x ->
                            x.getClass().equals(ForStmt.class)||
                            x.getClass().equals(IfStmt.class)||
                            x.getClass().equals(WhileStmt.class)||
                            x.getClass().equals(SwitchStmt.class)||
                            x.getClass().equals(DoStmt.class) ||
                            x.getClass().equals(ForEachStmt.class)).size();
            Pair<String,Long> currentMethodStats =
                    new Pair<>(method.getNameAsString(), complexityScore);
            updateThreeWorstMethods(worstThreeMethods,
                    currentMethodStats);
        });
        if (worstThreeMethods.isEmpty())
        {
            System.out.println("File " + file.getName() + "has no methods");
            return;
        }
        System.out.println("In file " + file.getName() +
                " the worst three methods in term of " +
                "their complexity score are:");
        worstThreeMethods.forEach(method -> {
            System.out.println("Method " + method.a + " with complexity score "+ method.b);
        });
    }

    /**
     * Utilitary function in which I update the worst three methods found
     * in terms of complexity score
     * @param worstThreeMethods The current worst three methods in terms of
     *                          complexity score
     *                          (the first one being the worst one)
     * @param methodToAdd The new method found in the parsed tree of the file
     */
    private static void updateThreeWorstMethods(List <Pair<String,Long>> worstThreeMethods,
                         Pair<String,Long> methodToAdd)
    {
        if (worstThreeMethods.size()<3)
        {
            worstThreeMethods.add(methodToAdd);
            return;
        }
        for (int i=0;i<3;i++)
        {
            // Comparing the complexity scores of the function with the ith one
            if (worstThreeMethods.get(i).b < methodToAdd.b)
            {
                // If it has a bigger complexity score than one of the elements from the list
                // We add it and then remove the last one from the list
                worstThreeMethods.add(i,methodToAdd);
                worstThreeMethods.remove(worstThreeMethods.size()-1);
                return;
            }
        }
    }

    /**
     * Utilitary function that takes a given file and outputs the percentage of
     * method names that do not match the camelCase convention.
     * @param file The given file
     * @throws IOException if an I/O occurs due to the Files.newInputStream function
     */
    private static void printPercentageOfIncorrectMethodNames(File file) throws IOException {
        final String path = directoryPath+"/"+ file.getName();

        CompilationUnit cu = StaticJavaParser.parse(
                Files.newInputStream(Paths.get(path)));

        List<String> methodNames = new ArrayList<>();

        VoidVisitor<List<String>> methodNameVisitor = new MethodNameCollector();

        methodNameVisitor.visit(cu, methodNames);

        if (methodNames.isEmpty())
        {
            System.out.println("File " + file.getName() +" has a " +
                    0 + "% of incorrect method names");
            return;
        }
        long totalIncorrectNamedMethods = methodNames.stream()
                .filter(methodName-> !methodName.matches(camelCasePattern)).count();

        double percentageOfIncorrectNamedMethods =
                (double)totalIncorrectNamedMethods/methodNames.size()*100;

        System.out.println("File " + file.getName() +" has a " +
                percentageOfIncorrectNamedMethods + "% of incorrect method names");
    }
}
