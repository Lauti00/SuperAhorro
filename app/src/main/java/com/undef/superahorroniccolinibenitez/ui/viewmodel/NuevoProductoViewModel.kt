package com.undef.superahorroniccolinibenitez.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CatalogoEntity
import com.undef.superahorroniccolinibenitez.data.network.productos.ProductosApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/*
Estado de la búsqueda en la API local.

Idle       → no se buscó nada todavía (estado inicial)
Buscando   → GET en curso, mostramos spinner
Encontrado → la API devolvió el producto, campos autocompletos
NoEncontrado → 404, el usuario debe completar todo a mano
              y al guardar se hará un POST a la API
Error      → no se pudo conectar con la API
*/
sealed class ApiEstado {
    object Idle : ApiEstado()
    object Buscando : ApiEstado()
    object Encontrado : ApiEstado()
    object NoEncontrado : ApiEstado()
    data class Error(val mensaje: String) : ApiEstado()
}

data class NuevoProductoUiState(
    val codigo: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: String = "",
    val idProductoEditando: Int? = null,
    val error: String = "",
    val apiEstado: ApiEstado = ApiEstado.Idle
)

class NuevoProductoViewModel : ViewModel() {

    private val productosApiRepository = ProductosApiRepository()

    private val _uiState = MutableStateFlow(NuevoProductoUiState())
    val uiState: StateFlow<NuevoProductoUiState> = _uiState.asStateFlow()

    fun onCodigoChange(codigo: String) {
        _uiState.update {
            it.copy(
                codigo = codigo,
                error = "",
                // Al cambiar el código a mano reseteamos el estado de la API
                apiEstado = ApiEstado.Idle
            )
        }
    }

    fun onNombreChange(nombre: String) {
        _uiState.update { it.copy(nombre = nombre, error = "") }
    }

    fun onDescripcionChange(descripcion: String) {
        _uiState.update { it.copy(descripcion = descripcion, error = "") }
    }

    fun onPrecioChange(precio: String) {
        if (precio.all { it.isDigit() || it == '.' || it == ',' }) {
            _uiState.update { it.copy(precio = precio, error = "") }
        }
    }

    /*
    Recibe el EAN detectado por el escáner o escrito a mano
    y lanza la búsqueda en la API local.
    */
    fun buscarPorEan(ean: String) {
        if (ean.isBlank()) return

        _uiState.update { it.copy(codigo = ean, apiEstado = ApiEstado.Buscando) }

        viewModelScope.launch(Dispatchers.IO) {
            val resultado = productosApiRepository.buscarPorEan(ean)

            _uiState.update { current ->
                when (resultado) {
                    is ProductosApiRepository.BusquedaResult.Encontrado -> {
                        /*
                        Autocompleta nombre y descripción con los datos de la API.
                        El precio lo deja vacío para que el usuario lo complete.
                        */
                        current.copy(
                            nombre      = resultado.producto.nombre,
                            descripcion = resultado.producto.descripcion,
                            apiEstado   = ApiEstado.Encontrado
                        )
                    }
                    is ProductosApiRepository.BusquedaResult.NoEncontrado -> {
                        current.copy(apiEstado = ApiEstado.NoEncontrado)
                    }
                    is ProductosApiRepository.BusquedaResult.Error -> {
                        current.copy(apiEstado = ApiEstado.Error(resultado.mensaje))
                    }
                }
            }
        }
    }

    fun cargarParaEdicion(producto: CatalogoEntity) {
        _uiState.update {
            it.copy(
                idProductoEditando = producto.id,
                codigo             = producto.codigo,
                nombre             = producto.nombre,
                descripcion        = producto.descripcion,
                precio             = producto.precio.toString(),
                error              = "",
                apiEstado          = ApiEstado.Idle
            )
        }
    }

    fun cancelarEdicion() {
        _uiState.update { NuevoProductoUiState() }
    }

    /*
    Valida, guarda en Room y — si la API no tenía el producto —
    también hace el POST para registrarlo en la API local.
    */
    fun validarYGuardar(
        catalogoExistente: List<CatalogoEntity>,
        onCrear: (String, String, String, Double) -> Boolean,
        onEditar: (Int, String, String, String, Double) -> Unit
    ) {
        val state = _uiState.value

        val codigoTrim      = state.codigo.trim()
        val nombreTrim      = state.nombre.trim()
        val descripcionTrim = state.descripcion.trim()
        val precioLimpio    = state.precio.replace(",", ".")
        val precioDouble    = precioLimpio.toDoubleOrNull()

        /*
        Si el código ya existe en Room y NO estamos editando todavía,
        cargamos ese producto en modo edición automáticamente.
        Así el usuario puede cambiar el precio (y nombre/descripción)
        sin ver un mensaje de error.
        */
        val productoExistenteEnRoom = catalogoExistente.find {
            it.codigo == codigoTrim && it.id != state.idProductoEditando
        }

        if (productoExistenteEnRoom != null && state.idProductoEditando == null) {
            cargarParaEdicion(productoExistenteEnRoom)
            return
        }

        when {
            codigoTrim.isEmpty() ->
                _uiState.update { it.copy(error = "El código no puede estar vacío") }

            catalogoExistente.any {
                it.codigo == codigoTrim && it.id != state.idProductoEditando
            } ->
                _uiState.update { it.copy(error = "Ya existe un producto con este código") }

            nombreTrim.isEmpty() ->
                _uiState.update { it.copy(error = "El nombre no puede estar vacío") }

            nombreTrim.length < 2 ->
                _uiState.update { it.copy(error = "Nombre demasiado corto") }

            descripcionTrim.isEmpty() ->
                _uiState.update { it.copy(error = "La descripción no puede estar vacía") }

            precioLimpio.isEmpty() ->
                _uiState.update { it.copy(error = "Debes ingresar un precio") }

            precioDouble == null ->
                _uiState.update { it.copy(error = "Formato de precio inválido") }

            precioDouble <= 0 ->
                _uiState.update { it.copy(error = "El precio debe ser mayor a 0") }

            else -> {
                if (state.idProductoEditando == null) {

                    val agregado = onCrear(codigoTrim, nombreTrim, descripcionTrim, precioDouble)

                    if (!agregado) {
                        _uiState.update { it.copy(error = "Ese producto ya existe") }
                    } else {
                        /*
                        Si la API no tenía el producto (NoEncontrado), lo enviamos
                        ahora con un POST para que quede registrado en la API local.
                        El precio no se manda a la API — solo ean, nombre y descripción.
                        */
                        if (state.apiEstado is ApiEstado.NoEncontrado) {
                            viewModelScope.launch(Dispatchers.IO) {
                                productosApiRepository.guardarProducto(
                                    ean         = codigoTrim,
                                    nombre      = nombreTrim,
                                    descripcion = descripcionTrim
                                )
                            }
                        }
                        _uiState.update { NuevoProductoUiState() }
                    }

                } else {
                    onEditar(
                        state.idProductoEditando,
                        codigoTrim,
                        nombreTrim,
                        descripcionTrim,
                        precioDouble
                    )

                    /*
                    PUT a la API: actualiza nombre y descripción del producto.
                    El precio no se manda — vive solo en Room.
                    Fire-and-forget: si falla la red el UPDATE en Room ya ocurrió.
                    */
                    viewModelScope.launch(Dispatchers.IO) {
                        productosApiRepository.actualizarProducto(
                            ean         = codigoTrim,
                            nombre      = nombreTrim,
                            descripcion = descripcionTrim
                        )
                    }

                    _uiState.update { NuevoProductoUiState() }
                }
            }
        }
    }
}
