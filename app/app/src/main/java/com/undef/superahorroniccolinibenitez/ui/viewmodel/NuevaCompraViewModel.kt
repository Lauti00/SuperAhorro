package com.undef.superahorroniccolinibenitez.ui.viewmodel

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.undef.superahorroniccolinibenitez.model.CatalogoProducto
import com.undef.superahorroniccolinibenitez.model.Compra
import com.undef.superahorroniccolinibenitez.model.Producto
import com.undef.superahorroniccolinibenitez.model.Supermercado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class NuevaCompraUiState(
    val supermercado: String = "",
    val supermercadoSeleccionado: Supermercado? = null,
    val expandedSupermercados: Boolean = false,
    val productoSeleccionado: CatalogoProducto? = null,
    val expanded: Boolean = false,
    val cantidadProducto: String = "",
    val imagenUri: Uri? = null,
    val productos: List<Producto> = emptyList(),
    val errorGeneral: String = "",
    val errorProducto: String = ""
) {
    val totalCalculado: Double get() = productos.sumOf { it.subtotal() }
}

@RequiresApi(Build.VERSION_CODES.O)
class NuevaCompraViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NuevaCompraUiState())
    val uiState: StateFlow<NuevaCompraUiState> = _uiState.asStateFlow()

    fun onSupermercadoChange(nuevoTexto: String) {
        _uiState.update { 
            it.copy(
                supermercado = nuevoTexto, 
                supermercadoSeleccionado = null,
                errorGeneral = ""
            ) 
        }
    }

    fun onExpandedSupermercadosChange(expanded: Boolean) {
        _uiState.update { it.copy(expandedSupermercados = expanded) }
    }

    fun onSupermercadoSeleccionado(supermercado: Supermercado) {
        _uiState.update { 
            it.copy(
                supermercadoSeleccionado = supermercado, 
                supermercado = supermercado.nombre,
                expandedSupermercados = false,
                errorGeneral = ""
            ) 
        }
    }

    fun onExpandedChange(expanded: Boolean) {
        _uiState.update { it.copy(expanded = expanded) }
    }

    fun onProductoSeleccionado(producto: CatalogoProducto) {
        _uiState.update { it.copy(productoSeleccionado = producto, expanded = false, errorProducto = "") }
    }

    fun onCantidadChange(cantidad: String) {
        _uiState.update { it.copy(cantidadProducto = cantidad, errorProducto = "") }
    }

    fun onImagenUriChange(uri: Uri?) {
        _uiState.update { it.copy(imagenUri = uri) }
    }

    fun agregarProductoLocal() {
        val state = _uiState.value
        val cantidad = state.cantidadProducto.toIntOrNull()

        if (state.productoSeleccionado == null) {
            _uiState.update { it.copy(errorProducto = "Seleccioná un producto") }
        } else if (cantidad == null || cantidad <= 0) {
            _uiState.update { it.copy(errorProducto = "Cantidad inválida") }
        } else {
            val nuevoProducto = Producto(producto = state.productoSeleccionado, cantidad = cantidad)
            _uiState.update {
                it.copy(
                    productos = it.productos + nuevoProducto,
                    productoSeleccionado = null,
                    cantidadProducto = "",
                    errorProducto = ""
                )
            }
        }
    }

    fun editarProductoLocal(producto: Producto) {
        _uiState.update {
            it.copy(
                productoSeleccionado = producto.producto,
                cantidadProducto = producto.cantidad.toString(),
                productos = it.productos - producto
            )
        }
    }

    fun eliminarProductoLocal(producto: Producto) {
        _uiState.update { it.copy(productos = it.productos - producto) }
    }

    fun validarYGuardar(idNuevaCompra: Int, onSuccess: (Compra) -> Unit) {
        val state = _uiState.value
        val superLimpio = state.supermercado.trim()

        if (superLimpio.isEmpty()) {
            _uiState.update { it.copy(errorGeneral = "El campo de supermercado no puede estar vacío") }
            return
        }
        if (state.productos.isEmpty()) {
            _uiState.update { it.copy(errorGeneral = "Agregá al menos un producto") }
            return
        }

        val superNormalizado = superLimpio.lowercase().replaceFirstChar { it.uppercase() }
        val fecha = LocalDate.now().toString()
        val hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

        val nuevaCompra = Compra(
            id = idNuevaCompra,
            supermercado = superNormalizado,
            fecha = fecha,
            hora = hora,
            productos = state.productos,
            imagenUri = state.imagenUri?.toString()
        )
        onSuccess(nuevaCompra)
    }
}