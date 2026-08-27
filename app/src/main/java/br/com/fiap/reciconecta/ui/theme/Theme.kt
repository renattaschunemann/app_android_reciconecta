package br.com.fiap.reciconecta.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

/**
 * ColorScheme claro, mapeado 1:1 a partir de theme.css (:root) do protótipo Figma Make.
 */
private val LightColors = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightSecondary,          // --secondary usado como container do primary
    onPrimaryContainer = LightOnSecondary,
    secondary = LightAccent,                    // --accent
    onSecondary = LightOnAccent,
    secondaryContainer = LightSecondary,
    onSecondaryContainer = LightOnSecondary,
    tertiary = ChartBlue,                       // --chart-3, cor extra de apoio (não definida como tertiary no CSS)
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    error = LightError,
    onError = LightOnError,
    outline = LightOutline,
    outlineVariant = LightOutline,
)

/**
 * ColorScheme escuro. Derivado da marca (não vem do theme.css — ver Colors.kt).
 */
private val DarkColors = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkSecondary,
    onPrimaryContainer = DarkOnSecondary,
    secondary = DarkAccent,
    onSecondary = DarkOnAccent,
    secondaryContainer = DarkSecondary,
    onSecondaryContainer = DarkOnSecondary,
    tertiary = ChartBlue,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = DarkError,
    onError = DarkOnError,
    outline = DarkOutline,
    outlineVariant = DarkOutline,
)

/**
 * Tipografia base. O protótipo usa a fonte "Nunito Sans" (fonts.css, Google Fonts).
 * Para usá-la no Android, baixe os arquivos .ttf da família e adicione em res/font/,
 * depois troque FontFamily.Default abaixo por um FontFamily customizado.
 * font-weight-medium do CSS = 600 (light) / 500 (dark) -> aqui usamos SemiBold como padrão.
 */
val ReciconectaTypography = Typography(
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 36.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 30.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 27.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
)

/**
 * Shapes derivados de --radius: 0.75rem (12dp), com --radius-sm/-lg/-xl proporcionais ao CSS.
 */
val ReciconectaShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),  // radius - 4px
    small = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),      // radius - 2px
    medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),     // radius
    large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),      // radius + 4px
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
)

@Composable
fun ReciconectaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Cor dinâmica (Material You) desligada por padrão para preservar a identidade visual da marca.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ReciconectaTypography,
        shapes = ReciconectaShapes,
        content = content
    )
}
