package br.com.montemax.activity.model

object AvaliacaoRepositorio {

    private val avaliacoes = mutableListOf<Avaliacao>()

    fun adicionar(avaliacao: Avaliacao): Boolean {

        val jaExiste = avaliacoes.any { item ->
            item.idUsuario == avaliacao.idUsuario &&
                    item.idProfissional == avaliacao.idProfissional
        }

        if (jaExiste) {
            return false
        }

        avaliacoes.add(avaliacao)
        return true
    }

    fun listarTodas(): List<Avaliacao> {
        return avaliacoes.toList()
    }

    fun listarPorUsuario(idUsuario: Int): List<Avaliacao> {
        return avaliacoes.filter { avaliacao ->
            avaliacao.idUsuario == idUsuario
        }
    }

    fun listarPorProfissional(idProfissional: Int): List<Avaliacao> {
        return avaliacoes.filter { avaliacao ->
            avaliacao.idProfissional == idProfissional
        }
    }

    fun jaFoiAvaliado(
        idUsuario: Int,
        idProfissional: Int
    ): Boolean {

        return avaliacoes.any { avaliacao ->
            avaliacao.idUsuario == idUsuario &&
                    avaliacao.idProfissional == idProfissional
        }
    }
}