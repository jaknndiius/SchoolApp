package io.github.jaknndiius.schoolapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(
    context: Context,
    NAME: String = "employee.db",
    VERSION: Int = 1
): SQLiteOpenHelper(context, NAME, null, VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        println("온크리에이트 호출!")

        val sql = ("create table if not exists emp("
                + " _id integer PRIMARY KEY autoincrement, "
                + " name text, "
                + " age integer, "
                + " mobile text)")
        db?.execSQL(sql)
    }

    override fun onOpen(db: SQLiteDatabase?) {
        println("온 오픈 호출됨!")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        println("온 업그레이드 호출됨: $oldVersion -> $newVersion")

        if(newVersion > 1) {
            db?.execSQL("DROP TABLE IF EXISTS emp")
        }
    }

    private fun println(data: String) {
        Log.d("데이터베이스 헬퍼", data)
    }

}