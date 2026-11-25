package br.com.montemax.activity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.montemax.activity.model.FotoPublicacao
import br.com.montemax.montemax.databinding.ItemFotoPublicacaoBinding
import com.bumptech.glide.Glide

class FotoPublicacaoAdapter :
    ListAdapter<
            FotoPublicacao,
            FotoPublicacaoAdapter.FotoPublicacaoViewHolder
            >(DIFF) {

    class FotoPublicacaoViewHolder(
        private val itemFotoPublicacaoBinding:
        ItemFotoPublicacaoBinding
    ) : RecyclerView.ViewHolder(
        itemFotoPublicacaoBinding.root
    ) {

        fun bind(
            fotoPublicacao: FotoPublicacao
        ) {

            itemFotoPublicacaoBinding.tvDescricao.text =
                fotoPublicacao.descricao

            Glide.with(
                itemFotoPublicacaoBinding.ivFoto.context
            )
                .load(fotoPublicacao.imagemUrl)
                .into(
                    itemFotoPublicacaoBinding.ivFoto
                )
        }

        companion object {

            fun create(
                parent: ViewGroup
            ): FotoPublicacaoViewHolder {

                val itemFotoPublicacaoBinding =
                    ItemFotoPublicacaoBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )

                return FotoPublicacaoViewHolder(
                    itemFotoPublicacaoBinding
                )
            }
        }
    }

    companion object {

        private val DIFF =
            object : DiffUtil.ItemCallback<FotoPublicacao>() {

                override fun areItemsTheSame(
                    oldItem: FotoPublicacao,
                    newItem: FotoPublicacao
                ): Boolean {

                    return oldItem.idFoto ==
                            newItem.idFoto
                }

                override fun areContentsTheSame(
                    oldItem: FotoPublicacao,
                    newItem: FotoPublicacao
                ): Boolean {

                    return oldItem == newItem
                }
            }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FotoPublicacaoViewHolder {

        return FotoPublicacaoViewHolder.create(
            parent
        )
    }

    override fun onBindViewHolder(
        holder: FotoPublicacaoViewHolder,
        position: Int
    ) {

        try {

            holder.bind(
                fotoPublicacao = getItem(position)
            )

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }
}