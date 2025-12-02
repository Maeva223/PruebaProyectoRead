package com.inacap.iotmobileapp.ui.navigation

/**
 * Rutas de navegación de la aplicación
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Recovery : Screen("recovery")
    object CreatePassword : Screen("create_password/{email}/{code}") {
        fun createRoute(email: String, code: String) = "create_password/$email/$code"
    }
    object MainMenu : Screen("main_menu")
    object UserManagement : Screen("user_management")
    object RegisterUserAdmin : Screen("register_user_admin")
    object ListUsers : Screen("list_users")
    object ModifyUser : Screen("modify_user/{userId}") {
        fun createRoute(userId: Long) = "modify_user/$userId"
    }
    object Sensors : Screen("sensors")
    object Developer : Screen("developer")
    object EditProfile : Screen("edit_profile")
}
