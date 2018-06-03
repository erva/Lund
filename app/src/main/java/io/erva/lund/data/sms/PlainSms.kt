package io.erva.lund.data.sms

data class PlainSms(
        val threadId: Long,
        val address: String,
        val date: Long,
        val dateSent: Long,
        val body: String) {

    override fun toString(): String {
        return "\nBankSms(address='$address', body='$body', dateSent='$dateSent')"
    }
}