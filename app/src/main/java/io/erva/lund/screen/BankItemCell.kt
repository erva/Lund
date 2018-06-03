package io.erva.lund.screen

import android.view.View
import io.erva.celladapter.Cell
import io.erva.celladapter.Layout
import io.erva.lund.R

@Layout(R.layout.item_bank)
class BankItemCell(view: View) : Cell<BankItemModel, Cell.Listener<BankItemModel>>(view) {

    override fun bindView() {
        val item = item()

        view.bank_icon.setBackgroundResource(item.icon)
        view.bank_name.text = item.name
    }
}