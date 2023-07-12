package com.meawallet.mtp.sampleapp.helpers

import com.meawallet.mtp.sampleapp.enums.EventSourceEnum

object CardListenerEventHandler : BaseTransactionEventHandler() {
    override fun getEventSource(): EventSourceEnum {
        return EventSourceEnum.CARD_TRANSACTION_LISTENER
    }
}