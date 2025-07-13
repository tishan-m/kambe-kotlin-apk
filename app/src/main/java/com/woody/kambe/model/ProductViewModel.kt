package com.woody.kambe.model

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.woody.kambe.entity.OrderItem
import com.woody.kambe.repository.StoreRepository
import com.woody.kambe.roomdb.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class Product(
    var name: String,
    var quantity: Double,
    var unitPrice: Double,
    var totalPrice: Double
)

class ProductViewModel(private val repository: StoreRepository, application: Application)
    : AndroidViewModel(application) {

    // LiveData to hold the list of products
    val productList: MutableLiveData<MutableList<Product>> = MutableLiveData(mutableListOf())

    private val _productMap = MutableLiveData<MutableMap<String, Double>>()
    val productMap: LiveData<MutableMap<String, Double>> get() = _productMap

    private val db = AppDatabase.getDatabase(application)

    fun saveOrderData(products: List<Product>, total: Double, cash: Double) {
        viewModelScope.launch {
            val orderItems = mutableListOf<OrderItem>()
            for (product in products) {
                val productId = repository.addProductIfNotExists(product.name, product.unitPrice, product.unitPrice)
                orderItems.add(OrderItem(
                    product_id = productId,
                    unit_price_entered = product.unitPrice,
                    quantity = product.quantity,
                    order_id = 0
                ))
            }
            repository.saveOrder(total, cash, orderItems)
        }
    }

    init {
        loadProductMap()
    }

    private fun loadProductMap() {
        viewModelScope.launch(Dispatchers.IO) {
            val productMap = loadMapFromStorage(getApplication())
            _productMap.postValue(productMap.toMutableMap())
        }
    }

    fun addOrUpdateProduct(name: String, unitPrice: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentMap = _productMap.value ?: mutableMapOf()
            currentMap[name] = unitPrice
            saveMapToStorage(getApplication(), currentMap)
            _productMap.postValue(currentMap)
        }
    }

    private fun saveMapToStorage(context: Context, map: Map<String, Double>) {
        val json = Gson().toJson(map)
        context.openFileOutput("product_map.json", Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }

    private fun loadMapFromStorage(context: Context): Map<String, Double> {
        return try {
            val file = context.openFileInput("product_map.json")
            val json = file.bufferedReader().use { it.readText() }
            Gson().fromJson(json, object : TypeToken<Map<String, Double>>() {}.type)
        } catch (e: Exception) {
            emptyMap()
        }
    }
}


