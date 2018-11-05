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

import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceActivity
import android.support.annotation.LayoutRes
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup

abstract class AppCompatPreferenceActivity : PreferenceActivity() {

	private var mDelegate: AppCompatDelegate? = null

	val supportActionBar: ActionBar?
		get() = delegate.supportActionBar

	private val delegate: AppCompatDelegate
		get() {
			if (mDelegate == null) {
				mDelegate = AppCompatDelegate.create(this, null)
			}
			return mDelegate!!
		}

	override fun onCreate(savedInstanceState: Bundle?) {
		delegate.installViewFactory()
		delegate.onCreate(savedInstanceState)
		super.onCreate(savedInstanceState)
	}

	override fun onPostCreate(savedInstanceState: Bundle?) {
		super.onPostCreate(savedInstanceState)
		delegate.onPostCreate(savedInstanceState)
	}

	fun setSupportActionBar(toolbar: Toolbar?) {
		delegate.setSupportActionBar(toolbar)
	}

	override fun getMenuInflater(): MenuInflater {
		return delegate.menuInflater
	}

	override fun setContentView(@LayoutRes layoutResID: Int) {
		delegate.setContentView(layoutResID)
	}

	override fun setContentView(view: View) {
		delegate.setContentView(view)
	}

	override fun setContentView(view: View, params: ViewGroup.LayoutParams) {
		delegate.setContentView(view, params)
	}

	override fun addContentView(view: View, params: ViewGroup.LayoutParams) {
		delegate.addContentView(view, params)
	}

	override fun onPostResume() {
		super.onPostResume()
		delegate.onPostResume()
	}

	override fun onTitleChanged(title: CharSequence, color: Int) {
		super.onTitleChanged(title, color)
		delegate.setTitle(title)
	}

	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)
		delegate.onConfigurationChanged(newConfig)
	}

	override fun onStop() {
		super.onStop()
		delegate.onStop()
	}

	override fun onDestroy() {
		super.onDestroy()
		delegate.onDestroy()
	}

	override fun invalidateOptionsMenu() {
		delegate.invalidateOptionsMenu()
	}
}
