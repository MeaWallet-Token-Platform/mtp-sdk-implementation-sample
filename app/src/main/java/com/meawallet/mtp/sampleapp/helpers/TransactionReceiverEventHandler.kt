package com.meawallet.mtp.sampleapp.helpers

import com.meawallet.mtp.sampleapp.enums.EventSourceEnum

object TransactionReceiverEventHandler : BaseTransactionEventHandler() {
    override fun getEventSource(): EventSourceEnum {
        return EventSourceEnum.LOCAL_BROADCAST_RECEIVER
    }
}