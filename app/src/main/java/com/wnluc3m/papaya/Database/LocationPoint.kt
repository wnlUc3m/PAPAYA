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

import android.os.Parcel
import android.os.Parcelable

class LocationPoint : Point, Parcelable, Cloneable {

	var id: Int = 0
	var provider: String? = null
		private set
	var time: String? = null

	constructor(id: Int, provider: String, latitude: Double, longitude: Double, time: String,
	            accuracy: Float) {
		this.id = id
		this.provider = provider
		this.latitude = latitude
		this.longitude = longitude
		this.time = time
		this.accuracy = accuracy
	}

	// PARCELABLE
	constructor(`in`: Parcel) {
		id = `in`.readInt()
		provider = `in`.readString()
		latitude = `in`.readDouble()
		longitude = `in`.readDouble()
		time = `in`.readString()
		accuracy = `in`.readFloat()
	}

	override fun writeToParcel(dest: Parcel, flags: Int) {
		dest.writeInt(id)
		dest.writeString(provider)
		dest.writeDouble(latitude)
		dest.writeDouble(longitude)
		dest.writeString(time)
		dest.writeFloat(accuracy)
	}

	override fun describeContents(): Int {
		return 0
	}

	override fun toString(): String {
		return "LocationPoint{" +
			"id=" + id +
			", provider='" + provider + '\''.toString() +
			", latitude=" + latitude +
			", longitude=" + longitude +
			", time='" + time + '\''.toString() +
			", accuracy=" + accuracy +
			'}'.toString()
	}

	companion object CREATOR : Parcelable.Creator<LocationPoint> {
		override fun createFromParcel(parcel: Parcel): LocationPoint {
			return LocationPoint(parcel)
		}

		override fun newArray(size: Int): Array<LocationPoint?> {
			return arrayOfNulls(size)
		}
	}
}
