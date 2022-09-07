package com.github.MaierFlorian.testsmelldetection.parsing;

import com.github.MaierFlorian.testsmelldetection.data.Method;
import com.github.MaierFlorian.testsmelldetection.data.Statistics;
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
        long startTime = System.nanoTime();

        List<Method> methods = new ArrayList<>();

        String content = new TextFromFileExtractor().getFileContentWithoutComments(path).replace("\r", "");
        String contentForGettingMethodDeclarations = new TextFromFileExtractor().getFileContentWithoutComments(path).replace("\r", "");

        List<String> methodDeclarations = new ArrayList<>();
        // remove all method bodies, so that we also remove inner methods
        while(true) {
            int oldLength = contentForGettingMethodDeclarations.length();
            List<String> methodBodies = new CodeParser().getCodeBetweenCurvedBrackets("((@Test)?((\\n)|(\\s)|(\\t))*(((public|protected|private|static) +[\\w\\<\\>\\[\\]]+)|((public|protected|private|static)? *void))\\s+(\\w+) *\\([^\\)]*\\)[\\s\\n]*(\\{?|[^;]))", contentForGettingMethodDeclarations);
            methodBodies.sort((s1, s2) -> s2.length() - s1.length());
            for (String s : methodBodies)
                contentForGettingMethodDeclarations = contentForGettingMethodDeclarations.replace(s, "");
            if(contentForGettingMethodDeclarations.length() == oldLength)
                break;
        }
        // select all methodDeclarations
        Pattern pat = Pattern.compile("((@Test)?((\\n)|(\\s)|(\\t))*(((public|protected|private|static) +[\\w\\<\\>\\[\\]]+)|((public|protected|private|static)? *void))\\s+(\\w+) *\\([^\\)]*\\)[\\s\\n]*(\\{?|[^;]))");
        Matcher m = pat.matcher(contentForGettingMethodDeclarations);
        while (m.find()) {
            methodDeclarations.add(m.group());
        }
        for(int i = 0; i<methodDeclarations.size(); i++) {
            Method newMethod = new Method();
            newMethod.setMethodDeclaration(methodDeclarations.get(i));
            newMethod.setMethodName(getMethodName(methodDeclarations.get(i)));
            CodeParser cp = new CodeParser();
            newMethod.setMethodBody(cp.getCodeBetweenCurvedBrackets("(?<![:] *)(" + getMethodName(methodDeclarations.get(i)) + " *[(][^;]*?)(?=[\\s\\n]*+[{])" ,content).get(0));
            boolean alreadyInList = false;
            for(Method me : methods){
                if(me.getMethodName().equals(newMethod.getMethodName())) {
                    alreadyInList = true;
                    break;
                }
            }
            if(!alreadyInList)
                methods.add(newMethod);
        }

        long stopTime = System.nanoTime();
        Statistics.getInstance().addTimeReadingFiles(stopTime - startTime);

        if(!methods.isEmpty())
            ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] Successfully extracted methods from " + path);
        else
            ControllerFromOutside.writeToLog("[ERROR - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] No methods found in " + path);
        return methods;
    }

    public String getMethodBodyWithoutInnerMethods(String methodBody){
        CodeParser cp = new CodeParser();
        List<String> innerMethods = cp.getCodeBetweenCurvedBrackets("@Override", methodBody);
        for(String s : innerMethods){
            methodBody = methodBody.replace(s, "");
        }
        return methodBody;
    }

    private String getMethodName(String methodDeclaration){
        Pattern pat = Pattern.compile("\s([a-zA-z\\d_]*)(?=[(])");
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

    public List<Method> getHelperMethods(Method testMethod, List<Method> allMethods){
        List<Method> helperMethods = new ArrayList<>();
        List<Method> candidates = getNonTestMethods(allMethods);
        // check if the methodName of a candidate appears in the body of the testMethod
        for(Method candidate : candidates){
            if((testMethod.getMethodBody().contains(candidate.getMethodName() + "(") || testMethod.getMethodBody().contains(candidate.getMethodName() + " (")) && !helperMethods.contains(candidate))
                helperMethods.add(candidate);
        }
        return helperMethods;
    }

}
