package com.example.superahorro.ui.screens

import android.content.Context
import android.net.Uri
import android.os.Build
import java.io.File
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.superahorro.ui.components.*
import com.example.superahorro.ui.viewmodel.HomeViewModel
import com.example.superahorro.model.Compra
import com.example.superahorro.model.Producto
import com.example.superahorro.model.CatalogoProducto

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NuevaCompraScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit,
    onCompraGuardada: () -> Unit,
    onNavigateToNuevoProducto: () -> Unit // 🔥 NUEVO
) {
    val localContext = LocalContext.current

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
    Ahora viene del ViewModel (dinámico)
    */
    val catalogo = viewModel.catalogo

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
     URI de la imagen del ticket
    */
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    /*
     ESTADOS DE ERROR
    */
    var errorGeneral by remember { mutableStateOf("") }
    var errorProducto by remember { mutableStateOf("") }

    /*
     Launcher para abrir galería
    */
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagenUri = uri
    }

    /*
     Total automático:
    Se calcula en base a los productos
    */
    val totalCalculado = productos.sumOf { it.subtotal() }

    // Crear archivo temporal para la foto
    fun crearImagenUri(ctx: Context): Uri {
        val file = File.createTempFile(
            "ticket_",
            ".jpg",
            ctx.cacheDir
        )
        return FileProvider.getUriForFile(
            ctx,
            "${ctx.packageName}.provider",
            file
        )
    }

    // Launcher de cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { }

    SimpleScreenContainer(
        title = "Nueva Compra",
        onBack = onBack
    ) {

        Column(modifier = Modifier.fillMaxWidth()) {

            //  Supermercado
            SuperAhorroTextField(
                value = supermercado,
                onValueChange = {
                    supermercado = it
                    errorGeneral = ""
                },
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
                                errorProducto = ""
                            }
                        )
                    }
                }
            }

            EspacioPequeño()

            /*
            BOTÓN PARA CREAR PRODUCTO
            */
            SuperAhorroTextButton(
                text = "¿No está en la lista? Crear producto",
                onClick = {
                    onNavigateToNuevoProducto()
                }
            )

            EspacioNormal()

            /*
         Cantidad (único input real del usuario)
        */
            SuperAhorroTextField(
                value = cantidadProducto,
                onValueChange = {
                    cantidadProducto = it
                    errorProducto = ""
                },
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
                    when {
                        productoSeleccionado == null -> {
                            errorProducto = "Seleccioná un producto"
                        }

                        cantidad == null || cantidad <= 0 -> {
                            errorProducto = "Cantidad inválida"
                        }

                        else -> {
                            productos.add(
                                Producto(
                                    producto = productoSeleccionado!!,
                                    cantidad = cantidad
                                )
                            )

                            // Limpiamos selección
                            productoSeleccionado = null
                            cantidadProducto = ""
                            errorProducto = ""
                        }
                    }
                }
            )

            /*
            ERROR DE PRODUCTO
            */
            if (errorProducto.isNotEmpty()) {
                EspacioPequeño()
                Text(errorProducto, color = MaterialTheme.colorScheme.error)
            }

            EspacioNormal()

            // Contenido scrolleable
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {

                // LISTA DE PRODUCTOS AGREGADOS
                productos.forEach {
                    Text("${it.producto.nombre} - ${it.cantidad} x $${it.producto.precio}")
                }

                EspacioNormal()

                // TOTAL AUTOMÁTICO
                Text("Total: $${"%.2f".format(totalCalculado)}")

                EspacioNormal()

                imagenUri?.let { uri ->

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Ticket cargado:", style = MaterialTheme.typography.labelLarge)

                    Spacer(modifier = Modifier.height(8.dp))

                    /*
MOSTRAR IMAGEN REAL DEL TICKET
*/
                    coil.compose.AsyncImage(
                        model = uri,
                        contentDescription = "Ticket de compra",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
            }

            /*
            ERROR GENERAL
            */
            if (errorGeneral.isNotEmpty()) {
                Text(errorGeneral, color = MaterialTheme.colorScheme.error)
                EspacioPequeño()
            }

            //BOTONES FIJOS ABAJO
            Column {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    /*
         BOTONES PARA ADJUNTAR TICKET
        */
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SuperAhorroButton(
                            text = "Galería",
                            onClick = {
                                imagePickerLauncher.launch("image/*")
                            },
                            modifier = Modifier.weight(1f)
                        )

                        SuperAhorroButton(
                            text = "Cámara",
                            onClick = {
                                val uri = crearImagenUri(localContext)
                                imagenUri = uri
                                cameraLauncher.launch(uri)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    SuperAhorroButton(
                        text = "Guardar Compra",
                        onClick = {

                            /*
                            VALIDACIONES FINALES
                            */
                            when {
                                supermercado.isBlank() -> {
                                    errorGeneral = "Ingresá el supermercado"
                                }

                                productos.isEmpty() -> {
                                    errorGeneral = "Agregá al menos un producto"
                                }

                                else -> {

                                    val nuevoId = (viewModel.compras.size + 1)

                                    val nuevaCompra = Compra(
                                        id = nuevoId,
                                        supermercado = supermercado,
                                        fecha = fecha,
                                        productos = productos.toList(),
                                        imagenUri = imagenUri?.toString()
                                    )

                                    viewModel.agregarCompra(nuevaCompra)

                                    onCompraGuardada()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}