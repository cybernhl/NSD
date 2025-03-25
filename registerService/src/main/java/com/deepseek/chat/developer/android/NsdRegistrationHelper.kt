package com.deepseek.chat.developer.android

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class NsdRegistrationHelper private constructor(private val nsdManager: NsdManager) {
    private var registrationListener: NsdManager.RegistrationListener? = null

    companion object {

        @Volatile
        private var instance: NsdRegistrationHelper? = null

        fun initialize(nsdManager: NsdManager): NsdRegistrationHelper {
            return instance ?: synchronized(this) {
                instance ?: NsdRegistrationHelper(nsdManager).also { instance = it }
            }
        }

        fun getInstance(): NsdRegistrationHelper {
            return instance
                ?: throw IllegalStateException("NsdRegistrationHelper must be initialized first")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun registerService(name: String = "NsdChat", tyep: String = "_nsdchat._tcp", port: Int) {
        val attributes =
            mutableMapOf<String, Any>("path" to "index.html", "key2" to 2, "key3" to true)
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = name
            serviceType = tyep
            setPort(port)
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                attributes.forEach { key,value->
                    setAttribute(key, value.toString())
                }
            }
        }

        registrationListener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(serviceInfo: NsdServiceInfo) {
                Log.d("NSD", "Service registered: ${serviceInfo.serviceName}")
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.e("NSD", "Registration failed: $errorCode")
            }

            override fun onServiceUnregistered(serviceInfo: NsdServiceInfo) {
                Log.d("NSD", "Service unregistered")
            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.e("NSD", "Unregistration failed: $errorCode")
            }
        }

        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
    }

    fun shutdown() {
        registrationListener?.let { nsdManager.unregisterService(it) }
    }
}