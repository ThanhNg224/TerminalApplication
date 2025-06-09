package com.atin.arcface.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.atin.arcface.R;
import com.atin.arcface.activity.Application;
import com.atin.arcface.common.Constants;
import com.atin.arcface.model.Language;
import com.atin.arcface.service.ContextWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LanguageUtils {

    private static Language sCurrentLanguage = null;

    public static Language getCurrentLanguage() {
        //if (sCurrentLanguage == null)
        {
            sCurrentLanguage = initCurrentLanguage();
        }
        return sCurrentLanguage;
    }

    /**
     * check language exist in SharedPrefs, if not exist then default language is English
     */
    private static Language initCurrentLanguage() {
        Language currentLanguage =  ConfigUtil.getLanguage();
        if (currentLanguage != null) {
            return currentLanguage;
        }
        currentLanguage = new Language(Constants.Value.DEFAULT_LANGUAGE_ID,
                Application.getInstance().getString(R.string.language_vietnamese),
                Application.getInstance().getString(R.string.language_vietnamese_code));
        ConfigUtil.setLanguage(currentLanguage);
        return currentLanguage;
    }

    /**
     * return language list from string.xml
     */
    public static List<Language> getLanguageData() {
        List<Language> languageList = new ArrayList<>();
        List<String> languageNames =
                Arrays.asList(Application.getInstance().getResources().getStringArray(R.array.language_names));
        List<String> languageCodes =
                Arrays.asList(Application.getInstance().getResources().getStringArray(R.array.language_codes));
        if (languageNames.size() != languageCodes.size()) {
            // error, make sure these arrays are same size
            return languageList;
        }
        for (int i = 0, size = languageNames.size(); i < size; i++) {
            languageList.add(new Language(i, languageNames.get(i), languageCodes.get(i)));
        }
        return languageList;
    }

    /**
     * return language by languageCode from string.xml
     */
    public static Language searchLanguageData(String languageCode) {
        List<Language> allLanguage = getLanguageData();
        Language currentLanguage = getCurrentLanguage();

        Language languageMatch = allLanguage
                .stream()
                .filter(l -> l.getCode().toUpperCase().equals(languageCode.toUpperCase()))
                .findAny()
                .orElse(currentLanguage);
        return languageMatch;
    }

    /**
     * load current locale and change language
     */
    public static void loadLocale() {
        changeLanguage(initCurrentLanguage());
    }

    /**
     * change app language
     */
    @SuppressWarnings("deprecation")
    public static void changeLanguage(Language language) {
        ConfigUtil.setLanguage(language);
        sCurrentLanguage = language;
        Locale locale = new Locale(language.getCode());
        Resources resources = Application.getInstance().getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    private static Resources getLanguageResources(){
        Language language = ConfigUtil.getLanguage();
        Locale locale = new Locale(language == null ? Application.getInstance().getString(R.string.language_vietnamese_code) : language.getCode());
        Context context = ContextWrapper.wrap(Application.getInstance(), locale);
        Resources resources = context.getResources();
        return resources;
    }

    public static String getString(int idResources){
        return getLanguageResources().getString(idResources);
    }
}
