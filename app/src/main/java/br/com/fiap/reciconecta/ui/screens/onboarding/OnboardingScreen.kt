package br.com.fiap.reciconecta.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.reciconecta.R
import br.com.fiap.reciconecta.ui.theme.OrangeCardBackground
import br.com.fiap.reciconecta.ui.theme.OrangeCardBorder
import br.com.fiap.reciconecta.ui.theme.OrangeIconBackground
import br.com.fiap.reciconecta.ui.theme.OrangeIconColor
import br.com.fiap.reciconecta.ui.theme.ReciconectaTheme

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onNavigateToCreateProfile: () -> Unit = {}
) {
    var selectedOption by remember { mutableStateOf(3) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.primaryContainer)
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
                text = stringResource(R.string.onboarding_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.onboarding_selection_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            OptionCard(
                title = stringResource(R.string.profile_option_individual),
                description = stringResource(R.string.profile_option_individual_desc),
                icon = Icons.Default.Person,
                isSelected = selectedOption == 1,
                onClick = {
                    selectedOption = 1
                    onNavigateToCreateProfile()
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            OptionCard(
                title = stringResource(R.string.profile_option_company),
                description = stringResource(R.string.profile_option_company_desc),
                icon = Icons.Default.Home,
                isSelected = selectedOption == 2,
                onClick = {
                    selectedOption = 2
                    onNavigateToCreateProfile()
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            OptionCard(
                title = stringResource(R.string.profile_option_collector),
                description = stringResource(R.string.profile_option_collector_desc),
                icon = Icons.Default.Recycling,
                isSelected = selectedOption == 3,
                selectedCardBgColor = OrangeCardBackground,
                selectedCardBorderColor = OrangeCardBorder,
                selectedIconColor = OrangeIconColor,
                selectedIconBgColor = OrangeIconBackground,
                onClick = {
                    selectedOption = 3
                    onNavigateToCreateProfile()
                }
            )
        }
        val termsText = buildAnnotatedString {
            append(stringResource(R.string.onboarding_terms_consent))
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                append(stringResource(R.string.terms_of_use))
            }
        }
        Text(
            text = termsText,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
    }
}
@Composable
fun OptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    selectedCardBgColor: Color = MaterialTheme.colorScheme.primaryContainer,
    selectedCardBorderColor: Color = MaterialTheme.colorScheme.primary,
    selectedIconColor: Color? = null,
    selectedIconBgColor: Color? = null,
    onClick: () -> Unit
) {
    val cardBackground = if (isSelected) selectedCardBgColor else MaterialTheme.colorScheme.surface
    val cardBorderColor = if (isSelected) selectedCardBorderColor else MaterialTheme.colorScheme.outline
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    val descColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
    val arrowColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val iconBgColor = if (isSelected) {
        selectedIconBgColor ?: MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val iconColor = if (isSelected) {
        selectedIconColor ?: MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(cardBackground)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = cardBorderColor,
                shape = MaterialTheme.shapes.medium
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = textColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 13.sp),
                color = descColor
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Arrow right",
            tint = arrowColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    ReciconectaTheme {
        OnboardingScreen()
    }
}
