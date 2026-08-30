package br.com.fiap.reciconecta.domain.model

data class UserProfile(
    val nome: String,
    val email: String,
    val telefone: String,
    val cpf: String,
    val cep: String
)
