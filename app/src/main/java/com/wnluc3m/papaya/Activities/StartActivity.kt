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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.wnluc3m.papaya.Preferences.PrefManager

class StartActivity : AppCompatActivity() {

	private val REQUEST_CODE_INTRO = 1
	private var pm: PrefManager? = null


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		pm = PrefManager.getInstance(this)
		if (pm!!.isFirstTimeLaunch) {
			startActivityForResult(Intent(this@StartActivity, SplashIntroActivity::class.java),
				REQUEST_CODE_INTRO)
		} else {
			startActivity(Intent(this@StartActivity, PapayaMain::class.java))
			finish()
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == REQUEST_CODE_INTRO) {
			if (resultCode == Activity.RESULT_OK) {
				// Finished the intro
				pm!!.setFirstTimeLaunch()
				startActivity(Intent(this@StartActivity, PapayaMain::class.java))
				finish()
			} else {
				// Cancelled the intro
				finish()
			}
		}
	}
}