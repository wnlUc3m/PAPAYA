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

import android.Manifest
import android.os.Bundle
//import android.support.design.widget.Snackbar
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide
//import com.wnluc3m.papaya.Helpers.LogDebug
import com.wnluc3m.papaya.R

class SplashIntroActivity : IntroActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		isFullscreen = true
		super.onCreate(savedInstanceState)
		isButtonBackVisible = true
		buttonBackFunction = IntroActivity.BUTTON_BACK_FUNCTION_BACK
		//TODO: uncomment as soon as the lib gets updated
		/*addOnNavigationBlockedListener { _, _ ->
			Snackbar.make(contentView, getString(R.string.error_permissions),
				Snackbar.LENGTH_SHORT)
				.setAction(R.string.btn_ok
				) {
					LogDebug.d(SplashIntroActivity::class.java.simpleName,
						"Not permissions")
				}.show()
		}*/
		// Intro slide
		addSlide(SimpleSlide.Builder()
			.title(getString(R.string.slide_intro_tool))
			.description(getString(R.string.license))
			.background(R.color.colorPrimary)
			.backgroundDark(R.color.colorPrimaryDark)
			.scrollable(false)
			.build()
		)
		// What it does
		addSlide(SimpleSlide.Builder()
			.title(getString(R.string.slide_intro_what))
			.description(getString(R.string.slide_intro_what_desc))
			.background(R.color.blueColor)
			.backgroundDark(R.color.blueDarkColor)
			.scrollable(false)
			.build()
		)
		// Data storage
		addSlide(SimpleSlide.Builder()
			.title(getString(R.string.slide_intro_storage))
			.description(getString(R.string.slide_intro_storage_desc))
			.image(R.drawable.ic_save)
			.background(R.color.yellowColor)
			.backgroundDark(R.color.yellowDarkColor)
			.scrollable(false)
			.build()
		)
		// KDD
		addSlide(SimpleSlide.Builder()
			.title(getString(R.string.slide_intro_kdd))
			.description(getString(R.string.slide_intro_kdd_desc))
			.image(R.drawable.ic_map_location)
			.background(R.color.brownColor)
			.backgroundDark(R.color.brownDarkColor)
			.scrollable(false)
			.build()
		)
		// Permissions
		addSlide(SimpleSlide.Builder()
			.title(getString(R.string.slide_intro_permissions))
			.description(getString(R.string.slide_intro_permissions_desc))
			.image(R.drawable.ic_unlocked)
			.background(R.color.redColor)
			.backgroundDark(R.color.redDarkColor)
			.scrollable(false)
			.permissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.ACCESS_COARSE_LOCATION,
				Manifest.permission.ACCESS_FINE_LOCATION))
			.build()
		)
	}

	override fun onBackPressed() {
		finish()
	}
}
