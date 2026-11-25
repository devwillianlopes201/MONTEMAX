package br.com.montemax.activity.model

import java.io.Serializable

// Classe que representa os dados de um usuário do MonteMax
data class Usuario(
    val idUsuario: Int,
    val tipoUsuario: Int,
    val nome: String,
    val email: String,
    val senha: String,
    val fotoPerfil: String?

) : Serializable
