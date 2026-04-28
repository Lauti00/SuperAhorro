package com.example.superahorro.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.superahorro.ui.components.*
import com.example.superahorro.ui.viewmodel.HomeViewModel
import com.example.superahorro.model.Compra
import com.example.superahorro.model.Producto
import com.example.superahorro.model.CatalogoProducto
import com.example.superahorro.model.catalogoProductos

@OptIn(ExperimentalMaterial3Api::class)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NuevaCompraScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit,
    onCompraGuardada: () -> Unit
) {

    /*
     IMPORTANTE:
    Usamos el MISMO ViewModel que Home
    */

    //  Estado del supermercado
    var supermercado by remember { mutableStateOf("") }

    /*
     Fecha automática:
    No la escribe el usuario, se genera sola
    */
    val fecha = remember {
        java.time.LocalDate.now().toString()
    }

    /*
     Lista de productos (estado dinámico)
    */
    val productos = remember { mutableStateListOf<Producto>() }

    /*
     Catálogo:
    Lista fija de productos disponibles
    */
    val catalogo = catalogoProductos

    /*
     Producto seleccionado del catálogo
    */
    var productoSeleccionado by remember { mutableStateOf<CatalogoProducto?>(null) }

    /*
     Estado del dropdown
    */
    var expanded by remember { mutableStateOf(false) }

    /*
     Cantidad (lo único que escribe el usuario)
    */
    var cantidadProducto by remember { mutableStateOf("") }

    /*
     Total automático:
    Se calcula en base a los productos
    */
    val totalCalculado = productos.sumOf { it.subtotal() }

    SimpleScreenContainer(
        title = "Nueva Compra",
        onBack = onBack
    ) {

        //  Supermercado
        SuperAhorroTextField(
            value = supermercado,
            onValueChange = { supermercado = it },
            label = "Supermercado"
        )

        EspacioNormal()

        // Mostramos la fecha (no editable)
        Text("Fecha: $fecha")

        EspacioNormal()

        /*
         SELECTOR DE PRODUCTOS (DROPDOWN)
        */
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = productoSeleccionado?.nombre ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Seleccionar producto") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                catalogo.forEach { producto ->
                    DropdownMenuItem(
                        text = { Text("${producto.nombre} - $${producto.precio}") },
                        onClick = {
                            productoSeleccionado = producto
                            expanded = false
                        }
                    )
                }
            }
        }

        EspacioNormal()

        /*
         Cantidad (único input real del usuario)
        */
        SuperAhorroTextField(
            value = cantidadProducto,
            onValueChange = { cantidadProducto = it },
            label = "Cantidad"
        )

        EspacioPequeño()

        SuperAhorroButton(
            text = "Agregar producto",
            onClick = {

                val cantidad = cantidadProducto.toIntOrNull()

                /*
                Validaciones:
                - producto seleccionado
                - cantidad válida
                */
                if (productoSeleccionado != null && cantidad != null) {

                    productos.add(
                        Producto(
                            producto = productoSeleccionado!!,
                            cantidad = cantidad
                        )
                    )

                    // Limpiamos selección
                    productoSeleccionado = null
                    cantidadProducto = ""
                }
            }
        )

        EspacioNormal()

        // LISTA DE PRODUCTOS AGREGADOS
        productos.forEach {
            Text("${it.producto.nombre} - ${it.cantidad} x $${it.producto.precio}")
        }

        EspacioNormal()

        // TOTAL AUTOMÁTICO
        Text("Total: $${"%.2f".format(totalCalculado)}")

        EspacioGrande()

        SuperAhorroButton(
            text = "Guardar Compra",
            onClick = {

                // Generamos un ID simple
                val nuevoId = (viewModel.compras.size + 1)

                // Creamos la compra con productos
                val nuevaCompra = Compra(
                    id = nuevoId,
                    supermercado = supermercado,
                    fecha = fecha,
                    productos = productos.toList()
                )

                /*
                 ACA se guarda en el ViewModel compartido
                */
                viewModel.agregarCompra(nuevaCompra)

                // Volvemos atrás (Home)
                onCompraGuardada()
            }
        )
    }
}
