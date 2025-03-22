package com.deepseek.chat.developer.android

import android.net.nsd.NsdManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
 fun discoverServices(
        nsdManager: NsdManager,
        serviceName: String = "MyService",
        serviceType: String = "_nsdchat._tcp."
    ) {
        NsdDiscoveryHelper.initialize(nsdManager)
        viewModelScope.launch(Dispatchers.IO) {
            NsdDiscoveryHelper.getInstance().discoveredServices.collect {
                   Log.e("MainViewModel","Show me get $it")
            }
        }
        NsdDiscoveryHelper.getInstance().discoverServices(serviceName, serviceType)
    }
}