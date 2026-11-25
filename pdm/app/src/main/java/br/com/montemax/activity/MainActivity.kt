package br.com.montemax.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.montemax.montemax.R
import br.com.montemax.montemax.databinding.ActivityMainBinding
import br.com.montemax.api.RetrofitClient
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Activity responsável pela tela de login.
class MainActivity : AppCompatActivity() {

    // Faz a ligação entre os componentes do XML.
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define o layout da tela.
        setContentView(binding.root)

        // Evento de clique do botão Entrar.
        binding.btnEntrar.setOnClickListener {

            // Recupera os dados digitados pelo usuário.
            val email = binding.tieEmail.text.toString().trim()
            val senha = binding.tieSenha.text.toString()

            // Controla se os campos foram preenchidos.
            var isValido = true

            // Verifica se o e-mail está vazio.
            if (email.isEmpty()) {

                binding.tilEmail.error =
                    getString(R.string.str_email_obrigatorio)

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

            // Verifica se a senha contém espaços.
            if (senha.any { it.isWhitespace() }) {

                binding.tilSenha.error =
                    "A senha não pode conter espaços."

                isValido = false
            }

            // Continua somente se os campos estiverem válidos.
            if (isValido) {

                // Desabilita o botão enquanto o login é realizado.
                binding.btnEntrar.isEnabled = false

                // Chama o método de login da API.
                RetrofitClient.apiService.fazerLogin(
                    email,
                    senha
                ).enqueue(object : Callback<JsonObject> {

                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {

                        // Habilita novamente o botão.
                        binding.btnEntrar.isEnabled = true

                        if (response.isSuccessful) {

                            // Recupera o objeto de retorno da API.
                            val retorno = response.body()
                                ?.getAsJsonObject("retorno")

                            if (retorno != null) {

                                // Recupera os dados do usuário.
                                val idUsuario = retorno
                                    .get("idUsuario").asInt

                                val nome = retorno
                                    .get("nome").asString

                                val emailUsuario = retorno
                                    .get("email").asString

                                val tipoUsuarioTexto = retorno
                                    .get("tipoUsuario").asString

                                val fotoPerfil = if (
                                    retorno.get("fotoPerfil").isJsonNull
                                ) {
                                    null
                                } else {
                                    retorno.get("fotoPerfil").asString
                                }

                                // Recupera o status do cadastro profissional.
                                val statusCadastro = if (
                                    retorno.get("statusCadastro") == null ||
                                    retorno.get("statusCadastro").isJsonNull
                                ) {
                                    null
                                } else {
                                    retorno.get("statusCadastro").asString
                                }

                                // Recupera os dados profissionais.
                                val whatsapp = if (
                                    retorno.get("whatsapp") == null ||
                                    retorno.get("whatsapp").isJsonNull
                                ) {
                                    ""
                                } else {
                                    retorno.get("whatsapp").asString
                                }

                                val cidade = if (
                                    retorno.get("cidade") == null ||
                                    retorno.get("cidade").isJsonNull
                                ) {
                                    ""
                                } else {
                                    retorno.get("cidade").asString
                                }

                                val areasAtuacao = if (
                                    retorno.get("areasAtuacao") == null ||
                                    retorno.get("areasAtuacao").isJsonNull
                                ) {
                                    ""
                                } else {
                                    retorno.get("areasAtuacao").asString
                                }

                                // Recupera a descrição do profissional.
                                val descricao = if (
                                    retorno.get("descricao") == null ||
                                    retorno.get("descricao").isJsonNull
                                ) {
                                    ""
                                } else {
                                    retorno.get("descricao").asString
                                }

                                // Recupera os serviços oferecidos.
                                val servicos = if (
                                    retorno.get("servicos") == null ||
                                    retorno.get("servicos").isJsonNull
                                ) {
                                    ""
                                } else {
                                    retorno.get("servicos").asString
                                }

                                // Converte o tipo de usuário para o padrão utilizado no aplicativo.
                                val tipoUsuario = when (
                                    tipoUsuarioTexto.uppercase()
                                ) {
                                    "PROFISSIONAL" -> 1
                                    "CLIENTE" -> 0
                                    else -> -1
                                }

                                // Verifica se o tipo de usuário é válido.
                                if (tipoUsuario == -1) {

                                    Toast.makeText(
                                        this@MainActivity,
                                        "Tipo de usuário inválido.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    return
                                }

                                // Acessa o armazenamento local do aplicativo.
                                val preferencias = getSharedPreferences(
                                    "MonteMax",
                                    MODE_PRIVATE
                                )

                                // Cria um editor para atualizar os dados.
                                val editor = preferencias.edit()

                                // Atualiza os dados da conta atualmente conectada.
                                editor.putInt("idUsuario", idUsuario)
                                editor.putString("nome", nome)
                                editor.putString("email", emailUsuario)
                                editor.putInt("tipoUsuario", tipoUsuario)
                                editor.putString("fotoPerfil", fotoPerfil)
                                editor.putString("statusCadastro", statusCadastro)

                                // Salva os dados específicos da conta atualmente conectada.
                                val chaveConta =
                                    "conta_${emailUsuario.trim().lowercase()}"

                                editor.putString("${chaveConta}_nome", nome)
                                editor.putInt("${chaveConta}_tipoUsuario", tipoUsuario)
                                editor.putString("${chaveConta}_whatsapp", whatsapp)
                                editor.putString("${chaveConta}_cidade", cidade)
                                editor.putString("${chaveConta}_areasAtuacao", areasAtuacao)
                                editor.putString("${chaveConta}_fotoPerfil", fotoPerfil)
                                editor.putString("${chaveConta}_descricao", descricao)
                                editor.putString("${chaveConta}_servicos", servicos)

                                // Confirma a gravação das informações.
                                val isOk = editor.commit()

                                if (isOk) {

                                    // Exibe uma mensagem de sucesso.
                                    Toast.makeText(
                                        this@MainActivity,
                                        getString(R.string.str_login_sucesso),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // Cria a navegação para a tela principal.
                                    val intent = Intent(
                                        this@MainActivity,
                                        PrincipalActivity::class.java
                                    )

                                    // Envia os dados da conta para a próxima tela.
                                    intent.putExtra("tipoUsuario", tipoUsuario)
                                    intent.putExtra("email", emailUsuario)
                                    intent.putExtra("idUsuario", idUsuario)
                                    intent.putExtra("statusCadastro", statusCadastro)

                                    startActivity(intent)

                                } else {

                                    Toast.makeText(
                                        this@MainActivity,
                                        "Não foi possível salvar os dados da conta.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } else {

                                Toast.makeText(
                                    this@MainActivity,
                                    "Resposta inválida do servidor.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {

                            // Recupera a mensagem de erro enviada pela API.
                            val mensagemErro = try {
                                JSONObject(
                                    response.errorBody()?.string() ?: "{}"
                                ).optString(
                                    "erro",
                                    "Não foi possível realizar o login."
                                )
                            } catch (e: Exception) {
                                "Não foi possível realizar o login."
                            }

                            Toast.makeText(
                                this@MainActivity,
                                mensagemErro,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<JsonObject>,
                        t: Throwable
                    ) {

                        // Habilita novamente o botão.
                        binding.btnEntrar.isEnabled = true

                        // Informa que não foi possível conectar à API.
                        Toast.makeText(
                            this@MainActivity,
                            "Erro de conexão: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
            }
        }

        // Evento de clique para abrir a tela de cadastro.
        binding.tvCriarConta.setOnClickListener {

            val intent = Intent(
                this@MainActivity,
                CadastroUsuarioActivity::class.java
            )

            startActivity(intent)
        }
    }
}