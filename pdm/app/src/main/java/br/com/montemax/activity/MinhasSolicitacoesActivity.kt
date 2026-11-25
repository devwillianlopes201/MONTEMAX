package br.com.montemax.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.montemax.activity.adapter.SolicitacaoServicoAdapter
import br.com.montemax.activity.model.SolicitacaoServico
import br.com.montemax.api.RetrofitClient
import br.com.montemax.montemax.databinding.ActivityMinhasSolicitacoesBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MinhasSolicitacoesActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMinhasSolicitacoesBinding.inflate(layoutInflater)
    }

    private val solicitacaoAdapter by lazy {
        SolicitacaoServicoAdapter()
    }

    private var idUsuarioAtual = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val preferencias = getSharedPreferences("MonteMax", MODE_PRIVATE)
        idUsuarioAtual = preferencias.getInt("idUsuario", 1)

        configurarRecyclerView()
        configurarEventos()
    }

    override fun onResume() {
        super.onResume()
        carregarSolicitacoes()
    }

    private fun configurarRecyclerView() {
        binding.rvSolicitacoes.apply {
            layoutManager = LinearLayoutManager(this@MinhasSolicitacoesActivity)
            adapter = solicitacaoAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun configurarEventos() {

        solicitacaoAdapter.itemClickConfirmar = { solicitacao ->
            confirmarConclusao(solicitacao)
        }

        solicitacaoAdapter.itemClickAvaliar = { solicitacao ->

            val intent = Intent(
                this@MinhasSolicitacoesActivity,
                AvaliarProfissionalActivity::class.java
            )

            intent.putExtra("profissional", solicitacao.profissional)
            intent.putExtra("idUsuario", idUsuarioAtual)

            startActivity(intent)
        }
    }

    private fun carregarSolicitacoes() {

        RetrofitClient.apiService.listarSolicitacoes(idUsuarioAtual)
            .enqueue(object : Callback<JsonObject> {

                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {

                    if (response.isSuccessful) {

                        val retorno = response.body()
                            ?.getAsJsonArray("retorno")

                        if (retorno != null) {

                            val tipoLista = object :
                                TypeToken<List<SolicitacaoServico>>() {}.type

                            val solicitacoes: List<SolicitacaoServico> =
                                Gson().fromJson(retorno, tipoLista)

                            exibirSolicitacoes(solicitacoes)

                        } else {
                            exibirSolicitacoes(emptyList())
                        }

                    } else {
                        exibirSolicitacoes(emptyList())
                    }
                }

                override fun onFailure(
                    call: Call<JsonObject>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@MinhasSolicitacoesActivity,
                        "Erro ao carregar solicitações: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun confirmarConclusao(solicitacao: SolicitacaoServico) {

        RetrofitClient.apiService.confirmarConclusaoSolicitacao(
            solicitacao.idSolicitacao,
            idUsuarioAtual
        ).enqueue(object : Callback<JsonObject> {

            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {

                if (response.isSuccessful) {

                    Toast.makeText(
                        this@MinhasSolicitacoesActivity,
                        "Serviço confirmado com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()

                    carregarSolicitacoes()

                } else {

                    Toast.makeText(
                        this@MinhasSolicitacoesActivity,
                        "Não foi possível confirmar a conclusão.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<JsonObject>,
                t: Throwable
            ) {

                Toast.makeText(
                    this@MinhasSolicitacoesActivity,
                    "Erro ao confirmar serviço: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun exibirSolicitacoes(
        solicitacoes: List<SolicitacaoServico>
    ) {

        solicitacaoAdapter.submitList(solicitacoes)

        if (solicitacoes.isEmpty()) {

            binding.tvNenhumaSolicitacao.visibility = View.VISIBLE
            binding.rvSolicitacoes.visibility = View.GONE

        } else {

            binding.tvNenhumaSolicitacao.visibility = View.GONE
            binding.rvSolicitacoes.visibility = View.VISIBLE
        }
    }
}