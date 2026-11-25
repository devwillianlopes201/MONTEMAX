package br.com.montemax.activity.model

import java.io.Serializable

data class ServicoRecebido(
    val idSolicitacao: Long,
    val idUsuario: Int,
    val clienteNome: String,
    val clienteEmail: String,
    val clienteFoto: String?,
    val tipoServico: String,
    val descricao: String,
    val dataSolicitacao: String,
    val status: String
) : Serializable
