package com.example.road.roadtest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_engineering_menu.*
import java.io.File

class Engineering_menu : AppCompatActivity() {

    companion object {
        private const val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1
        var fname = "nots.txt"
        fun getFN(): String {
            return this.fname
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_engineering_menu)

        val nm = sensor_test_act.getNM()
        if(nm){
            engineerlayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDarkMode))
        }else{
            engineerlayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        }

        val path = this.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        val file = File(letDirectory, "nots.txt")
        applybtn.setOnClickListener {
            var FN = FNin2.text.toString()
            var FND = File(letDirectory, "$FN")
            if (FND.exists()){
                fname = FN
                Toast.makeText(this, "File changed", Toast.LENGTH_LONG).show()}
            else
                Toast.makeText(this, "File doesn't exists", Toast.LENGTH_LONG).show()
        }
        stp_btn.setOnClickListener{
            var newfile = FNin.text.toString()
            if (letDirectory.exists())
            {
                val from = File(letDirectory, "nots.txt")
                val to = File(letDirectory, "$newfile.txt")
                if (from.exists())
                    from.renameTo(to)
                file.createNewFile()
                Toast.makeText(this, "File renamed", Toast.LENGTH_LONG).show()
            }
        }
        delbtn.setOnClickListener {
            file.delete()
            file.createNewFile()
            Toast.makeText(this, "File reCreated", Toast.LENGTH_LONG).show()
        }
        trybtn.setOnClickListener {
            val curacc = sensor_test_act.getCA()
            val curdif = sensor_test_act.getCD()
            accval.text = curacc
            difval.text = curdif
        }
        trycordbtn.setOnClickListener {
            val d = MapsActivity.getlat()
            val w = MapsActivity.getlong()
            val curcords = "  $d $w  "
            coordstext.text = curcords
        }
    }
}
