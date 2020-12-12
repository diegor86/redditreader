package com.diegor.redditreader.data

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SimpleStorageRepository @Inject constructor(appContext: Context) {
    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(appContext)
    }

    suspend fun getString(key: String, default: String = ""): String {
        return sharedPreferences.getString(key, default) ?: default
    }

    suspend fun getBoolean(key: String, default: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, default)
    }

    suspend fun storeValue(key: String, value: String) {
        sharedPreferences.edit(commit = true) { putString(key, value) }
    }

    suspend fun storeValue(key: String, value: Boolean) {
        sharedPreferences.edit(commit = true) { putBoolean(key, value) }
    }

    suspend fun removeKey(key: String) {
        sharedPreferences.edit { remove(key) }
    }
}