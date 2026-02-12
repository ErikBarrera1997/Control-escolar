package com.dev.sicenet.interfaces

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.sicenet.data.SNRepository
import com.dev.sicenet.model.ProfileStudent
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: SNRepository
) : ViewModel() {

    var profileState by mutableStateOf(ProfileState())
        private set

    fun loadProfile(matricula: String, contrasena: String) {
        viewModelScope.launch {
            profileState = profileState.copy(isLoading = true, errorMessage = null)
            try {
                val profile = repository.profile(matricula, contrasena)
                profileState = profileState.copy(
                    isLoading = false,
                    profile = profile,
                    errorMessage = null
                )
            } catch (e: Exception) {
                profileState = profileState.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar perfil: ${e.message}"
                )
            }
        }
    }

    fun reset() {
        profileState = ProfileState() // reinicia variables
    }
}

data class ProfileState(
    val isLoading: Boolean = false,
    val profile: ProfileStudent? = null,
    val errorMessage: String? = null
)

