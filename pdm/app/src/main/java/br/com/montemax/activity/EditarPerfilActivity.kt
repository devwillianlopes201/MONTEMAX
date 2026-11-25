package br.com.montemax.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.com.montemax.api.RetrofitClient
import br.com.montemax.montemax.R
import br.com.montemax.montemax.databinding.ActivityEditarPerfilBinding
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Activity responsável por permitir a edição dos dados do perfil.
class EditarPerfilActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityEditarPerfilBinding.inflate(layoutInflater)
    }

    private var tipoUsuario = 0
    private var idUsuario = 0

    private var fotoPerfil: String? = null

    // Guarda a foto escolhida para enviar ao servidor.
    private var fotoSelecionada: Uri? = null

    // Guarda o e-mail original da conta.
    private var emailOriginal = ""

    // Registra a abertura do seletor de imagens.
    private val selecionarFoto =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri != null) {

                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }

                fotoSelecionada = uri

                // Exibe a imagem escolhida no aparelho.
                binding.sivFotoPerfil.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        tipoUsuario = intent.getIntExtra("tipoUsuario", 0)
        idUsuario = intent.getIntExtra("idUsuario", 0)

        emailOriginal = (
                intent.getStringExtra("email") ?: ""
                ).trim().lowercase()

        carregarDados()

        configurarTela()

        // Abre o seletor de imagens.
        binding.btnSelecionarFoto.setOnClickListener {
            selecionarFoto.launch(arrayOf("image/*"))
        }

        // Salva as alterações do perfil.
        binding.btnSalvarAlteracoes.setOnClickListener {
            salvarAlteracoes()
        }
    }

    // Recupera os dados enviados pela tela de perfil.
    private fun carregarDados() {

        binding.tieNome.setText(
            intent.getStringExtra("nome")
        )

        binding.tieEmail.setText(
            intent.getStringExtra("email")
        )

        binding.tieWhatsapp.setText(
            intent.getStringExtra("whatsapp")
        )

        binding.tieCidade.setText(
            intent.getStringExtra("cidade")
        )

        binding.tieDescricao.setText(
            intent.getStringExtra("descricao")
        )

        fotoPerfil = intent.getStringExtra("fotoPerfil")

        val areasAtuacao =
            intent.getStringExtra("areasAtuacao") ?: ""

        val servicos =
            intent.getStringExtra("servicos") ?: ""

        // Recupera as categorias selecionadas.
        if (areasAtuacao.contains("Quarto")) {
            binding.cbQuarto.isChecked = true
        }

        if (areasAtuacao.contains("Cozinha")) {
            binding.cbCozinha.isChecked = true
        }

        // Recupera os serviços selecionados.
        if (servicos.contains("Montagem de móveis")) {
            binding.cbMontagem.isChecked = true
        }

        if (servicos.contains("Desmontagem de móveis")) {
            binding.cbDesmontagem.isChecked = true
        }

        if (servicos.contains("Transporte de móveis")) {
            binding.cbTransporte.isChecked = true
        }

        // Exibe a foto atual salva no servidor.
        if (!fotoPerfil.isNullOrEmpty()) {

            // Verifica se o endereço da foto já é completo.
            val enderecoFoto = if (fotoPerfil!!.startsWith("http")) {
                fotoPerfil!!
            } else {
                RetrofitClient.instancia
                    .baseUrl()
                    .resolve(fotoPerfil!!.trimStart('/'))
                    ?.toString()
            }

            // Carrega a foto do servidor usando Glide.
            Glide.with(this)
                .load(enderecoFoto)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.sivFotoPerfil)
        }
    }

    // Mostra ou esconde os campos conforme o tipo de usuário.
    private fun configurarTela() {

        if (tipoUsuario == 0) {

            // Cliente não visualiza os dados profissionais.
            binding.layoutDadosProfissional.visibility = View.GONE

        } else {

            // Profissional visualiza todos os campos.
            binding.layoutDadosProfissional.visibility = View.VISIBLE
        }
    }

    // Valida os campos e envia as alterações para a API.
    private fun salvarAlteracoes() {

        val nome = binding.tieNome.text.toString().trim()

        val email = binding.tieEmail.text.toString()
            .trim()
            .lowercase()

        val whatsapp = binding.tieWhatsapp.text.toString().trim()

        val cidade = binding.tieCidade.text.toString().trim()

        val descricao = binding.tieDescricao.text.toString().trim()

        var isValido = true

        // Validação do nome.
        if (nome.isEmpty()) {

            binding.tilNome.error =
                getString(R.string.str_nome_obrigatorio)

            isValido = false

        } else {

            binding.tilNome.error = null
        }

        // Validação do e-mail.
        if (email.isEmpty()) {

            binding.tilEmail.error =
                getString(R.string.str_email_obrigatorio)

            isValido = false

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            binding.tilEmail.error = "Digite um e-mail válido."

            isValido = false

        } else {

            binding.tilEmail.error = null
        }

        var areasAtuacao = ""
        var servicosOferecidos = ""

        // Valida os campos específicos do profissional.
        if (tipoUsuario == 1) {

            if (whatsapp.isEmpty()) {

                binding.tilWhatsapp.error =
                    getString(R.string.str_whatsapp_obrigatorio)

                isValido = false

            } else {

                binding.tilWhatsapp.error = null
            }

            if (!binding.cbQuarto.isChecked &&
                !binding.cbCozinha.isChecked
            ) {

                Toast.makeText(
                    this,
                    getString(R.string.str_areas_atuacao_obrigatorio),
                    Toast.LENGTH_SHORT
                ).show()

                isValido = false
            }

            if (binding.cbQuarto.isChecked) {
                areasAtuacao += "Quarto"
            }

            if (binding.cbCozinha.isChecked) {

                if (areasAtuacao.isNotEmpty()) {
                    areasAtuacao += ", "
                }

                areasAtuacao += "Cozinha"
            }

            if (cidade.isEmpty()) {

                binding.tilCidade.error =
                    getString(R.string.str_cidade_obrigatorio)

                isValido = false

            } else {

                binding.tilCidade.error = null
            }

            // Validação da descrição profissional.
            if (descricao.isEmpty()) {

                binding.tilDescricao.error =
                    "Informe uma descrição profissional."

                isValido = false

            } else {

                binding.tilDescricao.error = null
            }

            // Recupera os serviços selecionados.
            if (binding.cbMontagem.isChecked) {
                servicosOferecidos += "Montagem de móveis"
            }

            if (binding.cbDesmontagem.isChecked) {

                if (servicosOferecidos.isNotEmpty()) {
                    servicosOferecidos += ", "
                }

                servicosOferecidos += "Desmontagem de móveis"
            }

            if (binding.cbTransporte.isChecked) {

                if (servicosOferecidos.isNotEmpty()) {
                    servicosOferecidos += ", "
                }

                servicosOferecidos += "Transporte de móveis"
            }

            if (servicosOferecidos.isEmpty()) {

                Toast.makeText(
                    this,
                    "Selecione pelo menos um serviço.",
                    Toast.LENGTH_SHORT
                ).show()

                isValido = false
            }
        }

        // Verifica se o ID e o e-mail da conta foram recuperados.
        if (idUsuario <= 0 || emailOriginal.isEmpty()) {

            Toast.makeText(
                this,
                "Não foi possível identificar a conta.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Recupera o armazenamento local.
        val preferencias = getSharedPreferences(
            "MonteMax",
            MODE_PRIVATE
        )

        // Verifica se o novo e-mail já está cadastrado localmente.
        val novaChaveSenha = "conta_${email}_senha"

        if (email != emailOriginal &&
            preferencias.contains(novaChaveSenha)
        ) {

            binding.tilEmail.error =
                "Este e-mail já está cadastrado."

            isValido = false
        }

        // Continua somente se as validações forem aprovadas.
        if (!isValido) {
            return
        }

        // Define o tipo de conteúdo dos campos enviados.
        val tipoTexto = "text/plain".toMediaType()

        // Prepara os dados para envio à API.
        val idRequest = idUsuario.toString()
            .toRequestBody(tipoTexto)

        val nomeRequest = nome.toRequestBody(tipoTexto)

        val emailRequest = email.toRequestBody(tipoTexto)

        val whatsappRequest = if (tipoUsuario == 1) {
            whatsapp.toRequestBody(tipoTexto)
        } else {
            null
        }

        val cidadeRequest = if (tipoUsuario == 1) {
            cidade.toRequestBody(tipoTexto)
        } else {
            null
        }

        val descricaoRequest = if (tipoUsuario == 1) {
            descricao.toRequestBody(tipoTexto)
        } else {
            null
        }

        val categoriasRequest = if (tipoUsuario == 1) {
            areasAtuacao.toRequestBody(tipoTexto)
        } else {
            null
        }

        val servicosRequest = if (tipoUsuario == 1) {
            servicosOferecidos.toRequestBody(tipoTexto)
        } else {
            null
        }

        // Prepara a foto somente se uma nova imagem foi escolhida.
        val fotoRequest: MultipartBody.Part? = try {

            fotoSelecionada?.let { uri ->

                val bytes = contentResolver
                    .openInputStream(uri)
                    ?.use { it.readBytes() }

                if (bytes == null) {
                    throw Exception("Não foi possível ler a foto.")
                }

                val mimeType = contentResolver.getType(uri)
                    ?: "image/jpeg"

                val nomeArquivo = contentResolver
                    .query(uri, null, null, null, null)
                    ?.use { cursor ->

                        val coluna = cursor.getColumnIndex(
                            OpenableColumns.DISPLAY_NAME
                        )

                        if (coluna >= 0 && cursor.moveToFirst()) {
                            cursor.getString(coluna)
                        } else {
                            null
                        }
                    } ?: "foto_perfil.jpg"

                val corpoFoto = bytes.toRequestBody(
                    mimeType.toMediaType()
                )

                MultipartBody.Part.createFormData(
                    "foto_perfil",
                    nomeArquivo,
                    corpoFoto
                )
            }

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Não foi possível preparar a foto.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Evita que o usuário envie várias requisições.
        binding.btnSalvarAlteracoes.isEnabled = false

        // Envia os dados para o PHP através do Retrofit.
        RetrofitClient.apiService.editarPerfil(
            idRequest,
            nomeRequest,
            emailRequest,
            whatsappRequest,
            cidadeRequest,
            descricaoRequest,
            categoriasRequest,
            servicosRequest,
            fotoRequest
        ).enqueue(object : Callback<JsonObject> {

            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {

                binding.btnSalvarAlteracoes.isEnabled = true

                if (response.isSuccessful) {

                    val retorno = response.body()
                        ?.getAsJsonObject("retorno")

                    if (retorno == null) {

                        Toast.makeText(
                            this@EditarPerfilActivity,
                            "Resposta inválida do servidor.",
                            Toast.LENGTH_SHORT
                        ).show()

                        return
                    }

                    // Recupera os dados confirmados pelo servidor.
                    val nomeAtualizado =
                        retorno.get("nome").asString

                    val emailAtualizado =
                        retorno.get("email").asString

                    val fotoAtualizada =
                        if (retorno.has("fotoPerfil") &&
                            !retorno.get("fotoPerfil").isJsonNull
                        ) {
                            retorno.get("fotoPerfil").asString
                        } else {
                            null
                        }

                    val whatsappAtualizado =
                        if (retorno.has("whatsapp")) {
                            retorno.get("whatsapp").asString
                        } else {
                            whatsapp
                        }

                    val cidadeAtualizada =
                        if (retorno.has("cidade")) {
                            retorno.get("cidade").asString
                        } else {
                            cidade
                        }

                    val descricaoAtualizada =
                        if (retorno.has("descricao")) {
                            retorno.get("descricao").asString
                        } else {
                            descricao
                        }

                    val areasAtualizadas =
                        if (retorno.has("areasAtuacao")) {
                            retorno.get("areasAtuacao").asString
                        } else {
                            areasAtuacao
                        }

                    val servicosAtualizados =
                        if (retorno.has("servicos")) {
                            retorno.get("servicos").asString
                        } else {
                            servicosOferecidos
                        }

                    // Atualiza os dados locais somente após o sucesso da API.
                    val editor = preferencias.edit()

                    val chaveAntiga = "conta_${emailOriginal}_"
                    val chaveNova = "conta_${emailAtualizado}_"

                    // Transfere as chaves se o e-mail foi alterado.
                    if (emailAtualizado != emailOriginal) {

                        val dadosAntigos = preferencias.all

                        for ((chave, valor) in dadosAntigos) {

                            if (chave.startsWith(chaveAntiga)) {

                                val novaChave = chave.replaceFirst(
                                    chaveAntiga,
                                    chaveNova
                                )

                                when (valor) {

                                    is String -> editor.putString(
                                        novaChave,
                                        valor
                                    )

                                    is Int -> editor.putInt(
                                        novaChave,
                                        valor
                                    )

                                    is Boolean -> editor.putBoolean(
                                        novaChave,
                                        valor
                                    )

                                    is Float -> editor.putFloat(
                                        novaChave,
                                        valor
                                    )

                                    is Long -> editor.putLong(
                                        novaChave,
                                        valor
                                    )

                                    is Set<*> -> {
                                        @Suppress("UNCHECKED_CAST")
                                        editor.putStringSet(
                                            novaChave,
                                            valor as Set<String>
                                        )
                                    }
                                }

                                editor.remove(chave)
                            }
                        }
                    }

                    // Atualiza os dados individuais da conta.
                    editor.putString("${chaveNova}nome", nomeAtualizado)
                    editor.putString("${chaveNova}email", emailAtualizado)
                    editor.putInt("${chaveNova}tipoUsuario", tipoUsuario)
                    editor.putString("${chaveNova}fotoPerfil", fotoAtualizada)
                    editor.putString("${chaveNova}whatsapp", whatsappAtualizado)
                    editor.putString("${chaveNova}areasAtuacao", areasAtualizadas)
                    editor.putString("${chaveNova}cidade", cidadeAtualizada)
                    editor.putString("${chaveNova}descricao", descricaoAtualizada)
                    editor.putString("${chaveNova}servicos", servicosAtualizados)

                    // Atualiza os dados da conta atualmente conectada.
                    editor.putString("nome", nomeAtualizado)
                    editor.putString("email", emailAtualizado)
                    editor.putInt("tipoUsuario", tipoUsuario)
                    editor.putString("fotoPerfil", fotoAtualizada)
                    editor.putString("whatsapp", whatsappAtualizado)
                    editor.putString("areasAtuacao", areasAtualizadas)
                    editor.putString("cidade", cidadeAtualizada)
                    editor.putString("descricao", descricaoAtualizada)
                    editor.putString("servicos", servicosAtualizados)

                    // Confirma a gravação local.
                    val gravou = editor.commit()

                    if (!gravou) {

                        Toast.makeText(
                            this@EditarPerfilActivity,
                            "O servidor salvou os dados, mas houve um erro ao atualizar o aplicativo.",
                            Toast.LENGTH_LONG
                        ).show()

                        return
                    }

                    // Devolve os dados atualizados para a tela anterior.
                    val resultado = Intent()

                    resultado.putExtra("idUsuario", idUsuario)
                    resultado.putExtra("nome", nomeAtualizado)
                    resultado.putExtra("email", emailAtualizado)
                    resultado.putExtra("tipoUsuario", tipoUsuario)
                    resultado.putExtra("whatsapp", whatsappAtualizado)
                    resultado.putExtra("cidade", cidadeAtualizada)
                    resultado.putExtra("descricao", descricaoAtualizada)
                    resultado.putExtra("fotoPerfil", fotoAtualizada)
                    resultado.putExtra("areasAtuacao", areasAtualizadas)
                    resultado.putExtra("servicos", servicosAtualizados)

                    setResult(RESULT_OK, resultado)

                    Toast.makeText(
                        this@EditarPerfilActivity,
                        getString(R.string.str_perfil_atualizado),
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } else {

                    // Recupera a mensagem de erro enviada pelo PHP.
                    val mensagemErro = try {

                        val jsonErro = response.errorBody()
                            ?.string()

                        JSONObject(jsonErro ?: "{}")
                            .optString(
                                "erro",
                                "Não foi possível atualizar o perfil."
                            )

                    } catch (e: Exception) {

                        "Não foi possível atualizar o perfil."
                    }

                    Toast.makeText(
                        this@EditarPerfilActivity,
                        mensagemErro,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<JsonObject>,
                t: Throwable
            ) {

                binding.btnSalvarAlteracoes.isEnabled = true

                Toast.makeText(
                    this@EditarPerfilActivity,
                    "Erro de conexão com o servidor.",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}