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
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        tableView.dataSource = self
        tableView.delegate = self
        tableView.register(UITableViewCell.self, forCellReuseIdentifier: "ProductCell")
        
        fetchProducts()
    }
    
    @IBAction func onCloseTapped(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }
    
    private func fetchProducts() {
        statusLabel.text = "Status: Fetching products..."
        ZenithApp.shared.fetchProducts { [weak self] products in
            DispatchQueue.main.async {
                self?.statusLabel.text = "Status: Products fetched (\(products.count))"
                self?.products = products
                self?.tableView.reloadData()
            }
        } failure: { [weak self] error in
            DispatchQueue.main.async {
                self?.statusLabel.text = "Status: Fetch Failed - \(error.localizedDescription)"
                print("Fetch Error: \(error)")
            }
        }
    }
    
    private func purchaseProduct(_ product: ZenithProduct) {
        statusLabel.text = "Status: Purchasing \(product.title)..."
        ZenithApp.shared.purchaseProduct(productId: product.id) { [weak self] purchaseInfo in
            DispatchQueue.main.async {
                self?.statusLabel.text = "Status: Purchased \(purchaseInfo.productId)!"
                print("Purchase Success: \(purchaseInfo)")
            }
        } failure: { [weak self] error in
            DispatchQueue.main.async {
                self?.statusLabel.text = "Status: Purchase Failed - \(error.localizedDescription)"
                print("Purchase Error: \(error)")
            }
        }
    }
    
    // MARK: - UITableViewDataSource
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return products.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ProductCell", for: indexPath)
        let product = products[indexPath.row]
        var content = cell.defaultContentConfiguration()
        content.text = product.title
        
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.locale = product.priceLocale
        let priceString = formatter.string(from: product.price as NSDecimalNumber) ?? "\(product.price)"
        
        content.secondaryText = "\(priceString) (\(product.id))"
        cell.contentConfiguration = content
        return cell
    }
    
    // MARK: - UITableViewDelegate
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let product = products[indexPath.row]
        purchaseProduct(product)
    }
}
