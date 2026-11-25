package br.com.montemax.activity.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Comentario(

    // Identificador do comentário.
    @SerializedName("id_comentario")
    val idComentario: Int,

    // Identificador da publicação.
    @SerializedName("id_publicacao")
    val idPublicacao: Int,

    // Identificador do usuário que comentou.
    @SerializedName("id_usuario")
    val idUsuario: Int,

    // Conteúdo do comentário.
    val conteudo: String,

    // Data em que o comentário foi registrado.
    @SerializedName("data_comentario")
    val dataComentario: String,

    // Nome de quem escreveu o comentário.
    @SerializedName("nome")
    val nomeAutor: String = "Usuário do MonteMax",

    // Foto de perfil de quem escreveu o comentário.
    @SerializedName("foto_perfil")
    val fotoPerfil: String? = null

) : Serializable