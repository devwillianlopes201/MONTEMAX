package br.com.montemax.activity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.montemax.activity.model.Avaliacao
import br.com.montemax.montemax.databinding.ItemAvaliacaoBinding

class AvaliacaoAdapter :
    ListAdapter<Avaliacao, AvaliacaoAdapter.ViewHolder>(DiffCallback()) {

    // Classe responsável por preencher os dados de cada avaliação.
    inner class ViewHolder(
        private val binding: ItemAvaliacaoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(avaliacao: Avaliacao) {

            // Exibe o nome do cliente recebido da API.
            binding.tvCliente.text =
                "Cliente: ${avaliacao.nomeCliente ?: "Cliente não identificado"}"

            // Exibe a nota da avaliação.
            binding.ratingAvaliacao.rating = avaliacao.nota.toFloat()

            // Exibe o comentário.
            binding.tvComentario.text =
                avaliacao.comentario ?: "Sem comentário."

            // Exibe a data da avaliação.
            binding.tvDataAvaliacao.text = avaliacao.dataAvaliacao
        }
    }

    // Cria o layout de cada avaliação.
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = ItemAvaliacaoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    // Preenche o item com os dados da avaliação.
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    // Compara as avaliações para atualizar a lista.
    class DiffCallback : DiffUtil.ItemCallback<Avaliacao>() {

        override fun areItemsTheSame(
            oldItem: Avaliacao,
            newItem: Avaliacao
        ): Boolean {
            return oldItem.idAvaliacao == newItem.idAvaliacao
        }

        override fun areContentsTheSame(
            oldItem: Avaliacao,
            newItem: Avaliacao
        ): Boolean {
            return oldItem == newItem
        }
    }
}