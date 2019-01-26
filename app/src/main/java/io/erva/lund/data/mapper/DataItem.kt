package io.erva.lund.data.mapper

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class DataItem(
        val address: String?,
        val card: String,
        val dateSent: Date,
        val difference: Double,
        val balance: Double,
        val parsedDate: Date,
        val recost: Boolean,
        val details: String?) : Parcelable