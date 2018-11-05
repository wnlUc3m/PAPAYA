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

package com.wnluc3m.papaya.Services

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.wnluc3m.papaya.Database.LocationPoint
import com.wnluc3m.papaya.Database.SQLiteHandler
import com.wnluc3m.papaya.Helpers.LogDebug
import com.wnluc3m.papaya.Preferences.PrefManager
import com.wnluc3m.papaya.R

class LocationServiceNative : Service() {

	private val logTag = this.javaClass.name
	private var locationManager: LocationManager? = null
	private var locationServiceListener: LocationServiceListener? = null
	private var db: SQLiteHandler? = null
	private val serviceBinder = LocalBinder()
	private var pm: PrefManager? = null
	private var latitude: Double = 0.toDouble()
	private var longitude: Double = 0.toDouble()

	val data: Array<LocationPoint?>?
		get() = db?.getData(pm!!.timeSpanPoints)

	init {
		LogDebug.i(logTag, "Constructor LocationServiceNative()")
	}

	inner class LocalBinder : Binder() {
		val service: LocationServiceNative
			get() = this@LocationServiceNative
	}

	override fun onBind(intent: Intent): IBinder? {
		LogDebug.i(logTag, "service onBind")
		return serviceBinder
	}

	override fun onRebind(intent: Intent) {
		LogDebug.i(logTag, "service onRebind")
		super.onRebind(intent)
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		if (ActivityCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager
				.PERMISSION_GRANTED) {
			Toast.makeText(applicationContext, applicationContext.getString(R.string.error_permissions),
				Toast.LENGTH_LONG).show()
			LogDebug.e(logTag, "Not permission")
			return Service.START_NOT_STICKY
		}
		pm = PrefManager.getInstance(this)
		var prefLocationMTI: Long = -1
		var prefLocationMDI = -1f
		try {
			prefLocationMTI = pm!!.locationMTI
			prefLocationMDI = pm!!.locationMDI
		} catch (ex: NumberFormatException) {
			LogDebug.e(logTag, "No default settings")
		}

		if (prefLocationMTI == -1L || prefLocationMDI == -1f) {
			LogDebug.e(logTag, "START_NOT_STICKY")
			return Service.START_NOT_STICKY
		}

		LogDebug.d(logTag, "prefLocationMTI: " + prefLocationMTI + ", prefLocationMDI: " +
			prefLocationMDI)
		locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
		locationServiceListener = LocationServiceListener()
		locationManager!!.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
			prefLocationMTI, prefLocationMDI, locationServiceListener)
		db = SQLiteHandler.getInstance(this)
		if (db == null) {
			LogDebug.e(logTag, "db NULL")
		}
		super.onStartCommand(intent, flags, startId)
		LogDebug.d(logTag, "START_STICKY")
		return Service.START_STICKY
	}

	override fun onDestroy() {
		super.onDestroy()
		LogDebug.d(logTag, "onDestroy() executed")
		if (locationManager != null) {
			locationManager!!.removeUpdates(locationServiceListener)
		}
		if (db != null) {
			db!!.close()
		}
	}

	override fun onCreate() {
		super.onCreate()
		LogDebug.d(logTag, "Service created")
	}

	private inner class LocationServiceListener : LocationListener {

		override fun onLocationChanged(location: Location) {
			if (longitude != location.longitude || latitude != location
					.latitude) {
				latitude = location.latitude
				longitude = location.longitude
				if (pm!!.locationToastNotification) {
					Toast.makeText(this@LocationServiceNative,
						String.format(resources.getString(R.string.notification_location_data),
							location.provider, location.latitude, location.longitude,
							location.time.toString(), location.accuracy),
						Toast.LENGTH_SHORT).show()
				}
				LogDebug.d(logTag, "GPS MESSAGE:\n" + location.toString())
				db!!.addData(location.provider, location.latitude,
					location.longitude, location.time,
					location.accuracy)
			} else {
				/*Toast.makeText(this@LocationServiceNative, "No new location", Toast
				.LENGTH_SHORT).show()*/
				LogDebug.d(logTag, "No new location")
			}
		}

		override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {
			/*Toast.makeText(this@LocationServiceNative, "Status changed $s", Toast.LENGTH_SHORT)
				.show()*/
		}

		override fun onProviderEnabled(s: String) {
			if (s == LocationManager.GPS_PROVIDER) {
				/*Toast.makeText(this@LocationServiceNative, "Provider GPS ENABLED$s", Toast
					.LENGTH_SHORT)
					.show()*/
				LogDebug.d(logTag, "Provider GPS ENABLED - $s")
			} else if (s == LocationManager.NETWORK_PROVIDER) {
				/*Toast.makeText(this@LocationServiceNative, "Provider NETWORK ENABLED$s", Toast
					.LENGTH_SHORT)
					.show()*/
				LogDebug.d(logTag, "Provider NETWORK ENABLED - $s")
			}
		}

		override fun onProviderDisabled(s: String) {
			if (s == LocationManager.GPS_PROVIDER) {
				/*Toast.makeText(this@LocationServiceNative, "Provider GPS DISABLED$s", Toast
					.LENGTH_SHORT)
					.show()*/
				LogDebug.d(logTag, "Provider GPS DISABLED - $s")
			} else if (s == LocationManager.NETWORK_PROVIDER) {
				/*Toast.makeText(this@LocationServiceNative, "Provider NETWORK DISABLED$s", Toast
					.LENGTH_SHORT)
					.show()*/
				LogDebug.d(logTag, "Provider NETWORK DISABLED - $s")
			}
		}
	}
}
