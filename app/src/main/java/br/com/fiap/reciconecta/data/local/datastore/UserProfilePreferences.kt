package br.com.fiap.reciconecta.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import br.com.fiap.reciconecta.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "reciconecta_preferences"
)

class UserProfilePreferences(
    private val context: Context
) {
    companion object {
        private val NOME_KEY = stringPreferencesKey("nome")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TELEFONE_KEY = stringPreferencesKey("telefone")
        private val CPF_KEY = stringPreferencesKey("cpf")
        private val CEP_KEY = stringPreferencesKey("cep")
    }

    val userProfile: Flow<UserProfile?>
        get() = context.dataStore.data.map { preferences ->
            val nome = preferences[NOME_KEY]
            if (nome != null) {
                UserProfile(
                    nome = nome,
                    email = preferences[EMAIL_KEY] ?: "",
                    telefone = preferences[TELEFONE_KEY] ?: "",
                    cpf = preferences[CPF_KEY] ?: "",
                    cep = preferences[CEP_KEY] ?: ""
                )
            } else {
                null
            }
        }

    suspend fun saveProfile(profile: UserProfile) {
        context.dataStore.edit { preferences ->
            preferences[NOME_KEY] = profile.nome
            preferences[EMAIL_KEY] = profile.email
            preferences[TELEFONE_KEY] = profile.telefone
            preferences[CPF_KEY] = profile.cpf
            preferences[CEP_KEY] = profile.cep
        }
    }

    suspend fun clearProfile() {
        context.dataStore.edit { preferences ->
            preferences.remove(NOME_KEY)
            preferences.remove(EMAIL_KEY)
            preferences.remove(TELEFONE_KEY)
            preferences.remove(CPF_KEY)
            preferences.remove(CEP_KEY)
        }
    }
}
