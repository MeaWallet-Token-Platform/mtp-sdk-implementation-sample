package com.meawallet.mtp.sampleapp.helpers


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.FirebaseMessaging
import com.meawallet.mtp.sampleapp.listeners.PushServiceInstanceIdGetListener
import com.meawallet.mtp.sampleapp.receivers.MyFcmListenerService


object PushServiceInstanceManager : IPushServiceInstanceManager {

    private var tokenDataMerger: MediatorLiveData<String> = MediatorLiveData<String>()
    private var localTokenKeeper: MutableLiveData<String> = MutableLiveData("")

    init {
        tokenDataMerger.addSource(MyFcmListenerService.getLastReceivedToken()
        ) { value -> tokenDataMerger.postValue(value) }

        tokenDataMerger.addSource(localTokenKeeper) { value -> tokenDataMerger.postValue(value) }
    }

    override fun getIdToken(context: Context, onResultListener: PushServiceInstanceIdGetListener) {

        getFirebaseInstanceId(onResultListener)
    }

    override fun getObservableIdToken(context: Context): LiveData<String> {
        if (tokenDataMerger.value.isNullOrEmpty()) {
            getFirebaseInstanceId(null)
        }

        return tokenDataMerger
    }

    private fun getFirebaseInstanceId(onResultListener: PushServiceInstanceIdGetListener?) {

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful || task.result == null) {
                    onResultListener?.onFailure(Exception("Failed to get Firebase Token"))
                    return@addOnCompleteListener
                }

                // Get Instance ID token
                val token: String = task.result
                onResultListener?.onSuccess(token)

                localTokenKeeper.postValue(token)
            }
    }
}