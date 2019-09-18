package xyz.gigashi.chatworkglancer

import android.content.Context
import android.preference.PreferenceManager

val Context.defaultSharedPreferences get() = run { Preferences(this) }

class Preferences(context: Context) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var token: String
        get() = preferences.getString("token", "") ?: ""
        set(value) = preferences.edit().putString("token", value).apply()
}