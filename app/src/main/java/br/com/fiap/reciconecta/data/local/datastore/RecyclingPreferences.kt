package br.com.fiap.reciconecta.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import br.com.fiap.reciconecta.domain.model.RecyclingStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.recyclingDataStore by preferencesDataStore(
    name = "recycling_stats_preferences"
)

class RecyclingPreferences(
    private val context: Context
) {
    companion object {
        private val PLASTIC_KEY = floatPreferencesKey("plastic_kg")
        private val PAPER_KEY = floatPreferencesKey("paper_kg")
        private val METAL_KEY = floatPreferencesKey("metal_kg")
    }

    val recyclingStats: Flow<RecyclingStats>
        get() = context.recyclingDataStore.data.map { preferences ->
            RecyclingStats(
                plasticKg = preferences[PLASTIC_KEY] ?: 0f,
                paperKg = preferences[PAPER_KEY] ?: 0f,
                metalKg = preferences[METAL_KEY] ?: 0f
            )
        }

    suspend fun addMaterial(plastic: Float, paper: Float, metal: Float) {
        context.recyclingDataStore.edit { preferences ->
            val currentPlastic = preferences[PLASTIC_KEY] ?: 0f
            val currentPaper = preferences[PAPER_KEY] ?: 0f
            val currentMetal = preferences[METAL_KEY] ?: 0f
            preferences[PLASTIC_KEY] = currentPlastic + plastic
            preferences[PAPER_KEY] = currentPaper + paper
            preferences[METAL_KEY] = currentMetal + metal
        }
    }

    suspend fun resetStats() {
        context.recyclingDataStore.edit { preferences ->
            preferences[PLASTIC_KEY] = 0f
            preferences[PAPER_KEY] = 0f
            preferences[METAL_KEY] = 0f
        }
    }
}
