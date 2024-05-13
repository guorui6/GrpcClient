package com.me.grpcclient

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.me.grpcclient.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnGetProductsGrpc.setOnClickListener {
            binding.etSize.let {
                it.text.toString().toIntOrNull()?.let { size ->
                    viewModel.getProductsGrpc(size)
                }
            }
        }

        binding.btnGetMap.setOnClickListener {
            viewModel.getMapResponse()
        }

        binding.btnGetProductsRestful.setOnClickListener {
            binding.etSize.let {
                it.text.toString().toIntOrNull()?.let { size ->
                    viewModel.getProductsRestful(size)
                }
            }
        }

        binding.grpcServerStream.setOnClickListener {
            binding.etSize.let {
                it.text.toString().toIntOrNull()?.let { size ->
                    viewModel.getProductsGrpcWithPayload(size)
                }
            }
        }

        binding.grpcClientStream.setOnClickListener {
            binding.etProduct.text?.let {
                it.toString().let { product ->
                    viewModel.grpcClientStream(product)
                }
            }
        }

        binding.grpcClientStreamFinish.setOnClickListener {
            viewModel.grpcClientStreamComplete()
        }

        binding.grpcBiStream.setOnClickListener {
            binding.etProduct.text?.let {
                it.toString().let { product ->
                    viewModel.grpcBiStream(product)
                }
            }
        }

        binding.grpcBiStreamFinish.setOnClickListener {
            viewModel.grpcBiStreamCompleted()
        }

        viewModel.result.observe(this) {
            binding.resultTextView.text = it
        }

        viewModel.resultToast.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        binding.clear.setOnClickListener { binding.resultTextView.text = "" }
    }

}
