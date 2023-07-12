package com.meawallet.mtp.sampleapp.listeners

interface PushServiceInstanceIdGetListener {

    fun onSuccess(token: String)

    fun onFailure(exception: Exception)
}