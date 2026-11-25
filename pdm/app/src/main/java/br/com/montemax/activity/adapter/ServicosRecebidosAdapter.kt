package br.com.montemax.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.montemax.activity.model.ServicoRecebido
import br.com.montemax.montemax.databinding.ItemServicoRecebidoBinding

class ServicosRecebidosAdapter :
    ListAdapter<ServicoRecebido, ServicosRecebidosAdapter.ViewHolder>(
        DiffCallback()
    ) {

    var itemClickAprovar: ((ServicoRecebido) -> Unit)? = null
    var itemClickRecusar: ((ServicoRecebido) -> Unit)? = null
    var itemClickIniciar: ((ServicoRecebido) -> Unit)? = null
    var itemClickConcluir: ((ServicoRecebido) -> Unit)? = null

    inner class ViewHolder(
        private val binding: ItemServicoRecebidoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(servico: ServicoRecebido) {

            // Exibe o nome do cliente.
            binding.tvCliente.text =
                "Cliente: ${servico.clienteNome}"

            // Exibe o tipo de serviço solicitado.
            binding.tvTipoServico.text =
                servico.tipoServico

            // Exibe a descrição da solicitação.
            binding.tvDescricao.text =
                servico.descricao

            // Exibe a data da solicitação.
            binding.tvData.text =
                servico.dataSolicitacao

            // Exibe o status atual.
            binding.tvStatus.text =
                "Status: ${servico.status}"

            // Esconde todos os botões inicialmente.
            binding.btnAprovarSolicitacao.visibility = View.GONE
            binding.btnRecusarSolicitacao.visibility = View.GONE
            binding.btnIniciarServico.visibility = View.GONE
            binding.btnConcluirServico.visibility = View.GONE

            // Exibe os botões conforme o status.
            when (servico.status) {

                "Pendente" -> {
                    binding.btnAprovarSolicitacao.visibility = View.VISIBLE
                    binding.btnRecusarSolicitacao.visibility = View.VISIBLE
                }

                "Aprovada" -> {
                    binding.btnIniciarServico.visibility = View.VISIBLE
                }

                "Em andamento" -> {
                    binding.btnConcluirServico.visibility = View.VISIBLE
                }
            }

            // Controla a visibilidade do espaço dos botões.
            binding.layoutBotoesSolicitacao.visibility =
                if (servico.status == "Pendente" ||
                    servico.status == "Aprovada" ||
                    servico.status == "Em andamento"
                ) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            // Evento do botão Aprovar.
            binding.btnAprovarSolicitacao.setOnClickListener {
                itemClickAprovar?.invoke(servico)
            }

            // Evento do botão Recusar.
            binding.btnRecusarSolicitacao.setOnClickListener {
                itemClickRecusar?.invoke(servico)
            }

            // Evento do botão Iniciar serviço.
            binding.btnIniciarServico.setOnClickListener {
                itemClickIniciar?.invoke(servico)
            }

            // Evento do botão Informar conclusão.
            binding.btnConcluirServico.setOnClickListener {
                itemClickConcluir?.invoke(servico)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = ItemServicoRecebidoBinding.inflate(
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

    class DiffCallback :
        DiffUtil.ItemCallback<ServicoRecebido>() {

        override fun areItemsTheSame(
            oldItem: ServicoRecebido,
            newItem: ServicoRecebido
        ): Boolean {

            return oldItem.idSolicitacao ==
                    newItem.idSolicitacao
        }

        override fun areContentsTheSame(
            oldItem: ServicoRecebido,
            newItem: ServicoRecebido
        ): Boolean {

            return oldItem == newItem
        }
    }
}