package br.com.fiap.reciconecta.ui.screens.coleta

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.reciconecta.data.local.datastore.RecyclingPreferences
import br.com.fiap.reciconecta.data.repository.RecyclingRepositoryImpl
import br.com.fiap.reciconecta.ui.viewmodels.ColetaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColetaScreen(
    viewModel: ColetaViewModel,
    onBackClick: () -> Unit = {},
    onAddItemClick: () -> Unit = {}, // Botão que leva para o Scanner
    onNavigateToMap: () -> Unit = {}  // Botão que leva para o Mapa
) {
    val itemsList = viewModel.itemsList
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferences = remember { RecyclingPreferences(context) }
    val recyclingRepository = remember { RecyclingRepositoryImpl(preferences) }

    // Cálculo dinâmico do valor estimado em R$ com base na quantidade de itens
    val valorEstimadoTotal = itemsList.size * 3.00

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Itens para Coleta", fontWeight = FontWeight.Bold) },
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
        },
        bottomBar = {
            // Rodapé com os botões: Adicionar Item e Confirmar Coleta (Indo para o Mapa)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botão para Adicionar mais itens via Scanner
                    OutlinedButton(
                        onClick = onAddItemClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1B4332))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Adicionar", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    // Botão para Confirmar Coleta e ir automaticamente para o Mapa
                    Button(
                        onClick = {
                            scope.launch {
                                // Calcula e adiciona os materiais com base nos itens escaneados
                                var plastic = 0f
                                var paper = 0f
                                var metal = 0f
                                itemsList.forEach { item ->
                                    val amt = item.amount.toFloat()
                                    val nameLower = item.name.lowercase()
                                    if (nameLower.contains("plast") || nameLower.contains("pet")) {
                                        plastic += amt * 0.050f
                                    } else if (nameLower.contains("papel") || nameLower.contains("cardboard")) {
                                        paper += amt * 0.450f
                                    } else if (nameLower.contains("metal") || nameLower.contains("alum") || nameLower.contains("ferro")) {
                                        metal += amt * 0.020f
                                    } else {
                                        plastic += amt * 0.050f // default fallback
                                    }
                                }
                                // Se a lista estiver vazia, adiciona um mock para não quebrar a demonstração
                                if (itemsList.isEmpty()) {
                                    plastic = 0.150f
                                    paper = 0.900f
                                }
                                recyclingRepository.addMaterial(plastic, paper, metal)
                                onNavigateToMap()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4332)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Confirmar Coleta", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Card de Resumo e Valor Estimado (R$)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Valor Estimado",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = "R$ %.2f".format(valorEstimadoTotal),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B4332)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1B4332)
                    ) {
                        Text(
                            text = "${itemsList.size} itens",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Materiais Escaneados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (itemsList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum item escaneado ainda.\nUse o botão abaixo para adicionar materiais.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(itemsList) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Qtd: ${item.amount} ${item.unit}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.removeItem(item) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remover item",
                                        tint = Color(0xFFD32F2F)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
