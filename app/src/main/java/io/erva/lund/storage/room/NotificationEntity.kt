package io.erva.lund.storage.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NotificationEntity(

        @PrimaryKey(autoGenerate = true) var id: Int? = null,
        @ColumnInfo(name = "package_name") var packageName: String,
        @ColumnInfo(name = "post_time") var postTime: Long,
        @ColumnInfo(name = "title") var title: String?,
        @ColumnInfo(name = "text") var text: String?
) {
    override fun toString(): String {
        return "NotificationEntity(id=$id, packageName='$packageName', postTime=$postTime, title=$title, text=$text)"
    }
}