package com.fredrueda.picoplacacamera.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.View
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
        //Manifest.permission.CAMERA,
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

        //verificarYSolicitarPermisos()

        binding.btnVerificar.setOnClickListener {
            binding.btnLeerPlaca.visibility = View.GONE
            binding.imagenRestriccion.setImageDrawable(null)
            val ciudadSeleccionada = binding.spCiudad.selectedItem.toString()
            val placa = binding.etPlaca.text.toString()
            val tipo = when {
                binding.rbCarro.isChecked -> "carro"
                binding.rbMoto.isChecked -> "moto"
                binding.rbTaxi.isChecked -> "taxi"
                else -> "carro"
            }

            if (ciudadSeleccionada == "Selecciona tu ciudad") {
                Toast.makeText(this, "Seleccione una ciudad", Toast.LENGTH_SHORT).show()
                binding.tvResultado.text = "Selecciona una ciudad valida"
                return@setOnClickListener
            }
            if (!esPlacaValida(placa, tipo)) {
                Toast.makeText(this, "La placa no es válida para el tipo seleccionado", Toast.LENGTH_SHORT).show()
                binding.tvResultado.text = "La placa no es válida para el tipo seleccionado"
                return@setOnClickListener
            }

            viewModel.verificarPlaca(ciudadSeleccionada.lowercase(), tipo, placa)
        }

        binding.btnLeerPlaca.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        viewModel.restriccion.observe(this) { restriccion ->
            if(binding.spCiudad.selectedItem.toString().lowercase() == "armenia"){
                binding.btnLeerPlaca.visibility = View.VISIBLE
            }else{
                binding.btnLeerPlaca.visibility = View.GONE
            }
            binding.tvResultado.text = restriccion.mensaje
            Glide.with(this)
                .load(restriccion.imagen)
                .into(binding.imagenRestriccion)
        }

    }

    private fun esPlacaValida(placa: String, tipo: String): Boolean {
        val placaMayuscula = placa.uppercase().trim()
        val regexCarro = Regex("^[A-Z]{3}\\d{3}\$") // ABC123
        val regexMoto = Regex("^[A-Z]{3}\\d{2}[A-Z]\$") // IVK57D
        val regexTaxi = Regex("^[A-Z]{3}\\d{3}[A-Z]?\$") // ABC123 o ABC123D

        return when (tipo.lowercase()) {
            "carro" -> regexCarro.matches(placaMayuscula)
            "moto" -> regexMoto.matches(placaMayuscula)
            "taxi" -> regexTaxi.matches(placaMayuscula)
            else -> false
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