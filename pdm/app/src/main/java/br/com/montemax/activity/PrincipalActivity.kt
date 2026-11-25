package br.com.montemax.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.montemax.montemax.databinding.ActivityPrincipalBinding

// Activity responsável por controlar o menu principal.
class PrincipalActivity : AppCompatActivity() {

    // Faz a ligação entre os componentes do XML e o Kotlin.
    private val binding by lazy {
        ActivityPrincipalBinding.inflate(layoutInflater)
    }

    // Armazena o tipo de usuário.
    // 0 = Cliente e 1 = Profissional.
    private var tipoUsuario = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        // Verifica se existe uma conta conectada.
        if (!verificarSessao()) {
            return
        }

        // Configura os botões conforme o tipo de usuário.
        configurarTela()

        // Evento de clique do botão Perfil.
        binding.btnPerfil.setOnClickListener {

            val intent = Intent(
                this@PrincipalActivity,
                PerfilActivity::class.java
            )

            intent.putExtra("tipoUsuario", tipoUsuario)

            startActivity(intent)
        }

        // Evento de clique do botão Buscar Profissionais.
        binding.btnBuscarProfissionais.setOnClickListener {

            val intent = Intent(
                this@PrincipalActivity,
                BuscarProfissionaisActivity::class.java
            )

            startActivity(intent)
        }

        // Evento de clique do botão Serviços Recebidos.
        binding.btnServicosRecebidos.setOnClickListener {

            val intent = Intent(
                this@PrincipalActivity,
                ServicosRecebidosActivity::class.java
            )

            startActivity(intent)
        }

        // Evento de clique do botão Comunidade.
        binding.btnComunidade.setOnClickListener {

            val intent = Intent(
                this@PrincipalActivity,
                ComunidadeActivity::class.java
            )

            startActivity(intent)
        }
    }

    // Verifica se existe uma sessão ativa.
    private fun verificarSessao(): Boolean {

        // Acessa os dados armazenados localmente.
        val preferencias = getSharedPreferences(
            "MonteMax",
            MODE_PRIVATE
        )

        // Recupera os dados da conta conectada.
        val idUsuario = preferencias.getInt("idUsuario", 0)

        val email = preferencias.getString("email", "") ?: ""

        tipoUsuario = preferencias.getInt("tipoUsuario", -1)

        val statusCadastro = preferencias
            .getString("statusCadastro", null)

        // Verifica se os dados da sessão são válidos.
        if (
            idUsuario <= 0 ||
            email.isBlank() ||
            tipoUsuario !in 0..1
        ) {

            // Retorna para a tela de login.
            val intent = Intent(
                this@PrincipalActivity,
                MainActivity::class.java
            )

            // Impede o retorno ao menu sem fazer login.
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            finish()

            return false
        }

        // Verifica se o profissional foi aprovado.
        if (
            tipoUsuario == 1 &&
            statusCadastro != "APROVADO"
        ) {

            // Informa a situação do cadastro.
            val mensagem = when (statusCadastro) {

                "PENDENTE" ->
                    "Seu cadastro está aguardando aprovação."

                "REPROVADO" ->
                    "Seu cadastro não foi aprovado."

                else ->
                    "Não foi possível verificar o status do cadastro."
            }

            Toast.makeText(
                this@PrincipalActivity,
                mensagem,
                Toast.LENGTH_LONG
            ).show()

            // Impede o acesso ao menu principal.
            finish()

            return false
        }

        return true
    }

    // Configura os botões conforme o tipo de usuário.
    private fun configurarTela() {

        // Verifica se o usuário é Cliente.
        if (tipoUsuario == 0) {

            // Mostra o botão Buscar Profissionais.
            binding.btnBuscarProfissionais.visibility =
                View.VISIBLE

            // Esconde o botão Serviços Recebidos.
            binding.btnServicosRecebidos.visibility =
                View.GONE

        } else {

            // Esconde o botão Buscar Profissionais.
            binding.btnBuscarProfissionais.visibility =
                View.GONE

            // Mostra o botão Serviços Recebidos.
            binding.btnServicosRecebidos.visibility =
                View.VISIBLE
        }
    }
}
