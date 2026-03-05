package com.dev.sicenet.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dev.sicenet.data.SNRepository
import com.dev.sicenet.interfaces.LoginViewModel

/**
 * Factory del composable de la ventana de la ventana principal (login).
 */
class LoginViewModelFactory(
    private val application: Application,
    private val repository: SNRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

