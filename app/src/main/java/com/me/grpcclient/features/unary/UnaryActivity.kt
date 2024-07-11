package com.me.grpcclient.features.unary

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.me.grpcclient.databinding.UnaryActivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UnaryActivity : AppCompatActivity() {
    private val binding by lazy { UnaryActivityBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<UnaryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.get.setOnClickListener {
            binding.unaryInput.text?.toString()?.let {
                if (it.isNotEmpty()) viewModel.getProductListWithUnary(it.toInt())
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