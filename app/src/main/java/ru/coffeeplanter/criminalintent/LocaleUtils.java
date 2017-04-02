package ru.coffeeplanter.criminalintent;

import android.content.Context;
import android.os.Build;

import java.util.Locale;

public class LocaleUtils {

    @SuppressWarnings("deprecation")
    public static String getCurrentLanguage(Context context) {

        Locale currentLocale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentLocale =  context.getResources().getConfiguration().getLocales().get(0);
        } else {
            currentLocale = context.getResources().getConfiguration().locale;
        }
        return currentLocale.getLanguage();
    }

}
