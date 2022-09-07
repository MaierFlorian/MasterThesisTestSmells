package com.github.MaierFlorian.testsmelldetection.data;

import java.util.Objects;

public class Method {

    private String MethodDeclaration;
    private String MethodName;
    private String MethodBody;

    // Anonymous Test

    private int anonymousTestRating = 3;
    private int test_methodname = 3;
    private int method_state_behaviour = 3;
    private int specificRequirement = 3;
    private int inputStateWorkflow_output = 3;
    private int action_conditionState = 3;

    // Conditional Test Logic

    private int amountIfAndSwitch;
    private int amountLoops;

    // Long Test

    private int amountLines = -1;

    // Assertion Roulette

    private int amountAssertions; // also used by Rotten Green Test

    // Rotten Green Test

    private int amountAssertionsInsideCTL;
    private boolean assertionAfterReturn = false;
    private int helperMethodsInsideCTL;

    // #################################
    // Equals
    // #################################

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Method method = (Method) o;
        return Objects.equals(MethodDeclaration, method.MethodDeclaration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(MethodDeclaration);
    }


    // #################################
    // Getter and Setter
    // #################################

    public String getMethodDeclaration() {
        return MethodDeclaration;
    }

    public void setMethodDeclaration(String methodDeclaration) {
        MethodDeclaration = methodDeclaration;
    }

    public String getMethodBody() {
        return MethodBody;
    }

    public void setMethodBody(String methodBody) {
        MethodBody = methodBody;
    }

    public int getAmountIfAndSwitch() {
        return amountIfAndSwitch;
    }

    public void setAmountIfAndSwitch(int amountIfAndSwitch) {
        this.amountIfAndSwitch = amountIfAndSwitch;
    }

    public int getAmountLoops() {
        return amountLoops;
    }

    public void setAmountLoops(int amountLoops) {
        this.amountLoops = amountLoops;
    }

    public String getMethodName() {
        return MethodName;
    }

    public void setMethodName(String methodName) {
        MethodName = methodName;
    }

    public int getAnonymousTestRating() {
        return anonymousTestRating;
    }

    public void setAnonymousTestRating(int anonymousTestRating) {
        this.anonymousTestRating = anonymousTestRating;
    }

    public int getTest_methodname() {
        return test_methodname;
    }

    public void setTest_methodname(int test_methodname) {
        this.test_methodname = test_methodname;
    }

    public int getMethod_state_behaviour() {
        return method_state_behaviour;
    }

    public void setMethod_state_behaviour(int method_state_behaviour) {
        this.method_state_behaviour = method_state_behaviour;
    }

    public int getSpecificRequirement() {
        return specificRequirement;
    }

    public void setSpecificRequirement(int specificRequirement) {
        this.specificRequirement = specificRequirement;
    }

    public int getInputStateWorkflow_output() {
        return inputStateWorkflow_output;
    }

    public void setInputStateWorkflow_output(int inputStateWorkflow_output) {
        this.inputStateWorkflow_output = inputStateWorkflow_output;
    }

    public int getAction_conditionState() {
        return action_conditionState;
    }

    public void setAction_conditionState(int action_conditionState) {
        this.action_conditionState = action_conditionState;
    }

    public int getAmountLines() {
        return amountLines;
    }

    public void setAmountLines(int amountLines) {
        this.amountLines = amountLines;
    }

    public int getAmountAssertions() {
        return amountAssertions;
    }

    public void setAmountAssertions(int amountAssertions) {
        this.amountAssertions = amountAssertions;
    }

    public int getAmountAssertionsInsideCTL() {
        return amountAssertionsInsideCTL;
    }

    public void setAmountAssertionsInsideCTL(int amountAssertionsInsideCTL) {
        this.amountAssertionsInsideCTL = amountAssertionsInsideCTL;
    }

    public boolean isAssertionAfterReturn() {
        return assertionAfterReturn;
    }

    public void setAssertionAfterReturn(boolean assertionAfterReturn) {
        this.assertionAfterReturn = assertionAfterReturn;
    }

    public int getHelperMethodsInsideCTL() {
        return helperMethodsInsideCTL;
    }

    public void setHelperMethodsInsideCTL(int helperMethodsInsideCTL) {
        this.helperMethodsInsideCTL = helperMethodsInsideCTL;
    }
}
