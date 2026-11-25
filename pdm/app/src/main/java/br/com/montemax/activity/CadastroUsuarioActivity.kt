package br.com.montemax.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.com.montemax.api.RetrofitClient
import br.com.montemax.montemax.R
import br.com.montemax.montemax.databinding.ActivityCadastroUsuarioBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

// Activity responsável pelo cadastro de usuários e profissionais.
// Também implementa a interface que permite detectar seleções no Spinner.
class CadastroUsuarioActivity :
    AppCompatActivity(),
    AdapterView.OnItemSelectedListener {

    // Cria a ligação entre os componentes do XML e o código Kotlin.
    private val binding by lazy {
        ActivityCadastroUsuarioBinding.inflate(layoutInflater)
    }

    // Permite acessar os serviços de localização do dispositivo.
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Armazena a ação que será executada depois da autorização da localização.
    private var acaoAposPermissao: (() -> Unit)? = null

    // Solicita ao usuário a permissão de localização.
    private val solicitarPermissaoLocalizacao =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissoes ->

            val permissaoConcedida =
                permissoes[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissoes[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (permissaoConcedida) {
                // Continua o cadastro após a autorização.
                acaoAposPermissao?.invoke()
            } else {
                acaoAposPermissao = null

                Toast.makeText(
                    this,
                    "A localização é necessária para o cadastro profissional.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    // Armazena o tipo de usuário selecionado no Spinner.
    private var tipoUsuario = 0

    // Armazena o endereço da imagem da frente do RG.
    private var documentoFrenteUri: Uri? = null

    // Armazena o endereço da imagem do verso do RG.
    private var documentoVersoUri: Uri? = null

    // Abre a galeria para selecionar a frente do RG.
    private val selecionarDocumentoFrente =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->

            if (uri != null) {
                try {
                    // Mantém a permissão de leitura da imagem selecionada.
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

                    // Guarda o endereço da imagem.
                    documentoFrenteUri = uri

                    // Exibe a imagem selecionada na tela.
                    binding.ivDocumentoFrente.setImageURI(uri)
                    binding.ivDocumentoFrente.visibility = View.VISIBLE

                    // Informa que a frente do RG foi selecionada.
                    binding.tvDocumentoFrenteSelecionado.text =
                        "Frente do RG selecionada com sucesso."

                    binding.tvDocumentoFrenteSelecionado.visibility =
                        View.VISIBLE

                } catch (e: SecurityException) {
                    documentoFrenteUri = null

                    Toast.makeText(
                        this,
                        "Não foi possível acessar a frente do RG.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    // Abre a galeria para selecionar o verso do RG.
    private val selecionarDocumentoVerso =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->

            if (uri != null) {
                try {
                    // Mantém a permissão de leitura da imagem selecionada.
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

                    // Guarda o endereço da imagem.
                    documentoVersoUri = uri

                    // Exibe a imagem selecionada na tela.
                    binding.ivDocumentoVerso.setImageURI(uri)
                    binding.ivDocumentoVerso.visibility = View.VISIBLE

                    // Informa que o verso do RG foi selecionado.
                    binding.tvDocumentoVersoSelecionado.text =
                        "Verso do RG selecionado com sucesso."

                    binding.tvDocumentoVersoSelecionado.visibility =
                        View.VISIBLE

                } catch (e: SecurityException) {
                    documentoVersoUri = null

                    Toast.makeText(
                        this,
                        "Não foi possível acessar o verso do RG.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        // Configura a máscara automática do WhatsApp.
        configurarMascaraWhatsapp()

        // Inicializa o serviço de localização.
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        // Preenche o Spinner com os tipos de usuários disponíveis.
        popularSpinner()

        // Evento do botão para selecionar a frente do RG.
        binding.btnSelecionarDocumentoFrente.setOnClickListener {
            selecionarDocumentoFrente.launch(arrayOf("image/*"))
        }

        // Evento do botão para selecionar o verso do RG.
        binding.btnSelecionarDocumentoVerso.setOnClickListener {
            selecionarDocumentoVerso.launch(arrayOf("image/*"))
        }

        // Evento de clique do botão Cadastrar.
        binding.btnCadastrar.setOnClickListener {

            // Recupera os dados informados pelo usuário.
            val nome = binding.tieNome.text.toString().trim()
            val email = binding.tieEmail.text.toString().trim()
            val senha = binding.tieSenha.text.toString()
            val confirmaSenha = binding.tieConfirmarSenha.text.toString()
            // Envia somente os números do WhatsApp para a API.
            val whatsapp = binding.tieWhatsapp.text.toString()
                .filter { it.isDigit() }
            val cidade = binding.tieCidade.text.toString().trim()
            val descricao = binding.tieDescricao.text.toString().trim()

            // Controla se todos os campos obrigatórios foram preenchidos.
            var isValido = true

            // Verifica se o nome está vazio.
            if (nome.isEmpty()) {
                binding.tilNome.error =
                    getString(R.string.str_nome_obrigatorio)

                isValido = false
            } else {
                binding.tilNome.error = null
            }

            // Verifica se o e-mail está vazio.
            if (email.isEmpty()) {
                binding.tilEmail.error =
                    getString(R.string.str_email_obrigatorio)

                isValido = false

                // Verifica se o formato do e-mail é válido.
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilEmail.error = "Digite um e-mail válido."

                isValido = false
            } else {
                binding.tilEmail.error = null
            }

            // Verifica se a senha está vazia.
            if (senha.isEmpty()) {
                binding.tilSenha.error =
                    getString(R.string.str_senha_obrigatorio)

                isValido = false
            } else {
                binding.tilSenha.error = null
            }

            // Verifica se a confirmação da senha está vazia.
            if (confirmaSenha.isEmpty()) {
                binding.tilConfirmarSenha.error =
                    getString(R.string.str_confirmar_senha)

                isValido = false

                // Compara a senha com a confirmação.
            } else if (senha != confirmaSenha) {
                binding.tilConfirmarSenha.error =
                    getString(R.string.str_senhas_diferentes)

                isValido = false
            } else {
                binding.tilConfirmarSenha.error = null
            }

            // Cria uma variável para armazenar as áreas de atuação.
            var areasAtuacao = ""

            // Cria uma variável para armazenar os serviços oferecidos.
            var servicosOferecidos = ""

            // Verifica se o cadastro selecionado é de um profissional.
            if (tipoUsuario == 1) {

                // Verifica se o WhatsApp possui DDD e número válidos.
                if (whatsapp.length != 11) {
                    binding.tilWhatsapp.error =
                        "Digite um WhatsApp válido com DDD."

                    isValido = false
                } else {
                    binding.tilWhatsapp.error = null
                }

                // Verifica se a descrição está vazia.
                if (descricao.isEmpty()) {
                    binding.tilDescricao.error =
                        "Informe uma descrição sobre seu trabalho."

                    isValido = false
                } else {
                    binding.tilDescricao.error = null
                }

                // Verifica se pelo menos uma área foi selecionada.
                if (!binding.cbQuarto.isChecked &&
                    !binding.cbCozinha.isChecked
                ) {
                    Toast.makeText(
                        this@CadastroUsuarioActivity,
                        getString(R.string.str_areas_atuacao_obrigatorio),
                        Toast.LENGTH_SHORT
                    ).show()

                    isValido = false
                }

                // Verifica se Quarto foi selecionado.
                if (binding.cbQuarto.isChecked) {
                    areasAtuacao += getString(R.string.str_quarto)
                }

                // Verifica se Cozinha foi selecionada.
                if (binding.cbCozinha.isChecked) {
                    if (areasAtuacao.isNotEmpty()) {
                        areasAtuacao += ", "
                    }

                    areasAtuacao += getString(R.string.str_cozinha)
                }

                // Verifica se pelo menos um serviço foi selecionado.
                if (!binding.cbMontagem.isChecked &&
                    !binding.cbDesmontagem.isChecked &&
                    !binding.cbTransporte.isChecked
                ) {
                    Toast.makeText(
                        this@CadastroUsuarioActivity,
                        "Selecione pelo menos um serviço oferecido.",
                        Toast.LENGTH_SHORT
                    ).show()

                    isValido = false
                }

                // Verifica se Montagem foi selecionada.
                if (binding.cbMontagem.isChecked) {
                    servicosOferecidos += "Montagem de móveis"
                }

                // Verifica se Desmontagem foi selecionada.
                if (binding.cbDesmontagem.isChecked) {
                    if (servicosOferecidos.isNotEmpty()) {
                        servicosOferecidos += ", "
                    }

                    servicosOferecidos += "Desmontagem de móveis"
                }

                // Verifica se Transporte foi selecionado.
                if (binding.cbTransporte.isChecked) {
                    if (servicosOferecidos.isNotEmpty()) {
                        servicosOferecidos += ", "
                    }

                    servicosOferecidos += "Transporte de móveis"
                }

                // Verifica se a cidade está vazia.
                if (cidade.isEmpty()) {
                    binding.tilCidade.error =
                        getString(R.string.str_cidade_obrigatorio)

                    isValido = false
                } else {
                    binding.tilCidade.error = null
                }

                // Verifica se a frente do RG foi selecionada.
                if (documentoFrenteUri == null) {
                    Toast.makeText(
                        this@CadastroUsuarioActivity,
                        "Selecione a frente do RG.",
                        Toast.LENGTH_SHORT
                    ).show()

                    isValido = false
                }

                // Verifica se o verso do RG foi selecionado.
                if (documentoVersoUri == null) {
                    Toast.makeText(
                        this@CadastroUsuarioActivity,
                        "Selecione o verso do RG.",
                        Toast.LENGTH_SHORT
                    ).show()

                    isValido = false
                }
            }

            // Continua o cadastro somente se as validações forem aprovadas.
            if (isValido) {

                if (tipoUsuario == 1) {

                    // Obtém a localização antes de cadastrar o profissional.
                    obterLocalizacao { latitude, longitude ->

                        cadastrarNaApi(
                            nome,
                            email,
                            senha,
                            whatsapp,
                            cidade,
                            descricao,
                            areasAtuacao,
                            servicosOferecidos,
                            latitude,
                            longitude,
                            15.0
                        )
                    }

                } else {

                    // O cadastro do Cliente não precisa de localização.
                    cadastrarNaApi(
                        nome,
                        email,
                        senha,
                        whatsapp,
                        cidade,
                        descricao,
                        areasAtuacao,
                        servicosOferecidos,
                        null,
                        null,
                        null
                    )
                }
            }
        }
    }

    // Aplica a máscara do WhatsApp enquanto o usuário digita.
    private fun configurarMascaraWhatsapp() {

        binding.tieWhatsapp.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable?) {

                // Mantém somente os números e limita a 11 dígitos.
                val numeros = s.toString()
                    .filter { it.isDigit() }
                    .take(11)

                // Monta a máscara conforme a quantidade de números digitados.
                val whatsappFormatado = when {
                    numeros.isEmpty() -> ""
                    numeros.length <= 2 -> "($numeros"
                    numeros.length <= 7 ->
                        "(${numeros.take(2)}) ${numeros.drop(2)}"
                    else ->
                        "(${numeros.take(2)}) ${numeros.substring(2, 7)}-${numeros.substring(7)}"
                }

                // Atualiza o campo somente quando o texto estiver diferente.
                if (s.toString() != whatsappFormatado) {
                    binding.tieWhatsapp.setText(whatsappFormatado)
                    binding.tieWhatsapp.setSelection(whatsappFormatado.length)
                }
            }
        })
    }

    // Obtém a localização atual do dispositivo.
    private fun obterLocalizacao(
        aoObterLocalizacao: (Double, Double) -> Unit
    ) {

        // Verifica se existe alguma permissão de localização concedida.
        val permissaoPrecisa = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val permissaoAproximada = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // Solicita autorização caso nenhuma permissão tenha sido concedida.
        if (!permissaoPrecisa && !permissaoAproximada) {

            acaoAposPermissao = {
                obterLocalizacao(aoObterLocalizacao)
            }

            solicitarPermissaoLocalizacao.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )

            return
        }

        // Solicita a localização atual do dispositivo.
        val tokenCancelamento = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            tokenCancelamento.token
        ).addOnSuccessListener { localizacao ->

            if (localizacao != null) {

                // Recupera latitude e longitude.
                val latitude = localizacao.latitude
                val longitude = localizacao.longitude

                // Continua o cadastro com as coordenadas obtidas.
                aoObterLocalizacao(latitude, longitude)

            } else {

                Toast.makeText(
                    this,
                    "Não foi possível obter sua localização. Verifique se o GPS está ativado.",
                    Toast.LENGTH_LONG
                ).show()
            }

        }.addOnFailureListener {

            Toast.makeText(
                this,
                "Erro ao obter a localização do dispositivo.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Envia os dados do cadastro para a API PHP.
    private fun cadastrarNaApi(
        nome: String,
        email: String,
        senha: String,
        whatsapp: String,
        cidade: String,
        descricao: String,
        areasAtuacao: String,
        servicosOferecidos: String,
        latitude: Double?,
        longitude: Double?,
        raioAtendimentoKm: Double?
    ) {

        // Converte os dados de texto para RequestBody.
        val tipo = if (tipoUsuario == 1) {
            "PROFISSIONAL"
        } else {
            "CLIENTE"
        }

        val nomeBody = nome.toRequestBody("text/plain".toMediaType())
        val emailBody = email.toRequestBody("text/plain".toMediaType())
        val senhaBody = senha.toRequestBody("text/plain".toMediaType())
        val tipoBody = tipo.toRequestBody("text/plain".toMediaType())

        // Os dados abaixo são enviados somente no cadastro profissional.
        val whatsappBody: RequestBody? =
            if (tipoUsuario == 1) {
                whatsapp.toRequestBody("text/plain".toMediaType())
            } else {
                null
            }

        val cidadeBody: RequestBody? =
            if (tipoUsuario == 1) {
                cidade.toRequestBody("text/plain".toMediaType())
            } else {
                null
            }

        // Prepara a latitude para envio à API.
        val latitudeBody: RequestBody? =
            latitude?.toString()?.toRequestBody("text/plain".toMediaType())

        // Prepara a longitude para envio à API.
        val longitudeBody: RequestBody? =
            longitude?.toString()?.toRequestBody("text/plain".toMediaType())

        // Prepara o raio de atendimento para envio à API.
        val raioAtendimentoKmBody: RequestBody? =
            raioAtendimentoKm?.toString()
                ?.toRequestBody("text/plain".toMediaType())

        val descricaoBody: RequestBody? =
            if (tipoUsuario == 1) {
                descricao.toRequestBody("text/plain".toMediaType())
            } else {
                null
            }

        val categoriasBody: RequestBody? =
            if (tipoUsuario == 1) {
                areasAtuacao.toRequestBody("text/plain".toMediaType())
            } else {
                null
            }

        val servicosBody: RequestBody? =
            if (tipoUsuario == 1) {
                servicosOferecidos.toRequestBody("text/plain".toMediaType())
            } else {
                null
            }

        // Prepara as imagens do RG para envio.
        val parteFrente = if (tipoUsuario == 1) {
            documentoFrenteUri?.let {
                criarParteArquivo(it, "rg_frente")
            }
        } else {
            null
        }

        val parteVerso = if (tipoUsuario == 1) {
            documentoVersoUri?.let {
                criarParteArquivo(it, "rg_verso")
            }
        } else {
            null
        }

        // Impede o envio se não foi possível preparar os documentos.
        if (tipoUsuario == 1 &&
            (parteFrente == null || parteVerso == null)
        ) {
            Toast.makeText(
                this,
                "Não foi possível preparar as imagens do RG.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        // Desabilita o botão durante o envio.
        binding.btnCadastrar.isEnabled = false

        // Realiza a chamada para a API PHP.
        RetrofitClient.apiService.cadastrarUsuario(
            nomeBody,
            emailBody,
            senhaBody,
            tipoBody,
            whatsappBody,
            cidadeBody,
            latitudeBody,
            longitudeBody,
            raioAtendimentoKmBody,
            descricaoBody,
            categoriasBody,
            servicosBody,
            parteFrente,
            parteVerso
        ).enqueue(object : Callback<JsonObject> {

            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                // Libera o botão após receber a resposta.
                binding.btnCadastrar.isEnabled = true

                if (response.isSuccessful) {

                    val retorno = response.body()?.getAsJsonObject("retorno")

                    if (retorno != null) {
                        // O cadastro foi confirmado pelo servidor.
                        tratarCadastroSucesso(retorno, nome, email)
                    } else {
                        Toast.makeText(
                            this@CadastroUsuarioActivity,
                            "A API não retornou os dados esperados.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {
                    // Recupera a mensagem de erro enviada pelo PHP.
                    val mensagemErro = try {
                        val corpoErro = response.errorBody()?.string()

                        if (!corpoErro.isNullOrBlank()) {
                            JsonParser.parseString(corpoErro)
                                .asJsonObject
                                .get("erro")
                                ?.asString
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    Toast.makeText(
                        this@CadastroUsuarioActivity,
                        mensagemErro ?: "Não foi possível realizar o cadastro.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                // Libera o botão caso ocorra uma falha de conexão.
                binding.btnCadastrar.isEnabled = true

                Toast.makeText(
                    this@CadastroUsuarioActivity,
                    "Erro de conexão com o servidor: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    // Converte a imagem selecionada em uma parte para envio Multipart.
    private fun criarParteArquivo(
        uri: Uri,
        nomeCampo: String
    ): MultipartBody.Part? {

        return try {
            // Recupera o tipo de imagem selecionada.
            val tipoImagem = contentResolver.getType(uri) ?: "image/jpeg"

            // Cria um arquivo temporário no armazenamento do aplicativo.
            val arquivo = File(
                cacheDir,
                "${nomeCampo}_${System.currentTimeMillis()}.jpg"
            )

            // Copia o conteúdo da imagem para o arquivo temporário.
            contentResolver.openInputStream(uri)?.use { entrada ->
                FileOutputStream(arquivo).use { saida ->
                    entrada.copyTo(saida)
                }
            } ?: return null

            // Prepara o arquivo para envio.
            val requestBody = arquivo.asRequestBody(tipoImagem.toMediaType())

            // Cria a parte Multipart com o nome esperado pelo PHP.
            MultipartBody.Part.createFormData(
                nomeCampo,
                arquivo.name,
                requestBody
            )

        } catch (e: Exception) {
            null
        }
    }

    // Trata o cadastro confirmado pela API.
    private fun tratarCadastroSucesso(
        retorno: JsonObject,
        nome: String,
        email: String
    ) {

        // Recupera o ID criado pelo banco de dados.
        // O PHP utiliza o nome idUsuario no retorno.
        val novoIdUsuario = retorno.get("idUsuario")?.asInt ?: 0

        // Acessa o armazenamento local somente para manter a sessão.
        val preferencias = getSharedPreferences(
            "MonteMax",
            MODE_PRIVATE
        )

        // Verifica se é um cadastro profissional.
        if (tipoUsuario == 1) {

            // Informa que o cadastro está aguardando aprovação.
            AlertDialog.Builder(this@CadastroUsuarioActivity)
                .setTitle("Cadastro recebido!")
                .setMessage(
                    "Seus dados, serviços selecionados e as duas imagens " +
                            "do RG foram recebidos e estão aguardando a análise " +
                            "da equipe MonteMax.\n\n" +
                            "Seu cadastro está com o status PENDENTE."
                )
                .setCancelable(false)
                .setPositiveButton("Entendi") { _, _ ->
                    // Retorna à tela anterior sem liberar o acesso.
                    finish()
                }
                .show()

        } else {

            // Ativa a sessão do cliente após a confirmação do servidor.
            val editorSessao = preferencias.edit()

            editorSessao.putInt("idUsuario", novoIdUsuario)
            editorSessao.putString("nome", nome)
            editorSessao.putString("email", email.lowercase())
            editorSessao.putInt("tipoUsuario", tipoUsuario)

            val sessaoSalva = editorSessao.commit()

            if (sessaoSalva) {

                // Exibe uma mensagem de sucesso.
                Toast.makeText(
                    this@CadastroUsuarioActivity,
                    getString(R.string.str_login_sucesso),
                    Toast.LENGTH_SHORT
                ).show()

                // Abre a tela principal.
                val intent = Intent(
                    this@CadastroUsuarioActivity,
                    PrincipalActivity::class.java
                )

                // Envia o tipo e o ID do usuário para a tela principal.
                intent.putExtra("tipoUsuario", tipoUsuario)
                intent.putExtra("idUsuario", novoIdUsuario)

                startActivity(intent)

                // Finaliza a tela de cadastro.
                finish()

            } else {
                Toast.makeText(
                    this@CadastroUsuarioActivity,
                    "Não foi possível iniciar a sessão.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Método responsável por preencher o Spinner.
    private fun popularSpinner() {

        // Cria um ArrayAdapter para fornecer as opções.
        val adapter = ArrayAdapter(
            this@CadastroUsuarioActivity,
            android.R.layout.simple_expandable_list_item_1,
            listOf(
                "Cliente",
                "Profissional"
            )
        )

        // Associa o ArrayAdapter ao Spinner.
        binding.spTipoUsuario.adapter = adapter

        // Define esta Activity para receber as seleções.
        binding.spTipoUsuario.onItemSelectedListener = this
    }

    // Método executado quando o usuário seleciona uma opção.
    override fun onItemSelected(
        p0: AdapterView<*>?,
        p1: View?,
        posicao: Int,
        p3: Long
    ) {

        // Atualiza o tipo de usuário conforme a posição selecionada.
        tipoUsuario = posicao

        // Verifica qual tipo de usuário foi escolhido.
        when (posicao) {

            // Quando Cliente é selecionado.
            0 -> {

                // Esconde os campos do profissional.
                binding.tilWhatsapp.visibility = View.GONE
                binding.tilDescricao.visibility = View.GONE
                binding.tvAreaAtuacao.visibility = View.GONE
                binding.cbQuarto.visibility = View.GONE
                binding.cbCozinha.visibility = View.GONE
                binding.tvServicos.visibility = View.GONE
                binding.cbMontagem.visibility = View.GONE
                binding.cbDesmontagem.visibility = View.GONE
                binding.cbTransporte.visibility = View.GONE
                binding.tilCidade.visibility = View.GONE

                // Esconde os campos dos documentos.
                binding.tvDocumentoTitulo.visibility = View.GONE
                binding.tvDocumentoDescricao.visibility = View.GONE
                binding.tvFrenteRG.visibility = View.GONE
                binding.ivDocumentoFrente.visibility = View.GONE
                binding.btnSelecionarDocumentoFrente.visibility = View.GONE
                binding.tvDocumentoFrenteSelecionado.visibility = View.GONE
                binding.tvVersoRG.visibility = View.GONE
                binding.ivDocumentoVerso.visibility = View.GONE
                binding.btnSelecionarDocumentoVerso.visibility = View.GONE
                binding.tvDocumentoVersoSelecionado.visibility = View.GONE
            }

            // Quando Profissional é selecionado.
            1 -> {

                // Exibe os campos do profissional.
                binding.tilWhatsapp.visibility = View.VISIBLE
                binding.tilDescricao.visibility = View.VISIBLE
                binding.tvAreaAtuacao.visibility = View.VISIBLE
                binding.cbQuarto.visibility = View.VISIBLE
                binding.cbCozinha.visibility = View.VISIBLE
                binding.tvServicos.visibility = View.VISIBLE
                binding.cbMontagem.visibility = View.VISIBLE
                binding.cbDesmontagem.visibility = View.VISIBLE
                binding.cbTransporte.visibility = View.VISIBLE
                binding.tilCidade.visibility = View.VISIBLE

                // Exibe os campos dos documentos.
                binding.tvDocumentoTitulo.visibility = View.VISIBLE
                binding.tvDocumentoDescricao.visibility = View.VISIBLE
                binding.tvFrenteRG.visibility = View.VISIBLE
                binding.btnSelecionarDocumentoFrente.visibility = View.VISIBLE
                binding.tvVersoRG.visibility = View.VISIBLE
                binding.btnSelecionarDocumentoVerso.visibility = View.VISIBLE

                // Exibe a prévia da frente se já houver imagem selecionada.
                if (documentoFrenteUri != null) {
                    binding.ivDocumentoFrente.visibility = View.VISIBLE
                    binding.tvDocumentoFrenteSelecionado.visibility = View.VISIBLE
                }

                // Exibe a prévia do verso se já houver imagem selecionada.
                if (documentoVersoUri != null) {
                    binding.ivDocumentoVerso.visibility = View.VISIBLE
                    binding.tvDocumentoVersoSelecionado.visibility = View.VISIBLE
                }
            }
        }
    }

    // Método chamado quando nenhuma opção está selecionada.
    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}
