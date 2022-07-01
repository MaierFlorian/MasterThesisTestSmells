package com.github.MaierFlorian.testsmelldetection.parsing;

import com.github.MaierFlorian.testsmelldetection.data.Method;
import com.github.MaierFlorian.testsmelldetection.util.ControllerFromOutside;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodExtractor {

    /**
     * Extracts methods from a java file and returns a list containing these methods. Additionally, it extracts three components:
     * 1) the methodDeclaration (including Annotations), 2) the name of the method (which is part of the methodDeclaration), and 3) the methods body (content).
     * The body of the last method in the file may contain additional content at the end (such as the closing bracket '}' from the class).
     * @param path The path to the java file.
     * @return A list of "method" objects.
     */
    public List<Method> extractMethodsFromFile(String path){
        List<Method> methods = new ArrayList<>();

        String content = new TextFromFileExtractor().getFileContentWithoutComments(path);

        List<String> methodDeclarations = new ArrayList<>();
        // select all methodDeclarations
        Pattern pat = Pattern.compile("((@Test)?((\\n)|(\\s)|(\\t))*(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\) *(\\{?|[^;]))");
        Matcher m = pat.matcher(content);
        while (m.find()) {
            methodDeclarations.add(m.group());
        }
        for(int i = 0; i<methodDeclarations.size(); i++) {
            // select everything from one methodDeclaration to the next methodDeclaration (excluding)
            if((i+1)<methodDeclarations.size()) {
                pat = Pattern.compile(methodDeclarations.get(i).replace("{", "\\{").replace("(", "\\(").replace(")", "\\)").replace("[", "\\[").replace("]", "\\]") +"(.*)(?=" + methodDeclarations.get(i+1).replace("{", "\\{").replace("(", "\\(").replace(")", "\\)").replace("[", "\\[").replace("]", "\\]") + ")", Pattern.DOTALL);
                m = pat.matcher(content);
                while (m.find()) {
                    Method newMethod = new Method();
                    newMethod.setMethodDeclaration(methodDeclarations.get(i));
                    newMethod.setMethodName(getMethodName(methodDeclarations.get(i)));
                    newMethod.setMethodBody(m.group(1));
                    methods.add(newMethod);
                }
            }
            // if its the last methodDeclaration in the file, select everything after the methodDeclaration
            else{
                pat = Pattern.compile("(" + methodDeclarations.get(i).replace("{", "\\{").replace("(", "\\(").replace(")", "\\)").replace("[", "\\[").replace("]", "\\]") +")(.*)", Pattern.DOTALL);
                m = pat.matcher(content);
                while (m.find()) {
                    Method newMethod = new Method();
                    newMethod.setMethodDeclaration(methodDeclarations.get(i));
                    newMethod.setMethodName(getMethodName(methodDeclarations.get(i)));
                    newMethod.setMethodBody(m.group(2));
                    methods.add(newMethod);
                }
            }
        }
        if(!methods.isEmpty())
            ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] Successfully extracted methods from " + path);
        else
            ControllerFromOutside.writeToLog("[ERROR - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] No methods found in " + path);
        return methods;
    }

    private String getMethodName(String methodDeclaration){
        Pattern pat = Pattern.compile("\s([a-zA-z0-9]*)(?=[(])");
        Matcher m = pat.matcher(methodDeclaration);
        while (m.find()) {
            return m.group().replace(" ", "");
        }
        return null;
    }

    /**
     * From a given list of "method" objects, it returns only the methods whose methodDeclarations start with the annotation '@Test'.
     * @param methods A list of "method" objects.
     * @return a list of "method" objects whose methodDeclarations start with the annotation '@Test'.
     */
    public List<Method> getTestMethods(List<Method> methods){
        List<Method> testMethods = new ArrayList<>();
        for(Method m : methods){
            if(m.getMethodDeclaration().startsWith("@Test"))
                testMethods.add(m);
        }
        return testMethods;
    }

    /**
     * From a given list of "method" objects, it returns only the methods whose methodDeclarations do not start with the annotation '@Test'.
     * This method might be helpful to find helper methods.
     * @param methods A list of "method" objects.
     * @return a list of "method" objects whose methodDeclarations do not start with the annotation '@Test'.
     */
    public List<Method> getNonTestMethods(List<Method> methods){
        List<Method> nonTestMethods = new ArrayList<>();
        for(Method m : methods){
            if(!m.getMethodDeclaration().startsWith("@Test"))
                nonTestMethods.add(m);
        }
        return nonTestMethods;
    }

}
