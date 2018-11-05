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
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wnluc3m.papaya.Activities.LicenseActivity
import com.wnluc3m.papaya.BuildConfig
import com.wnluc3m.papaya.R

class AboutFragment : Fragment() {

	private var listener: OnFragmentInteractionListener? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		val rootView = inflater.inflate(R.layout.fragment_about, container, false)

		val versionTextView = rootView.findViewById<TextView>(R.id.versionTextView)
		versionTextView.text = getString(R.string.about_papaya_version, BuildConfig.VERSION_NAME)

		val aboutLicensesButton = rootView.findViewById<TextView>(R.id.aboutLicensesButton)
		aboutLicensesButton.setOnClickListener {
			startActivity(Intent(context, LicenseActivity::class.java))
		}

		return rootView
	}

	override fun onAttach(context: Context) {
		super.onAttach(context)
		if (context is OnFragmentInteractionListener) {
			listener = context
		} else {
			throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
		}
	}

	override fun onDetach() {
		super.onDetach()
		listener = null
	}

	interface OnFragmentInteractionListener {
		fun onFragmentInteraction(uri: Uri)
	}

	companion object {
		fun newInstance(): AboutFragment {
			return AboutFragment()
		}
	}
}