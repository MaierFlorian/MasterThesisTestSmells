package com.github.MaierFlorian.testsmelldetection.testSmells;

import com.github.MaierFlorian.testsmelldetection.data.Method;
import com.github.MaierFlorian.testsmelldetection.data.Statistics;
import com.github.MaierFlorian.testsmelldetection.parsing.CodeParser;
import com.github.MaierFlorian.testsmelldetection.parsing.MethodExtractor;
import com.github.MaierFlorian.testsmelldetection.util.ControllerFromOutside;
import com.github.MaierFlorian.testsmelldetection.util.Tuple;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RottenGreenTestDetector {

    public List<Method> detectRGTInFile(List<Method> allMethods, String path){
        ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] Start to detect RGT in " + path);

        MethodExtractor me = new MethodExtractor();
        List<Method> methods = me.getTestMethods(allMethods);

        for(Method method : methods){
            long startTime = System.nanoTime();
            Tuple<Integer, Integer> assertionsInCTL = checkAssertionsInMethod(method, allMethods, null);
            method.setAmountAssertionsInsideCTL(assertionsInCTL.x);
            method.setHelperMethodsInsideCTL(assertionsInCTL.y);
            method.setAssertionAfterReturn(isAssertionAfterReturn(method, allMethods, null));
            long stopTime = System.nanoTime();
            Statistics.getInstance().addTimeToDetectRGT(stopTime - startTime);
            if(method.getAmountAssertions() < 1) // if not already set be AssertionRouletteDetector
                method.setAmountAssertions(countAssertionsInMethod(method, allMethods, null));
        }

        ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] Finished detecting RGT in " + path);
        return methods;
    }

    private boolean isAssertionAfterReturn(Method method, List<Method> allMethods, List<Method> visitedHelperMethods){
        if(visitedHelperMethods == null)
            visitedHelperMethods = new ArrayList<>();
        boolean assertionAfterReturn = false;
        Pattern pat = Pattern.compile("(?:return(\\s(\\w)+)?;)(.(?<!(assert|fail))|\\n|\\s|\\t)*(?:((([a|A]ssert)\\w*)|([f|F]ail))(\\s)*(?=[(]))");
        MethodExtractor me = new MethodExtractor();
        String content = me.getMethodBodyWithoutInnerMethods(method.getMethodBody());
        CodeParser cp = new CodeParser();
        content = cp.removeStringsInsideQuotationMarks(content);
        Matcher m = pat.matcher(content);
        while (m.find()){
            assertionAfterReturn = true;
            break;
        }
        if(assertionAfterReturn)
            return true;
        else{ // if there is no assert-statement after a return-statement in the current method, check helper methods
            // go through helper methods if there are any
            List<Method> helperMethods = new MethodExtractor().getHelperMethods(method, allMethods);
            for(Method helperMethod : helperMethods){
                if(helperMethod.getMethodName().equals(method.getMethodName()) || visitedHelperMethods.contains(helperMethod)) // to avoid endless loop if a method calls itself
                    continue;
                else{
                    visitedHelperMethods.add(helperMethod);
                    if(isAssertionAfterReturn(helperMethod, allMethods, visitedHelperMethods)) {
                        assertionAfterReturn = true;
                        break;
                    }
                }
            }
        }
        return assertionAfterReturn;
    }

    /**
     * Iterative method to check how many assert-statements there are inside conditional test logic. Includes Helper-tests.
     * @param method where to check for assertions inside CTL
     * @param allMethods all methods inside the current file
     * @param visitedHelperMethods should be null
     * @return number of assertions inside CTL, including helper methods. And number of helper method calls inside CTL.
     */
    private Tuple<Integer, Integer> checkAssertionsInMethod(Method method, List<Method> allMethods, List<Method> visitedHelperMethods){
        if(visitedHelperMethods == null)
            visitedHelperMethods = new ArrayList<>();
        Pattern pat = Pattern.compile("(?:[a|A]ssert[A-Za-z]*+)|(?:[f|F]ail)(?:\s)?(?=[(])");
        String content = method.getMethodBody();
        CodeParser cp = new CodeParser();
        content = cp.removeStringsInsideQuotationMarks(content);
        List<String> ifBodies = new CodeParser().getCodeBetweenCurvedBrackets("[\\n\\s\\t]*+(if)|(switch)\\s?+(?=[(])", content);
        List<String> ifOneLiners = new CodeParser().getBodyOfOneLiners("[\\n\\s\\t]*+((if)|(switch))\\s?+(?=[(])", content);
        for(String s : ifOneLiners)
            ifBodies.add(s);
        List<String> tryCatchBodies = new CodeParser().getCodeBetweenCurvedBrackets("[\\n\\s\\t]*+(?:try)|(?:catch\\s?+[(].+[)])\\s?(?=[{])", content);
        List<String> loopBodies = new CodeParser().getCodeBetweenCurvedBrackets("[\\n\\s\\t]*+(?:while)|(?:for)\\s?+(?=[(])", content);
        List<String> loopOneLiners = new CodeParser().getBodyOfOneLiners("[\\n\\s\\t]*+((?:while)|(?:for))\\s?+(?=[(])", content);
        for(String s : loopOneLiners)
            loopBodies.add(s);

        int assertionsFoundInCTL = 0;
        for(String s : ifBodies){
            Matcher m = pat.matcher(s);
            while (m.find()) {
                assertionsFoundInCTL++;
            }
        }
        for(String s : tryCatchBodies){
            Matcher m = pat.matcher(s);
            while (m.find()) {
                assertionsFoundInCTL++;
            }
        }
        for(String s : loopBodies){
            Matcher m = pat.matcher(s);
            while (m.find()) {
                assertionsFoundInCTL++;
            }
        }

        // helper methods including assertions
        List<Method> helperMethodsWithAssertions = new ArrayList<>();
        // go through helper methods if there are any
        List<Method> helperMethods = new MethodExtractor().getHelperMethods(method, allMethods);
        for(Method helperMethod : helperMethods){
            if(helperMethod.getMethodName().equals(method.getMethodName()) || visitedHelperMethods.contains(helperMethod)) // to avoid endless loop if a method calls itself
                continue;
            else{
                visitedHelperMethods.add(helperMethod);
                assertionsFoundInCTL += checkAssertionsInMethod(helperMethod, allMethods, visitedHelperMethods).x;
                if(countAssertionsInMethod(helperMethod, allMethods, null) > 0 && !helperMethodsWithAssertions.contains(helperMethod))
                    helperMethodsWithAssertions.add(helperMethod);
            }
        }

        int helperMethodsInsideCTL = checkHelperMethodsInsideCTL(method, helperMethodsWithAssertions, ifBodies, tryCatchBodies, loopBodies);

        return new Tuple<>(assertionsFoundInCTL, helperMethodsInsideCTL);
    }

    /**
     * Counts helper methods that appear inside CTL in a test method.
     * @param method the test method
     * @param helperMethodsWithAssertions a list of helper methods
     * @param ifBodies List of Strings where each element is the code inside if-blocks
     * @param tryCatchBodies List of Strings where each element is the code inside try-catch-blocks
     * @param loopBodies List of Strings where each element is the code inside a loop
     * @return
     */
    private int checkHelperMethodsInsideCTL(Method method, List<Method> helperMethodsWithAssertions, List<String> ifBodies, List<String> tryCatchBodies, List<String> loopBodies) {
        int helperMethodsInsideCTL = 0;
        for(Method hp : helperMethodsWithAssertions) {
            for (String s : ifBodies) {
                if (s.contains(hp.getMethodName()))
                    helperMethodsInsideCTL++;
            }
            for (String s : tryCatchBodies) {
                if (s.toLowerCase().contains(hp.getMethodName()))
                    helperMethodsInsideCTL++;
            }
            for (String s : loopBodies) {
                if (s.toLowerCase().contains(hp.getMethodName()))
                    helperMethodsInsideCTL++;
            }
        }
        return helperMethodsInsideCTL;
    }

    /**
     * Iterative method to count all Assertions inside the method and all helper methods.
     * @param method
     * @param visitedHelperMethods should be null
     * @param allMethods all methods inside the current file
     * @return
     */
    private int countAssertionsInMethod(Method method, List<Method> allMethods, List<Method> visitedHelperMethods){
        if(visitedHelperMethods == null)
            visitedHelperMethods = new ArrayList<>();
        int found = 0;
        Pattern pat = Pattern.compile("((([a|A]ssert)(\\w*))|([f|F]ail))(\\s)*(?=[(])");
        String content = method.getMethodBody();
        CodeParser cp = new CodeParser();
        content = cp.removeStringsInsideQuotationMarks(content);
        Matcher m = pat.matcher(content);
        while (m.find()) {
            found++;
        }
        // go through helper methods if there are any
        List<Method> helperMethods = new MethodExtractor().getHelperMethods(method, allMethods);
        for(Method helperMethod : helperMethods){
            if(helperMethod.getMethodName().equals(method.getMethodName()) || visitedHelperMethods.contains(helperMethod)) // to avoid endless loop if a method calls itself
                continue;
            else {
                visitedHelperMethods.add(helperMethod);
                if(helperMethod.getMethodName().toLowerCase().startsWith("assert"))
                    found--;
                found += countAssertionsInMethod(helperMethod, allMethods, visitedHelperMethods);
            }
        }
        return found;
    }

}
