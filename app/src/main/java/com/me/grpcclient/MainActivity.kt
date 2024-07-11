package com.me.grpcclient

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.me.grpcclient.databinding.MainActivityBinding
import com.me.grpcclient.features.bistream.BiStreamActivity
import com.me.grpcclient.features.serverstreaming.ServerStreamingActivity
import com.me.grpcclient.features.clientstream.ClientSteamingActivity
import com.me.grpcclient.features.unary.UnaryActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { MainActivityBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.unary.setOnClickListener {
            startActivity(Intent(this, UnaryActivity::class.java))
        }
        binding.clientStreaming.setOnClickListener {
            startActivity(Intent(this, ServerStreamingActivity::class.java))
        }
        binding.serverStreaming.setOnClickListener {
            startActivity(Intent(this, ClientSteamingActivity::class.java))
        }
        binding.biStreaming.setOnClickListener {
            startActivity(Intent(this, BiStreamActivity::class.java))
        }
    }
}