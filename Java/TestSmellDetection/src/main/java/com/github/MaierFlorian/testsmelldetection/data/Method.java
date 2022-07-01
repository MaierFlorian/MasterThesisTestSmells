package com.github.MaierFlorian.testsmelldetection.data;

public class Method {

    private String MethodDeclaration;
    private String MethodName;
    private String MethodBody;

    // Anonymous Test

    private int anonymousTestRating = -1;
    private int test_methodname = -1;
    private int method_state_behaviour = -1;
    private int specificRequirement = -1;
    private int inputStateWorkflow_output = -1;
    private int action_conditionState = -1;

    // Conditional Test Logic

    private int amountIfAndSwitch;
    private int amountLoops;

    // Long Test

    private int amountLines = -1;


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
}
