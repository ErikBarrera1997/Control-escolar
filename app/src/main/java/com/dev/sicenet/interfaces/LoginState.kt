package com.dev.sicenet.interfaces

data class LoginState(
    val matricula: String = "",
    val contrasena: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val token: String? = null
)
