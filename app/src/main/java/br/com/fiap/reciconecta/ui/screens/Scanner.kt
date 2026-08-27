package br.com.fiap.reciconecta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fiap.reciconecta.R
import br.com.fiap.reciconecta.ui.components.AppBottomBar
import br.com.fiap.reciconecta.ui.theme.DarkAccent
import br.com.fiap.reciconecta.ui.theme.ReciconectaTheme
import br.com.fiap.reciconecta.ui.theme.ScannerBackground
import br.com.fiap.reciconecta.ui.theme.ScannerCardBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    onBackClick: () -> Unit = {},
    onCodeClick: () -> Unit = {},
    onGalleryClick: () -> Unit = {},
    onCaptureClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ScannerBackground)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.scanner_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(ScannerCardBackground)
                            .clickable { onCodeClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan QR",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            AppBottomBar(
                currentRoute = "coletas",
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ScannerBackground),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier.size(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ScannerFrameCorners(color = DarkAccent)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(2.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        DarkAccent,
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.scanner_instruction),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomActionButton(
                    icon = Icons.Default.QrCode,
                    label = stringResource(R.string.scanner_action_code),
                    tintColor = DarkAccent,
                    backgroundColor = ScannerCardBackground,
                    onClick = onCodeClick
                )

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                        .background(ScannerBackground)
                        .clickable { onCaptureClick() }
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFF133825))
                    )
                }

                BottomActionButton(
                    icon = Icons.Default.Photo,
                    label = stringResource(R.string.scanner_action_gallery),
                    tintColor = DarkAccent,
                    backgroundColor = ScannerCardBackground,
                    onClick = onGalleryClick
                )
            }
        }
    }
}

@Composable
private fun ScannerFrameCorners(color: Color) {
    val cornerLength = 32.dp
    val strokeWidth = 3.dp

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(cornerLength)
                .align(Alignment.TopStart)
                .border(width = strokeWidth, color = color, shape = RoundedCornerShape(topStart = 16.dp))
        )
        Box(
            modifier = Modifier
                .size(cornerLength)
                .align(Alignment.TopEnd)
                .border(width = strokeWidth, color = color, shape = RoundedCornerShape(topEnd = 16.dp))
        )
        Box(
            modifier = Modifier
                .size(cornerLength)
                .align(Alignment.BottomStart)
                .border(width = strokeWidth, color = color, shape = RoundedCornerShape(bottomStart = 16.dp))
        )
        Box(
            modifier = Modifier
                .size(cornerLength)
                .align(Alignment.BottomEnd)
                .border(width = strokeWidth, color = color, shape = RoundedCornerShape(bottomEnd = 16.dp))
        )
    }
}

@Composable
private fun BottomActionButton(
    icon: ImageVector,
    label: String,
    tintColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(backgroundColor)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = tintColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ScannerScreenPreview() {
    ReciconectaTheme {
        ScannerScreen()
    }
}