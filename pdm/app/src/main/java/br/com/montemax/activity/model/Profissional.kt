package br.com.montemax.activity.model

import java.io.Serializable

data class Profissional(
    val idProfissional: Int,
    val idUsuario: Int,
    val nome: String,
    val email: String,
    val whatsapp: String,
    val areasAtuacao: String,
    val cidade: String,
    val fotoPerfil: String?,
    val descricao: String? = null,
    val servicos: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val raioAtendimentoKm: Double? = null

) : Serializable
