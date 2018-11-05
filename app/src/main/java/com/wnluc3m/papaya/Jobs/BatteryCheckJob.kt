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

package com.wnluc3m.papaya.Jobs

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.wnluc3m.papaya.Activities.MapStayPointsActivity
import com.wnluc3m.papaya.Database.LocationPoint
import com.wnluc3m.papaya.Database.SQLiteHandler
import com.wnluc3m.papaya.Database.StayPoint
import com.wnluc3m.papaya.Helpers.LogDebug
import com.wnluc3m.papaya.Preferences.PrefManager
import com.wnluc3m.papaya.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class BatteryCheckJob : JobService() {

	private val logTag = this.javaClass.name
	private var pm: PrefManager? = null

	override fun onStartJob(params: JobParameters?): Boolean {
		pm = this.let { PrefManager.getInstance(it) }
		if (pm!!.kddAllowAlgExec) {
			try {
				val sqLiteHandler = this.let { SQLiteHandler.getInstance(it) }
				LogDebug.d(logTag, "PAPTime span: " + pm!!.timeSpanPoints)
				KDD().execute(sqLiteHandler.getData(pm!!.timeSpanPoints))
			} catch (e: InterruptedException) {
				e.printStackTrace()
			} catch (e: ExecutionException) {
				e.printStackTrace()
			}
		}
		return true
	}

	override fun onStopJob(params: JobParameters?): Boolean {
		LogDebug.d(logTag, "LOL STAPH")
		return true
	}

	@SuppressLint("StaticFieldLeak")
	internal inner class KDD : AsyncTask<Array<LocationPoint?>, Int, ArrayList<StayPoint>>() {

		private val distThreshold = pm!!.distThreshold.toDouble()
		private val timeThreshold = pm!!.timeThreshold

		override fun doInBackground(vararg params: Array<LocationPoint?>?): ArrayList<StayPoint> {
			LogDebug.d(logTag, "Threshold:\nDistance:" + distThreshold
				+ ", Time: " + timeThreshold)
			val data = params[0]
			val stayPointList = ArrayList<StayPoint>()
			val count = data!!.size
			var i = 0
			while (i < count) {
				var token = false
				for (j in i + 1 until count) {
					LogDebug.d(logTag, "VALUES (i, j, count):\t$i\t-\t$j\t-\t$count")
					// Calculate the distance between two points
					val dist = distance(data[i]!!.latitude, data[j]!!.latitude,
						data[i]!!.longitude, data[j]!!.longitude)
					LogDebug.d(logTag, "DIST (m)\t$i\t-\t$j: $dist")
					if (dist > distThreshold) {
						var time: Long = 0
						try {
							val dateFirstPoint = SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
								Locale("es", "ES"))
								.parse(data[i]!!.time)
							val dateSecondPoint = SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
								Locale("es", "ES"))
								.parse(data[j]!!.time)
							LogDebug.d(logTag, "TIME dateFirstPoint: " + dateFirstPoint.time)
							LogDebug.d(logTag, "TIME dateSecondPoint: " + dateSecondPoint.time)
							// Calculate the time span between two points
							time = TimeUnit.MINUTES.toMinutes(
								((dateSecondPoint.time - dateFirstPoint.time)) / (60 * 1000) % 60)
						} catch (e: ParseException) {
							e.printStackTrace()
						}
						LogDebug.d(logTag, "TIME(min)\t$i\t-\t$j: $time")
						if (time > timeThreshold) {
							// Check accuracy
							val acc = pm!!.accuracyPoints
							LogDebug.d(logTag, "Accuracy: $acc")
							LogDebug.d(logTag, "Data [i] acc: " + data[i]!!.accuracy)
							LogDebug.d(logTag, "Data [j] acc: " + data[j]!!.accuracy)
							if (data[i]!!.accuracy <= acc && data[j]!!.accuracy <= acc) {
								// Compute mean coord
								var latMean: Double = 0.toDouble()
								var lonMean: Double = 0.toDouble()
								var accMean: Float = 0.toFloat()
								var meanCount = 0
								for (k in i..j) {
									latMean += data[k]!!.latitude
									lonMean += data[k]!!.longitude
									accMean += data[k]!!.accuracy
									++meanCount
								}
								LogDebug.d(logTag, "Mean: $meanCount")
								latMean /= meanCount
								lonMean /= meanCount
								accMean /= meanCount
								val stayPoint = StayPoint(latMean, lonMean, accMean,
									data[i]!!.time!!,
									data[j]!!.time!!,
									time)
								LogDebug.d(logTag, "StayPoint: " + stayPoint.toString())
								// Insert into stay points list
								stayPointList.add(stayPoint)
								i = j
								token = true
							}
						}
						break
					}
				}
				if (!token) {
					++i
				}
			}
			LogDebug.i(logTag, "doInBackground")
			return stayPointList
		}

		override fun onPostExecute(result: ArrayList<StayPoint>) {
			//val notificationManager = context?.let { NotificationManagerCompat.from(it) }
			val notificationManager = (this@BatteryCheckJob.getSystemService(Context
				.NOTIFICATION_SERVICE)) as NotificationManager
			val notificationID = getString(R.string.notification_kdd_id)
			// TODO: notification for Oreo
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				// Create the NotificationChannel, but only on API 26+ because
				// the NotificationChannel class is new and not in the support library
				val channel = NotificationChannel(
					notificationID,
					getString(R.string.notification_channel_kdd_name),
					NotificationManager.IMPORTANCE_DEFAULT
				)
				channel.description = getString(R.string.notification_channel_kdd_desc)
				// Register the channel with the system
				notificationManager.createNotificationChannel(channel)
			}
			val notification = NotificationCompat.Builder(
				this@BatteryCheckJob, notificationID)
			notification.setContentTitle(this@BatteryCheckJob.getString(R.string.notification_kdd_title))
			notification.setSmallIcon(R.drawable.ic_my_location_black)
			notification.setAutoCancel(true)
			if (result.size > 0) {
				val mapStayPoints = Intent(this@BatteryCheckJob, MapStayPointsActivity::class.java)
				mapStayPoints.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
				mapStayPoints.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
				mapStayPoints.putExtra("stayPointsArrayList", result)
				val pendingIntent = PendingIntent.getActivity(
					this@BatteryCheckJob,
					0,
					mapStayPoints,
					PendingIntent.FLAG_UPDATE_CURRENT
				)
				notification.setContentIntent(pendingIntent)
				notification.addAction(R.drawable.ic_clear_black,
					this@BatteryCheckJob.getString(R.string.notification_kdd_show_button), pendingIntent)
			}
			notification.setContentText(String.format(
				this@BatteryCheckJob.getString(R.string.notification_kdd_results_text),
				result.size))
			notificationManager.notify(0, notification.build())
			LogDebug.d(logTag, "SIZE: " + result.size)
		}
	}

	private companion object {
		// Haversine formula
		// https://en.wikipedia.org/wiki/Haversine_formula
		fun distance(
			lat1: Double, lat2: Double,
			lon1: Double, lon2: Double): Double {
			val earthRadius = 6371.1370 * Math.pow(10.0, 3.0)

			val latDistance = Math.toRadians(lat2 - lat1)
			val lonDistance = Math.toRadians(lon2 - lon1)

			val a = Math.pow(Math.sin(latDistance / 2), 2.0) + Math.cos(Math.toRadians(lat1)) *
				Math.cos(Math.toRadians(lat2)) * Math.pow(Math.sin(lonDistance / 2), 2.0)
			val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
			return c * earthRadius
		}
	}
}