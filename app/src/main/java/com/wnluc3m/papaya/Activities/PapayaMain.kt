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

import android.app.AlertDialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.wnluc3m.papaya.AdapterContent.AppInfo
import com.wnluc3m.papaya.Fragments.AboutFragment
import com.wnluc3m.papaya.Fragments.AppLocPermsFragment
import com.wnluc3m.papaya.Fragments.DebugFragment
import com.wnluc3m.papaya.Fragments.MapFragment
import com.wnluc3m.papaya.Helpers.LogDebug
import com.wnluc3m.papaya.Jobs.BatteryCheckJob
import com.wnluc3m.papaya.R
import com.wnluc3m.papaya.Services.LocationServiceNative


class PapayaMain : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
	MapFragment.OnFragmentInteractionListener, DebugFragment.OnFragmentInteractionListener,
	AppLocPermsFragment.OnListFragmentInteractionListener,
	AboutFragment.OnFragmentInteractionListener {

	private val logTag = this.javaClass.name
	private var serviceIntent: Intent? = null
	private val serviceMonitor = LocationServiceNative::class.java
	private var boundService: LocationServiceNative? = null
	private var serviceConnection: ServiceConnection? = null
	private var context: Context? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_papaya_main)

		val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
		setSupportActionBar(toolbar)

		val actionBar = supportActionBar
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true)
			actionBar.setHomeAsUpIndicator(R.drawable.ic_menu)
		}

		val drawerToggle = object : ActionBarDrawerToggle(
			this,
			findViewById<View>(R.id.drawer_layout) as DrawerLayout,
			toolbar,
			R.string.drawer_open,
			R.string.drawer_closed) {
		}
		drawerToggle.syncState()

		// Debug fragment
		val fm = supportFragmentManager.beginTransaction()
		fm.replace(R.id.frame, DebugFragment.newInstance()).commit()

		// Nav. menu
		val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
		navigationView.setNavigationItemSelectedListener(this)

		context = applicationContext

		serviceConnection = object : ServiceConnection {
			override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
				boundService = (iBinder as LocationServiceNative.LocalBinder).service
				LogDebug.d(logTag, "Binded")
			}

			override fun onServiceDisconnected(componentName: ComponentName) {
				boundService = null
				LogDebug.d(logTag, "Not binded")
			}
		}
		startMyService()

		// KDD Algorithm when connected to USB
		val componentName = ComponentName(this, BatteryCheckJob::class.java)
		val jobInfo = JobInfo.Builder(taskId, componentName)
			.setRequiresCharging(true)
			.build()
		val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
		val resultCode = jobScheduler.schedule(jobInfo)
		if (resultCode == JobScheduler.RESULT_SUCCESS) {
			LogDebug.d(logTag, "Job Scheduled")
		} else {
			LogDebug.e(logTag, "Job failed to be scheduled")
		}
	}

	override fun onBackPressed() {
		val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START)
		} else {
			super.onBackPressed()
		}
	}

	override fun onNavigationItemSelected(item: MenuItem): Boolean {
		val id = item.itemId
		var fragment: Fragment? = null
		var itemSelected = true
		val alert = AlertDialog.Builder(this)
		alert.setCancelable(false)
		when (id) {
			R.id.nav_debug -> fragment = DebugFragment.newInstance()
			R.id.nav_map -> {
				val locationData = boundService!!.data
				if (locationData != null && locationData.isNotEmpty()) {
					LogDebug.d(logTag, "Size: " + locationData.size)
					fragment = MapFragment.newInstance(locationData)
				} else {
					alert.setMessage(getString(R.string.error_no_location_data))
					alert.setNeutralButton(getString(R.string.btn_ok), null)
					alert.show()
					itemSelected = false
				}
			}
			R.id.nav_location_perms -> fragment = AppLocPermsFragment.newInstance()
			R.id.nav_manage -> {
				startActivity(Intent(context, SettingsActivity::class.java))
				itemSelected = false
			}
			R.id.nav_share -> {
				val sharingIntent = Intent(Intent.ACTION_SEND)
				sharingIntent.type = "text/plain"
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					getString(R.string.share_papaya_tool))
				startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_by)))
				itemSelected = false
			}
			R.id.nav_about -> fragment = AboutFragment.newInstance()
		}

		if (fragment != null) {
			val fm = supportFragmentManager.beginTransaction()
			fm.replace(R.id.frame, fragment).commit()
		}

		val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
		drawer.closeDrawer(GravityCompat.START)

		return itemSelected
	}

	public override fun onPause() {
		super.onPause()
		LogDebug.d(logTag, "onPause")
	}

	public override fun onDestroy() {
		super.onDestroy()
		LogDebug.d(logTag, "onDestroy")
		if (serviceConnection != null) {
			unbindService(serviceConnection)
		}
	}

	public override fun onResume() {
		super.onResume()
		LogDebug.d(logTag, "onResume")
	}

	private fun startMyService() {
		serviceIntent = Intent(context, serviceMonitor)
		context!!.startService(serviceIntent)
		bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
		Toast.makeText(context, "Service started", Toast.LENGTH_SHORT).show()
	}

	/*private void stopMyService() {
		context.stopService(serviceIntent);
		unbindService(serviceConnection);
		Toast.makeText(context, "Service stopped", Toast.LENGTH_SHORT).show();
	}*/

	override fun onFragmentInteraction(uri: Uri) {

	}

	override fun onPointerCaptureChanged(hasCapture: Boolean) {

	}

	override fun onListFragmentInteraction(item: AppInfo) {

	}
}
