package com.inacap.iotmobileapp.utils

import com.inacap.iotmobileapp.data.database.entities.User

/**
 * Objeto singleton para mantener la sesión del usuario logeado
 * ACTUALIZADO para Evaluación III: Almacena objeto User completo con token, rol y departamento
 */
object UserSession {
    // Usuario completo (incluye token, rol, departamento)
    var currentUser: User? = null
        private set

    // Mantener compatibilidad con código legacy
    private var currentUserId: Long? = null

    /**
     * Login con objeto User completo (NUEVO - Recomendado)
     * Usar después de login exitoso para guardar token JWT
     */
    fun login(user: User) {
        currentUser = user
        currentUserId = user.id
    }

    /**
     * Login con solo userId (Legacy - mantener compatibilidad)
     */
    fun login(userId: Long) {
        currentUserId = userId
    }

    /**
     * Logout - Limpia toda la sesión
     */
    fun logout() {
        currentUser = null
        currentUserId = null
    }

    /**
     * Obtiene el ID del usuario actual
     */
    fun getCurrentUserId(): Long {
        return currentUser?.id ?: currentUserId
            ?: throw IllegalStateException("No hay usuario logeado")
    }

    /**
     * Verifica si hay un usuario logeado
     */
    fun isLoggedIn(): Boolean {
        return currentUser != null || currentUserId != null
    }

    /**
     * Obtiene el token JWT del usuario (para APIs)
     */
    fun getAuthToken(): String? {
        return currentUser?.token
    }

    /**
     * Verifica si el usuario actual es ADMIN
     */
    fun isAdmin(): Boolean {
        return currentUser?.rol == "ADMIN"
    }

    /**
     * Obtiene el ID del departamento del usuario
     */
    fun getDepartmentId(): Int? {
        return currentUser?.id_departamento
    }

    /**
     * Actualiza el token del usuario (después de refresh)
     */
    fun updateToken(newToken: String) {
        currentUser = currentUser?.copy(token = newToken)
    }
}
