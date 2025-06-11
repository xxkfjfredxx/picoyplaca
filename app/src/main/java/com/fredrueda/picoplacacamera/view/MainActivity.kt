package com.fredrueda.picoplacacamera.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fredrueda.picoplacacamera.Utils
import com.fredrueda.picoplacacamera.databinding.ActivityMainBinding
import com.fredrueda.picoplacacamera.viewmodel.RestriccionViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: RestriccionViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val permisosNecesarios = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permisos ->
            var todosConcedidos = true
            permisos.entries.forEach {
                if (!it.value) todosConcedidos = false
            }
            if (!todosConcedidos) {
                Toast.makeText(this, "Se requieren permisos para continuar", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializar OpenCV
        //if (!OpenCVLoader.initDebug()) {
        //    Toast.makeText(this, "Error al cargar OpenCV", Toast.LENGTH_SHORT).show()
        //}
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configurarSpinnerCiudad()
        detectarCiudadYSeleccionar()
        viewModel = ViewModelProvider(this).get(RestriccionViewModel::class.java)

        verificarYSolicitarPermisos()

        binding.btnVerificar.setOnClickListener {
            val ciudadSeleccionada = binding.spCiudad.selectedItem.toString()
            val placa = binding.etPlaca.text.toString()
            val tipo = if (binding.rbCarro.isChecked) "carro" else "moto"

            if (ciudadSeleccionada == "Selecciona tu ciudad") {
                Toast.makeText(this, "Seleccione una ciudad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (placa.isBlank()) {
                Toast.makeText(this, "Ingrese una placa válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.verificarPlaca(ciudadSeleccionada.lowercase(), tipo, placa)
        }

        binding.btnLeerPlaca.setOnClickListener {
            val ciudadSeleccionada = binding.spCiudad.selectedItem.toString()
            val placa = binding.etPlaca.text.toString()
            val tipo = if (binding.rbCarro.isChecked) "carro" else "moto"

            if (ciudadSeleccionada == "Selecciona tu ciudad") {
                Toast.makeText(this, "Seleccione una ciudad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (placa.isBlank()) {
                Toast.makeText(this, "Ingrese una placa válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.verificarPlaca(ciudadSeleccionada.lowercase(), tipo, placa)
        }

        viewModel.restriccion.observe(this) { restriccion ->
            binding.tvResultado.text = restriccion.mensaje
            Glide.with(this)
                .load(restriccion.imagen)
                .into(binding.imagenRestriccion)
        }

    }

    private fun verificarYSolicitarPermisos() {
        val permisosNoConcedidos = permisosNecesarios.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (permisosNoConcedidos.isNotEmpty()) {
            requestPermissionLauncher.launch(permisosNoConcedidos.toTypedArray())
        }
    }

    private fun configurarSpinnerCiudad() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            Utils.ciudadesDisponibles
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCiudad.adapter = adapter
    }

    private fun detectarCiudadYSeleccionar() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val direccion = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    val ciudadDetectada = direccion?.firstOrNull()?.locality?.capitalize(Locale.ROOT)

                    ciudadDetectada?.let { ciudad ->
                        val index = Utils.ciudadesDisponibles.indexOfFirst {
                            it.equals(ciudad, ignoreCase = true)
                        }
                        if (index > 0) binding.spCiudad.setSelection(index)
                    }
                }
            }
        } else {
            // Solicitar permisos si no están concedidos
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }



}