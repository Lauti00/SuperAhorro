package com.undef.superahorroniccolinibenitez.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.undef.superahorroniccolinibenitez.R

@Composable
fun ProfileHeader(nombre: String, email: String) {
    SuperAhorroCard {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            Surface(
                modifier = Modifier.size(92.dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = nombre.take(1).ifBlank { "?" },
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            EspacioNormal()

            Text(
                text = nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// En ProfileComponents.kt donde ya tenés el Header
@Composable
fun ProfileForm(
    nombre: String,
    onNombreChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    onSave: () -> Unit
) {
    SuperAhorroCard {
        SuperAhorroSectionTitle(
            title = stringResource(id = R.string.profile_form_title),
            subtitle = stringResource(id = R.string.profile_form_subtitle)
        )

        EspacioNormal()

        SuperAhorroTextField(
            value = nombre,
            onValueChange = onNombreChange,
            label = stringResource(id = R.string.label_nombre)
        )

        EspacioNormal()

        SuperAhorroTextField(
            value = email,
            onValueChange = onEmailChange,
            label = stringResource(id = R.string.label_email)
        )

        EspacioGrande()

        SuperAhorroButton(
            text = stringResource(id = R.string.btn_guardar_cambios),
            onClick = onSave,
            modifier = Modifier.fillMaxWidth()
        )
    }
}