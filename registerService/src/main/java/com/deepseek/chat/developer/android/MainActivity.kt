package com.deepseek.chat.developer.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.fragment.NavHostFragment
import com.deepseek.chat.developer.android.databinding.ActivityMainBinding
import com.deepseek.chat.developer.android.ui.theme.NDSTheme

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment)?.apply {
//        }
        ////compose
        setContent {
            NDSTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainPage()
                }
            }
        }
    }
}

