package br.com.fiap.reciconecta.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import br.com.fiap.reciconecta.ui.screens.ColetaItem

class ColetaViewModel : ViewModel() {

    // Lista persistida na memória com dados mockados
    private val _itemsList = mutableStateListOf(
        ColetaItem(id = "mock1", name = "PET/Plástico", amount = 1.5, unit = "kg"),
        ColetaItem(id = "mock2", name = "Papelão", amount = 2.0, unit = "kg")
    )

    // Expondo a lista de forma segura para a tela
    val itemsList: List<ColetaItem> get() = _itemsList

    fun addItem(item: ColetaItem) {
        _itemsList.add(item)
    }

    fun removeItem(item: ColetaItem) {
        _itemsList.remove(item)
    }
}