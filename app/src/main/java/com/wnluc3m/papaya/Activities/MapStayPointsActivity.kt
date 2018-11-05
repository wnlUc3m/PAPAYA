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

package com.wnluc3m.papaya.Activities

import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.wnluc3m.papaya.BuildConfig
import com.wnluc3m.papaya.Database.StayPoint
import com.wnluc3m.papaya.Helpers.LogDebug
import com.wnluc3m.papaya.Helpers.MapTools
import com.wnluc3m.papaya.Preferences.PrefManager
import com.wnluc3m.papaya.R
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import java.io.File
import java.util.*

class MapStayPointsActivity : AppCompatActivity() {

	private val logTag = this.javaClass.name
	private var map: MapView? = null
	private var stay: ArrayList<StayPoint>? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_map_stay_points)

		stay = intent.getParcelableArrayListExtra("stayPointsArrayList")
		if (stay == null) {
			LogDebug.e(logTag, "STAY NULL")
		}

		val context = applicationContext

		val asyncMapLoadHandler = Handler()
		asyncMapLoadHandler.postDelayed({
			// Map loading
			Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
			val pm = PrefManager.getInstance(context)
			Configuration.getInstance().load(context, pm.sharedPreferences)
			//TODO: external SD caching (Settings include)
			//TODO: setting path in Preferences
			// https://stackoverflow.com/questions/12339438/file-chooser-intent-opened-from-preferences
			Configuration.getInstance().osmdroidBasePath = File(Environment
				.getExternalStorageDirectory(), "PAPAYA/osmdroid")
			Configuration.getInstance().osmdroidTileCache = File(Environment.getExternalStorageDirectory(),
				"PAPAYA/osmdroid/tiles")
			map = findViewById<View>(R.id.map_stay_points) as MapView
			map!!.overlays.clear()
			//TODO: list of map providers
			map!!.setTileSource(TileSourceFactory.MAPNIK)
			map!!.isTilesScaledToDpi = true
			map!!.setMultiTouchControls(pm.mapMultitouch)
			map!!.setBuiltInZoomControls(pm.zoomControlInScreen)
			map!!.isVerticalMapRepetitionEnabled = false
			map!!.isHorizontalMapRepetitionEnabled = false
			if (pm.rotationGesture) {
				map!!.overlays.add(RotationGestureOverlay(map))
			}
			if (pm.showScalebar) {
				map!!.overlays.add(ScaleBarOverlay(map))
			}
			map!!.minZoomLevel = 1.0
			map!!.maxZoomLevel = 21.0
			val mMapController = map!!.controller as MapController
			val geoPointList = ArrayList<GeoPoint>()
			for (stayPoint in stay!!) {
				geoPointList.add(GeoPoint(stayPoint.latitude, stayPoint.longitude))
			}
			if (geoPointList.size > 1) {
				val pol = Polyline()
				pol.isGeodesic = true
				pol.points = geoPointList
				map!!.overlays.add(pol)
				// Clustering
				val cluster = RadiusMarkerClusterer(applicationContext)
				var poiMarker: Marker
				//var overlay: GroundOverlay
				for (p in geoPointList) {
					poiMarker = Marker(map!!)

					//TODO: overlay with accuracy
					/*
					overlay = GroundOverlay()
					overlay.position = p
					overlay.setDimensions(2000.toFloat())
					overlay.image = resources.getDrawable(R.drawable.ic_map_black).mutate()
					map!!.overlays.add(overlay)
					*/

					poiMarker.position = p
					poiMarker.setIcon(resources.getDrawable(R.drawable.ic_location_blue))
					cluster.add(poiMarker)
				}
				map!!.overlays.add(cluster)
				/*
				val clusterIconD = resources.getDrawable(R.drawable.marker_cluster)
				val clusterIcon = (clusterIconD as BitmapDrawable).bitmap
				cluster.setIcon(clusterIcon)
				*/

				val box = MapTools.getAreaBoundingBox(stay!!.toTypedArray())
				map!!.zoomToBoundingBox(box, false)
				LogDebug.d(logTag, "zoomLevelDouble: " + map!!.zoomLevelDouble)
				if (map!!.zoomLevelDouble > MapTools.ZOOM_ONE_POINT) {
					mMapController.setZoom((MapTools.ZOOM_ONE_POINT - MapTools.ZOOM_CNT).toDouble())
				} else {
					mMapController.setZoom(map!!.zoomLevelDouble - MapTools.ZOOM_CNT)
				}
			} else {
				mMapController.setZoom(MapTools.ZOOM_ONE_POINT)
				mMapController.setCenter(geoPointList[0])
				val marker = Marker(map!!)
				marker.position = geoPointList[0]
				marker.setIcon(resources.getDrawable(R.drawable.ic_location_blue))
				marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
				map!!.overlays.add(marker)
			}
			// Actualizar el mapa
			map!!.invalidate()
		}, 150)
	}
}
