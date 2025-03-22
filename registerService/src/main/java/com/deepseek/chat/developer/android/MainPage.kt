package com.deepseek.chat.developer.android

import android.content.Context
import android.net.nsd.NsdManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.deepseek.chat.developer.android.databinding.FragmentMainBinding
import kotlin.getValue

//class MainPage : Fragment() {
//    private var _binding: FragmentMainBinding? = null
//    private val binding get() = _binding!!
//    private val viewmodel: MainViewModel by viewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentMainBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.buttonFirst.setOnClickListener {
//            (requireContext().applicationContext.getSystemService(Context.NSD_SERVICE) as NsdManager)?.let {
//                viewmodel.registerService(it,"MyService","_http._tcp",3389)
//            }
//
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

@Composable
fun MainPage(viewmodel: MainViewModel = viewModel()) {
    var serviceName by remember { mutableStateOf("MyService") }
    var serviceType by remember { mutableStateOf("_http._tcp") }
    var port by remember { mutableStateOf("3389") }
    Scaffold(

    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val context = LocalContext.current
            OutlinedTextField(
                value = serviceName,
                onValueChange = { serviceName = it },
                label = { Text("Service Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = serviceType,
                onValueChange = { serviceType = it },
                label = { Text("Service Type") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = port,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        port = it
                    }
                },
                label = { Text("Port") },
                isError = port.isNotEmpty() && !port.all { it.isDigit() },
                supportingText = {
                    if (port.isNotEmpty() && !port.all { it.isDigit() }) {
                        Text("Please enter numbers only")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    (context.applicationContext.getSystemService(Context.NSD_SERVICE) as NsdManager)?.let {
                        viewmodel.registerService(it,
                            serviceName = serviceName,
                            serviceType = serviceType,
                            port = port.toInt())
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go")
            }
        }
    }
}