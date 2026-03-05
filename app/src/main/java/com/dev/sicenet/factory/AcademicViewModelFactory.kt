package com.dev.sicenet.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dev.sicenet.dao.LocalSNRepository
import com.dev.sicenet.data.SNRepository
import com.dev.sicenet.interfaces.AcademicViewModel

/**
 * Factory del composable de la ventana de los datos académicos.
 */
class AcademicViewModelFactory(
    private val remoteRepository: SNRepository,
    private val localRepository: LocalSNRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AcademicViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AcademicViewModel(remoteRepository, localRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
