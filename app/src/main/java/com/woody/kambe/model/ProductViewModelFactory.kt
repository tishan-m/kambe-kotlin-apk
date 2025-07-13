package com.woody.kambe.model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woody.kambe.repository.StoreRepository
import com.woody.kambe.roomdb.AppDatabase

class ProductViewModelFactory(private val application: Application, private val db: AppDatabase)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(StoreRepository(db), application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}




