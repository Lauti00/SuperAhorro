package com.undef.superahorroniccolinibenitez.ui.screens

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.undef.superahorroniccolinibenitez.R
import java.util.concurrent.Executors

/*
Pantalla de escaneo de código EAN con la cámara.

Usa CameraX para mostrar el preview y ML Kit para
detectar códigos de barras en tiempo real.

Cuando detecta un EAN válido llama a onEanDetectado()
y la navegación vuelve automáticamente a NuevoProductoScreen.

yaDetectado evita que ML Kit dispare onEanDetectado
múltiples veces para el mismo código mientras la cámara
sigue procesando frames.
*/
@Composable
fun EanScannerScreen(
    onEanDetectado: (String) -> Unit,
    onCancelar: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val yaDetectado = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        // Vista de la cámara
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = Executors.newSingleThreadExecutor()
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val scanner = BarcodeScanning.getClient()

                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { analysis ->
                            analysis.setAnalyzer(executor) { imageProxy ->
                                if (yaDetectado.value) {
                                    imageProxy.close()
                                    return@setAnalyzer
                                }

                                val mediaImage = imageProxy.image
                                if (mediaImage != null) {
                                    val image = InputImage.fromMediaImage(
                                        mediaImage,
                                        imageProxy.imageInfo.rotationDegrees
                                    )

                                    scanner.process(image)
                                        .addOnSuccessListener { barcodes ->
                                            for (barcode in barcodes) {
                                                /*
                                                Filtramos solo códigos EAN-13 y EAN-8.
                                                Son los formatos estándar de productos
                                                de supermercado en Argentina.
                                                */
                                                if (
                                                    barcode.format == Barcode.FORMAT_EAN_13 ||
                                                    barcode.format == Barcode.FORMAT_EAN_8
                                                ) {
                                                    val ean = barcode.rawValue
                                                    if (!ean.isNullOrEmpty() && !yaDetectado.value) {
                                                        yaDetectado.value = true
                                                        Log.d("EanScanner", "EAN detectado: $ean")
                                                        onEanDetectado(ean)
                                                    }
                                                }
                                            }
                                        }
                                        .addOnFailureListener {
                                            Log.w("EanScanner", "Error al procesar frame: ${it.message}")
                                        }
                                        .addOnCompleteListener {
                                            imageProxy.close()
                                        }
                                } else {
                                    imageProxy.close()
                                }
                            }
                        }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalyzer
                        )
                    } catch (e: Exception) {
                        Log.e("EanScanner", "Error al iniciar cámara: ${e.message}")
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay oscuro con instrucciones arriba
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(Color.Black.copy(alpha = 0.55f))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.ean_scanner_instruccion),
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }

        // Botón cancelar abajo
        Button(
            onClick = onCancelar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black.copy(alpha = 0.7f)
            )
        ) {
            Text(
                text = stringResource(id = R.string.btn_cancelar_escaneo),
                color = Color.White
            )
        }
    }
}
