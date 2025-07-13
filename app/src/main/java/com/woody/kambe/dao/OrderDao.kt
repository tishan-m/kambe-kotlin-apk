package com.woody.kambe.dao

import androidx.room.Dao
import androidx.room.Insert
import com.woody.kambe.entity.Order
import com.woody.kambe.entity.OrderItem

@Dao
interface OrderDao {
    @Insert
    suspend fun insertOrder(order: Order): Long

    @Insert
    suspend fun insertOrderItems(orderItems: List<OrderItem>)
}
