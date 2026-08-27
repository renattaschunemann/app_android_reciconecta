package br.com.fiap.reciconecta.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.reciconecta.R

@Composable
fun AppBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    topContent: (@Composable () -> Unit)? = null // Permite passar um botão superior opcional (ex: Reciclar ou Confirmar)
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Renderiza o botão superior se a tela enviar um (ex: ImpactoScreen ou ColetaScreen)
        topContent?.invoke()
        // Barra cinza com os ícones de navegação
        Surface(
            tonalElevation = 8.dp,
            shadowElevation = 16.dp,
            color = Color(0xFFE8ECE9),
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
                    isSelected = currentRoute == "inicio",
                    onClick = { onNavigate("inicio") }
                )
                BottomNavItem(
                    icon = Icons.Default.Recycling,
                    label = stringResource(R.string.nav_collections),
                    isSelected = currentRoute == "coletas",
                    onClick = { onNavigate("coletas") }
                )
                BottomNavItem(
                    icon = Icons.Default.Person,
                    label = stringResource(R.string.nav_profile),
                    isSelected = currentRoute == "perfil",
                    onClick = { onNavigate("perfil") }
                )
            }
        }
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