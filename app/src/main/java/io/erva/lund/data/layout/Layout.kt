package io.erva.lund.data.layout

import android.content.Context
import android.widget.RemoteViews
import io.erva.lund.data.mapper.DataItem

interface Layout {

    fun layoutData(context: Context, item: DataItem): RemoteViews
}