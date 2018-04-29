package io.erva.lund.data

import android.content.Context
import java.util.*

class StubDataProvider : DataProvider {

    override fun provideData(context: Context): List<DataItem> {
        val list = mutableListOf<Long>()
        for (i in 1..5) {
            list.add(System.currentTimeMillis() - 103 * i)
        }
        return list.map { it -> DataItem(it.toString(), Date(), 0.0, 0.0, Date()) }
    }
}