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
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wnluc3m.papaya.BuildConfig

import com.wnluc3m.papaya.R

class DebugFragment : Fragment() {

	private var mListener: OnFragmentInteractionListener? = null

	/*override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}*/

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		val rootView = inflater.inflate(R.layout.fragment_debug, container, false)

		val versionTextViewMain = rootView.findViewById<TextView>(R.id.versionTextViewMain)
		versionTextViewMain.text = getString(R.string.about_papaya_version, BuildConfig.VERSION_NAME)

		return rootView
	}

	// TODO: Rename method, update argument and hook method into UI event
	/*fun onButtonPressed(uri: Uri) {
		if (mListener != null) {
			mListener!!.onFragmentInteraction(uri)
		}
	}*/

	override fun onAttach(context: Context?) {
		super.onAttach(context)
		if (context is OnFragmentInteractionListener) {
			mListener = context
		} else {
			throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
		}
	}

	override fun onDetach() {
		super.onDetach()
		mListener = null
	}

	interface OnFragmentInteractionListener {
		fun onFragmentInteraction(uri: Uri)
	}

	companion object {

		fun newInstance(): DebugFragment {
			return DebugFragment()
		}
	}
}