package br.com.fiap.reciconecta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import br.com.fiap.reciconecta.navigation.NavigationRoutes
import br.com.fiap.reciconecta.ui.theme.ReciconectaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReciconectaTheme {
                // Cria o controlador de navegação
                val navController = rememberNavController()
                // Chama a sua função de rotas
                NavigationRoutes(navController = navController)
            }
        }
    }
}