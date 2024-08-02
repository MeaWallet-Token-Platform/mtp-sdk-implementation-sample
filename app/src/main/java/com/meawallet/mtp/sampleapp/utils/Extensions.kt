package com.meawallet.mtp.sampleapp.utils

import android.content.Context
import android.content.res.AssetManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import android.util.Log
import com.meawallet.mtp.*


private const val TAG = "Extensions"

fun MeaCard.isBackgroundImageDownloaded(context: Context): Boolean {
    val filename = this.id + ".png"
    val file = context.getFileStreamPath(filename)

    return file != null && file.exists()
}

fun MeaCard.hasBackgroundImage(): Boolean {
    this.productConfig?.cardBackgroundAssetId?.let {
        return true
    }

    return false
}

fun MeaCard.downloadBackgroundImage(context: Context) {
    this.productConfig?.cardBackgroundAssetId?.let { assetId ->
        MeaTokenPlatform.getAsset(assetId, object: MeaGetAssetListener {

            override fun onFailure(error: MeaError) {
                Log.e(TAG,"Failed to retrieve asset id id = $assetId; error = $error")
            }

            override fun onSuccess(mediaContentArray: Array<out MeaMediaContent>) {
                for (media in mediaContentArray) {
                    Log.d(TAG,"Received media. Type = ${media.type}")

                    if (media.type == MeaMediaContent.Type.PNG) {
                        val filename = "$id.png"
                        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
                            val decodedImage: ByteArray =
                                Base64.decode(media.base64DataString, Base64.DEFAULT)
                            it.write(decodedImage)
                        }
                    }
                }
            }
        })
    }
}

fun MeaCard.getBackgroundImage(context: Context): Drawable? {
    Log.d(TAG,"Get background image for Card Id = ${this.id}")

    if (!isBackgroundImageDownloaded(context)) {
        return null
    }

    val filename = this.id + ".png"
    val inputStream = context.openFileInput(filename)

    return BitmapDrawable.createFromStream(inputStream, null)
}

fun MeaCard.isReadyForPayment(): Boolean {
    return (this.state == MeaCardState.ACTIVE
            && this.transactionCredentialsCount != null
            && this.transactionCredentialsCount!! > 0)
}

fun MeaCard.isSelectedForPayment(): Boolean {

    return this.id == MeaTokenPlatform.getCardSelectedForContactlessPayment()?.id
}

fun AssetManager.readAssetsFile(fileName : String): String = open(fileName).bufferedReader().use{it.readText()}
