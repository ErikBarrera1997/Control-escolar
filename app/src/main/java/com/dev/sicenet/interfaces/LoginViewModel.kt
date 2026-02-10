package com.dev.sicenet.interfaces

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.sicenet.data.SNRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: SNRepository
) : ViewModel() {

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
            // Reiniciar estado al iniciar login
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
                } else {
                    // Credenciales inválidas
                    loginState = loginState.copy(
                        isLoading = false,
                        isSuccess = false,
                        token = "",
                        errorMessage = "Credenciales inválidas"
                    )
                }
            } catch (e: Exception) {
                // Error de conexión
                loginState = loginState.copy(
                    isLoading = false,
                    isSuccess = false,
                    token = "",
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }

}
