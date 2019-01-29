package io.erva.lund.data.provider.sms

data class PlainSms(
        val threadId: Long,
        val address: String,
        val date: Long,
        val dateSent: Long,
        val body: String) {

    override fun toString(): String {
        return "\nPlainSms(address='$address', body='$body', dateSent='$dateSent')"
    }
}