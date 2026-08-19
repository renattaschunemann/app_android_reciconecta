package br.com.fiap.reciconecta.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.reciconecta.R

@Composable
fun InitialScreen(modifier: Modifier = Modifier) {
    // State to track which option is selected (1 = Pessoa Física, 2 = Empresa, 3 = Catador/Cooperativa)
    var selectedOption by remember { mutableStateOf(3) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7F6))
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Section: Logo Card
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFFEBF1ED))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.reciconecta_logo),
                    contentDescription = "Reciconecta Logo",
                    modifier = Modifier.fillMaxHeight(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Conectando quem recicla com quem coleta",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF6F7973),
                textAlign = TextAlign.Center
            )
        }

        // Middle Section: Selection Options
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "COMO VOCÊ VAI USAR O APP?",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5A625C),
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OptionCard(
                title = "Pessoa Física",
                description = "Quero reciclar em casa",
                icon = Icons.Default.Person,
                isSelected = selectedOption == 1,
                onClick = { selectedOption = 1 }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OptionCard(
                title = "Empresa",
                description = "Gerenciar resíduos corporativos",
                icon = Icons.Default.Home,
                isSelected = selectedOption == 2,
                onClick = { selectedOption = 2 }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OptionCard(
                title = "Catador / Cooperativa",
                description = "Ofereço serviços de coleta",
                icon = Icons.Default.Refresh,
                isSelected = selectedOption == 3,
                onClick = { selectedOption = 3 }
            )
        }

        // Bottom Section: Terms of Use
        val termsText = buildAnnotatedString {
            append("Ao continuar você concorda com os ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF00381C))) {
                append("Termos de Uso")
            }
        }

        Text(
            text = termsText,
            fontSize = 12.sp,
            color = Color(0xFF6F7973),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .clickable { /* Action for Terms of Use */ }
        )
    }
}

@Composable
fun OptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Style configurations depending on state (selected vs unselected)
    val cardBackground = if (isSelected) Color(0xFFFDF1E8) else Color.White
    val cardBorderColor = if (isSelected) Color(0xFFE47B3E) else Color(0xFFE1E4E2)
    val textColor = if (isSelected) Color(0xFF53250A) else Color(0xFF2C322E)
    val descColor = if (isSelected) Color(0xFF7A4A2F) else Color(0xFF6F7973)
    val arrowColor = if (isSelected) Color(0xFFD9621E) else Color(0xFF8F9A93)
    val iconBgColor = if (isSelected) Color(0xFFFDE3D2) else Color(0xFFE5EDE9)
    val iconColor = if (isSelected) Color(0xFFD9621E) else Color(0xFF2E6C40)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBackground)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = cardBorderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Icon in a circle
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Center Texts
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                color = descColor
            )
        }

        // Right Arrow Icon
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Arrow right",
            tint = arrowColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InitialScreenPreview() {
    InitialScreen()
}