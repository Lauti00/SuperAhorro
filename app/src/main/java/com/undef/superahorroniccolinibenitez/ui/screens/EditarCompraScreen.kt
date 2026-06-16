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

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditarCompraScreen(
    compra: Compra,
    homeViewModel: HomeViewModel,
    editarCompraViewModel: NuevaCompraViewModel = viewModel(),
    onBack: () -> Unit,
    onCompraGuardada: () -> Unit,
    onNavigateToNuevoProducto: () -> Unit,
    onNavigateToNuevoSupermercado: () -> Unit
) {
    val localContext = LocalContext.current
    val scope = rememberCoroutineScope()

    val state by editarCompraViewModel.uiState.collectAsState()
    val catalogo by homeViewModel.catalogo.collectAsState()
    val supermercados by homeViewModel.supermercados.collectAsState()

    /*
    Precargamos los datos de la compra existente la primera vez que
    se abre la pantalla. LaunchedEffect con la clave compra.id garantiza
    que solo se ejecuta una vez por compra, no en cada recomposición.
    */
    LaunchedEffect(compra.id) {
        editarCompraViewModel.cargarCompraParaEdicion(compra)
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> editarCompraViewModel.onImagenUriChange(uri) }

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
            editarCompraViewModel.onImagenUriChange(null)
        }
    }

    val permisoCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            val uri = crearImagenUri(localContext)
            editarCompraViewModel.onImagenUriChange(uri)
            cameraLauncher.launch(uri)
        }
    }

    val tieneCamara =
        localContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

    SimpleScreenContainer(
        title = stringResource(id = R.string.title_editar_compra),
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
                        title = stringResource(id = R.string.title_editar_compra),
                        subtitle = stringResource(id = R.string.editar_compra_subtitle)
                    )

                    EspacioNormal()

                    // Dropdown de supermercados
                    ExposedDropdownMenuBox(
                        expanded = state.expandedSupermercados,
                        onExpandedChange = { editarCompraViewModel.onExpandedSupermercadosChange(!state.expandedSupermercados) }
                    ) {
                        OutlinedTextField(
                            value = state.supermercado,
                            onValueChange = { editarCompraViewModel.onSupermercadoChange(it) },
                            label = { Text(stringResource(id = R.string.label_supermercado)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.expandedSupermercados) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        )

                        ExposedDropdownMenu(
                            expanded = state.expandedSupermercados,
                            onDismissRequest = { editarCompraViewModel.onExpandedSupermercadosChange(false) }
                        ) {
                            supermercados.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s.nombre) },
                                    onClick = { editarCompraViewModel.onSupermercadoSeleccionado(s) }
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
                        onExpandedChange = { editarCompraViewModel.onExpandedChange(!state.expanded) }
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
                            onDismissRequest = { editarCompraViewModel.onExpandedChange(false) }
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
                                    onClick = { editarCompraViewModel.onProductoSeleccionado(producto) }
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
                        onValueChange = { editarCompraViewModel.onCantidadChange(it) },
                        label = stringResource(id = R.string.label_cantidad)
                    )

                    EspacioPequeño()

                    // Precio editable — pre-relleno con el valor del catálogo al elegir producto
                    SuperAhorroTextField(
                        value = state.precioProducto,
                        onValueChange = { editarCompraViewModel.onPrecioChange(it) },
                        label = stringResource(id = R.string.label_precio_placeholder)
                    )

                    EspacioPequeño()

                    SuperAhorroButton(
                        text = stringResource(id = R.string.btn_agregar_producto),
                        onClick = { editarCompraViewModel.agregarProductoLocal() },
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
                                    editarCompraViewModel.editarProductoLocal(item)
                                }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = stringResource(id = R.string.cd_editar),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                // Eliminar: quita el producto de la lista
                                IconButton(onClick = {
                                    editarCompraViewModel.eliminarProductoLocal(item)
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
                                editarCompraViewModel.onImagenUriChange(uri)
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
                Al guardar reutilizamos validarYGuardar pero pasamos el ID
                original de la compra (compra.id), para que HomeViewModel
                ejecute un UPDATE en Room en lugar de un INSERT.
                */
                SuperAhorroButton(
                    text = stringResource(id = R.string.btn_guardar_cambios),
                    onClick = {
                        editarCompraViewModel.validarYGuardar(
                            idNuevaCompra = compra.id
                        ) { compraEditada ->
                            scope.launch {
                                val compraFinal = compraEditada.copy(
                                    fecha = compra.fecha,
                                    hora = compra.hora,
                                    productos = state.productos
                                )
                                homeViewModel.editarCompra(compraFinal)
                                onCompraGuardada()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}