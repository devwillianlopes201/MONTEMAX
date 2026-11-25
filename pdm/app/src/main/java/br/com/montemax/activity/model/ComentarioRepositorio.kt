package br.com.montemax.activity.model

object ComentarioRepositorio {

    // Lista que armazena os comentários temporariamente
    private val comentarios = mutableListOf<Comentario>()

    // Adiciona um novo comentário
    fun adicionar(comentario: Comentario) {
        comentarios.add(comentario)
    }

    // Lista os comentários de uma publicação específica
    fun listarPorPublicacao(idPublicacao: Int): List<Comentario> {
        return comentarios.filter { comentario ->
            comentario.idPublicacao == idPublicacao
        }
    }

    // Atualiza um comentário existente
    fun atualizar(comentarioAtualizado: Comentario) {
        val indice = comentarios.indexOfFirst { comentario ->
            comentario.idComentario == comentarioAtualizado.idComentario
        }

        if (indice != -1) {
            comentarios[indice] = comentarioAtualizado
        }
    }

    // Exclui um comentário pelo seu ID
    fun excluir(idComentario: Int) {
        comentarios.removeAll { comentario ->
            comentario.idComentario == idComentario
        }
    }

    // Exclui todos os comentários de uma publicação
    fun excluirPorPublicacao(idPublicacao: Int) {
        comentarios.removeAll { comentario ->
            comentario.idPublicacao == idPublicacao
        }
    }

    // Exclui todos os comentários feitos por um usuário
    fun excluirPorUsuario(idUsuario: Int) {
        comentarios.removeAll { comentario ->
            comentario.idUsuario == idUsuario
        }
    }
}