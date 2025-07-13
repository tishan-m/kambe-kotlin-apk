package com.woody.kambe.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val purchased_uprice: Double, // Original purchase price
    val uprice: Double // Selling price
)
