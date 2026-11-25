package br.com.montemax.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.montemax.activity.adapter.ComentarioAdapter
import br.com.montemax.api.RetrofitClient
import br.com.montemax.activity.model.Comentario
import br.com.montemax.montemax.R
import br.com.montemax.montemax.databinding.ActivityComentarioBinding
import com.google.gson.JsonObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComentarioActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityComentarioBinding.inflate(layoutInflater)
    }

    private val comentarioAdapter by lazy {
        ComentarioAdapter()
    }

    private var idUsuarioAtual = 0

    private var idPublicacao = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        // Recupera o ID da publicação selecionada.
        idPublicacao = intent.getIntExtra("idPublicacao", 0)

        // Recupera o ID do usuário conectado.
        val preferencias = getSharedPreferences(
            "MonteMax",
            MODE_PRIVATE
        )

        idUsuarioAtual = preferencias.getInt("idUsuario", 0)

        // Verifica se existe uma publicação válida.
        if (idPublicacao <= 0) {

            Toast.makeText(
                this,
                "Publicação não encontrada.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        // Verifica se existe um usuário conectado.
        if (idUsuarioAtual <= 0) {

            Toast.makeText(
                this,
                "Faça login novamente para comentar.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        configurarRecyclerView()

        carregarComentarios()

        binding.btnPublicarComentario.setOnClickListener {
            publicarComentario()
        }
    }

    // Configura a lista de comentários.
    private fun configurarRecyclerView() {

        binding.rvComentarios.apply {

            layoutManager =
                LinearLayoutManager(this@ComentarioActivity)

            adapter = comentarioAdapter
        }
    }

    // Envia um novo comentário para o PHP.
    private fun publicarComentario() {

        val conteudo =
            binding.tieComentario.text.toString().trim()

        // Verifica se o campo foi preenchido.
        if (conteudo.isEmpty()) {

            binding.tilComentario.error =
                getString(R.string.str_comentario_obrigatorio)

            return

        } else {

            binding.tilComentario.error = null
        }

        // Desabilita o botão para evitar envios repetidos.
        binding.btnPublicarComentario.isEnabled = false

        RetrofitClient.apiService.registrarComentario(
            idPublicacao,
            idUsuarioAtual,
            conteudo
        ).enqueue(object : Callback<JsonObject> {

            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {

                binding.btnPublicarComentario.isEnabled = true

                if (response.isSuccessful) {

                    // Limpa o campo após o registro.
                    binding.tieComentario.text?.clear()

                    Toast.makeText(
                        this@ComentarioActivity,
                        getString(R.string.str_comentario_sucesso),
                        Toast.LENGTH_SHORT
                    ).show()

                    // Atualiza a lista com os dados do banco.
                    carregarComentarios()

                } else {

                    Toast.makeText(
                        this@ComentarioActivity,
                        "Não foi possível registrar o comentário.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<JsonObject>,
                t: Throwable
            ) {

                binding.btnPublicarComentario.isEnabled = true

                Toast.makeText(
                    this@ComentarioActivity,
                    "Erro de conexão com o servidor.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // Consulta os comentários da publicação no banco de dados.
    private fun carregarComentarios() {

        RetrofitClient.apiService.listarComentarios(
            idPublicacao
        ).enqueue(object : Callback<JsonObject> {

            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {

                if (response.isSuccessful) {

                    // Recupera a lista retornada pelo PHP.
                    val retorno = response.body()
                        ?.getAsJsonArray("retorno")

                    if (retorno == null) {

                        comentarioAdapter.submitList(emptyList())
                        return
                    }

                    // Converte o JSON em uma lista de Comentario.
                    val tipoLista = object :
                        TypeToken<List<Comentario>>() {}.type

                    val comentarios: List<Comentario> =
                        Gson().fromJson(
                            retorno,
                            tipoLista
                        )

                    // Atualiza a lista exibida na tela.
                    comentarioAdapter.submitList(comentarios)

                } else if (response.code() == 404) {

                    // Limpa a lista quando não existem comentários.
                    comentarioAdapter.submitList(emptyList())

                } else {

                    Toast.makeText(
                        this@ComentarioActivity,
                        "Não foi possível carregar os comentários.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<JsonObject>,
                t: Throwable
            ) {

                Toast.makeText(
                    this@ComentarioActivity,
                    "Erro de conexão com o servidor.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}