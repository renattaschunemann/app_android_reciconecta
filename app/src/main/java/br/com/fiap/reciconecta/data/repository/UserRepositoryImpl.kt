package br.com.fiap.reciconecta.data.repository

import br.com.fiap.reciconecta.data.local.datastore.UserProfilePreferences
import br.com.fiap.reciconecta.domain.model.UserProfile
import br.com.fiap.reciconecta.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(
    private val preferences: UserProfilePreferences
) : UserRepository {

    override val userProfile: Flow<UserProfile?>
        get() = preferences.userProfile

    override suspend fun saveProfile(profile: UserProfile) {
        preferences.saveProfile(profile)
    }

    override suspend fun clearProfile() {
        preferences.clearProfile()
    }
}
