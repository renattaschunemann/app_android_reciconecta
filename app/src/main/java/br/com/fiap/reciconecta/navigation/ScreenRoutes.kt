package br.com.fiap.reciconecta.navigation

sealed class ScreenRoutes(val route: String) {
    object Onboarding : ScreenRoutes("onboarding")
    object CriarPerfil : ScreenRoutes("criar_perfil")
    object CriarPerfilPF : ScreenRoutes("criar_perfil_pf")
    object CriarPerfilPJ : ScreenRoutes("criar_perfil_pj")
    object CriarPerfilColetor : ScreenRoutes("criar_perfil_coletor")
    object Home : ScreenRoutes("home")
    object Coletas : ScreenRoutes("coletas")
    object Perfil : ScreenRoutes("perfil")
    object Mapa : ScreenRoutes("mapa")
    object Scanner : ScreenRoutes("scanner")
    object Login : ScreenRoutes("login")
}