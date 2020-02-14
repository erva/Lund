package io.erva.lund.storage.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotificationDao {

  @Query(
      "SELECT * FROM NotificationEntity WHERE package_name IS :packageName ORDER BY post_time DESC LIMIT :limit"
  )
  fun getAll(
    packageName: String,
    limit: Int = 30
  ): List<NotificationEntity>

  @Insert
  fun insert(notificationEntity: NotificationEntity)
}