package com.woody.kambe.repository

import com.woody.kambe.entity.Order
import com.woody.kambe.entity.OrderItem
import com.woody.kambe.entity.Product
import com.woody.kambe.roomdb.AppDatabase

class StoreRepository(private val db: AppDatabase) {

    suspend fun addProductIfNotExists(name: String, purchasedUPrice: Double, uprice: Double): Int {
        val existingProduct = db.productDao().getProductByName(name)
        return if (existingProduct == null) {
            db.productDao().insertProduct(Product(name = name, purchased_uprice = purchasedUPrice, uprice = uprice)).toInt()
        } else {
            existingProduct.id
        }
    }

    suspend fun saveOrder(total: Double, cash: Double, orderItems: List<OrderItem>) {
        val orderId = db.orderDao().insertOrder(Order(total = total, cash = cash)).toInt()
        val updatedItems = orderItems.map { it.copy(order_id = orderId) }
        db.orderDao().insertOrderItems(updatedItems)
    }
}
