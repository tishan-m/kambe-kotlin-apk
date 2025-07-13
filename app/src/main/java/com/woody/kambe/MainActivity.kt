package com.woody.kambe // Replace with your actual package name

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.compose.material.Button
import com.woody.kambe.component.ProductAdapter
import com.woody.kambe.model.Product
import com.woody.kambe.model.ProductViewModel
import com.woody.kambe.model.ProductViewModelFactory
import com.woody.kambe.repository.StoreRepository
import com.woody.kambe.roomdb.AppDatabase
import com.woody.kambe.ui.screens.DashboardScreen

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ProductViewModel
    private lateinit var adapter: ProductAdapter

    private var isEditing = false // Tracks if we are editing a product
    private var editingPosition: Int? = null // Tracks the position of the product being edited
    private var lastTotalPrice: Double = 0.0
    private lateinit var lastTotalButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        // Set up Toolbar as ActionBar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Find NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Configure AppBar with Navigation Component
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.dashboardFragment))
        toolbar.setupWithNavController(navController, appBarConfiguration)

        val db = AppDatabase.getDatabase(application) // Get database instance
        val factory = ProductViewModelFactory(application,db)
        viewModel = ViewModelProvider(this, factory).get(ProductViewModel::class.java)

        val productNameInput: AutoCompleteTextView = findViewById(R.id.productNameInput)
        val quantityInput: EditText = findViewById(R.id.quantityInput)
        val unitPriceInput: EditText = findViewById(R.id.unitPriceInput)
        val subTotalPriceText: TextView = findViewById(R.id.subTotalPriceText)
        val addProductButton: Button = findViewById(R.id.addProductButton)
        val recyclerView: RecyclerView = findViewById(R.id.productRecyclerView)
        lastTotalButton = findViewById(R.id.lastTotalButton)

        lastTotalButton.setOnClickListener {
            // Show popup dialog
            showCashDialog()
        }        // Setup RecyclerView
        adapter = ProductAdapter(mutableListOf()) { product, position ->
            // Populate inputs with the product's data for editing
            productNameInput.setText(product.name)
            quantityInput.setText(product.quantity.toString())
            unitPriceInput.setText(product.unitPrice.toString())

            // Set editing state
            isEditing = true
            editingPosition = position
            addProductButton.text = "Update Product" // Change button text to "Update"
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Observe product list
        viewModel.productList.observe(this) { productList ->
            adapter.updateList(productList)
        }

        val nameAdapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line)
        productNameInput.setAdapter(nameAdapter)

        // Observe product map and update AutoComplete suggestions
        viewModel.productMap.observe(this) { productMap ->
            nameAdapter.clear()
            nameAdapter.addAll(productMap.keys)
        }

        // On product name selection, prefill unit price
        productNameInput.setOnItemClickListener { _, _, position, _ ->
            val selectedProduct = nameAdapter.getItem(position)
            val unitPrice = viewModel.productMap.value?.get(selectedProduct)
            unitPriceInput.setText(unitPrice?.toString())
        }

        // Add TextWatchers to update subtotal dynamically
        val updateSubtotal: () -> Unit = {
            val quantity = quantityInput.text.toString().toDoubleOrNull() ?: 0.0
            val unitPrice = unitPriceInput.text.toString().toDoubleOrNull() ?: 0.0
            val subTotal = quantity * unitPrice
            subTotalPriceText.text = "Rs $subTotal" // Update the TextView
        }

        quantityInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSubtotal()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        unitPriceInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSubtotal()
            }
            override fun afterTextChanged(s: Editable?) {}
        })


        // Add or Update Product Logic
        addProductButton.setOnClickListener {
            val name = productNameInput.text.toString()
            val quantity = quantityInput.text.toString().toDoubleOrNull() ?: 0.0
            val unitPrice = unitPriceInput.text.toString().toDoubleOrNull() ?: 0.0
            val totalPrice = quantity * unitPrice

            if (name.isBlank()) {
                productNameInput.error = "Name is required"
                productNameInput.requestFocus()
                return@setOnClickListener
            }
            if (quantity <= 0) {
                quantityInput.error = "Enter a valid quantity"
                quantityInput.requestFocus()
                return@setOnClickListener
            }
            if (unitPrice <= 0) {
                unitPriceInput.error = "Enter a valid price"
                unitPriceInput.requestFocus()
                return@setOnClickListener
            }

            val updatedList = viewModel.productList.value.orEmpty().toMutableList()

            if (isEditing && editingPosition != null) {
                // Update the existing product
                val product = updatedList[editingPosition!!]
                product.name = name
                product.quantity = quantity
                product.unitPrice = unitPrice
                product.totalPrice = totalPrice

                adapter.notifyItemChanged(editingPosition!!)
            } else {
                // Add a new product
                val newProduct = Product(name, quantity, unitPrice, totalPrice)
                updatedList.add(newProduct)

                adapter.notifyItemInserted(updatedList.size - 1)
                recyclerView.scrollToPosition(updatedList.size - 1) // Scroll to the new product
            }

            // Update LiveData
            viewModel.productList.value = updatedList

            viewModel.addOrUpdateProduct(name, unitPrice)

            lastTotalPrice += totalPrice // Update lastTotalPrice with the new product's price
            lastTotalButton.text = "Rs $lastTotalPrice"
            // Reset inputs and state
            productNameInput.text.clear()
            quantityInput.text.clear()
            unitPriceInput.text.clear()
            productNameInput.requestFocus()

            isEditing = false
            editingPosition = null
            addProductButton.text = "Add Product" // Reset button text to "Add Product"
        }
    }

    private fun showCashDialog() {
        val dialogView = layoutInflater.inflate(R.layout.cash_dialog, null)

        val cashInput = dialogView.findViewById<EditText>(R.id.cashInput)
        val balanceText = dialogView.findViewById<TextView>(R.id.balanceText) // TextView for showing balance
        val proceedButton = dialogView.findViewById<Button>(R.id.proceedButton) // Button to proceed order

        // Initially hide the "Proceed Order" button and balance text
        balanceText.text = ""
        proceedButton.isEnabled = false

        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Enter Customer Cash")
            .setView(dialogView)
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss() // Close the dialog on "Cancel"
            }
            .create()

        // Add a TextWatcher to calculate the balance dynamically
        cashInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val cashEntered = cashInput.text.toString().toDoubleOrNull() ?: 0.0
                val balance = cashEntered - lastTotalPrice

                if (cashEntered > 0) {
                    if (balance >= 0) {
                        balanceText.text = "Balance: Rs $balance"
                        proceedButton.isEnabled = true // Enable the proceed button
                    } else {
                        balanceText.text = "Insufficient Cash: Rs ${-balance}"
                        proceedButton.isEnabled = false // Disable the proceed button
                    }
                } else {
                    balanceText.text = "" // Clear balance if no valid input
                    proceedButton.isEnabled = false
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        proceedButton.setOnClickListener {

            val productList = viewModel.productList.value.orEmpty()
            val cashEntered = cashInput.text.toString().toDoubleOrNull() ?: 0.0

            if (productList.isNotEmpty()) {
                viewModel.saveOrderData(productList, lastTotalPrice, cashEntered)
            }

            viewModel.productList.value = mutableListOf() // Reset LiveData list
            lastTotalPrice = 0.0
            lastTotalButton.text = "Rs 0.0"
            // Notify the adapter that the data has changed
            adapter.updateList(emptyList()) // Pass empty list to clear RecyclerView

//            Toast.makeText(this@MainActivity, "Order Saved Successfully", Toast.LENGTH_SHORT).show()

            // Close the dialog
            dialog.dismiss()
        }


        dialog.show()
    }
}

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    Scaffold(
        topBar = { AppBar(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("dashboard") { DashboardScreen() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(navController: NavController) {
    TopAppBar(
        title = { Text("My App") },
        actions = {
            IconButton(onClick = { navController.navigate("dashboard") }) {
                Icon(Icons.Default.Menu, contentDescription = "Dashboard")
            }
        }
    )
}

@Composable
fun HomeScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = { navController.navigate("dashboard") },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("Go to Dashboard")
        }
    }
}
