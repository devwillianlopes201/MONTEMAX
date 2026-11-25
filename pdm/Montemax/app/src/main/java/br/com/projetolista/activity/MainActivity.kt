package br.com.projetolista.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.projetolista.modelo.Usuario

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
             setContentView(R.layout.activity_main)

    }
    private fun recuperarListaUsuario(): List<Usuario>{
        return listOf(
            Usuario(
                1,
                "willian",
                "10/02/1999",
                "null",
                "emai.lll@gmail.com",
                ),
            Usuario(
                2,
                "w",
                "02/22/6688",
                "219999-8888",
                "uol.22@gmail.com"
            ),
            Usuario(
                5,
                "wi",
                "05/22/6688",
                "218599-8888",
                "uol.777@gmail.com"
            ),
        )
    }
}