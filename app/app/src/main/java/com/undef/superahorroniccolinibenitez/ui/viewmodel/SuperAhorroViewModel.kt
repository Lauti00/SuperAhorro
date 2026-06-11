package com.undef.superahorroniccolinibenitez.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CatalogoEntity
import com.undef.superahorroniccolinibenitez.data.datastore.local.entities.CompraEntity
import com.undef.superahorroniccolinibenitez.data.datastore.repository.SuperAhorroRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SuperAhorroViewModel(
    private val repository: SuperAhorroRepository
) : ViewModel() {

    // =========================
    // COMPRAS
    // =========================

    /*
    Trae todas las compras de Room en tiempo real
    */
    val listaCompras: StateFlow<List<CompraEntity>> =
        repository.todasLasCompras
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // =========================
    // CATÁLOGO
    // =========================

    /*
    Trae todos los productos del catálogo desde Room
    */
    val listaCatalogo: StateFlow<List<CatalogoEntity>> =
        repository.catalogo
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // =========================
    // COMPRAS
    // =========================

    fun guardarCompra(
        supermercado: String,
        hora: String
    ) {

        viewModelScope.launch {

            val fechaActual =
                java.text.SimpleDateFormat(
                    "dd/MM/yyyy",
                    java.util.Locale.getDefault()
                ).format(java.util.Date())

            val nuevaCompra = CompraEntity(
                supermercado = supermercado,
                hora = hora,
                fecha = fechaActual
            )

            repository.insertarCompra(nuevaCompra)
        }
    }

    // =========================
    // CATALOGO
    // =========================

    /*
    AGREGAR PRODUCTO
    */
    fun agregarProductoAlCatalogo(
        codigo: String,
        nombre: String,
        descripcion: String,
        precio: Double
    ) {

        viewModelScope.launch {

            val nuevoProducto = CatalogoEntity(
                codigo = codigo,
                nombre = nombre,
                descripcion = descripcion,
                precio = precio
            )

            repository.insertarProductoCatalogo(
                nuevoProducto
            )
        }
    }

    /*
    ACTUALIZAR PRODUCTO
    */
    fun actualizarProductoDelCatalogo(
        id: Int,
        codigo: String,
        nombre: String,
        descripcion: String,
        precio: Double
    ) {

        viewModelScope.launch {

            val productoActualizado = CatalogoEntity(
                id = id,
                codigo = codigo,
                nombre = nombre,
                descripcion = descripcion,
                precio = precio
            )

            repository.actualizarProducto(
                productoActualizado
            )
        }
    }

    /*
    ELIMINAR PRODUCTO
    */
    fun eliminarProductoDelCatalogo(
        producto: CatalogoEntity
    ) {

        viewModelScope.launch {

            repository.eliminarProducto(producto)
        }
    }
}