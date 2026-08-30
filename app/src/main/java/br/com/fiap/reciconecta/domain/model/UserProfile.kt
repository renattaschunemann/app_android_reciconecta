package br.com.fiap.reciconecta.domain.model

data class UserProfile(
    val nome: String,
    val email: String,
    val telefone: String,
    val cpfOrCnpj: String,
    val cep: String,
    val tipoPerfil: String = "PF", // "PF", "PJ", "COLETOR"
    val materiais: List<String> = emptyList()
)
