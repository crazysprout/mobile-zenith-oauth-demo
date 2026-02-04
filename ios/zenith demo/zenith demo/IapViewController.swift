//
//  IapViewController.swift
//  zenith demo
//
//

import UIKit
import Zenith

class IapViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    private var products: [ZenithProduct] = []
    
    private lazy var statusLabel: UILabel = {
        let label = UILabel()
        label.text = "Status: Ready"
        label.textAlignment = .center
        label.numberOfLines = 0
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    
    private lazy var tableView: UITableView = {
        let tv = UITableView()
        tv.dataSource = self
        tv.delegate = self
        tv.register(UITableViewCell.self, forCellReuseIdentifier: "ProductCell")
        tv.translatesAutoresizingMaskIntoConstraints = false
        return tv
    }()
    
    private lazy var closeButton: UIButton = {
        let btn = UIButton(type: .system)
        btn.setTitle("Close", for: .normal)
        btn.addTarget(self, action: #selector(onCloseTapped), for: .touchUpInside)
        btn.translatesAutoresizingMaskIntoConstraints = false
        return btn
    }()

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .systemBackground
        setupUI()
        fetchProducts()
    }
    
    private func setupUI() {
        view.addSubview(statusLabel)
        view.addSubview(tableView)
        view.addSubview(closeButton)
        
        NSLayoutConstraint.activate([
            statusLabel.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 20),
            statusLabel.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 20),
            statusLabel.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -20),
            
            tableView.topAnchor.constraint(equalTo: statusLabel.bottomAnchor, constant: 20),
            tableView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            tableView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            tableView.bottomAnchor.constraint(equalTo: closeButton.topAnchor, constant: -20),
            
            closeButton.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -20),
            closeButton.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            closeButton.heightAnchor.constraint(equalToConstant: 44)
        ])
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
    
    @objc private func onCloseTapped() {
        dismiss(animated: true, completion: nil)
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
