package com.me.grpcclient.features.serverstreaming

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.me.grpcclient.databinding.ClientStreamActivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServerStreamingActivity : AppCompatActivity() {
    private val binding by lazy { ClientStreamActivityBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<ServerStreamingViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.get.setOnClickListener {
            binding.input.text?.toString()?.let {
                if (it.isNotEmpty()) {
                    viewModel.getProductsByAsyncStub(it.toInt())
                }
            }
        }

        viewModel.result.observe(this) {
            binding.result.text = it
        }

        viewModel.resultToast.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }
}