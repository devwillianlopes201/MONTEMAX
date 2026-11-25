package br.com.montemax.activity

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.montemax.api.RetrofitClient
import br.com.montemax.activity.model.Profissional
import br.com.montemax.montemax.R
import br.com.montemax.montemax.databinding.ActivityRegistrarServicoBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrarServicoActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityRegistrarServicoBinding.inflate(layoutInflater)
    }

    private var profissional: Profissional? = null

    private val servicos = arrayOf(
        "Selecione um serviço",
        "Montagem de móveis",
        "Desmontagem de móveis",
        "Transporte de móveis"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        profissional = intent.getSerializableExtra(
            "profissional"
        ) as? Profissional

        if (profissional == null) {
            Toast.makeText(
                this,
                "Não foi possível identificar o profissional.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        carregarProfissional()
        configurarSpinner()

        binding.btnRegistrar.setOnClickListener {
            registrarServico()
        }
    }

    private fun carregarProfissional() {

        val profissional = profissional ?: return

        binding.tvProfissional.text = profissional.nome
    }

    private fun configurarSpinner() {

        val adapter = ArrayAdapter(
            this@RegistrarServicoActivity,
            android.R.layout.simple_spinner_item,
            servicos
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.spServico.adapter = adapter
    }

    private fun registrarServico() {

        val profissional = profissional ?: return

        val descricao =
            binding.tieDescricaoSolicitacao.text
                .toString()
                .trim()

        var isValido = true

        if (binding.spServico.selectedItemPosition == 0) {

            Toast.makeText(
                this@RegistrarServicoActivity,
                getString(R.string.str_servico_obrigatorio),
                Toast.LENGTH_SHORT
            ).show()

            isValido = false
        }

        if (descricao.isEmpty()) {

            binding.tilDescricaoSolicitacao.error =
                getString(R.string.str_descricao_obrigatoria)

            isValido = false

        } else {

            binding.tilDescricaoSolicitacao.error = null
        }

        if (!isValido) {
            return
        }

        val preferencias = getSharedPreferences(
            "MonteMax",
            MODE_PRIVATE
        )

        // Recupera o ID do cliente conectado.
        val idUsuario = preferencias.getInt("idUsuario", -1)

        // Verifica se quem está solicitando é um cliente.
        val tipoUsuario = preferencias.getInt("tipoUsuario", -1)

        if (idUsuario <= 0 || tipoUsuario != 0) {

            Toast.makeText(
                this,
                "Faça login como cliente para solicitar um serviço.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Recupera o ID do serviço selecionado.
        val idServico = binding.spServico.selectedItemPosition

        // Envia a solicitação para a API PHP.
        RetrofitClient.apiService.registrarSolicitacao(
            idUsuario = idUsuario,
            idProfissional = profissional.idProfissional,
            idServico = idServico,
            descricao = descricao
        ).enqueue(object : Callback<JsonObject> {

            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {

                if (response.isSuccessful) {

                    val retorno = response.body()?.get("retorno")

                    if (retorno != null && !retorno.isJsonNull) {

                        Toast.makeText(
                            this@RegistrarServicoActivity,
                            getString(R.string.str_registro_sucesso),
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    } else {

                        Toast.makeText(
                            this@RegistrarServicoActivity,
                            "Não foi possível registrar a solicitação.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {

                    Toast.makeText(
                        this@RegistrarServicoActivity,
                        "Erro ao registrar solicitação. Código: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<JsonObject>,
                t: Throwable
            ) {

                Toast.makeText(
                    this@RegistrarServicoActivity,
                    "Falha na comunicação com o servidor.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
