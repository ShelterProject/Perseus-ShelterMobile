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

    /**
     * Google sign-in creates the Firebase Auth user before any `users/{uid}` doc exists.
     * Seeds it from the Google account's display name (gender unknown) on first login only.
     */
    suspend fun fetchOrCreateProfile(uid: String, email: String, googleDisplayName: String): User {
        val docRef = usersCollection().document(uid)
        val snapshot = docRef.get().await()
        if (!snapshot.exists()) {
            docRef.set(mapOf("fullName" to googleDisplayName, "gender" to null, "address" to null)).await()
            return User(uid = uid, email = email, fullName = googleDisplayName, gender = null, address = null)
        }
        return fetchProfile(uid, email)
    }
}
