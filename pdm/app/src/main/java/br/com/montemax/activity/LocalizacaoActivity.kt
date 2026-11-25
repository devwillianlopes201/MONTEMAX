package br.com.montemax.activity

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.montemax.montemax.databinding.ActivityLocalizacaoBinding
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Geocoder
import java.util.Locale

class LocalizacaoActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLocalizacaoBinding.inflate(layoutInflater)
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                obterLocalizacao()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(
            this
        )

        binding.btnLocalizar.setOnClickListener {
            verificarPermissao()
        }

    }

    private fun verificarPermissao() {
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            obterLocalizacao()
        } else {
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun obterLocalizacao() {
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        try {
                            val geocoder = Geocoder(
                                this,
                                Locale.getDefault()
                            )

                            val enderecos = geocoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1
                            )

                            if (!enderecos.isNullOrEmpty()) {
                                binding.tvLocalizacao.text =
                                    enderecos[0].getAddressLine(0)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
        }
    }

}