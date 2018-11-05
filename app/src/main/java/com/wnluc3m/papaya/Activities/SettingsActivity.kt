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

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.view.MenuItem
import com.wnluc3m.papaya.Database.SQLiteHandler
import com.wnluc3m.papaya.Helpers.LogDebug
import com.wnluc3m.papaya.Preferences.PreferencesConstants
import com.wnluc3m.papaya.R

class SettingsActivity : AppCompatPreferenceActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setupActionBar()
	}

	/*override fun onDestroy() {
		super.onDestroy()
	}*/

	/**
	 * Set up the [android.app.ActionBar], if the API is available.
	 */
	private fun setupActionBar() {
		val actionBar = supportActionBar
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true)
			actionBar.setDisplayShowHomeEnabled(true)
		}
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			android.R.id.home -> {
				finish()
				return true
			}
		}
		return super.onOptionsItemSelected(item)
	}

	/**
	 * {@inheritDoc}
	 */
	override fun onIsMultiPane(): Boolean {
		return isXLargeTablet(this)
	}

	/**
	 * {@inheritDoc}
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
		loadHeadersFromResource(R.xml.pref_headers, target)
	}

	/**
	 * This method stops fragment injection in malicious applications.
	 * Make sure to deny any unknown fragments here.
	 */
	override fun isValidFragment(fragmentName: String): Boolean {
		return (PreferenceFragment::class.java.name == fragmentName
			|| KDDAlgorithmFragment::class.java.name == fragmentName
			|| GeneralPreferenceFragment::class.java.name == fragmentName
			|| OSMPreferenceFragment::class.java.name == fragmentName
			|| DatabaseFragment::class.java.name == fragmentName)
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	class GeneralPreferenceFragment : PreferenceFragment() {
		override fun onCreate(savedInstanceState: Bundle?) {
			super.onCreate(savedInstanceState)
			addPreferencesFromResource(R.xml.pref_location_service)
			setHasOptionsMenu(true)
			bindPreferenceSummaryToValue(findPreference(PreferencesConstants.LOCATION_MDI))
			bindPreferenceSummaryToValue(findPreference(PreferencesConstants.LOCATION_MTI))
		}

		override fun onOptionsItemSelected(item: MenuItem): Boolean {
			when (item.itemId) {
				R.id.home -> {
					activity.finish()
					return true
				}
			}
			return super.onOptionsItemSelected(item)
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	class OSMPreferenceFragment : PreferenceFragment() {
		override fun onCreate(savedInstanceState: Bundle?) {
			super.onCreate(savedInstanceState)
			addPreferencesFromResource(R.xml.pref_osm)
			setHasOptionsMenu(true)
		}

		override fun onOptionsItemSelected(item: MenuItem): Boolean {
			when (item.itemId) {
				R.id.home -> {
					activity.finish()
					return true
				}
			}
			return super.onOptionsItemSelected(item)
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	class KDDAlgorithmFragment : PreferenceFragment() {
		override fun onCreate(savedInstanceState: Bundle?) {
			super.onCreate(savedInstanceState)
			addPreferencesFromResource(R.xml.pref_kdd)
			setHasOptionsMenu(true)
			bindPreferenceSummaryToValue(findPreference(PreferencesConstants.TIME_SPAN_POINTS))
			bindPreferenceSummaryToValue(findPreference(PreferencesConstants.DIST_THRESHOLD))
			bindPreferenceSummaryToValue(findPreference(PreferencesConstants.TIME_THRESHOLD))
			bindPreferenceSummaryToValue(findPreference(PreferencesConstants.ACCURACY_VALUE))
		}

		override fun onOptionsItemSelected(item: MenuItem): Boolean {
			when (item.itemId) {
				R.id.home -> {
					activity.finish()
					return true
				}
			}
			return super.onOptionsItemSelected(item)
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	class DatabaseFragment : PreferenceFragment() {
		override fun onCreate(savedInstanceState: Bundle?) {
			super.onCreate(savedInstanceState)
			addPreferencesFromResource(R.xml.pref_database)
			setHasOptionsMenu(true)
		}

		override fun onOptionsItemSelected(item: MenuItem): Boolean {
			when (item.itemId) {
				R.id.home -> {
					activity.finish()
					return true
				}
			}
			return super.onOptionsItemSelected(item)
		}

		override fun onPreferenceTreeClick(preferenceScreen: PreferenceScreen?, preference: Preference?): Boolean {
			val sqliteHandler = SQLiteHandler.getInstance(preferenceScreen!!.context)
			when (preference!!.key) {
				PreferencesConstants.DATABASE_EXPORT -> sqliteHandler.exportFullDB()
				PreferencesConstants.DATABASE_IMPORT -> sqliteHandler.importFullDB()
				PreferencesConstants.DATABASE_DELETE -> sqliteHandler.eraseData()
			}
			LogDebug.d("SETTINGS", "ID: " + preference.key)
			return super.onPreferenceTreeClick(preferenceScreen, preference)
		}
	}

	companion object {

		/**
		 * A preference value change listener that updates the preference's summary
		 * to reflect its new value.
		 */
		private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
			val stringValue = value.toString()

			if (preference is ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				val index = preference.findIndexOfValue(stringValue)

				// Set the summary to reflect the new value.
				preference.setSummary(
					if (index >= 0) preference.entries[index] else null)
			} else {
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.summary = stringValue
			}
			true
		}

		/**
		 * Helper method to determine if the device has an extra-large screen. For
		 * example, 10" tablets are extra-large.
		 */
		private fun isXLargeTablet(context: Context): Boolean {
			return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
		}

		/**
		 * Binds a preference's summary to its value. More specifically, when the
		 * preference's value is changed, its summary (line of text below the
		 * preference title) is updated to reflect the value. The summary is also
		 * immediately updated upon calling this method. The exact display format is
		 * dependent on the type of preference.
		 *
		 * @see .sBindPreferenceSummaryToValueListener
		 */
		private fun bindPreferenceSummaryToValue(preference: Preference) {
			// Set the listener to watch for value changes.
			preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

			// Trigger the listener immediately with the preference's
			// current value.
			sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
				PreferenceManager
					.getDefaultSharedPreferences(preference.context)
					.getString(preference.key, ""))
		}
	}
}
