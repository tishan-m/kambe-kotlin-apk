package com.woody.kambe.roomdb

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.woody.kambe.dao.OrderDao
import com.woody.kambe.dao.ProductDao
import com.woody.kambe.entity.Order
import com.woody.kambe.entity.OrderItem
import com.woody.kambe.entity.Product

@Database(entities = [Product::class, Order::class, OrderItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Application): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "store_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
