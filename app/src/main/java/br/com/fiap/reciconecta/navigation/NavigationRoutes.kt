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

import br.com.fiap.reciconecta.ui.screens.onboarding.OnboardingScreen
import br.com.fiap.reciconecta.ui.screens.perfil.criar.CriarPerfilScreen
import br.com.fiap.reciconecta.ui.screens.perfil.criar.CriarPerfilPJScreen
import br.com.fiap.reciconecta.ui.screens.perfil.criar.CriarPerfilColetorScreen
import br.com.fiap.reciconecta.ui.screens.perfil.PerfilScreen
import br.com.fiap.reciconecta.ui.screens.impacto.ImpactoScreen
import br.com.fiap.reciconecta.ui.screens.coleta.ColetaScreen
import br.com.fiap.reciconecta.ui.screens.mapa.MapaScreen
import br.com.fiap.reciconecta.ui.screens.scanner.ScannerScreen
import br.com.fiap.reciconecta.ui.screens.login.LoginScreen
import br.com.fiap.reciconecta.ui.viewmodels.ColetaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {

    val context = LocalContext.current
    val sharedViewModel: ColetaViewModel = viewModel(context as ComponentActivity)

    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.Onboarding.route
    ) {
        composable(ScreenRoutes.Onboarding.route) {
            OnboardingScreen(
                onNavigateToCreateProfile = { profileType ->
                    when (profileType) {
                        1 -> navController.navigate(ScreenRoutes.CriarPerfilPF.route)
                        2 -> navController.navigate(ScreenRoutes.CriarPerfilPJ.route)
                        3 -> navController.navigate(ScreenRoutes.CriarPerfilColetor.route)
                        else -> navController.navigate(ScreenRoutes.CriarPerfilPF.route)
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(ScreenRoutes.Login.route)
                }
            )
        }

        composable(ScreenRoutes.CriarPerfilPF.route) {
            CriarPerfilScreen(
                isEditMode = false,
                onBackClick = { navController.popBackStack() },
                onLoginClick = {
                    navController.navigate(ScreenRoutes.Login.route) {
                        popUpTo(ScreenRoutes.Onboarding.route) { inclusive = false }
                    }
                },
                onSaveProfileClick = { _, _, _, _, _ ->
                    navController.navigate(ScreenRoutes.Login.route) {
                        popUpTo(ScreenRoutes.Onboarding.route) { inclusive = false }
                    }
                }
            )
        }

        composable(ScreenRoutes.CriarPerfilPJ.route) {
            CriarPerfilPJScreen(
                isEditMode = false,
                onBackClick = { navController.popBackStack() },
                onLoginClick = {
                    navController.navigate(ScreenRoutes.Login.route) {
                        popUpTo(ScreenRoutes.Onboarding.route) { inclusive = false }
                    }
                },
                onSaveProfileClick = { _, _, _, _, _ ->
                    navController.navigate(ScreenRoutes.Login.route) {
                        popUpTo(ScreenRoutes.Onboarding.route) { inclusive = false }
                    }
                }
            )
        }

        composable(ScreenRoutes.CriarPerfilColetor.route) {
            CriarPerfilColetorScreen(
                isEditMode = false,
                onBackClick = { navController.popBackStack() },
                onLoginClick = {
                    navController.navigate(ScreenRoutes.Login.route) {
                        popUpTo(ScreenRoutes.Onboarding.route) { inclusive = false }
                    }
                },
                onSaveProfileClick = { _, _, _, _, _ ->
                    navController.navigate(ScreenRoutes.Login.route) {
                        popUpTo(ScreenRoutes.Onboarding.route) { inclusive = false }
                    }
                }
            )
        }

        composable(ScreenRoutes.CriarPerfil.route) {
            CriarPerfilScreen(
                isEditMode = false,
                onBackClick = { navController.popBackStack() },
                onLoginClick = {
                    navController.navigate(ScreenRoutes.Login.route) {
                        popUpTo(ScreenRoutes.Onboarding.route) { inclusive = false }
                    }
                },
                onSaveProfileClick = { _, _, _, _, _ ->
                    navController.navigate(ScreenRoutes.Login.route) {
                        popUpTo(ScreenRoutes.Onboarding.route) { inclusive = false }
                    }
                }
            )
        }

        // Rota Dinâmica para Edição
        composable("editar_perfil/{tipoPerfil}") { backStackEntry ->
            val tipoPerfil = backStackEntry.arguments?.getString("tipoPerfil") ?: "PF"

            when (tipoPerfil) {
                "PJ" -> CriarPerfilPJScreen(
                    isEditMode = true,
                    onBackClick = { navController.popBackStack() },
                    onLoginClick = {},
                    onSaveProfileClick = { _, _, _, _, _ -> navController.popBackStack() }
                )
                "COLETOR" -> CriarPerfilColetorScreen(
                    isEditMode = true,
                    onBackClick = { navController.popBackStack() },
                    onLoginClick = {},
                    onSaveProfileClick = { _, _, _, _, _ -> navController.popBackStack() }
                )
                else -> CriarPerfilScreen(
                    isEditMode = true,
                    onBackClick = { navController.popBackStack() },
                    onLoginClick = {},
                    onSaveProfileClick = { _, _, _, _, _ -> navController.popBackStack() }
                )
            }
        }

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

        composable(ScreenRoutes.Home.route) {
            ImpactoScreen(
                onNavigate = { destination ->
                    navController.navigate(destination) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(ScreenRoutes.Perfil.route) {
            PerfilScreen(
                onNavigate = { destination ->
                    navController.navigate(destination) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onEditProfileClick = { tipoPerfil ->
                    navController.navigate("editar_perfil/$tipoPerfil")
                },
                onLogoutClick = {
                    navController.navigate(ScreenRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onDeleteAccountConfirm = {
                    navController.navigate(ScreenRoutes.Onboarding.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(ScreenRoutes.Coletas.route) {
            ColetaScreen(
                viewModel = sharedViewModel,
                onBackClick = { navController.popBackStack() },
                onAddItemClick = { navController.navigate(ScreenRoutes.Scanner.route) },
                onNavigateToMap = { navController.navigate(ScreenRoutes.Mapa.route) }
            )
        }

        composable(ScreenRoutes.Scanner.route) {
            ScannerScreen(
                onBackClick = { navController.popBackStack() },
                onItemScanned = { nome, qtd, unidade ->
                    sharedViewModel.addItem(
                        br.com.fiap.reciconecta.ui.viewmodels.ColetaItem(
                            id = System.currentTimeMillis().toString(),
                            name = nome,
                            amount = qtd,
                            unit = unidade
                        )
                    )
                    navController.popBackStack()
                }
            )
        }

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