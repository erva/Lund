package io.erva.lund.data

import android.content.Context
import java.util.*

class DataProviderFactory {

    companion object {

        fun getDataProvider() = PumbDataProvider()
//        fun getDataProvider() = StubDataProvider()
    }
}

interface DataProvider {

    fun provideData(context: Context): List<DataItem>
}

data class DataItem(
        val card: String,
        val dateSent: Date,
        val difference: Double,
        val balance: Double,
        val parsedDate: Date)