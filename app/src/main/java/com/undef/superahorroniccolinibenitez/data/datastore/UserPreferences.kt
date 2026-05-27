package com.undef.superahorroniccolinibenitez.data.datastore

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

        val USER_EMAIL =
            stringPreferencesKey("user_email")

        /*
         Guardamos el nombre del usuario
        */
        val USER_NAME =
            stringPreferencesKey("user_name")

        /*
         Guardamos si el usuario inició sesión
        */
        val IS_LOGGED_IN =
            booleanPreferencesKey("is_logged_in")

        /*
         NUEVO:
         Guardamos si el tema oscuro está activado
        */
        val DARK_THEME =
            booleanPreferencesKey("dark_theme")
    }

    /*
      Obtener email como Flow (reactivo)
    */
    val userEmail: Flow<String> =

        context.userDataStore.data.map { preferences ->

            preferences[USER_EMAIL] ?: ""
        }

    /*
     Obtener nombre como Flow (reactivo)
    */
    val userName: Flow<String> =

        context.userDataStore.data.map { preferences ->

            preferences[USER_NAME] ?: ""
        }

    /*
     Obtener estado de sesión
    */
    val isLoggedIn: Flow<Boolean> =

        context.userDataStore.data.map { preferences ->

            preferences[IS_LOGGED_IN] ?: false
        }

    /*
     NUEVO:
     Obtener estado del tema
    */
    val darkTheme: Flow<Boolean> =

        context.userDataStore.data.map { preferences ->

            preferences[DARK_THEME] ?: false
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
     Guardar estado de sesión
    */
    suspend fun saveLoginState(
        loggedIn: Boolean
    ) {

        context.userDataStore.edit { preferences ->

            preferences[IS_LOGGED_IN] = loggedIn
        }
    }

    /*
     NUEVO:
     Guardar estado del tema oscuro
    */
    suspend fun saveTheme(
        isDark: Boolean
    ) {

        context.userDataStore.edit { preferences ->

            preferences[DARK_THEME] = isDark
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