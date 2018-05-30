package io.erva.lund.screen

import android.Manifest
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import io.erva.celladapter.Cell
import io.erva.celladapter.CellAdapter
import io.erva.lund.R
import io.erva.lund.data.Data
import kotlinx.android.synthetic.main.activity_preference.*

class PreferenceActivity : AppCompatActivity() {

    private val REQUEST_SMS_PERMISSION = 2

    private var adapter: CellAdapter = CellAdapter().let {
        it.cell(BankItemCell::class) {
            item(BankItemModel::class)
            listener(object : Cell.Listener<BankItemModel> {
                override fun onCellClicked(item: BankItemModel) {

                }
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)

        if (!isSmsPermissionGranted()) {
            requestReadAndSendSmsPermission()
        }

        bank_list.layoutManager = LinearLayoutManager(this)
        bank_list.adapter = adapter
        adapter.items.addAll(Data.values().map { BankItemModel(it.bankName, it.bankIcon, it) })
        adapter.notifyDataSetChanged()
    }

    var mAppWidgetId: Int? = null
    private fun m() {
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
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
                    m()
                } else {
                }
            }
        }
    }
}