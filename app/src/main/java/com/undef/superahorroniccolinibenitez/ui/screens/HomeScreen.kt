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

    MainDrawerContainer(

        drawerState = drawerState,

        userEmail = userEmailState,

        onLogout = {

            viewModel.logout {

                onLogout()
            }
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
                                    "Cambiar tema"
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

                    Text("+")
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

                    val texto = """

                        Compra en ${compra.supermercado}

                        Fecha: ${compra.fecha}

                        Total: $${compra.total()}

                    """.trimIndent()

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
                            "Compartir compra"
                        )
                    )
                }
            )
        }
    }
}