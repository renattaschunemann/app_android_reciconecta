package br.com.fiap.reciconecta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.fiap.reciconecta.ui.theme.ReciconectaTheme
import br.com.fiap.reciconecta.ui.screens.PerfilScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReciconectaTheme {
                PerfilScreen()
            }
        }
    }
}


