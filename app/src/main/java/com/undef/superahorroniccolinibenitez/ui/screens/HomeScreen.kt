package com.undef.superahorroniccolinibenitez.ui.screens

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.undef.superahorroniccolinibenitez.R
import com.undef.superahorroniccolinibenitez.data.datastore.UserPreferences
import com.undef.superahorroniccolinibenitez.model.Compra
import com.undef.superahorroniccolinibenitez.ui.components.MainDrawerContainer
import com.undef.superahorroniccolinibenitez.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(

    /*
    IMPORTANTE:
    Ahora recibimos el ViewModel compartido
    */
    viewModel: HomeViewModel,

    onLogout: () -> Unit,
    onNavigateToHistorial: () -> Unit,
    onNavigateToEstadisticas: () -> Unit,
    onNavigateToNuevaCompra: () -> Unit,
    onCompraClick: (Compra) -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToSettings: () -> Unit
) {

    val drawerState =
        rememberDrawerState(
            initialValue = DrawerValue.Closed
        )

    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    /*
    Controla el AlertDialog de cierre de sesión
    */
    var showLogoutDialog by remember {
        mutableStateOf(false)
    }

    /*
    UserPreferences
    */
    val userPreferences = remember {
        UserPreferences(context)
    }

    /*
    Estado del tema
    */
    val darkThemeEnabled by
    userPreferences.darkTheme.collectAsState(
        initial = false
    )

    /*
    Datos del ViewModel compartido
    */
    val userEmailState by
    viewModel.userEmail.collectAsState()

    val comprasState by
    viewModel.compras.collectAsState()

    if (showLogoutDialog) {

        AlertDialog(
            onDismissRequest = {
                showLogoutDialog = false
            },

            title = {
                Text(
                    text = stringResource(id = R.string.dialog_logout_title)
                )
            },

            text = {
                Text(
                    text = stringResource(id = R.string.dialog_logout_message)
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false

                        viewModel.logout {

                            onLogout()
                        }
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.dialog_logout_confirm)
                    )
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.dialog_logout_cancel)
                    )
                }
            }
        )
    }

    MainDrawerContainer(

        drawerState = drawerState,

        userEmail = userEmailState,

        onLogout = {

            showLogoutDialog = true
        },

        onNavigateToHistorial =
            onNavigateToHistorial,

        onNavigateToEstadisticas =
            onNavigateToEstadisticas,

        onNavigateToPerfil =
            onNavigateToPerfil,

        onNavigateToSettings =
            onNavigateToSettings

    ) {

        Scaffold(

            topBar = {

                TopAppBar(

                    title = {
                        Text(
                            stringResource(
                                R.string.home_title
                            )
                        )
                    },

                    navigationIcon = {

                        IconButton(

                            onClick = {

                                if (!drawerState.isOpen) {

                                    scope.launch {

                                        drawerState.open()
                                    }
                                }
                            }

                        ) {

                            Icon(
                                Icons.Default.Menu,
                                contentDescription =
                                    stringResource(
                                        R.string.cd_menu
                                    )
                            )
                        }
                    },

                    /*
                    BOTÓN GLOBAL MODO OSCURO
                    */
                    actions = {

                        IconButton(

                            onClick = {

                                scope.launch {

                                    userPreferences.saveTheme(
                                        !darkThemeEnabled
                                    )
                                }
                            }

                        ) {

                            Icon(

                                imageVector =
                                    if (darkThemeEnabled) {
                                        Icons.Default.Brightness7
                                    } else {
                                        Icons.Default.Brightness4
                                    },

                                contentDescription =
                                    stringResource(id = R.string.cd_cambiar_tema)
                            )
                        }
                    }
                )
            },

            floatingActionButton = {

                FloatingActionButton(
                    onClick =
                        onNavigateToNuevaCompra
                ) {

                    Text(
                        text = stringResource(id = R.string.btn_nueva_compra_fab)
                    )
                }
            }

        ) { paddingValues ->

            HomeContent(

                paddingValues = paddingValues,

                compras = comprasState,

                /*
                Cuando el usuario toca una compra,
                seleccionamos la compra y navegamos
                */
                onItemClick = { compra ->

                    viewModel.seleccionarCompra(
                        compra
                    )

                    onCompraClick(compra)
                },

                /*
                Compartir compra
                */
                onShare = { compra ->

                    val texto = context.getString(
                        R.string.formato_compartir_compra,
                        compra.supermercado,
                        compra.fecha,
                        compra.hora,
                        "%.2f".format(compra.total())
                    )

                    val intent =
                        Intent(Intent.ACTION_SEND).apply {

                            type = "text/plain"

                            putExtra(
                                Intent.EXTRA_TEXT,
                                texto
                            )
                        }

                    context.startActivity(

                        Intent.createChooser(
                            intent,
                            context.getString(R.string.chooser_compartir_compra)
                        )
                    )
                }
            )
        }
    }
}