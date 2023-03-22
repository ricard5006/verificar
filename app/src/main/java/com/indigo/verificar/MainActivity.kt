package com.indigo.verificar

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.indigo.verificar.DbHelper.DbHelper
import org.apache.poi.hssf.usermodel.HeaderFooter.file
import org.apache.poi.ooxml.POIXMLException
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import kotlin.Exception


class MainActivity : AppCompatActivity() {
    
    private lateinit var btn_abrir_excel:Button
    private lateinit var tb_codigo_unico:EditText
    private lateinit var tb_codigo_barras:EditText
    private lateinit var btn_verificar:ImageButton
    private lateinit var progressBar:ProgressBar

    private lateinit var lb_rojo:TextView
    private lateinit var lb_verde:TextView


    private val CodigoBotonLinea = 5
    var ruta:String?=""
    var nArchivo:String?=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_abrir_excel = findViewById(R.id.btn_abrir_excel)
        tb_codigo_unico = findViewById(R.id.tb_codigo_unico)
        tb_codigo_barras = findViewById(R.id.tb_codigo_barras)
        btn_verificar = findViewById(R.id.btn_verificar)
        progressBar = findViewById(R.id.progressBar)

        lb_rojo = findViewById(R.id.lb_rojo)
        lb_verde = findViewById(R.id.lb_verde)


        obtener_nombre_archivo()

        btn_abrir_excel.setOnClickListener {
            val i = Intent(this, dialog_archivos::class.java)
            startActivityForResult(i,CodigoBotonLinea)

        }

        tb_codigo_barras.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->

            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP && textoVacio(tb_codigo_unico) && textoVacio(tb_codigo_barras)) {

                consultar_codigo(tb_codigo_unico.text.toString(),tb_codigo_barras.text.toString())

            }


            return@OnKeyListener false
        })

        btn_verificar.setOnClickListener{

            if(textoVacio(tb_codigo_unico) && textoVacio(tb_codigo_barras)) {

            consultar_codigo(tb_codigo_unico.text.toString(),tb_codigo_barras.text.toString())

        }
        }


    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //archivo plano
        if(resultCode == RESULT_OK){
            if(requestCode == CodigoBotonLinea){

                ruta = data!!.getStringExtra("ruta")
                nArchivo = data!!.getStringExtra("archivo")

                cargar_archivo().execute()


            }

        }

    }

    inner class cargar_archivo: AsyncTask<Void, Void, String>(){
        override fun doInBackground(vararg p0: Void?): String {

            return abrir_archivos()
        }

        override fun onPreExecute() {
            progressBar.visibility = View.VISIBLE
            tb_codigo_unico.isEnabled = false
            tb_codigo_barras.isEnabled = false

        }

        override fun onPostExecute(result: String?) {
            btn_abrir_excel.setText(result)
            progressBar.visibility = View.GONE
            tb_codigo_unico.isEnabled = true
            tb_codigo_barras.isEnabled = true
            tb_codigo_unico.requestFocus()

        }


    }

    private fun abrir_archivos():String {
        var msj = "..."
        if (nArchivo != null && nArchivo != "") {

            val dbHelper = DbHelper(this)
            dbHelper.deleteData()


            var ruta = "/sdcard" + ruta + nArchivo

            val inputStream = FileInputStream(ruta)

            if (nArchivo!!.contains(".xlsx")) {


                try {

                    val xlsxWb: Workbook = XSSFWorkbook(ruta)
                    val dataFormatter = DataFormatter()
                    val sheet = xlsxWb.getSheetAt(0)
                    sheet.forEach { row ->

                        val campo1 = dataFormatter.formatCellValue(row.getCell(0))
                        val campo2 = dataFormatter.formatCellValue(row.getCell(1))
                        val campo3 = dataFormatter.formatCellValue(row.getCell(2))
                        guardar_datos(campo1, campo2, campo3, nArchivo!!)

                        println()
                    }

                } catch (ex: Exception) {
                    Log.e("XLSX" ,"$ex")
                    return "XLSX " + ex.message.toString()
                }

            }
            else {

                try {

                    var xlsWb = WorkbookFactory.create(inputStream)
                    val dataFormatter = DataFormatter()
                    val sheet = xlsWb.getSheetAt(0)
                    sheet.forEach { row ->

                        val campo1 = dataFormatter.formatCellValue(row.getCell(0))
                        val campo2 = dataFormatter.formatCellValue(row.getCell(1))
                        val campo3 = dataFormatter.formatCellValue(row.getCell(2))
                        guardar_datos(campo1, campo2, campo3, nArchivo!!)

                    }

                        msj = nArchivo.toString()


                } catch (ex: Exception) {
                    Log.e("XLS" ,"$ex")
                    return "XLS" + ex.message.toString()
                }

            }


        }

        return msj
    }

    private fun guardar_datos(campo1: String, campo2: String, campo3: String,campo4:String) {

        try {
            val dbHelper = DbHelper(this)

            dbHelper.insertData(campo1, campo2, campo3, campo4)
        }catch (ex:Exception){
            Log.e("guardar_datos","$ex")
        }

    }

    private fun obtener_nombre_archivo(){
        val dbHelper = DbHelper(this)
        nArchivo = dbHelper.selectData_nombre()
        if(nArchivo != null && nArchivo != "") {
            btn_abrir_excel.setText(nArchivo)
        }

    }

    private fun consultar_codigo(codigoUnico:String,codigoBarras: String) {

        try {
            val dbHelper = DbHelper(this)
            val data = dbHelper.selectData(codigoUnico, codigoBarras)
            if(data[1]!="" && data[1]!=null ){
                lb_rojo.visibility = View.GONE
                lb_verde.visibility = View.VISIBLE
                lb_verde.text = data[0] + "\n" + data[1] + "\n" + data[2]
                tb_codigo_barras.setText("")
                tb_codigo_barras.requestFocus()
            }else{
                lb_verde.visibility = View.GONE
                lb_rojo.visibility = View.VISIBLE
                lb_rojo.text = codigoBarras
                tb_codigo_barras.setText("")
                tb_codigo_barras.requestFocus()
            }
        }catch (ex:Exception){
            Toast.makeText(this,"${ex.message}",Toast.LENGTH_LONG).show()
            Log.e("consultar_codigo","$ex")
        }
    }


    private fun textoVacio(txt: EditText): Boolean {
        if (txt.text.toString().trim { it <= ' ' } == "") {
            txt.error = "Campo obligatorio"
            return false
        } else {
            txt.error = null
        }
        return true
    }

}