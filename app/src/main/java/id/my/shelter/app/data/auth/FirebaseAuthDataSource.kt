package id.my.shelter.app.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) {
    /** Wraps FirebaseAuth's listener as a Flow so the SDK's own token refresh drives the app's session. */
    val currentFirebaseUser: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth -> trySend(auth.currentUser) }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    suspend fun signIn(email: String, password: String): FirebaseUser {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return requireNotNull(result.user) { "Firebase returned no user after sign in" }
    }

    suspend fun signUp(email: String, password: String): FirebaseUser {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        return requireNotNull(result.user) { "Firebase returned no user after sign up" }
    }

    /** [googleIdToken] comes from Credential Manager (see LoginScreen); GoogleAuthProvider turns it into a Firebase credential. */
    suspend fun signInWithGoogleIdToken(googleIdToken: String): FirebaseUser {
        val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
        val result = firebaseAuth.signInWithCredential(credential).await()
        return requireNotNull(result.user) { "Firebase returned no user after Google sign in" }
    }

    fun signOut() = firebaseAuth.signOut()
}
