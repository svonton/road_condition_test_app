package com.example.road.roadtest

import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_settings.*
import android.R.id.edit
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.view.WindowManager
import android.widget.Toast
import java.io.File


class SettingsActivity : AppCompatActivity() {


    val SHARED_PREFS = "sharedPrefs"


    private var switchOnOff1: Boolean = false
    private var switchOnOff2: Boolean = false
    private var switchOnOff3: Boolean = false
    private var switchOnOff4: Boolean = false
    private var switchOnOff5: Boolean = false
    val SWITCH1 = "holder"
    val SWITCH2 = "pocket"
    val SWITCH3 = "free"
    val SWITCH4 = "dispaly"
    val SWITCH5 = "nightmode"

    companion object {
        var accurl = sensor_test_act.getAL()
        var accurh = sensor_test_act.getAH()
        var dispFL = sensor_test_act.getDF()
        var nm = sensor_test_act.getNM()
        var update = 0
        fun upd():Int{
            return  this.update
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        var i = 0
        dispFL = sensor_test_act.getDF()
        if (dispFL){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        creators.setOnClickListener {
            i += 1
            if (i > 5) {
                val intent = Intent(this, Engineering_menu::class.java)
                startActivity(intent)
            }
        }
        loadData()
        updateData()
        if (holder.isChecked) {
            accurl = 1
            accurh = 3
        } else if (pocket.isChecked){
            accurl = 3
            accurh = 5
        } else if (free.isChecked){
            accurl = 4
            accurh = 7
        }
        if (dispaly.isChecked){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            dispFL = true
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            dispFL = false
        }
        if (nightmode.isChecked){
            nm = true
            settingconslayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDarkMode))
        }else{
            nm = false
            settingconslayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        }

        val path = this.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")

        holder.setOnClickListener {
            if (holder.isChecked) {
                accurl = 1
                accurh = 3
                pocket.isChecked = false
                free.isChecked = false
                holder.isClickable=false
                pocket.isClickable=true
                free.isClickable=true
                saveData()
                File(letDirectory, "Settings.txt").writeText("|$accurl|$accurh|$dispFL|$nm|")
                update = 1
            }
        }
        pocket.setOnClickListener {
            if (pocket.isChecked) {
                accurl = 3
                accurh = 5
                holder.isChecked = false
                free.isChecked = false
                holder.isClickable=true
                pocket.isClickable=false
                free.isClickable=true
                saveData()
                File(letDirectory, "Settings.txt").writeText("|$accurl|$accurh|$dispFL|$nm|")
                update = 1
            }
        }
        free.setOnClickListener {
            if (free.isChecked) {
                accurl = 4
                accurh = 7
                holder.isChecked = false
                pocket.isChecked = false
                holder.isClickable=true
                pocket.isClickable=true
                free.isClickable=false
                saveData()
                File(letDirectory, "Settings.txt").writeText("|$accurl|$accurh|$dispFL|$nm|")
                update = 1
            }
        }
        dispaly.setOnClickListener {
            if (dispaly.isChecked){
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                dispFL = true
            }else{
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                dispFL = false
            }
            saveData()
            File(letDirectory, "Settings.txt").writeText("|$accurl|$accurh|$dispFL|$nm|")
            update = 1
        }
        nightmode.setOnClickListener {
            if (nightmode.isChecked){
                nm = true

            }else{
                nm = false
            }
            saveData()
            Toast.makeText(this, "To apply restart map", Toast.LENGTH_LONG).show()
            File(letDirectory, "Settings.txt").writeText("|$accurl|$accurh|$dispFL|$nm|")
            update = 1
        }
    }

    private fun saveData(){
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putBoolean(SWITCH1,holder.isChecked)
        editor.putBoolean(SWITCH2,pocket.isChecked)
        editor.putBoolean(SWITCH3,free.isChecked)
        editor.putBoolean(SWITCH4,dispaly.isChecked)
        editor.putBoolean(SWITCH5,nightmode.isChecked)
        editor.apply()
    }
    private fun loadData(){
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        switchOnOff1 = sharedPreferences.getBoolean(SWITCH1, true)
        switchOnOff2 = sharedPreferences.getBoolean(SWITCH2, false)
        switchOnOff3 = sharedPreferences.getBoolean(SWITCH3, false)
        switchOnOff4 = sharedPreferences.getBoolean(SWITCH4, true)
        switchOnOff5 = sharedPreferences.getBoolean(SWITCH5, false)
    }
    private fun updateData(){
        holder.setChecked(switchOnOff1)
        pocket.setChecked(switchOnOff2)
        free.setChecked(switchOnOff3)
        dispaly.setChecked(switchOnOff4)
        nightmode.setChecked(switchOnOff5)
    }
}

