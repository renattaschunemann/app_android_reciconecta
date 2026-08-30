package br.com.fiap.reciconecta.domain.repository

import br.com.fiap.reciconecta.domain.model.RecyclingStats
import kotlinx.coroutines.flow.Flow

interface RecyclingRepository {
    val recyclingStats: Flow<RecyclingStats>
    suspend fun addMaterial(plasticKg: Float, paperKg: Float, metalKg: Float)
    suspend fun resetStats()
}
