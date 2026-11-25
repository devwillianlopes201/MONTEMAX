package br.com.montemax.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.montemax.activity.model.Profissional
import br.com.montemax.api.RetrofitClient
import br.com.montemax.montemax.R
import br.com.montemax.montemax.databinding.ActivityAvaliarProfissionalBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.JsonObject

class AvaliarProfissionalActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAvaliarProfissionalBinding.inflate(layoutInflater)
    }

    private var profissional: Profissional? = null
    private var idUsuarioAtual = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Recebe os dados do profissional.
        profissional =
            intent.getSerializableExtra("profissional") as? Profissional

        // Recebe o ID do usuário que está avaliando.
        idUsuarioAtual = intent.getIntExtra("idUsuario", 0)

        carregarProfissional()

        // Configura o botão para enviar a avaliação.
        binding.btnEnviarAvaliacao.setOnClickListener {
            enviarAvaliacao()
        }
    }

    // Exibe o nome do profissional na tela.
    private fun carregarProfissional() {
        val profissional = profissional ?: return

        binding.tvNomeProfissional.text = profissional.nome
    }

    // Envia a avaliação para a API.
    private fun enviarAvaliacao() {

        val profissional = profissional ?: return

        // Obtém a nota selecionada.
        val nota = binding.ratingBar.rating.toInt()

        // Obtém o comentário digitado.
        val comentario = binding.tieComentario.text.toString().trim()

        // Verifica se a nota foi informada.
        if (nota == 0) {

            Toast.makeText(
                this,
                getString(R.string.str_nota_obrigatoria),
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Verifica se o usuário está identificado.
        if (idUsuarioAtual <= 0) {

            Toast.makeText(
                this,
                "Não foi possível identificar o usuário.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Desabilita o botão durante o envio.
        binding.btnEnviarAvaliacao.isEnabled = false

        // Chama o método da API para registrar a avaliação.
        RetrofitClient.apiService.registrarAvaliacao(
            nota,
            idUsuarioAtual,
            profissional.idProfissional,
            comentario.ifEmpty { null }
        ).enqueue(object : Callback<JsonObject> {

            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {

                if (response.isSuccessful) {

                    Toast.makeText(
                        this@AvaliarProfissionalActivity,
                        getString(R.string.str_avaliacao_recebida),
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } else {

                    binding.btnEnviarAvaliacao.isEnabled = true

                    if (response.code() == 409) {

                        Toast.makeText(
                            this@AvaliarProfissionalActivity,
                            getString(R.string.str_avaliacao_ja_realizada),
                            Toast.LENGTH_LONG
                        ).show()

                    } else {

                        Toast.makeText(
                            this@AvaliarProfissionalActivity,
                            "Não foi possível registrar a avaliação.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(
                call: Call<JsonObject>,
                t: Throwable
            ) {

                binding.btnEnviarAvaliacao.isEnabled = true

                Toast.makeText(
                    this@AvaliarProfissionalActivity,
                    "Erro de conexão com o servidor.",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}