package com.me.grpcclient.features.bistream

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.model.DeliveryResult
import org.example.model.Product
import org.example.model.ProductServiceGrpc
import javax.inject.Inject

@HiltViewModel
class BiStreamViewModel @Inject constructor(
    private val productServiceStub: ProductServiceGrpc.ProductServiceStub,
) : ViewModel() {

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    private val _resultToast = MutableLiveData<String>()
    val resultToast: LiveData<String> = _resultToast

    private val biStream: StreamObserver<Product> by lazy {
        productServiceStub.delivery(object : StreamObserver<DeliveryResult> {
            override fun onNext(summary: DeliveryResult?) {
                _result.postValue("Message from server: ${summary.toString()}")
            }

            override fun onCompleted() {
                _resultToast.postValue("Server received complete operation from client!")
            }

            override fun onError(t: Throwable?) {
                _resultToast.postValue("Server error: ${t?.message}")
            }
        })
    }

    fun sendProductToServer(name: String, year: Int) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val product = Product.newBuilder().setName(name).setYear(year).build()
            try {
                biStream.onNext(product)
            } catch (e: Exception) {
                _resultToast.postValue("Server error: ${e.message}")
            }
        }
    }

    fun tellServerCompleted() {
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                biStream.onCompleted()
            } catch (e: Exception) {
                _resultToast.postValue("Server error: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            biStream.onCompleted()
        } catch (e: Exception) {
            _resultToast.postValue("Server error: ${e.message}")
        }
    }
}