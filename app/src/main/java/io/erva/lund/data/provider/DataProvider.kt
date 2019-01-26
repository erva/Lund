package io.erva.lund.data.provider

import io.erva.lund.data.mapper.DataItem
import io.erva.lund.widget.layout.Layout

interface DataProvider {

    fun getLayout(): Layout

    fun provide(): List<DataItem>
}