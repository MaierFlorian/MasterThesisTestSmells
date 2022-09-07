package com.github.MaierFlorian.testsmelldetection.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class CodeParser {

    /**
     * Some if-statements or loops are one-liner, i.e., they have no curved brackets. This method returns the body of such code.
     * @param startPattern the words before the target code, excluding curved brackets. E.g., "if (x > 3)". There should be a curved bracket after this.
     * @param content The code, e.g. a methods body
     * @return List where each element is a line of code. Spaces, tabs and newLines removed.
     */
    public List<String> getBodyOfOneLiners(String startPattern, String content){
        List<String> result = new ArrayList<>();

        List<String> startPatternOccurrences = new ArrayList<>();
        Pattern pat = Pattern.compile(startPattern);
        Matcher m = pat.matcher(content);
        while (m.find()) {
            startPatternOccurrences.add(m.group());
        }

        for(int i = 0; i<startPatternOccurrences.size(); i++) {
            // select everything from the current startPatternOccurrences to the end of the  code
            pat = Pattern.compile(startPatternOccurrences.get(i).replace("{", "\\{").replace("(", "\\(").replace(")", "\\)").replace("[", "\\[").replace("]", "\\]") + "(.*)", Pattern.DOTALL);
            m = pat.matcher(content);
            boolean found = false;
            while (m.find()){
                if(found)
                    break;
                String text = m.group().replace(" ", "").replace("\t", ""). replace("\n", "");
                int startIndex = -1;
                int amountOpenRoundBrackets = 0;
                int amountClosedRoundBrackets = 0;
                boolean isCharAfterRoundBracket = false;
                for(int k = 0; k<text.length(); k++){
                    if (text.charAt(k) == '(')
                        amountOpenRoundBrackets++;
                    if (text.charAt(k) == ')'){
                        amountClosedRoundBrackets++;
                        if(amountOpenRoundBrackets != 0 && amountClosedRoundBrackets == amountOpenRoundBrackets) {
                            isCharAfterRoundBracket = true;
                            continue;
                        }
                    }
                    if(isCharAfterRoundBracket && text.charAt(k) == '{')
                        break;
                    if(isCharAfterRoundBracket && text.charAt(k) != '{' && startIndex == -1)
                        startIndex = k;
                    else if(startIndex != -1 && text.charAt(k) == ';'){
                        result.add(text.substring(startIndex, k+1));
                        content = m.group().substring(pat.toString().length());
                        found = true;
                        break;
                    }
                }
            }
        }

        return result;
    }


    /**
     *
     * @param startPattern the words before the target code, excluding curved brackets. E.g., "if (x > 3)". There should be a curved bracket after this.
     * @param content The code, e.g. a methods body
     * @return List where each element is one or multiple lines of code, including spaces, tabs and newLines.
     */
    public List<String> getCodeBetweenCurvedBrackets(String startPattern, String content){
        List<String> result = new ArrayList<>();

        content = reduceMultipleSpacesToOne(content);

        List<String> startPatternOccurrences = new ArrayList<>();
        Pattern pat = Pattern.compile(startPattern);
        Matcher m = pat.matcher(content);
        while (m.find()) {
            startPatternOccurrences.add(m.group());
        }

        for(int i = 0; i<startPatternOccurrences.size(); i++) {
            // select everything from the current startPatternOccurrences to the end of the  code
            //pat = Pattern.compile(startPatternOccurrences.get(i).replace("{", "\\{").replace("(", "\\(").replace(")", "\\)").replace("[", "\\[").replace("]", "\\]").replace("<", "\\p{Punct}").replace(">", "\\p{Punct}").replace("?", "\\p{Punct}") + "(.|\\n)*+", Pattern.DOTALL);
            pat = Pattern.compile("(" + pat + ")(.|\\n)*+");
            m = pat.matcher(content);
            boolean found = false;
            while (m.find()) {
                if(found)
                    break;
                // check if its not a one-liner (has no curved brackets)
                boolean stop = false;
                String text = m.group().replace(" ", "").replace("\t", ""). replace("\n", "");
                int amountOpenRoundBrackets = 0;
                int amountClosedRoundBrackets = 0;
                boolean isCharAfterRoundBracket = false;
                for(int k = 0; k<text.length(); k++) {
                    if (text.charAt(k) == '(')
                        amountOpenRoundBrackets++;
                    if (text.charAt(k) == ')') {
                        amountClosedRoundBrackets++;
                        if (amountOpenRoundBrackets != 0 && amountClosedRoundBrackets == amountOpenRoundBrackets)
                            isCharAfterRoundBracket = true;
                    }
                    else if (isCharAfterRoundBracket && k+1<text.length() && text.charAt(k+1) == '{') // when "{" found after even amount of round brackets
                        break;
                    else if(amountClosedRoundBrackets == amountOpenRoundBrackets && text.charAt(k) == '{') // if there is a "{" and an even amount of round brackets
                        break;
                }
                if(stop)
                    break;
                // look for the first opening curved bracket and then start counting
                int amountOpeningBrackets = 0;
                int amountClosingBrackets = 0;
                int indexAfterFirstBracket = 0;
                for(int k = 0; k<m.group().length(); k++){
                    if(m.group().charAt(k) == '{') {
                        if(amountOpeningBrackets == 0)
                            indexAfterFirstBracket = k+1;

                        amountOpeningBrackets++;
                    }
                    else if(m.group().charAt(k) == '}') {
                        amountClosingBrackets++;
                        if (amountOpeningBrackets > 0 && amountOpeningBrackets == amountClosingBrackets) {
                            result.add(m.group().substring(indexAfterFirstBracket, k));
                            content = m.group().substring(indexAfterFirstBracket);
                            found = true;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    public String removeStringsInsideQuotationMarks(String content){
        Pattern p = Pattern.compile("(\".*?\")|('.*?')");
        //Pattern p = Pattern.compile("(\"(.*?)\")|('(.*?)')");
        Matcher m = p.matcher(content);
        while (m.find()) {
            content = content.replace(m.group(0), "");
        }
        return content;
    }

    public String removeUnwantedCharacters(String content){
        // strips off all non-ASCII characters
        content = content.replaceAll("[^\\x00-\\x7F]", "");

        // erases all the ASCII control characters
        content = content.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

        // removes non-printable characters from Unicode
        content = content.replaceAll("\\p{C}", "");

        return content.trim();
    }

    public String reduceMultipleSpacesToOne(String content){
        return content.trim().replaceAll(" +", " ");
    }

    public int countStringInString(String content, String searchString){
        int amount = 0;
        String temp[] = content.replace("(", " ").split("\\p{Blank}");
        for(int i=0; i<temp.length; i++){
            if(temp[i].toLowerCase().equals(searchString.toLowerCase()))
                amount += 1;
        }
        return amount;
    }

}
