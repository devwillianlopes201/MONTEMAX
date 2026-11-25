package br.com.montemax.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.montemax.activity.model.SolicitacaoServico
import br.com.montemax.montemax.databinding.ItemSolicitacaoBinding

class SolicitacaoServicoAdapter :
    ListAdapter<SolicitacaoServico, SolicitacaoServicoAdapter.ViewHolder>(
        DiffCallback()
    ) {

    var itemClickConfirmar: ((SolicitacaoServico) -> Unit)? = null

    var itemClickAvaliar: ((SolicitacaoServico) -> Unit)? = null

    inner class ViewHolder(
        private val binding: ItemSolicitacaoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(solicitacao: SolicitacaoServico) {

            binding.tvProfissional.text =
                solicitacao.profissional.nome

            binding.tvTipoServico.text =
                solicitacao.tipoServico

            binding.tvDescricao.text =
                solicitacao.descricao

            binding.tvData.text =
                solicitacao.dataSolicitacao

            binding.tvStatus.text =
                solicitacao.status

            binding.btnConfirmarServico.visibility =
                if (solicitacao.status == "Aguardando confirmação") {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            binding.btnAvaliarProfissional.visibility =
                if (solicitacao.status == "Concluída") {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            binding.btnConfirmarServico.setOnClickListener {
                itemClickConfirmar?.invoke(solicitacao)
            }

            binding.btnAvaliarProfissional.setOnClickListener {
                itemClickAvaliar?.invoke(solicitacao)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = ItemSolicitacaoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<SolicitacaoServico>() {

        override fun areItemsTheSame(
            oldItem: SolicitacaoServico,
            newItem: SolicitacaoServico
        ): Boolean {

            return oldItem.idSolicitacao ==
                    newItem.idSolicitacao
        }

        override fun areContentsTheSame(
            oldItem: SolicitacaoServico,
            newItem: SolicitacaoServico
        ): Boolean {

            return oldItem == newItem
        }
    }
}