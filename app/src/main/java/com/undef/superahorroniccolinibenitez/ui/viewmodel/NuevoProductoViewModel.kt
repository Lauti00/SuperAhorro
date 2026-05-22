package com.undef.superahorroniccolinibenitez.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.undef.superahorroniccolinibenitez.model.CatalogoProducto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class NuevoProductoUiState(
    val codigo: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: String = "",
    val idProductoEditando: Int? = null,
    val error: String = ""
)

class NuevoProductoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NuevoProductoUiState())
    val uiState: StateFlow<NuevoProductoUiState> = _uiState.asStateFlow()

    fun onCodigoChange(codigo: String) = _uiState.update { it.copy(codigo = codigo, error = "") }
    fun onNombreChange(nombre: String) = _uiState.update { it.copy(nombre = nombre, error = "") }
    fun onDescripcionChange(descripcion: String) = _uiState.update { it.copy(descripcion = descripcion, error = "") }

    fun onPrecioChange(precio: String) {
        if (precio.all { char -> char.isDigit() || char == '.' || char == ',' }) {
            _uiState.update { it.copy(precio = precio, error = "") }
        }
    }

    fun cargarParaEdicion(producto: CatalogoProducto) {
        _uiState.update {
            it.copy(
                idProductoEditando = producto.id,
                codigo = producto.codigo,
                nombre = producto.nombre,
                descripcion = producto.descripcion,
                precio = producto.precio.toString(),
                error = ""
            )
        }
    }

    fun cancelarEdicion() {
        _uiState.update { NuevoProductoUiState() }
    }

    fun validarYGuardar(
        catalogoExistente: List<CatalogoProducto>,
        onCrear: (String, String, String, Double) -> Boolean,
        onEditar: (Int, String, String, String, Double) -> Unit
    ) {
        val state = _uiState.value
        val codigoTrim = state.codigo.trim()
        val nombreTrim = state.nombre.trim()
        val descripcionTrim = state.descripcion.trim()
        val precioLimpio = state.precio.replace(",", ".")
        val precioDouble = precioLimpio.toDoubleOrNull()

        when {
            codigoTrim.isEmpty() -> _uiState.update { it.copy(error = "El código no puede estar vacío") }
            catalogoExistente.any { it.codigo == codigoTrim && it.id != state.idProductoEditando } ->
                _uiState.update { it.copy(error = "Ya existe un producto con este código") }
            nombreTrim.isEmpty() -> _uiState.update { it.copy(error = "El nombre no puede estar vacío") }
            nombreTrim.length < 2 -> _uiState.update { it.copy(error = "Nombre demasiado corto") }
            descripcionTrim.isEmpty() -> _uiState.update { it.copy(error = "La descripción no puede estar vacía") }
            precioLimpio.isEmpty() -> _uiState.update { it.copy(error = "Debes ingresar un precio") }
            precioDouble == null -> _uiState.update { it.copy(error = "Formato de precio inválido") }
            precioDouble <= 0 -> _uiState.update { it.copy(error = "El precio debe ser mayor a 0") }
            else -> {
                if (state.idProductoEditando == null) {
                    val agregado = onCrear(codigoTrim, nombreTrim, descripcionTrim, precioDouble)
                    if (!agregado) {
                        _uiState.update { it.copy(error = "Ese producto ya existe") }
                    } else {
                        _uiState.update { NuevoProductoUiState() }
                    }
                } else {
                    onEditar(state.idProductoEditando, codigoTrim, nombreTrim, descripcionTrim, precioDouble)
                    _uiState.update { NuevoProductoUiState() }
                }
            }
        }
    }
}