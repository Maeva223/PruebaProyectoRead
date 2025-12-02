package com.inacap.iotmobileapp.utils

/**
 * Objeto singleton para mantener la sesión del usuario logeado
 */
object UserSession {
    private var currentUserId: Long? = null

    fun login(userId: Long) {
        currentUserId = userId
    }

    fun logout() {
        currentUserId = null
    }

    fun getCurrentUserId(): Long {
        return currentUserId ?: throw IllegalStateException("No hay usuario logeado")
    }

    fun isLoggedIn(): Boolean {
        return currentUserId != null
    }
}
