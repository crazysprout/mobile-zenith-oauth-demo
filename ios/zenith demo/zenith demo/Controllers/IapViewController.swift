//
//  IapViewController.swift
//  zenith demo
//
//

import UIKit
import Zenith

class IapViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    @IBOutlet weak var statusLabel: UILabel!
    @IBOutlet weak var tableView: UITableView!
    
    private var products: [ZenithProduct] = []
    private var transactions: [ZenithTransaction] = []
    private var isShowingHistory = false

    
    @IBOutlet weak var historyButton: UIButton!

    override func viewDidLoad() {
        super.viewDidLoad()
        
        tableView.dataSource = self
        tableView.delegate = self
        tableView.register(UITableViewCell.self, forCellReuseIdentifier: "ProductCell")
        
        fetchProducts()
    }
    
    @IBAction func onHistoryTapped(_ sender: Any) {
        toggleHistory()
    }
    
    private func toggleHistory() {
        isShowingHistory = !isShowingHistory
        if isShowingHistory {
            historyButton.setTitle("Products", for: .normal)
            fetchHistory()
        } else {
            historyButton.setTitle("History", for: .normal)
            fetchProducts()
        }
    }
    
    private func fetchHistory() {
        statusLabel.text = "Status: Fetching history..."
        showLoading(message: "Loading History...")
        
        ZenithApp.shared.purchaseHistory { [weak self] transactions in
            DispatchQueue.main.async {
                self?.hideLoading {
                    self?.statusLabel.text = "Status: History fetched (\(transactions.count))"
                    self?.transactions = transactions
                    self?.tableView.reloadData()
                }
            }
        } failure: { [weak self] error in
            DispatchQueue.main.async {
                self?.hideLoading {
                    self?.statusLabel.text = "Status: History Fetch Failed - \(error.localizedDescription)"
                    print("History Fetch Error: \(error)")
                }
            }
        }
    }
    
    @IBAction func onCloseTapped(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }
    
    private func fetchProducts() {
        statusLabel.text = "Status: Fetching products..."
        showLoading(message: "Loading Products...")
        
        ZenithApp.shared.fetchProducts { [weak self] products in
            DispatchQueue.main.async {
                self?.hideLoading {
                    self?.statusLabel.text = "Status: Products fetched (\(products.count))"
                    self?.products = products
                    self?.tableView.reloadData()
                }
            }
        } failure: { [weak self] error in
            DispatchQueue.main.async {
                self?.hideLoading {
                    self?.statusLabel.text = "Status: Fetch Failed - \(error.localizedDescription)"
                    print("Fetch Error: \(error)")
                }
            }
        }
    }
    
    private func purchaseProduct(_ product: ZenithProduct) {
        statusLabel.text = "Status: Purchasing \(product.title)..."
        showLoading(message: "Purchasing...")
        
        ZenithApp.shared.purchaseProduct(productId: product.id) { [weak self] purchaseInfo in
            DispatchQueue.main.async {
                self?.hideLoading {
                    self?.statusLabel.text = "Status: Purchased \(purchaseInfo.productId)!"
                    print("Purchase Success: \(purchaseInfo)")
                }
            }
        } pending: { [weak self] in
            DispatchQueue.main.async {
                self?.statusLabel.text = "Status: Purchase Pending..."
            }
        } failure: { [weak self] error in
            DispatchQueue.main.async {
                self?.hideLoading {
                    self?.statusLabel.text = "Status: Purchase Failed - \(error.localizedDescription)"
                    print("Purchase Error: \(error)")
                }
            }
        }
    }
    
    // MARK: - UITableViewDataSource
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return isShowingHistory ? transactions.count : products.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ProductCell", for: indexPath)
        var content = cell.defaultContentConfiguration()
        
        if isShowingHistory {
            let transaction = transactions[indexPath.row]
            content.text = transaction.productId
            content.secondaryText = "Transaction" // Modify based on ZenithTransaction properties
        } else {
            let product = products[indexPath.row]
            content.text = product.title
            
            let formatter = NumberFormatter()
            formatter.numberStyle = .currency
            formatter.locale = product.priceLocale
            let priceString = formatter.string(from: product.price as NSDecimalNumber) ?? "\(product.price)"
            
            content.secondaryText = "\(priceString) (\(product.id))"
        }
        
        cell.contentConfiguration = content
        return cell
    }
    
    // MARK: - UITableViewDelegate
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        if !isShowingHistory {
            let product = products[indexPath.row]
            purchaseProduct(product)
        }
    }
}
