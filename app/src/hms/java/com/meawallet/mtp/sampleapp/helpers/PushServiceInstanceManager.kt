package com.meawallet.mtp.sampleapp.helpers

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.api.ApiException
import com.huawei.agconnect.AGConnectOptionsBuilder
import com.huawei.hms.aaid.HmsInstanceId

import com.meawallet.mtp.sampleapp.listeners.PushServiceInstanceIdGetListener
import com.meawallet.mtp.sampleapp.receivers.MyHmsListenerService

object PushServiceInstanceManager : IPushServiceInstanceManager {

    private var tokenDataMerger: MediatorLiveData<String> = MediatorLiveData<String>()
    private var localTokenKeeper: MutableLiveData<String> = MutableLiveData("")

    init {
        tokenDataMerger.addSource(MyHmsListenerService.getLastReceivedToken()
        ) { value -> tokenDataMerger.postValue(value) }

        tokenDataMerger.addSource(localTokenKeeper) { value -> tokenDataMerger.postValue(value) }
    }

    override fun getIdToken(context: Context, onResultListener: PushServiceInstanceIdGetListener) {

        getHuaweiPushKitInstanceId(context, onResultListener)
    }

    override fun getObservableIdToken(context: Context): LiveData<String> {
        if (tokenDataMerger.value.isNullOrEmpty()) {
            getHuaweiPushKitInstanceId(context, null)
        }

        return tokenDataMerger
    }

    private fun getHuaweiPushKitInstanceId(
        context: Context,
        onResultListener: PushServiceInstanceIdGetListener?
    ) {
        val hmsResponseHandler = Handler(Looper.getMainLooper())
        object : Thread() {
            override fun run() {
                try {
                    // Obtain the app ID from the agconnect-service.json file.
                    val appId: String =
                        AGConnectOptionsBuilder().build(context).getString("/client/app_id")
                    val tokenScope = "HCM"

                    // If the device EMUI version is earlier than 10, this method returns null,
                    // and then the token is returned from the onNewToken callback.
                    val token: String =
                        HmsInstanceId.getInstance(context).getToken(appId, tokenScope)

                    // Check whether the token is empty.
                    if (!TextUtils.isEmpty(token)) {
                        hmsResponseHandler.post {
                            localTokenKeeper.postValue(token)
                            onResultListener?.onSuccess(token)
                        }
                    }

                } catch (e: ApiException) {
                    hmsResponseHandler.post { onResultListener?.onFailure(e) }
                }
            }
        }.start()
    }

}