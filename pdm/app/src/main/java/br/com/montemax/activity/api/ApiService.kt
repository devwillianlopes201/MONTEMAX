package br.com.montemax.api

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    // Cadastra um cliente ou profissional.
    @Multipart
    @POST("api/cadastro_usuario.php")
    fun cadastrarUsuario(
        @Part("nome") nome: RequestBody,
        @Part("email") email: RequestBody,
        @Part("senha") senha: RequestBody,
        @Part("tipo_usuario") tipoUsuario: RequestBody,
        @Part("whatsapp") whatsapp: RequestBody?,
        @Part("cidade") cidade: RequestBody?,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part("raio_atendimento_km") raioAtendimentoKm: RequestBody?,
        @Part("descricao") descricao: RequestBody?,
        @Part("categorias") categorias: RequestBody?,
        @Part("servicos") servicos: RequestBody?,
        @Part rgFrente: MultipartBody.Part?,
        @Part rgVerso: MultipartBody.Part?
    ): Call<JsonObject>

    // Realiza o login do usuário.
    @FormUrlEncoded
    @POST("api/login.php")
    fun fazerLogin(
        @Field("email") email: String,
        @Field("senha") senha: String
    ): Call<JsonObject>

    // Consulta os profissionais aprovados cadastrados no banco.
    @GET("api/listar_profissionais.php")
    fun listarProfissionais(): Call<JsonObject>

    // Envia os dados atualizados do perfil para a API.
    @Multipart
    @POST("api/editar_perfil.php")
    fun editarPerfil(
        @Part("id_usuario") idUsuario: RequestBody,
        @Part("nome") nome: RequestBody,
        @Part("email") email: RequestBody,
        @Part("whatsapp") whatsapp: RequestBody?,
        @Part("cidade") cidade: RequestBody?,
        @Part("descricao") descricao: RequestBody?,
        @Part("categorias") categorias: RequestBody?,
        @Part("servicos") servicos: RequestBody?,
        @Part fotoPerfil: MultipartBody.Part?
    ): Call<JsonObject>

    // Registra uma solicitação de serviço.
    @FormUrlEncoded
    @POST("api/registrar_solicitacao.php")
    fun registrarSolicitacao(
        @Field("id_usuario") idUsuario: Int,
        @Field("id_profissional") idProfissional: Int,
        @Field("id_servico") idServico: Int,
        @Field("descricao_solicitacao") descricao: String
    ): Call<JsonObject>

    // Lista as solicitações realizadas pelo cliente.
    @GET("api/listar_solicitacoes.php")
    fun listarSolicitacoes(
        @Query("id_usuario") idUsuario: Int
    ): Call<JsonObject>

    // Lista as solicitações recebidas pelo profissional.
    @GET("api/listar_servicos_recebidos.php")
    fun listarServicosRecebidos(
        @Query("id_usuario") idUsuario: Int
    ): Call<JsonObject>

    // Atualiza o status de uma solicitação.
    @FormUrlEncoded
    @POST("api/atualizar_status_solicitacao.php")
    fun atualizarStatusSolicitacao(
        @Field("id_solicitacao") idSolicitacao: Long,
        @Field("id_usuario") idUsuario: Int,
        @Field("status") status: String
    ): Call<JsonObject>

    // Confirma a conclusão de uma solicitação.
    @FormUrlEncoded
    @POST("api/confirmar_conclusao_solicitacao.php")
    fun confirmarConclusaoSolicitacao(
        @Field("id_solicitacao") idSolicitacao: Long,
        @Field("id_usuario") idUsuario: Int
    ): Call<JsonObject>

    // Registra a avaliação do profissional.
    @FormUrlEncoded
    @POST("api/registrar_avaliacao.php")
    fun registrarAvaliacao(
        @Field("nota") nota: Int,
        @Field("id_usuario") idUsuario: Int,
        @Field("id_profissional") idProfissional: Int,
        @Field("comentario") comentario: String?
    ): Call<JsonObject>

    // Lista as avaliações de um profissional.
    @GET("api/listar_avaliacoes.php")
    fun listarAvaliacoes(
        @Query("id_profissional") idProfissional: Int
    ): Call<JsonObject>

    // Registra uma publicação na comunidade.
    @FormUrlEncoded
    @POST("api/registrar_publicacao.php")
    fun registrarPublicacao(
        @Field("id_usuario") idUsuario: Int,
        @Field("titulo") titulo: String,
        @Field("conteudo") conteudo: String
    ): Call<JsonObject>

    // Lista as publicações da comunidade.
    @GET("api/listar_publicacoes.php")
    fun listarPublicacoes(): Call<JsonObject>

    // Envia várias fotos e sua descrição para uma publicação.
    @Multipart
    @POST("api/registrar_fotos_publicacao.php")
    fun registrarFotosPublicacao(
        @Part("id_publicacao") idPublicacao: RequestBody,
        @Part("descricao") descricao: RequestBody,
        @Part fotos: List<MultipartBody.Part>
    ): Call<JsonObject>

    // Registra um comentário em uma publicação.
    @FormUrlEncoded
    @POST("api/registrar_comentario.php")
    fun registrarComentario(
        @Field("id_publicacao") idPublicacao: Int,
        @Field("id_usuario") idUsuario: Int,
        @Field("conteudo") conteudo: String
    ): Call<JsonObject>

    // Lista os comentários de uma publicação.
    @GET("api/listar_comentarios.php")
    fun listarComentarios(
        @Query("id_publicacao") idPublicacao: Int
    ): Call<JsonObject>

    // Edita uma publicação do usuário.
    @FormUrlEncoded
    @POST("api/editar_publicacao.php")
    fun editarPublicacao(
        @Field("id_publicacao") idPublicacao: Int,
        @Field("id_usuario") idUsuario: Int,
        @Field("titulo") titulo: String,
        @Field("conteudo") conteudo: String
    ): Call<JsonObject>

    // Exclui uma publicação do usuário.
    @FormUrlEncoded
    @POST("api/excluir_publicacao.php")
    fun excluirPublicacao(
        @Field("id_publicacao") idPublicacao: Int,
        @Field("id_usuario") idUsuario: Int
    ): Call<JsonObject>

    // Exclui definitivamente a conta do usuário.
    @FormUrlEncoded
    @POST("api/excluir_conta.php")
    fun excluirConta(
        @Field("id_usuario") idUsuario: Int,
        @Field("senha") senha: String
    ): Call<JsonObject>
}