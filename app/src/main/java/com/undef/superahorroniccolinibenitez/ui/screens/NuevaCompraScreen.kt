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
    onNavigateToNuevoProducto: () -> Unit
) {
    val localContext = LocalContext.current
    //  Scope local de Compose para controlar la sincronía del botón guardar
    val scope = rememberCoroutineScope()

    val state by nuevaCompraViewModel.uiState.collectAsState()
    val catalogo by homeViewModel.catalogo.collectAsState()

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
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                    .padding(horizontal = 4.dp)
            ) {
                SuperAhorroTextField(
                    value = state.supermercado,
                    onValueChange = { nuevaCompraViewModel.onSupermercadoChange(it) },
                    label = stringResource(id = R.string.label_supermercado)
                )

                EspacioPequeño()

                Text(
                    text = stringResource(
                        id = R.string.label_fecha_hora,
                        fechaDisplay,
                        horaDisplay
                    ),
                    style = MaterialTheme.typography.bodyMedium
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
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = state.expanded,
                        onDismissRequest = { nuevaCompraViewModel.onExpandedChange(false) }
                    ) {
                        catalogo.forEach { producto ->
                            DropdownMenuItem(
                                text = { Text("${producto.nombre} - $${producto.precio}") },
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
                    onClick = { nuevaCompraViewModel.agregarProductoLocal() }
                )

                if (state.errorProducto.isNotEmpty()) {
                    Text(
                        state.errorProducto,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                EspacioNormal()

                if (state.productos.isNotEmpty()) {
                    Text(
                        stringResource(id = R.string.title_productos_agregados),
                        style = MaterialTheme.typography.titleSmall
                    )
                    EspacioPequeño()

                    state.productos.forEach { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                    alpha = 0.5f
                                )
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        item.producto.nombre,
                                        style = MaterialTheme.typography.bodyLarge
                                    )

                                    val subtotalFormateado = "%.2f".format(item.subtotal())
                                    Text(
                                        text = stringResource(
                                            id = R.string.label_formato_subtotal,
                                            item.cantidad,
                                            item.producto.precio,
                                            subtotalFormateado
                                        ),
                                        style = MaterialTheme.typography.bodySmall
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
                    }
                }

                EspacioNormal()

                val totalFormateado = "%.2f".format(state.totalCalculado)
                Text(
                    text = stringResource(id = R.string.label_total_dinamico, totalFormateado),
                    style = MaterialTheme.typography.headlineSmall
                )
                state.imagenUri?.let { uri ->
                    EspacioNormal()
                    Text(
                        stringResource(id = R.string.label_ticket_cargado),
                        style = MaterialTheme.typography.labelLarge
                    )
                    coil.compose.AsyncImage(
                        model = uri,
                        contentDescription = stringResource(id = R.string.cd_ticket),
                        modifier = Modifier.fillMaxWidth().height(150.dp)
                            .padding(vertical = 8.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                if (state.errorGeneral.isNotEmpty()) {
                    Text(
                        state.errorGeneral,
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