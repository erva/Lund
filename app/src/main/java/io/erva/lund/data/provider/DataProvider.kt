package io.erva.lund.data.provider

import android.content.Context
import io.erva.lund.data.mapper.DataItem
import io.erva.lund.widget.layout.Layout

interface DataProvider {

  fun getLayout(): Layout

  fun provide(context: Context): List<DataItem>
}