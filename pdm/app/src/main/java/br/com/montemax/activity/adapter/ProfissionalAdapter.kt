package br.com.montemax.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.montemax.activity.model.Profissional
import br.com.montemax.api.RetrofitClient
import br.com.montemax.montemax.R
import br.com.montemax.montemax.databinding.ItemProfissionalBinding
import com.bumptech.glide.Glide
import java.util.Locale

class ProfissionalAdapter :
    ListAdapter<Profissional, ProfissionalAdapter.ProfissionalViewHolder>(DIFF) {

    var itemClickPerfil: ((profissional: Profissional) -> Unit)? = null

    // Armazena as distâncias calculadas entre o Cliente e os profissionais.
    var distanciasProfissionais: Map<Int, Float> = emptyMap()

    inner class ProfissionalViewHolder(
        private val itemProfissionalBinding: ItemProfissionalBinding
    ) : RecyclerView.ViewHolder(itemProfissionalBinding.root) {

        fun bind(profissional: Profissional) {

            // Apresenta os dados do profissional.
            itemProfissionalBinding.tvNome.text = profissional.nome

            itemProfissionalBinding.tvAreaAtuacao.text =
                "Área de atuação: " + profissional.areasAtuacao

            itemProfissionalBinding.tvCidade.text =
                "Cidade: " + profissional.cidade

            // Recupera a distância calculada para o profissional.
            val distancia = distanciasProfissionais[profissional.idProfissional]

            if (distancia != null) {
                itemProfissionalBinding.tvDistancia.text = String.format(
                    Locale.getDefault(),
                    "A %.1f km de você",
                    distancia
                )
            } else {
                itemProfissionalBinding.tvDistancia.text =
                    "Distância não disponível"
            }

            // Recupera o caminho da foto do profissional.
            val foto = profissional.fotoPerfil

            if (foto.isNullOrEmpty()) {

                // Apresenta a imagem padrão quando não existe foto.
                itemProfissionalBinding.sivFotoPerfil.visibility = View.VISIBLE

                Glide.with(itemProfissionalBinding.sivFotoPerfil.context)
                    .load(R.drawable.ic_launcher_foreground)
                    .into(itemProfissionalBinding.sivFotoPerfil)

            } else {

                // Verifica se o caminho já possui o endereço do servidor.
                val enderecoFoto = if (foto.startsWith("http")) {
                    foto
                } else {
                    RetrofitClient.instancia
                        .baseUrl()
                        .resolve(foto.trimStart('/'))
                        ?.toString()
                }

                itemProfissionalBinding.sivFotoPerfil.visibility = View.VISIBLE

                // Carrega a foto armazenada no servidor.
                Glide.with(itemProfissionalBinding.sivFotoPerfil.context)
                    .load(enderecoFoto)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(itemProfissionalBinding.sivFotoPerfil)
            }

            // Abre o perfil do profissional selecionado.
            itemProfissionalBinding.btnVerPerfil.setOnClickListener {
                itemClickPerfil?.invoke(profissional)
            }
        }
    }

    companion object {

        private val DIFF =
            object : DiffUtil.ItemCallback<Profissional>() {

                override fun areItemsTheSame(
                    oldItem: Profissional,
                    newItem: Profissional
                ): Boolean {

                    return oldItem.idProfissional ==
                            newItem.idProfissional
                }

                override fun areContentsTheSame(
                    oldItem: Profissional,
                    newItem: Profissional
                ): Boolean {

                    return oldItem == newItem
                }
            }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfissionalViewHolder {

        val binding = ItemProfissionalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ProfissionalViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProfissionalViewHolder,
        position: Int
    ) {

        try {
            holder.bind(getItem(position))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
