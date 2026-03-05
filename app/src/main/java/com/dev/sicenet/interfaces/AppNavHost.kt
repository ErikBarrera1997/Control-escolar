package com.dev.sicenet.interfaces

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dev.sicenet.dao.DatabaseProvider
import com.dev.sicenet.dao.LocalSNRepository
import com.dev.sicenet.data.NetworSNRepository
import com.dev.sicenet.data.SNRepository
import com.dev.sicenet.factory.AcademicViewModelFactory
import com.dev.sicenet.factory.LoginViewModelFactory
import com.dev.sicenet.factory.ProfileViewModelFactory
import com.dev.sicenet.network.SICENETWService


@SuppressLint("NewApi")
@Composable
fun AppNavHost(
    navController: NavHostController,
    loginFactory: LoginViewModelFactory,
    repository: SNRepository,
    innerPadding: PaddingValues,
    service: SICENETWService
) {
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = Modifier.padding(innerPadding)
    ) {
        // Pantalla de login
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

        // Pantalla de perfil
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

            // Aquí se usa la API
            LaunchedEffect(matricula, contrasena) {
                viewModel.loadProfile(matricula, contrasena)
            }

            ProfileScreen(
                viewModel = viewModel,
                navController = navController,
                matricula = matricula,
                contrasena = contrasena
            )

        }

        /**
         * Pantalla de los datos académicos
         * Carga todos los datos en una lazyColumn
         * SOLAMENTE SE CARGAN DESPUÉS DE CARGAR LOS DATOS PRINCIPALES.
         */
        composable(
            "academicData/{matricula}/{contrasena}",
            arguments = listOf(
                navArgument("matricula") { type = NavType.StringType },
                navArgument("contrasena") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val matricula = backStackEntry.arguments?.getString("matricula") ?: ""
            val contrasena = backStackEntry.arguments?.getString("contrasena") ?: ""

            val remoteRepository = NetworSNRepository(service)
            val context = LocalContext.current
            val database = DatabaseProvider.getDatabase(context)
            val localRepository = LocalSNRepository(database.academicDao())

            val academicFactory = AcademicViewModelFactory(remoteRepository, localRepository)
            val viewModel: AcademicViewModel = viewModel(factory = academicFactory)

            LaunchedEffect(matricula, contrasena) {
                viewModel.loadAcademicData(hasInternet = true, matricula, contrasena)
            }

            AcademicDataScreen(
                viewModel = viewModel,
                navController = navController
            )
        }





    }
}


