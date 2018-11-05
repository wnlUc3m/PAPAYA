/* Copyright 2018 Adolfo "captainepoch" Santiago
 *
 * This file is a part of PAPAYA.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * Tusky is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Tusky; if not,
 * see <http://www.gnu.org/licenses>.
 */

package com.wnluc3m.papaya.Database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Environment
import android.provider.BaseColumns
import android.widget.Toast
import com.ajts.androidmads.sqliteimpex.SQLiteImporterExporter
import com.wnluc3m.papaya.Helpers.LogDebug
import java.text.SimpleDateFormat
import java.util.*


class SQLiteHandler private constructor(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

	private val logTag = this.javaClass.name
	private val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
		Locale("ES", "es"))

	init {
		LogDebug.d(logTag, "Constructor SQLiteHandler")
		LogDebug.d(logTag, context.getDatabasePath(DATABASE_NAME).path)
	}

	override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
		val sql = String.format(
			"CREATE TABLE " + SQLiteHandlerData.TABLE_NAME + " (" +
				SQLiteHandlerData.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				SQLiteHandlerData.COLUMN_NAME_PROVIDER + " TEXT NOT NULL, " +
				SQLiteHandlerData.COLUMN_NAME_LATITUDE + " REAL NOT NULL, " +
				SQLiteHandlerData.COLUMN_NAME_LONGITUDE + " REAL NOT NULL, " +
				SQLiteHandlerData.COLUMN_NAME_TIMESTAMP + " DATETIME NOT NULL, " +
				SQLiteHandlerData.COLUMN_NAME_ACCURACY + " REAL NOT NULL);")
		sqLiteDatabase.execSQL(sql)
		LogDebug.d(logTag, "DB Created")
	}

	override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SQLiteHandlerData.TABLE_NAME)
		onCreate(sqLiteDatabase)
		LogDebug.d(logTag, "onUpgrade()")
	}

	fun getData(daysSpan: Int): Array<LocationPoint?> {
		val sb = StringBuilder()
		sb.append("SELECT * FROM ").append(SQLiteHandlerData.TABLE_NAME)
		if (daysSpan > 0) {
			sb.append(" WHERE ").append(SQLiteHandlerData.COLUMN_NAME_TIMESTAMP)
				.append(" > datetime('now','-").append(daysSpan).append(" day')")
		}
		LogDebug.d(logTag, "Query getData: " + sb.toString())
		val readableDatabase = sqliteHandlerInstance!!.readableDatabase
		val cursor = readableDatabase.rawQuery(sb.toString(), null)
		val locationData = arrayOfNulls<LocationPoint>(cursor.count)
		LogDebug.d(logTag, "CURSOR COUNT: " + cursor.count.toString())
		for (i in 0 until cursor.count) {
			cursor.moveToPosition(i)
			locationData[i] = LocationPoint(cursor.getColumnIndex(SQLiteHandlerData.UID),
				cursor.getString(cursor.getColumnIndex(SQLiteHandlerData
					.COLUMN_NAME_PROVIDER)),
				cursor.getDouble(cursor.getColumnIndex(SQLiteHandlerData
					.COLUMN_NAME_LATITUDE)),
				cursor.getDouble(cursor.getColumnIndex(SQLiteHandlerData
					.COLUMN_NAME_LONGITUDE)),
				cursor.getString(cursor.getColumnIndex(SQLiteHandlerData
					.COLUMN_NAME_TIMESTAMP)),
				cursor.getFloat(cursor.getColumnIndex(SQLiteHandlerData.COLUMN_NAME_ACCURACY))
			)
		}
		cursor.close()
		return locationData
	}

	fun exportFullDB() {
		val sqlExporter = SQLiteImporterExporter(context, DATABASE_NAME)
		sqlExporter.setOnExportListener(object : SQLiteImporterExporter.ExportListener {
			override fun onSuccess(message: String) {
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
			}

			override fun onFailure(exception: Exception) {
				Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
			}
		})
		try {
			sqlExporter.exportDataBase(Environment.getExternalStorageDirectory().toString() +
				"/PAPAYA/")
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	fun importFullDB() {
		val sqlExporter = SQLiteImporterExporter(context, DATABASE_NAME)
		// Listeners for Import and Export DB
		sqlExporter.setOnImportListener(object : SQLiteImporterExporter.ImportListener {
			override fun onSuccess(message: String) {
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
			}

			override fun onFailure(exception: Exception) {
				Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
			}
		})
		try {
			sqlExporter.importDataBase(Environment.getExternalStorageDirectory().toString() +
				"/PAPAYA/")
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	/*fun exportDataToTxt() {
		val data = getData(1)
		val filePath = File(Environment.getExternalStorageDirectory(), "PAPAYA")
		if (!filePath.exists()) {
			val dirCreated = filePath.mkdir()
			if (!dirCreated) {
				LogDebug.i(logTag, "Dir. was already created")
			}
		}
		val file = File(filePath, "PAPAYA_DUMP.txt")
		try {
			val fw = FileWriter(file)
			for (d in data) {
				fw.append(d!!.provider).append("\t").append(d.latitude.toString())
					.append("\t").append(d.longitude.toString()).append("\t").append(d.time).append("\n")
			}
			fw.flush()
			fw.close()
		} catch (e: IOException) {
			e.printStackTrace()
		}
		Toast.makeText(context, "Exported", Toast.LENGTH_LONG).show()
	}*/

	fun eraseData() {
		if (sqliteHandlerInstance != null) {
			val db = sqliteHandlerInstance!!.writableDatabase
			db.execSQL("DELETE FROM " + SQLiteHandlerData.TABLE_NAME)
			LogDebug.d(logTag, "Data erased")
			Toast.makeText(context, "Data erased", Toast.LENGTH_LONG).show()
		}
	}

	fun addData(
		provider: String, latitude: Double, longitude: Double, time: Long,
		accuracy: Float) {
		val db = sqliteHandlerInstance!!.readableDatabase
		val contentValues = ContentValues()
		contentValues.put(SQLiteHandlerData.COLUMN_NAME_PROVIDER, provider)
		contentValues.put(SQLiteHandlerData.COLUMN_NAME_LATITUDE, latitude)
		contentValues.put(SQLiteHandlerData.COLUMN_NAME_LONGITUDE, longitude)
		contentValues.put(SQLiteHandlerData.COLUMN_NAME_TIMESTAMP,
			simpleDateFormat.format(time))
		contentValues.put(SQLiteHandlerData.COLUMN_NAME_ACCURACY, accuracy)
		if (db.insert(SQLiteHandlerData.TABLE_NAME, null, contentValues) < 0) {
			LogDebug.d(logTag, "Data not inserted")
		} else {
			LogDebug.d(logTag, "Data inserted")
		}
	}

	class SQLiteHandlerData : BaseColumns {
		companion object {
			internal val TABLE_NAME = "papayadata"
			internal val UID = "UID"
			internal val COLUMN_NAME_PROVIDER = "provider"
			internal val COLUMN_NAME_LATITUDE = "latitude"
			internal val COLUMN_NAME_LONGITUDE = "longitude"
			internal val COLUMN_NAME_TIMESTAMP = "time"
			internal val COLUMN_NAME_ACCURACY = "accuracy"
		}
	}

	companion object {

		@SuppressLint("StaticFieldLeak")
		private var sqliteHandlerInstance: SQLiteHandler? = null
		private val DATABASE_VERSION = 1
		private val DATABASE_NAME = "PapayaData.db"

		@Synchronized
		fun getInstance(context: Context): SQLiteHandler {
			if (sqliteHandlerInstance == null) {
				sqliteHandlerInstance = SQLiteHandler(context.applicationContext)
			}
			return sqliteHandlerInstance as SQLiteHandler
		}
	}
}
