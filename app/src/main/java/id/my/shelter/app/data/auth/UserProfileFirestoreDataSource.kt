package id.my.shelter.app.data.auth

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import id.my.shelter.app.domain.model.Gender
import id.my.shelter.app.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

/** Firestore `users/{uid}` document: profile fields Firebase Auth itself doesn't store. */
@Singleton
class UserProfileFirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    private fun usersCollection() = firestore.collection("users")

    // email is not duplicated here; it lives only in Firebase Auth and is merged back
    // into the User model by AuthRepositoryImpl.
    suspend fun createProfile(uid: String, fullName: String, gender: Gender) {
        val doc = mapOf(
            "fullName" to fullName,
            "gender" to gender.name,
            "address" to null,
        )
        usersCollection().document(uid).set(doc).await()
    }

    suspend fun fetchProfile(uid: String, email: String): User {
        val snapshot = usersCollection().document(uid).get().await()
        val fullName = snapshot.getString("fullName").orEmpty()
        val gender = snapshot.getString("gender")?.let { runCatching { Gender.valueOf(it) }.getOrNull() }
        val address = snapshot.getString("address")
        return User(uid = uid, email = email, fullName = fullName, gender = gender, address = address)
    }
}
