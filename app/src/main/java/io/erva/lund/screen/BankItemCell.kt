package io.erva.lund.screen

import android.view.View
import androidx.annotation.DrawableRes
import io.erva.celladapter.Layout
import io.erva.celladapter.x.Cell
import io.erva.lund.R
import io.erva.lund.data.provider.Bank
import io.erva.lund.data.provider.SOURCE
import kotlinx.android.synthetic.main.item_bank.view.bank_icon
import kotlinx.android.synthetic.main.item_bank.view.bank_name
import kotlinx.android.synthetic.main.item_bank.view.bank_source

data class BankItemModel(
  @DrawableRes val icon: Int,
  val name: String,
  val bank: Bank,
  val source: SOURCE
) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as BankItemModel

    if (bank != other.bank) return false
    if (source != other.source) return false

    return true
  }

  override fun hashCode(): Int {
    var result = bank.hashCode()
    result = 31 * result + source.hashCode()
    return result
  }
}

@Layout(R.layout.item_bank)
class BankItemCell(view: View) : Cell<BankItemModel, Cell.Listener<BankItemModel>>(view) {

  override fun bindView() {
    val item = item()
    view.bank_icon.setBackgroundResource(item.icon)
    view.bank_name.text = item.name
    view.bank_source.text = item.source.name.toLowerCase()
  }
}