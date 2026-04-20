package com.example.superahorro.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Creamos el DataStore (una sola vez en toda la app)
/*
Esto crea el archivo donde se guarda todo
 */
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    // 🔑 Claves para guardar datos
    private val IS_LOGGED = booleanPreferencesKey("is_logged")
    private val USER_EMAIL = stringPreferencesKey("user_email")

    //  Guardar sesión (cuando hace login)
    suspend fun saveUser(email: String) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED] = true
            prefs[USER_EMAIL] = email
        }
    }

    // Cerrar sesión
    suspend fun logout() {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED] = false
            prefs.remove(USER_EMAIL)
        }
    }

    // Saber si el usuario está logueado
    val isLogged: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[IS_LOGGED] ?: false }

    // Obtener email guardado
    val userEmail: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[USER_EMAIL] ?: "" }
}