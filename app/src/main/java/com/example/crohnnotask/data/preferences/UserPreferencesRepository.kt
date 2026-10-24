package com.example.crohnnotask.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.crohnnotask.domain.auth.SessionStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.preferencesDataStore by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) : SessionStore {
    val isDarkTheme: Flow<Boolean> = context.preferencesDataStore.data.map { preferences ->
        preferences[DARK_THEME_KEY] ?: false
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.preferencesDataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = enabled
        }
    }

    override val activeEmail: Flow<String?> = context.preferencesDataStore.data.map { preferences ->
        preferences[ACTIVE_EMAIL_KEY]
    }

    override suspend fun setActiveEmail(email: String) {
        context.preferencesDataStore.edit { preferences -> preferences[ACTIVE_EMAIL_KEY] = email }
    }

    override suspend fun clearActiveEmail() {
        context.preferencesDataStore.edit { preferences -> preferences.remove(ACTIVE_EMAIL_KEY) }
    }

    private companion object {
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        val ACTIVE_EMAIL_KEY = stringPreferencesKey("active_email")
    }
}
