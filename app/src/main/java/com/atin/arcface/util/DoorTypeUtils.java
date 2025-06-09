package com.atin.arcface.util;

import com.atin.arcface.R;
import com.atin.arcface.activity.Application;
import com.atin.arcface.model.DoorType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DoorTypeUtils {
    public static DoorType getDoorType() {
        DoorType result =  ConfigUtil.getDoorType();
        if (result != null) {
            return result;
        }
        result = new DoorType(Application.getInstance().getString(R.string.door_type_with_controller_name), Application.getInstance().getResources().getInteger(R.integer.door_type_with_controller_value));
        ConfigUtil.setDoorType(result);
        return result;
    }

    public static void setDoorType(DoorType item) {
        ConfigUtil.setDoorType(item);
    }

    public static List<DoorType> getListDoorType() {
        List<DoorType> lsData = new ArrayList<>();
        List<String> names =
                Arrays.asList(Application.getInstance().getResources().getStringArray(R.array.door_type_names));
        List<Integer> values = Arrays.stream(Application.getInstance().getResources().getIntArray(R.array.door_type_values))
                .boxed()
                .collect(Collectors.toList());
        if (names.size() != values.size()) {
            // error, make sure these arrays are same size
            return lsData;
        }
        for (int i = 0, size = names.size(); i < size; i++) {
            lsData.add(new DoorType(names.get(i), values.get(i)));
        }
        return lsData;
    }

    public static DoorType searchDoorType(int value) {
        List<DoorType> allData = getListDoorType();
        DoorType currentDoor = getDoorType();

        DoorType itemMatch = allData
                .stream()
                .filter(l -> l.getValue() == value)
                .findAny()
                .orElse(currentDoor);
        return itemMatch;
    }
}
