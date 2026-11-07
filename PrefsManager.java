package com.umko.pengeluaranku;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {
    private static final String PREF_NAME = "pengeluaranku_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_BUDGET = "monthly_budget";

    private final SharedPreferences prefs;

    public PrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setDarkMode(boolean value) {
        prefs.edit().putBoolean(KEY_DARK_MODE, value).apply();
    }

    public boolean isDarkMode() {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }

    public void setUsername(String name) {
        prefs.edit().putString(KEY_USERNAME, name).apply();
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }

    public void setMonthlyBudget(long amount) {
        prefs.edit().putLong(KEY_BUDGET, amount).apply();
    }

    public long getMonthlyBudget() {
        return prefs.getLong(KEY_BUDGET, 0L);
    }
}