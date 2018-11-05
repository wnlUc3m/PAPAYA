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

package com.wnluc3m.papaya.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.wnluc3m.papaya.AdapterContent.AppInfo
import com.wnluc3m.papaya.R

internal class AppLocPermsRVAdapter(private val mValues: List<AppInfo>) : RecyclerView.Adapter<AppLocPermsRVAdapter.ViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context)
			.inflate(R.layout.fragment_applocperms, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.mItem = mValues[position]
		holder.mIdView.text = mValues[position].appName
		holder.mContentView.setImageDrawable(mValues[position].appIcon)

		/*holder.mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != mListener) {
					mListener.onListFragmentInteraction(holder.mItem);
				}
			}
		});*/
	}

	override fun getItemCount(): Int {
		return mValues.size
	}

	internal inner class ViewHolder internal constructor(private val mView: View) : RecyclerView.ViewHolder(mView) {
		internal val mIdView: TextView = mView.findViewById<View>(R.id.txtvAppName) as TextView
		internal val mContentView: ImageView = mView.findViewById<View>(R.id.imgAppIcon) as ImageView
		internal var mItem: AppInfo? = null
	}
}
