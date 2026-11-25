package br.com.montemax.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.montemax.activity.adapter.ProfissionalAdapter
import br.com.montemax.activity.model.Profissional
import br.com.montemax.api.RetrofitClient
import br.com.montemax.montemax.databinding.ActivityBuscarProfissionaisBinding
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuscarProfissionaisActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityBuscarProfissionaisBinding.inflate(layoutInflater)
    }

    private val profissionalAdapter by lazy {
        ProfissionalAdapter()
    }

    // Lista que receberá os profissionais aprovados da API.
    private val profissionais = mutableListOf<Profissional>()

    // Cliente responsável por obter a localização do dispositivo.
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Armazena a localização atual do Cliente.
    private var latitudeCliente: Double? = null
    private var longitudeCliente: Double? = null

    // Armazena a distância entre o Cliente e cada profissional, em quilômetros.
    private val distanciasProfissionais = mutableMapOf<Int, Float>()

    // Solicita as permissões de localização ao usuário.
    private val solicitarPermissaoLocalizacao =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissoes ->

            val permissaoPrecisao =
                permissoes[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val permissaoAproximada =
                permissoes[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (permissaoPrecisao || permissaoAproximada) {
                obterLocalizacaoCliente()
            } else {
                Toast.makeText(
                    this,
                    "Permissão de localização não concedida.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        // Inicializa o serviço de localização do Google.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        configurarRecyclerView()

        // Configura a abertura do perfil profissional.
        profissionalAdapter.itemClickPerfil = { profissional ->

            val intent = Intent(
                this@BuscarProfissionaisActivity,
                PerfilProfissionalActivity::class.java
            )

            intent.putExtra("profissional", profissional)

            startActivity(intent)
        }

        // Busca os profissionais cadastrados no banco de dados.
        carregarProfissionais()

        // Verifica a permissão e obtém a localização do Cliente.
        verificarPermissaoLocalizacao()

        // Configura o botão de busca.
        binding.btnBuscar.setOnClickListener {
            buscarProfissionais()
        }
    }

    private fun verificarPermissaoLocalizacao() {

        val permissaoPrecisao = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val permissaoAproximada = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (permissaoPrecisao || permissaoAproximada) {
            obterLocalizacaoCliente()
        } else {
            solicitarPermissaoLocalizacao.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun obterLocalizacaoCliente() {

        val permissaoPrecisao = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val permissaoAproximada = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissaoPrecisao && !permissaoAproximada) {
            return
        }

        // Define a precisão de acordo com a permissão concedida.
        val prioridade = if (permissaoPrecisao) {
            Priority.PRIORITY_HIGH_ACCURACY
        } else {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY
        }

        // Solicita a localização atual do Cliente.
        val tokenCancelamento = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            prioridade,
            tokenCancelamento.token
        ).addOnSuccessListener { localizacao ->

            if (localizacao != null) {

                // Armazena latitude e longitude do Cliente.
                latitudeCliente = localizacao.latitude
                longitudeCliente = localizacao.longitude

                // Calcula as distâncias quando a localização do Cliente é obtida.
                calcularDistanciasProfissionais()

                Toast.makeText(
                    this,
                    "Localização obtida com sucesso.",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                // Tenta recuperar a última localização conhecida.
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { ultimaLocalizacao ->

                        if (ultimaLocalizacao != null) {

                            latitudeCliente = ultimaLocalizacao.latitude
                            longitudeCliente = ultimaLocalizacao.longitude

                            // Calcula as distâncias quando a última localização é recuperada.
                            calcularDistanciasProfissionais()

                            Toast.makeText(
                                this,
                                "Localização obtida com sucesso.",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            Toast.makeText(
                                this,
                                "Não foi possível obter a localização. Verifique se o GPS está ativado.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }.addOnFailureListener {

            Toast.makeText(
                this,
                "Erro ao obter a localização do Cliente.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun carregarProfissionais() {

        // Faz a consulta dos profissionais na API PHP.
        RetrofitClient.apiService.listarProfissionais()
            .enqueue(object : Callback<JsonObject> {

                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {

                    // Verifica se a consulta foi realizada com sucesso.
                    if (response.isSuccessful) {

                        val dados = response.body()

                        // Recupera a lista dentro do objeto retorno.
                        val listaJson = dados?.getAsJsonArray("retorno")

                        if (listaJson != null) {

                            // Converte o JSON recebido em uma lista de profissionais.
                            val tipoLista =
                                object : TypeToken<List<Profissional>>() {}.type

                            val listaProfissionais: List<Profissional> =
                                Gson().fromJson(listaJson, tipoLista)

                            // Atualiza a lista de profissionais.
                            profissionais.clear()
                            profissionais.addAll(listaProfissionais)

                            // Calcula as distâncias após carregar os profissionais.
                            calcularDistanciasProfissionais()

                            // Exibe os profissionais encontrados.
                            profissionalAdapter.submitList(
                                profissionais.toList()
                            )

                        } else {

                            Toast.makeText(
                                this@BuscarProfissionaisActivity,
                                "Nenhum profissional encontrado.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {

                        // Trata a resposta quando nenhum profissional é encontrado.
                        if (response.code() == 404) {

                            profissionais.clear()
                            profissionalAdapter.submitList(emptyList())

                            Toast.makeText(
                                this@BuscarProfissionaisActivity,
                                "Nenhum profissional encontrado.",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            Toast.makeText(
                                this@BuscarProfissionaisActivity,
                                "Erro ao consultar os profissionais.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(
                    call: Call<JsonObject>,
                    t: Throwable
                ) {

                    // Informa quando não foi possível acessar a API.
                    Toast.makeText(
                        this@BuscarProfissionaisActivity,
                        "Não foi possível conectar ao servidor.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // Calcula a distância entre o Cliente e cada profissional que possui coordenadas.
    private fun calcularDistanciasProfissionais() {

        // Verifica se a localização do Cliente já foi obtida.
        val latitude = latitudeCliente
        val longitude = longitudeCliente

        if (latitude == null || longitude == null) {
            return
        }

        // Limpa as distâncias anteriores antes de realizar novos cálculos.
        distanciasProfissionais.clear()

        profissionais.forEach { profissional ->

            // Verifica se o profissional possui latitude e longitude cadastradas.
            val latitudeProfissional = profissional.latitude
            val longitudeProfissional = profissional.longitude

            if (latitudeProfissional != null && longitudeProfissional != null) {

                // Armazena a distância calculada em metros.
                val distanciaEmMetros = FloatArray(1)

                Location.distanceBetween(
                    latitude,
                    longitude,
                    latitudeProfissional,
                    longitudeProfissional,
                    distanciaEmMetros
                )

                // Converte a distância de metros para quilômetros.
                val distanciaEmKm = distanciaEmMetros[0] / 1000f

                distanciasProfissionais[profissional.idProfissional] = distanciaEmKm
            }
        }

        // Envia as distâncias calculadas para o Adapter.
        profissionalAdapter.distanciasProfissionais = distanciasProfissionais.toMap()

        // Atualiza os itens para apresentar as distâncias na tela.
        profissionalAdapter.notifyDataSetChanged()
    }

    private fun configurarRecyclerView() {

        binding.rvProfissionais.apply {

            layoutManager = LinearLayoutManager(
                this@BuscarProfissionaisActivity
            )

            adapter = profissionalAdapter

            isNestedScrollingEnabled = false
        }
    }

    private fun buscarProfissionais() {

        val area = binding.tieAreaAtuacao.text.toString().trim()
        val cidade = binding.tieCidade.text.toString().trim()

        // Recupera a localização atual do Cliente.
        val latitude = latitudeCliente
        val longitude = longitudeCliente

        // Filtra os profissionais pela área, cidade e raio de atendimento.
        val resultado = profissionais.filter { profissional ->

            val correspondeArea =
                area.isEmpty() ||
                        profissional.areasAtuacao.contains(
                            area,
                            ignoreCase = true
                        )

            val correspondeCidade =
                cidade.isEmpty() ||
                        profissional.cidade.contains(
                            cidade,
                            ignoreCase = true
                        )

            // Recupera as coordenadas cadastradas do profissional.
            val latitudeProfissional = profissional.latitude
            val longitudeProfissional = profissional.longitude

            val correspondeRaio = if (
                latitude == null ||
                longitude == null ||
                latitudeProfissional == null ||
                longitudeProfissional == null
            ) {
                // Sem coordenadas, mantém o profissional na busca.
                true
            } else {

                // Calcula a distância entre o Cliente e o profissional.
                val distanciaEmMetros = FloatArray(1)

                Location.distanceBetween(
                    latitude,
                    longitude,
                    latitudeProfissional,
                    longitudeProfissional,
                    distanciaEmMetros
                )

                // Converte a distância de metros para quilômetros.
                val distanciaKm = distanciaEmMetros[0] / 1000f

                // Utiliza o raio cadastrado ou 15 km como padrão.
                val raioAtendimento =
                    (profissional.raioAtendimentoKm ?: 15.0).toFloat()

                // Verifica se o profissional está dentro do raio.
                distanciaKm <= raioAtendimento
            }

            correspondeArea && correspondeCidade && correspondeRaio
        }

        // Atualiza a lista exibida.
        profissionalAdapter.submitList(resultado.toList())

        // Informa quando nenhum profissional é encontrado.
        if (resultado.isEmpty()) {

            Toast.makeText(
                this@BuscarProfissionaisActivity,
                "Nenhum profissional encontrado.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
