package com.github.MaierFlorian.testsmelldetection.nlp;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MethodNameAnalyserTest {

    @Test
    public void splitCamelCaseCheckForAmountWordsOutputTrue(){
        String text = "exampleCamelCaseWordWith1Number";
        List<String> words = new MethodNameAnalyser().splitCamelCase(text);
        Assert.assertEquals(7, words.size());
    }

    @Test
    public void splitCamelCaseInput7WordsOutputFirstWordEquals(){
        String text = "exampleCamelCaseWordWith1Number";
        List<String> words = new MethodNameAnalyser().splitCamelCase(text);
        Assert.assertEquals("example", words.get(0));
    }

    @Test
    public void splitCamelCaseInput7WordsOutputLastWordEquals(){
        String text = "exampleCamelCaseWordWith1Number";
        List<String> words = new MethodNameAnalyser().splitCamelCase(text);
        Assert.assertEquals("Number", words.get(words.size()-1));
    }

    @Test
    public void splitCamelCaseInput7WordsOutputNumberEquals(){
        String text = "exampleCamelCaseWordWith1Number";
        List<String> words = new MethodNameAnalyser().splitCamelCase(text);
        Assert.assertEquals("1", words.get(5));
    }

    @Test
    public void testPosTaggingInputComputerOutputNN(){
        List<String> inputWords = new ArrayList<>();
        String word = "Computer"; // Noun
        inputWords.add(word);
        HashMap<String, String> result = new MethodNameAnalyser().posTagging(inputWords);
        Assert.assertEquals("NN", result.get("Computer"));
    }

    @Test
    public void testPosTaggingInputGamingOutputVBG(){
        List<String> inputWords = new ArrayList<>();
        String word = "Gaming"; // Verb
        inputWords.add(word);
        HashMap<String, String> result = new MethodNameAnalyser().posTagging(inputWords);
        Assert.assertEquals("VBG", result.get("Gaming"));
    }

    @Test
    public void testPosTaggingInputBeautifulOutputJJ(){
        List<String> inputWords = new ArrayList<>();
        String word = "Beautiful"; // Adjective
        inputWords.add(word);
        HashMap<String, String> result = new MethodNameAnalyser().posTagging(inputWords);
        Assert.assertEquals("JJ", result.get("Beautiful"));
    }

    @Test
    public void testPosTaggingInputWithOutputIN(){
        List<String> inputWords = new ArrayList<>();
        String word = "with"; // Preposition
        inputWords.add(word);
        HashMap<String, String> result = new MethodNameAnalyser().posTagging(inputWords);
        Assert.assertEquals("IN", result.get("with"));
    }

}
