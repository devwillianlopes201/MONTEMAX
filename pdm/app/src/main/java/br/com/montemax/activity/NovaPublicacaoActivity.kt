package br.com.montemax.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.montemax.activity.adapter.FotoPublicacaoAdapter
import br.com.montemax.activity.model.FotoPublicacao
import br.com.montemax.api.RetrofitClient
import br.com.montemax.montemax.R
import br.com.montemax.montemax.databinding.ActivityNovaPublicacaoBinding
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Activity responsável pela criação de novas publicações.
class NovaPublicacaoActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityNovaPublicacaoBinding.inflate(layoutInflater)
    }

    // Armazena os endereços das fotos selecionadas.
    private val fotosSelecionadas = mutableListOf<String>()

    // Adapter responsável pela pré-visualização das fotos.
    private lateinit var fotoPublicacaoAdapter: FotoPublicacaoAdapter

    // Abre o seletor para escolher várias imagens.
    private val selecionarFotos =
        registerForActivityResult(
            ActivityResultContracts.OpenMultipleDocuments()
        ) { uris ->

            if (uris.isNotEmpty()) {

                // Limpa a seleção anterior.
                fotosSelecionadas.clear()

                // Verifica a quantidade de fotos selecionadas.
                if (uris.size > 5) {
                    Toast.makeText(
                        this,
                        "Selecione no máximo 5 fotos.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                // Percorre as imagens selecionadas.
                uris.take(5).forEach { uri ->

                    try {
                        // Mantém a permissão de leitura da imagem.
                        contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )

                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }

                    // Armazena o endereço da imagem.
                    fotosSelecionadas.add(uri.toString())
                }

                // Atualiza a pré-visualização.
                atualizarFotosSelecionadas()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        configurarListaFotos()

        // Evento para selecionar várias fotos.
        binding.btnSelecionarFoto.setOnClickListener {
            selecionarFotos.launch(arrayOf("image/*"))
        }

        // Evento para publicar.
        binding.btnPublicar.setOnClickListener {
            publicar()
        }
    }

    // Configura a lista de fotos selecionadas.
    private fun configurarListaFotos() {

        fotoPublicacaoAdapter = FotoPublicacaoAdapter()

        binding.rvFotosSelecionadas.apply {
            layoutManager = LinearLayoutManager(this@NovaPublicacaoActivity)
            adapter = fotoPublicacaoAdapter
            isNestedScrollingEnabled = false
        }
    }

    // Atualiza a pré-visualização das imagens.
    private fun atualizarFotosSelecionadas() {

        val fotos = fotosSelecionadas.mapIndexed { indice, imagemUrl ->

            FotoPublicacao(
                idFoto = indice + 1,
                idPublicacao = 0,
                imagemUrl = imagemUrl,
                descricao = null
            )
        }

        fotoPublicacaoAdapter.submitList(fotos)
    }

    // Método responsável por validar e enviar a publicação para a API.
    private fun publicar() {

        // Recupera os dados informados pelo usuário.
        val titulo = binding.tieTitulo.text.toString().trim()

        val conteudo = binding.tieConteudo.text.toString().trim()

        val descricao = binding.tieDescricaoFoto.text.toString().trim()

        // Controla se os campos obrigatórios foram preenchidos.
        var isValido = true

        // Verifica se o título está vazio.
        if (titulo.isEmpty()) {

            binding.tilTitulo.error =
                getString(R.string.str_titulo_obrigatorio)

            isValido = false

        } else {

            binding.tilTitulo.error = null
        }

        // Verifica se o conteúdo está vazio.
        if (conteudo.isEmpty()) {

            binding.tilConteudo.error =
                getString(R.string.str_conteudo_obrigatorio)

            isValido = false

        } else {

            binding.tilConteudo.error = null
        }

        // Continua somente se os campos estiverem preenchidos.
        if (!isValido) {
            return
        }

        // Recupera o armazenamento local do aplicativo.
        val preferencias = getSharedPreferences(
            "MonteMax",
            MODE_PRIVATE
        )

        // Recupera o ID da conta atualmente conectada.
        val idUsuario = preferencias.getInt(
            "idUsuario",
            0
        )

        // Verifica se existe uma conta identificada.
        if (idUsuario <= 0) {

            Toast.makeText(
                this,
                "Não foi possível identificar o usuário conectado. Faça login novamente.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        // Evita que o usuário envie várias requisições.
        binding.btnPublicar.isEnabled = false

        // Envia os dados da publicação para a API PHP.
        RetrofitClient.apiService.registrarPublicacao(
            idUsuario,
            titulo,
            conteudo
        ).enqueue(object : Callback<JsonObject> {

            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {

                if (response.isSuccessful) {

                    // Recupera os dados retornados pelo PHP.
                    val retorno = response.body()
                        ?.getAsJsonObject("retorno")

                    if (retorno == null) {

                        binding.btnPublicar.isEnabled = true

                        Toast.makeText(
                            this@NovaPublicacaoActivity,
                            "Resposta inválida do servidor.",
                            Toast.LENGTH_SHORT
                        ).show()

                        return
                    }

                    // Recupera o ID da publicação cadastrada.
                    val idPublicacao = retorno
                        .get("idPublicacao")
                        ?.asInt ?: 0

                    if (idPublicacao <= 0) {

                        binding.btnPublicar.isEnabled = true

                        Toast.makeText(
                            this@NovaPublicacaoActivity,
                            "Não foi possível identificar a publicação.",
                            Toast.LENGTH_SHORT
                        ).show()

                        return
                    }

                    // Verifica se existem fotos selecionadas.
                    if (fotosSelecionadas.isEmpty()) {

                        Toast.makeText(
                            this@NovaPublicacaoActivity,
                            getString(R.string.str_publicacao_sucesso),
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    } else {

                        // Envia as fotos e a descrição.
                        enviarFotos(idPublicacao, descricao)
                    }

                } else {

                    binding.btnPublicar.isEnabled = true

                    // Recupera a mensagem de erro enviada pelo PHP.
                    val mensagemErro = try {

                        val jsonErro = response.errorBody()
                            ?.string()

                        JSONObject(jsonErro ?: "{}")
                            .optString(
                                "erro",
                                "Não foi possível realizar a publicação."
                            )

                    } catch (e: Exception) {

                        "Não foi possível realizar a publicação."
                    }

                    Toast.makeText(
                        this@NovaPublicacaoActivity,
                        mensagemErro,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<JsonObject>,
                t: Throwable
            ) {

                binding.btnPublicar.isEnabled = true

                Toast.makeText(
                    this@NovaPublicacaoActivity,
                    "Erro de conexão com o servidor.",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    // Método responsável por enviar as fotos e a descrição para a API.
    private fun enviarFotos(idPublicacao: Int, descricao: String) {

        try {

            // Prepara a lista de imagens para envio.
            val partesFotos = mutableListOf<MultipartBody.Part>()

            fotosSelecionadas.forEachIndexed { indice, caminho ->

                // Recupera o endereço da imagem.
                val uri = Uri.parse(caminho)

                // Lê os dados da imagem.
                val arquivo = contentResolver.openInputStream(uri)
                    ?.use { it.readBytes() }
                    ?: throw Exception("Não foi possível ler uma das fotos.")

                // Recupera o tipo da imagem.
                val tipoImagem = contentResolver.getType(uri)
                    ?: "application/octet-stream"

                // Cria o corpo da imagem.
                val requestBody = arquivo.toRequestBody(
                    tipoImagem.toMediaType()
                )

                // Cria a parte multipart da imagem.
                val parte = MultipartBody.Part.createFormData(
                    "fotos[]",
                    "foto_${indice + 1}",
                    requestBody
                )

                partesFotos.add(parte)
            }

            // Prepara o ID da publicação.
            val idRequestBody = idPublicacao.toString()
                .toRequestBody("text/plain".toMediaType())

            // Prepara a descrição das fotos.
            val descricaoRequestBody = descricao
                .toRequestBody("text/plain".toMediaType())

            // Envia as fotos e a descrição para o PHP.
            RetrofitClient.apiService.registrarFotosPublicacao(
                idRequestBody,
                descricaoRequestBody,
                partesFotos
            ).enqueue(object : Callback<JsonObject> {

                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@NovaPublicacaoActivity,
                            getString(R.string.str_publicacao_sucesso),
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        Toast.makeText(
                            this@NovaPublicacaoActivity,
                            "Publicação criada, mas não foi possível enviar as fotos.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    // Finaliza a tela após o processamento.
                    finish()
                }

                override fun onFailure(
                    call: Call<JsonObject>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@NovaPublicacaoActivity,
                        "Publicação criada, mas ocorreu um erro ao enviar as fotos.",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                }
            })

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Publicação criada, mas não foi possível preparar as fotos.",
                Toast.LENGTH_LONG
            ).show()

            finish()
        }
    }
}