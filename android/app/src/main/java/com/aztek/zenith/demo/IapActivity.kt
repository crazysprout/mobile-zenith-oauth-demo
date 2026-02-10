package com.aztek.zenith.demo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aztek.zenith.ZenithApp
import com.aztek.zenith.data.ZenithProduct

class IapActivity : ComponentActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var rvProducts: RecyclerView
    private lateinit var btnClose: ImageButton
    private val productsAdapter = ProductsAdapter { product -> purchaseProduct(product) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iap)

        tvStatus = findViewById(R.id.tv_status)
        rvProducts = findViewById(R.id.rv_products)
        btnClose = findViewById(R.id.btn_close)

        rvProducts.layoutManager = LinearLayoutManager(this)
        rvProducts.adapter = productsAdapter

        btnClose.setOnClickListener { finish() }

        fetchProducts()
    }

    private fun fetchProducts() {
        tvStatus.text = "Status: Fetching products..."
        ZenithApp.fetchProducts(
                onSuccess = { products ->
                    runOnUiThread {
                        tvStatus.text = "Status: Products fetched (${products.size})"
                        productsAdapter.submitList(products)
                    }
                },
                onFailure = { error ->
                    runOnUiThread {
                        tvStatus.text = "Status: Fetch Failed - ${error.message}"
                        Log.e("IapActivity", "Fetch Failed", error)
                    }
                }
        )
    }

    private fun purchaseProduct(product: ZenithProduct) {
        tvStatus.text = "Status: Purchasing ${product.title}..."
        ZenithApp.purchaseProduct(
                activity = this,
                productId = product.id,
                onSuccess = { purchaseInfo ->
                    runOnUiThread {
                        tvStatus.text = "Status: Purchased ${purchaseInfo.productId}!"
                        Toast.makeText(
                                        this,
                                        "Success: ${purchaseInfo.productId}",
                                        Toast.LENGTH_LONG
                                )
                                .show()
                    }
                },
                onFailure = { error ->
                    runOnUiThread {
                        tvStatus.text = "Status: Purchase Failed - ${error.message}"
                        Log.e("IapActivity", "Purchase Failed", error)
                    }
                }
        )
    }
}

class ProductsAdapter(private val onProductClick: (ZenithProduct) -> Unit) :
        RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    private var items: List<ZenithProduct> = emptyList()

    fun submitList(newItems: List<ZenithProduct>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
                LayoutInflater.from(parent.context)
                        .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onProductClick)
    }

    override fun getItemCount() = items.size

    class ViewHolder(val view: android.view.View) : RecyclerView.ViewHolder(view) {
        private val text1: TextView = view.findViewById(android.R.id.text1)
        private val text2: TextView = view.findViewById(android.R.id.text2)

        fun bind(product: ZenithProduct, onClick: (ZenithProduct) -> Unit) {
            text1.text = product.title
            text2.text = "${product.formattedPrice} (${product.id})"
            view.setOnClickListener { onClick(product) }
        }
    }
}
