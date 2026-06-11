package com.undef.superahorroniccolinibenitez.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.undef.superahorroniccolinibenitez.model.Supermercado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class NuevoSupermercadoUiState(
    val nombre: String = "",
    val idSupermercadoEditando: Int? = null,
    val error: String = ""
)

class NuevoSupermercadoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NuevoSupermercadoUiState())
    val uiState: StateFlow<NuevoSupermercadoUiState> = _uiState.asStateFlow()

    fun onNombreChange(nombre: String) {
        _uiState.update { it.copy(nombre = nombre, error = "") }
    }

    fun cargarParaEdicion(supermercado: Supermercado) {
        _uiState.update {
            it.copy(
                idSupermercadoEditando = supermercado.id,
                nombre = supermercado.nombre,
                error = ""
            )
        }
    }

    fun cancelarEdicion() {
        _uiState.update { NuevoSupermercadoUiState() }
    }

    fun validarYGuardar(
        supermercadosExistentes: List<Supermercado>,
        onCrear: (String) -> Boolean,
        onEditar: (Int, String) -> Unit
    ) {
        val state = _uiState.value
        val nombreTrim = state.nombre.trim()

        when {
            nombreTrim.isEmpty() -> {
                _uiState.update { it.copy(error = "El nombre no puede estar vacío") }
            }
            nombreTrim.length < 2 -> {
                _uiState.update { it.copy(error = "El nombre es demasiado corto") }
            }
            supermercadosExistentes.any {
                it.nombre.trim().equals(nombreTrim, ignoreCase = true) &&
                        it.id != state.idSupermercadoEditando
            } -> {
                _uiState.update { it.copy(error = "Ya existe un supermercado con ese nombre") }
            }
            else -> {
                if (state.idSupermercadoEditando == null) {
                    val agregado = onCrear(nombreTrim)
                    if (!agregado) {
                        _uiState.update { it.copy(error = "Ese supermercado ya existe") }
                    } else {
                        _uiState.update { NuevoSupermercadoUiState() }
                    }
                } else {
                    onEditar(state.idSupermercadoEditando, nombreTrim)
                    _uiState.update { NuevoSupermercadoUiState() }
                }
            }
        }
    }
}
