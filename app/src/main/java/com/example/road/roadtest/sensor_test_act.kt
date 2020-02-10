package com.example.road.roadtest

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sensor_test_act.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.abs

@SuppressLint("ByteOrderMark")
class sensor_test_act : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    lateinit var accselerom: Sensor
    lateinit var gyrosc: Sensor
    private var xVal = 0f
    private var yVal = 0f
    private var zVal = 0f
    private var xVal_zer = 0f
    private var yVal_zer = 0f
    private var zVal_zer = 0f
    private var xx_dif = 0f
    private var yy_dif = 0f
    private var zz_dif = 0f
    var d = 0.2
    var w = 0.2
    var d1 = 0.2
    var w1 = 0.2
    var accurl = 1
    var accurh = 3
    var rt = false
    var update = 1

    companion object {
        private const val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1
        var flag = ""
        fun getFl(): String {
            return this.flag
        }
        var accurl = 1
        var accurh = 3
        var DispFl = true
        var nm = false
        fun getAL(): Int {
            return this.accurl
        }
        fun getAH(): Int {
            return this.accurh
        }
        fun getDF(): Boolean {
            return this.DispFl
        }
        fun getNM(): Boolean {
            return this.nm
        }
        var stringValueacc = ""
        fun getCA(): String {
            return this.stringValueacc
        }
        var curDif = ""
        fun getCD(): String {
            return this.curDif
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_test_act)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accselerom = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyrosc = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        setupPermissions()
        Timer().schedule(3000, 3000) {
            xVal_zer = xVal
            yVal_zer = yVal
            zVal_zer = zVal
        }
        button.setOnClickListener{
            val intent = Intent(this,MapsActivity::class.java)
            startActivity(intent)
        }
        val path = this.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        letDirectory.mkdirs()
        val file = File(letDirectory, "nots.txt")
        val fileSet = File(letDirectory, "Settings.txt")
        if (!file.exists()) {
            file.createNewFile()
        }
        if (!fileSet.exists()) {
            fileSet.createNewFile()
            File(letDirectory, "Settings.txt").writeText("|1|3|true|false|")
        }
        Timer().schedule(5000, 5000) {
            rt = MapsActivity.getRT()
            if (rt){
                d=MapsActivity.getlat()
                w=MapsActivity.getlong()
                if ((w!=0.1)&&(d!=0.1)&&(w!=0.2)&&(d!=0.2)&&(w!=w1)&&(d!=d1)){
                    File(letDirectory, "nots.txt").appendText("$d|$w|$flag|\n")
                    d1=d
                    w1=w
                }
            }
        }
    }
    private fun setupPermissions() {
        val permission1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permission2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        if ((permission1 != PackageManager.PERMISSION_GRANTED)||(permission2 != PackageManager.PERMISSION_GRANTED)) {
            makeRequest()
        }
    }
    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION),
                WRITE_EXTERNAL_STORAGE_REQUEST_CODE)
    }
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this,accselerom , SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, gyrosc, SensorManager.SENSOR_DELAY_NORMAL)
    }
    override fun onSensorChanged(sensorEvent: SensorEvent) {
        if (sensorEvent.sensor === accselerom) {
            stringValueacc = "    X:"+ sensorEvent.values[0].toString() + "    \n" + "    Y:"+ sensorEvent.values[1].toString() + "    \n" + "    Z:" + sensorEvent.values[2].toString() + "    "
            getAccelerometer(sensorEvent)
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
    private fun getAccelerometer(event: SensorEvent) {
        if(update == 1) {
            val path = this.getExternalFilesDir(null)
            val letDirectory = File(path, "LET")
            val a = File(letDirectory, "Settings.txt").readText()
            val n = a.split("|")
            accurl = n[1].toInt()
            accurh = n[2].toInt()
            DispFl = n[3].toBoolean()
            nm = n[4].toBoolean()
            if(nm){
                sensortestlayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDarkMode))
            }else{
                sensortestlayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
            }
            if (DispFl){
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }else{
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            update = 0
        }
        update = SettingsActivity.upd()
        xVal = event.values[0]
        yVal = event.values[1]
        zVal = event.values[2]
        var x_dif = abs(xVal_zer) - abs(xVal)
        var y_dif = abs(yVal_zer) - abs(yVal)
        var z_dif = abs(zVal_zer) - abs(zVal)
        xx_dif = abs(x_dif)
        yy_dif = abs(y_dif)
        zz_dif = abs(z_dif)
        if (xx_dif >accurh || yy_dif >accurh || zz_dif>accurh) {
            flag = "R"

        }else if ((xx_dif <accurh && xx_dif >accurl)||(yy_dif <accurh && yy_dif >accurl)||(zz_dif <accurh && zz_dif >accurl)){
            flag = "Y"

        }else if  (xx_dif <accurl||yy_dif <accurl||zz_dif <accurl){
            /*relative.setBackgroundColor(Color.GREEN)*/
            flag = "G"
        }
        curDif = "    X:$xx_dif    \n    Y:$yy_dif    \n    Z:$zz_dif    "
    }
}
