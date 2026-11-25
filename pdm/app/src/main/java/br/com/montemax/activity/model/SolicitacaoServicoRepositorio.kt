package br.com.montemax.activity.model

object SolicitacaoServicoRepositorio {

    private val solicitacoes =
        mutableListOf<SolicitacaoServico>()

    fun adicionar(solicitacao: SolicitacaoServico) {
        solicitacoes.add(solicitacao)
    }

    fun listarTodas(): List<SolicitacaoServico> {
        return solicitacoes.toList()
    }

    fun listarPorUsuario(idUsuario: Int): List<SolicitacaoServico> {
        return solicitacoes.filter { solicitacao ->
            solicitacao.idUsuario == idUsuario
        }
    }

    fun listarPorProfissional(idProfissional: Int): List<SolicitacaoServico> {
        return solicitacoes.filter { solicitacao ->
            solicitacao.profissional.idProfissional == idProfissional
        }
    }

    fun atualizar(solicitacaoAtualizada: SolicitacaoServico) {
        val indice = solicitacoes.indexOfFirst { solicitacao ->
            solicitacao.idSolicitacao == solicitacaoAtualizada.idSolicitacao
        }

        if (indice != -1) {
            solicitacoes[indice] = solicitacaoAtualizada
        }
    }
}
