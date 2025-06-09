package com.atin.arcface.util;

import com.atin.arcface.R;
import com.atin.arcface.activity.Application;
import com.atin.arcface.model.LivenessLevel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LivenessLevelUtils {
    public static LivenessLevel getLivenessLevel() {
        LivenessLevel result =  ConfigUtil.getLivenessLevel();
        if (result != null) {
            return result;
        }
        result = new LivenessLevel(Application.getInstance().getString(R.string.liveness_level_no_name), Application.getInstance().getResources().getInteger(R.integer.liveness_level_no_value));
        ConfigUtil.setLivenessLevel(result);
        return result;
    }

    public static void setLivenessLevel(LivenessLevel item) {
        ConfigUtil.setLivenessLevel(item);
    }

    public static List<LivenessLevel> getListLevel() {
        List<LivenessLevel> lsData = new ArrayList<>();
        List<String> names =
                Arrays.asList(Application.getInstance().getResources().getStringArray(R.array.liveness_level_names));
        List<Integer> values = Arrays.stream(Application.getInstance().getResources().getIntArray(R.array.liveness_level_values))
                .boxed()
                .collect(Collectors.toList());
        if (names.size() != values.size()) {
            // error, make sure these arrays are same size
            return lsData;
        }
        for (int i = 0, size = names.size(); i < size; i++) {
            lsData.add(new LivenessLevel(names.get(i), values.get(i)));
        }
        return lsData;
    }

    public static LivenessLevel searchMachineFunction(int levelValue) {
        List<LivenessLevel> alllevel = getListLevel();
        LivenessLevel currentLevel = getLivenessLevel();

        LivenessLevel levelMatch = alllevel
                .stream()
                .filter(l -> l.getLevelValue() == levelValue)
                .findAny()
                .orElse(currentLevel);
        return levelMatch;
    }
}
