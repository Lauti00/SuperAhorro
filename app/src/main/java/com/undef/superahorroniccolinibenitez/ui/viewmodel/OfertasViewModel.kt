package com.undef.superahorroniccolinibenitez.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorroniccolinibenitez.data.network.ofertas.OfertasRepository
import com.undef.superahorroniccolinibenitez.model.OfertaSupermercado
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
/*
Estado completo de la sección de ofertas.

Esto permite que la UI sepa si está cargando,
si hay datos o si ocurrió un error.
*/
data class OfertasUiState(
    val isLoading: Boolean = false,
    val ofertas: List<OfertaSupermercado> = emptyList(),
    val error: String? = null
)

class OfertasViewModel : ViewModel() {

    private val repository =
        OfertasRepository()

    private val _uiState =
        MutableStateFlow(OfertasUiState())

    val uiState: StateFlow<OfertasUiState> =
        _uiState.asStateFlow()

    init {
        cargarOfertas()
    }

    /*
    Carga ofertas desde internet usando corrutinas.

    La llamada real se ejecuta en Dispatchers.IO
    porque es una operación de red.
    */
    fun cargarOfertas() {

        viewModelScope.launch {

            _uiState.value =
                OfertasUiState(isLoading = true)

            try {

                val ofertas =
                    withContext(Dispatchers.IO) {
                        repository.obtenerOfertas()
                    }

                _uiState.value =
                    OfertasUiState(
                        isLoading = false,
                        ofertas = ofertas,
                        error = null
                    )

            } catch (exception: Exception) {

                Log.e(
                    "OfertasViewModel",
                    "Error cargando ofertas",
                    exception
                )

                _uiState.value =
                    OfertasUiState(
                        isLoading = false,
                        ofertas = emptyList(),
                        error = "Error: ${exception.message}"
                    )
            }
        }
    }
}