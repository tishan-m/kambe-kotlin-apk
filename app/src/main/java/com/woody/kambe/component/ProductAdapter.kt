package com.woody.kambe.component

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.woody.kambe.R
import com.woody.kambe.model.Product

class ProductAdapter(private val products: MutableList<Product>, private val onEdit: (Product, Int) -> Unit) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.productName)
        val quantity: TextView = view.findViewById(R.id.productQuantity)
        val price: TextView = view.findViewById(R.id.productPrice)

        init {
            view.setOnClickListener {
                onEdit(products[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.name.text = product.name
        holder.quantity.text = "${product.quantity}"
        holder.price.text = "${product.totalPrice}"
    }

    override fun getItemCount(): Int = products.size

    fun updateList(newList: List<Product>) {
        products.clear()
        products.addAll(newList)
        notifyDataSetChanged()
    }
}
