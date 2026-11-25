package br.com.montemax.activity.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Postagem(

    // Identificador da publicação.
    @SerializedName("id_publicacao")
    val idPublicacao: Int,

    // Identificador do usuário que publicou.
    @SerializedName("id_usuario")
    val idUsuario: Int,

    // Nome do autor da publicação.
    @SerializedName("nome")
    val nomeAutor: String,

    // Foto de perfil do autor.
    @SerializedName("foto_perfil")
    val fotoPerfil: String?,

    // Título da publicação.
    val titulo: String,

    // Conteúdo da publicação.
    val conteudo: String,

    // Data da publicação.
    @SerializedName("data_publicacao")
    val dataPublicacao: String,

    // Fotos relacionadas à publicação.
    @SerializedName("fotos")
    val fotosPublicacao: List<FotoPublicacao> = emptyList()

) : Serializable