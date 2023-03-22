package com.indigo.verificar.DbHelper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper



class DbHelper(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VER)
{
    companion object {
        private val DATABASE_VER = 1
        private val DATABASE_NAME = "indigo.db"

    }

    override fun onCreate(db: SQLiteDatabase?) {

        //al instalar la app, se crea una BD con la tabla configuracion que tiene toda la config de la app

        try {

            val CREATE_TABLE_CONFIG: String =
                ("CREATE TABLE configuracion (parametro TEXT PRIMARY KEY,valor TEXT)")

            db!!.execSQL(CREATE_TABLE_CONFIG);

            val CREATE_TABLE_DATA: String =
                ("CREATE TABLE data"+
                 "(id INTEGER PRIMARY KEY AUTOINCREMENT"+
                        " ,campo1 TEXT" +
                        " ,campo2 TEXT" +
                        " ,campo3 TEXT" +
                        " ,campo4 TEXT" +
                        " )")

            db!!.execSQL(CREATE_TABLE_DATA);





        } catch (ex: Exception) {


        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS configuracion")
        onCreate(db!!)
        db!!.execSQL("DROP TABLE IF EXISTS data")
        onCreate(db!!)



    }



    /**
     * FUNCIONES QUERY para los parametros de configuracion de la aplicacion

    fun updateParametro(param: Parametros): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("parametro", param.parametro)
        values.put("valor", param.valor)
        var sucess =  db.replace("CONFIGURACION", null, values)

        db.close()
        return sucess
    }

    fun selectParametro(objParam: Parametros):String {
        var dato:String=""
        val selectQuery = "SELECT * FROM configuracion WHERE parametro = ?"
        val db = this.writableDatabase

        val cursor = db.rawQuery(selectQuery, arrayOf(objParam.parametro.toString()))
        if (cursor.moveToFirst()) {

            do {
                // val parametro = Parametros()
                // parametro.parametro = cursor.getString(cursor.getColumnIndex("parametro"))
                dato = cursor.getString(cursor.getColumnIndex("valor"))


            } while (cursor.moveToNext())

        }
        cursor.close()
        db.close()
        return dato



    }
     */
    /**
     * FUNCIONES QUERY para guardar los datos
     */

    fun insertData(campo1:String,campo2:String,campo3:String,campo4:String):Long{

        val db = this.writableDatabase
        val values = ContentValues()
        values.put("campo1",campo1.trim())
        values.put("campo2",campo2.trim())
        values.put("campo3",campo3.trim())
        values.put("campo4",campo4.trim())


        return db.insert("data", null, values)


    }

    @SuppressLint("Range")
    fun selectData(campo1:String,campo3: String): Array<String?> {
        val vecData = arrayOfNulls<String>(3)
        try{

        val selectQuery = "SELECT * FROM data where campo1='$campo1' and campo3='$campo3'"
        val db = this.writableDatabase

        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {

            do {
                vecData[0] =  cursor.getString(cursor.getColumnIndex("campo1"))
                vecData[1] =  cursor.getString(cursor.getColumnIndex("campo2"))
                vecData[2] =  cursor.getString(cursor.getColumnIndex("campo3"))

            } while (cursor.moveToNext())

        }

            cursor.close()
            db.close()

        }catch (e:Exception){

        }
        return vecData
    }

    fun deleteData():Long{
        val db = this.writableDatabase

        var _success = db.delete("data",null,null).toLong()

        db.close()
        return _success
    }

    @SuppressLint("Range")
    fun selectData_nombre():String {
        var nArchivo=""
        try{

            val selectQuery = "select campo4 from data GROUP by campo4"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {

                do {

                    nArchivo = cursor.getString(cursor.getColumnIndex("campo4"))

                } while (cursor.moveToNext())

            }
            cursor.close()
            db.close()

        }catch (e:Exception){

        }
        return nArchivo
    }


}