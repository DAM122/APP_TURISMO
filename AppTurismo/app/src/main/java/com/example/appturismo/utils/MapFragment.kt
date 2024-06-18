package com.example.appturismo.utils

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.appturismo.model.MonumentoResponse
import com.example.appturismo.service.GeocodingApi
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MapFragment : SupportMapFragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private var monumento: MonumentoResponse? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private var isLocationPermissionGranted = false
    private lateinit var userLocation: LatLng
    private lateinit var monumentLocation: LatLng

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)
        monumento = arguments?.getParcelable(ARG_MONUMENTO)
        getMapAsync(this)
        return rootView
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        monumento?.let {
            Log.d("MapFragment", "Nombre del monumento: ${it.nombre}")
            fetchMonumentLocation(it.nombre)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isLocationPermissionGranted) {
            fetchLastLocation()
        } else {
            checkLocationPermission()
        }
    }

    private fun fetchMonumentLocation(monumentName: String) {
        val context = requireContext()
        val apiKey = getApiKey(context)

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        val geocodingApi = retrofit.create(GeocodingApi::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = geocodingApi.getCoordinates(monumentName, apiKey)
                if (response.status == "OK") {
                    val location = response.results.firstOrNull()?.geometry?.location
                    location?.let {
                        monumentLocation = LatLng(it.lat, it.lng)
                        withContext(Dispatchers.Main) {
                            googleMap.addMarker(MarkerOptions().position(monumentLocation).title(monumentName))
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(monumentLocation, 15f))
                        }

                        // Una vez obtenida la ubicación del monumento, obtener la ubicación del usuario
                        fetchLastLocation()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getApiKey(context: Context): String {
        val ai = context.packageManager.getApplicationInfo(context.packageName, android.content.pm.PackageManager.GET_META_DATA)
        val bundle = ai.metaData
        return bundle.getString("com.google.android.geo.API_KEY") ?: throw IllegalStateException("API_KEY not found in manifest")
    }

    companion object {
        private const val ARG_MONUMENTO = "arg_monumento"

        @JvmStatic
        fun newInstance(monumento: MonumentoResponse): MapFragment {
            val fragment = MapFragment()
            val args = Bundle()
            args.putParcelable(ARG_MONUMENTO, monumento)
            fragment.arguments = args
            return fragment
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionGranted = true
            fetchLastLocation()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun fetchLastLocation() {
        val context = requireContext()
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        // Verificar si el GPS está activado
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS desactivado, mostrar diálogo para activarlo
            AlertDialog.Builder(context)
                .setTitle("Ubicación desactivada")
                .setMessage("Para obtener tu ubicación actual, activa el GPS.")
                .setPositiveButton("Activar") { _, _ ->
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton("Cancelar", null)
                .show()
            return
        }

        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        userLocation = LatLng(location.latitude, location.longitude)
                        val markerOptions = MarkerOptions().position(userLocation).title("Mi ubicación")
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)) // Cambiar color a azul
                        googleMap.addMarker(markerOptions)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))

                        // Dibujar la ruta entre la ubicación del usuario y el monumento
                        if (::monumentLocation.isInitialized) {
                            drawRoute(userLocation, monumentLocation)
                        } else {
                            Log.e("MapFragment", "monumentLocation no inicializado")
                        }
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    // Manejar el caso en que no se pudo obtener la ubicación
                }
        } catch (e: SecurityException) {
            e.printStackTrace()
            // Manejar explícitamente la excepción de permisos denegados
        }
    }

    private fun drawRoute(origin: LatLng, destination: LatLng) {
        val geoApiContext = GeoApiContext.Builder()
            .apiKey(getApiKey(requireContext()))
            .build()

        val directionsApiRequest = DirectionsApi.newRequest(geoApiContext)
            .origin(com.google.maps.model.LatLng(origin.latitude, origin.longitude))
            .destination(com.google.maps.model.LatLng(destination.latitude, destination.longitude))

        try {
            val directionsResult: DirectionsResult = directionsApiRequest.await()

            // Decodificar la ruta polyline y dibujarla en el mapa
            CoroutineScope(Dispatchers.Main).launch {
                val decodedPath = decodePolyline(directionsResult.routes[0].overviewPolyline.decodePath())
                val polylineOptions = PolylineOptions().addAll(decodedPath).color(Color.BLUE).width(10f)
                googleMap.addPolyline(polylineOptions)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun decodePolyline(encodedPath: List<com.google.maps.model.LatLng>): List<LatLng> {
        val poly = ArrayList<LatLng>()
        for (latLng in encodedPath) {
            poly.add(LatLng(latLng.lat, latLng.lng))
        }
        return poly
    }
}