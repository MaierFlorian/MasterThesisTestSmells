package com.github.MaierFlorian.testsmelldetection.testSmells;

import com.github.MaierFlorian.testsmelldetection.data.Configuration;
import com.github.MaierFlorian.testsmelldetection.data.Method;
import com.github.MaierFlorian.testsmelldetection.data.Statistics;
import com.github.MaierFlorian.testsmelldetection.parsing.MethodExtractor;
import com.github.MaierFlorian.testsmelldetection.parsing.CodeParser;
import com.github.MaierFlorian.testsmelldetection.util.ControllerFromOutside;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionalTestLogicDetector {

    /**
     * Looks for conditional test logic (following statements: if, switch, for, while) inside a java file.
     * @param path Path pointing to the java file.
     * @return a List of "method" objects. Such an object includes e.g. the method-name, method-body, and how many loops it contains.
     */
    public List<Method> detectCTLinFile(List<Method> methods, String path){
        ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] Start to detect CTL in " + path);

        for(Method method : methods) {
            long startTime = System.nanoTime();
            method.setAmountIfAndSwitch(findIfAndSwitchStatements(method) + findTryStatements(method));
            method.setAmountLoops(findLoops(method));
            long stopTime = System.nanoTime();
            Statistics.getInstance().addTimeToDetectCTL(stopTime - startTime);
            ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] " + method.getAmountIfAndSwitch() + " if or switch statements found in Method '" + method.getMethodName() + "'");
            ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] " + method.getAmountLoops() + " loops found in Method '" + method.getMethodName() + "'");
        }

        ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] Finished detecting CTL in " + path);
        return methods;
    }

    /**
     * Finds all 'if' and 'switch' statements inside a methods-body using a regular expression.
     * If enabled in the settings, 'if' statements containing only print-statements (beginning with 'System.out.print') do not count.
     * @param method
     * @return the amount of 'if' and 'switch' statements combined.
     */
    private int findIfAndSwitchStatements(Method method){
        int found = 0;
        Pattern pat = Pattern.compile("(\n)(\s|\t)*((if)|(switch))(\s)*(?=[(])");
        Matcher m = pat.matcher(method.getMethodBody());
        while (m.find()) {
            found++;
        }
        if(found > 0 && Configuration.getInstance().isOnlyPrint()){
            List<String> ifBodies = new CodeParser().getCodeBetweenCurvedBrackets("(\n)(\s|\t)*((if)|(switch))(\s)*(?=[(])", method.getMethodBody());
            List<String> ifOneLinersBodies = new CodeParser().getBodyOfOneLiners("(\n)(\s|\t)*((if)|(switch))(\s)*(?=[(])", method.getMethodBody());
            for(String s : ifOneLinersBodies)
                ifBodies.add(s);
            boolean onlyContainsPrints = false;
            for(String s : ifBodies) {
                Scanner scanner = new Scanner(s);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if(line.isBlank())
                        continue;
                    if(line.endsWith(";") && line.replace(" ", "").replace("\t", ""). replace("\n", "").toLowerCase().startsWith("system.out.print")){
                        onlyContainsPrints = true;
                    }
                    else{
                        onlyContainsPrints = false;
                    }
                }
                if(onlyContainsPrints)
                    found--;
                scanner.close();
            }
            if(onlyContainsPrints) {
                found = 0;
                ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] Method '" + method.getMethodName() + "' only contains 'if'/'switch' statements containing only 'print' statements.");
            }
        }
        return found;
    }

    private int findTryStatements(Method method){
        int found = 0;
        Pattern pat = Pattern.compile("(\n)(\s|\t)*((try))(\s)*([(].*)?(?=[{])");
        Matcher m = pat.matcher(method.getMethodBody());
        while (m.find()) {
            found++;
        }
        return found;
    }

    /**
     * Finds all 'for' and 'while' loops inside a methods-body using a regular expression.
     * If enabled in the settings, 'for' and 'while' statements containing only print-statements (beginning with 'System.out.print') do not count.
     * @param method
     * @return the amount of 'for' and 'while' statements combined.
     */
    private int findLoops(Method method){
        int found = 0;
        Pattern pat = Pattern.compile("(\n)(\s|\t)*((while)|(for))(\s)*(?=[(])");
        Matcher m = pat.matcher(method.getMethodBody());
        while (m.find()) {
            found++;
        }
        if(found > 0 && Configuration.getInstance().isOnlyPrint()){
            List<String> ifBodies = new CodeParser().getCodeBetweenCurvedBrackets("(\n)(\s|\t)*((while)|(for))(\s)*(?=[(])", method.getMethodBody());
            List<String> ifOneLinersBodies = new CodeParser().getBodyOfOneLiners("(\n)(\s|\t)*((while)|(for))(\s)*(?=[(])", method.getMethodBody());
            for(String s : ifOneLinersBodies)
                ifBodies.add(s);
            boolean onlyContainsPrints = false;
            for(String s : ifBodies) {
                Scanner scanner = new Scanner(s);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if(line.isBlank())
                        continue;
                    if(line.replace(" ", "").replace("\t", ""). replace("\n", "").toLowerCase().startsWith("system.out.print")){
                        onlyContainsPrints = true;
                    }
                    else{
                        onlyContainsPrints = false;
                    }
                }
                if(onlyContainsPrints)
                    found--;
                scanner.close();
            }
            if(onlyContainsPrints) {
                found = 0;
                ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] Method '" + method.getMethodName() + "' only contains 'for'/'while' loops containing only 'print' statements.");
            }
        }
        return found;
    }

}
