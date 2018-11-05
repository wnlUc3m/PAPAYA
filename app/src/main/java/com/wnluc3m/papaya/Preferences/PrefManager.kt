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

package com.wnluc3m.papaya.Preferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

import com.wnluc3m.papaya.R

class PrefManager private constructor(context: Context) {
	val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

	val isFirstTimeLaunch: Boolean
		get() = sharedPreferences.getBoolean(PreferencesConstants.FIRST_TIME_INIT, true)

	// Location collector service
	val locationMTI: Long
		@Throws(NumberFormatException::class)
		get() = sharedPreferences.getString(PreferencesConstants.LOCATION_MTI, "1000").toLong()

	val locationMDI: Float
		@Throws(NumberFormatException::class)
		get() = sharedPreferences.getString(PreferencesConstants.LOCATION_MDI, "1.0").toFloat()

	val locationToastNotification: Boolean
		get() = sharedPreferences.getBoolean(PreferencesConstants.LOCATION_TOAST_NOTIFICATION,
			false)

	// KDD Algorithm
	val kddAllowAlgExec: Boolean
		get() = sharedPreferences.getBoolean(PreferencesConstants.ALLOW_ALG_EXECUTE, false)

	val distThreshold: Int
		get() = sharedPreferences.getString(PreferencesConstants.DIST_THRESHOLD, "-1").toInt()

	val timeThreshold: Int
		get() = sharedPreferences.getString(PreferencesConstants.TIME_THRESHOLD, "-1").toInt()

	val timeSpanPoints: Int
		get() = sharedPreferences.getString(PreferencesConstants.TIME_SPAN_POINTS, "-1").toInt()

	val accuracyPoints: Int
		get() = sharedPreferences.getString(PreferencesConstants.ACCURACY_VALUE,
			"-1").toInt()

	// OSM
	val mapMultitouch: Boolean
		get() = sharedPreferences.getBoolean(PreferencesConstants.ALLOW_OSM_MULTITOUCH, true)

	val zoomControlInScreen: Boolean
		get() = sharedPreferences.getBoolean(PreferencesConstants.ALLOW_OSM_ZOOM_CONTROL_SCREEN,
			false)

	val rotationGesture: Boolean
		get() = sharedPreferences.getBoolean(PreferencesConstants.ALLOW_OSM_ROTATION_GESTURE, false)

	val showScalebar: Boolean
		get() = sharedPreferences.getBoolean(PreferencesConstants.ALLOW_OSM_SCALEBAR, false)

	init {
		// Set default preferences
		PreferenceManager.setDefaultValues(context, R.xml.pref_location_service, true)
		PreferenceManager.setDefaultValues(context, R.xml.pref_kdd, true)
		PreferenceManager.setDefaultValues(context, R.xml.pref_osm, true)
	}

	fun setFirstTimeLaunch() {
		val editor = sharedPreferences.edit()
		editor.putBoolean(PreferencesConstants.FIRST_TIME_INIT, false)
		editor.apply()
	}

	companion object {

		private var pm: PrefManager? = null

		@Synchronized
		fun getInstance(context: Context?): PrefManager {
			if (pm == null) {
				pm = PrefManager(context!!.applicationContext)
			}
			return pm as PrefManager
		}
	}

	//TODO: request Location permissions from Settings
	//TODO: request Write permissions from Settings
}
