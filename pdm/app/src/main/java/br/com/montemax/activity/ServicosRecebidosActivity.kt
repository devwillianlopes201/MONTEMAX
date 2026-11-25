package br.com.montemax.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.montemax.activity.adapter.ServicosRecebidosAdapter
import br.com.montemax.activity.model.ServicoRecebido
import br.com.montemax.api.RetrofitClient
import br.com.montemax.montemax.databinding.ActivityServicosRecebidosBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServicosRecebidosActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityServicosRecebidosBinding.inflate(layoutInflater)
    }

    private val servicosAdapter by lazy {
        ServicosRecebidosAdapter()
    }

    private var idProfissionalAtual = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val preferencias = getSharedPreferences("MonteMax", MODE_PRIVATE)

        // Recupera o ID do usuário profissional conectado.
        idProfissionalAtual = preferencias.getInt("idUsuario", -1)

        // Verifica o tipo de usuário conectado.
        val tipoUsuario = preferencias.getInt("tipoUsuario", -1)

        // Verifica se existe uma sessão válida de profissional.
        if (idProfissionalAtual <= 0 || tipoUsuario != 1) {

            Toast.makeText(
                this,
                "Acesso permitido somente para profissionais.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        configurarRecyclerView()
        configurarEventos()
    }

    override fun onResume() {
        super.onResume()

        if (idProfissionalAtual > 0) {
            carregarServicos()
        }
    }

    private fun configurarRecyclerView() {

        binding.rvServicosRecebidos.apply {
            layoutManager = LinearLayoutManager(
                this@ServicosRecebidosActivity
            )

            adapter = servicosAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun configurarEventos() {

        // Evento do botão Aprovar.
        servicosAdapter.itemClickAprovar = { servico ->
            atualizarStatus(servico, "Aprovada")
        }

        // Evento do botão Recusar.
        servicosAdapter.itemClickRecusar = { servico ->
            atualizarStatus(servico, "Recusada")
        }

        // Evento do botão Iniciar serviço.
        servicosAdapter.itemClickIniciar = { servico ->
            atualizarStatus(servico, "Em andamento")
        }

        // Evento do botão Informar conclusão.
        servicosAdapter.itemClickConcluir = { servico ->
            atualizarStatus(servico, "Aguardando confirmação")
        }
    }

    private fun carregarServicos() {

        // Consulta as solicitações recebidas pelo profissional na API PHP.
        RetrofitClient.apiService.listarServicosRecebidos(
            idProfissionalAtual
        ).enqueue(object : Callback<JsonObject> {

            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {

                if (response.isSuccessful) {

                    // Recupera a lista de solicitações retornada pelo PHP.
                    val jsonArray = response.body()
                        ?.getAsJsonArray("retorno")

                    // Converte os dados JSON para objetos Kotlin.
                    val servicos = if (jsonArray != null) {

                        Gson().fromJson(
                            jsonArray,
                            Array<ServicoRecebido>::class.java
                        ).toList()

                    } else {

                        emptyList()
                    }

                    // Atualiza a lista exibida na tela.
                    servicosAdapter.submitList(servicos)

                    // Verifica se existem solicitações recebidas.
                    if (servicos.isEmpty()) {

                        binding.tvNenhumServico.visibility = View.VISIBLE
                        binding.rvServicosRecebidos.visibility = View.GONE

                    } else {

                        binding.tvNenhumServico.visibility = View.GONE
                        binding.rvServicosRecebidos.visibility = View.VISIBLE
                    }

                } else if (response.code() == 404) {

                    // Exibe a lista vazia quando não existem solicitações.
                    servicosAdapter.submitList(emptyList())

                    binding.tvNenhumServico.visibility = View.VISIBLE
                    binding.rvServicosRecebidos.visibility = View.GONE

                } else {

                    Toast.makeText(
                        this@ServicosRecebidosActivity,
                        "Não foi possível carregar os serviços recebidos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<JsonObject>,
                t: Throwable
            ) {

                Toast.makeText(
                    this@ServicosRecebidosActivity,
                    "Falha na comunicação com o servidor.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun atualizarStatus(
        servico: ServicoRecebido,
        novoStatus: String
    ) {

        // Envia a atualização do status para a API PHP.
        RetrofitClient.apiService.atualizarStatusSolicitacao(
            servico.idSolicitacao,
            idProfissionalAtual,
            novoStatus
        ).enqueue(object : Callback<JsonObject> {

            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {

                if (response.isSuccessful) {

                    // Atualiza o status do serviço na lista.
                    val servicosAtualizados = servicosAdapter.currentList.map {
                        if (it.idSolicitacao == servico.idSolicitacao) {
                            it.copy(status = novoStatus)
                        } else {
                            it
                        }
                    }

                    servicosAdapter.submitList(servicosAtualizados)

                    Toast.makeText(
                        this@ServicosRecebidosActivity,
                        "Solicitação atualizada com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        this@ServicosRecebidosActivity,
                        "Não foi possível atualizar a solicitação.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<JsonObject>,
                t: Throwable
            ) {

                Toast.makeText(
                    this@ServicosRecebidosActivity,
                    "Falha na comunicação com o servidor.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}