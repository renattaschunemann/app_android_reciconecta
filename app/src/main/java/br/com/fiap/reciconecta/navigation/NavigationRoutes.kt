package br.com.fiap.reciconecta.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.com.fiap.reciconecta.ui.screens.onboarding.OnboardingScreen
import br.com.fiap.reciconecta.ui.screens.perfil.criar.CriarPerfilScreen
import br.com.fiap.reciconecta.ui.screens.perfil.PerfilScreen
import br.com.fiap.reciconecta.ui.screens.impacto.ImpactoScreen
import br.com.fiap.reciconecta.ui.screens.coleta.ColetaScreen
import br.com.fiap.reciconecta.ui.screens.mapa.MapaScreen
import br.com.fiap.reciconecta.ui.screens.scanner.ScannerScreen
import br.com.fiap.reciconecta.ui.screens.login.LoginScreen

@Composable
fun NavigationRoutes(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.Onboarding.route,
        modifier = modifier
    ) {
        // Tela de Onboarding (Onboarding.kt)
        composable(ScreenRoutes.Onboarding.route) {
            OnboardingScreen(
                onNavigateToCreateProfile = {
                    navController.navigate(ScreenRoutes.CriarPerfil.route)
                },
                onNavigateToLogin = {
                    navController.navigate(ScreenRoutes.Login.route)
                }
            )
        }
        // Tela de Criar Perfil (CriarPerfil.kt)
        composable(ScreenRoutes.CriarPerfil.route) {
            CriarPerfilScreen(
                onBackClick = { navController.popBackStack() },
                onLoginClick = { navController.navigate(ScreenRoutes.Login.route) },
                onCreateProfileClick = { _, _, _, _, _ ->
                    navController.navigate(ScreenRoutes.Home.route) {
                        popUpTo(ScreenRoutes.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        // Tela de Login (LoginScreen.kt)
        composable(ScreenRoutes.Login.route) {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onRegisterClick = {
                    navController.navigate(ScreenRoutes.Onboarding.route) {
                        popUpTo(ScreenRoutes.Onboarding.route) { inclusive = true }
                    }
                },
                onLoginSuccess = {
                    navController.navigate(ScreenRoutes.Home.route) {
                        popUpTo(ScreenRoutes.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        // Tela Home (Impacto.kt)
        composable(ScreenRoutes.Home.route) {
            ImpactoScreen(
                onRecycleClick = { navController.navigate(ScreenRoutes.Coletas.route) },
                onNavigate = { destination ->
                    navController.navigate(destination) {
                        launchSingleTop = true
                    }
                }
            )
        }
        // Tela de Coletas (Coleta.kt)
        composable(ScreenRoutes.Coletas.route) {
            ColetaScreen(
                onAddItemClick = { navController.navigate(ScreenRoutes.Scanner.route) },
                onBackClick = { navController.popBackStack() },
                onConfirmClick = { navController.navigate(ScreenRoutes.Mapa.route) },
                onNavigate = { destination ->
                    navController.navigate(destination) {
                        launchSingleTop = true
                    }
                }
            )
        }
        // Tela de Perfil (Perfil.kt)
        composable(ScreenRoutes.Perfil.route) {
            PerfilScreen(
                onNavigate = { destination ->
                    navController.navigate(destination) {
                        launchSingleTop = true
                    }
                }
            )
        }
        // Tela do Mapa (Mapa.kt)
        composable(ScreenRoutes.Mapa.route) {
            MapaScreen(
                onNavigate = { destination ->
                    navController.navigate(destination) {
                        launchSingleTop = true
                    }
                }
            )
        }
        // Tela do Scanner (Scanner.kt)
        composable(ScreenRoutes.Scanner.route) {
            ScannerScreen(
                onNavigate = { destination ->
                    navController.navigate(destination) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}