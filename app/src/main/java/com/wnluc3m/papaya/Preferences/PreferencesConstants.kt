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

object PreferencesConstants {

	// Startup
	val FIRST_TIME_INIT = "first_time_init"

	// Location Preferences key
	val LOCATION_MTI = "pref_location_mti"
	val LOCATION_MDI = "pref_location_mdi"
	val LOCATION_TOAST_NOTIFICATION = "pref_location_toast_notification"

	// KDD Preferences key
	val ALLOW_ALG_EXECUTE = "pref_kdd_allow_alg_execute"
	val DIST_THRESHOLD = "def_dist_threshold"
	val TIME_THRESHOLD = "def_time_threshold"
	val TIME_SPAN_POINTS = "def_time_span_points_value"
	val ACCURACY_VALUE = "def_accuracy_value"

	// OSM Preferences key
	val ALLOW_OSM_MULTITOUCH = "allow_osm_multitouch"
	val ALLOW_OSM_ZOOM_CONTROL_SCREEN = "allow_osm_zoom_control_screen"
	val ALLOW_OSM_ROTATION_GESTURE = "allow_osm_rotation_gesture"
	val ALLOW_OSM_SCALEBAR = "allow_osm_scalebar"

	// Database Preferences Key
	val DATABASE_EXPORT = "pref_database_static_field_export"
	val DATABASE_IMPORT = "pref_database_static_field_import"
	val DATABASE_DELETE = "pref_database_static_field_delete"
}
