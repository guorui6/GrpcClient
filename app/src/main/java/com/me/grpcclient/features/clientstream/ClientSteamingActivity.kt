package com.me.grpcclient.features.clientstream

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.me.grpcclient.databinding.ServerStreamActivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClientSteamingActivity : AppCompatActivity() {
    private val binding by lazy { ServerStreamActivityBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<ClientStreamingViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.send.setOnClickListener {
            val name = binding.inputName.text?.toString()
            val year = binding.inputYear.text?.toString()
            if (name != null && year != null) {
                viewModel.sendProductToServer(name, year.toInt())
            }
        }
        binding.complete.setOnClickListener {
            viewModel.tellServerComplete()
        }
        viewModel.result.observe(this) {
            binding.result.text = it
        }
        viewModel.resultToast.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }
}