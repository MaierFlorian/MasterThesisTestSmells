package com.github.MaierFlorian.testsmelldetection.nlp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class MethodNameAnalyser {

    /**
     * @param text
     * @return words in text separated by CamelCase, transformed into lowerCase.
     */
    public List<String> splitCamelCase(String text){
        List<String> wordsInText = new ArrayList<>();
        for (String w : text.split("(?<!(^|[A-Z]|[0-9]))(?=[A-Z]|[0-9])|(?<!^)(?=[A-Z][a-z]|[0-9])")) {
            wordsInText.add(w.toLowerCase());
        }
        return  wordsInText;
    }

    public HashMap<String, String> posTagging(List<String> words){
        HashMap<String, String> wordPosTags = new HashMap<String, String>();

        InputStream inputStream = null;
        POSModel model = null;
        try {
            inputStream = new FileInputStream("src/main/resources/com/github/MaierFlorian/testsmelldetection/nlp/en-pos-maxent.bin");
            model = new POSModel(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        POSTaggerME tagger=new POSTaggerME(model);
        String[] result = tagger.tag(words.toArray(new String[words.size()]));
        for(int i=0; i<result.length; i++){
            wordPosTags.put(words.get(i), result[i]);
        }

        return wordPosTags;
    }

}
