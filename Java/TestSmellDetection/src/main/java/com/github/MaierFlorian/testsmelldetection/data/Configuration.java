package com.github.MaierFlorian.testsmelldetection.data;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Configuration {

    // Singleton

    private static Configuration instance = new Configuration();

    public static Configuration getInstance(){
        return instance;
    }

    // Files
    private List<File> files = new ArrayList<>();

    //#############################################
    // Configuration
    //#############################################

    private boolean anonymousTest = true;
    private boolean test_methodname = true;
    private boolean method_state_behaviour = true; // TODO
    private boolean specificRequirement = true; // TODO
    private boolean inputStateWorkflow_output = true; // TODO
    private boolean action_conditionState = true; // TODO

    private boolean longTest = true; // TODO
    private int numberLines = 13; // TODO

    private boolean conditionalTestLogic = true;
    private boolean onlyPrint = false;

    private boolean rottenGreenTest = true; // TODO

    private boolean assertionRoulette = true; // TODO
    private int numberAssertions = 1; // TODO

    private boolean eagerTest = true; // TODO

    private boolean lazyTest = true; // TODO

    //#############################################
    // Log
    //#############################################

    private void somethingChanged(){
        System.out.println("[INFO - " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "] The configuration was modified.");
    }

    //#############################################
    // Getter and Setter
    //#############################################

    public boolean isAnonymousTest() {
        return anonymousTest;
    }

    public void setAnonymousTest(boolean anonymousTest) {
        this.anonymousTest = anonymousTest;
        somethingChanged();
    }

    public boolean isTest_methodname() {
        return test_methodname;
    }

    public void setTest_methodname(boolean test_methodname) {
        this.test_methodname = test_methodname;
        somethingChanged();
    }

    public boolean isMethod_state_behaviour() {
        return method_state_behaviour;
    }

    public void setMethod_state_behaviour(boolean method_state_behaviour) {
        this.method_state_behaviour = method_state_behaviour;
        somethingChanged();
    }

    public boolean isSpecificRequirement() {
        return specificRequirement;
    }

    public void setSpecificRequirement(boolean specificRequirement) {
        this.specificRequirement = specificRequirement;
        somethingChanged();
    }

    public boolean isInputStateWorkflow_output() {
        return inputStateWorkflow_output;
    }

    public void setInputStateWorkflow_output(boolean inputStateWorkflow_output) {
        this.inputStateWorkflow_output = inputStateWorkflow_output;
        somethingChanged();
    }

    public boolean isAction_conditionState() {
        return action_conditionState;
    }

    public void setAction_conditionState(boolean action_conditionState) {
        this.action_conditionState = action_conditionState;
        somethingChanged();
    }

    public boolean isLongTest() {
        return longTest;
    }

    public void setLongTest(boolean longTest) {
        this.longTest = longTest;
        somethingChanged();
    }

    public int getNumberLines() {
        return numberLines;
    }

    public void setNumberLines(int numberLines) {
        this.numberLines = numberLines;
        somethingChanged();
    }

    public boolean isConditionalTestLogic() {
        return conditionalTestLogic;
    }

    public void setConditionalTestLogic(boolean conditionalTestLogic) {
        this.conditionalTestLogic = conditionalTestLogic;
        somethingChanged();
    }

    public boolean isOnlyPrint() {
        return onlyPrint;
    }

    public void setOnlyPrint(boolean onlyPrint) {
        this.onlyPrint = onlyPrint;
        somethingChanged();
    }

    public boolean isRottenGreenTest() {
        return rottenGreenTest;
    }

    public void setRottenGreenTest(boolean rottenGreenTest) {
        this.rottenGreenTest = rottenGreenTest;
        somethingChanged();
    }

    public boolean isAssertionRoulette() {
        return assertionRoulette;
    }

    public void setAssertionRoulette(boolean assertionRoulette) {
        this.assertionRoulette = assertionRoulette;
        somethingChanged();
    }

    public int getNumberAssertions() {
        return numberAssertions;
    }

    public void setNumberAssertions(int numberAssertions) {
        this.numberAssertions = numberAssertions;
        somethingChanged();
    }

    public boolean isEagerTest() {
        return eagerTest;
    }

    public void setEagerTest(boolean eagerTest) {
        this.eagerTest = eagerTest;
        somethingChanged();
    }

    public boolean isLazyTest() {
        return lazyTest;
    }

    public void setLazyTest(boolean lazyTest) {
        this.lazyTest = lazyTest;
        somethingChanged();
    }

    public List<File> getFiles() {
        return files;
    }

    public void addFile(File file) {
        this.files.add(file);
        somethingChanged();
    }

    public void clearFiles() {
        this.files.clear();
        somethingChanged();
    }
}
