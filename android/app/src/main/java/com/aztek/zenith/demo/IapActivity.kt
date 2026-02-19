package com.aztek.zenith.demo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aztek.zenith.ZenithApp
import com.aztek.zenith.data.ZenithProduct
import com.aztek.zenith.data.ZenithTransaction

class IapActivity : ComponentActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var rvProducts: RecyclerView
    private lateinit var btnClose: ImageButton
    private lateinit var btnHistory: Button
    private val productsAdapter = ProductsAdapter { product -> purchaseProduct(product) }
    private val historyAdapter = HistoryAdapter()
    private var isShowingHistory = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iap)

        tvStatus = findViewById(R.id.tv_status)
        rvProducts = findViewById(R.id.rv_products)
        btnClose = findViewById(R.id.btn_close)
        btnHistory = findViewById(R.id.btn_history)

        rvProducts.layoutManager = LinearLayoutManager(this)
        rvProducts.adapter = productsAdapter

        btnClose.setOnClickListener { finish() }
        btnHistory.setOnClickListener { toggleHistory() }

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

    private fun toggleHistory() {
        isShowingHistory = !isShowingHistory
        if (isShowingHistory) {
            btnHistory.text = "Products"
            rvProducts.adapter = historyAdapter
            fetchHistory()
        } else {
            btnHistory.text = "History"
            rvProducts.adapter = productsAdapter
            fetchProducts()
        }
    }

    private fun fetchHistory() {
        tvStatus.text = "Status: Fetching history..."
        ZenithApp.purchaseHistory(
                success = { transactions ->
                    runOnUiThread {
                        tvStatus.text = "Status: History fetched (${transactions.size})"
                        historyAdapter.submitList(transactions)
                    }
                },
                failure = { error ->
                    runOnUiThread {
                        tvStatus.text = "Status: History Fetch Failed - ${error.message}"
                        Log.e("IapActivity", "History Fetch Failed", error)
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
                onPending = {
                    runOnUiThread {
                        tvStatus.text = "Status: Purchase Pending..."
                        Toast.makeText(this, "Purchase Pending", Toast.LENGTH_SHORT).show()
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
            text2.text = "${product.formattedPrice}"
            view.setOnClickListener { onClick(product) }
        }
    }
}

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private var items: List<ZenithTransaction> = emptyList()

    fun submitList(newItems: List<ZenithTransaction>) {
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
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class ViewHolder(val view: android.view.View) : RecyclerView.ViewHolder(view) {
        private val text1: TextView = view.findViewById(android.R.id.text1)
        private val text2: TextView = view.findViewById(android.R.id.text2)

        fun bind(transaction: ZenithTransaction) {
            // Assuming ZenithTransaction has productId and date/id
            text1.text =
                    transaction
                            .productId // Adjust based on actual ZenithTransaction fields if known,
            // guessing common ones
            text2.text = "Transaction" // Placeholder if detail unknown, or verify ZenithTransaction
            // class
        }
    }
}
