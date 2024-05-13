package com.me.grpcclient

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lime.supply.grpc.model.MapViewRequest
import com.lime.supply.grpc.model.MapViewResponse
import com.lime.supply.grpc.model.MapViewServiceGrpc
import com.me.grpcclient.rest.RestService
import io.grpc.ManagedChannelBuilder
import io.grpc.TlsChannelCredentials
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.model.DeliveryResult
import org.example.model.Product
import org.example.model.ProductServiceGrpc
import org.example.model.SizeRequest
import org.example.model.Summary
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val address = "192.168.1.143"
private const val portGRPC = 9090
private const val portREST = 8080

class MainViewModel : ViewModel() {
    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    private val _resultToast = MutableLiveData<String>()
    val resultToast: LiveData<String> = _resultToast

    private val channel by lazy {
        ManagedChannelBuilder.forAddress(address, portGRPC).usePlaintext().build()
    }
    private val stub by lazy {
        ProductServiceGrpc.newBlockingStub(channel)
            .withMaxInboundMessageSize(Int.MAX_VALUE)
            .withMaxOutboundMessageSize(Int.MAX_VALUE)
    }
    private val mapStub by lazy {
        MapViewServiceGrpc.newBlockingStub(channel)
            .withMaxInboundMessageSize(Int.MAX_VALUE)
            .withMaxOutboundMessageSize(Int.MAX_VALUE)
    }
    private val mapAsyncStub by lazy {
        MapViewServiceGrpc.newStub(channel)
    }
    private val asyncStub by lazy { ProductServiceGrpc.newStub(channel) }

    private val requestForClientStream: StreamObserver<Product> by lazy {
        asyncStub.sendProduct(object : StreamObserver<Summary> {
            override fun onNext(summary: Summary?) {
                _result.postValue("Message from server: ${summary.toString()}")
            }

            override fun onCompleted() {
                _resultToast.postValue("Server received complete operation from client!")
            }

            override fun onError(t: Throwable?) {
                _result.postValue("Server error: ${t?.message}")
            }
        })
    }

    private val requestForBiStream: StreamObserver<Product> by lazy {
        asyncStub.delivery(object : StreamObserver<DeliveryResult> {
            override fun onNext(summary: DeliveryResult?) {
                _result.postValue("Message from server: ${summary.toString()}")
            }

            override fun onCompleted() {
                _result.postValue("Server received complete operation from client!")
            }

            override fun onError(t: Throwable?) {
                _result.postValue("Server error: ${t?.message}")
            }
        })
    }

    fun getProductsGrpc(size: Int) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val request = SizeRequest.newBuilder().setSize(size).build()
            val start = System.currentTimeMillis()
            val response = stub.getProducts(request)
            val products = response.productList
            val end = System.currentTimeMillis()
            val res = products.size.toString()
                .plus(" products received. Time used: ${end - start} ms.")
            _result.postValue(res)
        }
    }

    fun getMapResponse() {
        viewModelScope.launch(context = Dispatchers.IO) {
            val request = MapViewRequest.newBuilder().build()
            mapAsyncStub.mapView(request, object : StreamObserver<MapViewResponse> {
                override fun onNext(value: MapViewResponse?) {
                    _result.postValue("Map response: ${value?.toString()}")
                }

                override fun onCompleted() {
                    _result.postValue("Map response completed")
                }

                override fun onError(t: Throwable?) {
                    _result.postValue("Map response error: ${t?.message}")
                }
            })
        }
    }

    fun getProductsRestful(size: Int) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://$address:$portREST/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val restService = retrofit.create(RestService::class.java)
            val start = System.currentTimeMillis()
            val response = restService.getProducts(size)
            val end = System.currentTimeMillis()
//            val res = response.body()?.size.toString()
//                .plus(" products received. Time used: ${end - start} ms.")
            _result.postValue("body:" + response.body()?.toString() ?: "")
        }
    }

    fun getProductsGrpcWithPayload(size: Int) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val request = SizeRequest.newBuilder().setSize(size).build()
            asyncStub.getProductStream(request, object : StreamObserver<Product> {
                override fun onNext(value: Product?) {
                    val name = value?.name
                    val year = value?.year
                    val price = value?.price
                    val quantity = value?.quantity
                    val res = "name: $name, year: $year, price: $price, quantity: $quantity\n"
                    _result.postValue(res)
                }

                override fun onError(t: Throwable?) {
                    _result.postValue("error: ${t?.message}\n")
                }

                override fun onCompleted() {
                    _result.postValue("completed\n")
                }
            })
        }
    }

    fun grpcClientStream(productString: String) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val product = buildProduct(productString)
            requestForClientStream.onNext(product)
        }
    }

    fun grpcClientStreamComplete() {
        viewModelScope.launch(context = Dispatchers.IO) {
            requestForClientStream.onCompleted()
        }
    }

    private fun buildProduct(productString: String): Product {
        val productBuilder = Product.newBuilder()
        productString.split(",").toList().forEachIndexed { index, value ->
            when (index) {
                0 -> productBuilder.name = value.trim()
                1 -> productBuilder.year = value.trim().toInt()
            }
        }
        productBuilder.setPrice(100.0).setQuantity(99)
        return productBuilder.build()
    }

    fun grpcBiStream(stringProduct: String) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val product = buildProduct(stringProduct)
            requestForBiStream.onNext(product)
        }
    }

    fun grpcBiStreamCompleted() {
        viewModelScope.launch(context = Dispatchers.IO) {
            requestForBiStream.onCompleted()
        }
    }
}