package dev.iconpln.mims.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.text.TextUtils
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.securepreferences.SecurePreferences

/**
 * A pack of helpful getter and setter methods for reading/writing to [SharedPreferences].
 */
object SharedPrefsUtils {
    private const val SHARED_PREFS_FILENAME = "encrypted_shared_prefs"

    private fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

            EncryptedSharedPreferences.create(
                SHARED_PREFS_FILENAME,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } else {
            SecurePreferences(context, Config.KEY_SHARED_PREF, SHARED_PREFS_FILENAME)
        }
    }

    /**
     * Helper method to clear all preferences value from [SharedPreferences].
     *
     * @param ctx a [Context] object.
     */
    fun clearPreferences(ctx: Context) {
        val encryptedSharedPreferences = getEncryptedSharedPreferences(ctx)
        val editor = encryptedSharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    /**
     * Helper method to retrieve a String value from [SharedPreferences].
     *
     * @param context a [Context] object.
     * @param key
     * @return The value from shared preferences, or null if the value could not be read.
     */
    fun getStringPreference(context: Context, key: String, defaultValue: String): String? {
        val encryptedSharedPreferences = getEncryptedSharedPreferences(context)
        return encryptedSharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    /**
     * Helper method to write a String value to [SharedPreferences].
     *
     * @param context a [Context] object.
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    fun setStringPreference(context: Context, key: String, value: String): Boolean {
        val encryptedSharedPreferences = getEncryptedSharedPreferences(context)
        if (encryptedSharedPreferences != null && !TextUtils.isEmpty(key)) {
            val editor = encryptedSharedPreferences.edit()
            editor.putString(key, value)
            editor.apply()
        }
        return false
    }

    /**
     * Helper method to retrieve a float value from [SharedPreferences].
     *
     * @param context a [Context] object.
     * @param key
     * @param defaultValue A default to return if the value could not be read.
     * @return The value from shared preferences, or the provided default.
     */
    fun getFloatPreference(context: Context, key: String, defaultValue: Float): Float {
        val encryptedSharedPreferences = getEncryptedSharedPreferences(context)
        return encryptedSharedPreferences.getFloat(key, defaultValue) ?: defaultValue
    }

    /**
     * Helper method to write a float value to [SharedPreferences].
     *
     * @param context a [Context] object.
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    fun setFloatPreference(context: Context, key: String, value: Float): Boolean {
        val encryptedSharedPreferences = getEncryptedSharedPreferences(context)
        if (encryptedSharedPreferences != null && !TextUtils.isEmpty(key)) {
            val editor = encryptedSharedPreferences.edit()
            editor.putFloat(key, value)
            editor.apply()
        }
        return false
    }

    /**
     * Helper method to retrieve a long value from [SharedPreferences].
     *
     * @param context a [Context] object.
     * @param key
     * @param defaultValue A default to return if the value could not be read.
     * @return The value from shared preferences, or the provided default.
     */
    fun getLongPreference(context: Context, key: String, defaultValue: Long): Long {
        val encryptedSharedPreferences = getEncryptedSharedPreferences(context)
        return encryptedSharedPreferences.getLong(key, defaultValue) ?: defaultValue
    }

    /**
     * Helper method to write a long value to [SharedPreferences].
     *
     * @param context a [Context] object.
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    fun setLongPreference(context: Context, key: String, value: Long): Boolean {
        val encryptedSharedPreferences = getEncryptedSharedPreferences(context)
        if (encryptedSharedPreferences != null && !TextUtils.isEmpty(key)) {
            val editor = encryptedSharedPreferences.edit()
            editor.putLong(key, value)
            editor.apply()
        }
        return false
    }

    /**
     * Helper method to retrieve an integer value from [SharedPreferences].
     *
     * @param context a [Context] object.
     * @param key
     * @param defaultValue A default to return if the value could not be read.
     * @return The value from shared preferences, or the provided default.
     */
    fun getIntegerPreference(context: Context, key: String, defaultValue: Int): Int {
        val encryptedSharedPreferences = getEncryptedSharedPreferences(context)
        return encryptedSharedPreferences.getInt(key, defaultValue) ?: defaultValue
    }

    /**
     * Helper method to write an integer value to [SharedPreferences].
     *
     * @param context a [Context] object.
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    fun setIntegerPreference(context: Context, key: String, value: Int): Boolean {
        val encryptedSharedPreferences = getEncryptedSharedPreferences(context)
        if (encryptedSharedPreferences != null && !TextUtils.isEmpty(key)) {
            val editor = encryptedSharedPreferences.edit()
            editor.putInt(key, value)
            editor.apply()
        }
        return false
    }

    /**
     * Helper method to retrieve a boolean value from [SharedPreferences].
     *
     * @param context a [Context] object.
     * @param key
     * @param defaultValue A default to return if the value could not be read.
     * @return The value from shared preferences, or the provided default.
     */
    fun getBooleanPreference(context: Context, key: String, defaultValue: Boolean): Boolean {
        val encryptedSharedPreferences = getEncryptedSharedPreferences(context)
        return encryptedSharedPreferences.getBoolean(key, defaultValue) ?: defaultValue
    }

    /**
     * Helper method to write a boolean value to [SharedPreferences].
     *
     * @param context a [Context] object.
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    fun setBooleanPreference(context: Context, key: String, value: Boolean): Boolean {
        val encryptedSharedPreferences = getEncryptedSharedPreferences(context)
        if (encryptedSharedPreferences != null && !TextUtils.isEmpty(key)) {
            val editor = encryptedSharedPreferences.edit()
            editor.putBoolean(key, value)
            editor.apply()
        }
        return false
    }
}