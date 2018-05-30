package io.erva.lund.screen

import android.view.View
import io.erva.celladapter.Cell
import io.erva.celladapter.Layout

@Layout(android.R.layout.simple_list_item_1)
class BankItemCell(view: View) : Cell<BankItemModel, Cell.Listener<BankItemModel>>(view) {

    override fun bindView() {
        val item = item()
    }
}