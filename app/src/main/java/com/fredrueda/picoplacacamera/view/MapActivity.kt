package com.fredrueda.picoplacacamera.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fredrueda.picoplacacamera.R
import com.fredrueda.picoplacacamera.model.DelimitacionesPorCiudad
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Locale

class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map)
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val map = findViewById<MapView>(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val mapController = map.controller
        mapController.setZoom(14.0)
        map.controller.setCenter(GeoPoint(4.535, -75.675))

        pintarTrazadoCiudad(
            context = this, // o requireContext() si estás en un fragmento
            ciudad = "armenia",
            mapView = map
        )
        val permisos = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permisos.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            ActivityCompat.requestPermissions(this, permisos, 1001)
        }
        map.setMultiTouchControls(true) // permite zoom y gestos multitáctiles

        val rotationGestureOverlay = RotationGestureOverlay(map)
        rotationGestureOverlay.isEnabled = true
        map.overlays.add(rotationGestureOverlay)
        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        locationOverlay.enableMyLocation()  // Comienza a rastrear
        locationOverlay.enableFollowLocation() // Sigue tu ubicación automáticamente
        map.overlays.add(locationOverlay)
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Cierra la actividad y vuelve atrás
        }
    }

    fun pintarTrazadoCiudad(context: Context, ciudad: String, mapView: MapView) {
        val delimitaciones = leerDelimitacionDesdeRaw(context)
        val ciudadData = delimitaciones?.get(ciudad.lowercase(Locale.ROOT)) ?: return

        ciudadData.perimetros.forEachIndexed { index, perimetro ->
            val puntos = perimetro.coordinates.map {
                GeoPoint(it[1], it[0]) // LatLng = (latitude, longitude)
            }

            val polyline = Polyline().apply {
                setPoints(puntos)
                color = if (index == 0) Color.parseColor("#FFA500") else Color.parseColor("#00AA00") // naranja y verde
                width = 6.0f
            }

            mapView.overlays.add(polyline)
        }

        mapView.invalidate()
    }



    fun leerDelimitacionDesdeRaw(context: Context): DelimitacionesPorCiudad? {
        return try {
            val inputStream = context.resources.openRawResource(R.raw.armenia)
            val json = inputStream.bufferedReader().use { it.readText() }
            val gson = Gson()
            gson.fromJson(json, object : TypeToken<DelimitacionesPorCiudad>() {}.type)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}