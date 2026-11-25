package br.com.montemax.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.montemax.activity.model.Postagem
import br.com.montemax.api.RetrofitClient
import br.com.montemax.montemax.R
import br.com.montemax.montemax.databinding.ActivityEditarPublicacaoBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditarPublicacaoActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityEditarPublicacaoBinding.inflate(layoutInflater)
    }

    private var postagem: Postagem? = null

    private var idUsuarioAtual = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        // Recupera o ID do usuário conectado.
        val preferencias = getSharedPreferences(
            "MonteMax",
            MODE_PRIVATE
        )

        idUsuarioAtual = preferencias.getInt("idUsuario", 0)

        // Recupera a publicação enviada pela tela anterior.
        postagem = intent.getSerializableExtra(
            "postagem"
        ) as? Postagem

        // Verifica se a publicação existe.
        if (postagem == null) {

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
                "Faça login novamente.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        // Verifica se a publicação pertence ao usuário.
        if (postagem?.idUsuario != idUsuarioAtual) {

            Toast.makeText(
                this,
                "Você não pode editar esta publicação.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        carregarDados()

        binding.btnSalvar.setOnClickListener {
            salvarAlteracoes()
        }
    }

    private fun carregarDados() {

        val postagemAtual = postagem ?: return

        // Preenche os campos com os dados atuais.
        binding.tieTitulo.setText(
            postagemAtual.titulo
        )

        binding.tieConteudo.setText(
            postagemAtual.conteudo
        )
    }

    private fun salvarAlteracoes() {

        val titulo =
            binding.tieTitulo.text.toString().trim()

        val conteudo =
            binding.tieConteudo.text.toString().trim()

        var isValido = true

        // Validação do título.
        if (titulo.isEmpty()) {

            binding.tilTitulo.error =
                getString(R.string.str_titulo_obrigatorio)

            isValido = false

        } else {

            binding.tilTitulo.error = null
        }

        // Validação do conteúdo.
        if (conteudo.isEmpty()) {

            binding.tilConteudo.error =
                getString(R.string.str_conteudo_obrigatorio)

            isValido = false

        } else {

            binding.tilConteudo.error = null
        }

        if (isValido) {

            val postagemAtual = postagem ?: return

            // Confere se a publicação pertence ao usuário.
            if (postagemAtual.idUsuario != idUsuarioAtual) {

                Toast.makeText(
                    this,
                    "Você não pode editar esta publicação.",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
                return
            }

            // Envia as alterações para a API.
            RetrofitClient.apiService.editarPublicacao(
                idPublicacao = postagemAtual.idPublicacao,
                idUsuario = idUsuarioAtual,
                titulo = titulo,
                conteudo = conteudo
            ).enqueue(object : Callback<JsonObject> {

                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@EditarPublicacaoActivity,
                            getString(
                                R.string.str_editar_publicacao_sucesso
                            ),
                            Toast.LENGTH_SHORT
                        ).show()

                        setResult(RESULT_OK)

                        finish()

                    } else {

                        Toast.makeText(
                            this@EditarPublicacaoActivity,
                            "Não foi possível editar a publicação. Código: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<JsonObject>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@EditarPublicacaoActivity,
                        "Erro de conexão: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }
}