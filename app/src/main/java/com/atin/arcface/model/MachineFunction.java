package com.atin.arcface.model;

public class MachineFunction {
    private String functionName;
    private int functionValue;

    public MachineFunction(String functionName, int functionValue) {
        this.functionName = functionName;
        this.functionValue = functionValue;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public int getFunctionValue() {
        return functionValue;
    }

    public void setFunctionValue(int functionValue) {
        this.functionValue = functionValue;
    }
}
