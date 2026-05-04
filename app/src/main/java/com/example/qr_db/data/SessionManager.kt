package com.example.qr_db.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

class SessionManager(private val context: Context) {

    companion object {
        private val USER_ID = intPreferencesKey("user_id")
        private val FULL_NAME = stringPreferencesKey("full_name")
        private val ROLE_ID = intPreferencesKey("role_id")
        private val EMAIL = stringPreferencesKey("email")
    }

    suspend fun saveSession(user: User) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = user.userId
            preferences[FULL_NAME] = user.fullName
            preferences[ROLE_ID] = user.roleId
            preferences[EMAIL] = user.email ?: ""
        }
    }

    val userFlow: Flow<User?> = context.dataStore.data.map { preferences ->
        val id = preferences[USER_ID]
        if (id != null) {
            User(
                userId = id,
                fullName = preferences[FULL_NAME] ?: "",
                email = preferences[EMAIL],
                roleId = preferences[ROLE_ID] ?: 1
            )
        } else null
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}
