package br.com.fiap.reciconecta.domain.repository

import br.com.fiap.reciconecta.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val userProfile: Flow<UserProfile?>
    suspend fun saveProfile(profile: UserProfile)
    suspend fun clearProfile()
}
