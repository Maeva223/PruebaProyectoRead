package com.inacap.iotmobileapp.utils

import java.util.regex.Pattern

/**
 * Clase de utilidades para validaciones de formularios
 */
object Validators {

    /**
     * Valida formato de email
     */
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return Pattern.compile(emailPattern).matcher(email).matches()
    }

    /**
     * Valida que la contraseña sea robusta:
     * - Mínimo 8 caracteres
     * - Al menos 1 mayúscula
     * - Al menos 1 minúscula
     * - Al menos 1 número
     * - Al menos 1 carácter especial
     */
    fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false

        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar
    }

    /**
     * Obtiene mensaje de error descriptivo para contraseña débil
     */
    fun getPasswordErrorMessage(password: String): String {
        val errors = mutableListOf<String>()

        if (password.length < 8) {
            errors.add("mínimo 8 caracteres")
        }
        if (!password.any { it.isUpperCase() }) {
            errors.add("1 mayúscula")
        }
        if (!password.any { it.isLowerCase() }) {
            errors.add("1 minúscula")
        }
        if (!password.any { it.isDigit() }) {
            errors.add("1 número")
        }
        if (!password.any { !it.isLetterOrDigit() }) {
            errors.add("1 carácter especial")
        }

        return if (errors.isNotEmpty()) {
            "Falta: ${errors.joinToString(", ")}"
        } else {
            ""
        }
    }

    /**
     * Valida que el nombre solo contenga letras y espacios
     */
    fun isValidName(name: String): Boolean {
        if (name.isBlank()) return false
        return name.all { it.isLetter() || it.isWhitespace() }
    }

    /**
     * Valida que el código sea numérico de 5 dígitos
     */
    fun isValidRecoveryCode(code: String): Boolean {
        return code.length == 5 && code.all { it.isDigit() }
    }

    /**
     * Genera un código aleatorio de 5 dígitos
     */
    fun generateRecoveryCode(): String {
        return (10000..99999).random().toString()
    }
}
