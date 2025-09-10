package com.codepan.location

import android.location.Address

data class Place(
    val data: Address
) {
    val city: String?
        get() = data.locality

    val area: String?
        get() = data.adminArea

    val subArea: String?
        get() = data.subAdminArea

    val zipCode: String?
        get() = data.postalCode

    val country: String?
        get() = data.countryName

    val countryCode: String?
        get() = data.countryCode

    val address: String
        get() {
            val builder = StringBuilder()
            val number = data.subThoroughfare
            val street = data.thoroughfare
            val locality = data.locality
            val subArea = data.subAdminArea
            val area = data.adminArea
            val zipCode = data.postalCode
            val country = data.countryName
            if (number != null) {
                builder.append(number)
            }
            if (street != null) {
                group(builder, false);
                builder.append(street)
            }
            if (locality != null) {
                group(builder)
                builder.append(locality)
            }
            if (subArea != null) {
                group(builder)
                builder.append(subArea)
            }
            if (area != null) {
                group(builder)
                builder.append(area)
            }
            if (zipCode != null) {
                group(builder)
                builder.append(zipCode)
            }
            if (country != null) {
                group(builder)
                builder.append(country)
            }
            return builder.toString()
        }

    private fun group(
        builder: StringBuilder,
        withComma: Boolean = true
    ) {
        if (builder.isNotEmpty()) {
            if (withComma) {
                builder.append(", ")
            } else {
                builder.append(" ")
            }
        }
    }
}