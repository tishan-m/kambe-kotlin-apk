package com.woody.kambe.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(entity = Product::class, parentColumns = ["id"], childColumns = ["product_id"]),
        ForeignKey(entity = Order::class, parentColumns = ["order_id"], childColumns = ["order_id"])
    ]
)
data class OrderItem(
    @PrimaryKey(autoGenerate = true) val item_id: Int = 0,
    val order_id: Int, // Linked to orders table
    val product_id: Int, // Linked to product table
    val unit_price_entered: Double,
    val quantity: Double
)
