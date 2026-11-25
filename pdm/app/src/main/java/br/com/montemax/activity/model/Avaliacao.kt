package br.com.montemax.activity.model

import java.io.Serializable

data class Avaliacao(
    val idAvaliacao: Int,
    val nota: Int,
    val dataAvaliacao: String,
    val idUsuario: Int,
    val idProfissional: Int,
    val comentario: String?,
    val nomeCliente: String? = null
) : Serializable