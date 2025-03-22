package com.deepseek.chat.developer.android

import android.net.nsd.NsdManager
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    fun registerService(
        nsdManager: NsdManager,
        serviceName: String = "NsdChat",
        serviceType: String = "_nsdchat._tcp",
        port: Int = 8888
    ) {
        NsdRegistrationHelper.initialize(nsdManager)
        NsdRegistrationHelper.getInstance().registerService(serviceName, serviceType, port)
    }
}