package br.com.montemax.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.montemax.activity.adapter.PostagemAdapter
import br.com.montemax.activity.model.Postagem
import br.com.montemax.api.RetrofitClient
import br.com.montemax.montemax.databinding.ActivityComunidadeBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComunidadeActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityComunidadeBinding.inflate(layoutInflater)
    }

    // Cria o Adapter sem parâmetros.
    private val postagemAdapter by lazy {
        PostagemAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        // Configura a lista de publicações.
        binding.rvPostagens.apply {
            layoutManager = LinearLayoutManager(this@ComunidadeActivity)
            adapter = postagemAdapter
        }

        // Configura o clique do botão de comentários.
        postagemAdapter.itemClickComentarios = { postagem ->

            val intent = Intent(
                this@ComunidadeActivity,
                ComentarioActivity::class.java
            )

            intent.putExtra(
                "idPublicacao",
                postagem.idPublicacao
            )

            startActivity(intent)
        }

        // Não exibe os botões de editar e excluir na comunidade.
        postagemAdapter.mostrarAcoes = false

        // Abre a tela para criar uma nova publicação.
        binding.btnNovaPublicacao.setOnClickListener {

            val intent = Intent(
                this@ComunidadeActivity,
                NovaPublicacaoActivity::class.java
            )

            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        // Atualiza as publicações quando a tela fica visível.
        carregarPublicacoes()
    }

    // Consulta as publicações cadastradas no banco de dados.
    private fun carregarPublicacoes() {

        RetrofitClient.apiService.listarPublicacoes()
            .enqueue(object : Callback<JsonObject> {

                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {

                    if (response.isSuccessful) {

                        // Recupera a lista de publicações retornada pelo PHP.
                        val retorno = response.body()
                            ?.getAsJsonArray("retorno")

                        if (retorno == null) {

                            Toast.makeText(
                                this@ComunidadeActivity,
                                "Não foi possível carregar as publicações.",
                                Toast.LENGTH_SHORT
                            ).show()

                            return
                        }

                        // Converte o JSON recebido em uma lista de Postagem.
                        val tipoLista = object :
                            TypeToken<List<Postagem>>() {}.type

                        val publicacoes: List<Postagem> =
                            Gson().fromJson(
                                retorno,
                                tipoLista
                            )

                        // Atualiza a lista exibida na comunidade.
                        postagemAdapter.submitList(publicacoes)

                    } else if (response.code() == 404) {

                        // Limpa a lista quando não existem publicações.
                        postagemAdapter.submitList(emptyList())

                    } else {

                        Toast.makeText(
                            this@ComunidadeActivity,
                            "Não foi possível carregar as publicações.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<JsonObject>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@ComunidadeActivity,
                        "Erro de conexão com o servidor.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}