package br.com.fiap.reciconecta.navigation

sealed class ScreenRoutes(val route: String) {
    object Onboarding : ScreenRoutes("onboarding")
    object CriarPerfil : ScreenRoutes("criar_perfil")
    // Rotas das abas principais (Bottom Bar)
    object Home : ScreenRoutes("inicio")
    object Coletas : ScreenRoutes("coletas")
    object Perfil : ScreenRoutes("perfil")
    // Outras telas do fluxo
    object Mapa : ScreenRoutes("mapa")
    object Scanner : ScreenRoutes("scanner")
}