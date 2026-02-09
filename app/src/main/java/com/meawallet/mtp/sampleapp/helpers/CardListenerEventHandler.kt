package com.meawallet.mtp.sampleapp.helpers

import com.meawallet.mtp.sampleapp.enums.EventSourceEnum
import com.meawallet.mtp.sampleapp.platform.TokenPlatform

class CardListenerEventHandler(
    tokenPlatform: TokenPlatform
) : BaseTransactionEventHandler(tokenPlatform) {

    override fun getEventSource(): EventSourceEnum {
        return EventSourceEnum.CARD_TRANSACTION_LISTENER
    }
}