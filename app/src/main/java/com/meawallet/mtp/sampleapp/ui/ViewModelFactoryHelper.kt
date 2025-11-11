package com.meawallet.mtp.sampleapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

fun <VM: ViewModel> viewModelFactory(
    vmClass: Class<VM>,
    initializer: () -> VM
): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // Ensure call sites only ask for VM (or its supertype)
            if (modelClass.isAssignableFrom(vmClass)) {
                @Suppress("UNCHECKED_CAST")
                return initializer() as T
            }
            throw IllegalArgumentException("Unknown ViewModel ${modelClass.name}")
        }
    }
}