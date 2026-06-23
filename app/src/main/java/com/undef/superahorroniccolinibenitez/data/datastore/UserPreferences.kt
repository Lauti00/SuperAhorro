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
         Guardamos si el tema oscuro está activado
        */
        val DARK_THEME =
            booleanPreferencesKey("dark_theme")

        /*
         Datos del usuario registrado.
         Estos datos quedan guardados aunque se cierre sesión.
        */
        val REGISTERED_NAME =
            stringPreferencesKey("registered_name")

        val REGISTERED_EMAIL =
            stringPreferencesKey("registered_email")

        val REGISTERED_PASSWORD =
            stringPreferencesKey("registered_password")

        /*
         Guardamos el idioma seleccionado por el usuario ("es", "en")
        */
        val APP_LANGUAGE =
            stringPreferencesKey("app_language")
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
     Obtener estado del tema
    */
    val darkTheme: Flow<Boolean> =
        context.userDataStore.data.map { preferences ->
            preferences[DARK_THEME] ?: false
        }

    /*
     Obtener datos del usuario registrado.
    */
    val registeredName: Flow<String> =
        context.userDataStore.data.map { preferences ->
            preferences[REGISTERED_NAME] ?: ""
        }

    val registeredEmail: Flow<String> =
        context.userDataStore.data.map { preferences ->
            preferences[REGISTERED_EMAIL] ?: ""
        }

    val registeredPassword: Flow<String> =
        context.userDataStore.data.map { preferences ->
            preferences[REGISTERED_PASSWORD] ?: ""
        }

    /*
     Obtener el idioma actual. Por defecto usa "es" (Español).
    */
    val appLanguage: Flow<String> =
        context.userDataStore.data.map { preferences ->
            preferences[APP_LANGUAGE] ?: "es"
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
    suspend fun saveLoginState(loggedIn: Boolean) {
        context.userDataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = loggedIn
        }
    }

    /*
     Guardar estado del tema oscuro
    */
    suspend fun saveTheme(isDark: Boolean) {
        context.userDataStore.edit { preferences ->
            preferences[DARK_THEME] = isDark
        }
    }

    /*
     Guarda el usuario registrado.
    */
    suspend fun saveRegisteredUser(
        name: String,
        email: String,
        password: String
    ) {
        context.userDataStore.edit { preferences ->
            preferences[REGISTERED_NAME] = name
            preferences[REGISTERED_EMAIL] = email
            preferences[REGISTERED_PASSWORD] = password
        }
    }

    /*

     Actualiza también el nombre registrado.
     Así el perfil no vuelve al nombre anterior después de cerrar sesión.
    */
    suspend fun updateRegisteredName(name: String) {
        context.userDataStore.edit { preferences ->
            preferences[REGISTERED_NAME] = name
        }
    }

    /*
     Guardar el idioma seleccionado ("es" o "en")
    */
    suspend fun saveLanguage(languageCode: String) {
        context.userDataStore.edit { preferences ->
            preferences[APP_LANGUAGE] = languageCode
        }
    }

    /*
   Logout
   Borra SOLO los datos de sesión
   pero mantiene configuraciones y usuario registrado
  */
    suspend fun logout() {
        context.userDataStore.edit { preferences ->

            preferences.remove(USER_EMAIL)

            preferences.remove(USER_NAME)

            preferences.remove(IS_LOGGED_IN)
        }

    }
}