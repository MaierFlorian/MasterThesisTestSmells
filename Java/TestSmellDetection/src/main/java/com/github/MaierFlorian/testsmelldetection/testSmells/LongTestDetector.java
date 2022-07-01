package com.github.MaierFlorian.testsmelldetection.testSmells;

import com.github.MaierFlorian.testsmelldetection.data.Method;
import com.github.MaierFlorian.testsmelldetection.parsing.CodeParser;
import com.github.MaierFlorian.testsmelldetection.parsing.MethodExtractor;
import com.github.MaierFlorian.testsmelldetection.util.ControllerFromOutside;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LongTestDetector {

    public List<Method> detectLongTestsInFile(String path){
        ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] Start to detect LT in " + path);

        List<Method> methods = new MethodExtractor().extractMethodsFromFile(path);

        for(Method method : methods) {
            method.setAmountLines(countStatementsInMethod(method));
            ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] " + method.getAmountLines() + " statements found in Method '" + method.getMethodName() + "'");
        }

        ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] Finished detecting LT in " + path);
        return methods;
    }

    private int countStatementsInMethod(Method method){
        String content = method.getMethodBody();
        CodeParser cp = new CodeParser();
        content = cp.removeStringsInsideQuotationMarks(content); // remove all strings inside quotation marks so we can actually filter for keywords later

        List<String> keywords = new ArrayList<>(Arrays.asList("catch", "do", "else", "finally", "for", "if", "switch", "try", "while"));

        int amountSemicolons = content.length() - content.replace(";", "").length(); // https://stackoverflow.com/questions/275944/how-do-i-count-the-number-of-occurrences-of-a-char-in-a-string

        String temp[] = content.replace("(", " ").split(" ");
        int amountKeywords = 0;
        for(int i=0; i<temp.length; i++){
            if(keywords.contains(temp[i].toLowerCase()))
                amountKeywords++;
        }

        return amountSemicolons + amountKeywords;
    }

}
