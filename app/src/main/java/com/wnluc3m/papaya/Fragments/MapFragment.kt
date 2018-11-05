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

package com.wnluc3m.papaya.Fragments

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wnluc3m.papaya.BuildConfig
import com.wnluc3m.papaya.Database.LocationPoint
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

class MapFragment : Fragment() {

	private val logTag = this.javaClass.name
	private var locationDataParam: Array<LocationPoint>? = null
	private var mListener: OnFragmentInteractionListener? = null
	private var map: MapView? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		retainInstance = true
		if (arguments != null) {
			@Suppress("UNCHECKED_CAST")
			locationDataParam = arguments!!.getParcelableArray(DATALIST_PARAM) as Array<LocationPoint>
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		//val rootView = inflater.inflate(R.layout.fragment_map, container, false)
		val rootView = inflater.inflate(R.layout.fragment_map, container, false)
		map = rootView.findViewById<View>(R.id.map) as MapView
		return rootView
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		val context = activity!!.applicationContext
		// Async load of osmdroid's map
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
			//Drawable cusMarker = ContextCompat.getDrawable
			// (context, R.drawable.ic_location_red);
			//ArrayList<OverlayItem> ab = new ArrayList<>();
			//OverlayItem a;
			for (locationData in locationDataParam!!) {
				geoPointList.add(GeoPoint(locationData.latitude, locationData.longitude))
				//a = new OverlayItem("Title", "Description",
				//new GeoPoint(locationData.getLatitude(), locationData.getLongitude()));
				//a.setMarker(cusMarker);
				//ab.add(a);
				//geoPointList.add((GeoPoint) a.getPoint());
			}
			if (geoPointList.size > 1) {
				//ItemizedIconOverlay j = new
				//ItemizedIconOverlay<>(ab, null, context);
				val pol = Polyline()
				pol.isGeodesic = true
				pol.points = geoPointList
				map!!.overlays.add(pol)
				//map.getOverlays().add(j);
				//IMapController mapController = map.getController();
				//mMapController.setZoom(14);
				//mMapController.setCenter(geoPointList.get(0));
				//mapController.setCenter(ab.get(0).getPoint());
				// Clustering
				val cluster = RadiusMarkerClusterer(getContext())
				var poiMarker: Marker
				for (p in geoPointList) {
					poiMarker = Marker(map!!)
					poiMarker.position = p
					poiMarker.setIcon(resources.getDrawable(R.drawable.ic_location_red))
					cluster.add(poiMarker)
				}
				map!!.overlays.add(cluster)
				val clusterIconD = resources.getDrawable(R.drawable.marker_cluster)
				val clusterIcon = (clusterIconD as BitmapDrawable).bitmap
				cluster.setIcon(clusterIcon)

				val box = MapTools.getAreaBoundingBox(locationDataParam!!)
				//mMapController.zoomToSpan(box.getLatitudeSpan(), box.getLongitudeSpan());
				//mMapController.setCenter(box.getCenterWithDateLine());
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
				marker.setIcon(resources.getDrawable(R.drawable.ic_location_red))
				marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
				map!!.overlays.add(marker)
			}
			// Actualizar el mapa
			map!!.invalidate()
			//TODO: Caching
			//TODO: check if offline works
		}, 150)
	}

	/*public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}*/

	override fun onAttach(context: Context?) {
		if (context is OnFragmentInteractionListener) {
			mListener = context
		} else {
			throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
		}
		super.onAttach(context)
	}

	override fun onResume() {
		super.onResume()
		map!!.onResume()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		map!!.onDetach()

	}

	override fun onPause() {
		map!!.onPause()
		super.onPause()
	}

	override fun onDetach() {
		map!!.onDetach()
		super.onDetach()
		mListener = null
	}

	interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		fun onFragmentInteraction(uri: Uri)

	}

	companion object {
		private const val DATALIST_PARAM = "datalist"

		fun newInstance(locationData: Array<LocationPoint?>?): MapFragment {
			val fragment = MapFragment()
			val args = Bundle()
			args.putParcelableArray(DATALIST_PARAM, locationData)
			fragment.arguments = args
			return fragment
		}
	}
}
