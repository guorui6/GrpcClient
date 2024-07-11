package com.me.grpcclient.features.bistream

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.me.grpcclient.databinding.BiStreamActivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BiStreamActivity : AppCompatActivity() {

    private val binding by lazy { BiStreamActivityBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<BiStreamViewModel>()

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
            viewModel.tellServerCompleted()
        }
        viewModel.result.observe(this) {
            binding.result.text = it
        }
        viewModel.resultToast.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }
}