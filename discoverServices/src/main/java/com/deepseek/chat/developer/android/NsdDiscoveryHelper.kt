package com.deepseek.chat.developer.android

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.InetAddress

class NsdDiscoveryHelper private constructor(private val nsdManager: NsdManager) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var discoveryListener: NsdManager.DiscoveryListener? = null
    private val _discoveredServices = MutableStateFlow<NsdServiceInfo?>(null)
    val discoveredServices: StateFlow<NsdServiceInfo?> = _discoveredServices

    companion object {
        @Volatile
        private var instance: NsdDiscoveryHelper? = null

        fun initialize(nsdManager: NsdManager): NsdDiscoveryHelper {
            return instance ?: synchronized(this) {
                instance ?: NsdDiscoveryHelper(nsdManager).also { instance = it }
            }
        }

        fun getInstance(): NsdDiscoveryHelper {
            return instance
                ?: throw IllegalStateException("NsdDiscoveryHelper must be initialized first")
        }
    }

    fun discoverServices(serviceName: String, serviceType: String) {
        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(serviceType: String) {
                Log.d("NSD", "Discovery started")
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                Log.d("NSD", "Service found: ${serviceInfo.serviceName}")
                when {
                    serviceInfo.serviceType != serviceType -> // Service type is the string containing the protocol and
                        // transport layer for this service.
                        Log.d("NSD", "Unknown Service Type: ${serviceInfo.serviceType}")

                    serviceInfo.serviceName.contains(serviceName) -> nsdManager.resolveService(
                        serviceInfo,
                        object : NsdManager.ResolveListener {
                            override fun onResolveFailed(
                                serviceInfo: NsdServiceInfo,
                                errorCode: Int
                            ) {
                                // Called when the resolve fails. Use the error code to debug.
                                Log.e("NSD", "Resolve failed: $errorCode")
                            }

                            @RequiresApi(Build.VERSION_CODES.N)
                            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                                Log.e("NSD", "Resolve Succeeded. $serviceInfo")
                                val port: Int = serviceInfo.port
                                val host: InetAddress = serviceInfo.host
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    serviceInfo.attributes.asSequence().forEach { (key, value) ->
                                        Log.e(
                                            "NSD", "Resolve attribute key : $key value : ${
                                                try {
                                                    String(value, Charsets.UTF_8)
                                                } catch (e: CharacterCodingException) {
                                                    "Invalid UTF-8 sequence"
                                                }
                                            }"
                                        )
                                    }
                                }
                                scope.launch {
                                    _discoveredServices.emit(serviceInfo)
                                }
                            }
                        }
                    )
                }
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                Log.d("NSD", "Service lost: ${serviceInfo.serviceName}")
            }

            override fun onDiscoveryStopped(serviceType: String) {
                Log.d("NSD", "Discovery stopped")
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(
                    "NSD",
                    "Discovery Start failed  serviceType $serviceType errorCode : $errorCode"
                )
//                nsdManager.stopServiceDiscovery(this)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(
                    "NSD",
                    "Stop discovery Failed serviceType $serviceType errorCode : $errorCode"
                )
//                nsdManager.stopServiceDiscovery(this)
            }
        }
        nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    fun shutdown() {
        discoveryListener?.let { nsdManager.stopServiceDiscovery(it) }
    }
}