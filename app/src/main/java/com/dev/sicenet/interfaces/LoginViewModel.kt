package com.dev.sicenet.interfaces

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.dev.sicenet.data.SNRepository
import com.dev.sicenet.workers.FetchAcademicDataWorker
import com.dev.sicenet.workers.SaveAcademicDataWorker
import kotlinx.coroutines.launch

class LoginViewModel(
    application: Application,
    private val repository: SNRepository,
) : AndroidViewModel(application)  {

    var loginState by mutableStateOf(LoginState())
        private set

    fun onMatriculaChanged(newValue: String) {
        loginState = loginState.copy(matricula = newValue)
    }

    fun onContrasenaChanged(newValue: String) {
        loginState = loginState.copy(contrasena = newValue)
    }

    fun login() {
        viewModelScope.launch {
            loginState = loginState.copy(
                isLoading = true,
                isSuccess = false,
                token = "",
                errorMessage = null
            )

            try {
                val token = repository.acceso(loginState.matricula, loginState.contrasena)

                if (token.isNotBlank()) {
                    // Autenticación exitosa
                    loginState = loginState.copy(
                        isLoading = false,
                        isSuccess = true,
                        token = token,
                        errorMessage = null
                    )

                    syncProfileAndUsuario(loginState.matricula, loginState.contrasena) ///AQUI SE USAN LOS WORKERS DE FETCH Y SAVE
                } else {
                    loginState = loginState.copy(
                        isLoading = false,
                        isSuccess = false,
                        token = "",
                        errorMessage = "Credenciales inválidas"
                    )
                }
            } catch (e: Exception) {
                loginState = loginState.copy(
                    isLoading = false,
                    isSuccess = false,
                    token = "",
                    errorMessage = "No se pudo conectar al servidor"
                )
            }
        }
    }

    private fun syncProfileAndUsuario(matricula: String, password: String) {
        val fetchWork = OneTimeWorkRequestBuilder<FetchAcademicDataWorker>()
            .setInputData(
                workDataOf(
                    "matricula" to matricula,
                    "password" to password
                )
            )
            .build()

        val saveWork = OneTimeWorkRequestBuilder<SaveAcademicDataWorker>().build()

        WorkManager.getInstance(getApplication())
            .beginUniqueWork("syncPerfil", ExistingWorkPolicy.REPLACE, fetchWork)
            .then(saveWork)
            .enqueue()
    }

}


