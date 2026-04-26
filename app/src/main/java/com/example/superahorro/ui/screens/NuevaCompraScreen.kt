package com.example.superahorro.ui.screens

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Text
import com.example.superahorro.ui.components.*
import com.example.superahorro.ui.viewmodel.HomeViewModel
import com.example.superahorro.model.Compra
import com.example.superahorro.model.Producto
import com.example.superahorro.navigation.AppScreens

@Composable
fun NuevaCompraScreen(    onBack: () -> Unit,
                          onCompraGuardada: () -> Unit
)
{

    /*
     IMPORTANTE:
    Usamos el MISMO ViewModel que Home
    */
    val viewModel: HomeViewModel = viewModel(
        navController.getBackStackEntry(AppScreens.Home.route)
    )

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

    //  Estados del formulario de producto
    var nombreProducto by remember { mutableStateOf("") }
    var precioProducto by remember { mutableStateOf("") }
    var cantidadProducto by remember { mutableStateOf("") }

    /*
     Total automático:
    Se calcula en base a los productos
    */
    val total = productos.sumOf { it.precio * it.cantidad }

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

        //  FORMULARIO DE PRODUCTO
        SuperAhorroTextField(
            value = nombreProducto,
            onValueChange = { nombreProducto = it },
            label = "Nombre producto"
        )

        SuperAhorroTextField(
            value = precioProducto,
            onValueChange = { precioProducto = it },
            label = "Precio"
        )

        SuperAhorroTextField(
            value = cantidadProducto,
            onValueChange = { cantidadProducto = it },
            label = "Cantidad"
        )

        EspacioPequeño()

        SuperAhorroButton(
            text = "Agregar producto",
            onClick = {

                val precio = precioProducto.toDoubleOrNull()
                val cantidad = cantidadProducto.toIntOrNull()

                /*
                Validaciones básicas:
                - nombre no vacío
                - precio válido
                - cantidad válida
                */
                if (nombreProducto.isNotBlank() && precio != null && cantidad != null) {

                    productos.add(
                        Producto(
                            nombre = nombreProducto,
                            precio = precio,
                            cantidad = cantidad
                        )
                    )

                    // Limpiamos campos
                    nombreProducto = ""
                    precioProducto = ""
                    cantidadProducto = ""
                }
            }
        )

        EspacioNormal()

        // LISTA DE PRODUCTOS AGREGADOS
        productos.forEach {
            Text("${it.nombre} - ${it.cantidad} x $${it.precio}")
        }

        EspacioNormal()

        // TOTAL AUTOMÁTICO
        Text("Total: $${"%.2f".format(total)}")

        EspacioGrande()

        SuperAhorroButton(
            text = "Guardar Compra",
            onClick = {

                // Generamos un ID simple
                val nuevoId = (viewModel.compras.size + 1)

                // Creamos la compra con productos y total automático
                val nuevaCompra = Compra(
                    id = nuevoId,
                    supermercado = supermercado,
                    fecha = fecha,
                    total = total,
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
