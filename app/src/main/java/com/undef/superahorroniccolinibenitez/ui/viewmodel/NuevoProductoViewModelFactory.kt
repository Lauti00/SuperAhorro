package com.undef.superahorroniccolinibenitez.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.undef.superahorroniccolinibenitez.data.datastore.repository.SuperAhorroRepository

/*
Factory necesaria porque NuevoProductoViewModel recibe SuperAhorroRepository
en el constructor. Sin esto, viewModel() no sabría cómo instanciarlo.
*/
class NuevoProductoViewModelFactory(
    private val repository: SuperAhorroRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return NuevoProductoViewModel(repository) as T
    }
}