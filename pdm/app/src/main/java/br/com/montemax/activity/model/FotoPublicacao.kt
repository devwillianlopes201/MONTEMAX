package br.com.montemax.activity.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FotoPublicacao(

    // Identificador da foto.
    @SerializedName("id_foto")
    val idFoto: Int,

    // Identificador da publicação relacionada.
    @SerializedName("id_publicacao")
    val idPublicacao: Int,

    // Caminho da imagem armazenada no servidor.
    @SerializedName("imagem_url")
    val imagemUrl: String,

    // Descrição da foto.
    val descricao: String?

) : Serializable