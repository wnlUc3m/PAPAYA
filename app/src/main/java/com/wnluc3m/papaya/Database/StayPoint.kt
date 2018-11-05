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

class StayPoint : Point, Parcelable, Cloneable {

	var arrivalTime: String? = null
	var leavingTime: String? = null
	var minTime: Long = 0

	constructor(latitude: Double, longitude: Double, accuracy: Float, arrivalTime: String,
	            leavingTime: String, minTime: Long) {
		this.latitude = latitude
		this.longitude = longitude
		this.accuracy = accuracy
		this.arrivalTime = arrivalTime
		this.leavingTime = leavingTime
		this.minTime = minTime
	}

	override fun toString(): String {
		return "StayPoint{" +
			"latitude=" + latitude +
			", longitude=" + longitude +
			", accuracy=" + accuracy +
			", arrivalTime='" + arrivalTime + '\''.toString() +
			", leavingTime='" + leavingTime + '\''.toString() +
			", minTime=" + minTime +
			'}'.toString()
	}

	constructor(`in`: Parcel) {
		latitude = `in`.readDouble()
		longitude = `in`.readDouble()
		accuracy = `in`.readFloat()
		arrivalTime = `in`.readString()
		leavingTime = `in`.readString()
		minTime = `in`.readLong()
	}

	override fun describeContents(): Int {
		return 0
	}

	override fun writeToParcel(dest: Parcel, flags: Int) {
		dest.writeDouble(latitude)
		dest.writeDouble(longitude)
		dest.writeFloat(accuracy)
		dest.writeString(arrivalTime)
		dest.writeString(leavingTime)
		dest.writeLong(minTime)
	}

	companion object CREATOR : Parcelable.Creator<StayPoint> {
		override fun createFromParcel(parcel: Parcel): StayPoint {
			return StayPoint(parcel)
		}

		override fun newArray(size: Int): Array<StayPoint?> {
			return arrayOfNulls(size)
		}
	}
}
