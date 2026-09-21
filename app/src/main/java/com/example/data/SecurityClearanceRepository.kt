package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aegora_clearance_prefs")

/**
 * DataStore Repository managing First-Time Security Clearance Onboarding
 */
class SecurityClearanceRepository(private val context: Context) {

  companion object {
    private val KEY_CLEARANCE_VERIFIED = booleanPreferencesKey("security_clearance_verified")
    private val KEY_OPERATOR_ALIAS = stringPreferencesKey("security_operator_alias")
  }

  val isClearanceVerifiedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
    preferences[KEY_CLEARANCE_VERIFIED] ?: false
  }

  val operatorAliasFlow: Flow<String?> = context.dataStore.data.map { preferences ->
    preferences[KEY_OPERATOR_ALIAS]
  }

  suspend fun recordClearanceVerified(alias: String) {
    context.dataStore.edit { preferences ->
      preferences[KEY_CLEARANCE_VERIFIED] = true
      preferences[KEY_OPERATOR_ALIAS] = alias.trim()
    }
  }

  suspend fun resetClearanceForTesting() {
    context.dataStore.edit { preferences ->
      preferences[KEY_CLEARANCE_VERIFIED] = false
      preferences.remove(KEY_OPERATOR_ALIAS)
    }
  }
}
