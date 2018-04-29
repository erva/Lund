package io.erva.lund.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import io.erva.lund.R

class PreferenceActivity : AppCompatActivity() {

    private val REQUEST_SMS_PERMISSION = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)

        if (!isSmsPermissionGranted()) requestReadAndSendSmsPermission()
    }

    private fun isSmsPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this@PreferenceActivity, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this@PreferenceActivity, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadAndSendSmsPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS), REQUEST_SMS_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_SMS_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                } else {
                }
            }
        }
    }
}