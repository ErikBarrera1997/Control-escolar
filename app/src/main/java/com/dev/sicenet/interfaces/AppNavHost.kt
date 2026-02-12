package com.dev.sicenet.interfaces

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dev.sicenet.data.SNRepository
import com.dev.sicenet.factory.LoginViewModelFactory
import com.dev.sicenet.factory.ProfileViewModelFactory


@SuppressLint("NewApi")
@Composable
fun AppNavHost(
    navController: NavHostController,
    loginFactory: LoginViewModelFactory,
    repository: SNRepository,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = Modifier.padding(innerPadding)
    ) {
        composable("login") {
            val viewModel: LoginViewModel = viewModel(factory = loginFactory)

            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { matricula, contrasena ->
                    navController.navigate("profile/$matricula/$contrasena") {
                        // limpia el backstack hasta login
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            "profile/{matricula}/{contrasena}",
            arguments = listOf(
                navArgument("matricula") { type = NavType.StringType },
                navArgument("contrasena") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val matricula = backStackEntry.arguments?.getString("matricula") ?: ""
            val contrasena = backStackEntry.arguments?.getString("contrasena") ?: ""

            val profileFactory = ProfileViewModelFactory(repository)
            val viewModel: ProfileViewModel = viewModel(factory = profileFactory)

            //Aqui se usa la api
            LaunchedEffect(matricula, contrasena) {
                viewModel.loadProfile(matricula, contrasena)
            }

            ProfileScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}

