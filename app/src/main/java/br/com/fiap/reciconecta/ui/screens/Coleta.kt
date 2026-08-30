package br.com.fiap.reciconecta.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.reciconecta.R
import br.com.fiap.reciconecta.ui.components.AppBottomBar
import br.com.fiap.reciconecta.ui.theme.ReciconectaTheme
import br.com.fiap.reciconecta.ui.viewmodels.ColetaViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Locale

data class ColetaItem(
    val id: String,
    val name: String,
    var amount: Double,
    val unit: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColetaScreen(
    viewModel: ColetaViewModel = viewModel(), // Injeção do ViewModel
    userProfileAddress: String = "Vila Mariana, 04101-000 - São Paulo/SP",
    scannedItemName: String? = null,
    scannedItemUnit: String? = null,
    onClearScannedData: () -> Unit = {},
    onLocationClick: () -> Unit = {},
    onAddItemClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    onBackClick: () -> Boolean = { true }
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var currentAddressText by remember { mutableStateOf(userProfileAddress) }
    var isLoadingLocation by remember { mutableStateOf(false) }

    // Conectando a lista visual aos dados persistidos no ViewModel
    val itemsList = viewModel.itemsList

    var selectedTimeWindow by remember { mutableIntStateOf(1) }
    var observations by remember { mutableStateOf("") }

    // Estados do Modal
    var showQuantityDialog by remember { mutableStateOf(false) }
    var quantityInput by remember { mutableStateOf("") }
    var quantityError by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val orangeBrandColor = Color(0xFFE47B3E)

    // Dispara o modal automaticamente se vierem dados do Scanner
    LaunchedEffect(scannedItemName, scannedItemUnit) {
        if (!scannedItemName.isNullOrEmpty() && !scannedItemUnit.isNullOrEmpty()) {
            showQuantityDialog = true
        }
    }

    // Modal de Quantidade
    if (showQuantityDialog && scannedItemName != null && scannedItemUnit != null) {
        AlertDialog(
            onDismissRequest = {
                showQuantityDialog = false
                quantityInput = ""
                onClearScannedData()
            },
            title = {
                Text(
                    text = "Adicionar $scannedItemName",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column {
                    Text(
                        text = "Informe a quantidade do material para a coleta:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = quantityInput,
                        onValueChange = {
                            if (it.matches(Regex("^[0-9.,]*$"))) {
                                quantityInput = it
                                quantityError = false
                            }
                        },
                        label = { Text("Quantidade ($scannedItemUnit)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = quantityError,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (quantityError) {
                        Text(
                            text = "Insira um valor maior que zero",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = quantityInput.replace(",", ".").toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            // Adicionando o item através do ViewModel
                            viewModel.addItem(
                                ColetaItem(
                                    id = System.currentTimeMillis().toString(),
                                    name = scannedItemName,
                                    amount = amount,
                                    unit = scannedItemUnit
                                )
                            )
                            showQuantityDialog = false
                            quantityInput = ""
                            onClearScannedData()
                        } else {
                            quantityError = true
                        }
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showQuantityDialog = false
                        quantityInput = ""
                        onClearScannedData()
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    fun fetchAddress(latitude: Double, longitude: Double) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val street = address.thoroughfare ?: address.subThoroughfare ?: ""
                        val subLocality = address.subLocality ?: ""
                        val city = address.subAdminArea ?: address.locality ?: ""
                        currentAddressText = listOf(street, subLocality, city)
                            .filter { it.isNotBlank() }
                            .joinToString(", ")
                            .ifEmpty { "Lat: %.4f, Long: %.4f".format(latitude, longitude) }
                    } else {
                        currentAddressText = "Lat: %.4f, Long: %.4f".format(latitude, longitude)
                    }
                    isLoadingLocation = false
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val street = address.thoroughfare ?: ""
                    val subLocality = address.subLocality ?: ""
                    val city = address.subAdminArea ?: address.locality ?: ""
                    currentAddressText = listOf(street, subLocality, city)
                        .filter { it.isNotBlank() }
                        .joinToString(", ")
                        .ifEmpty { "Lat: %.4f, Long: %.4f".format(latitude, longitude) }
                } else {
                    currentAddressText = "Lat: %.4f, Long: %.4f".format(latitude, longitude)
                }
                isLoadingLocation = false
            }
        } catch (e: Exception) {
            currentAddressText = "Lat: %.4f, Long: %.4f".format(latitude, longitude)
            isLoadingLocation = false
        }
    }

    fun captureCurrentLocation() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled) {
            Toast.makeText(context, "Por favor, ative a localização no seu dispositivo.", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
            return
        }

        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            isLoadingLocation = true
            val cancellationTokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    fetchAddress(location.latitude, location.longitude)
                } else {
                    isLoadingLocation = false
                    Toast.makeText(context, "Não foi possível obter a localização exata no momento.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception: Exception ->
                isLoadingLocation = false
                Toast.makeText(context, "Erro ao obter localização: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineGranted || coarseGranted) {
            captureCurrentLocation()
        } else {
            Toast.makeText(context, "Permissão de localização negada.", Toast.LENGTH_SHORT).show()
        }
    }

    val onUseLocationClicked = {
        onLocationClick()
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            captureCurrentLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        bottomBar = {
            AppBottomBar(
                currentRoute = "coletas",
                onNavigate = onNavigate,
                topContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Button(
                            onClick = onConfirmClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(
                                text = stringResource(R.string.action_confirm_coleta),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.coleta_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.coleta_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Endereço da Coleta
            SectionHeader(title = stringResource(R.string.section_address))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                if (isLoadingLocation) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Buscando localização...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = currentAddressText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .clickable { onUseLocationClicked() }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.NearMe,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.action_use_current_location),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Itens para Coleta
            SectionHeader(title = stringResource(R.string.section_items))
            Spacer(modifier = Modifier.height(8.dp))

            if (itemsList.isEmpty()) {
                Text(
                    text = "Nenhum item adicionado para coleta.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    itemsList.forEachIndexed { index, item ->
                        EditableItemRow(
                            item = item,
                            // Removendo o item através do ViewModel
                            onRemove = { viewModel.removeItem(item) }
                        )
                        if (index < itemsList.size - 1) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botão Pontilhado "+ Adicionar item" (Redireciona para o Scanner)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .drawBehind {
                        val stroke = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                        )
                        drawRoundRect(
                            color = orangeBrandColor,
                            style = stroke,
                            cornerRadius = CornerRadius(12.dp.toPx())
                        )
                    }
                    .clickable { onAddItemClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "+  ",
                        color = orangeBrandColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.action_add_item),
                        color = orangeBrandColor,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Janela de Horário
            SectionHeader(title = stringResource(R.string.section_time_window))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TimeSlotCard(
                    timeRange = stringResource(R.string.time_window_morning),
                    isSelected = selectedTimeWindow == 1,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedTimeWindow = 1 }
                )
                TimeSlotCard(
                    timeRange = stringResource(R.string.time_window_afternoon),
                    isSelected = selectedTimeWindow == 2,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedTimeWindow = 2 }
                )
                TimeSlotCard(
                    timeRange = stringResource(R.string.time_window_evening),
                    isSelected = selectedTimeWindow == 3,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedTimeWindow = 3 }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Observações
            SectionHeader(title = stringResource(R.string.section_observations))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = observations,
                onValueChange = { observations = it },
                placeholder = {
                    Text(
                        text = stringResource(R.string.placeholder_observations),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun TimeSlotCard(
    timeRange: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.medium
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = timeRange,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun EditableItemRow(
    item: ColetaItem,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Recycling,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = String.format(Locale.getDefault(), "%.3f %s", item.amount, item.unit),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remover item",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ColetaScreenPreview() {
    ReciconectaTheme {
        ColetaScreen()
    }
}