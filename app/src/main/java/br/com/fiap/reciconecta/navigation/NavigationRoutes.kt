package br.com.fiap.reciconecta.navigation

import androidx.activity.ComponentActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Imports de TODAS as suas telas baseadas na sua estrutura de pastas
import br.com.fiap.reciconecta.ui.screens.ColetaScreen
import br.com.fiap.reciconecta.ui.screens.CriarPerfilScreen
import br.com.fiap.reciconecta.ui.screens.ImpactoScreen
import br.com.fiap.reciconecta.ui.screens.MapaScreen
import br.com.fiap.reciconecta.ui.screens.PerfilScreen
import br.com.fiap.reciconecta.ui.screens.ScannerScreen
import br.com.fiap.reciconecta.ui.viewmodels.ColetaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {

    val context = LocalContext.current
    val sharedViewModel: ColetaViewModel = viewModel(context as ComponentActivity)

    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.CriarPerfil.route // 1. Fluxo correto: começa no perfil
    ) {

        // --- TELA CRIAR PERFIL ---
        composable(ScreenRoutes.CriarPerfil.route) {
            CriarPerfilScreen(
                onBackClick = { navController.popBackStack() },
                onCreateProfileClick = { _, _, _, _, _ ->
                    // 2. Vai para a Home e impede o usuário de voltar para a tela de criação apertando "Voltar"
                    navController.navigate(ScreenRoutes.Home.route) {
                        popUpTo(ScreenRoutes.CriarPerfil.route) { inclusive = true }
                    }
                }
            )
        }

        // --- TELA HOME (IMPACTO) ---
        composable(ScreenRoutes.Home.route) {
            ImpactoScreen(
                onNavigate = { destination ->
                    navController.navigate(destination) {
                        launchSingleTop = true // Evita empilhar várias telas iguais
                        restoreState = true
                    }
                }
            )
        }

        // --- TELA DE PERFIL ---
        composable(ScreenRoutes.Perfil.route) {
            PerfilScreen(
                onNavigate = { destination ->
                    navController.navigate(destination) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // --- TELA DE COLETAS ---
        composable(ScreenRoutes.Coletas.route) { backStackEntry ->
            val savedStateHandle = backStackEntry.savedStateHandle
            val scannedName = savedStateHandle.get<String>("scanned_name")
            val scannedUnit = savedStateHandle.get<String>("scanned_unit")

            ColetaScreen(
                viewModel = sharedViewModel,
                scannedItemName = scannedName,
                scannedItemUnit = scannedUnit,
                onClearScannedData = {
                    savedStateHandle.remove<String>("scanned_name")
                    savedStateHandle.remove<String>("scanned_unit")
                },
                onAddItemClick = { navController.navigate(ScreenRoutes.Scanner.route) },
                onConfirmClick = { navController.navigate(ScreenRoutes.Mapa.route) },
                onNavigate = { destination ->
                    navController.navigate(destination) { launchSingleTop = true }
                }
            )
        }

        // --- TELA DO SCANNER ---
        composable(ScreenRoutes.Scanner.route) {
            ScannerScreen(
                onBackClick = { navController.popBackStack() },
                onItemScanned = { nome, unidade ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("scanned_name", nome)
                    navController.previousBackStackEntry?.savedStateHandle?.set("scanned_unit", unidade)
                    navController.popBackStack()
                }
            )
        }

        // --- TELA DO MAPA ---
        composable(ScreenRoutes.Mapa.route) {
            MapaScreen(
                viewModel = sharedViewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(ScreenRoutes.Home.route) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}