package com.inacap.iotmobileapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.inacap.iotmobileapp.ui.developer.DeveloperScreen
import com.inacap.iotmobileapp.ui.login.LoginScreen
import com.inacap.iotmobileapp.ui.menu.MainMenuScreen
import com.inacap.iotmobileapp.ui.recovery.CreatePasswordScreen
import com.inacap.iotmobileapp.ui.recovery.RecoveryScreen
import com.inacap.iotmobileapp.ui.register.RegisterScreen
import com.inacap.iotmobileapp.ui.sensors.SensorsScreen
import com.inacap.iotmobileapp.ui.splash.SplashScreen
import com.inacap.iotmobileapp.ui.profile.EditProfileScreen
import com.inacap.iotmobileapp.ui.users.ListUsersScreen
import com.inacap.iotmobileapp.ui.users.ModifyUserScreen
import com.inacap.iotmobileapp.ui.users.RegisterUserAdminScreen
import com.inacap.iotmobileapp.ui.users.UserManagementScreen
import com.inacap.iotmobileapp.utils.UserSession

/**
 * Grafo de navegación principal de la aplicación
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToRecovery = {
                    navController.navigate(Screen.Recovery.route)
                },
                onNavigateToMainMenu = {
                    navController.navigate(Screen.MainMenu.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Register Screen
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // Recovery Screen
        composable(Screen.Recovery.route) {
            RecoveryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCodeVerified = { email, code ->
                    navController.navigate(Screen.CreatePassword.createRoute(email, code))
                }
            )
        }

        // Create Password Screen
        composable(
            route = Screen.CreatePassword.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("code") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val code = backStackEntry.arguments?.getString("code") ?: ""

            CreatePasswordScreen(
                email = email,
                code = code,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Main Menu Screen
        composable(Screen.MainMenu.route) {
            MainMenuScreen(
                onNavigateToUserManagement = {
                    navController.navigate(Screen.UserManagement.route)
                },
                onNavigateToSensors = {
                    navController.navigate(Screen.Sensors.route)
                },
                onNavigateToDeveloper = {
                    navController.navigate(Screen.Developer.route)
                },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onLogout = {
                    UserSession.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // User Management Screen
        composable(Screen.UserManagement.route) {
            UserManagementScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.RegisterUserAdmin.route)
                },
                onNavigateToList = {
                    navController.navigate(Screen.ListUsers.route)
                }
            )
        }

        // Register User Admin Screen
        composable(Screen.RegisterUserAdmin.route) {
            RegisterUserAdminScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // List Users Screen
        composable(Screen.ListUsers.route) {
            ListUsersScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToModify = { userId ->
                    navController.navigate(Screen.ModifyUser.createRoute(userId))
                }
            )
        }

        // Modify User Screen
        composable(
            route = Screen.ModifyUser.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L

            ModifyUserScreen(
                userId = userId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Sensors Screen
        composable(Screen.Sensors.route) {
            SensorsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Developer Screen
        composable(Screen.Developer.route) {
            DeveloperScreen(
                userId = UserSession.getCurrentUserId(),
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Edit Profile Screen
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                userId = UserSession.getCurrentUserId(),
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
