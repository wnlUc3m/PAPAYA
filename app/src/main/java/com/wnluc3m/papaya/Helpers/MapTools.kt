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

package com.wnluc3m.papaya.Helpers

import com.wnluc3m.papaya.Database.LocationPoint
import com.wnluc3m.papaya.Database.StayPoint
import org.osmdroid.util.BoundingBox

object MapTools {

	const val ZOOM_CNT = 0.3f
	const val ZOOM_ONE_POINT = 18

	fun getAreaBoundingBox(arr: Array<LocationPoint>): BoundingBox {
		var nord = 0.0
		var sud = 0.0
		var ovest = 0.0
		var est = 0.0
		var lat: Double
		var lon: Double
		for (i in arr.indices) {
			lat = arr[i].latitude
			lon = arr[i].longitude
			if (i == 0 || lat > nord) nord = lat
			if (i == 0 || lat < sud) sud = lat
			if (i == 0 || lon < ovest) ovest = lon
			if (i == 0 || lon > est) est = lon
		}
		return BoundingBox(nord, est, sud, ovest)
	}

	fun getAreaBoundingBox(arr: Array<StayPoint>): BoundingBox {
		var nord = 0.0
		var sud = 0.0
		var ovest = 0.0
		var est = 0.0
		var lat: Double
		var lon: Double
		for (i in arr.indices) {
			lat = arr[i].latitude
			lon = arr[i].longitude
			if (i == 0 || lat > nord) nord = lat
			if (i == 0 || lat < sud) sud = lat
			if (i == 0 || lon < ovest) ovest = lon
			if (i == 0 || lon > est) est = lon
		}
		return BoundingBox(nord, est, sud, ovest)
	}
}
