package com.example.superahorro.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/*
 Esto permite acceder a userDataStore desde cualquier parte usando context.userDataStore
*/
private val Context.userDataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    /*
     Definimos las claves que vamos a guardar
    */
    companion object {
        val USER_EMAIL = stringPreferencesKey("user_email")

        //  guardamos el nombre del usuario
        val USER_NAME = stringPreferencesKey("user_name")
    }

    /*
      Obtener email como Flow (reactivo)
    */
    val userEmail: Flow<String> = context.userDataStore.data
        .map { preferences ->
            preferences[USER_EMAIL] ?: ""
        }

    /*
     Obtener nombre como Flow (reactivo)
    */
    val userName: Flow<String> = context.userDataStore.data
        .map { preferences ->
            preferences[USER_NAME] ?: ""
        }

    /*
      Guardar email (login)
    */
    suspend fun saveUser(email: String) {
        context.userDataStore.edit { preferences ->
            preferences[USER_EMAIL] = email
        }
    }

    /*
      Guardar nombre del usuario (perfil)
    */
    suspend fun saveUserName(name: String) {
        context.userDataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    /*
     Logout
     Borra todos los datos guardados
    */
    suspend fun logout() {
        context.userDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}