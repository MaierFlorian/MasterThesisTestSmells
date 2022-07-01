package com.github.MaierFlorian.testsmelldetection.logic;

import com.github.MaierFlorian.testsmelldetection.data.Configuration;
import com.github.MaierFlorian.testsmelldetection.data.Method;
import com.github.MaierFlorian.testsmelldetection.parsing.MethodExtractor;
import com.github.MaierFlorian.testsmelldetection.testSmells.AnonymousTestDetector;
import com.github.MaierFlorian.testsmelldetection.testSmells.ConditionalTestLogicDetector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.github.MaierFlorian.testsmelldetection.testSmells.LongTestDetector;
import com.github.MaierFlorian.testsmelldetection.util.ControllerFromOutside;
import com.opencsv.*;

public class SmellDetector {

    // Singleton

    private static SmellDetector instance = new SmellDetector();

    private SmellDetector(){
        instance = this;
    }

    public static SmellDetector getInstance(){
        return instance;
    }

    //##################################

    private List<String> csv_header;
    private List<List<String>> rows;

    /**
     * Executes all test smell detection algorithms selected by the user and saves the results into a list which is later used to create a csv file.
     */
    public void executeSmellDetector(){
        csv_header = createCsvContent();

        rows = new ArrayList<>();

        for(File file : Configuration.getInstance().getFiles()) {
            // Create row for each method in file
            for (Method m : new MethodExtractor().extractMethodsFromFile(file.getAbsolutePath())){
                List<String> row = createEmptyRow(csv_header);
                row.set(csv_header.indexOf("Path"), file.getAbsolutePath());
                row.set(csv_header.indexOf("Method"), m.getMethodName());
                if(m.getMethodDeclaration().replace(" ", "").replace("\t", "").replace("\n", "").startsWith("@Test"))
                    row.set(csv_header.indexOf("isTestMethod"), "yes");
                rows.add(row);
            }
            if(Configuration.getInstance().isAnonymousTest()){
                List<Method> result = new AnonymousTestDetector().detectAnonymousTestsInFile(file.getAbsolutePath());
                for(Method m : result){
                    List<String> row = getRowOfMethod(m, file.getAbsolutePath());
//                    if(m.getAnonymousTestRating() == 0)
//                        row.set(csv_header.indexOf("Anonymous Test"), "0");
//                    else if(m.getAnonymousTestRating() == 1)
//                        row.set(csv_header.indexOf("Anonymous Test"), "1");
//                    else if(m.getAnonymousTestRating() == 2)
//                        row.set(csv_header.indexOf("Anonymous Test"), "2");
                    row.set(csv_header.indexOf("Anonymous Test"), String.valueOf(m.getAnonymousTestRating()));
                    if(Configuration.getInstance().isTest_methodname())
                        row.set(csv_header.indexOf("Pattern: test_methodName"), String.valueOf(m.getTest_methodname()));
                    if(Configuration.getInstance().isMethod_state_behaviour())
                        row.set(csv_header.indexOf("Pattern: method_state_behaviour"), String.valueOf(m.getMethod_state_behaviour()));
                    if(Configuration.getInstance().isSpecificRequirement())
                        row.set(csv_header.indexOf("Pattern: specific_requirement"), String.valueOf(m.getSpecificRequirement()));
                    if(Configuration.getInstance().isInputStateWorkflow_output())
                        row.set(csv_header.indexOf("Pattern: inputStateWorkflow_output"), String.valueOf(m.getInputStateWorkflow_output()));
                    if(Configuration.getInstance().isAction_conditionState())
                        row.set(csv_header.indexOf("Pattern: action_conditionState"), String.valueOf(m.getAction_conditionState()));
                }
            }
            if(Configuration.getInstance().isConditionalTestLogic()){
                List<Method> result = new ConditionalTestLogicDetector().detectCTLinFile(file.getAbsolutePath());
                for(Method m : result){
                    List<String> row = getRowOfMethod(m, file.getAbsolutePath());
                    if(m.getAmountIfAndSwitch() > 0 || m.getAmountLoops() > 0)
                        row.set(csv_header.indexOf("Conditional Test Logic"), "1");
                    else
                        row.set(csv_header.indexOf("Conditional Test Logic"), "0");
                    row.set(csv_header.indexOf("#ifSwitch"), String.valueOf(m.getAmountIfAndSwitch()));
                    row.set(csv_header.indexOf("#loops"), String.valueOf(m.getAmountLoops()));
                }
            }
            if(Configuration.getInstance().isLongTest()){
                List<Method> result = new LongTestDetector().detectLongTestsInFile(file.getAbsolutePath());
                for(Method m : result){
                    List<String> row = getRowOfMethod(m, file.getAbsolutePath());
                    if(m.getAmountLines() > -1 && m.getAmountLines() <= Configuration.getInstance().getNumberLines())
                        row.set(csv_header.indexOf("Long Test"), "0");
                    else if (m.getAmountLines() > -1 && m.getAmountLines() > Configuration.getInstance().getNumberLines())
                        row.set(csv_header.indexOf("Long Test"), "1");
                    row.set(csv_header.indexOf("#statements"), String.valueOf(m.getAmountLines()));
                }
            }
        }

        ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] Finished! Result can now be downloaded as CSV file.");
        ControllerFromOutside.enableDownloadButtonAndResetButton();
    }

    /**
     * Creates a csv file containing all the results of the test smell detection algorithms.
     * It is necessary to previously execute the method executeSmellDetector(). Else this method might fail.
     * @param path where the csv file should be saved.
     */
    public void createCSV(String path){
        File file = new File(path + File.separator + "results.csv");
        try {
            FileWriter outputfile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputfile);
            writer.writeNext(csv_header.toArray(new String[csv_header.size()]));
            for(List<String> row : rows)
                writer.writeNext(row.toArray(new String[row.size()]));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> createEmptyRow(List<String> header){
        List<String> row = new ArrayList<>();
        for(int i=0; i<header.size(); i++)
            row.add("-");
        return row;
    }

    private List<String> createCsvContent(){
        List<String> csv_header = new ArrayList<>();
        csv_header.add("Path");
        csv_header.add("Method");
        csv_header.add("isTestMethod");

        if(Configuration.getInstance().isAnonymousTest()){
            csv_header.add("Anonymous Test");
            if(Configuration.getInstance().isTest_methodname())
                csv_header.add("Pattern: test_methodName");
            if(Configuration.getInstance().isMethod_state_behaviour())
                csv_header.add("Pattern: method_state_behaviour");
            if(Configuration.getInstance().isSpecificRequirement())
                csv_header.add("Pattern: specific_requirement");
            if(Configuration.getInstance().isInputStateWorkflow_output())
                csv_header.add("Pattern: inputStateWorkflow_output");
            if(Configuration.getInstance().isAction_conditionState())
                csv_header.add("Pattern: action_conditionState");
        }
        if(Configuration.getInstance().isLongTest()){
            csv_header.add("Long Test");
            csv_header.add("#statements");
        }
        if(Configuration.getInstance().isConditionalTestLogic()){
            csv_header.add("Conditional Test Logic");
            csv_header.add("#ifSwitch");
            csv_header.add("#loops");
        }
        if(Configuration.getInstance().isRottenGreenTest()){
            csv_header.add("Rotten Green Test");
        }
        if(Configuration.getInstance().isAssertionRoulette()){
            csv_header.add("Assertion Roulette");
            csv_header.add("#assertions");
        }
        if(Configuration.getInstance().isEagerTest()){
            csv_header.add("Eager Test");
        }
        if(Configuration.getInstance().isLazyTest()){
            csv_header.add("Lazy Test");
        }

        return csv_header;
    }

    // TODO: if there are multiple methods with the same methodName in the same java file, there will be a wrong output.
    private List<String> getRowOfMethod(Method method, String path){
        for(List<String> row : rows){
            if(row.get(csv_header.indexOf("Method")).equals(method.getMethodName()) && row.get(csv_header.indexOf("Path")).equals(path))
                return row;
        }
        return null;
    }

}
