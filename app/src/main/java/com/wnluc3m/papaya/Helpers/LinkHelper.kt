/* Copyright 2017 Andrew Dawson
 *
 * This file is a part of Tusky.
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

package com.wnluc3m.papaya.Helpers

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

object LinkHelper {

	private val logTag = this.javaClass.name

	fun openLinkInBrowser(url: String, context: Context) {
		val uri = Uri.parse(url).normalizeScheme()
		val intent = Intent(Intent.ACTION_VIEW, uri)
		try {
			context.startActivity(intent)
		} catch (e: ActivityNotFoundException) {
			LogDebug.d(logTag, "Actvity was not found for intent, " + intent.toString())
		}
	}
}