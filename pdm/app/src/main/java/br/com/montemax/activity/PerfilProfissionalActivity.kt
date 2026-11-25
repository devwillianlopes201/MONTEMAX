package br.com.montemax.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.montemax.activity.adapter.AvaliacaoAdapter
import br.com.montemax.activity.model.Avaliacao
import br.com.montemax.activity.model.Profissional
import br.com.montemax.api.RetrofitClient
import br.com.montemax.montemax.R
import br.com.montemax.montemax.databinding.ActivityPerfilProfissionalBinding
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class PerfilProfissionalActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPerfilProfissionalBinding.inflate(layoutInflater)
    }

    private val avaliacaoAdapter by lazy {
        AvaliacaoAdapter()
    }

    private var profissional: Profissional? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        profissional =
            intent.getSerializableExtra("profissional") as? Profissional

        if (profissional != null) {
            carregarDados()
        }

        configurarRecyclerView()

        binding.btnWhatsApp.setOnClickListener {
            abrirWhatsApp()
        }

        binding.btnRegistrarServico.setOnClickListener {
            registrarServico()
        }
    }

    override fun onResume() {
        super.onResume()
        carregarAvaliacoes()
    }

    private fun carregarDados() {

        val profissional = profissional ?: return

        binding.tvNome.text = profissional.nome

        binding.tvEmail.text = profissional.email

        binding.tvWhatsapp.text =
            getString(R.string.str_whatsapp) +
                    ": " + profissional.whatsapp

        // Exibe as categorias de atuação.
        binding.tvAreaAtuacao.text =
            getString(R.string.str_area_atuacao) +
                    ": " + profissional.areasAtuacao

        // Exibe a descrição do profissional.
        binding.tvDescricao.text =
            "Descrição: " +
                    (profissional.descricao ?: "Não informada")

        // Exibe os serviços oferecidos.
        binding.tvServicos.text =
            "Serviços oferecidos: " +
                    (profissional.servicos ?: "Não informados")

        binding.tvCidade.text =
            getString(R.string.str_cidade) +
                    ": " + profissional.cidade

        // Recupera o caminho da foto do profissional.
        val foto = profissional.fotoPerfil

        if (!foto.isNullOrEmpty()) {

            // Verifica se o caminho já possui o endereço do servidor.
            val enderecoFoto = if (foto.startsWith("http")) {
                foto
            } else {
                RetrofitClient.instancia
                    .baseUrl()
                    .resolve(foto.trimStart('/'))
                    ?.toString()
            }

            // Carrega a foto do servidor usando Glide.
            Glide.with(this@PerfilProfissionalActivity)
                .load(enderecoFoto)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.sivFotoPerfil)

        } else {

            // Apresenta a imagem padrão quando não existe foto.
            binding.sivFotoPerfil.setImageResource(
                R.drawable.ic_launcher_foreground
            )
        }
    }

    private fun configurarRecyclerView() {

        binding.rvAvaliacoes.apply {
            layoutManager = LinearLayoutManager(
                this@PerfilProfissionalActivity
            )

            adapter = avaliacaoAdapter

            isNestedScrollingEnabled = false
        }
    }

    private fun carregarAvaliacoes() {

        val profissional = profissional ?: return

        // Consulta as avaliações do profissional na API.
        RetrofitClient.apiService.listarAvaliacoes(
            profissional.idProfissional
        ).enqueue(object : Callback<JsonObject> {

            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {

                if (response.isSuccessful) {

                    // Recupera a lista de avaliações retornada pelo PHP.
                    val jsonArray = response.body()
                        ?.getAsJsonArray("retorno")

                    // Converte o JSON em uma lista de avaliações.
                    val tipoLista = object : TypeToken<List<Avaliacao>>() {}.type

                    val avaliacoes: List<Avaliacao> =
                        if (jsonArray != null) {
                            Gson().fromJson(jsonArray, tipoLista)
                        } else {
                            emptyList()
                        }

                    // Atualiza a lista e a média das avaliações.
                    atualizarAvaliacoes(avaliacoes)

                    android.util.Log.d(
                        "AVALIACOES",
                        "Quantidade recuperada: ${avaliacoes.size}"
                    )

                } else {

                    Toast.makeText(
                        this@PerfilProfissionalActivity,
                        "Não foi possível carregar as avaliações.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<JsonObject>,
                t: Throwable
            ) {

                android.util.Log.e(
                    "AVALIACOES",
                    "Erro ao consultar avaliações: ${t.message}"
                )

                Toast.makeText(
                    this@PerfilProfissionalActivity,
                    "Erro de conexão ao carregar avaliações.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun atualizarAvaliacoes(avaliacoes: List<Avaliacao>) {

        // Envia a lista para o adapter.
        avaliacaoAdapter.submitList(avaliacoes.toList())

        if (avaliacoes.isEmpty()) {

            binding.tvMediaAvaliacoes.text = "0,0"

            binding.ratingMedia.rating = 0f

            binding.tvQuantidadeAvaliacoes.text =
                getString(R.string.str_nenhuma_avaliacao)

        } else {

            // Soma todas as notas recebidas.
            val somaNotas = avaliacoes.sumOf {
                it.nota
            }

            // Calcula a média das avaliações.
            val media = somaNotas.toFloat() /
                    avaliacoes.size

            binding.tvMediaAvaliacoes.text =
                String.format(
                    Locale.getDefault(),
                    "%.1f",
                    media
                )

            binding.ratingMedia.rating = media

            binding.tvQuantidadeAvaliacoes.text =
                getString(
                    R.string.str_quantidade_avaliacoes,
                    avaliacoes.size
                )
        }
    }

    private fun abrirWhatsApp() {

        val profissional = profissional ?: return

        val numero = profissional.whatsapp.filter {
            it.isDigit()
        }

        if (numero.isEmpty()) {

            Toast.makeText(
                this@PerfilProfissionalActivity,
                getString(R.string.str_whatsapp_indisponivel),
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val numeroCompleto =
            if (numero.startsWith("55")) {
                numero
            } else {
                "55$numero"
            }

        val uri = Uri.parse(
            "https://wa.me/$numeroCompleto"
        )

        val intent = Intent(
            Intent.ACTION_VIEW,
            uri
        )

        try {

            startActivity(intent)

        } catch (e: Exception) {

            Toast.makeText(
                this@PerfilProfissionalActivity,
                getString(R.string.str_whatsapp_indisponivel),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun registrarServico() {

        val profissional = profissional ?: return

        val intent = Intent(
            this@PerfilProfissionalActivity,
            RegistrarServicoActivity::class.java
        )

        intent.putExtra(
            "profissional",
            profissional
        )

        startActivity(intent)
    }
}