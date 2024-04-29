# My implementation of a simple code metrics engine


## 1. Installation

1. Clone the source repository
2. Open in IntelliJ Idea and mark the src folder as a source folder.
3. Also make sure that the files in the src/main/resources are not compiled by intelliJ Idea. To do this, go into the Build,Execution, Deployment of Intellij Idea and select Compiler. Then in the Excludes tab ,add the respective folder as in the image. ![grafik](https://github.com/stefantaga24/SimpleCodeChecker/assets/145774127/e46ff59c-7e70-4348-905b-9c441bbdf045)

## 2. Short introduction 

My application takes all the java files from the directory src/main/resources and outputs to the console the percentage of method names that are not named with the camelCase convention. Then it also outputs the names of the worst three functions in terms of their complexity score and their complexity score after. To calculate the complexity score I counted the number of **if**, **while**, **for**, **switch**, **forEach** and **doWhile** statements. 

## 3. Approach

To be able to complete this project I used the JavaParser opensource project in order to get a parsed Tree of the java file. As I had no prior knowledge of the library, I downloaded the book that explained it and looked through it. This enabled to learn more about their Visitor class, which helped me write the **MethodNameParser** class in order to get all the method names in a java file. As for the part with the complexity score, I was inspired by their example on the how to use JavaSymbolExtractor where they did modifications on all if statements that had a certain property and I was able to find the "findAll" method.

## 4. Design decisions

I decided only to evaluate java files as it is the language that I have the most experience with. However, with a good parser for Kotlin this could be extended to those type of files as well. As honesty is the best policy, this solution is limited by the parser that I am using as it does not cover new functionalities implemented in java 20. However, the solution is valid for java files written with a java version of up to 18. Last but not least, for the method of getting the best 3 functions , I implemented the solution with an array as it was the shortest to write. However, a more efficient one would have been with three variables Max1, Max2 and Max3 , modification which can be made pretty quickly.
