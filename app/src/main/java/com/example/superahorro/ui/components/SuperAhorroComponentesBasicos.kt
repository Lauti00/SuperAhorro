package com.example.superahorro.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- TEXTOS Y TÍTULOS ---
@Composable
fun SuperAhorroTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.displayLarge,
        color = MaterialTheme.colorScheme.primary
    )
}

// --- BOTONES ---
@Composable
fun SuperAhorroButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp)
    ) {
        Text(text = text, fontSize = 16.sp, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun SuperAhorroTextButton(text: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(text = text, color = MaterialTheme.colorScheme.secondary)
    }
}

// --- CAMPOS DE TEXTO ---
@Composable
fun SuperAhorroTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        modifier = modifier.fillMaxWidth()
    )
}

// --- ESPACIADORES ---
@Composable
fun EspacioPequeño() { Spacer(modifier = Modifier.height(8.dp)) }
@Composable
fun EspacioNormal() { Spacer(modifier = Modifier.height(16.dp)) }
@Composable
fun EspacioGrande() { Spacer(modifier = Modifier.height(32.dp)) }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleScreenContainer(
    title: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            content = content
        )
    }
}