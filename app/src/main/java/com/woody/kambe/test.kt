//package com.woody.kambe
//
//import android.os.Bundle
//import android.widget.ArrayAdapter
//import android.widget.AutoCompleteTextView
//import android.widget.Button
//import android.widget.EditText
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.ViewModelProvider
//import androidx.recyclerview.widget.RecyclerView
//import com.woody.kambe.model.ProductViewModel
//
//class MainActivity1 : AppCompatActivity() {
//    private lateinit var viewModel: ProductViewModel
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.main_activity)
//
//        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
//
//        val productNameInput: AutoCompleteTextView = findViewById(R.id.productNameInput)
//        val quantityInput: EditText = findViewById(R.id.quantityInput)
//        val unitPriceInput: EditText = findViewById(R.id.unitPriceInput)
//        val addProductButton: Button = findViewById(R.id.addProductButton)
//        val recyclerView: RecyclerView = findViewById(R.id.productRecyclerView)
//
//        val nameAdapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line)
//        productNameInput.setAdapter(nameAdapter)
//
//        // Observe product map and update AutoComplete suggestions
//        viewModel.productMap.observe(this) { productMap ->
//            nameAdapter.clear()
//            nameAdapter.addAll(productMap.keys)
//        }
//
//        // On product name selection, prefill unit price
//        productNameInput.setOnItemClickListener { _, _, position, _ ->
//            val selectedProduct = nameAdapter.getItem(position)
//            val unitPrice = viewModel.productMap.value?.get(selectedProduct)
//            unitPriceInput.setText(unitPrice?.toString())
//        }
//
//        // Add Product Logic
//        addProductButton.setOnClickListener {
//            val name = productNameInput.text.toString().trim()
//            val unitPrice = unitPriceInput.text.toString().toDoubleOrNull()
//
//            if (name.isBlank()) {
//                productNameInput.error = "Name is required"
//                productNameInput.requestFocus()
//                return@setOnClickListener
//            }
//            if (unitPrice == null || unitPrice <= 0) {
//                unitPriceInput.error = "Enter a valid price"
//                unitPriceInput.requestFocus()
//                return@setOnClickListener
//            }
//
//            // Add or update the product map
//            viewModel.addOrUpdateProduct(name, unitPrice)
//
//            // Clear inputs
//            productNameInput.text.clear()
//            unitPriceInput.text.clear()
//            productNameInput.requestFocus()
//        }
//    }
//}
