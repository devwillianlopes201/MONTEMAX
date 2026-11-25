package br.com.montemax.activity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.montemax.activity.model.Usuario
import br.com.montemax.montemax.databinding.ItemUsuarioBinding
import com.bumptech.glide.Glide

class UsuarioAdapter :
    ListAdapter<Usuario, UsuarioAdapter.UsuarioViewHolder>(DIFF) {

    var itemClickUsuario: ((posicao: Int) -> Unit)? = null

    // PRIMEIRO
    class UsuarioViewHolder(
        private val itemUsuarioBinding: ItemUsuarioBinding,
        private val itemClickUsuario: ((posicao: Int) -> Unit)? = null
    ) : RecyclerView.ViewHolder(itemUsuarioBinding.root) {

        // SEGUNDO
        // faz a exibição dos dados da lista ser populada nas linhas,
        // chamado pelo onBindViewHolder
        fun bind(usuario: Usuario, posicao: Int) {

            // OITAVO
            itemUsuarioBinding.tvNome.text = usuario.nome
            itemUsuarioBinding.tvEmail.text = usuario.email

            itemUsuarioBinding.sivFoto.setOnClickListener {
                itemClickUsuario?.invoke(posicao)
            }

            Glide.with(itemUsuarioBinding.sivFoto.context)
                .load(usuario.fotoPerfil)
                .into(itemUsuarioBinding.sivFoto)
        }

        // TERCEIRO
        companion object {

            fun create(
                parent: ViewGroup,
                itemClickUsuario: ((posicao: Int) -> Unit)? = null
            ): UsuarioViewHolder {

                val itemUsuarioBinding = ItemUsuarioBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

                return UsuarioViewHolder(
                    itemUsuarioBinding,
                    itemClickUsuario
                )
            }
        }
    }

    // QUARTO - atualizar itens
    companion object {

        private val DIFF = object : DiffUtil.ItemCallback<Usuario>() {

            override fun areItemsTheSame(
                oldItem: Usuario,
                newItem: Usuario
            ): Boolean {
                return oldItem.idUsuario == newItem.idUsuario
            }

            override fun areContentsTheSame(
                oldItem: Usuario,
                newItem: Usuario
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    // SEXTO
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsuarioViewHolder {

        return UsuarioViewHolder.create(
            parent,
            itemClickUsuario
        )
    }

    // SÉTIMO
    override fun onBindViewHolder(
        holder: UsuarioViewHolder,
        position: Int
    ) {
        try {
            holder.bind(
                usuario = getItem(position),
                posicao = position
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}