package sidev.app.shelter.data.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sidev.app.shelter.core.result.Resource
import sidev.app.shelter.domain.model.Gender
import sidev.app.shelter.domain.model.User
import sidev.app.shelter.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val userProfileFirestoreDataSource: UserProfileFirestoreDataSource,
) : AuthRepository {

    override val currentUser: Flow<User?> =
        firebaseAuthDataSource.currentFirebaseUser.map { firebaseUser ->
            firebaseUser?.let {
                userProfileFirestoreDataSource.fetchProfile(it.uid, it.email.orEmpty())
            }
        }

    override suspend fun signIn(email: String, password: String): Resource<User> = try {
        val firebaseUser = firebaseAuthDataSource.signIn(email, password)
        val user = userProfileFirestoreDataSource.fetchProfile(firebaseUser.uid, email)
        Resource.Success(user)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Gagal masuk, coba lagi.", e)
    }

    override suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        gender: Gender,
    ): Resource<User> = try {
        val firebaseUser = firebaseAuthDataSource.signUp(email, password)
        userProfileFirestoreDataSource.createProfile(firebaseUser.uid, fullName, gender)
        Resource.Success(User(uid = firebaseUser.uid, email = email, fullName = fullName, gender = gender))
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Gagal mendaftar, coba lagi.", e)
    }

    override fun signOut() = firebaseAuthDataSource.signOut()
}
