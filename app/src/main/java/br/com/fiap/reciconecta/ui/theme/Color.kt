package br.com.fiap.reciconecta.ui.theme

import androidx.compose.ui.graphics.Color

// ---------- LIGHT ----------
// --background: #F7F9F8
val LightBackground = Color(0xFFF7F9F8)
// --foreground: #2D2D2D
val LightOnBackground = Color(0xFF2D2D2D)
// --card / --popover: #FFFFFF
val LightSurface = Color(0xFFFFFFFF)
// --card-foreground / --popover-foreground: #2D2D2D
val LightOnSurface = Color(0xFF2D2D2D)
// --primary: #1B4332 (verde floresta da marca)
val LightPrimary = Color(0xFF1B4332)
// --primary-foreground: #FFFFFF
val LightOnPrimary = Color(0xFFFFFFFF)
// --secondary: #EBF3EF (usado também como primaryContainer, é o tom mais claro do verde)
val LightSecondary = Color(0xFFEBF3EF)
// --secondary-foreground: #1B4332
val LightOnSecondary = Color(0xFF1B4332)
// --accent: #52B788 (verde vibrante, mapeado como "secondary"/tertiary no M3 — ver Theme.kt)
val LightAccent = Color(0xFF52B788)
// --accent-foreground: #1B4332
val LightOnAccent = Color(0xFF1B4332)
// --muted: #F0F5F2 -> usado como surfaceVariant
val LightSurfaceVariant = Color(0xFFF0F5F2)
// --muted-foreground: #6B7280 -> onSurfaceVariant
val LightOnSurfaceVariant = Color(0xFF6B7280)
// --destructive: #C0392B
val LightError = Color(0xFFC0392B)
// --destructive-foreground: #FFFFFF
val LightOnError = Color(0xFFFFFFFF)
// --border: rgba(0,0,0,0.08) sobre fundo claro ≈ #E9EBEA (convertido, não é um HEX explícito no CSS)
val LightOutline = Color(0xFFE9EBEA)
// --input-background: #F0F5F2
val LightInputBackground = Color(0xFFF0F5F2)
// --switch-background: #A7C4B5
val LightSwitchBackground = Color(0xFFA7C4B5)
// --ring: #1B4332
val LightRing = Color(0xFF1B4332)

// Chart colors (--chart-1..5), úteis se o app tiver gráficos/estatísticas de coleta
val ChartGreenDark = Color(0xFF1B4332)   // --chart-1
val ChartGreenLight = Color(0xFF52B788)  // --chart-2
val ChartBlue = Color(0xFF3B82F6)        // --chart-3
val ChartAmber = Color(0xFFF59E0B)       // --chart-4
val ChartRed = Color(0xFFC0392B)         // --chart-5

// ---------- DARK ----------
// O bloco .dark do theme.css é o padrão neutro do shadcn/ui (OKLCH cinza),
// sem customização da marca. As cores abaixo foram DERIVADAS do verde da marca
// (#1B4332 / #52B788) para manter a identidade visual também no modo escuro —
// não vieram diretamente do arquivo CSS. Ajuste se o Figma Make gerar tokens
// de dark mode customizados no futuro.
val DarkBackground = Color(0xFF14201A)      // fundo escuro com leve matiz verde
val DarkOnBackground = Color(0xFFE3E9E5)
val DarkSurface = Color(0xFF1B2A22)
val DarkOnSurface = Color(0xFFE3E9E5)
val DarkPrimary = Color(0xFF74C69D)         // tom claro do verde da marca, para contraste em fundo escuro
val DarkOnPrimary = Color(0xFF0B2818)
val DarkSecondary = Color(0xFF2D6A4F)       // container mais escuro do verde
val DarkOnSecondary = Color(0xFFD8F3E3)
val DarkAccent = Color(0xFF52B788)          // mantém o mesmo verde vibrante do light (já funciona em fundo escuro)
val DarkOnAccent = Color(0xFF0B2818)
val DarkSurfaceVariant = Color(0xFF2A362F)
val DarkOnSurfaceVariant = Color(0xFFB3BFB8)
val DarkError = Color(0xFFE57373)
val DarkOnError = Color(0xFF3B0A06)
val DarkOutline = Color(0xFF3D4A43)
val DarkInputBackground = Color(0xFF2A362F)
val DarkSwitchBackground = Color(0xFF4C6A5C)
val DarkRing = Color(0xFF74C69D)