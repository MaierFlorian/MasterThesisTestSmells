package com.github.MaierFlorian.testsmelldetection.testSmells;

import com.github.MaierFlorian.testsmelldetection.data.Method;
import com.github.MaierFlorian.testsmelldetection.data.Statistics;
import com.github.MaierFlorian.testsmelldetection.parsing.CodeParser;
import com.github.MaierFlorian.testsmelldetection.parsing.MethodExtractor;
import com.github.MaierFlorian.testsmelldetection.util.ControllerFromOutside;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssertionRouletteDetector {

    /**
     * Looks for JUnit Assertions ("fail" method and methods beginning with "assert") inside a Java file.
     * May not only cover JUnit Assertions but also custom Assertions if they follow the pattern.
     * @param path Path pointing to the java file.
     * @return a List of "method" objects. Such an object includes e.g. the method-name, method-body, and how many assertions it contains.
     */
    public List<Method> detectARinFile(List<Method> allMethods, String path){
        ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] Start to detect AR in " + path);

        MethodExtractor me = new MethodExtractor();
        List<Method> methods = me.getTestMethods(allMethods);

        for(Method method : methods) {
            long startTime = System.nanoTime();
            method.setAmountAssertions(countAssertionsInMethod(method, allMethods, null));
            long stopTime = System.nanoTime();
            Statistics.getInstance().addTimeToCountAssertions(stopTime - startTime);
            ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] " + method.getAmountAssertions() + " JUnit Assertions found in Method '" + method.getMethodName() + "'");
        }

        ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] Finished detecting AR in " + path);
        return methods;
    }

    /**
     * Iterative method to count all Assertions inside the method and all helper methods.
     * @param method
     * @param allMethods all methods inside this file
     * @param visitedHelperMethods should be null
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
                // count how often this helper method is called
                int amountCallsOfHelperMethod = cp.countStringInString(method.getMethodBody(), helperMethod.getMethodName());
                if (amountCallsOfHelperMethod < 1)
                    amountCallsOfHelperMethod = 1; // cant be 0
                if(helperMethod.getMethodName().toLowerCase().startsWith("assert"))
                    found -= amountCallsOfHelperMethod;
                // increase found variable by amount of assertions inside helper method multiplied by the amount of calls of that helper method
                found += countAssertionsInMethod(helperMethod, allMethods, visitedHelperMethods) * amountCallsOfHelperMethod;
            }
        }
        return found;
    }

}
