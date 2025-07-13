package com.woody.kambe.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val order_id: Int = 0,
    val total: Double,
    val cash: Double,
    val createdDateTime: Long = System.currentTimeMillis() // Timestamp
)
