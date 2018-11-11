package io.erva.lund.screen

import android.view.View
import io.erva.celladapter.Layout
import io.erva.celladapter.x.Cell
import io.erva.lund.R
import kotlinx.android.synthetic.main.item_bank.view.*

@Layout(R.layout.item_bank)
class BankItemCell(view: View) : Cell<BankItemModel, Cell.Listener<BankItemModel>>(view) {

    override fun bindView() {
        val item = item()

        view.bank_icon.setBackgroundResource(item.icon)
        view.bank_name.text = item.name
    }
}