package com.me.grpcclient.features.clientstream

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.grpc.stub.StreamObserver
import org.example.model.Product
import org.example.model.ProductServiceGrpc.ProductServiceBlockingStub
import org.example.model.ProductServiceGrpc.ProductServiceStub
import org.example.model.Summary
import javax.inject.Inject

@HiltViewModel
class ClientStreamingViewModel @Inject constructor(
    private val asyncStub: ProductServiceStub,
    private val blockingStub: ProductServiceBlockingStub,
) : ViewModel() {

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    private val _resultToast = MutableLiveData<String>()
    val resultToast: LiveData<String> = _resultToast

    private val productStream: StreamObserver<Product> by lazy {
        asyncStub.sendProduct(object : StreamObserver<Summary> {
            override fun onNext(summary: Summary?) {
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
        val product = Product.newBuilder().setName(name).setYear(year).build()
        try {
            productStream.onNext(product)
        } catch (e: Exception) {
            _resultToast.postValue("Server error: ${e.message}")
        }
    }

    fun tellServerComplete() {
        try {
            productStream.onCompleted()
        } catch (e: Exception) {
            _resultToast.postValue("Server error: ${e.message}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            productStream.onCompleted()
        } catch (e: Exception) {
            _resultToast.postValue("Server error: ${e.message}")
        }
    }
}