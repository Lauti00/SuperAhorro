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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
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
     URI de la imagen del ticket
    */
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

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
    ) { success ->
        if (success) {
            // La imagen ya quedó guardada en imagenUri
        }
    }

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

        EspacioNormal()

        /*
         BOTONES PARA ADJUNTAR TICKET
        */
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
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

        /*
  MOSTRAR IMAGEN REAL DEL TICKET
 Si el usuario seleccionó o sacó una foto,
 la mostramos directamente en pantalla
*/
        imagenUri?.let { uri ->

            Spacer(modifier = Modifier.height(12.dp))

            Text("Ticket cargado:", style = MaterialTheme.typography.labelLarge)

            Spacer(modifier = Modifier.height(8.dp))

            /*
             AsyncImage:
             Componente de Coil que carga imágenes desde una URI
             (puede ser cámara, galería, internet, etc)
            */
            coil.compose.AsyncImage(
                model = uri, // 🔥 acá le pasamos la imagen
                contentDescription = "Ticket de compra",

                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),

                // Opcional: ajusta cómo se ve la imagen
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        }

        EspacioGrande()

        SuperAhorroButton(
            text = "Guardar Compra",
            onClick = {

                // Generamos un ID simple
                val nuevoId = (viewModel.compras.size + 1)

                // Creamos la compra con productos + imagen
                val nuevaCompra = Compra(
                    id = nuevoId,
                    supermercado = supermercado,
                    fecha = fecha,
                    productos = productos.toList(),
                    imagenUri = imagenUri?.toString() // 🔥 guardamos la URI
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
