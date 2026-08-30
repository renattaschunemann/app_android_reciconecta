package br.com.fiap.reciconecta.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
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
        private val CPF_CNPJ_KEY = stringPreferencesKey("cpf_cnpj")
        private val CEP_KEY = stringPreferencesKey("cep")
        private val TIPO_PERFIL_KEY = stringPreferencesKey("tipo_perfil")
        private val MATERIAIS_KEY = stringSetPreferencesKey("materiais")
    }

    val userProfile: Flow<UserProfile?>
        get() = context.dataStore.data.map { preferences ->
            val nome = preferences[NOME_KEY]
            if (nome != null) {
                UserProfile(
                    nome = nome,
                    email = preferences[EMAIL_KEY] ?: "",
                    telefone = preferences[TELEFONE_KEY] ?: "",
                    cpfOrCnpj = preferences[CPF_CNPJ_KEY] ?: "",
                    cep = preferences[CEP_KEY] ?: "",
                    tipoPerfil = preferences[TIPO_PERFIL_KEY] ?: "PF",
                    materiais = preferences[MATERIAIS_KEY]?.toList() ?: emptyList()
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
            preferences[CPF_CNPJ_KEY] = profile.cpfOrCnpj
            preferences[CEP_KEY] = profile.cep
            preferences[TIPO_PERFIL_KEY] = profile.tipoPerfil
            preferences[MATERIAIS_KEY] = profile.materiais.toSet()
        }
    }

    suspend fun clearProfile() {
        context.dataStore.edit { preferences ->
            preferences.remove(NOME_KEY)
            preferences.remove(EMAIL_KEY)
            preferences.remove(TELEFONE_KEY)
            preferences.remove(CPF_CNPJ_KEY)
            preferences.remove(CEP_KEY)
            preferences.remove(TIPO_PERFIL_KEY)
            preferences.remove(MATERIAIS_KEY)
        }
    }
}
