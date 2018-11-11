package io.erva.lund.screen

import androidx.annotation.DrawableRes
import io.erva.lund.data.Data

data class BankItemModel(val name: String, @DrawableRes val icon: Int, val data: Data)