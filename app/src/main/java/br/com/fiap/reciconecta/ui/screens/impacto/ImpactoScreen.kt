package br.com.fiap.reciconecta.ui.screens.impacto

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.reciconecta.R
import br.com.fiap.reciconecta.ui.components.AppBottomBar
import br.com.fiap.reciconecta.ui.theme.ReciconectaTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import br.com.fiap.reciconecta.data.local.datastore.RecyclingPreferences
import br.com.fiap.reciconecta.data.repository.RecyclingRepositoryImpl
import br.com.fiap.reciconecta.domain.model.RecyclingStats
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpactoScreen(
    onRecycleClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val orangeBrandColor = Color(0xFFEA580C)
    
    val context = LocalContext.current
    val preferences = remember { RecyclingPreferences(context) }
    val recyclingRepository = remember { RecyclingRepositoryImpl(preferences) }
    val stats by recyclingRepository.recyclingStats.collectAsState(initial = RecyclingStats(71f, 22f, 11f))

    val totalWeight = stats.plasticKg + stats.paperKg + stats.metalKg
    val co2Avoided = totalWeight * 0.36f
    val income = totalWeight * 2.80f

    val formattedWeight = String.format(Locale.getDefault(), "%.3f", totalWeight)
    val formattedCo2 = String.format(Locale.getDefault(), "%.3f", co2Avoided)
    val formattedIncome = String.format(Locale.getDefault(), "R$ %.2f", income)

    Scaffold(
        bottomBar = {
            AppBottomBar(
                currentRoute = "inicio",
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF7F9F8))
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.impact_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.impact_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = formattedWeight,
                    labelUnit = "kg",
                    labelDesc = stringResource(R.string.stat_recycled),
                    icon = Icons.Default.Eco,
                    iconTint = Color(0xFF2D6A4F),
                    iconBg = Color(0xFFD8F3E3)
                )

                StatCard(
                    modifier = Modifier.weight(1f),
                    value = formattedCo2,
                    labelUnit = "kg",
                    labelDesc = stringResource(R.string.stat_co2),
                    icon = Icons.Default.Air,
                    iconTint = Color(0xFF3B82F6),
                    iconBg = Color(0xFFDBEAFE)
                )

                StatCard(
                    modifier = Modifier.weight(1f),
                    value = formattedIncome,
                    labelUnit = "",
                    labelDesc = stringResource(R.string.stat_income),
                    icon = Icons.Default.MonetizationOn,
                    iconTint = Color(0xFFE47B3E),
                    iconBg = Color(0xFFFFF5EE)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), MaterialTheme.shapes.large)
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.chart_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFEBF3EF))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.chart_year),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (i in 0..3) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            ChartBar(month = "Mar", heightFraction = 0.4f)
                            ChartBar(month = "Abr", heightFraction = 0.55f)
                            ChartBar(month = "Mai", heightFraction = 0.7f)
                            ChartBar(month = "Jun", heightFraction = 0.5f)
                            ChartBar(month = "Jul", heightFraction = 0.8f)
                            ChartBar(month = "Ago", heightFraction = 0.9f)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = stringResource(R.string.section_by_material),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            val maxWeight = if (totalWeight > 0f) totalWeight else 1f

            MaterialProgressItem(
                name = stringResource(R.string.material_plastic),
                weight = stats.plasticKg,
                color = orangeBrandColor,
                progress = stats.plasticKg / maxWeight
            )

            Spacer(modifier = Modifier.height(16.dp))

            MaterialProgressItem(
                name = stringResource(R.string.material_paper),
                weight = stats.paperKg,
                color = MaterialTheme.colorScheme.primary,
                progress = stats.paperKg / maxWeight
            )

            Spacer(modifier = Modifier.height(16.dp))

            MaterialProgressItem(
                name = stringResource(R.string.tag_metal),
                weight = stats.metalKg,
                color = Color(0xFF52B788),
                progress = stats.metalKg / maxWeight
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    labelUnit: String,
    labelDesc: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color
) {
    Card(
        modifier = modifier.height(145.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), MaterialTheme.shapes.large)
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (labelUnit.isNotEmpty()) {
                        Text(
                            text = labelUnit,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = iconTint
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = labelDesc,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ChartBar(month: String, heightFraction: Float) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxHeight()
    ) {
        Box(
            modifier = Modifier
                .width(16.dp)
                .fillMaxHeight(heightFraction)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(Color(0xFF1B4332))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = month,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun MaterialProgressItem(
    name: String,
    weight: Float,
    color: Color,
    progress: Float
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            val formattedW = String.format(Locale.getDefault(), "%.3f", weight)
            Text(
                text = "$formattedW kg",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = color,
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ImpactoScreenPreview() {
    ReciconectaTheme {
        ImpactoScreen()
    }
}
