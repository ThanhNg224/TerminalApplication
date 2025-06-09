package com.atin.arcface.util;

import com.atin.arcface.R;
import com.atin.arcface.activity.Application;
import com.atin.arcface.model.MachineFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MachineFunctionUtils {
    public static MachineFunction getMachineFunction() {
        MachineFunction machineFunction =  ConfigUtil.getMachineFunction();
        if (machineFunction != null) {
            return machineFunction;
        }
        machineFunction = new MachineFunction(Application.getInstance().getString(R.string.function_timekeeping_name), Application.getInstance().getResources().getInteger(R.integer.function_timekeeping_value));
        ConfigUtil.setMachineFunction(machineFunction);
        return machineFunction;
    }

    public static void setMachineFunction(MachineFunction machineFunction) {
        ConfigUtil.setMachineFunction(machineFunction);
    }

    /**
     * return pccovid list from string.xml
     */
    public static List<MachineFunction> getListFunction() {
        List<MachineFunction> lsData = new ArrayList<>();
        List<String> functionNames =
                Arrays.asList(Application.getInstance().getResources().getStringArray(R.array.machine_function_names));
        List<Integer> functionValues = Arrays.stream(Application.getInstance().getResources().getIntArray(R.array.machine_function_values))
                .boxed()
                .collect(Collectors.toList());
        if (functionNames.size() != functionValues.size()) {
            // error, make sure these arrays are same size
            return lsData;
        }
        for (int i = 0, size = functionNames.size(); i < size; i++) {
            lsData.add(new MachineFunction(functionNames.get(i), functionValues.get(i)));
        }
        return lsData;
    }

    /**
     * return MachineFunction by functionValue from string.xml
     */
    public static MachineFunction searchMachineFunction(int functionValue) {
        List<MachineFunction> allMachineFunction = getListFunction();
        MachineFunction currentFunction = getMachineFunction();

        MachineFunction functionMatch = allMachineFunction
                .stream()
                .filter(l -> l.getFunctionValue() == functionValue)
                .findAny()
                .orElse(currentFunction);
        return functionMatch;
    }
}
