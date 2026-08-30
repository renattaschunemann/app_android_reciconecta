package br.com.fiap.reciconecta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import br.com.fiap.reciconecta.ui.viewmodels.ColetaViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

// --- MODELO DOS CATADORES ---
data class Catador(
    val id: String,
    val nome: String,
    val avaliacao: Double,
    val distanciaKm: Double,
    val materiaisAceitos: List<String>,
    val lat: Double,
    val lng: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaScreen(
    viewModel: ColetaViewModel,
    onBackClick: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    val materiaisDoUsuario = viewModel.itemsList.map { it.name }.distinct()

    // Mock com coordenadas próximas à Vila Mariana (SP)
    val todosCatadores = listOf(
        Catador("1", "Carlos Silva", 4.8, 0.8, listOf("PET/Plástico", "Papelão", "Metal", "Vidro"), -23.5850, -46.6320),
        Catador("2", "Maria Souza", 4.9, 2.5, listOf("Vidro", "Óleo de Cozinha", "Metal"), -23.5900, -46.6380),
        Catador("3", "João da Silva", 4.5, 1.2, listOf("PET/Plástico", "Papelão", "Vidro", "Metal", "Eletrônico"), -23.5880, -46.6300),
        Catador("4", "Ana Costa", 5.0, 3.1, listOf("PET/Plástico", "Papelão"), -23.5820, -46.6360)
    )

    // LÓGICA DE FILTRO
    val catadoresFiltrados = todosCatadores.filter { catador ->
        if (materiaisDoUsuario.isEmpty()) true
        else materiaisDoUsuario.all { itemUsuario -> catador.materiaisAceitos.contains(itemUsuario) }
    }

    // Configuração inicial da Câmera do Mapa (Centro da Vila Mariana)
    val spVilaMariana = LatLng(-23.5898, -46.6342)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(spVilaMariana, 14f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catadores Próximos", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {

            // 1. O MAPA DO GOOGLE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    catadoresFiltrados.forEach { catador ->
                        // Criamos um estado individual para cada marcador
                        val markerState = remember(catador.id) {
                            MarkerState(position = LatLng(catador.lat, catador.lng))
                        }

                        // Força o balão com o nome e a distância a aparecer aberto automaticamente
                        LaunchedEffect(Unit) {
                            markerState.showInfoWindow()
                        }

                        Marker(
                            state = markerState,
                            title = catador.nome,
                            snippet = "📍 ${catador.distanciaKm} km • ⭐ ${catador.avaliacao}"
                        )
                    }
                }
            }

            // 2. Lista de Catadores Filtrados
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Selecione um catador para solicitar coleta",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (materiaisDoUsuario.isNotEmpty()) {
                        Text(
                            text = "Filtro ativo para: ${materiaisDoUsuario.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (catadoresFiltrados.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Nenhum catador próximo recolhe todos\nos materiais listados no momento.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(catadoresFiltrados) { catador ->
                                CatadorCard(
                                    catador = catador,
                                    onSolicitacaoConfirmada = onNavigateToHome
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CatadorCard(
    catador: Catador,
    onSolicitacaoConfirmada: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = catador.nome.first().toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = catador.nome,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${catador.avaliacao} • A ${catador.distanciaKm} km",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = catador.materiaisAceitos.take(3).joinToString(", ") + if(catador.materiaisAceitos.size > 3) "..." else "",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Laranja de Solicitação
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Autorenew,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Solicitar Coleta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showDialog) {
        SolicitacaoSucessoDialog(
            catadorNome = catador.nome,
            onDismiss = { showDialog = false },
            onConfirm = {
                showDialog = false
                onSolicitacaoConfirmada()
            }
        )
    }
}

@Composable
fun SolicitacaoSucessoDialog(
    catadorNome: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Sucesso",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Solicitação Registrada!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B1B1B)
                )

                Spacer(modifier = Modifier.height(12.dp))

                val textoFormatado = buildAnnotatedString {
                    append("Seu pedido foi enviado para ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1B4332))) {
                        append(catadorNome)
                    }
                    append(".\nVocê receberá uma confirmação em breve.")
                }

                Text(
                    text = textoFormatado,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4332)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("OK", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}