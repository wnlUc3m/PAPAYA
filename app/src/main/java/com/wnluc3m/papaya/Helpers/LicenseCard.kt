/* Copyright 2018 Conny Duck
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

import android.content.Context
import android.support.v4.content.res.ResourcesCompat.getColor
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.View
import com.wnluc3m.papaya.R
import kotlinx.android.synthetic.main.license_card.view.*

class LicenseCard
@JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

	init {
		inflate(context, R.layout.license_card, this)

		setCardBackgroundColor(ThemeUtils.getColor(context, android.R.attr.colorBackground))

		val a = context.theme.obtainStyledAttributes(attrs, R.styleable.LicenseCard, 0, 0)

		val name: String? = a.getString(R.styleable.LicenseCard_name)
		val license: String? = a.getString(R.styleable.LicenseCard_license)
		val link: String? = a.getString(R.styleable.LicenseCard_link)

		a.recycle()

		licenseCardName.text = name
		licenseCardLicense.text = license

		if (link.isNullOrBlank()) {
			licenseCardLink.visibility = View.GONE
		} else {
			licenseCardLink.text = link
			setOnClickListener {
				LinkHelper.openLinkInBrowser(link!!, context)
			}
		}
	}
}