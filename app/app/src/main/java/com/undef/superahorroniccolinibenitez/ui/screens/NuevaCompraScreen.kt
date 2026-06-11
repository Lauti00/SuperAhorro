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
import androidx.compose.ui.res.stringResource // IMPORTANTE: Librería para i18n
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorroniccolinibenitez.ui.components.*
import com.undef.superahorroniccolinibenitez.ui.viewmodel.HomeViewModel
import com.undef.superahorroniccolinibenitez.ui.viewmodel.NuevaCompraViewModel
import com.undef.superahorroniccolinibenitez.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NuevaCompraScreen(
    homeViewModel: HomeViewModel,
    nuevaCompraViewModel: NuevaCompraViewModel = viewModel(),
    onBack: () -> Unit,
    onCompraGuardada: () -> Unit,
    onNavigateToNuevoProducto: () -> Unit,
    onNavigateToNuevoSupermercado: () -> Unit
) {
    val localContext = LocalContext.current
    //  Scope local de Compose para controlar la sincronía del botón guardar
    val scope = rememberCoroutineScope()

    val state by nuevaCompraViewModel.uiState.collectAsState()
    val catalogo by homeViewModel.catalogo.collectAsState()
    val supermercados by homeViewModel.supermercados.collectAsState()

    val fechaDisplay = remember { LocalDate.now().toString() }
    val horaDisplay = remember { LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> nuevaCompraViewModel.onImagenUriChange(uri) }

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
            nuevaCompraViewModel.onImagenUriChange(null)
        }
    }

    val permisoCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            val uri = crearImagenUri(localContext)
            nuevaCompraViewModel.onImagenUriChange(uri)
            cameraLauncher.launch(uri)
        }
    }

    val tieneCamara =
        localContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

    SimpleScreenContainer(
        title = stringResource(id = R.string.title_nueva_compra),
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
                        title = stringResource(id = R.string.title_nueva_compra),
                        subtitle = stringResource(id = R.string.nueva_compra_form_subtitle)
                    )

                    EspacioNormal()

                    // Combo de supermercados precargados
                    ExposedDropdownMenuBox(
                        expanded = state.expandedSupermercados,
                        onExpandedChange = { nuevaCompraViewModel.onExpandedSupermercadosChange(!state.expandedSupermercados) }
                    ) {
                        OutlinedTextField(
                            value = state.supermercado,
                            onValueChange = { nuevaCompraViewModel.onSupermercadoChange(it) },
                            label = { Text(stringResource(id = R.string.label_supermercado)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.expandedSupermercados) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        )

                        ExposedDropdownMenu(
                            expanded = state.expandedSupermercados,
                            onDismissRequest = { nuevaCompraViewModel.onExpandedSupermercadosChange(false) }
                        ) {
                            supermercados.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s.nombre) },
                                    onClick = { nuevaCompraViewModel.onSupermercadoSeleccionado(s) }
                                )
                            }
                        }
                    }

                    EspacioPequeño()

                    SuperAhorroTextButton(
                        text = stringResource(id = R.string.btn_crear_supermercado),
                        onClick = onNavigateToNuevoSupermercado
                    )

                    EspacioPequeño()

                    Text(
                        text = stringResource(
                            id = R.string.label_fecha_hora,
                            fechaDisplay,
                            horaDisplay
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    EspacioNormal()

                    ExposedDropdownMenuBox(
                        expanded = state.expanded,
                        onExpandedChange = { nuevaCompraViewModel.onExpandedChange(!state.expanded) }
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
                            onDismissRequest = { nuevaCompraViewModel.onExpandedChange(false) }
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
                                    onClick = { nuevaCompraViewModel.onProductoSeleccionado(producto) }
                                )
                            }
                        }
                    }

                    SuperAhorroTextButton(
                        text = stringResource(id = R.string.btn_crear_producto),
                        onClick = onNavigateToNuevoProducto
                    )

                    SuperAhorroTextField(
                        value = state.cantidadProducto,
                        onValueChange = { nuevaCompraViewModel.onCantidadChange(it) },
                        label = stringResource(id = R.string.label_cantidad)
                    )

                    EspacioPequeño()

                    SuperAhorroButton(
                        text = stringResource(id = R.string.btn_agregar_producto),
                        onClick = { nuevaCompraViewModel.agregarProductoLocal() },
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

                                    val subtotalFormateado = "%.2f".format(item.subtotal())

                                    Text(
                                        text = stringResource(
                                            id = R.string.label_formato_subtotal,
                                            item.cantidad,
                                            item.producto.precio,
                                            subtotalFormateado
                                        ),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                IconButton(onClick = {
                                    nuevaCompraViewModel.editarProductoLocal(item)
                                }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = stringResource(id = R.string.cd_editar),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                IconButton(onClick = {
                                    nuevaCompraViewModel.eliminarProductoLocal(item)
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

                SuperAhorroCard {
                    Text(
                        text = stringResource(id = R.string.label_total_dinamico, "%.2f".format(state.totalCalculado)),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

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
                                localContext,
                                permiso
                            ) == PackageManager.PERMISSION_GRANTED

                            if (tienePermiso) {
                                val uri = crearImagenUri(localContext)
                                nuevaCompraViewModel.onImagenUriChange(uri)
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

                SuperAhorroButton(
                    text = stringResource(id = R.string.btn_guardar_compra),
                    onClick = {
                        //  Pasamos 0 como ID provisional. Room autogenerará la secuencia numérica única real.
                        nuevaCompraViewModel.validarYGuardar(idNuevaCompra = 0) { compraLista ->
                            scope.launch {
                                // CORREGIDO: Agregamos la compra incluyendo su estado de productos actual
                                val compraConProductos = compraLista.copy(productos = state.productos)
                                homeViewModel.agregarCompra(compraConProductos)
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