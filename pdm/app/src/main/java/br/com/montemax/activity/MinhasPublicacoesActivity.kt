package br.com.montemax.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.montemax.activity.adapter.PostagemAdapter
import br.com.montemax.activity.model.Postagem
import br.com.montemax.api.RetrofitClient
import br.com.montemax.montemax.databinding.ActivityMinhasPublicacoesBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MinhasPublicacoesActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMinhasPublicacoesBinding.inflate(layoutInflater)
    }

    private var idUsuarioAtual: Int = 0

    // Adapter responsável por exibir as publicações.
    private lateinit var postagemAdapter: PostagemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        // Recupera o ID do usuário conectado.
        val preferencias = getSharedPreferences(
            "MonteMax",
            MODE_PRIVATE
        )

        idUsuarioAtual = preferencias.getInt("idUsuario", 0)

        if (idUsuarioAtual <= 0) {

            Toast.makeText(
                this,
                "Não foi possível identificar o usuário. Faça login novamente.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        configurarRecyclerView()
    }

    private fun configurarRecyclerView() {

        // Define como os itens serão organizados na tela.
        binding.rvMinhasPublicacoes.layoutManager =
            LinearLayoutManager(this)

        // Cria o Adapter sem parâmetros.
        postagemAdapter = PostagemAdapter()

        // Abre a tela de comentários.
        postagemAdapter.itemClickComentarios = { postagem ->

            val intent = Intent(
                this@MinhasPublicacoesActivity,
                ComentarioActivity::class.java
            )

            intent.putExtra(
                "idPublicacao",
                postagem.idPublicacao
            )

            startActivity(intent)
        }

        // Configura a ação para editar uma publicação.
        postagemAdapter.itemClickEditar = { postagem ->
            editarPublicacao(postagem)
        }

        // Configura a ação para excluir uma publicação.
        postagemAdapter.itemClickExcluir = { postagem ->
            confirmarExclusao(postagem)
        }

        // Exibe os botões de editar e excluir.
        postagemAdapter.mostrarAcoes = true

        // Define o Adapter do RecyclerView.
        binding.rvMinhasPublicacoes.adapter = postagemAdapter
    }

    private fun carregarPublicacoes() {

        // Busca as publicações cadastradas na API.
        RetrofitClient.apiService.listarPublicacoes()
            .enqueue(object : Callback<JsonObject> {

                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {

                    if (response.isSuccessful) {

                        val json = response.body()

                        val retorno =
                            json?.getAsJsonArray("retorno")

                        if (retorno != null) {

                            // Converte o JSON em uma lista de publicações.
                            val tipoLista =
                                object : TypeToken<List<Postagem>>() {}.type

                            val publicacoes: List<Postagem> =
                                Gson().fromJson(retorno, tipoLista)

                            // Filtra somente as publicações do usuário conectado.
                            val minhasPublicacoes =
                                publicacoes.filter {
                                    it.idUsuario == idUsuarioAtual
                                }

                            // Atualiza a lista.
                            postagemAdapter.submitList(minhasPublicacoes)

                            // Verifica se existem publicações.
                            binding.tvSemPublicacoes.visibility =
                                if (minhasPublicacoes.isEmpty()) {
                                    View.VISIBLE
                                } else {
                                    View.GONE
                                }

                        } else {

                            postagemAdapter.submitList(emptyList())

                            binding.tvSemPublicacoes.visibility =
                                View.VISIBLE
                        }

                    } else if (response.code() == 404) {

                        // Não existem publicações cadastradas.
                        postagemAdapter.submitList(emptyList())

                        binding.tvSemPublicacoes.visibility =
                            View.VISIBLE

                    } else {

                        Toast.makeText(
                            this@MinhasPublicacoesActivity,
                            "Não foi possível carregar suas publicações.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<JsonObject>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@MinhasPublicacoesActivity,
                        "Erro de conexão: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun editarPublicacao(postagem: Postagem) {

        // Verifica se a publicação pertence ao usuário conectado.
        if (postagem.idUsuario != idUsuarioAtual) {

            Toast.makeText(
                this,
                "Você não pode editar esta publicação.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val intent = Intent(
            this,
            EditarPublicacaoActivity::class.java
        )

        intent.putExtra("postagem", postagem)

        startActivity(intent)
    }

    private fun confirmarExclusao(postagem: Postagem) {

        // Verifica se a publicação pertence ao usuário conectado.
        if (postagem.idUsuario != idUsuarioAtual) {

            Toast.makeText(
                this,
                "Você não pode excluir esta publicação.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        AlertDialog.Builder(this)
            .setTitle("Excluir publicação")
            .setMessage(
                "Tem certeza de que deseja excluir esta publicação?"
            )
            .setPositiveButton("Sim") { _, _ ->
                excluirPublicacao(postagem)
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun excluirPublicacao(postagem: Postagem) {

        // Envia a solicitação de exclusão para a API.
        RetrofitClient.apiService.excluirPublicacao(
            idPublicacao = postagem.idPublicacao,
            idUsuario = idUsuarioAtual
        ).enqueue(object : Callback<JsonObject> {

            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {

                if (response.isSuccessful) {

                    Toast.makeText(
                        this@MinhasPublicacoesActivity,
                        "Publicação excluída com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Atualiza a lista depois da exclusão.
                    carregarPublicacoes()

                } else {

                    Toast.makeText(
                        this@MinhasPublicacoesActivity,
                        "Não foi possível excluir a publicação. Código: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<JsonObject>,
                t: Throwable
            ) {

                Toast.makeText(
                    this@MinhasPublicacoesActivity,
                    "Erro de conexão: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()

        // Atualiza a lista quando o usuário retorna à tela.
        if (idUsuarioAtual > 0 && ::postagemAdapter.isInitialized) {
            carregarPublicacoes()
        }
    }
}