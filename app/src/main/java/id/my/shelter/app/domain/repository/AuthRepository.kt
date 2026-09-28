package id.my.shelter.app.domain.repository

import kotlinx.coroutines.flow.Flow
import id.my.shelter.app.core.result.Resource
import id.my.shelter.app.domain.model.Gender
import id.my.shelter.app.domain.model.User

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
