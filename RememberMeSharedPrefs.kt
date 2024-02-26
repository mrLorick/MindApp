package com.mindbyromanzanoni.sharedPreference

import android.content.Context
import android.content.SharedPreferences
import com.mindbyromanzanoni.utils.constant.AppConstants


/**
 * [RememberMeSharedPrefs]
 * Shared Preferences allow you to save and retrieve data in the form of key,value pair.
 * In order to use shared preferences, you have to call a method getSharedPreferences() that returns a
 * SharedPreference instance pointing to the file that contains the values of preferences.
 * */

class RememberMeSharedPrefs(context: Context)  {
    // Get a reference to SharedPreferences
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(AppConstants.SHARED_PREFERENCE_REMEMBER_ME, Context.MODE_PRIVATE)

    // Get an editor to modify SharedPreferences
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    /**
     * General function to save preference value
     */
    private fun saveToPreference(key:String, value: Any) {
        with(editor) {
            when (value) {
                is Int -> putInt(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                is Boolean -> putBoolean(key, value)
                is String -> putString(key, value)
            }
            apply()
        }
    }

    /**
     * Function which will save the boolean value to preference with given key
     */
    fun saveRememberMe(booleanValue: Boolean) {
        editor.putBoolean(
            AppConstants.SAVE_REMEMBER_ME,
            booleanValue
        )
    }

    /**
     * Function which will return the boolean value saved in the preference corresponds to the given preference key
     */
    fun getRememberMe(): Boolean {
        return sharedPreferences.getBoolean(AppConstants.SAVE_REMEMBER_ME,false)
    }



    /**
     * Function which will save the boolean value to preference with given key
     */
    fun save(preferenceKey: String, booleanValue: Boolean) {
        saveToPreference(
            preferenceKey,
            booleanValue
        )
    }

    /**
     * Function which will save the integer value to preference with given key
     */
    fun save(@SharedPrefs.PrefKey preferenceKey: String, integerValue: Int) {
        saveToPreference(
            preferenceKey,
            integerValue
        )
    }

    /**
     * Function which will save the string value to preference with given key
     */
    fun save(@SharedPrefs.PrefKey preferenceKey: String, stringValue: String?) {
        stringValue?.let { saveToPreference(preferenceKey, stringValue) }
    }



    /**
     * Function which will return the string value saved in the preference corresponds to the given preference key
     */
    fun getInt(preferenceKey: String): Int {
        return sharedPreferences.getInt(preferenceKey, -1) ?: -1
    }



    /**
     * Function which will return the string value saved in the preference corresponds to the given preference key
     */
    fun getString(preferenceKey: String): String {
        return sharedPreferences.getString(preferenceKey, "") ?: ""
    }

    /**
     * Function which will return the boolean value saved in the preference corresponds to the given preference key
     */
    fun getBoolean(preferenceKey: String): Boolean {
        return sharedPreferences.getBoolean(preferenceKey, false)
    }



    /**
     * this function is use to clear preference according to [preferenceKey]
     * @param preferenceKey
     * */
    fun remove(preferenceKey: String) {
        with(sharedPreferences.edit()) {
            remove(preferenceKey)
            apply()
        }
    }

    /**
     * this function use to clear Preference
     * */
    fun clearPreference() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}
