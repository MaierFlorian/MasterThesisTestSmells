package com.github.MaierFlorian.testsmelldetection.testSmells;

import com.github.MaierFlorian.testsmelldetection.data.Configuration;
import com.github.MaierFlorian.testsmelldetection.data.Method;
import com.github.MaierFlorian.testsmelldetection.data.Statistics;
import com.github.MaierFlorian.testsmelldetection.nlp.MethodNameAnalyser;
import com.github.MaierFlorian.testsmelldetection.util.ControllerFromOutside;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class AnonymousTestDetector {

    public List<Method> detectAnonymousTestsInFile(List<Method> methods, String path){
        ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] Start to detect Anonymous Tests in " + path);

        for(Method m : methods){
            long startTime = System.nanoTime();
            m.setAnonymousTestRating(analyseMethodName(m));
            long stopTime = System.nanoTime();
            Statistics.getInstance().addTimeToDetectAT(stopTime - startTime);
            ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] Method '" + m.getMethodName() + "' got an Anonymous-Test-Rating of " + m.getAnonymousTestRating() + ".");
        }

        ControllerFromOutside.writeToLog("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] Finished detecting Anonymous Tests in " + path);
        return methods;
    }

    private int analyseMethodName(Method method){
        // 0 = good name, 1 = could be better / fits a combination of guidelines, 2 = is bad, 3 = not defined
        // The rating can not increase. If it is set to 0 once (= follows a guideline), it can not undo that.
        int bestRating = 3;
        String methodName = method.getMethodName();

        // if the methodName is just "test" + optional a number
        if(methodName.toLowerCase().matches("(\\s|\\t)*(test)(\\d)*"))
            return 2;

        MethodNameAnalyser analyser = new MethodNameAnalyser();
        List<String> wordsInName = analyser.splitCamelCase(methodName);
        HashMap<String, String> posTags = analyser.posTagging(wordsInName);

        // check pattern: "test" + methodName
        if(Configuration.getInstance().isTest_methodname()){
            method.setTest_methodname(2);
            int rating = rateTestMethodName(method, methodName, wordsInName);
            method.setTest_methodname(rating);
            bestRating = rating < bestRating ? rating : bestRating;
        }
        /*
        check pattern in any order: methodName + state + behaviour
        A State can be a preposition + noun, or an adjective.
        The behaviour is what we expect, i.e. the output/result.
         */
        if(Configuration.getInstance().isMethod_state_behaviour()){
            method.setMethod_state_behaviour(2);
            int rating = rateMethod_state_behaviour(method, wordsInName, posTags);
            method.setMethod_state_behaviour(rating);
            bestRating = rating < bestRating ? rating : bestRating;
        }
        /*
        check for the pattern: express a specific requirement
         */
        if(Configuration.getInstance().isSpecificRequirement()){
            method.setSpecificRequirement(2);
            int rating = rateSpecificRequirement(method, wordsInName, posTags);
            method.setSpecificRequirement(rating);
            bestRating = rating < bestRating ? rating : bestRating;
        }
        /*
        checks for the pattern: action + condition/state
         */
        if(Configuration.getInstance().isAction_conditionState()){
            method.setAction_conditionState(2);
            int rating = rateActionConditionState(method, wordsInName, posTags);
            method.setAction_conditionState(rating);
            bestRating = rating < bestRating ? rating : bestRating;
        }
        /*
        checks for the pattern: input/state/workflow + output
         */
        if(Configuration.getInstance().isInputStateWorkflow_output()){
            method.setInputStateWorkflow_output(2);
            int rating = rateInputStateWorkflowOutput(method, wordsInName, posTags);
            method.setInputStateWorkflow_output(rating);
            bestRating = rating < bestRating ? rating : bestRating;
        }

        return bestRating;
    }

    private int rateTestMethodName(Method method, String methodName, List<String> wordsInName) {
        int rating = 2;
        // checks if the methodName starts with "test". If yes, check if the second part of the methodName is a method called in the methods body.
        if(methodName.toLowerCase().startsWith("test") && checkIfStringIsMethodInBody(method, methodName.substring(4))){
            rating = 0;
        }
        // if the methodName starts with "test" but the second part is not a method called in the methods body, check if the other words in the name at least appear in the body.
        else if(methodName.toLowerCase().startsWith("test") && checkIfStringsAreInBody(method, wordsInName)){
            rating = 1;
        }
        // checks if the methodName ends with "test". If yes, check if the first part of the methodName is a method called in the methods body. If yes, give a rating of 1, because it does not perfectly fit the pattern.
        else if(methodName.toLowerCase().endsWith("test") && checkIfStringIsMethodInBody(method, methodName.substring(0, methodName.length()-4))){
            rating = 1;
        }
        else {
            rating = 2;
        }
        return rating;
    }

    private int rateInputStateWorkflowOutput(Method method, List<String> wordsInName, HashMap<String, String> posTags) {
        int rating = 2;
        List<String> words = getWordsWithoutMethodNameAndKeywords(method, wordsInName, new ArrayList<>(Arrays.asList("test")));
        if(words.isEmpty()){
            if(rating >= 2) {
                rating = 2;
            }
        }
        // check for some keywords
        boolean hasInput = false;
        boolean hasOutput = false;
        boolean maybeWorkflow = false;
        if(words.contains("input") || words.contains("in")){
            hasInput = true;
        }
        if(words.contains("output") || words.contains("out") || words.contains("result") || words.contains("results") || words.contains("produces") || words.contains("yields") || words.contains("return") || words.contains("returns") || words.contains("false") || words.contains("true")){
            hasOutput = true;
        }
        if(words.contains("and") || words.contains("then") || words.contains("first") || words.contains("then") || words.contains("after") || words.contains("before")){
            maybeWorkflow = true;
        }
        List<List<String>> meanings = getMeaningsOfWords(words, posTags);
        boolean hasAction = false; // for workflow checking
        boolean hasState = false; // for state checking
        for(List<String> meaning : meanings){
            if(meaning.contains("action"))
                hasAction = true;
            else if(meaning.contains("state"))
                hasState = true;
        }
        if(!hasOutput){
            if(rating >= 2) {
                rating = 2;
            }
        }
        else{
            if(hasInput) {
                rating = 0;
            }
            if(hasAction && maybeWorkflow) {
                rating = 0;
            }
            if(hasState) {
                rating = 0;
            }
        }
        return rating;
    }

    private int rateActionConditionState(Method method, List<String> wordsInName, HashMap<String, String> posTags) {
        int rating = 2;
        List<String> words = getWordsWithoutMethodNameAndKeywords(method, wordsInName, new ArrayList<>(Arrays.asList("test")));
        if(words.isEmpty()){
            if(rating >= 2) {
                rating = 2;
            }
        }
        List<List<String>> meanings = getMeaningsOfWords(words, posTags);
        boolean hasAction = false;
        boolean hasCondition = false;
        boolean hasHalfCondition = false;
        boolean hasState = false;
        for(List<String> meaning : meanings){
            if(meaning.contains("action"))
                hasAction = true;
            else if(meaning.contains("condition"))
                hasCondition = true;
            else if(meaning.contains("state"))
                hasState = true;
            else if(meaning.contains("half-condition"))
                hasHalfCondition = true;
        }
        if(!hasAction){
            if(rating >= 2) {
                rating = 2;
            }
        }
        else{
            if(hasCondition) {
                rating = 0;
            }
            else if (hasState) {
                rating = 0;
            }
            else if (hasHalfCondition){
                if(rating >= 1) {
                    rating = 1;
                }
            }
        }
        return rating;
    }

    private int rateSpecificRequirement(Method method, List<String> wordsInName, HashMap<String, String> posTags) {
        int rating = 2;
        List<String> words = getWordsWithoutMethodNameAndKeywords(method, wordsInName, null);
        if(words.isEmpty()){
            if(rating >= 2) {
                rating = 2;
            }
        }
        // check for some keywords that express a requirement. Probably not really needed since covered by pos
        if(words.contains("if") || words.contains("unless") || words.contains("with") || words.contains("without") || words.contains("condition") ||
                words.contains("requires") || words.contains("requirement")){
            rating = 0;
        }
        List<List<String>> meanings = getMeaningsOfWords(words, posTags);
        // check if 'condition' is part of meanings
        boolean isCondition = false;
        boolean isHalfCondition = false; // not sure if it really expresses a requirement
        for(List<String> meaning : meanings){
            if(meaning.contains("condition")) {
                isCondition = true;
                break;
            }
            else if(meaning.contains("half-condition")){
                isHalfCondition = true;
            }
        }
        if(isCondition) {
            rating = 0;
        }
        else if (isHalfCondition){
            if(rating >= 1) {
                rating = 1;
            }
        }
        return rating;
    }

    private int rateMethod_state_behaviour(Method method, List<String> wordsInName, HashMap<String, String> posTags){
        int rating = 2;
        List<String> name = checkIfWordsCombinedAreMethod(method, wordsInName);
        if(name.isEmpty()) { // if there is no methodName in it, it can not fulfill this pattern
            if (rating >= 2) {
                rating = 2;
            }
        }
        else {
            List<String> remainingWords = new ArrayList<String>(wordsInName);
            remainingWords.removeAll(name); // remove all words which are part of the methodName
            remainingWords.remove("test"); // remove 'test' keyword since it is not relevant for this pattern
            // check if certain keywords are in the list, e.g., "input", "output", "result", ...
            List<String> keywords = new ArrayList<>();
            List<String> kw = new ArrayList<>(Arrays.asList("input", "output", "result", "in", "out", "true", "false"));
            for(String s : remainingWords){
                if(kw.contains(s.toLowerCase())){
                    keywords.add(s);
                }
            }
            remainingWords.removeAll(keywords);
            List<List<String>> meanings = getMeaningsOfWords(remainingWords, posTags);
            // check which meanings are present
            int amountAction = 0;
            int amountStates = 0;
            int amountObjects = 0;
            int amountConditions = 0;
            int amountResults = 0;
            for(List<String> meaning : meanings){
                if(meaning.contains("condition"))
                    amountConditions++;
                else if(meaning.contains("state"))
                    amountStates++;
                else if(meaning.contains("object"))
                    amountObjects++;
                else if(meaning.contains("result"))
                    amountResults++;
                else if(meaning.contains("verb"))
                    amountAction++;
            }
            // TODO: maybe improve the following lines
            if((amountStates + amountObjects + amountConditions >= 2 && amountAction >= 1) || (!keywords.isEmpty() && amountStates + amountObjects + amountConditions >= 2) ||
                    (amountResults >= 1 && amountStates + amountObjects + amountConditions >= 2)) {
                rating = 0;
            }
            else if ((amountStates + amountObjects + amountConditions >= 1 && amountAction >= 1) || (!keywords.isEmpty() && amountStates + amountObjects + amountConditions >= 1) ||
                    (amountResults >= 1 && amountStates + amountObjects + amountConditions >= 1)) {
                if (rating >= 1) {
                    rating = 1;
                }
            }
            else {
                if (rating >= 2) {
                    rating = 2;
                }
            }
        }
        return rating;
    }

    /**
     * Remove all words which are part of the target-methodName and remove keywords (e.g., 'test'). Example:
     * 'testSomeMethodInBodyInputStringOutputTrue' -> 'InputStringOutputTrue'
     * @param method
     * @param wordsInName all words of the methodName (e.g., 'testMethod' -> 'test', 'method')
     * @param keywords words that will be removes if present (e.g., 'test'). Can be null.
     * @return
     */
    private List<String> getWordsWithoutMethodNameAndKeywords(Method method, List<String> wordsInName, List<String> keywords){
        List<String> remainingWords = new ArrayList<String>(wordsInName);
        List<String> name = checkIfWordsCombinedAreMethod(method, wordsInName);
        remainingWords.removeAll(name); // remove all words which are part of the methodName
        if(keywords != null)
            remainingWords.removeAll(keywords); // remove all keywords
        return remainingWords;
    }

    private List<List<String>> getMeaningsOfWords(List<String> words, HashMap<String, String> posTags){
        // check with pos tags
        List<String> pos = new ArrayList<>();
        for(String s : words)
            pos.add(posTags.get(s));
        // get meanings for each pos
        List<List<String>> meanings = new ArrayList<>();
        for(String p : pos){
            List<String> meaning = checkPosMeaning(p);
            if(!(meaning.contains("") && meaning.size() == 1)) // if the list contains a meaning
                meanings.add(meaning);
        }
        return meanings;
    }

    /**
     * @param pos A POS tag, e.g., "NN" or "CC"
     * @return a list of possible meanings, e.g., an adjective describes something. Therefore, it can be a requirement, condition, ...
     */
    private List<String> checkPosMeaning(String pos){
        HashMap<String, List<String>> meanings = new HashMap<>();
        meanings.put("CC", new ArrayList<>(Arrays.asList(""))); // coordinating conjunction
        meanings.put("CD", new ArrayList<>(Arrays.asList(""))); // cardinal digit
        meanings.put("DT", new ArrayList<>(Arrays.asList(""))); // determiner
        meanings.put("EX", new ArrayList<>(Arrays.asList(""))); // existential there
        meanings.put("FW", new ArrayList<>(Arrays.asList(""))); // foreign word
        meanings.put("IN", new ArrayList<>(Arrays.asList("condition"))); // preposition/subordinating conjunction
        meanings.put("JJ", new ArrayList<>(Arrays.asList("condition"))); // This NLTK POS Tag is an adjective (large)
        meanings.put("JJR", new ArrayList<>(Arrays.asList("condition"))); // adjective, comparative (larger)
        meanings.put("JJS", new ArrayList<>(Arrays.asList("condition"))); // adjective, superlative (largest)
        meanings.put("LS", new ArrayList<>(Arrays.asList(""))); // list market
        meanings.put("MD", new ArrayList<>(Arrays.asList("result"))); // modal (could, will)
        meanings.put("NN", new ArrayList<>(Arrays.asList("state", "object"))); // noun, singular (cat, tree)
        meanings.put("NNS", new ArrayList<>(Arrays.asList("state", "object"))); // noun plural (desks)
        meanings.put("NNP", new ArrayList<>(Arrays.asList("object"))); // proper noun, singular (sarah)
        meanings.put("NNPS", new ArrayList<>(Arrays.asList("object"))); // proper noun, plural (indians or americans)
        meanings.put("PDT", new ArrayList<>(Arrays.asList("condition"))); // predeterminer (all, both, half)
        meanings.put("POS", new ArrayList<>(Arrays.asList("half-condition"))); // possessive ending (parent\ ‘s) // half-condition = not sure if it really expresses a requirement
        meanings.put("PRP", new ArrayList<>(Arrays.asList("half-condition"))); // personal pronoun (hers, herself, him, himself)
        meanings.put("PRP$", new ArrayList<>(Arrays.asList("half-condition"))); // possessive pronoun (her, his, mine, my, our )
        meanings.put("RB", new ArrayList<>(Arrays.asList("condition"))); // adverb (occasionally, swiftly)
        meanings.put("RBR", new ArrayList<>(Arrays.asList("condition"))); // adverb, comparative (greater)
        meanings.put("RBS", new ArrayList<>(Arrays.asList("condition"))); // adverb, superlative (biggest)
        meanings.put("RP", new ArrayList<>(Arrays.asList(""))); // particle (about)
        meanings.put("TO", new ArrayList<>(Arrays.asList(""))); // infinite marker (to)
        meanings.put("UH", new ArrayList<>(Arrays.asList(""))); // interjection (goodbye)
        meanings.put("VB", new ArrayList<>(Arrays.asList("action"))); // verb (ask)
        meanings.put("VBG", new ArrayList<>(Arrays.asList("action"))); // verb gerund (judging)
        meanings.put("VBD", new ArrayList<>(Arrays.asList("action"))); // verb past tense (pleaded)
        meanings.put("VBN", new ArrayList<>(Arrays.asList("action"))); // verb past participle (reunified)
        meanings.put("VBP", new ArrayList<>(Arrays.asList("action"))); // verb, present tense not 3rd person singular(wrap)
        meanings.put("VBZ", new ArrayList<>(Arrays.asList("action"))); // verb, present tense with 3rd person singular (bases)
        meanings.put("WDT", new ArrayList<>(Arrays.asList(""))); // wh-determiner (that, what)
        meanings.put("WP", new ArrayList<>(Arrays.asList(""))); // wh- pronoun (who)
        meanings.put("WRB", new ArrayList<>(Arrays.asList(""))); // wh- adverb (how)
        meanings.put(".", new ArrayList<>(Arrays.asList("")));
        return meanings.get(pos);
    }

    /**
     * Check if words combined represent a method that is called in the body of the current method.
     * Example:
     * words = "example", "method", "Output", "True".
     * And there is a method called "exampleMethod". Then the words "example" and "method" will be returned.
     * @param method the body of this method will be investigated
     * @param words
     * @return the words that form a methodName
     */
    private List<String> checkIfWordsCombinedAreMethod(Method method, List<String> words){
        List<String> result = new ArrayList<>();
        for(int i=0; i<words.size(); i++){
            if(checkIfStringIsMethodInBody(method, words.get(i))){
                result.add(words.get(i));
                break;
            }
            if((i+1)<words.size()) {
                String combination = words.get(i);
                for (int j = i + 1; j < words.size(); j++) {
                    combination = combination + words.get(j);
                    if(checkIfStringIsMethodInBody(method, combination)){
                        for(int k=i; k<j; k++){
                            result.add(words.get(k));
                        }
                    }
                }
            }
        }
        return result;
    }

    private boolean checkIfStringsAreInBody(Method method, List<String> words){
        boolean containsAllWords = true;
        if(words.contains("test"))
            words.remove("test");
        if(words.isEmpty())
            return false;
        int continueCounter = 0;
        for(String word : words) {
            if(word.chars().allMatch( Character::isDigit )) { // skip numbers
                continueCounter++;
                continue;
            }
            if(word.length() <= 2) { // skip word with a length of 2 or less
                continueCounter++;
                continue;
            }
            if (!method.getMethodBody().toLowerCase().contains(word.toLowerCase()))
                containsAllWords = false;
        }
        if(continueCounter == words.size()) // if all words were skipped
            containsAllWords = false;
        return containsAllWords;
    }

    private boolean checkIfStringIsMethodInBody(Method method, String possibleMethodName){
        if(method.getMethodBody().toLowerCase().contains(possibleMethodName.toLowerCase() + "(") || method.getMethodBody().toLowerCase().contains(possibleMethodName.toLowerCase() + " ("))
            return true;
        return false;
    }

}
