package com.me.grpcclient.features.serverstreaming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.model.Product
import org.example.model.ProductServiceGrpc
import org.example.model.SizeRequest
import javax.inject.Inject

@HiltViewModel
class ServerStreamingViewModel @Inject constructor(
    private val productServiceStub: ProductServiceGrpc.ProductServiceStub,
    private val productBlockingStub: ProductServiceGrpc.ProductServiceBlockingStub,
) : ViewModel() {
    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    private val _resultToast = MutableLiveData<String>()
    val resultToast: LiveData<String> = _resultToast

    fun getProductsByBlockingStub(size: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = SizeRequest.newBuilder().setSize(size).build()
            val response = productBlockingStub.getProductStream(request)
            try {
                response.forEach { product ->
                    _result.postValue("Product: ${product.name}, ${product.year}")
                }
            } catch (e: Exception) {
                _resultToast.postValue("Server error: ${e.message}")
            }
        }
    }

    fun getProductsByAsyncStub(size: Int) {
        val request = SizeRequest.newBuilder().setSize(size).build()
        productServiceStub.getProductStream(request, object : StreamObserver<Product> {
            override fun onNext(product: Product) {
                _result.postValue("Product: ${product.name}, ${product.year}")
            }

            override fun onCompleted() {
                _resultToast.postValue("Server received complete operation from client!")
            }

            override fun onError(t: Throwable) {
                _result.postValue("Server error: ${t.message}")
            }
        })
    }
}