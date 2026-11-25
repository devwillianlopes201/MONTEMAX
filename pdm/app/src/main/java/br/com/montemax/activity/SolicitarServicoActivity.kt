package br.com.montemax.activity

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.montemax.activity.model.Profissional
import br.com.montemax.montemax.R
import br.com.montemax.montemax.databinding.ActivitySolicitarServicoBinding

class SolicitarServicoActivity :
    AppCompatActivity() {

    private val binding by lazy {
        ActivitySolicitarServicoBinding.inflate(
            layoutInflater
        )
    }

    private var profissional: Profissional? = null

    private var areasDisponiveis =
        mutableListOf<String>()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        profissional =
            intent.getSerializableExtra(
                "profissional"
            ) as? Profissional

        configurarSpinner()

        binding.btnEnviarSolicitacao.setOnClickListener {
            validarSolicitacao()
        }
    }

    private fun configurarSpinner() {

        val profissional =
            profissional

        if (profissional == null) {
            Toast.makeText(
                this@SolicitarServicoActivity,
                getString(
                    R.string.str_profissional_nao_encontrado
                ),
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        if (
            profissional.areasAtuacao.contains(
                getString(R.string.str_quarto),
                ignoreCase = true
            )
        ) {
            areasDisponiveis.add(
                getString(R.string.str_quarto)
            )
        }

        if (
            profissional.areasAtuacao.contains(
                getString(R.string.str_cozinha),
                ignoreCase = true
            )
        ) {
            areasDisponiveis.add(
                getString(R.string.str_cozinha)
            )
        }

        val adapter =
            ArrayAdapter(
                this@SolicitarServicoActivity,
                android.R.layout.simple_spinner_item,
                areasDisponiveis
            )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.spAreaAtuacao.adapter =
            adapter
    }

    private fun validarSolicitacao() {

        val areaAtuacao =
            binding.spAreaAtuacao
                .selectedItem
                ?.toString() ?: ""

        val tipoMovel =
            binding.tieTipoMovel.text
                .toString()
                .trim()

        val cidade =
            binding.tieCidade.text
                .toString()
                .trim()

        val endereco =
            binding.tieEndereco.text
                .toString()
                .trim()

        val descricao =
            binding.tieDescricao.text
                .toString()
                .trim()

        var isValido = true

        if (areaAtuacao.isEmpty()) {

            Toast.makeText(
                this@SolicitarServicoActivity,
                getString(
                    R.string.str_area_movel_obrigatoria
                ),
                Toast.LENGTH_SHORT
            ).show()

            isValido = false
        }

        if (tipoMovel.isEmpty()) {

            binding.tilTipoMovel.error =
                getString(
                    R.string.str_tipo_movel_obrigatorio
                )

            isValido = false

        } else {

            binding.tilTipoMovel.error = null
        }

        if (cidade.isEmpty()) {

            binding.tilCidade.error =
                getString(
                    R.string.str_cidade_obrigatorio
                )

            isValido = false

        } else {

            binding.tilCidade.error = null
        }

        if (endereco.isEmpty()) {

            binding.tilEndereco.error =
                getString(
                    R.string.str_endereco_obrigatorio
                )

            isValido = false

        } else {

            binding.tilEndereco.error = null
        }

        if (isValido) {

            Toast.makeText(
                this@SolicitarServicoActivity,
                getString(
                    R.string.str_solicitacao_preenchida
                ),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}