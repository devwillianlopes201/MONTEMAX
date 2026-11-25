package br.com.montemax.activity.model

object PostagemRepositorio {

    private val postagens =
        mutableListOf<Postagem>()

    fun adicionar(
        postagem: Postagem
    ) {
        postagens.add(postagem)
    }

    fun listarTodas(): List<Postagem> {
        return postagens.toList()
    }

    fun listarPorUsuario(
        idUsuario: Int
    ): List<Postagem> {

        return postagens.filter { postagem ->
            postagem.idUsuario == idUsuario
        }
    }

    fun atualizar(
        postagemAtualizada: Postagem
    ) {

        val indice =
            postagens.indexOfFirst { postagem ->

                postagem.idPublicacao ==
                        postagemAtualizada.idPublicacao
            }

        if (indice != -1) {

            postagens[indice] =
                postagemAtualizada
        }
    }

    fun excluir(
        idPublicacao: Int
    ) {

        // Exclui primeiro os comentários da publicação
        ComentarioRepositorio.excluirPorPublicacao(
            idPublicacao
        )

        // Depois exclui a publicação
        postagens.removeAll { postagem ->
            postagem.idPublicacao == idPublicacao
        }
    }
}
