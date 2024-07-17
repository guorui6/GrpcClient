package com.me.grpcclient.features.unary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.model.ProductServiceGrpc
import org.example.model.SizeRequest
import javax.inject.Inject

@HiltViewModel
class UnaryViewModel @Inject constructor(
    private val productServiceStub: ProductServiceGrpc.ProductServiceStub,
    private val productBlockingStub: ProductServiceGrpc.ProductServiceBlockingStub,
) : ViewModel() {

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    private val _resultToast = MutableLiveData<String>()
    val resultToast: LiveData<String> = _resultToast

    fun getProductListWithUnary(size: Int) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val request = SizeRequest.newBuilder().setSize(size).build()
            try {
                val response = productBlockingStub.getProducts(request)
                val products = response.productList.map { it.name + ", " + it.year }
                val res = products.joinToString("\n")
                _result.postValue(res)
            } catch (e: Exception) {
                _result.postValue("Server error: ${e.message}")
            }
        }
    }

    fun getProductListWithAsyncStub(size: Int) {
        val request = SizeRequest.newBuilder().setSize(size).build()
        try {
            productServiceStub.getProducts(
                request,
                object : io.grpc.stub.StreamObserver<org.example.model.ProductList> {
                    override fun onNext(value: org.example.model.ProductList) {
                        val products = value.productList.map { it.name + ", " + it.year }
                        val res = products.joinToString("\n")
                        _result.postValue(res)
                    }

                    override fun onError(t: Throwable) {
                        _result.postValue("Server error: ${t.message}")
                    }

                    override fun onCompleted() {
                        _resultToast.postValue("Server received complete operation from client!")
                    }
                })
        } catch (e: Exception) {
            _result.postValue("Server error: ${e.message}")
        }
    }
}