package br.com.montemax.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.montemax.activity.model.Comentario
import br.com.montemax.montemax.databinding.ItemComentarioBinding

class ComentarioAdapter :
    ListAdapter<Comentario, ComentarioAdapter.ComentarioViewHolder>(DIFF) {

    class ComentarioViewHolder(
        private val itemComentarioBinding: ItemComentarioBinding
    ) : RecyclerView.ViewHolder(itemComentarioBinding.root) {

        fun bind(comentario: Comentario) {

            // Exibe o nome de quem escreveu o comentário.
            itemComentarioBinding.tvNomeAutorComentario.text =
                comentario.nomeAutor

            // Exibe o conteúdo do comentário.
            itemComentarioBinding.tvComentario.text =
                comentario.conteudo

            // Exibe a data do comentário.
            itemComentarioBinding.tvDataComentario.text =
                comentario.dataComentario

            // Oculta edição e exclusão enquanto não existem endpoints PHP.
            itemComentarioBinding.btnEditarComentario.visibility =
                View.GONE

            itemComentarioBinding.btnExcluirComentario.visibility =
                View.GONE
        }

        companion object {

            fun create(parent: ViewGroup): ComentarioViewHolder {

                val itemComentarioBinding =
                    ItemComentarioBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )

                return ComentarioViewHolder(itemComentarioBinding)
            }
        }
    }

    companion object {

        private val DIFF =
            object : DiffUtil.ItemCallback<Comentario>() {

                override fun areItemsTheSame(
                    oldItem: Comentario,
                    newItem: Comentario
                ): Boolean {

                    return oldItem.idComentario ==
                            newItem.idComentario
                }

                override fun areContentsTheSame(
                    oldItem: Comentario,
                    newItem: Comentario
                ): Boolean {

                    return oldItem == newItem
                }
            }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ComentarioViewHolder {

        return ComentarioViewHolder.create(parent)
    }

    override fun onBindViewHolder(
        holder: ComentarioViewHolder,
        position: Int
    ) {

        try {

            holder.bind(
                comentario = getItem(position)
            )

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }
}