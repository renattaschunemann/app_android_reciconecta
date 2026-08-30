package br.com.fiap.reciconecta.data.repository

import br.com.fiap.reciconecta.data.local.datastore.RecyclingPreferences
import br.com.fiap.reciconecta.domain.model.RecyclingStats
import br.com.fiap.reciconecta.domain.repository.RecyclingRepository
import kotlinx.coroutines.flow.Flow

class RecyclingRepositoryImpl(
    private val preferences: RecyclingPreferences
) : RecyclingRepository {

    override val recyclingStats: Flow<RecyclingStats>
        get() = preferences.recyclingStats

    override suspend fun addMaterial(plasticKg: Float, paperKg: Float, metalKg: Float) {
        preferences.addMaterial(plasticKg, paperKg, metalKg)
    }

    override suspend fun resetStats() {
        preferences.resetStats()
    }
}
