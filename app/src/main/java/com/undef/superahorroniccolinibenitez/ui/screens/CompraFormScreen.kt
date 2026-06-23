package com.undef.superahorroniccolinibenitez.ui.screens

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import java.io.File
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorroniccolinibenitez.ui.components.*
import com.undef.superahorroniccolinibenitez.ui.viewmodel.HomeViewModel
import com.undef.superahorroniccolinibenitez.ui.viewmodel.NuevaCompraViewModel
import com.undef.superahorroniccolinibenitez.R
import com.undef.superahorroniccolinibenitez.model.Compra
import kotlinx.coroutines.launch

/*
Pantalla única para alta y edición de compras.

Antes existían dos archivos casi idénticos (NuevaCompraScreen y
EditarCompraScreen). Se unificaron en este composable porque la
única diferencia real entre "agregar" y "editar" es:
  1. si hay que precargar datos de una compra existente
  2. qué textos mostrar (título, subtítulo, botón final)
  3. si el guardado hace un alta o una actualización en Room

`compraExistente == null`  -> modo alta (antes NuevaCompraScreen)
`compraExistente != null`  -> modo edición (antes EditarCompraScreen)
*/
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CompraFormScreen(
    homeViewModel: HomeViewModel,
    compraExistente: Compra? = null,
    compraViewModel: NuevaCompraViewModel = viewModel(),
    onBack: () -> Unit,
    onCompraGuardada: () -> Unit,
    onNavigateToNuevoProducto: () -> Unit,
    onNavigateToNuevoSupermercado: () -> Unit
) {
    val esEdicion = compraExistente != null

    val localContext = LocalContext.current
    val scope = rememberCoroutineScope()

    val state by compraViewModel.uiState.collectAsState()
    val catalogo by homeViewModel.catalogo.collectAsState()
    val supermercados by homeViewModel.supermercados.collectAsState()

    /*
    Si estamos editando, precargamos los datos de la compra existente
    la primera vez que se abre la pantalla. La clave compra.id en
    LaunchedEffect garantiza que solo se ejecute una vez por compra,
    no en cada recomposición.
    */
    LaunchedEffect(compraExistente?.id) {
        if (compraExistente != null) {
            compraViewModel.cargarCompraParaEdicion(compraExistente)
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> compraViewModel.onImagenUriChange(uri) }

    val crearImagenUri: (Context) -> Uri = remember {
        { ctx ->
            val file = File.createTempFile("ticket_", ".jpg", ctx.cacheDir)
            FileProvider.getUriForFile(ctx, "${ctx.packageName}.provider", file)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
            compraViewModel.onImagenUriChange(null)
        }
    }

    val permisoCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            val uri = crearImagenUri(localContext)
            compraViewModel.onImagenUriChange(uri)
            cameraLauncher.launch(uri)
        }
    }

    val tieneCamara =
        localContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

    // Textos que cambian según el modo
    val tituloPantalla = if (esEdicion) {
        stringResource(id = R.string.title_editar_compra)
    } else {
        stringResource(id = R.string.title_nueva_compra)
    }

    val subtituloForm = if (esEdicion) {
        stringResource(id = R.string.editar_compra_subtitle)
    } else {
        stringResource(id = R.string.nueva_compra_form_subtitle)
    }

    val textoBotonGuardar = if (esEdicion) {
        stringResource(id = R.string.btn_guardar_cambios)
    } else {
        stringResource(id = R.string.btn_guardar_compra)
    }

    SimpleScreenContainer(
        title = tituloPantalla,
        onBack = onBack
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {

                SuperAhorroCard {

                    SuperAhorroSectionTitle(
                        title = tituloPantalla,
                        subtitle = subtituloForm
                    )

                    EspacioNormal()

                    // Dropdown de supermercados
                    ExposedDropdownMenuBox(
                        expanded = state.expandedSupermercados,
                        onExpandedChange = { compraViewModel.onExpandedSupermercadosChange(!state.expandedSupermercados) }
                    ) {
                        OutlinedTextField(
                            value = state.supermercado,
                            onValueChange = { compraViewModel.onSupermercadoChange(it) },
                            label = { Text(stringResource(id = R.string.label_supermercado)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.expandedSupermercados) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        )

                        ExposedDropdownMenu(
                            expanded = state.expandedSupermercados,
                            onDismissRequest = { compraViewModel.onExpandedSupermercadosChange(false) }
                        ) {
                            supermercados.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s.nombre) },
                                    onClick = { compraViewModel.onSupermercadoSeleccionado(s) }
                                )
                            }
                        }
                    }

                    EspacioPequeño()

                    SuperAhorroTextButton(
                        text = stringResource(id = R.string.btn_crear_supermercado),
                        onClick = onNavigateToNuevoSupermercado
                    )

                    EspacioNormal()

                    // Dropdown de productos del catálogo
                    ExposedDropdownMenuBox(
                        expanded = state.expanded,
                        onExpandedChange = { compraViewModel.onExpandedChange(!state.expanded) }
                    ) {
                        OutlinedTextField(
                            value = state.productoSeleccionado?.nombre ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(id = R.string.placeholder_seleccionar_producto)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        )

                        ExposedDropdownMenu(
                            expanded = state.expanded,
                            onDismissRequest = { compraViewModel.onExpandedChange(false) }
                        ) {
                            catalogo.forEach { producto ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            stringResource(
                                                id = R.string.formato_producto_dropdown,
                                                producto.nombre,
                                                "%.2f".format(producto.precio)
                                            )
                                        )
                                    },
                                    onClick = { compraViewModel.onProductoSeleccionado(producto) }
                                )
                            }
                        }
                    }

                    SuperAhorroTextButton(
                        text = stringResource(id = R.string.btn_crear_producto),
                        onClick = onNavigateToNuevoProducto
                    )

                    // Cantidad
                    SuperAhorroTextField(
                        value = state.cantidadProducto,
                        onValueChange = { compraViewModel.onCantidadChange(it) },
                        label = stringResource(id = R.string.label_cantidad)
                    )

                    EspacioPequeño()

                    // Precio editable — pre-relleno con el valor del catálogo al elegir producto
                    SuperAhorroTextField(
                        value = state.precioProducto,
                        onValueChange = { compraViewModel.onPrecioChange(it) },
                        label = stringResource(id = R.string.label_precio_placeholder)
                    )

                    EspacioPequeño()

                    SuperAhorroButton(
                        text = stringResource(id = R.string.btn_agregar_producto),
                        onClick = { compraViewModel.agregarProductoLocal() },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (state.errorProducto.isNotEmpty()) {
                        EspacioPequeño()
                        Text(
                            text = state.errorProducto,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                EspacioNormal()

                // Lista de productos agregados a esta compra
                if (state.productos.isNotEmpty()) {

                    SuperAhorroSectionTitle(
                        title = stringResource(id = R.string.title_productos_agregados),
                        subtitle = stringResource(id = R.string.nueva_compra_productos_subtitle)
                    )

                    EspacioPequeño()

                    state.productos.forEach { item ->

                        SuperAhorroCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.producto.nombre,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = stringResource(
                                            id = R.string.label_formato_subtotal,
                                            item.cantidad,
                                            item.producto.precio,
                                            "%.2f".format(item.subtotal())
                                        ),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Editar: sube el producto de vuelta al formulario
                                IconButton(onClick = {
                                    compraViewModel.editarProductoLocal(item)
                                }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = stringResource(id = R.string.cd_editar),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                // Eliminar: quita el producto de la lista
                                IconButton(onClick = {
                                    compraViewModel.eliminarProductoLocal(item)
                                }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = stringResource(id = R.string.cd_eliminar),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }

                        EspacioPequeño()
                    }
                }

                EspacioNormal()

                // Total dinámico
                SuperAhorroCard {
                    Text(
                        text = stringResource(
                            id = R.string.label_total_dinamico,
                            "%.2f".format(state.totalCalculado)
                        ),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Ticket adjunto
                state.imagenUri?.let { uri ->
                    EspacioNormal()

                    SuperAhorroSectionTitle(
                        title = stringResource(id = R.string.label_ticket_cargado)
                    )

                    EspacioPequeño()

                    SuperAhorroCard {
                        coil.compose.AsyncImage(
                            model = uri,
                            contentDescription = stringResource(id = R.string.cd_ticket),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Barra inferior: foto + guardar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                if (state.errorGeneral.isNotEmpty()) {
                    Text(
                        text = state.errorGeneral,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    EspacioPequeño()
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuperAhorroButton(
                        text = stringResource(id = R.string.btn_galeria),
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    )

                    SuperAhorroButton(
                        text = stringResource(id = R.string.btn_camara),
                        onClick = {
                            val permiso = android.Manifest.permission.CAMERA
                            val tienePermiso = androidx.core.content.ContextCompat.checkSelfPermission(
                                localContext, permiso
                            ) == PackageManager.PERMISSION_GRANTED

                            if (tienePermiso) {
                                val uri = crearImagenUri(localContext)
                                compraViewModel.onImagenUriChange(uri)
                                cameraLauncher.launch(uri)
                            } else {
                                permisoCameraLauncher.launch(permiso)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = tieneCamara
                    )
                }

                EspacioPequeño()

                /*
                Si es alta: idNuevaCompra = 0, Room autogenera el ID (INSERT).
                Si es edición: idNuevaCompra = compraExistente.id, y se preservan
                fecha y hora originales para que editar precios no las pise.
                */
                SuperAhorroButton(
                    text = textoBotonGuardar,
                    onClick = {
                        val idParaGuardar = compraExistente?.id ?: 0

                        compraViewModel.validarYGuardar(
                            idNuevaCompra = idParaGuardar
                        ) { compraResultado ->
                            scope.launch {
                                val compraFinal = if (esEdicion && compraExistente != null) {
                                    compraResultado.copy(
                                        fecha = compraExistente.fecha,
                                        hora = compraExistente.hora,
                                        productos = state.productos
                                    )
                                } else {
                                    compraResultado.copy(productos = state.productos)
                                }

                                if (esEdicion) {
                                    homeViewModel.editarCompra(compraFinal)
                                    onCompraGuardada()
                                } else {
                                    /*
                                    agregarCompra es suspend: esperamos a que termine
                                    completamente (Room + POST) antes de navegar.
                                    Así el scope de la corrutina no se destruye
                                    antes de que el POST llegue al servidor.
                                    */
                                    homeViewModel.agregarCompra(compraFinal)
                                    onCompraGuardada()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
