package com.meawallet.mtp.sampleapp.ui.cards

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.meawallet.mtp.*
import com.meawallet.mtp.sampleapp.R
import com.meawallet.mtp.sampleapp.enums.PaymentIntentActionsEnum
import com.meawallet.mtp.sampleapp.intents.CardIntent
import com.meawallet.mtp.sampleapp.platform.TokenPlatform
import com.meawallet.mtp.sampleapp.ui.addcard.AddCardActivity
import com.meawallet.mtp.sampleapp.ui.payment.PaymentActivity
import com.meawallet.mtp.sampleapp.utils.DeviceUtils
import com.meawallet.mtp.sampleapp.utils.isReadyForPayment
import com.meawallet.mtp.sampleapp.utils.isSelectedForPayment


class CardListAdapter(
    private val platform: TokenPlatform
) : RecyclerView.Adapter<CardListAdapter.ViewHolder>() {

    private var meaCards: ArrayList<MeaCard> = ArrayList()

    inner class ViewHolder constructor(v: View) : RecyclerView.ViewHolder(v){

        private val cardIdTv: TextView
        private val isDefaultTv: TextView
        private val isSelectedTv: TextView
        private val cardStateTv: TextView
        private val cardPaymentTokensTv: TextView
        private val cardPaymentNetworkTv: TextView
        private val maskedPanTv: TextView
        private val validThruTv: TextView
        private val payNowBtn: MaterialButton
        private val deleteBtn: MaterialButton
        private val menuIv: ImageView

        init {
            cardIdTv = v.findViewById(R.id.card_item_card_id)
            isDefaultTv = v.findViewById(R.id.card_item_is_default)
            isSelectedTv = v.findViewById(R.id.card_item_is_selected)
            cardStateTv = v.findViewById(R.id.card_item_state)
            cardPaymentTokensTv = v.findViewById(R.id.card_item_payment_tokens)
            cardPaymentNetworkTv = v.findViewById(R.id.card_item_payment_network)
            maskedPanTv = v.findViewById(R.id.card_item_masked_pan)
            validThruTv = v.findViewById(R.id.card_item_valid_thru)
            payNowBtn = v.findViewById(R.id.card_item_pay_btn)
            deleteBtn = v.findViewById(R.id.card_item_delete_btn)
            menuIv = v.findViewById(R.id.card_item_menu)
        }

        fun bind(card: MeaCard){
            cardIdTv.text = card.id
            isDefaultTv.text = card.isDefaultForContactlessPayments.toString()
            isSelectedTv.text = card.isSelectedForPayment().toString()
            cardStateTv.text = card.state.toString()
            cardPaymentTokensTv.text = card.transactionCredentialsCount.toString()
            cardPaymentNetworkTv.text = card.paymentNetwork?.name
            maskedPanTv.text = "****  ****  ****  ${card.tokenInfo?.tokenPanSuffix ?: "****"}"
            validThruTv.text= card.tokenInfo?.tokenExpiry.let {
                if (!it.isNullOrBlank()) {
                    val sb = StringBuilder(it)
                    sb.insert(2,'/')
                    return@let sb.toString()
                }  else {
                    return@let "**/**"
                }
            }

            payNowBtn.setOnClickListener {
                if (card.state == MeaCardState.ACTIVE) {
                    selectForContactlessPayment(card)
                    openPaymentActivity(card)
                }
            }
            payNowBtn.isEnabled = card.isReadyForPayment()

            deleteBtn.setOnClickListener {
                deleteOrMarkForDeletionCard(card)
            }

            menuIv.setOnClickListener {
                showMenu(it, R.menu.card_overflow_menu, card)
            }
        }

        private fun showMenu(v: View, @MenuRes menuRes: Int, card: MeaCard) {
            val popup = PopupMenu(v.context, v)
            popup.menuInflater.inflate(menuRes, popup.menu)

            prepareMenu(popup, card)

            popup.setOnMenuItemClickListener { menuItem: MenuItem ->

                when (menuItem.itemId) {
                    R.id.card_complete_digitization -> {
                        openAddCardActivity(card)
                        true
                    }
                    R.id.card_mark_for_delete -> {
                        markForDeletion(card)
                        true
                    }
                    R.id.card_set_as_default -> {
                        setAsDefaultForContactless(card)
                        true
                    }
                    R.id.card_unset_as_default -> {
                        unsetAsDefaultForContactless(card)
                        true
                    }
                    R.id.card_select_for_contactless -> {
                        selectForContactlessPayment(card)
                        true
                    }
                    R.id.card_deselect_for_contactless -> {
                        deselectForContactlessPayment(card)
                        true
                    }
                    R.id.card_delete_payment_tokens -> {
                        deletePaymentTokens(card)
                        true
                    }
                    R.id.card_replenish_payment_tokens -> {
                        replenishPaymentTokens(card)
                        true
                    }
                    else -> false
                }
            }
            popup.setOnDismissListener {
                // Respond to popup being dismissed.
            }

            popup.show()
        }

        private fun prepareMenu(popup: PopupMenu, card: MeaCard) {
            popup.menu.findItem(R.id.card_set_as_default).isVisible =
                !card.isDefaultForContactlessPayments
            popup.menu.findItem(R.id.card_unset_as_default).isVisible =
                card.isDefaultForContactlessPayments

            popup.menu.findItem(R.id.card_complete_digitization).isVisible =
                card.state == MeaCardState.DIGITIZATION_STARTED || isAdditionalAuthRequired(card)

            popup.menu.findItem(R.id.card_mark_for_delete).isVisible =
                card.state != MeaCardState.MARKED_FOR_DELETION

            popup.menu.findItem(R.id.card_select_for_contactless).isVisible =
                !card.isSelectedForPayment()
            popup.menu.findItem(R.id.card_deselect_for_contactless).isVisible =
                card.isSelectedForPayment()
        }

        private fun deleteOrMarkForDeletionCard(card: MeaCard) {
            if (DeviceUtils.isNetworkAvailable(cardIdTv.context)) {
                deleteCard(card)
            } else {
                markForDeletion(card)
            }
        }

        private fun openPaymentActivity(card: MeaCard) {
            val paymentIntent = CardIntent(
                cardIdTv.context,
                PaymentActivity::class.java,
                PaymentIntentActionsEnum.INTENT_ACTION_PAY_BY_CHOSEN_CARD,
                card.id
            )

            startActivity(cardIdTv.context, paymentIntent, null)
        }

        private fun openAddCardActivity(card: MeaCard) {
            val completeDigitizationIntent = Intent(cardIdTv.context, AddCardActivity::class.java)

            when (card.state) {
                MeaCardState.DIGITIZATION_STARTED -> {
                    completeDigitizationIntent.putExtra(
                        AddCardActivity.CARD_IN_ACTION_RECEIPT,
                        card.eligibilityReceipt
                    )
                }
                MeaCardState.DIGITIZED -> {
                    completeDigitizationIntent.putExtra(
                        AddCardActivity.CARD_IN_ACTION_RECEIPT,
                        card.eligibilityReceipt
                    )
                }
                else -> {
                    // ignore digitized cards
                    return
                }
            }

            startActivity(cardIdTv.context, completeDigitizationIntent, null)
        }

        private fun markForDeletion(card: MeaCard) {
            card.markForDeletion(object: MeaCardListener {
                override fun onFailure(p0: MeaError) {
                    Toast.makeText(cardIdTv.context, "Failed spectacularly ${p0.message}", Toast.LENGTH_LONG).show()
                }

                override fun onSuccess(p0: MeaCard) {
                    Toast.makeText(cardIdTv.context, "All super good. Marked for deletion.", Toast.LENGTH_LONG).show()
                    updateCards(platform.getCards())
                }

            })
        }

        private fun deleteCard(card: MeaCard) {
            card.delete(object: MeaCardListener {
                override fun onFailure(p0: MeaError) {
                    Toast.makeText(cardIdTv.context, "Failed spectacularly ${p0.message}", Toast.LENGTH_LONG).show()
                }

                override fun onSuccess(p0: MeaCard) {
                    Toast.makeText(cardIdTv.context, "All super good. Card deleted.", Toast.LENGTH_LONG).show()
                    updateCards(platform.getCards())
                }
            })
        }

        private fun setAsDefaultForContactless(card: MeaCard) {
            card.deselectForContactlessPayment()
            card.setAsDefaultForContactlessPayments()
            updateCards(platform.getCards())
        }

        private fun unsetAsDefaultForContactless(card: MeaCard) {
            card.deselectForContactlessPayment()
            card.unsetAsDefaultForContactlessPayments()
            updateCards(platform.getCards())
        }

        private fun selectForContactlessPayment(card: MeaCard) {
            card.selectForContactlessPayment(object: MeaCardListener {
                override fun onFailure(p0: MeaError) {
                    Toast.makeText(isSelectedTv.context, "Failed spectacularly ${p0.message}", Toast.LENGTH_LONG).show()
                }

                override fun onSuccess(p0: MeaCard) {
                    updateCards(platform.getCards())
                }
            })
        }

        private fun deselectForContactlessPayment(card: MeaCard) {
            card.deselectForContactlessPayment()
            updateCards(platform.getCards())
        }

        private fun deletePaymentTokens(card: MeaCard) {
            card.deletePaymentTokens()
            updateCard(card)
        }

        private fun replenishPaymentTokens(card: MeaCard) {
            card.replenishPaymentTokens()
        }

        private fun isAdditionalAuthRequired(card: MeaCard): Boolean {
           return when (card.yellowPathState) {
                MeaCardYellowPathState.REQUIRE_ADDITIONAL_AUTHENTICATION -> {
                    true
                }
                MeaCardYellowPathState.AUTHENTICATION_INITIALIZED -> {
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    fun updateCard(card: MeaCard) {
        notifyItemChanged(meaCards.indexOf(card))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCards(newIdeas: List<MeaCard>) {
        meaCards.clear()
        meaCards.addAll(newIdeas)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(meaCards[position])
    }

    override fun getItemCount(): Int {
        return meaCards.size
    }
}