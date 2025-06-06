package com.oliveratondo.practica2_modulo8.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseAuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val storage: SecureStorage
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            val user = auth.currentUser ?: return@withContext Result.failure(Exception("No user found"))

            val tokenResult = user.getIdToken(true).await()
            val token = tokenResult.token ?: return@withContext Result.failure(Exception("No token retrieved"))

            storage.saveUser(email, password)
            storage.saveToken(token)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isUserLoggedIn(): Boolean = storage.getToken() != null

    override fun logout() {
        storage.clear()
        auth.signOut()
    }
}
