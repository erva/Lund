package io.erva.lund.data.provider.notification

import android.content.Context
import io.erva.lund.data.mapper.DataItem
import io.erva.lund.data.mapper.DataMapper
import io.erva.lund.data.parser.Parser
import io.erva.lund.data.provider.DataProvider
import io.erva.lund.storage.room.AppDatabase
import io.erva.lund.storage.room.NotificationEntity
import io.erva.lund.widget.layout.Layout

class NotificationProvider(
  private val packageName: String,
  private val parser: Parser<NotificationEntity>,
  private val mapper: DataMapper,
  private val layout: Layout
) : DataProvider {

  override fun getLayout() = layout

  override fun provide(context: Context): List<DataItem> {
    return provideNotificationEntityList(context, packageName).map {
      DataItem(
          address = it.packageName,
          balance = 0.toDouble(),
          card = it.packageName,
          dateSent = null,
          parsedDate = null,
          difference = 0.toDouble(),
          recost = false,
          details = it.title + "\n" + it.text
      )
    }
  }

  private fun provideNotificationEntityList(
    context: Context,
    packageName: String
  ): List<NotificationEntity> {
    return AppDatabase.getInstance(context)
        .notificationDao()
        .getAll(packageName)
  }
}