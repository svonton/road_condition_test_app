package com.example.road.roadtest



import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.Toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.compat.Place
import com.google.android.libraries.places.compat.ui.PlaceSelectionListener
import com.google.android.libraries.places.compat.ui.SupportPlaceAutocompleteFragment
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.roadalert.view.*
import java.io.File
import java.util.*
import kotlin.String as String1
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.popuprate.*
import kotlinx.android.synthetic.main.popuprate.view.*
import kotlinx.android.synthetic.main.startdrive.view.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    override fun onMarkerClick(p0: Marker?) = false


    private var lat1 = 0.0
    private var long1 = 0.0
    var r = 0.0
    var r1 = 0.0
    var fl = ""
    private val sourcePointsRT = ArrayList<ColoredPoint>()
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    var press = 0
    var layer = 0
    var z = 0f
    var roadrate = ""
    var count = 0
    var ratroad = ArrayList<kotlin.String>()
    companion object {
        var lat = 0.1
        var long = 0.1
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
        fun getlat(): Double {
            return this.lat
        }

        fun getlong(): Double {
            return this.long
        }

        var rt = false
        fun getRT(): Boolean {
            return this.rt
        }
    }

    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val dispFL = sensor_test_act.getDF()
        if (dispFL){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val autocompleteFragment =
                supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as SupportPlaceAutocompleteFragment
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(place.latLng.latitude,place.latLng.longitude),15f))

                // TODO: Get info about the selected place.
                // Log.i(FragmentActivity.TAG, "Place: " + place.name)
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                // Log.i(FragmentActivity.TAG, "An error occurred: " + status)
            }
        })
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                if (rt == true) {
                    lastLocation = p0.lastLocation
                    placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
                }
            }
        }
        createLocationRequest()
        ic_settings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        ic_layer.setOnClickListener {
            if (layer == 0) {
                mMap.clear()
                layer = 1
                ic_layer.setColorFilter(Color.parseColor("#FF0000"))
            } else if (layer == 1) {
                layer()
                layer = 0
                ic_layer.colorFilter = null
            }
        }
        val path = this.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        ic_gps.setOnClickListener {
            if(press == 0) {
                val view1 = LayoutInflater.from(this).inflate(R.layout.startdrive,null)
                val alertDialog = AlertDialog.Builder(this)
                        .setView(view1).setTitle("Start recording your drive?")
                val dialog = alertDialog.show()
                view1.ysbtn.setOnClickListener {
                    rt = true
                    press = 1
                    ic_gps.setColorFilter(Color.parseColor("#FF0000"))
                    dialog.dismiss()
                }
                view1.nobtnn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            else if (press == 1){
                rt = false
                press = 0
                ic_gps.colorFilter = null
                val view = LayoutInflater.from(this).inflate(R.layout.roadalert,null)
                val alertDialog = AlertDialog.Builder(this)
                        .setView(view).setTitle("Whats your wish?")
                val dialog = alertDialog.show()
                view.ybtn.setOnClickListener {
                    val rb = view.ratingBar.rating
                    if(rb==0f){
                        roadrate = "Unrated"
                    }else{
                        roadrate = "$rb"
                    }
                    File(letDirectory, "nots.txt").appendText("$roadrate|F|$roadrate|\n")
                    dialog.dismiss()
                }
                view.nbtn.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }

    private fun placeMarkerOnMap(location: LatLng) {
        fl = sensor_test_act.getFl()
        lat = location.latitude
        long = location.longitude
        if ((lat1 == 0.0) && (long1 == 0.0)) {
            lat1 = location.latitude
            long1 = location.longitude
        }
        if ((lat != lat1) && (long != long1)&&(layer == 0)) {
            val sourcePoints = ArrayList<LatLng>()
            sourcePoints.add(LatLng(lat1, long1))
            sourcePoints.add(LatLng(lat, long))
            val polyLineOptions = PolylineOptions()
            polyLineOptions.addAll(sourcePoints)
            polyLineOptions.width(15f)
            if (fl == "R") {
                polyLineOptions.color(Color.RED)
            } else if (fl == "G") {
                polyLineOptions.color(Color.parseColor("#008000"))
            } else if (fl == "Y") {
                polyLineOptions.color(Color.parseColor("#FF8C00"))
            }
            mMap.addPolyline(polyLineOptions)
            sourcePoints.clear()
            lat1 = lat
            long1 = long
        }

        z = mMap.cameraPosition.zoom
        if (press == 1) {
            if (15f > z)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastLocation.latitude, lastLocation.longitude), 15f))
            else
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastLocation.latitude, lastLocation.longitude), z))
        }
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                //mMap.isTrafficEnabled = true;
            }
        }
    }

    private fun startLocationUpdates() {
        //1
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }

    private fun createLocationRequest() {
        // 1
        locationRequest = LocationRequest()
        // 2
        locationRequest.interval = 1000
        // 3
        locationRequest.fastestInterval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        // 4
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        // 5
        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            // 6
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(this@MapsActivity,
                            REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }
    internal inner class ColoredPoint(var coords: LatLng, var color: Int)

    private fun showPolyline(points: List<ColoredPoint>) {
        if (points.size < 2)
            return
        var ix = 0
        var currentPoint = points.get(ix)
        var currentColor = currentPoint.color
        val currentSegment = ArrayList<LatLng>()
        currentSegment.add(currentPoint.coords)
        ix++
        while (ix < points.size) {
            currentPoint = points.get(ix)
            if (currentPoint.color == currentColor) {
                currentSegment.add(currentPoint.coords)
            } else {
                currentSegment.add(currentPoint.coords)
                mMap.addPolyline(PolylineOptions()
                        .addAll(currentSegment)
                        .color(currentColor)
                        .width(15f))
                currentColor = currentPoint.color
                currentSegment.clear()
                currentSegment.add(currentPoint.coords)
            }
            ix++
        }
       mMap.addPolyline(PolylineOptions()
                .addAll(currentSegment)
                .color(currentColor)
                .width(15f).clickable(true))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val nm = sensor_test_act.getNM()
        if (nm) {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.night_map))
        }
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.uiSettings.isCompassEnabled = false
        mMap.setOnMarkerClickListener(this)
        setUpMap()
        layer()

        mMap.setOnPolylineClickListener(object: GoogleMap.OnPolylineClickListener {
            override fun onPolylineClick(polyline:Polyline) {
                val q = polyline.getTag().toString().toInt()
                //Toast.makeText(this@MapsActivity, ratroad[q] as String, Toast.LENGTH_SHORT).show()
                val view = LayoutInflater.from(this@MapsActivity).inflate(R.layout.popuprate,null)
                val alertDialog = AlertDialog.Builder(this@MapsActivity)
                        .setView(view).setTitle("Road rating")
                if (ratroad[q] == "\nUnrated"){
                    view.poprate.setVisibility(View.INVISIBLE)
                    view.unratetext.setVisibility(View.VISIBLE)
                } else {
                    view.unratetext.setVisibility(View.INVISIBLE)
                    view.poprate.setVisibility(View.VISIBLE)
                    view.poprate.setRating(ratroad[q].toFloat())
                }
                val dialog = alertDialog.show()
            }
        })
    }

    private fun layer() {
        val fname = Engineering_menu.getFN()
        val path = this.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        val sourcePoints = ArrayList<ColoredPoint>()
        val a = File(letDirectory, "$fname").readText()
        val n = a.split("|")
        var l = 0
        var z = 1
        var f = 2
        val sourcePointsTR = ArrayList<LatLng>()


        for (i in 0 until n.size - 1) {
            if ((l <= n.size - 3) && (z <= n.size - 2) && (f <= n.size - 1)) {
                if(n[z] == "F"){
                    showPolyline(sourcePoints)
                    mMap.addPolyline(PolylineOptions().addAll(sourcePointsTR).color(Color.TRANSPARENT).clickable(true)).setTag("$count")
                    sourcePoints.clear()
                    sourcePointsTR.clear()
                    ratroad.add(n[l])
                    //Toast.makeText(this, "test", Toast.LENGTH_LONG).show()
                    count ++
                }
                else if((n[l] != "F")&&(n[z] != "F")&&(n[f] != "F"))
                {
                    r = n[l].toDouble()
                    r1 = n[z].toDouble()
                    if (n[f] == "R") {
                        sourcePoints.add(ColoredPoint(LatLng(r, r1), Color.RED))
                    } else if (n[f] == "G") {
                        sourcePoints.add(ColoredPoint(LatLng(r, r1), Color.parseColor("#008000")))
                    } else if (n[f] == "Y") {
                        sourcePoints.add(ColoredPoint(LatLng(r, r1), Color.parseColor("#FF8C00")))
                    }
                    sourcePointsTR.add(LatLng(r, r1))
                }
                l += 3
                z += 3
                f += 3
            }
        }
    }
}
