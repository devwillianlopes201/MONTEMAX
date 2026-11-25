package br.com.montemax.activity.adapter

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.montemax.montemax.R
import br.com.montemax.activity.model.Postagem
import br.com.montemax.api.RetrofitClient
import br.com.montemax.montemax.databinding.ItemPostagemBinding
import com.bumptech.glide.Glide

class PostagemAdapter :
    ListAdapter<Postagem, PostagemAdapter.PostagemViewHolder>(DIFF) {

    var itemClickComentarios: (Postagem) -> Unit = {}
    var itemClickEditar: (Postagem) -> Unit = {}
    var itemClickExcluir: (Postagem) -> Unit = {}
    var mostrarAcoes: Boolean = false

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Postagem>() {

            override fun areItemsTheSame(
                oldItem: Postagem,
                newItem: Postagem
            ) = oldItem.idPublicacao == newItem.idPublicacao

            override fun areContentsTheSame(
                oldItem: Postagem,
                newItem: Postagem
            ) = oldItem == newItem
        }
    }

    inner class PostagemViewHolder(
        private val binding: ItemPostagemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(postagem: Postagem) {

            binding.tvTitulo.text = postagem.titulo
            binding.tvConteudo.text = postagem.conteudo
            binding.tvDataPublicacao.text = postagem.dataPublicacao

            carregarDadosAutor(postagem)
            carregarFotosPublicacao(postagem)

            binding.btnComentarios.setOnClickListener {
                itemClickComentarios(postagem)
            }

            if (mostrarAcoes) {
                binding.btnEditar.visibility = View.VISIBLE
                binding.btnExcluir.visibility = View.VISIBLE
            } else {
                binding.btnEditar.visibility = View.GONE
                binding.btnExcluir.visibility = View.GONE
            }

            binding.btnEditar.setOnClickListener {
                itemClickEditar(postagem)
            }

            binding.btnExcluir.setOnClickListener {
                itemClickExcluir(postagem)
            }
        }

        private fun carregarDadosAutor(postagem: Postagem) {

            binding.tvNomeAutor.text = postagem.nomeAutor

            if (!postagem.fotoPerfil.isNullOrEmpty()) {

                val url = RetrofitClient.instancia.baseUrl()
                    .resolve(postagem.fotoPerfil)
                    .toString()

                Glide.with(binding.root.context)
                    .load(url)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(binding.ivFotoAutor)

            } else {

                Glide.with(binding.root.context)
                    .load(R.drawable.ic_launcher_foreground)
                    .into(binding.ivFotoAutor)
            }
        }

        private fun carregarFotosPublicacao(postagem: Postagem) {

            binding.layoutFotosPublicacao.removeAllViews()

            if (postagem.fotosPublicacao.isNotEmpty()) {

                binding.scrollFotosPublicacao.visibility = View.VISIBLE

                for (foto in postagem.fotosPublicacao) {

                    val imageView = ImageView(binding.root.context)

                    val parametros = LinearLayout.LayoutParams(
                        (260 * binding.root.context.resources.displayMetrics.density).toInt(),
                        (200 * binding.root.context.resources.displayMetrics.density).toInt()
                    )

                    parametros.marginEnd =
                        (8 * binding.root.context.resources.displayMetrics.density).toInt()

                    imageView.layoutParams = parametros

                    imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                    imageView.adjustViewBounds = true

                    val url = RetrofitClient.instancia.baseUrl()
                        .resolve(foto.imagemUrl)
                        .toString()

                    Glide.with(binding.root.context)
                        .load(url)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(imageView)

                    // Abre a imagem ampliada ao clicar.
                    imageView.setOnClickListener {
                        abrirImagemAmpliada(url)
                    }

                    binding.layoutFotosPublicacao.addView(imageView)
                }

            } else {

                binding.scrollFotosPublicacao.visibility = View.GONE
            }
        }

        // Exibe a imagem em tamanho ampliado.
        private fun abrirImagemAmpliada(url: String) {

            val dialog = Dialog(binding.root.context)

            val imagem = ImageView(binding.root.context)

            imagem.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
            )

            imagem.scaleType = ImageView.ScaleType.FIT_CENTER
            imagem.setBackgroundColor(Color.BLACK)

            // Carrega a imagem no visualizador.
            Glide.with(binding.root.context)
                .load(url)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(imagem)

            // Tocar na imagem fecha a visualização.
            imagem.setOnClickListener {
                dialog.dismiss()
            }

            dialog.setContentView(imagem)
            dialog.setCancelable(true)
            dialog.show()

            // Ocupa praticamente toda a tela.
            dialog.window?.setBackgroundDrawable(
                ColorDrawable(Color.BLACK)
            )

            dialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostagemViewHolder {

        val binding = ItemPostagemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PostagemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PostagemViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
}