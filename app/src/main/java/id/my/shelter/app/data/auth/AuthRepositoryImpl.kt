package id.my.shelter.app.data.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import id.my.shelter.app.core.result.Resource
import id.my.shelter.app.domain.model.Gender
import id.my.shelter.app.domain.model.User
import id.my.shelter.app.domain.repository.AuthRepository
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

    override suspend fun signInWithGoogle(googleIdToken: String): Resource<User> = try {
        val firebaseUser = firebaseAuthDataSource.signInWithGoogleIdToken(googleIdToken)
        val user = userProfileFirestoreDataSource.fetchOrCreateProfile(
            uid = firebaseUser.uid,
            email = firebaseUser.email.orEmpty(),
            googleDisplayName = firebaseUser.displayName.orEmpty(),
        )
        Resource.Success(user)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Gagal masuk dengan Google, coba lagi.", e)
    }

    override fun signOut() = firebaseAuthDataSource.signOut()
}
