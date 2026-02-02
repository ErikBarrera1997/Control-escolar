package com.dev.sicenet.interfaces

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.sicenet.data.SNRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: SNRepository // inyectado o creado en MainActivity
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
            loginState = loginState.copy(isLoading = true, errorMessage = null)

            try {
                val result = repository.acceso(
                    loginState.matricula,
                    loginState.contrasena
                )

                loginState = loginState.copy(
                    isLoading = false,
                    isSuccess = true,
                    token = result
                )
            } catch (e: Exception) {
                loginState = loginState.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = e.message
                )
            }
        }
    }
}

