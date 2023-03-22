package com.indigo.verificar

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

class dialog_archivos : AppCompatActivity() {

    var lsRutas = ArrayList<String> ()
    var item = ArrayList<String> ()
    private lateinit var listArchivos: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_archivos)
        listArchivos = findViewById(R.id.listArchivos)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Verifica permisos para Android 6.0+
            checkExternalStoragePermission()
        }

        cargarLista("")
    }

    private fun cargarLista(rutaAnterior: String) {
        if (rutaAnterior !== "") {
            lsRutas.add(rutaAnterior)
        }
        item = java.util.ArrayList()
        var ruta = "/"
        for (i in lsRutas.indices) {
            ruta += lsRutas[i]
        }
        val f = File(Environment.getExternalStoragePublicDirectory("/"), ruta)
        //File f = new File(Environment.getExternalStorageDirectory() + "/WN Finances/");
        val files = f.listFiles()
        item.add("./")
        try {
            for (i in files.indices) {
                val file = files[i]
                if (file.isDirectory) item.add(file.name + "/") else {
                    if (file.name.contains(".xls") || file.name.contains(".xlsx")) {
                        item.add(file.name)
                    }
                }
            }
        } catch (ex: Exception) {
            val z = ""
        }

        // Localizamos y llenamos la lista

        listArchivos.adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,item)


        // Accion para realizar al pulsar sobre la lista
        listArchivos.onItemClickListener =
            AdapterView.OnItemClickListener { a, v, position, id ->
                if (position == 0) {
                    if (lsRutas.size > 0) {
                        lsRutas.removeAt(lsRutas.size - 1)
                        cargarLista("")
                    } else {
                        val i = Intent()
                        i.putExtra("ruta", "")
                        i.putExtra("archivo", "")
                        setResult(RESULT_OK, i)
                        finish()

                    }
                } else {
                    if (item[position].contains("/")) {
                        cargarLista(item[position])
                    } else {
                        retornaRuta(position)
                    }
                }
            }
    }

    private fun retornaRuta(position: Int) {
        var ruta = "/"

        for (i in lsRutas.indices) {
            ruta += lsRutas[i]
        }

        if(position > -1){

            val i = Intent(this, MainActivity::class.java)

            i.putExtra("ruta", ruta)
            i.putExtra("archivo", item.get(position))
            setResult(RESULT_OK, i)
            finish()



        }
    }

    private fun checkExternalStoragePermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para leer.")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                225
            )
        } else {
            Log.i("Mensaje", "Se tiene permiso para leer!")

        }
    }

}