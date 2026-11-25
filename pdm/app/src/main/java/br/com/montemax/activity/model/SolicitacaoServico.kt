package br.com.montemax.activity.model

import java.io.Serializable

data class SolicitacaoServico(
    val idSolicitacao: Long,
    val idUsuario: Int,
    val profissional: Profissional,
    val tipoServico: String,
    val descricao: String,
    val dataSolicitacao: String,
    val status: String
) : Serializable