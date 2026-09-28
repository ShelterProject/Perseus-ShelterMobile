package sidev.app.shelter.domain.repository

import kotlinx.coroutines.flow.Flow
import sidev.app.shelter.core.result.Resource
import sidev.app.shelter.domain.model.Gender
import sidev.app.shelter.domain.model.User

interface AuthRepository {
    val currentUser: Flow<User?>

    suspend fun signIn(email: String, password: String): Resource<User>

    suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        gender: Gender,
    ): Resource<User>

    fun signOut()
}
