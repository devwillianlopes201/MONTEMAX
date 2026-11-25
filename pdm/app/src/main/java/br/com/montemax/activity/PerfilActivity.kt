package br.com.montemax.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.com.montemax.montemax.R
import br.com.montemax.montemax.databinding.ActivityPerfilBinding
import br.com.montemax.activity.model.ComentarioRepositorio
import br.com.montemax.activity.model.PostagemRepositorio
import br.com.montemax.api.RetrofitClient
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Activity responsável por apresentar e controlar a tela de perfil.
class PerfilActivity : AppCompatActivity() {

    // Endereço do servidor utilizado pela API PHP.
    private val baseUrl = "http://192.168.0.73/montemax/"

    // Faz a ligação entre os componentes do XML e o Kotlin.
    private val binding by lazy {
        ActivityPerfilBinding.inflate(layoutInflater)
    }

    // Armazena o ID do usuário conectado.
    private var idUsuario = 0

    // 0 = Cliente e 1 = Profissional.
    private var tipoUsuario = 0

    // Armazena temporariamente as informações do perfil.
    private var nome = ""
    private var email = ""
    private var whatsapp = ""
    private var cidade = ""
    private var descricao = ""
    private var fotoPerfil: String? = null
    private var areasAtuacao = ""
    private var servicos = ""

    // Abre a galeria para selecionar uma nova foto de perfil.
    private val selecionarFotoPerfil =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->

            if (uri != null) {

                try {

                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

                    confirmarNovaFoto(uri)

                } catch (e: SecurityException) {

                    Toast.makeText(
                        this@PerfilActivity,
                        "Não foi possível acessar a imagem selecionada.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        // Recupera os dados da conta conectada.
        carregarDados()

        // Configura a tela conforme o tipo de usuário.
        configurarPerfil()

        // Evento de toque na foto de perfil.
        binding.sivFotoPerfil.setOnClickListener {
            selecionarFotoPerfil.launch(arrayOf("image/*"))
        }

        // Evento de clique do botão Editar Perfil.
        binding.btnEditarPerfil.setOnClickListener {

            val intent = Intent(
                this@PerfilActivity,
                EditarPerfilActivity::class.java
            )

            intent.putExtra("idUsuario", idUsuario)
            intent.putExtra("tipoUsuario", tipoUsuario)
            intent.putExtra("nome", nome)
            intent.putExtra("email", email)
            intent.putExtra("whatsapp", whatsapp)
            intent.putExtra("cidade", cidade)
            intent.putExtra("descricao", descricao)
            intent.putExtra("fotoPerfil", fotoPerfil)
            intent.putExtra("areasAtuacao", areasAtuacao)
            intent.putExtra("servicos", servicos)

            startActivityForResult(intent, 1)
        }

        // Evento de clique do botão Excluir Perfil.
        binding.btnExcluirPerfil.setOnClickListener {
            confirmarExclusao()
        }

        // Evento de clique do botão Sair da Conta.
        binding.btnSairConta.setOnClickListener {
            confirmarSaida()
        }

        // Evento de clique do botão Minhas Publicações.
        binding.btnMinhasPublicacoes.setOnClickListener {

            val intent = Intent(
                this@PerfilActivity,
                MinhasPublicacoesActivity::class.java
            )

            intent.putExtra("email", email)

            startActivity(intent)
        }

        // Evento de clique do botão Minhas Solicitações.
        binding.btnMinhasSolicitacoes.setOnClickListener {

            val intent = Intent(
                this@PerfilActivity,
                MinhasSolicitacoesActivity::class.java
            )

            intent.putExtra("email", email)

            startActivity(intent)
        }
    }

    // Método executado quando a tela volta a ficar visível.
    override fun onResume() {
        super.onResume()

        carregarDados()
        configurarPerfil()
    }

    // Apresenta a confirmação da nova foto.
    private fun confirmarNovaFoto(uri: Uri) {

        val imagem = ImageView(this@PerfilActivity)

        imagem.setImageURI(uri)

        imagem.adjustViewBounds = true
        imagem.scaleType = ImageView.ScaleType.CENTER_CROP

        val tamanho = (220 * resources.displayMetrics.density).toInt()

        imagem.layoutParams = android.view.ViewGroup.LayoutParams(
            tamanho,
            tamanho
        )

        AlertDialog.Builder(this@PerfilActivity)
            .setTitle("Confirmar foto de perfil")
            .setMessage("Deseja utilizar esta imagem como sua nova foto de perfil?")
            .setView(imagem)
            .setPositiveButton("Confirmar") { _, _ ->
                atualizarFotoPerfil(uri)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Envia a nova foto para a API PHP.
    private fun atualizarFotoPerfil(uri: Uri) {

        if (idUsuario <= 0) {

            Toast.makeText(
                this@PerfilActivity,
                "Não foi possível identificar a conta.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        try {

            // Recupera o conteúdo da imagem selecionada.
            val arquivo = contentResolver.openInputStream(uri)?.use {
                it.readBytes()
            }

            if (arquivo == null || arquivo.isEmpty()) {

                Toast.makeText(
                    this@PerfilActivity,
                    "Não foi possível ler a imagem selecionada.",
                    Toast.LENGTH_SHORT
                ).show()

                return
            }

            // Recupera o nome do arquivo selecionado.
            var nomeArquivo = "foto_perfil.jpg"

            contentResolver.query(
                uri,
                null,
                null,
                null,
                null
            )?.use { cursor ->

                val coluna = cursor.getColumnIndex(
                    OpenableColumns.DISPLAY_NAME
                )

                if (cursor.moveToFirst() && coluna >= 0) {
                    nomeArquivo = cursor.getString(coluna)
                }
            }

            // Identifica o tipo de arquivo da imagem.
            val tipoImagem = contentResolver.getType(uri)
                ?: "image/jpeg"

            // Prepara a imagem para o envio multipart.
            val corpoImagem = arquivo.toRequestBody(
                tipoImagem.toMediaType()
            )

            val parteFoto = MultipartBody.Part.createFormData(
                "foto_perfil",
                nomeArquivo,
                corpoImagem
            )

            // Prepara os dados de texto para a API.
            val tipoTexto = "text/plain".toMediaType()

            val corpoId = idUsuario.toString()
                .toRequestBody(tipoTexto)

            val corpoNome = nome.toRequestBody(tipoTexto)

            val corpoEmail = email.toRequestBody(tipoTexto)

            // Prepara os dados específicos do profissional.
            val corpoWhatsapp = if (tipoUsuario == 1) {
                whatsapp.toRequestBody(tipoTexto)
            } else {
                null
            }

            val corpoCidade = if (tipoUsuario == 1) {
                cidade.toRequestBody(tipoTexto)
            } else {
                null
            }

            val corpoDescricao = if (tipoUsuario == 1) {
                descricao.toRequestBody(tipoTexto)
            } else {
                null
            }

            val corpoCategorias = if (tipoUsuario == 1) {
                areasAtuacao.toRequestBody(tipoTexto)
            } else {
                null
            }

            val corpoServicos = if (tipoUsuario == 1) {
                servicos.toRequestBody(tipoTexto)
            } else {
                null
            }

            // Impede novos cliques durante o envio.
            binding.sivFotoPerfil.isEnabled = false

            Toast.makeText(
                this@PerfilActivity,
                "Enviando nova foto...",
                Toast.LENGTH_SHORT
            ).show()

            // Envia os dados e a imagem para a API.
            RetrofitClient.apiService.editarPerfil(
                corpoId,
                corpoNome,
                corpoEmail,
                corpoWhatsapp,
                corpoCidade,
                corpoDescricao,
                corpoCategorias,
                corpoServicos,
                parteFoto
            ).enqueue(object : Callback<JsonObject> {

                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {

                    binding.sivFotoPerfil.isEnabled = true

                    if (response.isSuccessful) {

                        // Recupera o objeto retorno enviado pelo PHP.
                        val retorno = response.body()
                            ?.getAsJsonObject("retorno")

                        // Recupera o caminho da foto salvo no servidor.
                        val novaFoto = retorno
                            ?.get("fotoPerfil")
                            ?.asString

                        if (!novaFoto.isNullOrEmpty()) {

                            val preferencias = getSharedPreferences(
                                "MonteMax",
                                MODE_PRIVATE
                            )

                            val editor = preferencias.edit()

                            editor.putString("fotoPerfil", novaFoto)
                            editor.putString(
                                "conta_${email}_fotoPerfil",
                                novaFoto
                            )

                            val isOk = editor.commit()

                            if (isOk) {

                                fotoPerfil = novaFoto

                                carregarFotoPerfil()

                                Toast.makeText(
                                    this@PerfilActivity,
                                    "Foto de perfil atualizada com sucesso!",
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {

                                Toast.makeText(
                                    this@PerfilActivity,
                                    "A foto foi enviada, mas não foi possível atualizar os dados locais.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        } else {

                            Toast.makeText(
                                this@PerfilActivity,
                                "O servidor não retornou o caminho da foto.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    } else {

                        val mensagem = try {
                            JSONObject(
                                response.errorBody()?.string() ?: ""
                            ).optString(
                                "erro",
                                "Não foi possível atualizar a foto."
                            )
                        } catch (e: Exception) {
                            "Não foi possível atualizar a foto."
                        }

                        Toast.makeText(
                            this@PerfilActivity,
                            mensagem,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<JsonObject>,
                    t: Throwable
                ) {

                    binding.sivFotoPerfil.isEnabled = true

                    Toast.makeText(
                        this@PerfilActivity,
                        "Erro de conexão com o servidor.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

        } catch (e: Exception) {

            binding.sivFotoPerfil.isEnabled = true

            Toast.makeText(
                this@PerfilActivity,
                "Não foi possível preparar a imagem para envio.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Carrega os dados do perfil.
    private fun carregarDados() {

        val preferencias = getSharedPreferences(
            "MonteMax",
            MODE_PRIVATE
        )

        idUsuario = preferencias.getInt("idUsuario", 0)

        email = (preferencias.getString("email", "") ?: "")
            .trim()
            .lowercase()

        // Monta as chaves individuais da conta.
        val chaveNome = "conta_${email}_nome"
        val chaveTipo = "conta_${email}_tipoUsuario"
        val chaveWhatsapp = "conta_${email}_whatsapp"
        val chaveCidade = "conta_${email}_cidade"
        val chaveDescricao = "conta_${email}_descricao"
        val chaveFoto = "conta_${email}_fotoPerfil"
        val chaveAreas = "conta_${email}_areasAtuacao"
        val chaveServicos = "conta_${email}_servicos"

        // Recupera os dados específicos da conta.
        nome = preferencias.getString(chaveNome, "") ?: ""

        tipoUsuario = preferencias.getInt(
            chaveTipo,
            preferencias.getInt("tipoUsuario", 0)
        )

        whatsapp = preferencias.getString(
            chaveWhatsapp,
            ""
        ) ?: ""

        cidade = preferencias.getString(
            chaveCidade,
            ""
        ) ?: ""

        descricao = preferencias.getString(
            chaveDescricao,
            preferencias.getString("descricao", "")
        ) ?: ""

        fotoPerfil = preferencias.getString(
            chaveFoto,
            preferencias.getString("fotoPerfil", null)
        )

        areasAtuacao = preferencias.getString(
            chaveAreas,
            ""
        ) ?: ""

        servicos = preferencias.getString(
            chaveServicos,
            preferencias.getString("servicos", "")
        ) ?: ""

        // Apresenta o nome e o e-mail na tela.
        binding.tvNome.text = nome
        binding.tvEmail.text = email

        carregarFotoPerfil()

        // Apresenta as informações específicas do profissional.
        if (tipoUsuario == 1) {

            binding.tvWhatsapp.text =
                "WhatsApp: $whatsapp"

            binding.tvDescricao.text =
                "Áreas de atuação: " +
                        areasAtuacao.ifEmpty { "Não informadas" }

            binding.tvDescricaoProfissional.text =
                "Descrição: " +
                        descricao.ifEmpty { "Não informada" }

            binding.tvServicos.text =
                "Serviços oferecidos: " +
                        servicos.ifEmpty { "Não informados" }

            binding.tvCidade.text =
                "Cidade: $cidade"
        }
    }

    // Carrega a imagem do perfil.
    private fun carregarFotoPerfil() {

        if (!fotoPerfil.isNullOrEmpty()) {

            val enderecoFoto = when {
                fotoPerfil!!.startsWith("http") -> fotoPerfil
                fotoPerfil!!.startsWith("content://") -> fotoPerfil
                fotoPerfil!!.startsWith("file://") -> fotoPerfil
                else -> baseUrl + fotoPerfil!!.trimStart('/')
            }

            Glide.with(this@PerfilActivity)
                .load(enderecoFoto)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.sivFotoPerfil)

        } else {

            binding.sivFotoPerfil.setImageResource(
                R.drawable.ic_launcher_foreground
            )
        }
    }

    // Configura a tela conforme o tipo de usuário.
    private fun configurarPerfil() {

        if (tipoUsuario == 0) {

            binding.tvLocalizacao.visibility = View.GONE

            binding.btnMinhasPublicacoes.visibility = View.VISIBLE
            binding.btnMinhasSolicitacoes.visibility = View.VISIBLE

            binding.tvWhatsapp.visibility = View.GONE
            binding.tvDescricao.visibility = View.GONE
            binding.tvDescricaoProfissional.visibility = View.GONE
            binding.tvServicos.visibility = View.GONE
            binding.tvCidade.visibility = View.GONE

        } else {

            binding.tvLocalizacao.visibility = View.GONE

            binding.btnMinhasPublicacoes.visibility = View.GONE
            binding.btnMinhasSolicitacoes.visibility = View.GONE

            binding.tvWhatsapp.visibility = View.VISIBLE
            binding.tvDescricao.visibility = View.VISIBLE
            binding.tvDescricaoProfissional.visibility = View.VISIBLE
            binding.tvServicos.visibility = View.VISIBLE
            binding.tvCidade.visibility = View.VISIBLE
        }
    }

    // Recebe o resultado da tela de edição do perfil.
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            carregarDados()
            configurarPerfil()
        }
    }

    // Apresenta a confirmação de exclusão.
    private fun confirmarExclusao() {

        AlertDialog.Builder(this@PerfilActivity)
            .setTitle(getString(R.string.str_excluir_conta_titulo))
            .setMessage(getString(R.string.str_excluir_conta_mensagem))
            .setPositiveButton(getString(R.string.str_excluir)) { _, _ ->
                solicitarSenhaExclusao()
            }
            .setNegativeButton(getString(R.string.str_cancelar), null)
            .show()
    }

    // Solicita a senha antes de excluir definitivamente a conta no servidor.
    private fun solicitarSenhaExclusao() {

        val campoSenha = android.widget.EditText(this@PerfilActivity)

        campoSenha.hint = "Digite sua senha"
        campoSenha.inputType =
            android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        val margem = (24 * resources.displayMetrics.density).toInt()
        campoSenha.setPadding(margem, margem, margem, margem)

        AlertDialog.Builder(this@PerfilActivity)
            .setTitle("Confirmar senha")
            .setMessage("Para excluir sua conta permanentemente, informe sua senha.")
            .setView(campoSenha)
            .setPositiveButton("Confirmar") { _, _ ->

                val senha = campoSenha.text.toString()

                if (senha.isBlank()) {

                    Toast.makeText(
                        this@PerfilActivity,
                        "Informe sua senha para continuar.",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    excluirConta(senha)
                }
            }
            .setNegativeButton(getString(R.string.str_cancelar), null)
            .show()
    }

    // Envia a solicitação de exclusão definitiva para a API PHP.
    private fun excluirConta(senha: String) {

        if (idUsuario <= 0) {

            Toast.makeText(
                this@PerfilActivity,
                "Não foi possível identificar a conta.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        binding.btnExcluirPerfil.isEnabled = false

        RetrofitClient.apiService.excluirConta(
            idUsuario,
            senha
        ).enqueue(object : Callback<JsonObject> {

            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {

                binding.btnExcluirPerfil.isEnabled = true

                if (response.isSuccessful) {

                    // O servidor confirmou a exclusão da conta.
                    val preferencias = getSharedPreferences(
                        "MonteMax",
                        MODE_PRIVATE
                    )

                    val editor = preferencias.edit()

                    // Remove os dados individuais da conta.
                    editor.remove("conta_${email}_idUsuario")
                    editor.remove("conta_${email}_nome")
                    editor.remove("conta_${email}_email")
                    editor.remove("conta_${email}_senha")
                    editor.remove("conta_${email}_tipoUsuario")
                    editor.remove("conta_${email}_whatsapp")
                    editor.remove("conta_${email}_areasAtuacao")
                    editor.remove("conta_${email}_cidade")
                    editor.remove("conta_${email}_descricao")
                    editor.remove("conta_${email}_servicos")
                    editor.remove("conta_${email}_fotoPerfil")

                    // Remove os dados gerais da sessão atual.
                    editor.remove("idUsuario")
                    editor.remove("nome")
                    editor.remove("email")
                    editor.remove("tipoUsuario")
                    editor.remove("whatsapp")
                    editor.remove("areasAtuacao")
                    editor.remove("cidade")
                    editor.remove("descricao")
                    editor.remove("servicos")
                    editor.remove("fotoPerfil")

                    editor.commit()

                    Toast.makeText(
                        this@PerfilActivity,
                        "Sua conta foi excluída com sucesso.",
                        Toast.LENGTH_LONG
                    ).show()

                    // Retorna à tela de login e encerra as telas anteriores.
                    val intent = Intent(
                        this@PerfilActivity,
                        MainActivity::class.java
                    )

                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)
                    finish()

                } else {

                    val mensagem = try {
                        JSONObject(
                            response.errorBody()?.string() ?: ""
                        ).optString(
                            "erro",
                            "Não foi possível excluir a conta."
                        )
                    } catch (e: Exception) {
                        "Não foi possível excluir a conta."
                    }

                    Toast.makeText(
                        this@PerfilActivity,
                        mensagem,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<JsonObject>,
                t: Throwable
            ) {

                binding.btnExcluirPerfil.isEnabled = true

                Toast.makeText(
                    this@PerfilActivity,
                    "Erro de conexão com o servidor.",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    // Confirma a saída da conta.
    private fun confirmarSaida() {

        AlertDialog.Builder(this@PerfilActivity)
            .setTitle("Sair da conta")
            .setMessage("Deseja realmente sair da sua conta?")
            .setPositiveButton("Sim") { _, _ ->
                sairConta()
            }
            .setNegativeButton(
                getString(R.string.str_cancelar),
                null
            )
            .show()
    }

    // Encerra a sessão atual.
    private fun sairConta() {

        val preferencias = getSharedPreferences(
            "MonteMax",
            MODE_PRIVATE
        )

        val editor = preferencias.edit()

        editor.remove("idUsuario")
        editor.remove("nome")
        editor.remove("email")
        editor.remove("tipoUsuario")
        editor.remove("whatsapp")
        editor.remove("areasAtuacao")
        editor.remove("cidade")
        editor.remove("descricao")
        editor.remove("servicos")
        editor.remove("fotoPerfil")

        val isOk = editor.commit()

        if (isOk) {

            Toast.makeText(
                this@PerfilActivity,
                "Você saiu da sua conta.",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(
                this@PerfilActivity,
                MainActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            finish()

        } else {

            Toast.makeText(
                this@PerfilActivity,
                "Não foi possível sair da conta.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
