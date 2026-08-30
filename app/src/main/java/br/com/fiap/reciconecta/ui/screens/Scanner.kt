package br.com.fiap.reciconecta.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun ScannerScreen(
    onBackClick: () -> Unit = {},
    onItemScanned: (nome: String, unidade: String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    var detectedMaterial by remember { mutableStateOf<String?>(null) }
    var confidence by remember { mutableFloatStateOf(0f) }

    // Estado para controlar o modal de seleção manual
    var showManualDialog by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Modal de Seleção Manual
    if (showManualDialog) {
        val manualOptions = listOf(
            "PET/Plástico",
            "Papelão",
            "Metal",
            "Vidro",
            "Eletrônico",
            "Óleo de Cozinha"
        )

        AlertDialog(
            onDismissRequest = { showManualDialog = false },
            title = {
                Text(
                    text = "Selecionar Material",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    manualOptions.forEach { material ->
                        TextButton(
                            onClick = {
                                showManualDialog = false
                                val isOleo = material == "Óleo de Cozinha"
                                val unidadeMedida = if (isOleo) "L" else "kg"

                                // Simula o mesmo retorno da câmera!
                                onItemScanned(material, unidadeMedida)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = material,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showManualDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escanear Material", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        if (hasCameraPermission) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Câmera ao fundo
                CameraPreviewAnalyzer(
                    onMaterialDetected = { material, conf ->
                        detectedMaterial = material
                        confidence = conf
                    }
                )

                // Mira de foco
                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .align(Alignment.Center)
                        .border(2.dp, Color(0xFFE47B3E), RoundedCornerShape(24.dp))
                )

                // Lógica de exibição da parte inferior
                if (detectedMaterial != null && confidence > 0.6f) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(24.dp)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Material Identificado",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = detectedMaterial ?: "",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Confiança: ${(confidence * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val isOleo = detectedMaterial == "Óleo de Cozinha"
                                    val unidadeMedida = if (isOleo) "L" else "kg"

                                    onItemScanned(detectedMaterial!!, unidadeMedida)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE47B3E))
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Adicionar à Coleta")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Botão discreto caso ele reconheça errado
                            TextButton(onClick = { showManualDialog = true }) {
                                Text("Não é isso? Inserir manualmente")
                            }
                        }
                    }
                } else {
                    // Quando nada é detectado
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                            color = Color.Black.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = "Aponte a câmera para o reciclável",
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botão de Inserção Manual
                        Button(
                            onClick = { showManualDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Inserir Manualmente", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Permissão de câmera necessária.")
            }
        }
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
private fun CameraPreviewAnalyzer(
    onMaterialDetected: (String, Float) -> Unit
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    val labeler = remember { ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS) }

    val materialTranslation = mapOf(
        "Bottle" to "PET/Plástico",
        "Plastic" to "PET/Plástico",
        "Water bottle" to "PET/Plástico",
        "Box" to "Papelão",
        "Cardboard" to "Papelão",
        "Paper" to "Papel",
        "Can" to "Metal",
        "Aluminum can" to "Metal",
        "Tin can" to "Metal",
        "Metal" to "Metal",
        "Glass" to "Vidro",
        "Glass bottle" to "Vidro",
        "Electronics" to "Eletrônico",
        "Mobile phone" to "Eletrônico",
        "Computer" to "Eletrônico",
        "Laptop" to "Eletrônico",
        "Television" to "Eletrônico",
        "Oil" to "Óleo de Cozinha",
        "Cooking oil" to "Óleo de Cozinha",
        "Liquid" to "Óleo de Cozinha",
        "Jug" to "Óleo de Cozinha"
    )

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor) { imageProxy ->
                            val mediaImage = imageProxy.image
                            if (mediaImage != null) {
                                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                                labeler.process(image)
                                    .addOnSuccessListener { labels ->
                                        for (label in labels) {
                                            if (materialTranslation.containsKey(label.text)) {
                                                onMaterialDetected(
                                                    materialTranslation[label.text]!!,
                                                    label.confidence
                                                )
                                                break
                                            }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("Scanner", "Falha na análise: ${e.message}")
                                    }
                                    .addOnCompleteListener {
                                        imageProxy.close()
                                    }
                            } else {
                                imageProxy.close()
                            }
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalyzer)
                } catch (exc: Exception) {
                    Log.e("Scanner", "Erro ao vincular câmera", exc)
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}