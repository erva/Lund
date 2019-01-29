package io.erva.lund.data.provider.notification

data class PlainNotification(
        val packageName: String,
        val postTime: Long,
        val title: String?,
        val text: String?) {

    override fun toString(): String {
        return "\nPlainNotification(packageName='$packageName', postTime='$postTime',\n  title='$title',\n   text='$text')"
    }
}