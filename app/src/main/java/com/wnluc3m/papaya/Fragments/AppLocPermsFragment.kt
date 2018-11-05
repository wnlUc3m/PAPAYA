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

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wnluc3m.papaya.AdapterContent.AppInfo
import com.wnluc3m.papaya.Adapters.AppLocPermsRVAdapter
import com.wnluc3m.papaya.R
import org.apache.commons.lang3.ArrayUtils
import java.util.*
import java.util.concurrent.ExecutionException

class AppLocPermsFragment : Fragment() {

	private var appLocPermsFragmentListener: OnListFragmentInteractionListener? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_applocperms_list, container, false)
		var appsList: List<AppInfo>? = null
		try {
			appsList = GetAppsAsync().execute().get()
		} catch (e: InterruptedException) {
			e.printStackTrace()
		} catch (e: ExecutionException) {
			e.printStackTrace()
		}

		if (view is RecyclerView) {
			val context = view.getContext()
			view.setHasFixedSize(true)
			view.layoutManager = LinearLayoutManager(context)
			view.adapter = AppLocPermsRVAdapter(appsList!!)
		}
		return view
	}

	override fun onAttach(context: Context?) {
		super.onAttach(context)
		if (context is OnListFragmentInteractionListener) {
			appLocPermsFragmentListener = context
		} else {
			throw RuntimeException(context!!.toString() + " must implement OnListFragmentInteractionListener")
		}
	}

	override fun onDetach() {
		super.onDetach()
		appLocPermsFragmentListener = null
	}

	interface OnListFragmentInteractionListener {
		fun onListFragmentInteraction(item: AppInfo)
	}


	@SuppressLint("StaticFieldLeak")
	internal inner class GetAppsAsync : AsyncTask<Void, Void, List<AppInfo>>() {

		override fun doInBackground(vararg voids: Void): List<AppInfo> {
			val apps = ArrayList<AppInfo>()
			val packageManager = activity!!.packageManager
			val applicationInfoList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
			for (a in applicationInfoList) {
				try {
					val p = packageManager.getPackageInfo(a.packageName, PackageManager
						.GET_PERMISSIONS)
					if (p.requestedPermissions != null) {
						if (ArrayUtils.contains(p.requestedPermissions, Manifest.permission
								.ACCESS_FINE_LOCATION) || ArrayUtils.contains(p.requestedPermissions, Manifest.permission
								.ACCESS_COARSE_LOCATION)) {
							apps.add(AppInfo(packageManager.getApplicationLabel(a)
								.toString(), packageManager.getApplicationIcon(p.packageName)))
						}
					}
				} catch (e: PackageManager.NameNotFoundException) {
					e.printStackTrace()
				}

			}
			Collections.sort(apps, Comparator { o1, o2 ->
				val comp = o1.appName.compareTo(o2.appName, ignoreCase = true)
				if (comp < 0) return@Comparator -1
				if (comp > 0) 1 else 0
			})
			return apps
		}
	}

	companion object {

		fun newInstance(): AppLocPermsFragment {
			return AppLocPermsFragment()
		}
	}
}
