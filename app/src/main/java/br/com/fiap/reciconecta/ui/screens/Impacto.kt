package br.com.fiap.reciconecta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Recycling
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
import br.com.fiap.reciconecta.ui.theme.OrangeIconColor
import br.com.fiap.reciconecta.ui.theme.ReciconectaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpactoScreen(
    onRecycleClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val orangeBrandColor = Color(0xFFEA580C)

    Scaffold(
        bottomBar = {
            // Stack the Orange Recycle Button and Bottom Navigation Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // "Reciclar Agora" Button Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp)
                ) {
                    Button(
                        onClick = onRecycleClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = orangeBrandColor,
                            contentColor = Color.White
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Recycling,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.action_recycle_now),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Persistent Navigation Bar
                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 16.dp,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BottomNavItem(
                            icon = Icons.Default.Home,
                            label = stringResource(R.string.nav_home),
                            isSelected = true,
                            onClick = { onNavigate("inicio") }
                        )
                        BottomNavItem(
                            icon = Icons.Default.Autorenew,
                            label = stringResource(R.string.nav_collections),
                            isSelected = false,
                            onClick = { onNavigate("coletas") }
                        )
                        BottomNavItem(
                            icon = Icons.Default.Person,
                            label = stringResource(R.string.nav_profile),
                            isSelected = false,
                            onClick = { onNavigate("perfil") }
                        )
                    }
                }
            }
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
            // Header Section
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

            // 1. Stats row (Leaf, Wind/Air, Dollar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Recycled
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = "104",
                    labelUnit = "kg",
                    labelDesc = stringResource(R.string.stat_recycled),
                    icon = Icons.Default.Eco,
                    iconTint = Color(0xFF2D6A4F),
                    iconBg = Color(0xFFD8F3E3)
                )

                // Card 2: CO2 Avoided
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = "38",
                    labelUnit = "kg",
                    labelDesc = stringResource(R.string.stat_co2),
                    icon = Icons.Default.Air,
                    iconTint = Color(0xFF3B82F6),
                    iconBg = Color(0xFFDBEAFE)
                )

                // Card 3: Income Generated
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = "R$290",
                    labelUnit = "",
                    labelDesc = stringResource(R.string.stat_income),
                    icon = Icons.Default.MonetizationOn,
                    iconTint = Color(0xFFE47B3E),
                    iconBg = Color(0xFFFFF5EE)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Bar Chart Card
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

                    // Bar Chart Visualization
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        // Background dashed guidelines
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

                        // Chart bars row
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

            // 3. Distribution By Material Section
            Text(
                text = stringResource(R.string.section_by_material),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Rows
            MaterialProgressItem(
                name = stringResource(R.string.material_plastic),
                weight = 71,
                color = orangeBrandColor,
                progress = 0.71f
            )

            Spacer(modifier = Modifier.height(16.dp))

            MaterialProgressItem(
                name = stringResource(R.string.material_paper),
                weight = 22,
                color = MaterialTheme.colorScheme.primary,
                progress = 0.22f
            )

            Spacer(modifier = Modifier.height(16.dp))

            MaterialProgressItem(
                name = stringResource(R.string.tag_metal),
                weight = 11,
                color = Color(0xFF52B788),
                progress = 0.11f
            )

            Spacer(modifier = Modifier.height(40.dp)) // Espaço para não sobrepor o botão ao rolar
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
            // Icon Badge
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

            // Value & labels
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
    weight: Int,
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
            Text(
                text = "$weight kg",
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

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (isSelected) Color(0xFF1B4332) else Color(0xFF8F9A93)

    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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
